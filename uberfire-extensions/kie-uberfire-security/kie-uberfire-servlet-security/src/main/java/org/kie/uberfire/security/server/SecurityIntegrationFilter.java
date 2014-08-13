package org.kie.uberfire.security.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.security.shared.api.UserCookieEncoder;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;

/**
 * TODO: update me
 */
public class SecurityIntegrationFilter implements Filter {

    @Inject
    private AuthenticationService authenticationService;

    public static final String PROBE_ROLES_INIT_PARAM = "probe-for-roles";

    private static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        MappingContextSingleton.get();

        String commaSeparatedRoles = filterConfig.getInitParameter( PROBE_ROLES_INIT_PARAM );
        if ( commaSeparatedRoles != null ) {
            for ( final String role : Collections.unmodifiableList( Arrays.asList( commaSeparatedRoles.split( "," ) ) ) ) {
                RolesRegistry.get().registerRole( role );
            }
        }
    }

    @Override
    public void destroy() {
        // no op
    }

    @Override
    public void doFilter( ServletRequest request,
                          ServletResponse response,
                          FilterChain chain ) throws IOException, ServletException {
        requests.set( (HttpServletRequest) request );
        final User user = authenticationService.getUser();
        if ( user != null ) {
            final Cookie erraiUserCacheCookie = new Cookie(
                    UserCookieEncoder.USER_COOKIE_NAME,
                    UserCookieEncoder.toCookieValue( user ) );
            ( (HttpServletResponse) response ).addCookie( erraiUserCacheCookie );
        }
        try {
            chain.doFilter( request, response );
        } finally {
            requests.remove();
        }
    }

    /**
     * Returns the current servlet request that this thread is handling, or null if this thread is not currently handling
     * a servlet request.
     */
    public static HttpServletRequest getRequest() {
        return requests.get();
    }
}