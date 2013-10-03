package org.uberfire.security.server.auth.source;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.HttpSecurityContext;

import static org.kie.commons.validation.Preconditions.*;

public class HttpServletRequestAuthenticationSource extends JACCAuthenticationSource {

    @Override
    public boolean supportsCredential( final Credential credential ) {
        if ( credential == null ) {
            return false;
        }
        return credential instanceof UserNameCredential;
    }

    @Override
    public boolean authenticate( final Credential credential, final SecurityContext securityContext ) {
        try {
            final UserNameCredential userNameCredential = checkInstanceOf( "credential", credential, UserNameCredential.class );
            Subject subject = (Subject) PolicyContext.getContext( "javax.security.auth.Subject.container" );
            if ( subject != null ) {
                return super.authenticate( credential, securityContext );
            }

            if ( userNameCredential instanceof UsernamePasswordCredential ) {
                final HttpServletRequest request = ((HttpSecurityContext) securityContext).getRequest();
                try {
                    request.login( userNameCredential.getUserName(), ( (UsernamePasswordCredential) userNameCredential ).getPassword().toString() );
                } catch ( final ServletException ex ) {
                    return false;
                }
            }
            return super.authenticate( credential, securityContext );
        } catch ( final Exception e ) {
            return false;
        }
    }
}
