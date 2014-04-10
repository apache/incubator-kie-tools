package org.uberfire.security.server;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.authentication.web.HTTPAuthenticationScheme;
import org.picketlink.credential.DefaultLoginCredentials;


public class FormAuthenticationScheme implements HTTPAuthenticationScheme {

    public static final String HOST_PAGE_INIT_PARAM = "hostPage";
    public static final String HTTP_FORM_SECURITY_CHECK_URI = "/uf_security_check";
    public static final String HTTP_FORM_USERNAME_PARAM = "uf_username";
    public static final String HTTP_FORM_PASSWORD_PARAM = "uf_password";

    /**
     * URI of the GWT host page, relative to the servlet container root (so it starts with '/' and includes the context
     * path).
     */
    private final String hostPageUri;

    public FormAuthenticationScheme(FilterConfig filterConfig) {
        String contextRelativeHostPageUri = filterConfig.getInitParameter( HOST_PAGE_INIT_PARAM );
        if (contextRelativeHostPageUri == null) {
            throw new IllegalStateException(
                    "FormAuthenticationScheme requires that you set the filter init parameter \""
                            + HOST_PAGE_INIT_PARAM + "\" to the context-relative URI of the host page.");
        }
        hostPageUri = filterConfig.getServletContext().getContextPath() + contextRelativeHostPageUri;
    }

    @Override
    public void extractCredential( HttpServletRequest request, DefaultLoginCredentials creds ) {
        if ( request.getMethod().equals( "POST" ) && request.getRequestURI().contains( HTTP_FORM_SECURITY_CHECK_URI ) ) {
            creds.setUserId( request.getParameter( HTTP_FORM_USERNAME_PARAM ) );
            creds.setPassword( request.getParameter( HTTP_FORM_PASSWORD_PARAM ) );
        }
    }

    @Override
    public void challengeClient( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.sendError( HttpServletResponse.SC_FORBIDDEN );
    }

    @Override
    public boolean postAuthentication( HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.sendRedirect( hostPageUri );
        return false;
    }

}
