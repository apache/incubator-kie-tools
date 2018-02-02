/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.FSTreeItem;
import org.uberfire.ext.widgets.core.client.tree.Tree;

public class FileExplorerViewImpl
        extends Composite
        implements FileExplorerView {

    private static final String REPOSITORY_ID = "repositories";
    private final Map<Repository, FSTreeItem> repositoryToTreeItemMap = new HashMap<Repository, FSTreeItem>();
    private final FlowPanel panel = GWT.create(FlowPanel.class);
    private final Tree<FSTreeItem> tree;
    FSTreeItem rootTreeItem = null;
    private CommonConstants constants = CommonConstants.INSTANCE;
    private FileExplorerPresenter presenter = null;

    public FileExplorerViewImpl() {
        this.tree = GWT.create(Tree.class);
    }

    FileExplorerViewImpl(final Tree<FSTreeItem> tree) {
        this.tree = tree;
    }

    public void init(final FileExplorerPresenter presenter) {
        this.presenter = presenter;
        FSTreeItem item = new FSTreeItem(FSTreeItem.FSType.FOLDER,
                                         constants.Repositories());
        rootTreeItem = tree.addItem(item);
        rootTreeItem.setState(FSTreeItem.State.OPEN);

        panel.getElement().getStyle().setFloat(Style.Float.LEFT);
        panel.getElement().getStyle().setWidth(100,
                                               Style.Unit.PCT);
        panel.add(tree);
        initWidget(panel);

        tree.addOpenHandler(new OpenHandler<FSTreeItem>() {
            @Override
            public void onOpen(final OpenEvent<FSTreeItem> event) {
                if (needsLoading(event.getTarget())) {
                    presenter.loadDirectoryContent(new FileExplorerItem(event.getTarget()),
                                                   (Path) event.getTarget().getUserObject());
                }
            }
        });

        tree.addSelectionHandler(new SelectionHandler<FSTreeItem>() {
            @Override
            public void onSelection(SelectionEvent<FSTreeItem> event) {
                final Object userObject = event.getSelectedItem().getUserObject();
                if (userObject != null && userObject instanceof Path) {
                    final Path path = (Path) userObject;
                    presenter.redirect(path);
                } else if (userObject != null && userObject instanceof Repository) {
                    final Repository root = (Repository) userObject;
                    presenter.redirect(root);
                } else if (event.getSelectedItem().getUserObject() instanceof String &&
                        (event.getSelectedItem().getUserObject()).equals(REPOSITORY_ID)) {
                    presenter.redirectRepositoryList();
                }
            }
        });
    }

    @Override
    public void reset() {
        rootTreeItem.setUserObject(REPOSITORY_ID);
        rootTreeItem.addItem(FSTreeItem.FSType.LOADING,
                             constants.Loading());
        rootTreeItem.removeItems();
        repositoryToTreeItemMap.clear();
    }

    @Override
    public void removeRepository(final Repository repo) {
        if (!repositoryToTreeItemMap.containsKey(repo)) {
            return;
        }
        final FSTreeItem repositoryRootItem = repositoryToTreeItemMap.remove(repo);
        repositoryRootItem.remove();
    }

    @Override
    public void addNewRepository(final Repository repository,
                                 final String branchName) {
        final FSTreeItem repositoryRootItem = rootTreeItem.addItem(FSTreeItem.FSType.FOLDER,
                                                                   repository.getAlias());
        repositoryRootItem.setUserObject(repository);
        repositoryRootItem.setState(FSTreeItem.State.OPEN,
                                    false,
                                    false);

        repositoryToTreeItemMap.put(repository,
                                    repositoryRootItem);

        repository.getBranch(branchName).ifPresent(branch -> presenter.loadDirectoryContent(new FileExplorerItem(repositoryRootItem),
                                                                                            branch.getPath()));
    }

    boolean needsLoading(final FSTreeItem item) {
        return item.getUserObject() instanceof Path
                && item.getFSType() == FSTreeItem.FSType.FOLDER
                && item.getChildCount() == 1
                && constants.Loading().equals(item.getChild(0).getText());
    }
}