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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ResourceContext;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
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

    private static final String MAIN_SRC_PATH = "src/main/java";
    private static final String TEST_SRC_PATH = "src/test/java";
    private static final String MAIN_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    private static String[] sourcePaths = { MAIN_SRC_PATH, MAIN_RESOURCES_PATH, TEST_SRC_PATH, TEST_RESOURCES_PATH };

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
        if ( group == null ) {
            return authorizedRepositories;
        }
        //Reload Group as the group's repository list might have been changed server-side
        final Collection<Repository> repositories = groupService.getGroup( group.getName() ).getRepositories();
        for ( Repository repository : repositories ) {
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
        if ( repository == null ) {
            return authorizedProjects;
        }
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( paths.convert( repositoryRoot ) );
        for ( org.kie.commons.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = paths.convert( nioRepositoryPath );
                final Project project = projectService.resolveProject( projectPath );
                if ( project != null ) {
                    if ( authorizationManager.authorize( project,
                                                         identity ) ) {
                        authorizedProjects.add( project );
                    }
                }
            }
        }
        return authorizedProjects;
    }

    @Override
    public Collection<Package> getPackages( final Project project ) {
        final Collection<Package> packages = new HashSet<Package>();
        final Set<String> packageNames = new HashSet<String>();
        if ( project == null ) {
            return packages;
        }
        //Build a set of all package names across /src/main/java, /src/main/resources, /src/test/java and /src/test/resources paths
        //It is possible (if the project was not created within the workbench that some packages only exist in certain paths)
        final Path projectRoot = project.getRootPath();
        final org.kie.commons.java.nio.file.Path nioProjectRootPath = paths.convert( projectRoot );
        for ( String src : sourcePaths ) {
            final org.kie.commons.java.nio.file.Path nioPackageRootSrcPath = nioProjectRootPath.resolve( src );
            packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                  nioPackageRootSrcPath ) );
        }

        //Construct Package objects for each package name
        final Set<String> resolvedPackages = new HashSet<String>();
        for ( String packagePathSuffix : packageNames ) {
            for ( String src : sourcePaths ) {
                final org.kie.commons.java.nio.file.Path nioPackagePath = nioProjectRootPath.resolve( src ).resolve( packagePathSuffix );
                if ( Files.exists( nioPackagePath ) && !resolvedPackages.contains( packagePathSuffix ) ) {
                    packages.add( makePackage( nioPackagePath ) );
                    resolvedPackages.add( packagePathSuffix );
                }
            }
        }

        return packages;
    }

    private Set<String> getPackageNames( final org.kie.commons.java.nio.file.Path nioProjectRootPath,
                                         final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final Set<String> packageNames = new HashSet<String>();
        if ( !Files.exists( nioPackageSrcPath ) ) {
            return packageNames;
        }
        packageNames.add( getPackagePathSuffix( nioProjectRootPath,
                                                nioPackageSrcPath ) );
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioChildPackageSrcPaths = ioService.newDirectoryStream( nioPackageSrcPath,
                                                                                                                          metaDataFileFilter );
        for ( org.kie.commons.java.nio.file.Path nioChildPackageSrcPath : nioChildPackageSrcPaths ) {
            if ( Files.isDirectory( nioChildPackageSrcPath ) ) {
                packageNames.addAll( getPackageNames( nioProjectRootPath,
                                                      nioChildPackageSrcPath ) );
            }
        }
        return packageNames;
    }

    private String getPackagePathSuffix( final org.kie.commons.java.nio.file.Path nioProjectRootPath,
                                         final org.kie.commons.java.nio.file.Path nioPackagePath ) {
        final org.kie.commons.java.nio.file.Path nioMainSrcPath = nioProjectRootPath.resolve( MAIN_SRC_PATH );
        final org.kie.commons.java.nio.file.Path nioTestSrcPath = nioProjectRootPath.resolve( TEST_SRC_PATH );
        final org.kie.commons.java.nio.file.Path nioMainResourcesPath = nioProjectRootPath.resolve( MAIN_RESOURCES_PATH );
        final org.kie.commons.java.nio.file.Path nioTestResourcesPath = nioProjectRootPath.resolve( TEST_RESOURCES_PATH );

        String packageName = null;
        org.kie.commons.java.nio.file.Path packagePath = null;
        if ( nioPackagePath.startsWith( nioMainSrcPath ) ) {
            packagePath = nioMainSrcPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioTestSrcPath ) ) {
            packagePath = nioTestSrcPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioMainResourcesPath ) ) {
            packagePath = nioMainResourcesPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        } else if ( nioPackagePath.startsWith( nioTestResourcesPath ) ) {
            packagePath = nioTestResourcesPath.relativize( nioPackagePath );
            packageName = packagePath.toString();
        }

        return packageName;
    }

    private Package makePackage( final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final Package pkg = projectService.resolvePackage( paths.convert( nioPackageSrcPath,
                                                                          false ) );
        return pkg;
    }

    @Override
    public Collection<FolderItem> getItems( final Package pkg ) {
        final Collection<FolderItem> folderItems = new HashSet<FolderItem>();
        if ( pkg == null ) {
            return folderItems;
        }
        folderItems.addAll( getItems( pkg.getPackageMainSrcPath() ) );
        folderItems.addAll( getItems( pkg.getPackageTestSrcPath() ) );
        folderItems.addAll( getItems( pkg.getPackageMainResourcesPath() ) );
        folderItems.addAll( getItems( pkg.getPackageTestResourcesPath() ) );
        return folderItems;
    }

    private Collection<FolderItem> getItems( final Path packagePath ) {
        final Collection<FolderItem> folderItems = new HashSet<FolderItem>();
        final org.kie.commons.java.nio.file.Path nioPackagePath = paths.convert( packagePath );
        if ( Files.exists( nioPackagePath ) ) {
            final DirectoryStream<org.kie.commons.java.nio.file.Path> nioPaths = ioService.newDirectoryStream( nioPackagePath,
                                                                                                               dotFileFilter );
            for ( org.kie.commons.java.nio.file.Path nioPath : nioPaths ) {
                if ( Files.isRegularFile( nioPath ) ) {
                    final org.uberfire.backend.vfs.Path path = paths.convert( nioPath );
                    final FolderItem folderItem = new FolderItem( path,
                                                                  path.getFileName(),
                                                                  FolderItemType.FILE );
                    folderItems.add( folderItem );
                }
            }
        }
        return folderItems;
    }

    @Override
    public FolderListing getFolderListing( final Path path ) {

        //Get list of files and folders contained in the path
        final Collection<FolderItem> folderItems = new HashSet<FolderItem>();

        //Scan upwards until the path exists (as the current path could have been deleted)
        org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
        while ( !Files.exists( nioPath ) ) {
            nioPath = nioPath.getParent();
        }
        final Path basePath = paths.convert( nioPath );
        final Path baseParentPath = paths.convert( nioPath.getParent() );
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioPaths = ioService.newDirectoryStream( nioPath,
                                                                                                           dotFileFilter );
        for ( org.kie.commons.java.nio.file.Path np : nioPaths ) {
            if ( Files.isRegularFile( np ) ) {
                final org.uberfire.backend.vfs.Path p = paths.convert( np );
                final FolderItem folderItem = new FolderItem( p,
                                                              p.getFileName(),
                                                              FolderItemType.FILE );
                folderItems.add( folderItem );
            } else if ( Files.isDirectory( np ) ) {
                final org.uberfire.backend.vfs.Path p = paths.convert( np );
                final FolderItem folderItem = new FolderItem( p,
                                                              p.getFileName(),
                                                              FolderItemType.FOLDER );
                folderItems.add( folderItem );
            }
        }

        //Get Path segments from the given Path back to the root
        final List<Path> segments = getPathSegments( basePath );

        return new FolderListing( basePath,
                                  baseParentPath,
                                  folderItems,
                                  segments );
    }

    private List<Path> getPathSegments( final Path path ) {
        org.kie.commons.java.nio.file.Path nioSegmentPath = paths.convert( path );
        //We're not interested in the terminal segment prior to root (i.e. the Project name)
        final int segmentCount = nioSegmentPath.getNameCount() - 1;
        if ( segmentCount < 1 ) {
            return new ArrayList<Path>();
        }
        //Order from root to leaf (as we use getParent from the leaf we add them in reverse order)
        final Path[] segments = new Path[ segmentCount ];
        for ( int idx = segmentCount; idx > 0; idx-- ) {
            segments[ idx - 1 ] = paths.convert( nioSegmentPath );
            nioSegmentPath = nioSegmentPath.getParent();
        }
        return Arrays.asList( segments );
    }

    @Override
    public ResourceContext resolveResourceContext( final Path path ) {
        if ( path == null ) {
            return new ResourceContext();
        }
        final Project project = projectService.resolveProject( path );
        final Package pkg = projectService.resolvePackage( path );
        return new ResourceContext( project,
                                    pkg );
    }

}
