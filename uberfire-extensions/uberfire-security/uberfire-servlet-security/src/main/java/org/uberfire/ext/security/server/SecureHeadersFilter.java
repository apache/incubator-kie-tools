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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HSTS servlet filter
 * For a detailed explanation please take a look at <a href="http://aerogear.org/docs/guides/aerogear-security/">http://aerogear.org/docs/guides/aerogear-security/</a>
 * <p>
 * Note: This implementation has been borrowed from Aerogear Security.
 */
public class SecureHeadersFilter implements Filter {

    public static final String LOCATION = "Location";
    public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    public static final String X_FRAME_OPTIONS = "X-FRAME-OPTIONS";
    public static final String X_XSS_OPTIONS = "X-XSS-Protection";

    private static SecureHeadersConfig config;

    public static void applyHeaders(final ServletRequest request,
                                    final HttpServletResponse response) {
        if (config != null) {
            addLocation(response);
            addFrameOptions(response);
            addXSSOptions(response);

            if (request.getScheme().equals("https")) {
                addStrictTransportSecurity(response);
            }
        }
    }

    private static void addStrictTransportSecurity(HttpServletResponse response) {
        if (config.hasMaxAge() && empty(response.getHeader(STRICT_TRANSPORT_SECURITY))) {
            response.addHeader(STRICT_TRANSPORT_SECURITY,
                               config.getMaxAge());
        }
    }

    private static void addFrameOptions(HttpServletResponse response) {
        if (config.hasFrameOptions() && empty(response.getHeader(X_FRAME_OPTIONS))) {
            response.addHeader(X_FRAME_OPTIONS,
                               config.getFrameOptions());
        }
    }

    private static void addLocation(HttpServletResponse response) {
        if (config.hasLocation() && empty(response.getHeader(LOCATION))) {
            response.addHeader(LOCATION,
                               config.getLocation());
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        }
    }

    private static void addXSSOptions(HttpServletResponse response) {
        if (config.hasXSSOptions() && empty(response.getHeader(X_XSS_OPTIONS))) {
            response.addHeader(X_XSS_OPTIONS,
                               config.getXssOptions());
        }
    }

    private static boolean empty(final String content) {
        return content == null || content.trim().isEmpty();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        config = new SecureHeadersConfig(filterConfig);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain chain) throws IOException, ServletException {

        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        applyHeaders(request,
                     response);

        chain.doFilter(request,
                       response);
    }
}