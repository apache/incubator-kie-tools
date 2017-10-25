/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.advnavigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.guvnor.structure.navigator.DataContent;
import org.guvnor.structure.navigator.FileNavigatorService;
import org.guvnor.structure.navigator.NavigatorContent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.FSTreeItem;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Named("TreeNav")
public class TreeNavigator extends Composite implements Navigator {

    private static final String LAZY_LOAD = "Loading...";

    @Inject
    private DotResourceTypeDefinition hiddenTypeDef;

    @Inject
    private Caller<FileNavigatorService> navigatorService;

    private NavigatorOptions options = NavigatorOptions.DEFAULT;

    private final Tree tree = new Tree();
    private ParameterizedCommand<Path> fileActionCommand = null;

    @PostConstruct
    public void init() {
        tree.addStyleName(NavigatorResources.INSTANCE.css().treeNav());
        initWidget(tree);

        tree.addOpenHandler(new OpenHandler<FSTreeItem>() {
            @Override
            public void onOpen(final OpenEvent<FSTreeItem> event) {
                if (needsLoading(event.getTarget()) && event.getTarget().getUserObject() instanceof Path) {
                    loadContent(new TreeNavigatorItemImpl(event.getTarget()),
                                (Path) event.getTarget().getUserObject());
                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<FSTreeItem>() {
            @Override
            public void onSelection(SelectionEvent<FSTreeItem> event) {
                if (fileActionCommand != null) {
                    final Object userObject = event.getSelectedItem().getUserObject();
                    if (userObject != null && userObject instanceof Path) {
                        fileActionCommand.execute((Path) userObject);
                    }
                }
            }
        });
    }

    private void loadContent(final NavigatorItem parent,
                             final Path path) {
        if (path != null) {
            navigatorService.call(new RemoteCallback<NavigatorContent>() {
                @Override
                public void callback(final NavigatorContent response) {
                    for (final DataContent dataContent : response.getContent()) {
                        if (dataContent.isDirectory()) {
                            if (options.showDirectories()) {
                                parent.addDirectory(dataContent.getPath());
                            }
                        } else {
                            if (options.showFiles()) {
                                if (!options.showHiddenFiles() && !hiddenTypeDef.accept(dataContent.getPath())) {
                                    parent.addFile(dataContent.getPath());
                                } else if (options.showHiddenFiles()) {
                                    parent.addFile(dataContent.getPath());
                                }
                            }
                        }
                    }
                }
            }).listContent(path);
        }
    }

    @Override
    public void loadContent(final Path path) {
        final NavigatorItem parent = new TreeNavigatorItemImpl(new FSTreeItem(FSTreeItem.FSType.FOLDER,
                                                                              path.getFileName()));
        tree.addItem(((TreeNavigatorItemImpl) parent).parent);

        loadContent(parent,
                    path);
    }

    private boolean needsLoading(final FSTreeItem item) {
        return item.getChildCount() == 1 && LAZY_LOAD.equals(item.getChild(0).getText());
    }

    private class TreeNavigatorItemImpl implements NavigatorItem {

        private final FSTreeItem parent;

        TreeNavigatorItemImpl(final FSTreeItem treeItem) {
            this.parent = checkNotNull("parent",
                                       treeItem);
        }

        public void addDirectory(final Path child) {
            checkCleanupLoading();
            final FSTreeItem newDirectory = parent.addItem(FSTreeItem.FSType.FOLDER,
                                                           child.getFileName());
            newDirectory.addItem(FSTreeItem.FSType.LOADING,
                                 LAZY_LOAD);
            newDirectory.setUserObject(child);
        }

        public void addFile(final Path child) {
            checkCleanupLoading();
            final FSTreeItem newFile = parent.addItem(FSTreeItem.FSType.ITEM,
                                                      child.getFileName());
            newFile.setUserObject(child);
        }

        private void checkCleanupLoading() {
            if (parent.getChild(0) != null && parent.getChild(0).getUserObject() == null) {
                parent.getChild(0).remove();
            }
        }
    }
}