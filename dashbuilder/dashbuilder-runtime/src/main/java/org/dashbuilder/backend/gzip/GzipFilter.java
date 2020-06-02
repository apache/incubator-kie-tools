/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.gzip;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.ws.rs.core.HttpHeaders.ACCEPT_ENCODING;

// NOTE: Brought this from uberfire-backend-server
public class GzipFilter implements Filter {

    static final String GZIP = "gzip";

    static final String ORG_UBERFIRE_GZIP_ENABLE = "org.uberfire.gzip.enable";

    public void init(final FilterConfig filterConfig) {
        // Empty on purpose
    }

    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {

        switch (getAction(req)) {
            case HALT:
                break;
            case DO_NOT_ACCEPT_GZIP:
            case DO_NOT_COMPRESS:
                chain.doFilter(req, res);
                break;
            case COMPRESS:
                compressAndContinue(req, (HttpServletResponse) res, chain);
                break;
        }
    }

    void compressAndContinue(final ServletRequest req,
                             final HttpServletResponse res,
                             final FilterChain chain) throws IOException, ServletException {

        final GzipHttpServletResponseWrapper wResponse = new GzipHttpServletResponseWrapper(res);
        chain.doFilter(req, wResponse);
        wResponse.close();
    }

    Action getAction(final ServletRequest req) {

        final String enabled = System.getProperty(ORG_UBERFIRE_GZIP_ENABLE);
        if (enabled != null && !enabled.equals("true")) {
            return Action.DO_NOT_COMPRESS;
        }

        if (!(req instanceof HttpServletRequest)) {
            return Action.HALT;
        }

        final String acceptEncodingHeader = ((HttpServletRequest) req).getHeader(ACCEPT_ENCODING);
        if (acceptEncodingHeader == null || !acceptEncodingHeader.contains(GZIP)) {
            return Action.DO_NOT_ACCEPT_GZIP;
        }

        return Action.COMPRESS;
    }

    public void destroy() {
        // Empty on purpose
    }

    enum Action {
        HALT,
        DO_NOT_COMPRESS,
        DO_NOT_ACCEPT_GZIP,
        COMPRESS;
    }
}

