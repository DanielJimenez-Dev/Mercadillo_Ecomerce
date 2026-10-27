package com.ecommerce.products.repository;

import com.ecommerce.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MerchantProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByShopId(Long shopId);
}
