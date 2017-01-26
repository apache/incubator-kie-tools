/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.project.factory;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.util.CaseManagementUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;
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
@ApplicationScoped
public class CaseManagementProjectDiagramFactory
        extends BindableDiagramFactory<ProjectMetadata, ProjectDiagram> {

    @Override
    public ProjectDiagram build(final String name,
                                final ProjectMetadata metadata,
                                final Graph<DefinitionSet, ?> graph) {
        updateDiagramProperties(name,
                                graph,
                                metadata);
        return new ProjectDiagramImpl(name,
                                      graph,
                                      metadata);
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    private void updateDiagramProperties(final String name,
                                         final Graph<DefinitionSet, ?> graph,
                                         final ProjectMetadata metadata) {
        final Node<Definition<CaseManagementDiagram>, ?> diagramNode = getFirstDiagramNode(graph);
        if (null == diagramNode) {
            throw new IllegalStateException("A Case Management Diagram is expected to be present on CaseManagementDiagram graphs.");
        }

        final CaseManagementDiagram diagram = diagramNode.getContent().getDefinition();
        final String id = diagram.getDiagramSet().getId().getValue();
        final String projectName = null != metadata.getProjectName() ? metadata.getProjectName() + "." : "";
        if (nil(id)) {
            diagram.getDiagramSet().getId().setValue(projectName + name);
        }

        final String p = diagram.getDiagramSet().getPackageProperty().getValue();
        if (nil(p)) {
            final String metadataPackage = metadata.getProjectPackage();
            if (nil(metadataPackage)) {
                diagram.getDiagramSet().getPackageProperty().setValue(Package.DEFAULT_PACKAGE);
            } else {
                diagram.getDiagramSet().getPackageProperty().setValue(metadata.getProjectPackage());
            }
        }
        final String diagramName = diagram.getDiagramSet().getName().getValue();
        if (nil(diagramName)) {
            diagram.getDiagramSet().getName().setValue(name);
        }
    }

    @SuppressWarnings("unchecked")
    private Node<Definition<CaseManagementDiagram>, ?> getFirstDiagramNode(final Graph graph) {
        return CaseManagementUtils.getFirstDiagramNode(graph);
    }

    private boolean nil(final String value) {
        return value == null || value.isEmpty();
    }
}