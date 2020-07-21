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

package org.dashbuilder.backend;

import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;

/**
 * Holds Runtime System properties
 *
 */
@ApplicationScoped
public class RuntimeOptions {

    Logger logger = LoggerFactory.getLogger(RuntimeOptions.class);

    private static final String IMPORTS_BASE_DIR_PROP = "org.dashbuilder.import.base.dir";

    private static final String IMPORT_FILE_LOCATION_PROP = "dashbuilder.runtime.import";

    private static final String UPLOAD_SIZE_PROP = "dashbuilder.runtime.upload.size";

    private static final String ALLOW_EXTERNAL_FILE_REGISTER_PROP = "dashbuilder.runtime.allowExternal";

    private static final String DASHBUILDER_RUNTIME_MULTIPLE_IMPORT = "dashbuilder.runtime.multiple";

    private static final int DEFAULT_UPLOAD_SIZE = 96 * 1024;

    private boolean multipleImport;
    private boolean allowExternal;
    private String importFileLocation;
    private String importsBaseDir;
    private int uploadSize;

    @PostConstruct
    public void init() {
        String multipleImportStr = System.getProperty(DASHBUILDER_RUNTIME_MULTIPLE_IMPORT, Boolean.FALSE.toString());
        String allowExternalStr = System.getProperty(ALLOW_EXTERNAL_FILE_REGISTER_PROP, Boolean.FALSE.toString());

        importFileLocation = System.getProperty(IMPORT_FILE_LOCATION_PROP);
        importsBaseDir = System.getProperty(IMPORTS_BASE_DIR_PROP, "/tmp/dashbuilder");
        multipleImport = Boolean.parseBoolean(multipleImportStr);
        allowExternal = Boolean.parseBoolean(allowExternalStr);

        uploadSize = DEFAULT_UPLOAD_SIZE;

        String uploadSizeStr = System.getProperty(UPLOAD_SIZE_PROP);
        if (uploadSizeStr != null) {
            try {
                uploadSize = 1024 * Integer.parseInt(uploadSizeStr);
            } catch (Exception e) {
                logger.warn("Not able to parse upload size {}", uploadSizeStr);
                logger.debug("Not able to parse upload size {}", uploadSizeStr, e);
            }
        }
    }

    /**
     * 
     * Returns the model path for the given id
     * @param id
     * The model ID 
     * @return
     * An optional containing the file path or an empty optional otherwise.
     */
    public Optional<String> modelPath(String id) {
        String filePath = buildFilePath(id);
        return Paths.get(filePath).toFile().exists() ? Optional.of(filePath) : Optional.empty();
    }

    /**
     * Generates a new valid file path
     * 
     * @return
     */
    public Pair<String, String> newFilePath() {
        String fileId = System.currentTimeMillis() + "";
        String filePath = buildFilePath(fileId);
        return Pair.newPair(fileId, filePath);
    }

    public boolean isMultipleImport() {
        return multipleImport;
    }

    public Optional<String> importFileLocation() {
        return Optional.ofNullable(importFileLocation);
    }

    public String getImportsBaseDir() {
        return importsBaseDir;
    }

    public int getUploadSize() {
        return uploadSize;
    }

    public boolean isAllowExternal() {
        return allowExternal;
    }

    public String buildFilePath(String fileId) {
        return String.join("/", getImportsBaseDir(), fileId).concat(".zip");
    }

}