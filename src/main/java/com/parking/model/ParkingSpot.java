package com.parking.model;

import com.parking.model.enums.SpotStatus;
import com.parking.model.enums.SpotType;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class ParkingSpot {
    @Id
    @GeneratedValue
    private UUID id;

    private String spotNumber;

    @Enumerated(EnumType.STRING)
    private SpotType spotType;

    @Enumerated(EnumType.STRING)
    private SpotStatus status;

    @Version
    private int version;
}
