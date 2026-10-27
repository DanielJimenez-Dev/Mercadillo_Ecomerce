package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.model.CustomsTariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomsTariffRepository extends JpaRepository<CustomsTariff, Long> {
}