package com.parking.service;

import com.parking.dto.ParkingTicketResponse;
import com.parking.exception.BadRequestException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.model.ParkingSpot;
import com.parking.model.ParkingTicket;
import com.parking.model.PricingRule;
import com.parking.model.Vehicle;
import com.parking.model.enums.SpotStatus;
import com.parking.model.enums.SpotType;
import com.parking.model.enums.VehicleType;
import com.parking.repository.ParkingSpotRepository;
import com.parking.repository.ParkingTicketRepository;
import com.parking.repository.PricingRuleRepository;
import com.parking.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParkingService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingTicketRepository parkingTicketRepository;
    private final PricingRuleRepository pricingRuleRepository;

    public ParkingService(VehicleRepository vehicleRepository,
                          ParkingSpotRepository parkingSpotRepository,
                          ParkingTicketRepository parkingTicketRepository,
                          PricingRuleRepository pricingRuleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingTicketRepository = parkingTicketRepository;
        this.pricingRuleRepository = pricingRuleRepository;
    }

    // ==============================
    //  CHECK-IN
    // ==============================

    @Transactional
    public ParkingTicketResponse checkIn(String licensePlate, VehicleType vehicleType) {

        // 1️Find or create vehicle
        Vehicle vehicle = vehicleRepository
                .findByLicensePlate(licensePlate)
                .orElseGet(() -> {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setLicensePlate(licensePlate);
                    newVehicle.setVehicleType(vehicleType);
                    return vehicleRepository.save(newVehicle);
                });

        // 2️ Find available parking spot
        SpotType requiredSpotType = SpotType.valueOf(vehicleType.name());

        ParkingSpot parkingSpot = parkingSpotRepository
                .findFirstBySpotTypeAndStatus(requiredSpotType, SpotStatus.AVAILABLE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No available parking spot")
                );

        // 3️ Mark spot as OCCUPIED
        parkingSpot.setStatus(SpotStatus.OCCUPIED);
        parkingSpotRepository.save(parkingSpot);

        // 4️Create ticket
        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicle(vehicle);
        ticket.setParkingSpot(parkingSpot);
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setActive(true);

        ParkingTicket savedTicket = parkingTicketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    // ==============================
    // 🚗 CHECK-OUT
    // ==============================

    @Transactional
    public ParkingTicketResponse checkOut(UUID ticketId) {

        ParkingTicket ticket = parkingTicketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid ticket ID")
                );

        if (!ticket.isActive()) {
            throw new BadRequestException("Ticket already closed");
        }

        // 1️Set exit time
        ticket.setExitTime(LocalDateTime.now());

        // 2️Calculate duration
        long hours = Duration.between(
                ticket.getEntryTime(),
                ticket.getExitTime()
        ).toHours();

        if (hours == 0) {
            hours = 1; // minimum 1 hour charge
        }

        // 3️Get pricing rule
        VehicleType vehicleType = ticket.getVehicle().getVehicleType();

        PricingRule pricingRule = pricingRuleRepository
                .findByVehicleType(vehicleType)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pricing rule not found")
                );

        double fee = hours * pricingRule.getPricePerHour();
        ticket.setTotalAmount(fee);

        // 4️ Mark ticket inactive
        ticket.setActive(false);

        // 5️ Free parking spot
        ParkingSpot spot = ticket.getParkingSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        parkingSpotRepository.save(spot);

        ParkingTicket updatedTicket = parkingTicketRepository.save(ticket);

        return mapToResponse(updatedTicket);
    }

    // ==============================
    // ENTITY → DTO MAPPER
    // ==============================

    private ParkingTicketResponse mapToResponse(ParkingTicket ticket) {

        ParkingTicketResponse response = new ParkingTicketResponse();

        response.setId(ticket.getId());
        response.setActive(ticket.isActive());
        response.setEntryTime(ticket.getEntryTime());
        response.setExitTime(ticket.getExitTime());
        response.setTotalAmount(ticket.getTotalAmount());

        response.setLicensePlate(ticket.getVehicle().getLicensePlate());
        response.setVehicleType(ticket.getVehicle().getVehicleType());

        response.setSpotNumber(ticket.getParkingSpot().getSpotNumber());
        response.setSpotType(ticket.getParkingSpot().getSpotType());

        return response;
    }
}
