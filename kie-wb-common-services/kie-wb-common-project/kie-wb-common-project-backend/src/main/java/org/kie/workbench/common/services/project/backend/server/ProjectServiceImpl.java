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

package org.kie.workbench.common.services.project.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.model.ProjectImports;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageAddedEvent;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectAddedEvent;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.workingset.client.model.WorkingSetSettings;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ChangeType;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    private static final String SOURCE_FILENAME = "src";

    private static final String POM_PATH = "pom.xml";
    private static final String PROJECT_IMPORTS_PATH = "project.imports";
    private static final String KMODULE_PATH = "src/main/resources/META-INF/kmodule.xml";

    private static final String MAIN_SRC_PATH = "src/main/java";
    private static final String TEST_SRC_PATH = "src/test/java";
    private static final String MAIN_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private IOService ioService;
    private Paths paths;

    private POMService pomService;
    private KModuleService kModuleService;
    private MetadataService metadataService;
    private ProjectConfigurationContentHandler projectConfigurationContentHandler;

    private Event<ResourceAddedEvent> resourceAddedEvent;
    private Event<ResourceBatchChangesEvent> resourceBatchChangesEvent;
    private Event<ProjectAddedEvent> projectAddedEvent;
    private Event<PackageAddedEvent> packageAddedEvent;

    private Identity identity;

    public ProjectServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public ProjectServiceImpl( final @Named("ioStrategy") IOService ioService,
                               final Paths paths,
                               final POMService pomService,
                               final KModuleService kModuleService,
                               final MetadataService metadataService,
                               final ProjectConfigurationContentHandler projectConfigurationContentHandler,
                               final Event<ResourceAddedEvent> resourceAddedEvent,
                               final Event<ResourceBatchChangesEvent> resourceBatchChangesEvent,
                               final Event<ProjectAddedEvent> projectAddedEvent,
                               final Event<PackageAddedEvent> packageAddedEvent,
                               final Identity identity ) {
        this.ioService = ioService;
        this.paths = paths;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.metadataService = metadataService;
        this.projectConfigurationContentHandler = projectConfigurationContentHandler;
        this.resourceAddedEvent = resourceAddedEvent;
        this.resourceBatchChangesEvent = resourceBatchChangesEvent;
        this.projectAddedEvent = projectAddedEvent;
        this.packageAddedEvent = packageAddedEvent;
        this.identity = identity;
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Project resolveProject( final Path resource ) {

        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //Check if resource is the project root
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();

        //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
        if ( Files.isRegularFile( path ) ) {
            path = path.getParent();
        }
        if ( hasPom( path ) && hasKModule( path ) ) {
            return makeProject( path );
        }
        while ( path.getNameCount() > 0 && !path.getFileName().toString().equals( SOURCE_FILENAME ) ) {
            path = path.getParent();
        }
        if ( path.getNameCount() == 0 ) {
            return null;
        }
        path = path.getParent();
        if ( path.getNameCount() == 0 || path == null ) {
            return null;
        }
        if ( !hasPom( path ) ) {
            return null;
        }
        if ( !hasKModule( path ) ) {
            return null;
        }
        return makeProject( path );
    }

    private Project makeProject( final org.kie.commons.java.nio.file.Path nioProjectRootPath ) {
        final Path projectRootPath = paths.convert( nioProjectRootPath );
        final Path pomXMLPath = paths.convert( nioProjectRootPath.resolve( POM_PATH ),
                                               false );
        final Path kmoduleXMLPath = paths.convert( nioProjectRootPath.resolve( KMODULE_PATH ),
                                                   false );
        final Path importsXMLPath = paths.convert( nioProjectRootPath.resolve( PROJECT_IMPORTS_PATH ),
                                                   false );
        return new Project( projectRootPath,
                            pomXMLPath,
                            kmoduleXMLPath,
                            importsXMLPath,
                            projectRootPath.getFileName() );
    }

    @Override
    public Package resolvePackage( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //If Path is not within a Project we cannot resolve a package
        final Project project = resolveProject( resource );
        if ( project == null ) {
            return null;
        }

        //pom.xml and kmodule.xml are not inside packages
        if ( isPom( resource ) || isKModule( resource ) ) {
            return null;
        }

        return makePackage( project,
                            resource );
    }

    private Package makePackage( final Project project,
                                 final Path resource ) {
        final Path projectRoot = project.getRootPath();
        final org.kie.commons.java.nio.file.Path nioProjectRoot = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path nioMainSrcPath = nioProjectRoot.resolve( MAIN_SRC_PATH );
        final org.kie.commons.java.nio.file.Path nioTestSrcPath = nioProjectRoot.resolve( TEST_SRC_PATH );
        final org.kie.commons.java.nio.file.Path nioMainResourcesPath = nioProjectRoot.resolve( MAIN_RESOURCES_PATH );
        final org.kie.commons.java.nio.file.Path nioTestResourcesPath = nioProjectRoot.resolve( TEST_RESOURCES_PATH );

        org.kie.commons.java.nio.file.Path nioResource = paths.convert( resource );

        if ( Files.isRegularFile( nioResource ) ) {
            nioResource = nioResource.getParent();
        }

        String packageName = null;
        org.kie.commons.java.nio.file.Path packagePath = null;
        if ( nioResource.startsWith( nioMainSrcPath ) ) {
            packagePath = nioMainSrcPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioTestSrcPath ) ) {
            packagePath = nioTestSrcPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioMainResourcesPath ) ) {
            packagePath = nioMainResourcesPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        } else if ( nioResource.startsWith( nioTestResourcesPath ) ) {
            packagePath = nioTestResourcesPath.relativize( nioResource );
            packageName = packagePath.toString().replaceAll( "/",
                                                             "." );
        }

        //Resource was not inside a package
        if ( packageName == null ) {
            return null;
        }

        boolean includeAttributes = Files.exists( nioMainSrcPath.resolve( packagePath ) );
        final Path mainSrcPath = paths.convert( nioMainSrcPath.resolve( packagePath ),
                                                includeAttributes );
        includeAttributes = Files.exists( nioTestSrcPath.resolve( packagePath ) );
        final Path testSrcPath = paths.convert( nioTestSrcPath.resolve( packagePath ),
                                                includeAttributes );
        includeAttributes = Files.exists( nioMainResourcesPath.resolve( packagePath ) );
        final Path mainResourcesPath = paths.convert( nioMainResourcesPath.resolve( packagePath ),
                                                      includeAttributes );
        includeAttributes = Files.exists( nioTestResourcesPath.resolve( packagePath ) );
        final Path testResourcesPath = paths.convert( nioTestResourcesPath.resolve( packagePath ),
                                                      includeAttributes );

        final Package pkg = new Package( project.getRootPath(),
                                         mainSrcPath,
                                         testSrcPath,
                                         mainResourcesPath,
                                         testResourcesPath,
                                         packageName,
                                         getPackageDisplayName( packageName ) );
        return pkg;
    }

    private String getPackageDisplayName( final String packageName ) {
        return packageName.isEmpty() ? "<default>" : packageName;
    }

    @Override
    public boolean isPom( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return false;
        }

        //Check if path equals pom.xml
        final Project project = resolveProject( resource );
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path pomFilePath = paths.convert( project.getPomXMLPath() );
        return path.startsWith( pomFilePath );
    }

    @Override
    public boolean isKModule( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return false;
        }

        //Check if path equals kmodule.xml
        final Project project = resolveProject( resource );
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path kmoduleFilePath = paths.convert( project.getKModuleXMLPath() );
        return path.startsWith( kmoduleFilePath );
    }

    @Override
    public Project newProject( final Repository repository,
                               final String projectName,
                               final POM pom,
                               final String baseUrl ) {
        //Projects are always created in the FS root
        final Path fsRoot = repository.getRoot();
        final Path projectRootPath = paths.convert( paths.convert( fsRoot ).resolve( projectName ),
                                                    false );

        //Set-up project structure and KModule.xml
        kModuleService.setUpKModuleStructure( projectRootPath );

        //Create POM.xml
        pomService.create( projectRootPath,
                           baseUrl,
                           pom );

        //Create Project configuration
        final Path projectConfigPath = paths.convert( paths.convert( projectRootPath ).resolve( "project.imports" ),
                                                      false );
        ioService.createFile( paths.convert( projectConfigPath ) );
        ioService.write( paths.convert( projectConfigPath ),
                         projectConfigurationContentHandler.toString( new ProjectImports() ) );

        //Raise an event for the new project
        final Project project = resolveProject( projectRootPath );
        projectAddedEvent.fire( new ProjectAddedEvent( project ) );

        //Raise an event for the other resources (UberFire components cannot handle PackageAddedEvents)
        resourceAddedEvent.fire( new ResourceAddedEvent( projectRootPath ) );

        return project;
    }

    @Override
    public Package newPackage( final Package parentPackage,
                               final String packageName ) {
        final Path mainSrcPath = parentPackage.getPackageMainSrcPath();
        final Path testSrcPath = parentPackage.getPackageTestSrcPath();
        final Path mainResourcesPath = parentPackage.getPackageMainResourcesPath();
        final Path testResourcesPath = parentPackage.getPackageTestResourcesPath();

        Path pkgPath = null;

        final ResourceBatchChangesEvent batchChangesEvent = new ResourceBatchChangesEvent();

        final org.kie.commons.java.nio.file.Path nioMainSrcPackagePath = paths.convert( mainSrcPath ).resolve( packageName );
        if ( !Files.exists( nioMainSrcPackagePath ) ) {
            pkgPath = paths.convert( ioService.createDirectory( nioMainSrcPackagePath ) );
            batchChangesEvent.getBatch().add( new ResourceChange( ChangeType.ADD,
                                                                  pkgPath ) );
        }
        final org.kie.commons.java.nio.file.Path nioTestSrcPackagePath = paths.convert( testSrcPath ).resolve( packageName );
        if ( !Files.exists( nioTestSrcPackagePath ) ) {
            pkgPath = paths.convert( ioService.createDirectory( nioTestSrcPackagePath ) );
            batchChangesEvent.getBatch().add( new ResourceChange( ChangeType.ADD,
                                                                  pkgPath ) );
        }
        final org.kie.commons.java.nio.file.Path nioMainResourcesPackagePath = paths.convert( mainResourcesPath ).resolve( packageName );
        if ( !Files.exists( nioMainResourcesPackagePath ) ) {
            pkgPath = paths.convert( ioService.createDirectory( nioMainResourcesPackagePath ) );
            batchChangesEvent.getBatch().add( new ResourceChange( ChangeType.ADD,
                                                                  pkgPath ) );
        }
        final org.kie.commons.java.nio.file.Path nioTestResourcesPackagePath = paths.convert( testResourcesPath ).resolve( packageName );
        if ( !Files.exists( nioTestResourcesPackagePath ) ) {
            pkgPath = paths.convert( ioService.createDirectory( nioTestResourcesPackagePath ) );
            batchChangesEvent.getBatch().add( new ResourceChange( ChangeType.ADD,
                                                                  pkgPath ) );
        }

        //pkgPath should not be null at this stage or something has gone wrong!
        PortablePreconditions.checkNotNull( "pkgPath",
                                            pkgPath );

        //Raise an event for the new package
        final Package pkg = resolvePackage( pkgPath );
        packageAddedEvent.fire( new PackageAddedEvent( pkg ) );

        //Raise an event for the other resources (UberFire components cannot handle PackageAddedEvents)
        resourceBatchChangesEvent.fire( batchChangesEvent );

        return pkg;
    }

    private boolean hasPom( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path pomPath = path.resolve( POM_PATH );
        return Files.exists( pomPath );
    }

    private boolean hasKModule( final org.kie.commons.java.nio.file.Path path ) {
        final org.kie.commons.java.nio.file.Path kmodulePath = path.resolve( KMODULE_PATH );
        return Files.exists( kmodulePath );
    }

    @Override
    public ProjectImports load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );
        return projectConfigurationContentHandler.toModel( content );
    }

    @Override
    public Path save( final Path resource,
                      final ProjectImports projectImports,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         projectConfigurationContentHandler.toString( projectImports ),
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //The pom.xml, kmodule.xml and project.imports are all saved from ProjectScreenPresenter
        //We only raise InvalidateDMOProjectCacheEvent and ResourceUpdatedEvent(pom.xml) events once
        //in POMService.save to avoid duplicating events (and re-construction of DMO).

        return resource;
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}