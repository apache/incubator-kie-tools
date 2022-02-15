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

package org.dashbuilder.backend.remote.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.backend.navigation.RuntimeNavigationBuilder;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.dashbuilder.shared.service.ExternalImportService;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.dashbuilder.shared.service.RuntimeModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RuntimeModelServiceImpl implements RuntimeModelService {

    Logger logger = LoggerFactory.getLogger(RuntimeModelServiceImpl.class);

    @Inject
    RuntimeModelRegistry registry;

    @Inject
    RuntimeNavigationBuilder runtimeNavigationBuilder;

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    ExternalImportService externalImportService;

    @Override
    public RuntimeServiceResponse info(String runtimeModelId) {
        var availableModels = new ArrayList<>(registry.availableModels());
        return new RuntimeServiceResponse(registry.getMode(),
                                          getRuntimeModel(runtimeModelId),
                                          availableModels,
                                          runtimeOptions.isAllowUpload());
    }

    @Override
    public Optional<RuntimeModel> getRuntimeModel(String exportId) {
        if (!registry.acceptingNewImports()) {
            return registry.single();
        }

        if (exportId == null || exportId.trim().isEmpty()) {
            return Optional.empty();
        }

        return loadImportById(exportId);

    }

    /**
     * Attempts to load a model which could be a local file, an already loaded model or an external file.
     * @param id
     * The model id or path
     * @return
     * An optional containing the loaded model or empty.
     */
    private Optional<RuntimeModel> loadImportById(String id) {
        var runtimeModelOp = registry.get(id);
        if (runtimeModelOp.isPresent()) {
            return loadLatestModel(id, runtimeModelOp.get());
        }

        var modelPath = runtimeOptions.modelPath(id);
        if (modelPath.isPresent()) {
            return registry.registerFile(modelPath.get());
        }

        if (runtimeOptions.isAllowExternal()) {
            return externalImportService.registerExternalImport(id);
        }
        return Optional.empty();
    }

    private Optional<RuntimeModel> loadLatestModel(String id, RuntimeModel runtimeModel) {
        var modelPath = runtimeOptions.modelPath(id);
        if (runtimeOptions.isModelUpdate() && modelPath.isPresent()) {
            String modelFilePath = modelPath.get();
            if (lastModified(modelFilePath) > runtimeModel.getLastModified()) {
                logger.info("Replacing model {}", id);
                registry.unregister(id);
                return registry.registerFile(modelFilePath);
            }
        }
        return Optional.of(runtimeModel);
    }

    private long lastModified(String modelFilePath) {
        try {
            return Files.readAttributes(Paths.get(modelFilePath), BasicFileAttributes.class)
                        .lastModifiedTime()
                        .toMillis();
        } catch (IOException e) {
            logger.error("Error reading file last modified time");
            logger.debug("Error reading file last modified time", e);
            return -1;
        }
    }

}