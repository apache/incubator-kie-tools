/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.service;

import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public abstract class KieService {

    @Inject
    protected MetadataService metadataService;

    @Inject
    protected SourceServices sourceServices;

    @Inject
    protected KieProjectService projectService;

    public Overview loadOverview( final Path path ) {

        Overview overview = new Overview();

        overview.setMetadata( metadataService.getMetadata( path ) );
        overview.setPreview( getSource( path ) );

        //Some resources are not within a Project (e.g. categories.xml) so don't assume we can set the project name
        final KieProject project = projectService.resolveProject( path );
        if ( project != null ) {
            overview.setProjectName( project.getProjectName() );
        }

        return overview;
    }

    public String getSource( final Path path ) {
        org.uberfire.java.nio.file.Path convertedPath = Paths.convert( path );

        if ( sourceServices.hasServiceFor( convertedPath ) ) {
            return sourceServices.getServiceFor( convertedPath ).getSource( convertedPath );
        } else {
            return "";
        }
    }

}
