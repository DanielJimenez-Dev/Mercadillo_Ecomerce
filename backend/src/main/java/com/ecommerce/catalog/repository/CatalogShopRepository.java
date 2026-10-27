package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogShopRepository extends JpaRepository<Shop, Long> {
}
