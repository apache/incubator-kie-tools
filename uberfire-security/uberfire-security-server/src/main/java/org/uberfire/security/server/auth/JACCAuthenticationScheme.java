package org.uberfire.security.server.auth;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.server.HttpSecurityContext;

import static org.kie.commons.validation.Preconditions.checkInstanceOf;

public class JACCAuthenticationScheme extends FormAuthenticationScheme implements AuthenticationScheme {

    @Override
    public void challengeClient(SecurityContext context) {

    }

    @Override
    public Credential buildCredential(SecurityContext context) {

        final HttpSecurityContext httpSecurityContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        final String userName = httpSecurityContext.getRequest().getUserPrincipal().getName();
        return new UserNameCredential(userName);
    }
}
