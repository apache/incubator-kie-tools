/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.m2repo.backend.server;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guvnor.m2repo.backend.server.helpers.HttpGetHelper;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.backend.server.helpers.HttpPutHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class M2Servlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(M2Servlet.class);

    @Inject
    private HttpPostHelper httpPostHelper;

    @Inject
    private HttpPutHelper httpPutHelper;

    @Inject
    private HttpGetHelper httpGetHelper;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        log.info("GET request received for " + request.getPathInfo());
        httpGetHelper.handle(request,
                             response,
                             getServletContext());
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        log.info("POST request received.");
        httpPostHelper.handle(request,
                              response);
    }

    @Override
    protected void doPut(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        log.info("PUT request received for " + request.getPathInfo());
        httpPutHelper.handle(request,
                             response);
    }
}
