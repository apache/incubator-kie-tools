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

package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.resources.ShowcaseImages;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.SimplePath;
import org.drools.guvnor.vfs.VFSService;
import org.drools.java.nio.file.DirectoryStream;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

@Dependent
public class FileExplorerPresenter implements ScreenService {

    @Inject View view;
    @Inject Caller<VFSService> vfsService;
    @Inject private PlaceManager placeManager;
    private static ShowcaseImages images = GWT.create(ShowcaseImages.class);
    private static final String LAZY_LOAD = "Loading...";

    @Override
    public void onStart() {
        vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback(DirectoryStream<Path> response) {
                for (final Path path : response) {
                    final TreeItem item;
                    if (path.isDirectory()) {
                        item = view.getRootItem().addItem(Util.getHeader(images.openedFolder(), path.getFileName()));
                        item.addItem(LAZY_LOAD);
                    } else {
                        item = view.getRootItem().addItem(Util.getHeader(images.file(), path.getFileName()));
                    }
                    item.setUserObject(path);
                }
            }
        }).newDirectoryStream();

        view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
            @Override public void onOpen(final OpenEvent<TreeItem> event) {
                if (needsLoading(event.getTarget())) {
                    vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
                        @Override
                        public void callback(DirectoryStream<Path> response) {
                            event.getTarget().getChild(0).remove();
                            for (final Path path : response) {
                                final TreeItem item;
                                if (path.isDirectory()) {
                                    item = event.getTarget().addItem(Util.getHeader(images.openedFolder(), path.getFileName()));
                                    item.addItem(LAZY_LOAD);
                                } else {
                                    item = event.getTarget().addItem(Util.getHeader(images.file(), path.getFileName()));
                                }
                                item.setUserObject(path);
                            }
                        }
                    }).newDirectoryStream((SimplePath) event.getTarget().getUserObject());
                }
            }
        });

        view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                final Path path = (Path) event.getSelectedItem().getUserObject();
                if (path.isFile()) {
                    //TODO {manstis} This needs to be made more generic. When a regularFile is selected we don't know
                    //what editor is needed. This should delegate to a mechanism to load an Asset from the backend. This
                    //service fronts the VFS and will create an AbstractAsset based upon the file extension (possibly populate
                    //AssetData and AssetMetaData) and return that. If we annotate Places with the file extension they support
                    //we can then identify and instantiate the correct Place for the AbstractAsset. The Place can also take a
                    //parameter that is the concrete AbstractAsset (e.g. FactModels or RuleContentText). Any unrecognised
                    //file extension can be wrapped into a plain-text asset and TextEditor used.
                    PlaceRequest placeRequest = new PlaceRequest("TextEditor");
                    placeRequest.parameter("path", path.toURI());
                    placeManager.goTo(placeRequest);
                }
            }
        });
    }

    public interface View extends IsWidget {

        TreeItem getRootItem();

        Tree getTree();
    }

    @Override public void onClose() {
    }

    @Override public boolean mayClose() {
        return true;
    }

    @Override
    public void onReveal() {
        //view.setFocus();
    }

    @Override public void onHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void mayOnHide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals(item.getChild(0).getText());
    }
}