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
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;

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

    @Inject
    private Paths paths;

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final AuthorizationManager authorizationManager,
                                final ProjectService projectService,
                                final OrganizationalUnitService organizationalUnitService,
                                final Identity identity,
                                final Paths paths ) {
        this.ioService = ioService;
        this.authorizationManager = authorizationManager;
        this.projectService = projectService;
        this.organizationalUnitService = organizationalUnitService;
        this.identity = identity;
        this.paths = paths;
    }

    @Override
    public ProjectExplorerContent getContent( final OrganizationalUnit organizationalUnit,
                                              final Repository repository,
                                              final Project project,
                                              final Package pkg ) {
        OrganizationalUnit selectedOrganizationalUnit = organizationalUnit;
        Repository selectedRepository = repository;
        Project selectedProject = project;
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

        final Set<Package> packages = getPackages( selectedProject );
        if ( !packages.contains( selectedPackage ) ) {
            selectedPackage = ( packages.isEmpty() ? null : packages.iterator().next() );
        }

        final Collection<FolderItem> items = getItems( selectedPackage );

        final ProjectExplorerContent content = new ProjectExplorerContent( organizationalUnits,
                                                                           selectedOrganizationalUnit,
                                                                           repositories,
                                                                           selectedRepository,
                                                                           projects,
                                                                           selectedProject,
                                                                           packages,
                                                                           selectedPackage,
                                                                           items );
        return content;
    }

    @Override
    public Set<OrganizationalUnit> getOrganizationalUnits() {
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

    @Override
    public Set<Repository> getRepositories( final OrganizationalUnit organizationalUnit ) {
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

    @Override
    public Set<Project> getProjects( final Repository repository ) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
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
    public Set<Package> getPackages( final Project project ) {
        return projectService.resolvePackages(project);
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
    public Package resolvePackage( final Path path ) {
        if ( path == null ) {
            return null;
        }
        final Package pkg = projectService.resolvePackage( path );
        return pkg;
    }

}
