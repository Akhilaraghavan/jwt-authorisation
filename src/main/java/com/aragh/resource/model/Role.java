package com.aragh.resource.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    READ_PRIVILEDGE, WRITE_PRIVILEDGE;

    @Override
    public String getAuthority() {
        return name();
    }
}
