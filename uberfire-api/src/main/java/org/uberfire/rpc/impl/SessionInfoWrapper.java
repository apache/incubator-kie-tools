package org.uberfire.rpc.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.rpc.SessionInfo;

@Dependent
public class SessionInfoWrapper {

    @Inject
    private SessionInfo sessionInfo;

    public boolean isAdmin() {
        for ( Role role : sessionInfo.getIdentity().getRoles() ) {
            if ( role.getName().equals( "admin" ) )
                return true;
        }
        return false;
    }

    public String getId() {
        return sessionInfo.getId();
    }

    public User getIdentity() {
        return sessionInfo.getIdentity();
    }
}
