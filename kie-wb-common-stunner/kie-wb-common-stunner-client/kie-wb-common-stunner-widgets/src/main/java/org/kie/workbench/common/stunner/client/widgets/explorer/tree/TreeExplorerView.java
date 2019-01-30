/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import java.util.function.BiPredicate;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

@Dependent
public class TreeExplorerView extends Composite implements TreeExplorer.View {

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    Tree<TreeItem> tree;

    private TreeExplorer presenter;
    private HandlerRegistration handlerRegistration;

    @Inject
    public TreeExplorerView(final Tree<TreeItem> tree) {
        this.tree = tree;
    }

    TreeExplorerView(final TreeExplorer presenter,
                     final ViewBinder uiBinder,
                     final Tree<TreeItem> tree,
                     final HandlerRegistration handlerRegistration
    ) {
        this.presenter = presenter;
        TreeExplorerView.uiBinder = uiBinder;
        this.tree = tree;
        this.handlerRegistration = handlerRegistration;
    }

    @Override
    public void init(final TreeExplorer presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));

        handlerRegistration = tree.addSelectionHandler(selectionEvent ->
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
                                     final boolean isContainer,
                                     final boolean state) {
        checkNotExist(uuid);
        final TreeItem.Type itemType = isContainer ? TreeItem.Type.CONTAINER : TreeItem.Type.ITEM;
        final TreeItem item = buildItem(uuid,
                                        name,
                                        icon,
                                        itemType);
        tree.addItem(item);

        item.setState(getState(state));
        return this;
    }

    public TreeExplorer.View addItem(final String uuid,
                                     final String parentsUuid,
                                     final String name,
                                     final IsWidget icon,
                                     final boolean isContainer,
                                     final boolean state) {
        checkNotExist(uuid);
        final TreeItem.Type itemType = isContainer ? TreeItem.Type.CONTAINER : TreeItem.Type.ITEM;
        final TreeItem item = buildItem(uuid,
                                        name,
                                        icon,
                                        itemType);
        final TreeItem parent = tree.getItemByUuid(parentsUuid);
        parent.addItem(itemType,
                       uuid,
                       name,
                       icon);
        parent.setState(getState(state));
        item.setState(getState(state));
        return this;
    }

    public boolean isItemChanged(final String uuid,
                                 final String parentUuid,
                                 final String name) {

        final TreeItem oldItem = tree.getItemByUuid(uuid);
        if (oldItem != null) {
            if (isNameChanged().test(oldItem,
                                     name)) {
                return true;
            }
            final TreeItem oldItemParent = oldItem.getParentItem();
            final String oldParentUuid = null != oldItemParent ? oldItemParent.getUuid() : null;
            return ((oldParentUuid == null && parentUuid == null) ||
                    (null != parentUuid && !parentUuid.equals(oldParentUuid)));
        }
        return false;
    }

    @Override
    public TreeExplorer.View clear() {
        tree.clear();
        return this;
    }

    public TreeExplorer.View destroy() {
        handlerRegistration.removeHandler();
        tree.clear();
        tree.removeFromParent();
        handlerRegistration = null;
        presenter = null;
        return this;
    }

    @Override
    public boolean isContainer(final String uuid) {
        final TreeItem oldItem = tree.getItemByUuid(uuid);
        return oldItem.getType().equals(TreeItem.Type.CONTAINER) || oldItem.getType().equals(TreeItem.Type.ROOT);
    }

    public TreeExplorer.View setSelectedItem(final String uuid) {
        final TreeItem selectedItem = tree.getItemByUuid(uuid);
        tree.setSelectedItem(selectedItem,
                             false);
        return this;
    }

    public TreeExplorer.View removeItem(String uuid) {
        final TreeItem item = tree.getItemByUuid(uuid);
        if (item != null) {
            item.remove();
        }
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

    private TreeItem.State getState(boolean state) {
        if (state) {
            return TreeItem.State.OPEN;
        } else {
            return TreeItem.State.CLOSE;
        }
    }

    private void checkNotExist(final String uuid) {
        if (null != tree.getItemByUuid(uuid)) {
            throw new RuntimeException("Trying to adding twice the tree item for element [" + uuid + "]");
        }
    }

    private BiPredicate<TreeItem, String> isNameChanged() {
        return (item, name) -> !item.getLabel().equals(name);
    }

    interface ViewBinder extends UiBinder<Widget, TreeExplorerView> {

    }
}
