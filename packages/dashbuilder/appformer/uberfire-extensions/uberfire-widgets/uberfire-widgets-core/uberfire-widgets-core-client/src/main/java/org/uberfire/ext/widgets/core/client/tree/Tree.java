/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.core.client.tree;

import java.util.Iterator;
import java.util.function.Supplier;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources;

public class Tree<T extends TreeItem> extends Composite implements HasSelectionHandlers<T>,
                                                                   HasOpenHandlers<T>,
                                                                   HasCloseHandlers<T> {

    private FlowPanel container;
    private T curSelection = null;

    public Tree() {
        this(FlowPanel::new);
    }

    Tree(final Supplier<FlowPanel> containerProvider) {
        container = containerProvider.get();
        initWidget(container);
        container.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().tree());
    }

    @Override
    public HandlerRegistration addOpenHandler(final OpenHandler<T> handler) {
        return addHandler(handler,
                          OpenEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(final CloseHandler<T> handler) {
        return addHandler(handler,
                          CloseEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(final SelectionHandler<T> handler) {
        return addHandler(handler,
                          SelectionEvent.getType());
    }

    public void clear() {
        container.clear();
    }

    public void setSelectedItem(final T item,
                                final boolean fireEvents) {
        onSelection(item,
                    fireEvents);
    }

    public T getSelectedItem() {
        return curSelection;
    }

    public void setSelectedItem(final T item) {
        onSelection(item,
                    true);
    }

    @SuppressWarnings("unchecked")
    public T addItem(final T item) {
        container.add(item);
        item.setTree(this);
        return item;
    }

    @SuppressWarnings("unchecked")
    public T getItemByUuid(String uuid) {
        final T item[] = (T[]) new TreeItem[1];
        getItems().forEach(i -> {
            if (item[0] == null) {
                item[0] = (T) i.getItemByUuid(uuid);
            }
        });
        return item[0];
    }

    public T getItem(int index) {
        T item = null;
        Iterator<T> itemIter = getItems().iterator();
        int idx = 0;
        while (itemIter.hasNext()) {
            T treeItem = itemIter.next();
            if (idx == index) {
                item = treeItem;
                break;
            }
            idx++;
        }
        return item;
    }

    public void removeItem(final T item) {
        container.remove(item);
    }

    public Iterable<T> getItems() {
        return () -> new T.TreeItemIterator<T>(container);
    }

    public boolean isEmpty() {
        return container.getWidgetCount() == 0;
    }

    void onSelection(final T item,
                     final boolean fireEvents) {
        if (curSelection != null) {
            curSelection.setSelected(false);
        }
        curSelection = item;
        if (curSelection != null) {
            // Select the item and fire the selection event.
            curSelection.setSelected(true);
            if (fireEvents) {
                SelectionEvent.fire(this,
                                    curSelection);
            }
        }
    }

    void fireStateChanged(final T item,
                          final T.State state) {
        if (state.equals(T.State.OPEN)) {
            OpenEvent.fire(this,
                           item);
        } else {
            CloseEvent.fire(this,
                            item);
        }
        onSelection(item,
                    true);
    }
}