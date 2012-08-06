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
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
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
import org.uberfire.client.common.Util;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.IdentifierUtils;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.CoreImages;
import org.uberfire.client.workbench.Position;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View                            view;

    @Inject
    private Caller<VFSService>              vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager                    placeManager;

    @Inject
    private IdentifierUtils                 idUtils;

    private static final String             REPOSITORY_ID = "repositories";

    public interface View
            extends
            IsWidget {

        TreeItem getRootItem();

        Tree getTree();

        void setFocus();
    }

    private static CoreImages   images    = GWT.create( CoreImages.class );
    private static final String LAZY_LOAD = "Loading...";

    @OnStart
    public void onStart() {
        view.getRootItem().setUserObject( REPOSITORY_ID );
        view.getRootItem().addItem( LAZY_LOAD );

        view.getRootItem().removeItems();

        placeManager.goTo( new PlaceRequest( "RepositoriesEditor" ) );

        rootService.call( new RemoteCallback<Collection<Root>>() {
            @Override
            public void callback(Collection<Root> response) {
                for ( final Root root : response ) {
                    loadRoot( root );
                }
            }
        } ).listRoots();

        view.getTree().addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen(final OpenEvent<TreeItem> event) {
                if ( needsLoading( event.getTarget() ) && event.getTarget().getUserObject() instanceof Path ) {
                    vfsService.call( new RemoteCallback<DirectoryStream<Path>>() {
                        @Override
                        public void callback(DirectoryStream<Path> response) {
                            event.getTarget().getChild( 0 ).remove();
                            for ( final Path path : response ) {
                                vfsService.call( new RemoteCallback<Map>() {
                                    @Override
                                    public void callback(final Map response) {
                                        final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                                        final TreeItem item;
                                        if ( attrs.isDirectory() ) {
                                            item = event.getTarget().addItem( Util.getHeader( images.openedFolder(),
                                                                                              path.getFileName() ) );
                                            item.addItem( LAZY_LOAD );
                                        } else {
                                            item = event.getTarget().addItem( Util.getHeader( images.file(),
                                                                                              path.getFileName() ) );
                                        }
                                        item.setUserObject( path );
                                    }
                                } ).readAttributes( path );
                            }
                        }
                    } ).newDirectoryStream( (Path) event.getTarget().getUserObject() );
                }
            }
        } );

        view.getTree().addSelectionHandler( new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                final Object userObject = event.getSelectedItem().getUserObject();
                if ( userObject != null && userObject instanceof Path ) {
                    final Path path = (Path) userObject;
                    vfsService.call( new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                            if ( attrs.isRegularFile() ) {
                                PlaceRequest placeRequest = getPlace( path );
                                placeManager.goTo( placeRequest );
                            }
                        }
                    } ).readAttributes( path );
                } else if ( event.getSelectedItem().getUserObject() instanceof String && ((String) event.getSelectedItem().getUserObject()).equals( REPOSITORY_ID ) ) {
                    placeManager.goTo( new PlaceRequest( "RepositoriesEditor" ) );
                } else if ( userObject != null && userObject instanceof Root ) {
                    final Root root = (Root) userObject;
                    placeManager.goTo( root.getPlaceRequest() );
                }
            }
        } );
    }

    private void loadRoot(final Root root) {

        //TODO check if it already exists and cleanup

        final TreeItem repositoryRootItem = view.getRootItem().addItem( Util.getHeader( images.packageIcon(),
                                                                                        root.getPath().getFileName() ) );
        repositoryRootItem.setState( true );
        repositoryRootItem.setUserObject( root );

        vfsService.call( new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback(DirectoryStream<Path> response) {
                for ( final Path path : response ) {
                    vfsService.call( new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                            final TreeItem item;
                            if ( attrs.isDirectory() ) {
                                item = repositoryRootItem.addItem( Util.getHeader( images.openedFolder(),
                                                                                   path.getFileName() ) );
                                item.addItem( LAZY_LOAD );
                            } else {
                                item = repositoryRootItem.addItem( Util.getHeader( images.file(),
                                                                                   path.getFileName() ) );
                            }
                            item.setUserObject( path );
                        }
                    } ).readAttributes( path );
                }
            }
        } ).newDirectoryStream( root.getPath() );
    }

    private PlaceRequest getPlace(final Path path) {

        final String fileType = getFileType( path.getFileName() );
        if ( fileType == null ) {
            return defaultPlace( path );
        }

        //Lookup an Activity that can handle the file extension and create a corresponding PlaceRequest.
        //We could simply construct a PlaceRequest for the fileType and leave PlaceManager to determine whether
        //an Activity for the fileType exists however that would place the decision as to what default editor
        //to use within PlaceManager. It is a design decision to let FileExplorer determine the default editor.
        //Consequentially we check for an Activity here and, if none found, define the default editor.
        final Set<IOCBeanDef<Activity>> activityBeans = idUtils.getActivities( fileType );
        if ( activityBeans.size() > 0 ) {
            final PlaceRequest place = new PlaceRequest( fileType );
            place.addParameter( "path:uri",
                                path.toURI() ).addParameter( "path:name",
                                                             path.getFileName() );
            return place;
        }

        //If a specific handler was not found use a TextEditor
        return defaultPlace( path );
    }

    private PlaceRequest defaultPlace(final Path path) {
        PlaceRequest defaultPlace = new PlaceRequest( "TextEditor" );
        defaultPlace.addParameter( "path:uri",
                                   path.toURI() ).addParameter( "path:name",
                                                                path.getFileName() );
        return defaultPlace;
    }

    private String getFileType(final String fileName) {
        final int dotIndex = fileName.indexOf( "." );
        if ( dotIndex >= 0 ) {
            return fileName.substring( dotIndex + 1 );
        }
        return null;
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
    public IsWidget getWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }

    public void newRootDirectory(@Observes Root root) {
        loadRoot( root );
    }

}