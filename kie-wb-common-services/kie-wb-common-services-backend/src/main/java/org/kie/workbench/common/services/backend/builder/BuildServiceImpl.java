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

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl
        implements BuildService {

    private static final Logger logger = LoggerFactory.getLogger( BuildServiceImpl.class );

    private POMService pomService;
    private ExtendedM2RepoService m2RepoService;
    private KieProjectService projectService;
    private LRUBuilderCache cache;
    private Instance<PostBuildHandler> handlers;

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl( final POMService pomService,
                             final ExtendedM2RepoService m2RepoService,
                             final KieProjectService projectService,
                             final LRUBuilderCache cache,
                             final Instance<PostBuildHandler> handlers ) {
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.projectService = projectService;
        this.cache = cache;
        this.handlers = handlers;
    }

    @Override
    public BuildResults build( final Project project ) {
        try {
            final BuildResults results = doBuild( project );
            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(),
                          e );

            // BZ-1007894: If throwing the exception, an error popup will be displayed, but it's not the expected behavior. The excepted one is to show the errors in problems widget.
            // So, instead of throwing the exception, a BuildResults instance is produced on the fly to simulate the error in the problems widget.
            return buildExceptionResults( e );
        }
    }

    @Override
    public BuildResults buildAndDeploy( final Project project ) {
        return buildAndDeploy(project, false);
    }

    @Override
    public BuildResults buildAndDeploy( final Project project, boolean suppressHandlers ) {
        try {
            //Build
            final BuildResults results = doBuild( project );

            //Deploy, if no errors
            final POM pom = pomService.load( project.getPomXMLPath() );
            if ( results.getErrorMessages().isEmpty() ) {
                final Builder builder = cache.assertBuilder( project );
                final InternalKieModule kieModule = (InternalKieModule) builder.getKieModule();
                final ByteArrayInputStream input = new ByteArrayInputStream( kieModule.getBytes() );
                m2RepoService.deployJar( input,
                        pom.getGav() );

                if (!suppressHandlers) {
                    for ( PostBuildHandler handler : handlers ) {
                        try {
                            handler.process( results );
                        } catch ( Exception e ) {
                            logger.warn( "PostBuildHandler {} failed due to {}", handler, e.getMessage() );
                        }
                    }
                }
            }

            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );

            // BZ-1007894: If throwing the exception, an error popup will be displayed, but it's not the expected behavior. The excepted one is to show the errors in problems widget.
            // So, instead of throwing the exception, a BuildResults instance is produced on the fly to simulate the error in the problems widget.
            return buildExceptionResults( e );
        }
    }

    /**
     * When an exception is produced by the builder service, this method is uses to generate an instance of
     * <code>org.guvnor.common.services.project.builder.model.BuildResults</code> in generated with the exception details.
     * @param e The error exception.
     * @return An instance of BuildResults with the exception details.
     */
    private BuildResults buildExceptionResults( Exception e ) {
        BuildResults exceptionResults = new BuildResults();
        BuildMessage exceptionMessage = new BuildMessage();
        exceptionMessage.setLevel( BuildMessage.Level.ERROR );
        exceptionMessage.setText( e.getMessage() );
        exceptionResults.addBuildMessage( exceptionMessage );

        return exceptionResults;
    }

    private BuildResults doBuild( final Project project ) {
        cache.invalidateCache( project );
        final Builder builder = cache.assertBuilder( project );
        final BuildResults results = builder.build();
        return results;
    }

    @Override
    public boolean isBuilt( final Project project ) {
        final Builder builder = cache.assertBuilder( project );
        return builder.isBuilt();
    }

    @Override
    public IncrementalBuildResults addPackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final KieProject project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.addResource( Paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(),
                          e );
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults deletePackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final KieProject project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.deleteResource( Paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(),
                          e );
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults updatePackageResource( final Path resource ) {
        try {
            IncrementalBuildResults results = new IncrementalBuildResults();
            final KieProject project = projectService.resolveProject( resource );
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.updateResource( Paths.convert( resource ) );
            }

            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(),
                          e );
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges( final Project project,
                                                              final Map<Path, Collection<ResourceChange>> changes ) {
        IncrementalBuildResults results = new IncrementalBuildResults();
        try {
            if ( project == null ) {
                return results;
            }
            final Builder builder = cache.assertBuilder( project );
            if ( !builder.isBuilt() ) {
                throw new IllegalStateException( "Incremental Build requires a full build be completed first." );
            } else {
                results = builder.applyBatchResourceChanges( changes );
            }

            return results;

        } catch ( Exception e ) {
            logger.error( e.getMessage(),
                          e );
            throw ExceptionUtilities.handleException( e );
        }
    }

}
