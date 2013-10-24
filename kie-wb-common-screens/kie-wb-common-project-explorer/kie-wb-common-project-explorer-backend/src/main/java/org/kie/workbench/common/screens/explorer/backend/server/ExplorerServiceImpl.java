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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ExplorerServiceImpl
        implements ExplorerService {

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    @SessionScoped
    private Identity identity;

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final AuthorizationManager authorizationManager,
                                final ProjectService projectService,
                                final OrganizationalUnitService organizationalUnitService,
                                final Identity identity ) {
        this.ioService = ioService;
        this.authorizationManager = authorizationManager;
        this.projectService = projectService;
        this.organizationalUnitService = organizationalUnitService;
        this.identity = identity;
    }

    @Override
    public ProjectExplorerContent getContent( final OrganizationalUnit organizationalUnit,
                                              final Repository repository,
                                              final Project project,
                                              final Package pkg,
                                              final FolderItem item,
                                              final Set<Option> options ) {
        OrganizationalUnit selectedOrganizationalUnit = organizationalUnit;
        Repository selectedRepository = repository;
        Project selectedProject = project;
        FolderItem selectedItem = item;
        Package selectedPackage = pkg;

        final Set<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        if ( !organizationalUnits.contains( selectedOrganizationalUnit ) ) {
            selectedOrganizationalUnit = ( organizationalUnits.isEmpty() ? null : organizationalUnits.iterator().next() );
        }

        final Set<Repository> repositories = getRepositories( selectedOrganizationalUnit );
        if ( !repositories.contains( selectedRepository ) ) {
            selectedRepository = ( repositories.isEmpty() ? null : repositories.iterator().next() );
        }

        final Set<Project> projects = getProjects( selectedRepository );
        if ( !projects.contains( selectedProject ) ) {
            selectedProject = ( projects.isEmpty() ? null : projects.iterator().next() );
        }

        if ( selectedOrganizationalUnit == null || selectedRepository == null ||
                selectedProject == null ) {
            return new ProjectExplorerContent(
                    new TreeSet<OrganizationalUnit>( Sorters.ORGANIZATIONAL_UNIT_SORTER ) {{
                        addAll( organizationalUnits );
                    }},
                    selectedOrganizationalUnit,
                    new TreeSet<Repository>( Sorters.REPOSITORY_SORTER ) {{
                        addAll( repositories );
                    }},
                    selectedRepository,
                    new TreeSet<Project>( Sorters.PROJECT_SORTER ) {{
                        addAll( projects );
                    }},
                    selectedProject,
                    new FolderListing( null, Collections.<FolderItem>emptyList(), Collections.<FolderItem>emptyList() )
            );
        }

        FolderListing folderListing = null;
        if ( selectedItem == null ) {
            final List<FolderItem> segments;
            if ( options.contains( Option.BUSINESS_CONTENT ) ) {
                final Package defautlPackage;
                if ( pkg == null ) {
                    defautlPackage = projectService.resolveDefaultPackage( selectedProject );
                    segments = Collections.emptyList();
                } else {
                    defautlPackage = pkg;
                    segments = getPackageSegments( pkg );
                }
                folderListing = new FolderListing( toFolderItem( defautlPackage ),
                                                   getItems( defautlPackage ),
                                                   segments );
            } else {
                folderListing = getFolderListing( selectedProject.getRootPath() );
            }
        } else {
            folderListing = getFolderListing( selectedItem, options );
        }

        if ( selectedPackage != null && folderListing == null ) {
            folderListing = new FolderListing( toFolderItem( selectedPackage ),
                                               getItems( selectedPackage ),
                                               getPackageSegments( pkg ) );
        }

        return new ProjectExplorerContent(
                new TreeSet<OrganizationalUnit>( Sorters.ORGANIZATIONAL_UNIT_SORTER ) {{
                    addAll( organizationalUnits );
                }},
                selectedOrganizationalUnit,
                new TreeSet<Repository>( Sorters.REPOSITORY_SORTER ) {{
                    addAll( repositories );
                }},
                selectedRepository,
                new TreeSet<Project>( Sorters.PROJECT_SORTER ) {{
                    addAll( projects );
                }},
                selectedProject,
                folderListing
        );
    }

    private FolderItem toFolderItem( final Package pkg ) {
        if ( pkg == null ) {
            return null;
        }
        return new FolderItem( pkg, pkg.getRelativeCaption(), FolderItemType.FOLDER );
    }

    private Set<OrganizationalUnit> getOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Set<OrganizationalUnit> authorizedOrganizationalUnits = new HashSet<OrganizationalUnit>();
        for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
            if ( authorizationManager.authorize( organizationalUnit,
                                                 identity ) ) {
                authorizedOrganizationalUnits.add( organizationalUnit );
            }
        }
        return authorizedOrganizationalUnits;
    }

    private Set<Repository> getRepositories( final OrganizationalUnit organizationalUnit ) {
        final Set<Repository> authorizedRepositories = new HashSet<Repository>();
        if ( organizationalUnit == null ) {
            return authorizedRepositories;
        }
        //Reload OrganizationalUnit as the organizational unit's repository list might have been changed server-side
        final Collection<Repository> repositories = organizationalUnitService.getOrganizationalUnit( organizationalUnit.getName() ).getRepositories();
        for ( Repository repository : repositories ) {
            if ( authorizationManager.authorize( repository,
                                                 identity ) ) {
                authorizedRepositories.add( repository );
            }
        }
        return authorizedRepositories;
    }

    private Set<Project> getProjects( final Repository repository ) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if ( repository == null ) {
            return authorizedProjects;
        }
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( Paths.convert( repositoryRoot ) );
        for ( org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = Paths.convert( nioRepositoryPath );
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

    private List<FolderItem> getItems( final Package pkg ) {
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();
        if ( pkg == null ) {
            return emptyList();
        }

        final Set<Package> childPackages = projectService.resolvePackages( pkg );
        for ( final Package childPackage : childPackages ) {
            folderItems.add( toFolderItem( childPackage ) );
        }

        folderItems.addAll( getItems( pkg.getPackageMainSrcPath() ) );
        folderItems.addAll( getItems( pkg.getPackageTestSrcPath() ) );
        folderItems.addAll( getItems( pkg.getPackageMainResourcesPath() ) );
        folderItems.addAll( getItems( pkg.getPackageTestResourcesPath() ) );

        Collections.sort( folderItems, Sorters.ITEM_SORTER );

        return folderItems;
    }

    private List<FolderItem> getItems( final Path packagePath ) {
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( packagePath );
        if ( Files.exists( nioPackagePath ) ) {
            final DirectoryStream<org.uberfire.java.nio.file.Path> nioPaths = ioService.newDirectoryStream( nioPackagePath,
                                                                                                            dotFileFilter );
            for ( org.uberfire.java.nio.file.Path nioPath : nioPaths ) {
                if ( Files.isRegularFile( nioPath ) ) {
                    final org.uberfire.backend.vfs.Path path = Paths.convert( nioPath );
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
    public FolderListing getFolderListing( final FolderItem item,
                                           final Set<Option> options ) {
        if ( item.getItem() instanceof Path ) {
            return getFolderListing( (Path) item.getItem() );
        } else if ( item.getItem() instanceof Package ) {
            return getFolderListing( (Package) item.getItem() );
        }

        return null;
    }

    private FolderListing getFolderListing( final Path path ) {
        //Get list of files and folders contained in the path
        final List<FolderItem> folderItems = new ArrayList<FolderItem>();

        //Scan upwards until the path exists (as the current path could have been deleted)
        org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        while ( !Files.exists( nioPath ) ) {
            nioPath = nioPath.getParent();
        }
        final Path basePath = Paths.convert( nioPath );
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioPaths = ioService.newDirectoryStream( nioPath,
                                                                                                        dotFileFilter );
        for ( org.uberfire.java.nio.file.Path np : nioPaths ) {
            if ( Files.isRegularFile( np ) ) {
                final org.uberfire.backend.vfs.Path p = Paths.convert( np );
                final FolderItem folderItem = new FolderItem( p,
                                                              p.getFileName(),
                                                              FolderItemType.FILE );
                folderItems.add( folderItem );
            } else if ( Files.isDirectory( np ) ) {
                final org.uberfire.backend.vfs.Path p = Paths.convert( np );
                final FolderItem folderItem = new FolderItem( p,
                                                              p.getFileName(),
                                                              FolderItemType.FOLDER );
                folderItems.add( folderItem );
            }
        }

        Collections.sort( folderItems, Sorters.ITEM_SORTER );

        return new FolderListing( toFolderItem( nioPath ),
                                  folderItems,
                                  getPathSegments( basePath ) );
    }

    private FolderListing getFolderListing( final Package pkg ) {
        return new FolderListing( toFolderItem( pkg ),
                                  getItems( pkg ),
                                  getPackageSegments( pkg ) );
    }

    private List<FolderItem> getPathSegments( final Path path ) {
        org.uberfire.java.nio.file.Path nioSegmentPath = Paths.convert( path ).getParent();
        //We're not interested in the terminal segment prior to root (i.e. the Project name)
        final int segmentCount = nioSegmentPath.getNameCount();
        if ( segmentCount < 1 ) {
            return new ArrayList<FolderItem>();
        }
        //Order from root to leaf (as we use getParent from the leaf we add them in reverse order)
        final FolderItem[] segments = new FolderItem[ segmentCount ];
        for ( int idx = segmentCount; idx > 0; idx-- ) {
            segments[ idx - 1 ] = toFolderItem( nioSegmentPath );
            nioSegmentPath = nioSegmentPath.getParent();
        }
        return Arrays.asList( segments );
    }

    private List<FolderItem> getPackageSegments( final Package _pkg ) {

        List<FolderItem> result = new ArrayList<FolderItem>();
        Package pkg = _pkg;
        while ( pkg != null ) {
            final Package parent = projectService.resolveParentPackage( pkg );
            if ( parent != null ) {
                result.add( toFolderItem( parent ) );
            }
            pkg = parent;
        }

        return Lists.reverse( result );
    }

    private FolderItem toFolderItem( final org.uberfire.java.nio.file.Path path ) {
        if ( Files.isRegularFile( path ) ) {
            final org.uberfire.backend.vfs.Path p = Paths.convert( path );
            return new FolderItem( p,
                                   p.getFileName(),
                                   FolderItemType.FILE );
        } else if ( Files.isDirectory( path ) ) {
            final org.uberfire.backend.vfs.Path p = Paths.convert( path );
            return new FolderItem( p,
                                   p.getFileName(),
                                   FolderItemType.FOLDER );
        }

        return null;
    }

    @Override
    public Package resolvePackage( final FolderItem item ) {
        if ( item == null ) {
            return null;
        }
        if ( item.getItem() instanceof Package ) {
            return (Package) item.getItem();
        }
        if ( item.getItem() instanceof Path ) {
            return projectService.resolvePackage( (Path) item.getItem() );
        }

        return null;
    }

}
