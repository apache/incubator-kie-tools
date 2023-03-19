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

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.backend.services.RuntimeInfoService;
import org.dashbuilder.shared.model.DashboardInfo;
import org.dashbuilder.shared.model.DashbuilderRuntimeInfo;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class RuntimeInfoServiceImpl implements RuntimeInfoService {

    @Inject
    RuntimeModelRegistry registry;

    @Override
    public DashbuilderRuntimeInfo info() {
        return new DashbuilderRuntimeInfo(registry.getMode().name(),
                                          registry.availableModels(),
                                          registry.acceptingNewImports());
    }

    @Override
    public Optional<DashboardInfo> dashboardInfo(String modelId) {
        return registry.get(modelId)
                       .map(model -> new DashboardInfo(modelId, perspectives(model)));
    }

    private List<String> perspectives(RuntimeModel runtimeModel) {
        return runtimeModel.getLayoutTemplates().stream()
                           .map(LayoutTemplate::getName)
                           .collect(toList());
    }

}