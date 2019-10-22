/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.standalone.backend;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CORSFilter
 */
// Enable it for Servlet 3.x implementations
@WebFilter(asyncSupported = true, urlPatterns = {"/*"})
public class CORSFilter implements Filter {

    /**
     * Default constructor.
     */
    public CORSFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        // Authorize (allow) all domains to consume the content
        final HttpServletResponse r = (HttpServletResponse) servletResponse;
        String origin = request.getHeader("origin");
        r.addHeader("Access-Control-Allow-Origin", origin);
        r.addHeader("Access-Control-Allow-Headers",
                    "Accept,Accept-Encoding,Accept-Language,Cache-Control,Connection,Content-Length,Content-Type," +
                            "Cookie,Host,Pragma,Referer,RemoteQueueID,User-Agent");
        r.addHeader("Access-Control-Allow-Credentials", "true");
        r.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

        final HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
        if (request.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        // pass the request along the filter chain
        chain.doFilter(request, servletResponse);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(final FilterConfig fConfig) throws ServletException {
    }
}