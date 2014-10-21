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
 * Note: This implementation has been borrowed from Aerogear Security.
 */
public class XSSServletFilter implements Filter {

    @Override
    public void init( FilterConfig config ) throws ServletException {
    }

    @Override
    public void doFilter( final ServletRequest servletRequest,
                          final ServletResponse servletResponse,
                          final FilterChain filterChain ) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        filterChain.doFilter( new XSSServletRequestWrapper( httpServletRequest ), httpServletResponse );
    }

    @Override
    public void destroy() {
    }
}
