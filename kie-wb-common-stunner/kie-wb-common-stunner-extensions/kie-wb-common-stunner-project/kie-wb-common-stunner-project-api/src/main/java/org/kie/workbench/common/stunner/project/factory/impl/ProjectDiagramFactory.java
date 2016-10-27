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
import org.kie.workbench.common.stunner.core.factory.impl.DiagramFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@ApplicationScoped
@Specializes
public class ProjectDiagramFactory
        extends DiagramFactoryImpl
        implements DiagramFactory {

    @Override
    public ProjectDiagram build( String name, Metadata metadata, Graph graph ) {
        // TODO: Handle project name.
        final ProjectMetadata projectMetadata =
                new ProjectMetadataImpl.ProjectMetadataBuilder()
                        .fromMetadata( metadata )
                        .forProjectName( "projectName" )
                        .build();
        return new ProjectDiagramImpl( name, graph, projectMetadata );
    }
}
