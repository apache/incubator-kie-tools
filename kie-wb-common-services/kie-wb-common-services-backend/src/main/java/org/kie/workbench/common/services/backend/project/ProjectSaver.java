/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.ProjectResourcePaths;
import org.guvnor.common.services.project.backend.server.utils.IdentifierUtils;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.guvnor.common.services.project.backend.server.ProjectResourcePaths.*;

public class ProjectSaver {

    private ProjectImportsService       projectImportsService;
    private Event<NewPackageEvent>             newPackageEvent;
    private IOService                          ioService;
    private POMService                         pomService;
    private KModuleService                     kModuleService;
    private Event<NewProjectEvent>             newProjectEvent;
    private KieResourceResolver                resourceResolver;
    private PackageNameWhiteListService packageNameWhiteListService;
    private CommentedOptionFactory             commentedOptionFactory;
    private SafeSessionInfo                    safeSessionInfo;

    public ProjectSaver() {
    }

    @Inject
    public ProjectSaver( final @Named( "ioStrategy" ) IOService ioService,
                         final SessionInfo sessionInfo,
                         final POMService pomService,
                         final KModuleService kModuleService,
                         final Event<NewProjectEvent> newProjectEvent,
                         final Event<NewPackageEvent> newPackageEvent,
                         final KieResourceResolver resourceResolver,
                         final ProjectImportsService projectImportsService,
                         final PackageNameWhiteListService packageNameWhiteListService,
                         final CommentedOptionFactory commentedOptionFactory ) {

        this.ioService = ioService;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.newProjectEvent = newProjectEvent;
        this.newPackageEvent = newPackageEvent;
        this.resourceResolver = resourceResolver;
        this.projectImportsService = projectImportsService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.commentedOptionFactory = commentedOptionFactory;
        safeSessionInfo = new SafeSessionInfo( sessionInfo );
    }

    public KieProject save( final Repository repository,
                            final POM pom,
                            final String baseUrl ) {
        try {
            ioService.startBatch( Paths.convert( repository.getRoot() ).getFileSystem(),
                                  commentedOptionFactory.makeCommentedOption( "New project [" + pom.getName() + "]" ) );

            KieProject kieProject = new NewProjectCreator( repository,
                                                           pom ).create( baseUrl );

            newProjectEvent.fire( new NewProjectEvent( kieProject,
                                                       safeSessionInfo.getId(),
                                                       safeSessionInfo.getIdentity().getIdentifier() ) );
            return kieProject;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
        }
    }


    private class NewProjectCreator {

        private Path       projectRootPath;
        private Repository repository;
        private POM        pom;
        private KieProject simpleProjectInstance;
        private final org.uberfire.java.nio.file.Path projectNioRootPath;

        public NewProjectCreator( final Repository repository,
                                  final POM pom ) {
            this.repository = repository;
            this.pom = pom;
            projectNioRootPath = Paths.convert( repository.getRoot() ).resolve( pom.getName() );
            projectRootPath = Paths.convert( projectNioRootPath );

            simpleProjectInstance = resourceResolver.simpleProjectInstance( Paths.convert( projectRootPath ) );

        }

        public KieProject create( final String baseUrl ) {

            createKieProject( baseUrl );

            return resourceResolver.resolveProject( projectRootPath );
        }

        private Path createKieProject( final String baseUrl ) {

            // Update parent pom.xml
            updateParentPOM();

            //Create POM.xml
            pomService.create( projectRootPath,
                               baseUrl,
                               pom );


            createMavenDirectories();

            kModuleService.setUpKModule( simpleProjectInstance.getKModuleXMLPath() );

            //Create Project configuration - project imports
            projectImportsService.saveProjectImports( simpleProjectInstance.getImportsPath() );

            //Create a default workspace based on the GAV
            createDefaultPackage();

            packageNameWhiteListService.createProjectWhiteList( simpleProjectInstance.getPackageNamesWhiteListPath() );

            return projectRootPath;
        }

        private void updateParentPOM() {
            Path parentPom = Paths.convert( Paths.convert( repository.getRoot() ).resolve( "pom.xml" ) );
            if ( ioService.exists( Paths.convert( parentPom ) ) ) {
                POM parent = pomService.load( parentPom );
                parent.setPackaging( "pom" );
                parent.getModules().add( pom.getName() );

                pom.setParent( parent.getGav() );

                pomService.save( parentPom,
                                 parent,
                                 null,
                                 "Adding child module " + pom.getName() );
            }
        }

        private void createMavenDirectories() {
            ioService.createDirectory( projectNioRootPath.resolve( ProjectResourcePaths.MAIN_SRC_PATH ) );
            ioService.createDirectory( projectNioRootPath.resolve( ProjectResourcePaths.MAIN_RESOURCES_PATH ) );
            ioService.createDirectory( projectNioRootPath.resolve( ProjectResourcePaths.TEST_SRC_PATH ) );
            ioService.createDirectory( projectNioRootPath.resolve( ProjectResourcePaths.TEST_RESOURCES_PATH ) );
        }

        private void createDefaultPackage() {
            //Raise an event for the new project's default workspace
            newPackageEvent.fire( new NewPackageEvent( resourceResolver.newPackage( getDefaultPackage(),
                                                                                    getDefaultWorkspacePath(),
                                                                                    false ) ) );
        }

        private Package getDefaultPackage() {
            return resourceResolver.resolvePackage( Paths.convert( Paths.convert( projectRootPath ).resolve( MAIN_RESOURCES_PATH ) ) );
        }

        private String getDefaultWorkspacePath() {
            return StringUtils.join( getLegalId( pom.getGav().getGroupId() ),
                                     "/" ) + "/" + StringUtils.join( getLegalId( pom.getGav().getArtifactId() ),
                                                                     "/" );
        }

        private String[] getLegalId( final String id ) {
            return IdentifierUtils.convertMavenIdentifierToJavaIdentifier( id.split( "\\.",
                                                                                     -1 ) );
        }

    }
}
