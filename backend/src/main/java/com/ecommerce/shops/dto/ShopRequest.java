package com.ecommerce.shops.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopRequest {

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String name;

    private String description;
}
