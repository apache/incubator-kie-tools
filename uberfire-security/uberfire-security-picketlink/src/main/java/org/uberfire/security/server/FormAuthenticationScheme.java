package org.uberfire.security.server;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.authentication.web.HTTPAuthenticationScheme;
import org.picketlink.credential.DefaultLoginCredentials;


public class FormAuthenticationScheme implements HTTPAuthenticationScheme {

    private static final String HOST_PAGE_INIT_PARAM = "hostPage";

    private final String hostPageUri;

    public FormAuthenticationScheme(FilterConfig filterConfig) {
        hostPageUri = filterConfig.getInitParameter( HOST_PAGE_INIT_PARAM );
        if (hostPageUri == null) {
            throw new IllegalStateException(
                    "FormAuthenticationScheme requires that you set the filter init parameter \""
                            + HOST_PAGE_INIT_PARAM + "\" to the context-relative URI of the host page.");
        }
    }

    @Override
    public void extractCredential( HttpServletRequest request, DefaultLoginCredentials creds ) {
        if ( request.getMethod().equals( "POST" ) && request.getRequestURI().contains( "uf_security_check" ) ) {
            creds.setUserId( request.getParameter( "uf_username" ) );
            creds.setPassword( request.getParameter( "uf_password" ) );
        }
    }

    @Override
    public void challengeClient( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setStatus( HttpServletResponse.SC_FORBIDDEN );
    }

    @Override
    public boolean postAuthentication( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.sendRedirect( hostPageUri );
        return false;
    }

}
