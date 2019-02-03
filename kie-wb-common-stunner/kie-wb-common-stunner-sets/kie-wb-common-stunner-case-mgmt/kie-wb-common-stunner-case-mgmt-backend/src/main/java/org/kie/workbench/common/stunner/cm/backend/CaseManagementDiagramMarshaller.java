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

import java.io.InputStream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.BaseDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2Marshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2UnMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.backend.marshall.json.CaseManagementMarshaller;
import org.kie.workbench.common.stunner.cm.backend.marshall.json.CaseManagementUnMarshaller;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
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

    @Override
    public Graph unmarshall(Metadata metadata, InputStream inputStream) {
        Graph result = super.unmarshall(metadata, inputStream);
        this.updateTitle(metadata, result);

        return result;
    }

    @Override
    public String marshall(Diagram diagram) {
        if (validateDiagram(diagram)) {
            return super.marshall(diagram);
        } else {
            throw new RuntimeException("Invalid definition for Case Modeler diagram.");
        }
    }

    /**
     * Check if name and id are assigned for the CM diagram
     * @param diagram the case modeler diagram
     * @return <code>true</code> if name and id are set, <code>false</code> if name or id is not set.
     */
    private boolean validateDiagram(Diagram diagram) {
        Node<Definition<CaseManagementDiagram>, ?> node = GraphUtils.getFirstNode(diagram.getGraph(), CaseManagementDiagram.class);
        if (node != null && node.getContent() != null) {
            CaseManagementDiagram definition = node.getContent().getDefinition();
            if (definition != null) {
                DiagramSet diagramSet = definition.getDiagramSet();
                if (diagramSet != null) {
                    Name name = diagramSet.getName();
                    Id id = diagramSet.getId();
                    return name != null && id != null
                            && name.getValue() != null && id.getValue() != null
                            && name.getValue().trim().length() > 0 && id.getValue().trim().length() > 0;
                }
            }
        }

        return false;
    }

    @Override
    protected Bpmn2Marshaller createBpmn2Marshaller(DefinitionManager definitionManager,
                                                    OryxManager oryxManager) {
        return new CaseManagementMarshaller(definitionManager,
                                            oryxManager);
    }

    @Override
    protected Bpmn2UnMarshaller createBpmn2UnMarshaller(GraphObjectBuilderFactory elementBuilderFactory,
                                                        DefinitionManager definitionManager,
                                                        FactoryManager factoryManager,
                                                        DefinitionsCacheRegistry definitionsCacheRegistry,
                                                        RuleManager rulesManager,
                                                        OryxManager oryxManager,
                                                        GraphCommandManager commandManager,
                                                        GraphCommandFactory commandFactory,
                                                        GraphIndexBuilder<?> indexBuilder,
                                                        Class<?> diagramDefinitionSetClass,
                                                        Class<? extends BPMNDiagram> diagramDefinitionClass) {
        return new CaseManagementUnMarshaller(elementBuilderFactory,
                                              definitionManager,
                                              factoryManager,
                                              definitionsCacheRegistry,
                                              rulesManager,
                                              oryxManager,
                                              commandManager,
                                              commandFactory,
                                              indexBuilder,
                                              diagramDefinitionSetClass,
                                              diagramDefinitionClass);
    }
}
