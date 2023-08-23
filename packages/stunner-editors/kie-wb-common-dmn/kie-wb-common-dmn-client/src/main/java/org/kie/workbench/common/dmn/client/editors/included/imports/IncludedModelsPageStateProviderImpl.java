/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static java.util.Collections.emptyList;

/**
 * Provides data for the {@link IncludedModelsPage} class, considering the {@link Import} context.
 */
@ApplicationScoped
public class IncludedModelsPageStateProviderImpl implements IncludedModelsPageStateProvider {

    private final DMNGraphUtils dmnGraphUtils;

    private final IncludedModelsFactory factory;

    private final DMNDiagramsSession dmnDiagramsSession;

    public IncludedModelsPageStateProviderImpl() {
        this(null, null, null);
    }

    @Inject
    public IncludedModelsPageStateProviderImpl(final DMNGraphUtils dmnGraphUtils,
                                               final IncludedModelsFactory factory,
                                               final DMNDiagramsSession dmnDiagramsSession) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.factory = factory;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    @Override
    public String getCurrentDiagramNamespace() {
        return getDiagram().map(diagram -> getDefinitions(diagram).getNamespace().getValue()).orElse("");
    }

    @Override
    public List<BaseIncludedModelActiveRecord> generateIncludedModels() {
        return factory.makeIncludedModels(getImports());
    }

    public List<Import> getImports() {
        return getDiagram()
                .map(this::getImports)
                .orElse(emptyList());
    }

    public Optional<Diagram> getDiagram() {
        return Optional.ofNullable(dmnDiagramsSession.getDRGDiagram());
    }

    private List<Import> getImports(final Diagram diagram) {
        return getDefinitions(diagram).getImport();
    }

    private Definitions getDefinitions(final Diagram diagram) {
        return dmnGraphUtils.getDefinitions(diagram);
    }
}
