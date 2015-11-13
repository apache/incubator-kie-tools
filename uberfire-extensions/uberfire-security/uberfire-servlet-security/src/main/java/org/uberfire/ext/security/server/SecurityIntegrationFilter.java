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
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.marshalling.server.MappingContextSingleton;

public class SecurityIntegrationFilter implements Filter {

    public static final String PROBE_ROLES_INIT_PARAM = "probe-for-roles";

    private static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException {
        MappingContextSingleton.get();

        String commaSeparatedRoles = filterConfig.getInitParameter( PROBE_ROLES_INIT_PARAM );
        if ( commaSeparatedRoles != null ) {
            for ( final String role : Collections.unmodifiableList( Arrays.asList( commaSeparatedRoles.split( "," ) ) ) ) {
                RolesRegistry.get().registerRole( role );
            }
        }
    }

    @Override
    public void destroy() {
        // no op
    }

    @Override
    public void doFilter( ServletRequest request,
                          ServletResponse response,
                          FilterChain chain ) throws IOException, ServletException {
        requests.set( (HttpServletRequest) request );
        try {
            chain.doFilter( request, response );
        } finally {
            requests.remove();
        }
    }

    /**
     * Returns the current servlet request that this thread is handling, or null if this thread is not currently handling
     * a servlet request.
     */
    public static HttpServletRequest getRequest() {
        return requests.get();
    }
}