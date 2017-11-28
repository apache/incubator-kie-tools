/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.gallery;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class GalleryTreeViewImpl extends Composite implements GalleryTreePresenter.GalleryTreeView {

    GalleryTreePresenter presenter;

    @Inject
    GalleryTree galleryTree;

    private final SimplePanel mainPanel = new SimplePanel();

    public void init(GalleryTreePresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    private void initUI() {
        initWidget(mainPanel);

        Tree tree = initNavigationTree();
        tree.setWidth("150px");
        Style leftStyle = mainPanel.getElement().getStyle();
        leftStyle.setPropertyPx("margin", 5);
        mainPanel.add(tree);
    }

    private Tree initNavigationTree() {
        Tree navTree = new Tree();

        List<GalleryTreeNode> mainNodes = galleryTree.getMainNodes();
        populateNavigationTree(mainNodes, navTree);

        navTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem ti = event.getSelectedItem();
                GalleryTreeNode node = (GalleryTreeNode) ti.getUserObject();
                treeItemClicked(ti, node);
            }
        });
        return navTree;
    }

    private void populateNavigationTree(List<GalleryTreeNode> nodes, HasTreeItems items) {
        if (nodes == null) return;
        for (GalleryTreeNode node: nodes) {
            TreeItem ti = new TreeItem();
            ti.setText(node.getName());
            ti.setUserObject(node);
            ti.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            items.addItem(ti);
            populateNavigationTree(node.getChildren(), ti);
        }
    }

    private void treeItemClicked(TreeItem ti, GalleryTreeNode node) {
        presenter.navigateToNode(node);
    }
}
