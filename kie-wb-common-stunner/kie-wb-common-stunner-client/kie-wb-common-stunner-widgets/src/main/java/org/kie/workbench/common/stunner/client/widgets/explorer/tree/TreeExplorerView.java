/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

public class TreeExplorerView extends Composite implements TreeExplorer.View {

    private static Logger LOGGER = Logger.getLogger(TreeExplorerView.class.getName());
    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    Tree<TreeItem> tree;

    private TreeExplorer presenter;

    @Override
    public void init(final TreeExplorer presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));

        tree.addSelectionHandler(selectionEvent ->
                                 {
                                     final TreeItem item = selectionEvent.getSelectedItem();
                                     final String uuid = item.getUuid();
                                     final Shape shape = presenter.getCanvasHandler().getCanvas().getShape(uuid);
                                     if (shape != null) {
                                         presenter.onSelect(uuid);
                                     }
                                 });
    }

    @SuppressWarnings("unchecked")
    public TreeExplorer.View addItem(final String uuid,
                                     final String name,
                                     final IsWidget icon,
                                     final TreeItem.Type itemType,
                                     final boolean state) {

        final TreeItem item = buildItem(uuid,
                                        name,
                                        icon,
                                        itemType);
        tree.addItem(item);

        item.setState(getState(state));
        return this;
    }

    @SuppressWarnings("unchecked")
    public TreeExplorer.View removeItem(final int index) {
        final TreeItem item = tree.getItem(index);
        item.removeItems();
        tree.removeItem(item);
        return this;
    }

    @SuppressWarnings("unchecked")
    public TreeExplorer.View removeItem(final int index,
                                        final int... parentsIds) {
        final TreeItem parent = getParent(parentsIds);
        final TreeItem item = parent.getChild(index);
        item.removeItems();
        parent.removeItem(item);
        return this;
    }

    @Override
    public TreeExplorer.View clear() {
        tree.clear();
        return this;
    }

    public TreeExplorer.View addItem(final String uuid,
                                     final String name,
                                     final IsWidget icon,
                                     final TreeItem.Type itemType,
                                     final boolean state,
                                     final int... parentsIds) {

        final TreeItem item = buildItem(uuid,
                                        name,
                                        icon,
                                        itemType);

        final TreeItem parent = getParent(parentsIds);

        parent.addItem(itemType,
                       uuid,
                       name,
                       icon);

        parent.setState(getState(state));
        item.setState(getState(state));

        return this;
    }

    private TreeItem buildItem(final String uuid,
                               final String name,
                               final IsWidget icon,
                               final TreeItem.Type itemType) {
        final TreeItem item = new TreeItem(itemType,
                                           uuid,
                                           name,
                                           icon);
        item.setUserObject(uuid);
        item.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        return item;
    }

    private TreeItem getParent(final int... parentsIds) {
        TreeItem parent = null;
        for (int x = 0; x < parentsIds.length - 1; x++) {
            final int parentIdx = parentsIds[x];
            if (null == parent) {
                parent = tree.getItem(parentIdx);
            } else {
                parent = parent.getChild(parentIdx);
            }
        }
        return parent;
    }

    private TreeItem.State getState(boolean state) {
        if (state) {
            return TreeItem.State.OPEN;
        } else {
            return TreeItem.State.CLOSE;
        }
    }

    interface ViewBinder extends UiBinder<Widget, TreeExplorerView> {

    }
}
