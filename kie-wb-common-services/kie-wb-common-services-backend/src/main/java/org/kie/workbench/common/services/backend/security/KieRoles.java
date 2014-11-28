package org.kie.workbench.common.services.backend.security;

import org.jboss.errai.security.shared.api.Role;

public enum KieRoles implements Role {
    ADMIN, DEVELOPER, ANALYST, USER, MANAGER, KIEMGMT;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    @Override
    public String getName() {
        return toString();
    }

}
