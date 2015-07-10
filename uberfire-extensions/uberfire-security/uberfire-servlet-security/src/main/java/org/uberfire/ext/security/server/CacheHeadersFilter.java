package org.uberfire.ext.security.server;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CacheHeadersFilter implements Filter {

    @Override
    public void init( final FilterConfig filterConfig ) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter( final ServletRequest request,
                          final ServletResponse response,
                          final FilterChain chain ) throws IOException, ServletException {
        ( (HttpServletResponse) response ).setHeader( "Cache-Control", "no-cache, no-store, must-revalidate" ); // HTTP 1.1.
        ( (HttpServletResponse) response ).setHeader( "Pragma", "no-cache" ); // HTTP 1.0.
        ( (HttpServletResponse) response ).setDateHeader( "Expires", 0 ); // Proxies.

        chain.doFilter( request, response );
    }

}