package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResProductDTO {
    private int id;
    private String name;
    private int quantity;
    private String description;
    private String unit;
    private String productImage;
    private double price;
    private SupplierProduct supplier;
    private CategoryProduct category;
    private Instant createdAt;
    private String createdBy;

    @Getter
    @Setter
    public static class SupplierProduct {
        private int id;
        private String name;
    }

    @Getter
    @Setter
    public static class CategoryProduct {
        private int id;
        private String name;
    }
}
