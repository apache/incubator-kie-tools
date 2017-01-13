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

package org.kie.workbench.common.stunner.bpmn.backend;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.util.BPMNUtils;
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
public class BPMNDiagramMarshaller extends BaseDiagramMarshaller<BPMNDiagram> {

    @Inject
    public BPMNDiagramMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
                                 final GraphObjectBuilderFactory bpmnGraphBuilderFactory,
                                 final DefinitionManager definitionManager,
                                 final GraphUtils graphUtils,
                                 final GraphIndexBuilder<?> indexBuilder,
                                 final OryxManager oryxManager,
                                 final FactoryManager factoryManager,
                                 final GraphCommandManager graphCommandManager,
                                 final GraphCommandFactory commandFactory) {
        super(diagramMetadataMarshaller,
              bpmnGraphBuilderFactory,
              definitionManager,
              graphUtils,
              indexBuilder,
              oryxManager,
              factoryManager,
              graphCommandManager,
              commandFactory);
    }

    @Override
    public Class<?> getDiagramDefinitionSetClass() {
        return BPMNDefinitionSet.class;
    }

    @Override
    public Class<? extends BPMNDefinition> getDiagramDefinitionClass() {
        return BPMNDiagram.class;
    }

    @Override
    public String getTitle(final Graph graph) {
        final Node<Definition<BPMNDiagram>, ?> diagramNode = getFirstDiagramNode(graph);
        final BPMNDiagram diagramBean = null != diagramNode ? (BPMNDiagram) ((Definition) diagramNode.getContent()).getDefinition() : null;
        return getTitle(diagramBean);
    }

    private String getTitle(final BPMNDiagram diagram) {
        final String title = diagram.getDiagramSet().getName().getValue();
        return title != null && title.trim().length() > 0 ? title : "-- Untitled BPMN2 diagram --";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node<Definition<BPMNDiagram>, ?> getFirstDiagramNode(final Graph graph) {
        return BPMNUtils.getFirstDiagramNode(graph);
    }
}
