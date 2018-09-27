/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.verifier.service;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifierWebWorkerServlet
        extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifierWebWorkerServlet.class);

    @Inject
    private VerifierWebWorkerRegistry verifierWebWorkerRegistry;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws
            ServletException,
            IOException {

        LOGGER.debug("Loading verifier web worker");

        try {
            final String requestURI = request.getRequestURI();
            final int indexOf = requestURI.indexOf("/verifier");

            if (indexOf >= 0) {

                final String fileName = requestURI.substring(indexOf + "/verifier".length());

                if (fileName.endsWith("cache.js")) {
                    final Optional<VerifierWebWorkerProvider> verifierWebWorkerProvider = verifierWebWorkerRegistry.get(trimId(fileName));

                    if (!verifierWebWorkerProvider.isPresent()) {
                        LOGGER.error("Failed to load verifier web worker. Verifier with id " + fileName + " was not found.");
                    } else {
                        final byte[] bytes = verifierWebWorkerProvider.get().getWebWorker(fileName).getBytes();

                        response.setContentType("application/javascript");

                        response.getOutputStream()
                                .write(
                                        bytes,
                                        0,
                                        bytes.length);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Failed to load verifier web worker.");
        }
    }

    private String trimId(final String fileName) {
        String result = fileName.substring(1, fileName.length());
        result = result.substring(0, result.indexOf("/"));
        return result;
    }
}
