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
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.model.ProjectImports;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.workingset.client.model.WorkingSetSettings;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceAddedEvent;

@Service
@ApplicationScoped
public class ProjectServiceImpl
        implements ProjectService {

    private static final String SOURCE_FILENAME = "src";

    private static final String POM_PATH = "pom.xml";
    private static final String PROJECT_IMPORTS_PATH = "project.imports";
    private static final String KMODULE_PATH = "src/main/resources/META-INF/kmodule.xml";

    private static final String SOURCE_JAVA_PATH = "src/main/java";
    private static final String SOURCE_RESOURCES_PATH = "src/main/resources";

    private static final String TEST_JAVA_PATH = "src/test/java";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private IOService ioService;
    private Paths paths;

    private POMService pomService;
    private KModuleService kModuleService;
    private MetadataService metadataService;
    private ProjectConfigurationContentHandler projectConfigurationContentHandler;

    private Event<ResourceAddedEvent> resourceAddedEvent;

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
                               final Identity identity ) {
        this.ioService = ioService;
        this.paths = paths;
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.metadataService = metadataService;
        this.projectConfigurationContentHandler = projectConfigurationContentHandler;
        this.resourceAddedEvent = resourceAddedEvent;
        this.identity = identity;
    }

    @Override
    public WorkingSetSettings loadWorkingSetConfig( final Path project ) {
        //TODO {porcelli}
        return new WorkingSetSettings();
    }

    @Override
    public Path resolveProject( final Path resource ) {

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
            return paths.convert( path );
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
        return paths.convert( path );
    }

    @Override
    public Path resolvePathToPom( final Path resource ) {
        final Path projectPath = resolveProject( resource );
        if ( projectPath == null ) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path pom = paths.convert( projectPath ).resolve( POM_PATH );
        if ( pom == null ) {
            return null;
        }
        return paths.convert( pom );
    }

    @Override
    public Path resolvePathToProjectImports( Path resource ) {
        final Path projectPath = resolveProject( resource );
        if ( projectPath == null ) {
            return null;
        }
        final org.kie.commons.java.nio.file.Path imports = paths.convert( projectPath ).resolve( PROJECT_IMPORTS_PATH );
        if ( imports == null ) {
            return null;
        }
        return paths.convert( imports );
    }

    @Override
    public Path resolvePackage( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //If Path is not within a Project we cannot resolve a package
        final Path projectRoot = resolveProject( resource );
        if ( projectRoot == null ) {
            return null;
        }

        //Check whether path is a Src package or a Test package
        Path packagePath = doResolveSrcPackage( resource,
                                                projectRoot );
        if ( packagePath == null ) {
            packagePath = doResolveTestPackage( resource,
                                                projectRoot );
        }
        return packagePath;
    }

    @Override
    public Path resolveSrcPackage( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //If Path is not within a Project we cannot resolve a package
        final Path projectRoot = resolveProject( resource );
        if ( projectRoot == null ) {
            return null;
        }

        return doResolveSrcPackage( resource,
                                    projectRoot );
    }

    private Path doResolveSrcPackage( final Path resource,
                                      final Path projectRoot ) {
        //The pom.xml and kmodule.xml files are not within a package
        if ( isPom( resource ) || isKModule( resource ) ) {
            return null;
        }

        //The Path must be within a Project's src/main/java or src/main/resources path
        boolean resolved = false;
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path srcJavaPath = paths.convert( projectRoot ).resolve( SOURCE_JAVA_PATH );
        final org.kie.commons.java.nio.file.Path srcResourcesPath = paths.convert( projectRoot ).resolve( SOURCE_RESOURCES_PATH );

        //Check if path resides within a Java or Resources path
        if ( path.startsWith( srcJavaPath ) ) {
            resolved = true;
        } else if ( path.startsWith( srcResourcesPath ) ) {
            resolved = true;
        }
        if ( !resolved ) {
            return null;
        }

        //If the Path is already a folder simply return it
        if ( Files.isDirectory( path ) ) {
            return resource;
        }

        path = path.getParent();

        return paths.convert( path );
    }

    @Override
    public Path resolveTestPackage( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return null;
        }

        //If Path is not within a Project we cannot resolve a package
        final Path projectRoot = resolveProject( resource );
        if ( projectRoot == null ) {
            return null;
        }

        return doResolveTestPackage( resource,
                                     projectRoot );
    }

    private Path doResolveTestPackage( final Path resource,
                                       final Path projectRoot ) {
        //The pom.xml and kmodule.xml files are not within a package
        if ( isPom( resource ) || isKModule( resource ) ) {
            return null;
        }

        //The Path must be within a Project's src/test/java or src/test/resources path
        boolean resolved = false;
        org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path testJavaPath = paths.convert( projectRoot ).resolve( TEST_JAVA_PATH );
        final org.kie.commons.java.nio.file.Path testResourcesPath = paths.convert( projectRoot ).resolve( TEST_RESOURCES_PATH );

        //Check if path resides within a Java or Resources path
        if ( path.startsWith( testJavaPath ) ) {
            resolved = true;
        } else if ( path.startsWith( testResourcesPath ) ) {
            resolved = true;
        }
        if ( !resolved ) {
            return null;
        }

        //If the Path is already a folder simply return it
        if ( Files.isDirectory( path ) ) {
            return resource;
        }

        path = path.getParent();

        return paths.convert( path );
    }

    @Override
    public boolean isPom( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return false;
        }

        //Check if path equals pom.xml
        final Path projectRoot = resolveProject( resource );
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path pomFilePath = paths.convert( projectRoot ).resolve( POM_PATH );
        return path.startsWith( pomFilePath );
    }

    @Override
    public boolean isKModule( final Path resource ) {
        //Null resource paths cannot resolve to a Project
        if ( resource == null ) {
            return false;
        }

        //Check if path equals kmodule.xml
        final Path projectRoot = resolveProject( resource );
        final org.kie.commons.java.nio.file.Path path = paths.convert( resource ).normalize();
        final org.kie.commons.java.nio.file.Path kmoduleFilePath = paths.convert( projectRoot ).resolve( KMODULE_PATH );
        return path.startsWith( kmoduleFilePath );
    }

    @Override
    public String resolvePackageName( final Path path ) {

        //Check path is actually within a Package within a Project
        final Path packagePath = resolvePackage( path );
        if ( packagePath == null ) {
            return null;
        }
        final Path projectPath = resolveProject( packagePath );
        if ( projectPath == null ) {
            return null;
        }

        //Use the relative path between Project root and Package path to build the package name
        final org.kie.commons.java.nio.file.Path nioProjectPath = paths.convert( projectPath );
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        final org.kie.commons.java.nio.file.Path nioDelta = nioProjectPath.relativize( nioPackagePath );

        //Build package name
        String packageName = nioDelta.toString();
        if ( packageName.startsWith( SOURCE_JAVA_PATH ) ) {
            packageName = packageName.replace( SOURCE_JAVA_PATH,
                                               "" );
        } else if ( packageName.startsWith( SOURCE_RESOURCES_PATH ) ) {
            packageName = packageName.replace( SOURCE_RESOURCES_PATH,
                                               "" );
        } else if ( packageName.startsWith( TEST_JAVA_PATH ) ) {
            packageName = packageName.replace( TEST_JAVA_PATH,
                                               "" );
        } else if ( packageName.startsWith( TEST_RESOURCES_PATH ) ) {
            packageName = packageName.replace( TEST_RESOURCES_PATH,
                                               "" );
        }
        if ( packageName.startsWith( "/" ) ) {
            packageName = packageName.substring( 1 );
        }
        return packageName.replaceAll( "/",
                                       "." );
    }

    @Override
    public Path newProject( final Path activePath,
                            final String projectName,
                            final POM pom,
                            final String baseUrl ) {
        //Projects are always created in the FS root
        final Path fsRoot = getFileSystemRoot( activePath );
        final Path projectRootPath = getProjectRootPath( fsRoot,
                                                         projectName );

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

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( projectRootPath ) );

        return paths.convert( paths.convert( projectRootPath ).resolve( "pom.xml" ) );
    }

    private Path getFileSystemRoot( final Path activePath ) {
        return paths.convert( paths.convert( activePath ).getRoot(),
                              false );
    }

    private Path getProjectRootPath( final Path fsRoot,
                                     final String projectName ) {
        return paths.convert( paths.convert( fsRoot ).resolve( projectName ),
                              false );
    }

    @Override
    public Path newPackage( final Path contextPath,
                            final String packageName ) {
        return newDirectory( contextPath,
                             packageName );
    }

    @Override
    public Path newDirectory( final Path contextPath,
                              final String dirName ) {
        final Path directoryPath = paths.convert( ioService.createDirectory( paths.convert( contextPath ).resolve( dirName ) ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( directoryPath ) );

        return directoryPath;
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