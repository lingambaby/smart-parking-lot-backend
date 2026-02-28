package com.parking.repository;

import com.parking.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, UUID> {

}

