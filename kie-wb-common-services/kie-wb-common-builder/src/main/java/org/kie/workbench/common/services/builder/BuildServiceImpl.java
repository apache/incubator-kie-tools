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

package org.kie.workbench.common.services.builder;

import java.io.ByteArrayInputStream;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.kie.workbench.common.services.shared.builder.model.BuildResults;
import org.kie.workbench.common.services.shared.builder.model.DeployResult;
import org.kie.workbench.common.services.shared.builder.model.IncrementalBuildResults;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private Paths paths;
    private Event<BuildResults> buildResultsEvent;
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;
    private POMService pomService;
    private ExtendedM2RepoService m2RepoService;
    private ProjectService projectService;
    private LRUBuilderCache cache;
    private Event<DeployResult> deployResultEvent;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final Paths paths,
                             final POMService pomService,
                             final ExtendedM2RepoService m2RepoService,
                             final Event<BuildResults> buildResultsEvent,
                             final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                             final ProjectService projectService,
                             final LRUBuilderCache cache,
                             final Event<DeployResult> deployResultEvent ) {
        this.paths = paths;
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.projectService = projectService;
        this.cache = cache;
        this.deployResultEvent = deployResultEvent;
    }

    @Override
    public void build( final Project project ) {
        try {
            final BuildResults results = doBuild( project );
            buildResultsEvent.fire( results );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }

    }

    @Override
    public void buildAndDeploy( final Project project ) {
        try {
            //Build
            final BuildResults results = doBuild( project );
            buildResultsEvent.fire( results );

            //Deploy, if no errors
            if ( results.getMessages().isEmpty() ) {
                final Builder builder = cache.assertBuilder( project );
                final POM pom = pomService.load( project.getPomXMLPath() );
                final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
                final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );
                m2RepoService.deployJar( input,
                                         pom.getGav() );
                deployResultEvent.fire(
                        new DeployResult( pom.getGav().getGroupId(), pom.getGav().getArtifactId(), pom.getGav().getVersion() ) );
            }

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }

    }

    private BuildResults doBuild( final Project project ) {
        final Builder builder = cache.assertBuilder( project );
        final BuildResults results = builder.build();
        return results;
    }

    @Override
    public void addPackageResource( final Path resource ) {
        try {
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                build( project );
            }
            final IncrementalBuildResults results = builder.addResource( paths.convert( resource ) );
            incrementalBuildResultsEvent.fire( results );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void deletePackageResource( final Path resource ) {
        try {
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                build( project );
            }
            final IncrementalBuildResults results = builder.deleteResource( paths.convert( resource ) );
            incrementalBuildResultsEvent.fire( results );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void updatePackageResource( final Path resource ) {
        try {
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                build( project );
            }
            final IncrementalBuildResults results = builder.updateResource( paths.convert( resource ) );
            incrementalBuildResultsEvent.fire( results );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void updateProjectResource( final Path resource ) {
        try {
            final Project project = projectService.resolveProject( resource );
            if ( project == null ) {
                return;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                build( project );
            }

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void applyBatchResourceChanges( final Project project,
                                           final Set<ResourceChange> changes ) {
        try {
            if ( project == null ) {
                return;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                build( project );
            }
            final IncrementalBuildResults results = builder.applyBatchResourceChanges( changes );
            incrementalBuildResultsEvent.fire( results );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }
}
