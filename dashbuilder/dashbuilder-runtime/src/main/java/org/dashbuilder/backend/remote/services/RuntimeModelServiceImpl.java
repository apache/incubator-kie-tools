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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.backend.navigation.RuntimeNavigationBuilder;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.model.RuntimeServiceResponse;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.dashbuilder.shared.service.RuntimeModelService;
import org.dashbuilder.shared.services.ExternalImportService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
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
        List<String> availableModels = new ArrayList<>(registry.availableModels());
        return new RuntimeServiceResponse(registry.getMode(),
                                          getRuntimeModel(runtimeModelId),
                                          availableModels);
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
        Optional<RuntimeModel> runtimeModelOp = registry.get(id);
        if (runtimeModelOp.isPresent()) {
            return runtimeModelOp;
        }

        Optional<String> modelPath = runtimeOptions.modelPath(id);
        if (modelPath.isPresent()) {
            return registry.registerFile(modelPath.get());
        }

        if (runtimeOptions.isAllowExternal()) {
            return externalImportService.registerExternalImport(id);
        }
        return Optional.empty();
    }

}