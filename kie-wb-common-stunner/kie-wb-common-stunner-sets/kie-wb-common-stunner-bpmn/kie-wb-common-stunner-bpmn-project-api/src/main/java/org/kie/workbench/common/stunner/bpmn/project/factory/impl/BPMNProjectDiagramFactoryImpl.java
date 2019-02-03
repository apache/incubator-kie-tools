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
package org.kie.workbench.common.stunner.bpmn.project.factory.impl;

import javax.enterprise.context.Dependent;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.bpmn.factory.AbstractBPMNDiagramFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;

/**
 * Custom BPMN factory instance for Diagrams on the Project context.
 * This factory initializes new BPMN diagrams with some specific project attributes ( given from the current
 * Project context ).
 * No need use use the Command API to set the diagram property values for the following reasons:
 * - No need to check runtime rules.
 * - No need to perform further undo/redos for these operations.
 */
@Dependent
public class BPMNProjectDiagramFactoryImpl
        extends AbstractBPMNDiagramFactory<ProjectMetadata, ProjectDiagram>
        implements BPMNProjectDiagramFactory {

    public BPMNProjectDiagramFactoryImpl() {
        setDiagramType(BPMNDiagramImpl.class);
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    @Override
    public ProjectDiagram doBuild(final String name,
                                  final ProjectMetadata metadata,
                                  final Graph<DefinitionSet, ?> graph) {
        return new ProjectDiagramImpl(name,
                                      graph,
                                      metadata);
    }

    @Override
    protected void updateDiagramProperties(final String name,
                                           final Node<Definition<BPMNDiagram>, ?> diagramNode,
                                           final ProjectMetadata metadata) {
        // Set kie related properties for the current project.
        final BPMNDiagram diagram = diagramNode.getContent().getDefinition();
        final BaseDiagramSet diagramSet = diagram.getDiagramSet();
        final String id = diagramSet.getId().getValue();
        if (id == null || id.isEmpty()) {
            final String projectName = null != metadata.getModuleName() ? metadata.getModuleName() + "." : "";
            diagramSet.getId().setValue(projectName + name);
        }
        final String p = diagramSet.getPackageProperty().getValue();
        if (p == null || p.isEmpty()) {
            final Package metadataPackage = metadata.getProjectPackage();
            final String value = metadataPackage == null ?
                    org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package.DEFAULT_PACKAGE :
                    metadata.getProjectPackage().getPackageName();
            diagramSet.getPackageProperty().setValue(value);
        }
        final String diagramName = diagramSet.getName().getValue();
        if (null == diagramName || diagramName.isEmpty()) {
            diagramSet.getName().setValue(name);
        }

        super.updateDiagramProperties(name, diagramNode, metadata);
    }
}
