/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache
        extends LRUCache<KieProject, ProjectDataModelOracle> {

    private ProjectDataModelOracleBuilderProvider builderProvider;
    private KieProjectService projectService;
    private LRUBuilderCache cache;

    public LRUProjectDataModelOracleCache() {
    }

    @Inject
    public LRUProjectDataModelOracleCache( final ProjectDataModelOracleBuilderProvider builderProvider,
                                           final KieProjectService projectService,
                                           final LRUBuilderCache cache ) {
        this.builderProvider = builderProvider;
        this.projectService = projectService;
        this.cache = cache;
    }

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final KieProject project = projectService.resolveProject( resourcePath );

        //If resource was not within a Project there's nothing to invalidate
        if ( project != null ) {
            invalidateCache( project );
        }
    }

    //Check the ProjectOracle for the Project has been created, otherwise create one!
    public synchronized ProjectDataModelOracle assertProjectDataModelOracle( final KieProject project ) {
        ProjectDataModelOracle projectOracle = getEntry( project );
        if ( projectOracle == null ) {
            projectOracle = makeProjectOracle( project );
            setEntry( project,
                      projectOracle );
        }
        return projectOracle;
    }

    private ProjectDataModelOracle makeProjectOracle( final KieProject project ) {
        return builderProvider.newBuilder( project,
                                           cache.assertBuilder( project ) ).build();
    }

}

