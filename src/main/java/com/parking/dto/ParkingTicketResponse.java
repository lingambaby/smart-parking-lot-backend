package com.parking.dto;

import com.parking.model.enums.VehicleType;
import com.parking.model.enums.SpotType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ParkingTicketResponse {

    private UUID id;
    private boolean active;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double totalAmount;

    private String licensePlate;
    private VehicleType vehicleType;

    private String spotNumber;
    private SpotType spotType;
}