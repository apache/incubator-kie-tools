/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.VFSLockServiceImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.concurrent.Managed;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.ext.editor.commons.backend.service.helper.RenameHelper;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Collections.emptyList;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotEmpty;

@Service
@Dependent
public class ExplorerServiceImpl
        implements ExplorerService {

    private static final Logger LOGGER = LoggerFactory.getLogger( ExplorerServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    private KieProjectService projectService;

    @Inject
    private ExplorerServiceHelper helper;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private UserServicesImpl userServices;

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private Instance<RenameHelper> renameHelpers;

    @Inject
    private Instance<CopyHelper> copyHelpers;

    @Inject
    private ProjectExplorerContentResolver projectExplorerContentResolver;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private VFSLockServiceImpl lockService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private CopyService copyService;

    @Inject
    //@AppResourcesAuthz
    private AuthorizationManager authorizationManager;

    @Inject
    @Managed
    private ExecutorService executorService;

    private XStream xs;

    // Boilerplate sacrifice for Weld
    public ExplorerServiceImpl() {
        xs = new XStream();
        String[] voidDeny = {"void.class", "Void.class"};
        xs.denyTypes(voidDeny);
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final KieProjectService projectService,
                                final OrganizationalUnitService organizationalUnitService,
                                final User identity ) {
        this();
        this.ioService = ioService;
        this.projectService = projectService;
        this.organizationalUnitService = organizationalUnitService;
        this.identity = identity;
    }

    @Override
    public ProjectExplorerContent getContent( final String _path,
                                              final ActiveOptions activeOptions ) {
        checkNotEmpty( "path", _path );

        final Path path = Paths.convert( ioService.get( URI.create( _path.trim() ) ) );
        final Project project = projectService.resolveProject( path );

        final Path convertedPath = Paths.convert( Paths.convert( path ).getRoot() );
        final Repository repo = repositoryService.getRepository( convertedPath );

        String branch = getBranchName( repo,
                                       convertedPath );

        OrganizationalUnit ou = null;
        for ( final OrganizationalUnit organizationalUnit : organizationalUnitService.getOrganizationalUnits() ) {
            if ( organizationalUnit.getRepositories().contains( repo ) ) {
                ou = organizationalUnit;
                break;
            }
        }

        return getContent( new ProjectExplorerContentQuery( ou,
                                                            repo,
                                                            branch,
                                                            project,
                                                            activeOptions ) );
    }

    private String getBranchName( final Repository repository,
                                  final Path convertedPath ) {
        for ( String branchName : repository.getBranches() ) {
            if ( repository.getBranchRoot( branchName ).equals( convertedPath ) ) {
                return branchName;
            }
        }
        return null;
    }

    @Override
    public ProjectExplorerContent getContent( final ProjectExplorerContentQuery query ) {
        return projectExplorerContentResolver.resolve( query );
    }

    @Override
    public URIStructureExplorerModel getURIStructureExplorerModel( final Path originalURI ) {
        final Project project = getURIProject( originalURI );
        final Repository repository = getURIRepository( originalURI );
        final OrganizationalUnit ou = getURIOrganizationalUnits( repository );
        return new URIStructureExplorerModel( ou,
                                              repository,
                                              project );
    }

    private KieProject getURIProject( final Path originalURI ) {
        return projectService.resolveProject( originalURI );
    }

    private Repository getURIRepository( final Path originalURI ) {
        org.uberfire.java.nio.file.Path ufPath = Paths.convert( originalURI );
        return repositoryService.getRepository( Paths.convert( ufPath.getRoot() ) );
    }

    private OrganizationalUnit getURIOrganizationalUnits( final Repository repository ) {
        for ( OrganizationalUnit organizationalUnit : getOrganizationalUnits() ) {
            if ( organizationalUnit.getRepositories().contains( repository ) ) {
                return organizationalUnit;
            }

        }
        throw new OrganizationalUnitNotFoundForURI();
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

    @Override
    public FolderListing getFolderListing( final OrganizationalUnit organizationalUnit,
                                           final Repository repository,
                                           final String branch,
                                           final Project project,
                                           final FolderItem item,
                                           final ActiveOptions options ) {
        //TODO: BUSINESS_CONTENT, TECHNICAL_CONTENT
        final FolderListing result = helper.getFolderListing( item,
                                                              options );

        if ( result != null ) {
            final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath( "explorer", "user.nav" );
            final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath( "explorer", "last.user.nav" );

            this.executorService.execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return "Serialize Navigation State";
                }

                @Override
                public void run() {
                    try {
                        Package pkg = null;
                        if ( item.getItem() instanceof Package ) {
                            pkg = (Package) item.getItem();
                        }
                        helper.store( userNavPath, lastUserNavPath, organizationalUnit,
                                      repository, branch, project, pkg, item, options );
                    } catch ( final Exception e ) {
                        LOGGER.error( "Can't serialize user's state navigation", e );
                    }
                }
            } );
        }

        return result;
    }

    private List<Path> resolvePath( final FolderItem item ) {
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
            //Path could represent a package
            if ( item.getType() == FolderItemType.FOLDER ) {
                final Package pkg = projectService.resolvePackage( (Path) item.getItem() );
                if ( pkg == null ) {
                    return new ArrayList<Path>( 1 ) {{
                        add( (Path) item.getItem() );
                    }};
                } else {
                    return new ArrayList<Path>( 4 ) {{
                        add( pkg.getPackageMainResourcesPath() );
                        add( pkg.getPackageMainSrcPath() );
                        add( pkg.getPackageTestResourcesPath() );
                        add( pkg.getPackageTestSrcPath() );
                    }};
                }

            } else {
                return new ArrayList<Path>( 1 ) {{
                    add( (Path) item.getItem() );
                }};
            }
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
        return helper.getLastContent().getOptions();
    }

    @Override
    public void deleteItem( final FolderItem folderItem,
                            final String comment ) {

        final Collection<Path> paths = resolvePath( folderItem );
        deleteService.deleteIfExists( paths, comment );
    }

    @Override
    public void renameItem( final FolderItem folderItem,
                            final String newName,
                            final String comment ) {
        final Collection<Path> paths = resolvePath( folderItem );
        renameService.renameIfExists( paths, newName, comment );
    }

    @Override
    public void copyItem( final FolderItem folderItem,
                          final String newName,
                          final Path targetDirectory,
                          final String comment ) {
        final List<Path> paths = resolvePath( folderItem );

        if ( paths != null && paths.size() == 1 ){
            copyService.copy( paths.get( 0 ), newName, targetDirectory, comment );
        } else {
            // when copying packages
            copyService.copyIfExists( paths, newName, comment );
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

        try {
            ioServiceConfig.startBatch( fileSystem );

            for ( org.uberfire.java.nio.file.Path path : userNavs ) {
                final UserExplorerData userContent = helper.loadUserContent( path );
                if ( userContent != null ) {
                    if ( userContent.deleteProject( project ) ) {
                        ioServiceConfig.write( path, xs.toXML( userContent ) );
                    }
                }
            }

            for ( org.uberfire.java.nio.file.Path lastNav : lastNavs ) {
                final UserExplorerLastData lastUserContent = helper.getLastContent( lastNav );
                if ( lastUserContent != null ) {
                    if ( lastUserContent.deleteProject( project ) ) {
                        ioServiceConfig.write( lastNav, xs.toXML( lastUserContent ) );
                    }
                }
            }
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    public class OrganizationalUnitNotFoundForURI extends RuntimeException {

    }

}
