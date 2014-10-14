package org.kie.uberfire.security.server;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;

public class BasicAuthSecurityFilter implements Filter {

    public static final String REALM_NAME_PARAM = "realmName";

    @Inject
    private AuthenticationService authenticationService;

    private String realmName = "UberFire Security Extension Default Realm";

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        final String realmName = filterConfig.getInitParameter( REALM_NAME_PARAM );
        if ( realmName != null ) {
            this.realmName = realmName;
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter( final ServletRequest _request,
                          final ServletResponse _response,
                          final FilterChain chain ) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) _request;
        final HttpServletResponse response = (HttpServletResponse) _response;

        if ( authenticate( request ) ) {
            chain.doFilter( request, response );
            if ( response.isCommitted() ) {
                authenticationService.logout();
            }
        } else {
            challengeClient( request, response );
        }
    }

    public void challengeClient( final HttpServletRequest request,
                                 final HttpServletResponse response ) throws IOException {
        response.setHeader( "WWW-Authenticate", "Basic realm=\"" + this.realmName + "\"" );

        // this usually means we have a failing authentication request from an ajax client. so we return SC_FORBIDDEN instead.
        if ( isAjaxRequest( request ) ) {
            response.sendError( HttpServletResponse.SC_FORBIDDEN );
        } else {
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
        }
    }

    private boolean authenticate( final HttpServletRequest req ) {
        final String authHead = req.getHeader( "Authorization" );

        if ( authHead != null ) {
            final int index = authHead.indexOf( ' ' );
            final String[] credentials = new String( Base64.decodeBase64( authHead.substring( index ) ), Charsets.UTF_8 ).split( ":" );

            try {
                authenticationService.login( credentials[ 0 ], credentials[ 1 ] );
                return true;
            } catch ( final FailedAuthenticationException e ) {
                return false;
            }
        }

        return false;
    }

    private boolean isAjaxRequest( HttpServletRequest request ) {
        return request.getHeader( "X-Requested-With" ) != null && "XMLHttpRequest".equalsIgnoreCase( request.getHeader( "X-Requested-With" ) );
    }

}