package com.peter.tanxuannewapp.domain.criteria;

import com.peter.tanxuannewapp.domain.Category;
import com.peter.tanxuannewapp.domain.Supplier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaSearchProduct {
    private String name;
    private Category category;
    private Supplier supplier;
    private String unit;
    private int quantity;
    private double price;
}
