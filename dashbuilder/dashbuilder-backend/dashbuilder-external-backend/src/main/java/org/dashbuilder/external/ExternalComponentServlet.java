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

package org.dashbuilder.external;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.external.service.ExternalComponentAssetProvider;
import org.dashbuilder.external.service.ExternalComponentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.joining;

public class ExternalComponentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String CACHE_CONTROL_PARAM = "cache-control";

    private static final Logger logger = LoggerFactory.getLogger(ExternalComponentServlet.class);

    @Inject
    ExternalComponentAssetProvider assetProvider;

    @Inject
    ExternalComponentLoader loader;

    String cacheControlHeaderValue = "no-cache";
    private MimetypesFileTypeMap mimetypesFileTypeMap;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mimetypesFileTypeMap = new MimetypesFileTypeMap();
        String cacheControl = config.getInitParameter(CACHE_CONTROL_PARAM);
        if (cacheControl != null) {
            cacheControlHeaderValue = cacheControl;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            handle(req, resp);
        } catch (IOException e) {
            logger.error("Error handling request to retrieve asset.");
            logger.debug("Error handling request to retrieve asset.", e);
            errorResponse(resp);
        }
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.reset();

        if (!loader.isEnabled()) {
            logger.debug("Ignoring request because External Components API is disabled.");
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            badRequest(resp);
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 3) {
            badRequest(resp);
            return;
        }

        String assetPath = Arrays.stream(pathParts).skip(1).collect(joining(File.separator));

        logger.debug("Retrieving component asset {}", assetPath);

        try (InputStream assetStream = assetProvider.openAsset(assetPath)) {
            int size = IOUtils.copy(assetStream, resp.getOutputStream());
            String mimeType = mimetypesFileTypeMap.getContentType(pathInfo);
            resp.setContentType(mimeType);
            resp.setContentLength(size);
            resp.setHeader(CACHE_CONTROL_PARAM, cacheControlHeaderValue);
        } catch (Exception e) {
            logger.info("Not able to find component asset {}", assetPath);
            logger.debug("Error opening external component asset", e);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void badRequest(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void errorResponse(HttpServletResponse resp) {
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error("Error setting \"internal server error\" response.");
            logger.debug("Error setting \"internal server error\" response.", e);
        }
    }

}