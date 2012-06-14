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

package org.drools.guvnor.client.editors.fileexplorer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.IPlaceRequestFactory;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.mvp.SupportedFormat;
import org.drools.guvnor.client.resources.ShowcaseImages;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.drools.guvnor.vfs.VFSTempUtil;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

@Dependent
public class FileExplorerPresenter
    implements
    ScreenService {

    @Inject
    View                          view;

    @Inject
    Caller<VFSService>            vfsService;

    @Inject
    private PlaceManager          placeManager;

    @Inject
    IOCBeanManager                iocManager;

    private static ShowcaseImages images    = GWT.create( ShowcaseImages.class );
    private static final String   LAZY_LOAD = "Loading...";

    @Override
    public void onStart() {
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
                                item = view.getRootItem().addItem( Util.getHeader( images.openedFolder(),
                                                                                   path.getFileName() ) );
                                item.addItem( LAZY_LOAD );
                            } else {
                                item = view.getRootItem().addItem( Util.getHeader( images.file(),
                                                                                   path.getFileName() ) );
                            }
                            item.setUserObject( path );
                        }
                    } ).readAttributes( path );
                }
            }
        } ).newDirectoryStream();

        view.getTree().addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen(final OpenEvent<TreeItem> event) {
                if ( needsLoading( event.getTarget() ) ) {
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
                final Path path = (Path) event.getSelectedItem().getUserObject();
                vfsService.call( new RemoteCallback<Map>() {
                    @Override
                    public void callback(final Map response) {
                        final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                        if ( attrs.isRegularFile() ) {
                            IPlaceRequest placeRequest = getPlace( path );
                            placeManager.goTo( placeRequest );
                        }
                    }
                } ).readAttributes( path );
            }
        } );
    }

    private IPlaceRequest getPlace(final Path path) {
        final String fileType = getFileType( path.getFileName() );

        //Lookup all Places and check if one handles the file extension
        Collection<IOCBeanDef> factories = iocManager.lookupBeans( IPlaceRequestFactory.class );
        for ( IOCBeanDef factory : factories ) {
            Set<Annotation> annotations = factory.getQualifiers();
            for ( Annotation a : annotations ) {
                if ( a instanceof SupportedFormat ) {
                    SupportedFormat format = (SupportedFormat) a;
                    if ( format.value().equalsIgnoreCase( fileType ) ) {
                        final IPlaceRequestFactory instance = (IPlaceRequestFactory) factory.getInstance();
                        final IPlaceRequest place = instance.makePlace( path );
                        return place;
                    }
                }
            }
        }

        PlaceRequest placeRequest = new PlaceRequest( "TextEditor" );
        placeRequest.addParameter( "path",
                                   path.toURI() );
        return placeRequest;
    }

    private String getFileType(final String fileName) {
        final int dotIndex = fileName.indexOf( "." );
        if ( dotIndex >= 0 ) {
            return fileName.substring( dotIndex + 1 );
        }
        return fileName;
    }

    public interface View
        extends
        IsWidget {

        TreeItem getRootItem();

        Tree getTree();
    }

    @Override
    public void onClose() {
    }

    @Override
    public boolean mayClose() {
        return true;
    }

    @Override
    public void onReveal() {
        //view.setFocus();
    }

    @Override
    public void onHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mayOnHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }
}
