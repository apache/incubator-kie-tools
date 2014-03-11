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

package org.uberfire.client.editors.fileexplorer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryRemovedEvent;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View view;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private PlaceManager placeManager;

    private Set<Repository> repositories = new HashSet<Repository>();

    public interface View
            extends
            UberView<FileExplorerPresenter> {

        void reset();

        void removeRepository( final Repository repo );

        void addNewRepository( final Repository repo );
    }

    public static interface FileExplorerItem {

        void addDirectory( final Path child );

        void addFile( final Path child );
    }

    @OnStartup
    public void onStartup() {

        view.reset();
        repositories.clear();

        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
                                    @Override
                                    public void callback( Collection<Repository> response ) {
                                        for ( final Repository root : response ) {
                                            if ( repositories.contains( root ) ) {
                                                view.removeRepository( root );
                                            }
                                            view.addNewRepository( root );
                                            repositories.add( root );
                                        }
                                    }
                                }, new ErrorCallback<Message>() {
                                    @Override
                                    public boolean error( final Message o,
                                                          final Throwable throwable ) {
                                        return false;
                                    }
                                }
                              ).getRepositories();
    }

    public void loadDirectoryContent( final FileExplorerItem item,
                                      final Path path ) {
        vfsService.call( new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback( DirectoryStream<Path> response ) {
                for ( final Path child : response ) {
                    vfsService.call( new RemoteCallback<Map>() {
                        @Override
                        public void callback( final Map response ) {
                            if ( isDirectory( response ) ) {
                                item.addDirectory( child );
                            } else {
                                item.addFile( child );
                            }
                        }
                    } ).readAttributes( child );
                }
            }
        } ).newDirectoryStream( path );
    }

    private boolean isDirectory( final Map response ) {
        return response != null && response.containsKey( "isDirectory" ) && (Boolean) response.get( "isDirectory" );
    }

    private boolean isRegularFile( final Map response ) {
        return response != null && response.containsKey( "isRegularFile" ) && (Boolean) response.get( "isRegularFile" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.FileExplorer();
    }

    @WorkbenchPartView
    public UberView<FileExplorerPresenter> getWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    public void redirect( final Path path ) {
        vfsService.call( new RemoteCallback<Map>() {
            @Override
            public void callback( final Map response ) {
                if ( isRegularFile( response ) ) {
                    placeManager.goTo( path );
                }
            }
        } ).readAttributes( path );
    }

    public void redirectRepositoryList() {
        placeManager.goTo( new DefaultPlaceRequest( "RepositoriesEditor" ) );
    }

    public void redirect( final Repository repo ) {
        placeManager.goTo( new DefaultPlaceRequest( "RepositoryEditor" ).addParameter( "alias", repo.getAlias() ) );
    }

    public void newRootDirectory( @Observes NewRepositoryEvent event ) {
        final Repository repository = event.getNewRepository();
        if ( repository == null ) {
            return;
        }
        if ( repositories.contains( repository ) ) {
            view.removeRepository( repository );
        }
        view.addNewRepository( repository );
        repositories.add( repository );
    }

    public void removeRootDirectory( @Observes RepositoryRemovedEvent event ) {
        final Repository repository = event.getRepository();
        if ( repository == null ) {
            return;
        }
        if ( repositories.contains( repository ) ) {
            view.removeRepository( repository );
            repositories.remove( repository );
        }
    }

    // Refresh when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        refreshView( event.getPath() );
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        refreshView( event.getPath() );
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        refreshView( event.getDestinationPath() );
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        refreshView( event.getDestinationPath() );
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChange( @Observes final ResourceBatchChangesEvent event ) {
        onStartup();
    }

    private void refreshView( final Path path ) {
        final String pathUri = path.toURI();
        for ( Repository repository : repositories ) {
            final String repositoryUri = repository.getRoot().toURI();
            if ( pathUri.startsWith( repositoryUri ) ) {
                onStartup();
                break;
            }
        }
    }

}