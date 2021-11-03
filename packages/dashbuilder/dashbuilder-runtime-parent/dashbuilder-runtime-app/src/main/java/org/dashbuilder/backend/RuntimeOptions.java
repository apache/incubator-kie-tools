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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.backend.model.RuntimeModelFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds Runtime System properties and information.
 *
 */
@ApplicationScoped
public class RuntimeOptions {

    Logger logger = LoggerFactory.getLogger(RuntimeOptions.class);

    public static final String DASHBOARD_EXTENSION = ".zip";

    private static final String DEFAULT_MODEL_DIR = "/tmp/dashbuilder/models";

    private static final int DEFAULT_UPLOAD_SIZE_KB = 10 * 1024 * 1024;

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

    /**
     * Boolean property that allows Runtime to check model last update in FS to update its content.
     */
    private static final String MODEL_UPDATE_PROP = "dashbuilder.model.update";

    /**
     * Boolean property when true will also remove actual model file from file system.
     */
    private static final String MODEL_FILE_REMOVAL_PROP = "dashbuilder.removeModelFile";

    /**
     * Boolean property when true will make Dashbuilder Runtime run on dev mode.
     */
    private static final String DEV_MODE_PROP = "dashbuilder.dev";
    
    /**
     * Boolean property when true will make dashbuilder watch the models dir to dynamically import models
     */
    private static final String WATCH_MODELS_PROP = "dashbuilder.models.watch";
    
    /**
     * Boolean property to determine if new uploads are allowed in multi mode
     */
    private static final String ALLOW_UPLOAD_PROP = "dashbuilder.runtime.allowUpload";

    private boolean multipleImport;
    private boolean datasetPartition;
    private boolean componentPartition;
    private boolean allowExternal;
    private boolean modelUpdate;
    private boolean removeModelFile;
    private boolean devMode;
    private boolean allowUpload;
    boolean watchModels;
    private String importFileLocation;
    private String importsBaseDir;
    private int uploadSize;

    @PostConstruct
    public void init() {

        importFileLocation = System.getProperty(IMPORT_FILE_LOCATION_PROP);
        importsBaseDir = System.getProperty(IMPORTS_BASE_DIR_PROP, DEFAULT_MODEL_DIR);

        multipleImport = booleanProp(DASHBUILDER_RUNTIME_MULTIPLE_IMPORT_PROP, Boolean.FALSE);
        allowExternal = booleanProp(ALLOW_EXTERNAL_FILE_REGISTER_PROP, Boolean.FALSE);
        datasetPartition = booleanProp(DATASET_PARTITION_PROP, Boolean.TRUE);
        componentPartition = booleanProp(COMPONENT_PARTITION_PROP, Boolean.TRUE);
        modelUpdate = booleanProp(MODEL_UPDATE_PROP, Boolean.TRUE);
        removeModelFile = booleanProp(MODEL_FILE_REMOVAL_PROP, Boolean.FALSE);
        devMode = booleanProp(DEV_MODE_PROP, Boolean.FALSE);
        watchModels = booleanProp(WATCH_MODELS_PROP, Boolean.FALSE);
        allowUpload = booleanProp(ALLOW_UPLOAD_PROP, Boolean.FALSE);
        uploadSize = DEFAULT_UPLOAD_SIZE_KB;

        var uploadSizeStr = System.getProperty(UPLOAD_SIZE_PROP);
        if (uploadSizeStr != null) {
            try {
                uploadSize = 1024 * Integer.parseInt(uploadSizeStr);
            } catch (Exception e) {
                logger.warn("Not able to parse upload size {}", uploadSizeStr);
                logger.debug("Not able to parse upload size {}", uploadSizeStr, e);
            }
        }
        logger.info("Max upload size is {}", uploadSize);
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
        var filePath = buildFilePath(id);
        return Paths.get(filePath).toFile().exists() ? Optional.of(filePath) : Optional.empty();
    }

    /**
     * Generates a new valid file path.
     * @param fileName
     * The fileName
     * @return
     */
    public RuntimeModelFileInfo newFilePath(final String fileName) {
        var newFileName = fileName;
        if (fileName == null || fileName.trim().isEmpty()) {
            newFileName = System.currentTimeMillis() + "";
        } else if (fileName.endsWith(DASHBOARD_EXTENSION)) {
            int lastIndex = fileName.length() - DASHBOARD_EXTENSION.length();
            newFileName = fileName.substring(0, lastIndex);
        }

        var filePath = buildFilePath(newFileName);
        return RuntimeModelFileInfo.newRuntimeModelFileInfo(newFileName, filePath);
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

    public boolean isModelUpdate() {
        // dev mode forces model update
        return modelUpdate || devMode;
    }

    public boolean isRemoveModelFile() {
        return removeModelFile;
    }

    public boolean isDevMode() {
        return devMode;
    }
    
    public boolean isWatchModels() {
        // dev mode requires to watch models
        return watchModels || devMode;
    }
    
    public boolean isAllowUpload() {
        return allowUpload;
    }
    
    public String buildFilePath(String fileId) {
        Path modelFile = Paths.get(fileId + DASHBOARD_EXTENSION);
        return Paths.get(getImportsBaseDir()).resolve(modelFile).toString();
    }

    private boolean booleanProp(String prop, Boolean defaultValue) {
        String propStr = System.getProperty(prop, defaultValue.toString());
        return Boolean.parseBoolean(propStr);
    }

}