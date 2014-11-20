package org.uberfire.security.server;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HSTS servlet filter
 * For a detailed explanation please take a look at <a href="http://aerogear.org/docs/guides/aerogear-security/">http://aerogear.org/docs/guides/aerogear-security/</a>
 * <p/>
 * Note: This implementation has been borrowed from Aerogear Security.
 */
public class SecureHeadersFilter implements Filter {

    public static final String LOCATION = "Location";
    public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    public static final String X_FRAME_OPTIONS = "X-FRAME-OPTIONS";
    public static final String X_XSS_OPTIONS = "X-XSS-Protection";

    private SecureHeadersConfig config;

    @Override
    public void init( final FilterConfig filterConfig ) throws ServletException {
        config = new SecureHeadersConfig( filterConfig );
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter( final ServletRequest servletRequest,
                          final ServletResponse servletResponse,
                          final FilterChain chain ) throws IOException, ServletException {

        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        if ( request.getScheme().equals( "http" ) ) {
            addLocation( response );
            addFrameOptions( response );
        } else if ( request.getScheme().equals( "https" ) ) {
            addStrictTransportSecurity( response );
        }
        addXSSOptions( response );

        chain.doFilter( request, response );
    }

    private void addStrictTransportSecurity( HttpServletResponse response ) {
        if ( config.hasMaxAge() ) {
            response.addHeader( STRICT_TRANSPORT_SECURITY, config.getMaxAge() );
        }
    }

    private void addFrameOptions( HttpServletResponse response ) {
        if ( config.hasFrameOptions() ) {
            response.addHeader( X_FRAME_OPTIONS, config.getFrameOptions() );
        }
    }

    private void addLocation( HttpServletResponse response ) {
        if ( config.hasLocation() ) {
            response.addHeader( LOCATION, config.getLocation() );
            response.setStatus( HttpServletResponse.SC_MOVED_PERMANENTLY );
        }
    }

    private void addXSSOptions( HttpServletResponse response ) {
        if ( config.hasXSSOptions() ) {
            response.addHeader( X_XSS_OPTIONS, config.getXssOptions() );
        }
    }

}