package com.peter.tanxuannewapp.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaSearchPermission {
    private String name;
    private String module;
    private String method;
    private String route;
    private String createdAt;
}
