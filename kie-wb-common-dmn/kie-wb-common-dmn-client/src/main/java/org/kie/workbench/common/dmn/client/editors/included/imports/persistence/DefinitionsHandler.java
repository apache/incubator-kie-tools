/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.Map;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class DefinitionsHandler {

    private final IncludedModelsPageStateProviderImpl stateProvider;

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public DefinitionsHandler(final IncludedModelsPageStateProviderImpl stateProvider,
                              final DMNGraphUtils dmnGraphUtils) {
        this.stateProvider = stateProvider;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public void destroy(final IncludedModel includedModel) {
        NamespaceHandler.removeIncludedNamespace(getNsContext(), includedModel.getNamespace());
    }

    public void create(final IncludedModel includedModel) {
        NamespaceHandler.addIncludedNamespace(getNsContext(), includedModel.getNamespace());
    }

    private Map<String, String> getNsContext() {
        return stateProvider
                .getDiagram()
                .map(dmnGraphUtils::getDefinitions)
                .map(DMNModelInstrumentedBase::getNsContext)
                .orElseThrow(UnsupportedOperationException::new);
    }
}
