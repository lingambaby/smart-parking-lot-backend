package com.parking.repository;

import com.parking.model.ParkingSpot;
import com.parking.model.enums.SpotStatus;
import com.parking.model.enums.SpotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, UUID> {

    Optional<ParkingSpot> findFirstBySpotTypeAndStatus(SpotType spotType, SpotStatus status);

}


