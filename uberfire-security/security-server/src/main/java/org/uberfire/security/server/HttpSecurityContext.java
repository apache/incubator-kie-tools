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

package org.uberfire.security.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.uberfire.security.Resource;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;

public class HttpSecurityContext implements SecurityContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final URLResource resource;
    private Subject subject;

    public HttpSecurityContext(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final Object... objects) {
        this.request = httpRequest;
        this.response = httpResponse;
        this.resource = new URLResource(request.getRequestURI());
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public Subject getCurrentSubject() {
        return subject;
    }

    public void setCurrentSubject(final Subject subject) {
        this.subject = subject;
    }

}
