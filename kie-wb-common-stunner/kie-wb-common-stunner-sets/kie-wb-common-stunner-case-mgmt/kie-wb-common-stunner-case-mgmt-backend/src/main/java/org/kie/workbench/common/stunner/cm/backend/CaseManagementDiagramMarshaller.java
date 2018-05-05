/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.BaseDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@Dependent
@CaseManagementEditor
public class CaseManagementDiagramMarshaller extends BaseDiagramMarshaller<CaseManagementDiagram> {

    @Inject
    public CaseManagementDiagramMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                           final @CaseManagementEditor GraphObjectBuilderFactory graphBuilderFactory,
                                           final DefinitionManager definitionManager,
                                           final GraphIndexBuilder<?> indexBuilder,
                                           final @CaseManagementEditor OryxManager oryxManager,
                                           final FactoryManager factoryManager,
                                           final DefinitionsCacheRegistry definitionsCacheRegistry,
                                           final RuleManager rulesManager,
                                           final GraphCommandManager graphCommandManager,
                                           final GraphCommandFactory commandFactory) {
        super(diagramMetadataMarshaller,
              graphBuilderFactory,
              definitionManager,
              indexBuilder,
              oryxManager,
              factoryManager,
              definitionsCacheRegistry,
              rulesManager,
              graphCommandManager,
              commandFactory);
    }

    @Override
    protected String getPreProcessingData(final Metadata metadata) {
        return null;
    }

    @Override
    public Class<?> getDiagramDefinitionSetClass() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    public Class<? extends BPMNDiagram> getDiagramDefinitionClass() {
        return CaseManagementDiagram.class;
    }
}
