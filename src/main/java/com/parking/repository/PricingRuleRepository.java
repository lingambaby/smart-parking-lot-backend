package com.parking.repository;

import com.parking.model.PricingRule;
import com.parking.model.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {

    Optional<PricingRule> findByVehicleType(VehicleType vehicleType);

}


