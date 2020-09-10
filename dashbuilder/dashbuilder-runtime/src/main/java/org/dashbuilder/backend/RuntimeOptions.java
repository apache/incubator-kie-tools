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
 * Holds Runtime System properties and information.
 *
 */
@ApplicationScoped
public class RuntimeOptions {

    Logger logger = LoggerFactory.getLogger(RuntimeOptions.class);

    public static final String DASHBOARD_EXTENSION = ".zip";

    private static final String DEFAULT_MODEL_DIR = "/tmp/dashbuilder/models";

    private static final int DEFAULT_UPLOAD_SIZE_KB =  10 * 1024 * 1024;

    /**
     * Base Directory where dashboards ZIPs are stored
     */
    private static final String IMPORTS_BASE_DIR_PROP = "dashbuilder.import.base.dir";

    /**
     * Set a static dashboard to run with runtime. When this property is set no new imports are allowed.
     */
    private static final String IMPORT_FILE_LOCATION_PROP = "dashbuilder.runtime.import";

    /**
     * Limits the size of uploaded dashboards (in kb).
     */
    private static final String UPLOAD_SIZE_PROP = "dashbuilder.runtime.upload.size";

    /**
     * When true will allow download of external (remote) files into runtime.
     */
    private static final String ALLOW_EXTERNAL_FILE_REGISTER_PROP = "dashbuilder.runtime.allowExternal";

    /**
     * If set to true Runtime will always allow use of new imports (multi tenancy)
     */
    private static final String DASHBUILDER_RUNTIME_MULTIPLE_IMPORT_PROP = "dashbuilder.runtime.multi";

    /**
     * If true datasets IDs will partitioned by the Runtime Model ID.
     */
    private static final String DATASET_PARTITION_PROP = "dashbuilder.dataset.partition";
    
    /**
     * If true components will be partitioned by the Runtime Model ID.
     */
    private static final String COMPONENT_PARTITION_PROP = "dashbuilder.components.partition";

    private boolean multipleImport;
    private boolean datasetPartition;
    private boolean componentPartition;
    private boolean allowExternal;
    private String importFileLocation;
    private String importsBaseDir;
    private int uploadSize;

    @PostConstruct
    public void init() {
        String multipleImportStr = System.getProperty(DASHBUILDER_RUNTIME_MULTIPLE_IMPORT_PROP, Boolean.FALSE.toString());
        String allowExternalStr = System.getProperty(ALLOW_EXTERNAL_FILE_REGISTER_PROP, Boolean.FALSE.toString());
        String datasetPartitionStr = System.getProperty(DATASET_PARTITION_PROP, Boolean.TRUE.toString());
        String componentPartitionStr = System.getProperty(COMPONENT_PARTITION_PROP, Boolean.TRUE.toString());

        importFileLocation = System.getProperty(IMPORT_FILE_LOCATION_PROP);
        importsBaseDir = System.getProperty(IMPORTS_BASE_DIR_PROP, DEFAULT_MODEL_DIR);
        multipleImport = Boolean.parseBoolean(multipleImportStr);
        allowExternal = Boolean.parseBoolean(allowExternalStr);
        datasetPartition = Boolean.parseBoolean(datasetPartitionStr);
        componentPartition = Boolean.parseBoolean(componentPartitionStr);
        
        uploadSize = DEFAULT_UPLOAD_SIZE_KB;

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
     * Generates a new valid file path.
     * @param fileName
     * The fileName
     * @return
     */
    public Pair<String, String> newFilePath(final String fileName) {
        String newFileName = fileName;
        if (fileName == null || fileName.trim().isEmpty()) {
            newFileName = System.currentTimeMillis() + "";
        } else if (fileName.endsWith(DASHBOARD_EXTENSION)) {
            int lastIndex = fileName.length() - DASHBOARD_EXTENSION.length();
            newFileName = fileName.substring(0, lastIndex);
        }

        String filePath = buildFilePath(newFileName);
        return Pair.newPair(newFileName, filePath);
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

    public boolean isDatasetPartition() {
        return datasetPartition;
    }
    
    public boolean isComponentPartition() {
        return componentPartition;
    }

    public String buildFilePath(String fileId) {
        return String.join("/", getImportsBaseDir(), fileId).concat(DASHBOARD_EXTENSION);
    }

}
