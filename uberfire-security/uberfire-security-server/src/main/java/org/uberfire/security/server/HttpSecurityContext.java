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

public class HttpSecurityContext extends MapSecurityContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpSecurityContext( final HttpServletRequest httpRequest,
                                final HttpServletResponse httpResponse,
                                final Object... objects ) {
        super( buildResource( httpRequest ) );
        this.request = httpRequest;
        this.response = httpResponse;
    }

    private static Resource buildResource( HttpServletRequest request ) {
        final StringBuilder url = new StringBuilder( request.getServletPath() );
        if ( request.getQueryString() != null ) {
            url.append( "?" ).append( request.getQueryString() );
        }

        return new URLResource( url.toString() );
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
}
