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

package org.kie.workbench.common.stunner.project.diagram.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.diagram.AbstractMetadata;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;

@Portable
public class ProjectMetadataImpl extends AbstractMetadata implements ProjectMetadata {

    private String projectName;

    public ProjectMetadataImpl() {
    }

    private ProjectMetadataImpl( @MapsTo( "definitionSetId" ) String definitionSetId,
                                 @MapsTo( "projectName" ) String projectName ) {
        super( definitionSetId );
        this.projectName = projectName;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @NonPortable
    public static class ProjectMetadataBuilder {

        private Metadata metadata;
        private String pName;

        public ProjectMetadataBuilder fromMetadata( Metadata metadata ) {
            this.metadata = metadata;
            return this;
        }

        public ProjectMetadataBuilder forProjectName( String pName ) {
            this.pName = pName;
            return this;
        }

        public ProjectMetadataImpl build() {
            final ProjectMetadataImpl result = new ProjectMetadataImpl( metadata.getDefinitionSetId(), pName );
            result.setShapeSetId( metadata.getShapeSetId() );
            result.setPath( metadata.getPath() );
            result.setCanvasRootUUID( metadata.getCanvasRootUUID() );
            result.setTitle( metadata.getTitle() );
            result.setThumbData( metadata.getThumbData() );
            return result;
        }

    }
}
