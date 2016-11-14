/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.project.factory.impl;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.util.BPMNUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;

import javax.enterprise.context.ApplicationScoped;

/**
 * Custom BPMN factory instance for Diagrams on the Project context.
 * This factory initializes new BPMN diagrams with some specific project attributes ( given from the current
 * Project context ).
 * No need use use the Command API to set the diagram property values for the following reasons:
 * - No need to check runtime rules.
 * - No need to perform further undo/redos for these operations.
 */
@ApplicationScoped
public class BPMNProjectDiagramFactory
        extends BindableDiagramFactory<ProjectMetadata, ProjectDiagram> {


    @Override
    public ProjectDiagram build( final String name,
                                 final ProjectMetadata metadata,
                                 final Graph<DefinitionSet, ?> graph ) {
        updateDiagramProperties( name, graph, metadata );
        return new ProjectDiagramImpl( name, graph, metadata );
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    private void updateDiagramProperties( final String name,
                                          final Graph<DefinitionSet, ?> graph,
                                          final ProjectMetadata metadata) {
        final Node<Definition<BPMNDiagram>, ?> diagramNode = getFirstDiagramNode( graph );
        if ( null == diagramNode ) {
            throw new IllegalStateException( "A BPMN Diagram is expected to be present on BPMN Diagram graphs." );
        }
        final BPMNDiagram diagram = diagramNode.getContent().getDefinition();
        final String id = diagram.getDiagramSet().getId().getValue();
        final String projectName = null != metadata.getProjectName() ? metadata.getProjectName() + "." : "";
        if ( null == id || diagram.getDiagramSet().getId().getDefaultValue().equals( id ) ) {
            diagram.getDiagramSet().getId().setValue( projectName + name );
        }
        final String p = diagram.getDiagramSet().getPackageProperty().getValue();
        if (  null == p ) {
            diagram.getDiagramSet().getPackageProperty().setValue( metadata.getProjectPackage() );
        }
        final String diagramName = diagram.getGeneral().getName().getValue();
        if ( null == diagramName ) {
            diagram.getGeneral().getName().setValue( name );
        }
    }

    @SuppressWarnings( "unchecked" )
    private static Node<Definition<BPMNDiagram>, ?> getFirstDiagramNode( final Graph graph ) {
        return BPMNUtils.getFirstDiagramNode( graph );
    }

}
