/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.BaseDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagement;
import org.kie.workbench.common.stunner.cm.util.CaseManagementUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@Dependent
@CaseManagement
public class CaseManagementDiagramMarshaller extends BaseDiagramMarshaller<CaseManagementDiagram> {

    @Inject
    public CaseManagementDiagramMarshaller( final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                            final @CaseManagement GraphObjectBuilderFactory bpmnGraphBuilderFactory,
                                            final DefinitionManager definitionManager,
                                            final GraphUtils graphUtils,
                                            final GraphIndexBuilder<?> indexBuilder,
                                            final @CaseManagement OryxManager oryxManager,
                                            final FactoryManager factoryManager,
                                            final GraphCommandManager graphCommandManager,
                                            final GraphCommandFactory commandFactory ) {
        super( diagramMetadataMarshaller,
               bpmnGraphBuilderFactory,
               definitionManager,
               graphUtils,
               indexBuilder,
               oryxManager,
               factoryManager,
               graphCommandManager,
               commandFactory );
    }

    @Override
    public Class<?> getDiagramDefinitionSetClass() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    public Class<? extends BPMNDefinition> getDiagramDefinitionClass() {
        return CaseManagementDiagram.class;
    }

    @Override
    public String getTitle( final Graph graph ) {
        final Node<Definition<CaseManagementDiagram>, ?> diagramNode = getFirstDiagramNode( graph );
        final CaseManagementDiagram diagramBean = null != diagramNode ? ( CaseManagementDiagram ) ( ( Definition ) diagramNode.getContent() ).getDefinition() : null;
        return getTitle( diagramBean );
    }

    private String getTitle( final CaseManagementDiagram diagram ) {
        final String title = diagram.getDiagramSet().getName().getValue();
        return title != null && title.trim().length() > 0 ? title : "-- Untitled Case Management diagram --";
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Node<Definition<CaseManagementDiagram>, ?> getFirstDiagramNode( final Graph graph ) {
        return CaseManagementUtils.getFirstDiagramNode( graph );
    }
}
