package org.uberfire.security.server.auth.source;

import javax.security.jacc.PolicyContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;

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
    public boolean authenticate( Credential credential ) {
        try {
            final UserNameCredential userNameCredential = checkInstanceOf( "credential", credential, UserNameCredential.class );
            if ( userNameCredential instanceof UsernamePasswordCredential ) {

                final HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext( "javax.servlet.http.HttpServletRequest" );
                try {
                    request.login( userNameCredential.getUserName(), ( (UsernamePasswordCredential) userNameCredential ).getPassword().toString() );
                } catch ( final ServletException ex ) {
                    return false;
                }
            }
            return super.authenticate( credential );
        } catch ( final Exception e ) {
            return false;
        }
    }
}
