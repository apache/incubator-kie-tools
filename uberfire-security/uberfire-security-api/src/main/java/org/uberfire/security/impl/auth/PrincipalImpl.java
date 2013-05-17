package org.uberfire.security.impl.auth;

import org.uberfire.security.auth.Principal;

public class PrincipalImpl implements Principal {

    private static final long serialVersionUID = 8303424583760827050L;
    private String name;

    public PrincipalImpl() {
    }

    public PrincipalImpl( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
