/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.services.backend.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Project, Builder> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private ProjectImportsService importsService;

    @Inject
    @Any
    private Instance<BuildValidationHelper> buildValidationHelperBeans;

    @Inject
    @Named("LRUProjectDependenciesClassLoaderCache")
    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;

    @Inject
    @Named("LRUPomModelCache")
    private LRUPomModelCache pomModelCache;

    @Inject
    private PackageNameWhiteListServiceImpl packageNameWhiteListService;

    private final List<BuildValidationHelper> buildValidationHelpers = new ArrayList<BuildValidationHelper>();

    @PostConstruct
    public void setupValidators() {
        final Iterator<BuildValidationHelper> itr = buildValidationHelperBeans.iterator();
        while ( itr.hasNext() ) {
            buildValidationHelpers.add( itr.next() );
        }
    }

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Project project = event.getProject();

        //If resource was not within a Project there's nothing to invalidate
        if ( project != null ) {
            invalidateCache( project );
        }
    }

    public synchronized Builder assertBuilder( POM pom )
            throws NoBuilderFoundException {
        for (Project project : getKeys()) {
            if ( project.getPom().getGav().equals( pom.getGav() ) ) {
                return makeBuilder( project );
            }
        }
        throw new NoBuilderFoundException();
    }

    public synchronized Builder assertBuilder( final Project project ) {
        return makeBuilder( project );
    }

    private Builder makeBuilder( Project project ) {
        Builder builder = getEntry( project );
        if ( builder == null ) {
            builder = new Builder( project,
                                   ioService,
                                   projectService,
                                   importsService,
                                   buildValidationHelpers,
                                   dependenciesClassLoaderCache,
                                   pomModelCache,
                                   packageNameWhiteListService );

            setEntry( project,
                      builder );
        }
        return builder;
    }
}
