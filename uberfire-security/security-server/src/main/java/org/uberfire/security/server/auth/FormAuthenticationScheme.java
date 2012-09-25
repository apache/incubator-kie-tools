/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.security.server.auth;

import javax.servlet.RequestDispatcher;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.HttpSecurityContext;
import org.uberfire.security.server.SecurityConstants;

import static org.uberfire.commons.util.PreconditionsServer.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class FormAuthenticationScheme implements AuthenticationScheme {

    public boolean isAuthenticationRequest(final SecurityContext context) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        return httpSecurityContext.getRequest().getRequestURI().contains(HTTP_FORM_J_SECURITY_CHECK);
    }

    @Override
    public void challengeClient(SecurityContext context) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        final RequestDispatcher rd = httpSecurityContext.getRequest().getRequestDispatcher(SecurityConstants.FORM_AUTH_PAGE);

        if (rd == null) {
            throw new RuntimeException("Unable to resolve RequestDispatcher.");
        }

        try {
            rd.forward(httpSecurityContext.getRequest(), httpSecurityContext.getResponse());
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }

    public Credential buildCredential(final SecurityContext context) {
        final HttpSecurityContext httpSecurityContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        final String userName = httpSecurityContext.getRequest().getParameter(HTTP_FORM_J_USERNAME);
        final String password = httpSecurityContext.getRequest().getParameter(HTTP_FORM_J_PASSWORD);

        if (userName == null || password == null) {
            return null;
        }

        return new UsernamePasswordCredential(userName, password);
    }

}
