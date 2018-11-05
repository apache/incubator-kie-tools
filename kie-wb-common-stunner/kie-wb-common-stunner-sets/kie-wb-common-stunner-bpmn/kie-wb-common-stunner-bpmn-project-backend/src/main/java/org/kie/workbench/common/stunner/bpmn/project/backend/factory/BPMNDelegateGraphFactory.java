/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;

@Dependent
public class BPMNDelegateGraphFactory implements BPMNGraphFactory {

    private final BPMNGraphFactoryImpl bpmnGraphFactory;
    private final CaseGraphFactoryImpl caseGraphFactory;
    private final GraphFactoryDelegation graphFactoryDelegation;

    private class GraphFactoryDelegation {

        Map<ProjectType, BPMNGraphFactory> factoryMap;

        GraphFactoryDelegation() {
            init();
        }

        void init() {
            factoryMap = new HashMap<>();
            factoryMap.put(ProjectType.CASE, caseGraphFactory);
            factoryMap.put(ProjectType.BPMN, bpmnGraphFactory);
        }

        BPMNGraphFactory get(Optional<ProjectType> projectType) {
            return projectType.map(factoryMap::get).orElse(bpmnGraphFactory);
        }
    }

    @Inject
    public BPMNDelegateGraphFactory(final BPMNGraphFactoryImpl bpmnGraphFactory,
                                    final CaseGraphFactoryImpl caseGraphFactory) {
        this.bpmnGraphFactory = bpmnGraphFactory;
        this.caseGraphFactory = caseGraphFactory;
        this.graphFactoryDelegation = new GraphFactoryDelegation();
    }

    @Override
    public void setDiagramType(Class<? extends BPMNDiagram> diagramType) {
        bpmnGraphFactory.setDiagramType(diagramType);
        caseGraphFactory.setDiagramType(diagramType);
    }

    @Override
    public Class<? extends ElementFactory> getFactoryType() {
        return BPMNGraphFactory.class;
    }

    @Override
    public Graph<DefinitionSet, Node> build(String uuid, String definition, Metadata metadata) {
        final Optional<ProjectType> projectType = (Objects.nonNull(metadata) && metadata instanceof ProjectMetadata)
                ? Optional.ofNullable(((ProjectMetadata) metadata).getProjectType()).map(ProjectType::valueOf)
                : Optional.empty();
        return graphFactoryDelegation.get(projectType).build(uuid, definition, metadata);
    }

    @Override
    public Graph<DefinitionSet, Node> build(String uuid, String definition) {
        return build(uuid, definition, null);
    }

    @Override
    public boolean accepts(String source) {
        return bpmnGraphFactory.accepts(source) && caseGraphFactory.accepts(source);
    }
}