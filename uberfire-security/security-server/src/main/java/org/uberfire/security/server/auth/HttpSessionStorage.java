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

import javax.servlet.http.HttpSession;

import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticatedStorageProvider;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.server.HttpSecurityContext;

import static org.kie.commons.validation.Preconditions.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class HttpSessionStorage implements AuthenticatedStorageProvider {

    @Override
    public Principal load(final SecurityContext context) {
        final HttpSecurityContext httpContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        return (Principal) httpContext.getRequest().getSession().getAttribute(SUBJECT_ON_SESSION_KEY);
    }

    @Override
    public void store(final SecurityContext context, final Subject subject) {
        final HttpSecurityContext httpContext = checkInstanceOf("context", context, HttpSecurityContext.class);

        final HttpSession session = httpContext.getRequest().getSession();
        session.setAttribute(SUBJECT_ON_SESSION_KEY, subject);
    }

    @Override
    public void cleanup(final SecurityContext context) {
        final HttpSecurityContext httpContext = checkInstanceOf("context", context, HttpSecurityContext.class);
        httpContext.getRequest().getSession().removeAttribute(SUBJECT_ON_SESSION_KEY);
    }
}
