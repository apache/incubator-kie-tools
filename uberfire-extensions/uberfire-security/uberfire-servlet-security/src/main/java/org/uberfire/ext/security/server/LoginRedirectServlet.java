/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workaround for the servlet form authentication process (j_security_check), which, at least on WildFly 8.1, uses the
 * HTTP POST method when forwarding the request after successful login. This blows up after login, giving an
 * <code>HTTP 405 Method Not Allowed</code> response when the desired resource is a static one, such as a GWT host page.
 * <p>
 * This servlet works around the issue by sending an HTTP redirect <i>only if the request comes from an authenticated
 * user</i> to a URL of your choice. The end result is that your user will see the real resource's URL in their
 * browser's location bar after login. In addition, this servlet copies all request parameters that were submitted along
 * with the login form (except j_username and j_password) to the redirect URL.
 * <p>
 * To set this servlet up, configure your web application as follows:
 * <ul>
 * <li>Declare this servlet in web.xml with init-param <code>display-after-login</code> set to the context-relative URI
 * of the page you want the user to land on after a successful login. (for example, <code>/host_page.html</code>)
 * <li>Map this servlet to a url-pattern that makes sense for a sign-in page, such as <code>/login</code>.
 * <li>Mark this servlet's url-pattern with a security-constraint that only allows logged-in users to see it. (I know,
 * this part is confusing! The login URL has to be a protected resource!)
 * <li>Configure web.xml for auth-method <code>FORM</code>
 * <li>Configure the form-login-page and form-error-page to any resource you like. It can even be JSP's under /WEB-INF,
 * since the login and error pages will never display under their "own" URLs. They always display in place of the "real"
 * resource that was being requested by an unauthorized user. In our case, that resource should always be whatever this
 * servlet is mapped to (eg. <code>/login</code>).
 * <li>(Optional) set welcome-page-uri to this servlet's uri-pattern, as an easy way to direct new visitors into this
 * tangled web of deceit.
 * </ul>
 */
public class LoginRedirectServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginRedirectServlet.class);

    public static final String DISPLAY_AFTER_LOGIN_INIT_PARAM = "display-after-login";
    public static final String DISPLAY_WHEN_NOT_AUTH_INIT_PARAM = "display-when-not-authenticated";

    /**
     * URI of the GWT host page, relative to the servlet container root (so it starts with '/' and includes the context
     * path).
     */
    private String displayAfterLoginUri;
    /**
     * Optional URI of a page to redirect in case coming request is not authenticated (no user principal exists),
     * relative to the servlet container root (so it starts with '/' and does not include context path)
     */
    private String displayWhenNotAuthenticatedUri;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String contextRelativeHostPageUri = config.getInitParameter(DISPLAY_AFTER_LOGIN_INIT_PARAM);
        if (contextRelativeHostPageUri == null) {
            throw new IllegalStateException(getClass().getSimpleName() + " requires that you set the init parameter \""
                                                    + DISPLAY_AFTER_LOGIN_INIT_PARAM + "\" to the context-relative URI of the host page.");
        }
        displayAfterLoginUri = config.getServletContext().getContextPath() + contextRelativeHostPageUri;

        // optional display-when-not-authenticated
        String contextRelativeNotAuthPageUri = config.getInitParameter(DISPLAY_WHEN_NOT_AUTH_INIT_PARAM);
        if (contextRelativeNotAuthPageUri != null) {
            displayWhenNotAuthenticatedUri = config.getServletContext().getContextPath() + contextRelativeNotAuthPageUri;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // perform optional check and redirect in case no authenticated request is available
        if (displayWhenNotAuthenticatedUri != null && req.getUserPrincipal() == null) {
            logger.debug("No authorized user thus cleaning up session and redirecting to " + displayWhenNotAuthenticatedUri);
            try {
                // clean up session
                req.logout();
                req.getSession().invalidate();
            } catch (Exception e) {
                // to avoid cases where logout causes issues for first request
            }
            resp.sendRedirect(displayWhenNotAuthenticatedUri);

            return;
        }

        logger.debug("Redirecting " + req.getUserPrincipal() + " to " + displayAfterLoginUri);
        StringBuilder redirectTarget = new StringBuilder(displayAfterLoginUri);
        String extraParams = extractParameters(req);
        if (extraParams.length() > 0) {
            redirectTarget.append("?").append(extraParams);
        }

        resp.sendRedirect(redirectTarget.toString());
    }

    /**
     * Extracts all parameters except the username and password into a URL-encoded query string. The string does not begin
     * or end with a "&amp;".
     */
    @SuppressWarnings("unchecked")
    private static String extractParameters(HttpServletRequest fromRequest) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String[]> param : (Set<Map.Entry<String,String[]>>) fromRequest.getParameterMap().entrySet()) {
                String paramName = URLEncoder.encode(param.getKey(), "UTF-8");
                if (paramName.equals("j_username") || paramName.equals("j_password")) {
                    continue;
                }
                for (String value : param.getValue()) {
                    if (sb.length() != 0) {
                        sb.append("&");
                    }
                    sb.append(paramName).append("=").append(URLEncoder.encode(value, "UTF-8"));
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 not supported on this JVM?");
        }
    }

}