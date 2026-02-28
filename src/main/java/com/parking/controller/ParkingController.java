package com.parking.controller;

import com.parking.dto.ParkingTicketResponse;
import com.parking.model.enums.VehicleType;
import com.parking.service.ParkingService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    //  CHECK-IN
    @PostMapping("/check-in")
    public ParkingTicketResponse checkIn(
            @RequestParam String licensePlate,
            @RequestParam VehicleType vehicleType
    ) {
        return parkingService.checkIn(licensePlate, vehicleType);
    }

    //  CHECK-OUT
    @PostMapping("/check-out/{ticketId}")
    public ParkingTicketResponse checkOut(@PathVariable UUID ticketId) {
        return parkingService.checkOut(ticketId);
    }
}
