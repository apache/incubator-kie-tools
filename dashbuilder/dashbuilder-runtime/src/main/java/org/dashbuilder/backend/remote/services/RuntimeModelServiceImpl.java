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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.backend.navigation.RuntimeNavigationBuilder;
import org.dashbuilder.shared.model.RuntimeModel;
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
    RuntimeModelRegistry importModelRegistry;

    @Inject
    RuntimeNavigationBuilder runtimeNavigationBuilder;

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    ExternalImportService externalImportService;

    @Override
    public Optional<RuntimeModel> getRuntimeModel(String exportId) {
        if (exportId == null) {
            return importModelRegistry.single();
        }

        Optional<RuntimeModel> runtimeModelOp = importModelRegistry.get(exportId);
        if (runtimeModelOp.isPresent()) {
            return runtimeModelOp;
        }

        Optional<String> modelPath = runtimeOptions.modelPath(exportId);
        if (modelPath.isPresent()) {
            return importModelRegistry.registerFile(modelPath.get());
        }

        if (runtimeOptions.isAllowExternal()) {
            return externalImportService.registerExternalImport(exportId);
        }

        return Optional.empty();
    }

}