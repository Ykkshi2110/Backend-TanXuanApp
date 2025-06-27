package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResRoleDTO {
    private int id;
    private String name;
    private String description;
    private PermissionDTO permission;

    @Getter
    @Setter
    public static class PermissionDTO {
        private int id;
        private String name;
    }
}
