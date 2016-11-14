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

package org.kie.workbench.common.stunner.project.factory.impl;

import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectDiagramFactory
        implements DiagramFactory<ProjectMetadata, ProjectDiagram> {

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    public ProjectDiagram build( final String name,
                                 final ProjectMetadata metadata,
                                 final Graph<DefinitionSet, ?> graph ) {
        return new ProjectDiagramImpl( name, graph, metadata );
    }

    /**
     * The default factory for ProjectDiagrams.
     * @param source the Definition Set identifier.
     * @return If does not accepts any concrete Definition Set, so no matter the identifier,
     * this instance is the default factory for all definition sets on the Project context (ProjectMetadata).
     */
    @Override
    public boolean accepts( final String source ) {
        return false;
    }

}
