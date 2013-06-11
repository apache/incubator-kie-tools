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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.Package;
import org.kie.workbench.common.screens.explorer.model.Project;
import org.kie.workbench.common.screens.explorer.model.ProjectPackage;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.backend.file.LinkedDotFileFilter;
import org.kie.workbench.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;

@Service
@ApplicationScoped
public class ExplorerServiceImpl
        implements ExplorerService {

    private static String[] sourcePaths = { "src/main/java", "src/main/resources", "src/test/java", "src/test/resources" };

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();
    private LinkedMetaInfFolderFilter metaDataFileFilter = new LinkedMetaInfFolderFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private GroupService groupService;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    @SessionScoped
    private Identity identity;

    @Inject
    private Paths paths;

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final AuthorizationManager authorizationManager,
                                final ProjectService projectService,
                                final GroupService groupService,
                                final Identity identity,
                                final Paths paths ) {
        this.ioService = ioService;
        this.authorizationManager = authorizationManager;
        this.projectService = projectService;
        this.groupService = groupService;
        this.identity = identity;
        this.paths = paths;
    }

    @Override
    public Collection<Group> getGroups() {
        final Collection<Group> groups = groupService.getGroups();
        final Collection<Group> authorizedGroups = new ArrayList<Group>();
        for ( Group group : groups ) {
            if ( authorizationManager.authorize( group,
                                                 identity ) ) {
                authorizedGroups.add( group );
            }
        }
        return authorizedGroups;
    }

    @Override
    public Collection<Repository> getRepositories( final Group group ) {
        final Collection<Repository> authorizedRepositories = new HashSet<Repository>();
        for ( Repository repository : group.getRepositories() ) {
            if ( authorizationManager.authorize( repository,
                                                 identity ) ) {
                authorizedRepositories.add( repository );
            }
        }
        return authorizedRepositories;
    }

    @Override
    public Collection<Project> getProjects( final Repository repository ) {
        final Collection<Project> authorizedProjects = new HashSet<Project>();
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( paths.convert( repositoryRoot ) );
        for ( org.kie.commons.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = paths.convert( nioRepositoryPath );
                if ( projectService.resolveProject( projectPath ) != null ) {
                    final Project project = new Project( projectPath,
                                                         projectPath.getFileName() );
                    authorizedProjects.add( project );
                }
            }
        }
        return authorizedProjects;
    }

    @Override
    public Collection<Package> getPackages( final Project project ) {
        final Collection<Package> packages = new HashSet<Package>();
        final Path projectRoot = project.getPath();
        for ( String src : sourcePaths ) {
            final org.kie.commons.java.nio.file.Path nioProjectRootPath = paths.convert( projectRoot );
            final org.kie.commons.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src );
            packages.addAll( getPackages( nioProjectRootPath,
                                          nioPackageRootSrcPath ) );
        }
        return packages;
    }

    private Collection<Package> getPackages( final org.kie.commons.java.nio.file.Path nioProjectRootPath,
                                             final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final Collection<Package> packages = new HashSet<Package>();
        if ( !Files.exists( nioPackageSrcPath ) ) {
            return packages;
        }
        packages.add( makePackage( nioProjectRootPath,
                                   nioPackageSrcPath ) );
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioChildPackageSrcPaths = ioService.newDirectoryStream( nioPackageSrcPath,
                                                                                                                          metaDataFileFilter );
        for ( org.kie.commons.java.nio.file.Path nioChildPackageSrcPath : nioChildPackageSrcPaths ) {
            if ( Files.isDirectory( nioChildPackageSrcPath ) ) {
                packages.addAll( getPackages( nioProjectRootPath,
                                              nioChildPackageSrcPath ) );
                packages.add( makePackage( nioProjectRootPath,
                                           nioChildPackageSrcPath ) );
            }
        }
        return packages;
    }

    private Package makePackage( final org.kie.commons.java.nio.file.Path nioProjectRootPath,
                                 final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final org.uberfire.backend.vfs.Path projectRootPath = paths.convert( nioProjectRootPath,
                                                                             false );
        final org.uberfire.backend.vfs.Path packageSrcPath = paths.convert( nioPackageSrcPath );
        final String packageName = getPackageName( packageSrcPath );
        final Package pkg = new Package( projectRootPath,
                                         packageName,
                                         getPackageDisplayName( packageName ) );
        return pkg;
    }

    private String getPackageName( final Path packageSrcPath ) {
        final String packageName = projectService.resolvePackageName( packageSrcPath );
        return packageName;
    }

    private String getPackageDisplayName( final String packageName ) {
        return packageName.isEmpty() ? "<default>" : packageName;
    }

    @Override
    public Collection<Item> getItems( final Package pkg ) {
        final Collection<Item> items = new HashSet<Item>();
        final Path projectRootPath = pkg.getProjectRootPath();
        final String packageName = pkg.getPackageName();
        final org.kie.commons.java.nio.file.Path nioProjectRootPath = paths.convert( projectRootPath );
        for ( String src : sourcePaths ) {
            final org.kie.commons.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src );
            final org.kie.commons.java.nio.file.Path nioPackageSrcPath = nioPackageRootSrcPath.resolve( getPackagePath( packageName ) );
            if ( Files.exists( nioPackageSrcPath ) ) {
                final DirectoryStream<org.kie.commons.java.nio.file.Path> nioSrcPaths = ioService.newDirectoryStream( nioPackageSrcPath,
                                                                                                                      dotFileFilter );
                for ( org.kie.commons.java.nio.file.Path nioPath : nioSrcPaths ) {
                    if ( Files.isRegularFile( nioPath ) ) {
                        final org.uberfire.backend.vfs.Path path = paths.convert( nioPath );
                        final String fileName = path.getFileName();
                        final Item item = new Item( path,
                                                    fileName );
                        items.add( item );
                    }
                }
            }
        }
        return items;
    }

    private String getPackagePath( final String packageName ) {
        return packageName.replaceAll( "\\.",
                                       "/" );
    }

    @Override
    public ProjectPackage resolveProjectPackage( final Path path ) {
        if ( path == null ) {
            return new ProjectPackage();
        }
        Project project = null;
        Package pkg = null;
        if ( projectService.resolveProject( path ) != null ) {
            project = new Project( path,
                                   path.getFileName() );
        }
        final String packageName = getPackageName( path );
        if ( packageName != null ) {
            pkg = new Package( path,
                               packageName,
                               getPackageDisplayName( packageName ) );
        }
        return new ProjectPackage( project,
                                   pkg );
    }
}
