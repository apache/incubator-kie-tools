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
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.CopyHelper;
import org.guvnor.common.services.backend.file.RenameHelper;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.model.URIStructureExplorerModel;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.server.cdi.AppResourcesAuthz;

import static java.util.Collections.*;

@Service
@ApplicationScoped
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
    private KieProjectService projectService;

    @Inject
    private ExplorerServiceHelper helper;

    @Inject
    private DeleteService deleteService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

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

    @Inject
    private Instance<RenameHelper> renameHelpers;

    @Inject
    private Instance<CopyHelper> copyHelpers;

    @Inject
    private ProjectExplorerContentResolver projectExplorerContentResolver;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    @AppResourcesAuthz
    private AuthorizationManager authorizationManager;

    private XStream xs = new XStream();

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final KieProjectService projectService,
                                final OrganizationalUnitService organizationalUnitService,
                                final Identity identity ) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.organizationalUnitService = organizationalUnitService;
        this.identity = identity;
    }

    @Override
    public ProjectExplorerContent getContent( final ProjectExplorerContentQuery query ) {

        return projectExplorerContentResolver.resolve( query );

    }

    @Override
    public URIStructureExplorerModel getURIStructureExplorerModel( Path originalURI ) {
        Project project = getURIProject( originalURI );
        Repository repository = getURIRepository( originalURI );
        OrganizationalUnit ou = getURIOrganizationalUnits( repository );
        return new URIStructureExplorerModel( ou, repository, project );
    }

    private KieProject getURIProject( Path originalURI ) {
        return projectService.resolveProject( originalURI );
    }

    private Repository getURIRepository( Path originalURI ) {
        org.uberfire.java.nio.file.Path ufPath = Paths.convert( originalURI );
        return repositoryService.getRepository( Paths.convert( ufPath.getRoot() ) );
    }

    private OrganizationalUnit getURIOrganizationalUnits( Repository repository ) {
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
                                           final Project project,
                                           final FolderItem item,
                                           final Set<Option> options ) {
        //TODO: BUSINESS_CONTENT, TECHNICAL_CONTENT
        final FolderListing result = helper.getFolderListing( item );

        if ( result != null ) {
            final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath( "explorer", "user.nav" );
            final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath( "explorer", "last.user.nav" );

            SimpleAsyncExecutorService.getDefaultInstance().execute( new DescriptiveRunnable() {
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
                                      repository, project, pkg, item, options );
                    } catch ( final Exception e ) {
                        LOGGER.error( "Can't serialize user's state navigation", e );
                    }
                }
            } );
        }

        return result;
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
        return helper.getLastContent().getOptions();
    }

    @Override
    public void deleteItem( final FolderItem folderItem,
                            final String comment ) {

        final Collection<Path> paths = resolvePath( folderItem );

        try {
            if ( paths.size() > 1 ) {
                ioService.startBatch( new FileSystem[]{ Paths.convert( paths.iterator().next() ).getFileSystem() } );
            }

            for ( final Path path : paths ) {
                ioService.deleteIfExists( Paths.convert( path ),
                                          new CommentedOption( sessionInfo.getId(),
                                                               identity.getName(),
                                                               null,
                                                               comment ),
                                          StandardDeleteOption.NON_EMPTY_DIRECTORIES
                                        );
            }
        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            if ( paths.size() > 1 ) {
                ioService.endBatch();
            }
        }
    }

    @Override
    public void renameItem( final FolderItem folderItem,
                            final String newName,
                            final String comment ) {
        final Collection<Path> paths = resolvePath( folderItem );

        try {
            //Always use a batch as RenameHelpers may be involved with the rename operation
            ioService.startBatch( new FileSystem[]{ Paths.convert( paths.iterator().next() ).getFileSystem() } );

            for ( final Path path : paths ) {
                final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

                if ( Files.exists( _path ) ) {
                    final org.uberfire.java.nio.file.Path _target;
                    if ( Files.isDirectory( _path ) ) {
                        _target = _path.resolveSibling( newName );
                    } else {
                        final String originalFileName = _path.getFileName().toString();
                        final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
                        _target = _path.resolveSibling( newName + extension );
                    }

                    ioService.move( _path,
                                    _target,
                                    new CommentedOption( sessionInfo.getId(),
                                                         identity.getName(),
                                                         null,
                                                         comment )
                                  );

                    //Delegate additional changes required for a rename to applicable Helpers
                    if ( _target != null ) {
                        final Path targetPath = Paths.convert( _target );
                        for ( RenameHelper helper : renameHelpers ) {
                            if ( helper.supports( targetPath ) ) {
                                helper.postProcess( path,
                                                    targetPath );
                            }
                        }
                    }
                }
            }
        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public void copyItem( final FolderItem folderItem,
                          final String newName,
                          final String comment ) {
        final Collection<Path> paths = resolvePath( folderItem );

        try {
            //Always use a batch as CopyHelpers may be involved with the rename operation
            ioService.startBatch( new FileSystem[]{ Paths.convert( paths.iterator().next() ).getFileSystem() } );

            for ( final Path path : paths ) {
                final org.uberfire.java.nio.file.Path _path = Paths.convert( path );

                if ( Files.exists( _path ) ) {
                    final org.uberfire.java.nio.file.Path _target;
                    if ( Files.isDirectory( _path ) ) {
                        _target = _path.resolveSibling( newName );
                    } else {
                        final String originalFileName = _path.getFileName().toString();
                        final String extension = originalFileName.substring( originalFileName.lastIndexOf( "." ) );
                        _target = _path.resolveSibling( newName + extension );
                    }

                    ioService.copy( _path,
                                    _target,
                                    new CommentedOption( sessionInfo.getId(),
                                                         identity.getName(),
                                                         null,
                                                         comment )
                                  );

                    //Delegate additional changes required for a copy to applicable Helpers
                    if ( _target != null ) {
                        final Path targetPath = Paths.convert( _target );
                        for ( CopyHelper helper : copyHelpers ) {
                            if ( helper.supports( targetPath ) ) {
                                helper.postProcess( path,
                                                    targetPath );
                            }
                        }
                    }
                }
            }
        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        } finally {
            ioService.endBatch();
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
    }

    public class OrganizationalUnitNotFoundForURI extends RuntimeException {

    }

}