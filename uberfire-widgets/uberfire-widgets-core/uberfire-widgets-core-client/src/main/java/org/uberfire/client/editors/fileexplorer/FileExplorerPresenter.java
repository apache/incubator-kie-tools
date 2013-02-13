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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceCopiedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceDeletedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceRenamedEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.VFSTempUtil;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.Position;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View view;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private Event<PathChangeEvent> pathChangedEvent;

    @Inject
    private PlaceManager placeManager;

    public interface View
            extends
            UberView<FileExplorerPresenter> {

        void setFocus();

        void reset();

        void removeIfExists( final Root root );

        void addNewRoot( final Root root );
    }

    public static interface FileExplorerItem {

        void addDirectory( final Path child );

        void addFile( final Path child );
    }

    @PostConstruct
    public void assertActivePath() {
        //When first launched no Path has been selected. Ensure remainder of Workbench knows.
        broadcastPathChange( null );
    }

    @OnStart
    public void onStart() {

        view.reset();

        rootService.call( new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback( Collection<Root> response ) {
                for ( final Root root : response ) {
                    view.removeIfExists( root );
                    view.addNewRoot( root );
                }
            }
        } ).listRoots();
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
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                            if ( attrs.isDirectory() ) {
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

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "File Explorer";
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
                final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                if ( attrs.isRegularFile() ) {
                    placeManager.goTo( path );
                }
                broadcastPathChange( path );
            }
        } ).readAttributes( path );
    }

    public void redirectRepositoryList() {
        placeManager.goTo( new DefaultPlaceRequest( "RepositoriesEditor" ) );
        broadcastPathChange( null );
    }

    public void redirect( Root root ) {
        placeManager.goTo( root.getPlaceRequest() );
        broadcastPathChange( root.getPath() );
    }

    public void newRootDirectory( @Observes Root root ) {
        view.removeIfExists( root );
        view.addNewRoot( root );
    }

    //Communicate change in context
    private void broadcastPathChange( final Path path ) {
        pathChangedEvent.fire( new PathChangeEvent( path ) );
    }

    // Refresh when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        onStart();
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        onStart();
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        onStart();
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        onStart();
    }

}