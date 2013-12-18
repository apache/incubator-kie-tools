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
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ExplorerServiceImpl
        implements ExplorerService {

    private static final Logger LOGGER = LoggerFactory.getLogger( ExplorerServiceImpl.class );

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    @Inject
    private ProjectService projectService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    @SessionScoped
    private Identity identity;

    @Inject
    @SessionScoped
    private SessionInfo sessionInfo;

    @Inject
    private UserServicesImpl userServices;

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    private XStream xs = new XStream();

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
        Package selectedPackage = pkg;
        FolderItem selectedItem = item;

        final UserExplorerLastData lastContent = getLastContent();
        final UserExplorerData userContent = loadUserContent();

        if ( !lastContent.isDataEmpty() ) {
            if ( organizationalUnit == null && repository == null && project == null ) {
                if ( options.contains( Option.BUSINESS_CONTENT ) && lastContent.getLastPackage() != null ) {
                    selectedOrganizationalUnit = lastContent.getLastPackage().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastPackage().getRepository();
                    selectedProject = lastContent.getLastPackage().getProject();
                    selectedPackage = lastContent.getLastPackage().getPkg();
                    selectedItem = null;
                } else if ( options.contains( Option.TECHNICAL_CONTENT ) && lastContent.getLastFolderItem() != null ) {
                    selectedOrganizationalUnit = lastContent.getLastFolderItem().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastFolderItem().getRepository();
                    selectedProject = lastContent.getLastFolderItem().getProject();
                    selectedItem = lastContent.getLastFolderItem().getItem();
                    selectedPackage = null;
                }
            } else if ( options.contains( Option.BUSINESS_CONTENT ) && lastContent.getLastPackage() != null ) {
                if ( !organizationalUnit.equals( lastContent.getLastPackage().getOrganizationalUnit() ) ||
                        repository != null && !repository.equals( lastContent.getLastPackage().getRepository() ) ||
                        project != null && !project.equals( lastContent.getLastPackage().getProject() ) ) {
                    selectedOrganizationalUnit = loadOrganizationalUnit( organizationalUnit, userContent );
                    selectedRepository = loadRepository( selectedOrganizationalUnit, repository, userContent );
                    selectedProject = loadProject( selectedOrganizationalUnit, selectedRepository, project, userContent );
                    selectedPackage = loadPackage( selectedOrganizationalUnit, selectedRepository, selectedProject, pkg, userContent );
                    selectedItem = null;
                }
            } else if ( options.contains( Option.TECHNICAL_CONTENT ) && lastContent.getLastFolderItem() != null ) {
                if ( !organizationalUnit.equals( lastContent.getLastFolderItem().getOrganizationalUnit() ) ||
                        repository != null && !repository.equals( lastContent.getLastFolderItem().getRepository() ) ||
                        project != null && !project.equals( lastContent.getLastFolderItem().getProject() ) ) {
                    selectedOrganizationalUnit = loadOrganizationalUnit( organizationalUnit, userContent );
                    selectedRepository = loadRepository( selectedOrganizationalUnit, repository, userContent );
                    selectedProject = loadProject( selectedOrganizationalUnit, selectedRepository, project, userContent );
                    selectedItem = loadFolderItem( selectedOrganizationalUnit, selectedRepository, selectedProject, item, userContent );
                    selectedPackage = null;
                }
            }
        }

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

        if ( selectedOrganizationalUnit == null || selectedRepository == null || selectedProject == null ) {
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
                if ( selectedPackage == null ) {
                    defautlPackage = projectService.resolveDefaultPackage( selectedProject );
                    segments = Collections.emptyList();
                } else {
                    defautlPackage = selectedPackage;
                    segments = getPackageSegments( selectedPackage );
                }
                folderListing = new FolderListing( toFolderItem( defautlPackage ),
                                                   getItems( defautlPackage ),
                                                   segments );
            } else {
                folderListing = getFolderListing( selectedProject.getRootPath() );
            }
        } else {
            folderListing = getFolderListing( selectedItem );
        }

        if ( selectedPackage != null && folderListing == null ) {
            folderListing = new FolderListing( toFolderItem( selectedPackage ),
                                               getItems( selectedPackage ),
                                               getPackageSegments( selectedPackage ) );
        }

        final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath( "explorer", "user.nav" );
        final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath( "explorer", "last.user.nav" );

        final OrganizationalUnit _selectedOrganizationalUnit = selectedOrganizationalUnit;
        final Repository _selectedRepository = selectedRepository;
        final Project _selectedProject = selectedProject;
        final FolderItem _selectedItem = folderListing.getItem();
        final Package _selectedPackage;
        if ( selectedPackage != null ) {
            _selectedPackage = selectedPackage;
        } else if ( folderListing.getItem().getItem() instanceof Package ) {
            _selectedPackage = (Package) folderListing.getItem().getItem();
        } else {
            _selectedPackage = null;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    store( userNavPath, lastUserNavPath, _selectedOrganizationalUnit,
                           _selectedRepository, _selectedProject,
                           _selectedPackage, _selectedItem, options );
                } catch ( final Exception e ) {
                    LOGGER.error( "Can't serialize user's state navigation", e );
                }
            }
        }.start();

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

    private OrganizationalUnit loadOrganizationalUnit( final OrganizationalUnit organizationalUnit,
                                                       final UserExplorerData content ) {
        if ( organizationalUnit != null ) {
            return organizationalUnit;
        }

        return content.getOrganizationalUnit();
    }

    private Repository loadRepository( final OrganizationalUnit organizationalUnit,
                                       final Repository repository,
                                       final UserExplorerData content ) {
        if ( organizationalUnit == null ) {
            return null;
        }
        if ( repository != null ) {
            return repository;
        }

        return content.get( organizationalUnit );
    }

    private Project loadProject( final OrganizationalUnit organizationalUnit,
                                 final Repository repository,
                                 final Project project,
                                 final UserExplorerData content ) {
        if ( repository == null ) {
            return null;
        }
        if ( project != null ) {
            return project;
        }

        return content.get( organizationalUnit, repository );
    }

    private Package loadPackage( final OrganizationalUnit organizationalUnit,
                                 final Repository repository,
                                 final Project project,
                                 final Package pkg,
                                 final UserExplorerData content ) {
        if ( project == null ) {
            return null;
        }

        if ( pkg != null ) {
            return pkg;
        }

        return content.getPackage( organizationalUnit, repository, project );
    }

    private FolderItem loadFolderItem( final OrganizationalUnit organizationalUnit,
                                       final Repository repository,
                                       final Project project,
                                       final FolderItem item,
                                       final UserExplorerData content ) {
        if ( project == null ) {
            return null;
        }

        if ( item != null ) {
            return item;
        }

        return content.getFolderItem( organizationalUnit, repository, project );
    }

    private UserExplorerData loadUserContent() {
        final UserExplorerData userExplorerData = loadUserContent( userServices.buildPath( "explorer", "user.nav" ) );
        if ( userExplorerData != null ) {
            return userExplorerData;
        }
        return new UserExplorerData();
    }

    private UserExplorerData loadUserContent( final org.uberfire.java.nio.file.Path path ) {
        try {
            if ( ioServiceConfig.exists( path ) ) {
                final String xml = ioServiceConfig.readAllString( path );
                return (UserExplorerData) xs.fromXML( xml );
            }
        } catch ( final Exception e ) {
        }
        return null;
    }

    private UserExplorerLastData getLastContent() {
        try {
            final UserExplorerLastData lastData = getLastContent( userServices.buildPath( "explorer", "last.user.nav" ) );
            if ( lastData != null ) {
                return lastData;
            }
        } catch ( final Exception e ) {
        }
        return new UserExplorerLastData();
    }

    private UserExplorerLastData getLastContent( final org.uberfire.java.nio.file.Path path ) {
        try {
            if ( ioServiceConfig.exists( path ) ) {
                final String xml = ioServiceConfig.readAllString( path );
                return (UserExplorerLastData) xs.fromXML( xml );
            }
        } catch ( final Exception e ) {
        }
        return null;
    }

    private void store( final org.uberfire.java.nio.file.Path userNav,
                        final org.uberfire.java.nio.file.Path lastUserNav,
                        final OrganizationalUnit organizationalUnit,
                        final Repository repository,
                        final Project project,
                        final Package pkg,
                        final FolderItem item,
                        final Set<Option> options ) {
        final UserExplorerData content;
        final UserExplorerData _content = loadUserContent( userNav );
        if ( _content == null ) {
            content = new UserExplorerData();
        } else {
            content = _content;
        }
        final UserExplorerLastData lastContent = new UserExplorerLastData();
        if ( organizationalUnit != null ) {
            content.setOrganizationalUnit( organizationalUnit );
        }
        if ( repository != null && organizationalUnit != null ) {
            content.addRepository( organizationalUnit, repository );
        }
        if ( project != null && organizationalUnit != null && repository != null ) {
            content.addProject( organizationalUnit, repository, project );
        }
        if ( item != null && organizationalUnit != null && repository != null && project != null ) {
            lastContent.setFolderItem( organizationalUnit, repository, project, item );
            content.addFolderItem( organizationalUnit, repository, project, item );
        }
        if ( pkg != null && organizationalUnit != null && repository != null && project != null ) {
            lastContent.setPackage( organizationalUnit, repository, project, pkg );
            content.addPackage( organizationalUnit, repository, project, pkg );
        }
        if ( options != null && !options.isEmpty() ) {
            lastContent.setOptions( options );
        }
        if ( !content.isEmpty() ) {
            ioServiceConfig.write( userNav,
                                   xs.toXML( content ) );
            ioServiceConfig.write( lastUserNav,
                                   xs.toXML( lastContent ) );
        }
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
    public FolderListing getFolderListing( final OrganizationalUnit organizationalUnit,
                                           final Repository repository,
                                           final Project project,
                                           final FolderItem item,
                                           final Set<Option> options ) {
        //TODO: BUSINESS_CONTENT, TECHNICAL_CONTENT
        final FolderListing result = getFolderListing( item );

        if ( result != null ) {
            final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath( "explorer", "user.nav" );
            final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath( "explorer", "last.user.nav" );

            new Thread() {
                @Override
                public void run() {
                    try {
                        Package pkg = null;
                        if ( item.getItem() instanceof Package ) {
                            pkg = (Package) item.getItem();
                        }
                        store( userNavPath, lastUserNavPath, organizationalUnit,
                               repository, project, pkg, item, options );
                    } catch ( final Exception e ) {
                        LOGGER.error( "Can't serialize user's state navigation", e );
                    }
                }
            }.start();
        }

        return result;
    }

    private FolderListing getFolderListing( final FolderItem item ) {

        FolderListing result = null;
        if ( item.getItem() instanceof Path ) {
            result = getFolderListing( (Path) item.getItem() );
        } else if ( item.getItem() instanceof Package ) {
            result = getFolderListing( (Package) item.getItem() );
        }

        return result;
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

    private Collection<Path> resolvePath( final FolderItem item ) {
        if ( item == null ) {
            return emptyList();
        }

        if ( item.getItem() instanceof Package ) {
            final Package pkg = ( (Package) item.getItem() );
            return new ArrayList<Path>( 4 ) {{
                add( pkg.getPackageMainResourcesPath() );
                add( pkg.getPackageMainSrcPath() );
                add( pkg.getPackageTestResourcesPath() );
                add( pkg.getPackageTestSrcPath() );
            }};
        }

        if ( item.getItem() instanceof Path ) {
            return new ArrayList<Path>( 1 ) {{
                add( (Path) item.getItem() );
            }};
        }

        return emptyList();
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

    @Override
    public Set<Option> getLastUserOptions() {
        return getLastContent().getOptions();
    }

    @Override
    public void deleteItem( final FolderItem folderItem,
                            final String comment ) {

        final Collection<Path> paths = resolvePath( folderItem );

        try {
            if ( paths.size() > 1 ) {
                ioService.startBatch();
            }

            for ( final Path path : paths ) {
                ioService.delete( Paths.convert( path ),
                                  new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ),
                                  StandardDeleteOption.NON_EMPTY_DIRECTORIES );
            }

            if ( paths.size() > 1 ) {
                ioService.endBatch();
            }

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void renameItem( final FolderItem folderItem,
                            final String newName,
                            final String comment ) {
        final Collection<Path> paths = resolvePath( folderItem );

        try {
            if ( paths.size() > 1 ) {
                ioService.startBatch();
            }

            for ( final Path path : paths ) {
                final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

                final org.uberfire.java.nio.file.Path _target;
                if ( Files.isDirectory( _path ) ) {
                    _target = _path.resolveSibling( newName );
                } else {
                    final String originalFileName = _path.getFileName().toString();
                    final String extension = originalFileName.substring( originalFileName.indexOf( "." ) );
                    _target = _path.resolveSibling( newName + extension );
                }

                ioService.move( _path,
                                _target,
                                new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ) );
            }

            if ( paths.size() > 1 ) {
                ioService.endBatch();
            }

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void copyItem( final FolderItem folderItem,
                          final String newName,
                          final String comment ) {
        final Collection<Path> paths = resolvePath( folderItem );

        try {
            if ( paths.size() > 1 ) {
                ioService.startBatch();
            }

            for ( final Path path : paths ) {
                final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

                final org.uberfire.java.nio.file.Path _target;
                if ( Files.isDirectory( _path ) ) {
                    _target = _path.resolveSibling( newName );
                } else {
                    final String originalFileName = _path.getFileName().toString();
                    final String extension = originalFileName.substring( originalFileName.indexOf( "." ) );
                    _target = _path.resolveSibling( newName + extension );
                }

                ioService.copy( _path,
                                _target,
                                new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ) );
            }

            if ( paths.size() > 1 ) {
                ioService.endBatch();
            }

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    void onProjectRename( @Observes final RenameProjectEvent event ) {
        cleanup( event.getOldProject() );
    }

    void onProjectDelete( @Observes final DeleteProjectEvent event ) {
        cleanup( event.getProject() );
    }

    private void cleanup( final Project project ) {
        final Collection<org.uberfire.java.nio.file.Path> lastNavs = userServicesBackend.getAllUsersData( "explorer", "last.user.nav" );
        final Collection<org.uberfire.java.nio.file.Path> userNavs = userServicesBackend.getAllUsersData( "explorer", "user.nav" );

        for ( org.uberfire.java.nio.file.Path path : userNavs ) {
            final UserExplorerData userContent = loadUserContent( path );
            if ( userContent != null ) {
                if ( userContent.deleteProject( project ) ) {
                    ioServiceConfig.write( path, xs.toXML( userContent ) );
                }
            }
        }

        for ( org.uberfire.java.nio.file.Path lastNav : lastNavs ) {
            final UserExplorerLastData lastUserContent = getLastContent( lastNav );
            if ( lastUserContent != null ) {
                if ( lastUserContent.deleteProject( project ) ) {
                    ioServiceConfig.write( lastNav, xs.toXML( lastUserContent ) );
                }
            }

        }
    }

}
