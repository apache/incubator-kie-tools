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

package org.dashbuilder.backend.services.impl;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.dashbuilder.shared.services.ExternalImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExternalImportServiceImpl implements ExternalImportService {

    private static final String ERROR_PARSING_URL = "Error parsing URL: {}";

    Logger logger = LoggerFactory.getLogger(ExternalImportServiceImpl.class);

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    RuntimeModelRegistry runtimeModelRegistry;

    @Override
    public Optional<RuntimeModel> registerExternalImport(String externalModelUrl) {
        String modelId = "";
        URL url = getExternalModelUrl(externalModelUrl);
        modelId = buildURLIdentifier(url);

        final String filePath = runtimeOptions.buildFilePath(modelId);
        int totalBytes = 0;
        final int pageSize = 1024;
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[pageSize];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, pageSize)) != -1) {
                fos.write(dataBuffer, 0, bytesRead);
                totalBytes += pageSize;
                checkSize(filePath, totalBytes);
            }
        } catch (IOException e) {
            logger.debug("Error downloading and parsing content from URL {}", externalModelUrl, e);
            logger.warn("Error downloading and parsing content from URL {}", externalModelUrl);
            deleteFile(filePath);
            throw new IllegalArgumentException("Not able to download file", e);
        }
        return runtimeModelRegistry.registerFile(filePath);
    }

    private void checkSize(final String filePath, int totalBytes) {
        if (totalBytes > runtimeOptions.getUploadSize()) {
            deleteFile(filePath);
            logger.error("Size file is bigger than max upload size {}", runtimeOptions.getUploadSize());
            throw new IllegalArgumentException("External file size is too big.");
        }
    }

    private String buildURLIdentifier(URL url) {
        try {
            return Math.abs(url.toURI().hashCode()) + "";
        } catch (URISyntaxException e) {
            logger.debug(ERROR_PARSING_URL, url.toExternalForm(), e);
            logger.warn(ERROR_PARSING_URL, url.toExternalForm());
            throw new IllegalArgumentException("Not a valid URL: " + url.toExternalForm(), e);
        }
    }

    private URL getExternalModelUrl(String externalModelUrl) {
        try {
            return new URL(externalModelUrl);
        } catch (Exception e) {
            logger.debug(ERROR_PARSING_URL, externalModelUrl, e);
            logger.error(ERROR_PARSING_URL, externalModelUrl);
            throw new IllegalArgumentException("Not a valid URL: " + externalModelUrl, e);
        }
    }

    private void deleteFile(final String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            logger.error("Error deleting bad model file: {}", filePath, e);
        }
    }

}