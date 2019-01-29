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
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.ext.widgets.core.client.resources.TreeNavigatorResources;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class TreeItem<I extends TreeItem> extends Composite {

    private final Type type;
    protected FlowPanel content;
    private Tree<I> tree;
    private Object userObject;
    private I parentItem;
    private State state;
    private String label;
    private String uuid;
    private boolean isSelected = false;
    private FlowPanel header;
    private IsWidget icon;
    private FlowPanel item;

    public TreeItem(final Type type,
                    final String value,
                    final String label,
                    final IsWidget icon) {
        this(type,
             value,
             label,
             icon,
             FlowPanel::new);
    }

    TreeItem(final Type type,
             final String value,
             final String label,
             final IsWidget icon,
             final Supplier<FlowPanel> contentProvider
    ) {
        this.label = label;
        this.uuid = value;
        this.type = checkNotNull("type",
                                 type);

        if (type.equals(Type.CONTAINER) || type.equals(Type.ROOT)) {
            final FlowPanel folder = contentProvider.get();
            folder.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeFolder());
            folder.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            {
                this.state = State.CLOSE;
                this.header = GWT.create(FlowPanel.class);
                this.icon = icon;
                this.content = contentProvider.get();
                final Anchor name = new Anchor();
                {
                    header.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeFolderHeader());
                    folder.add(header);
                    {
                        header.add(icon);
                    }
                    final FlowPanel folderName = new FlowPanel();
                    {
                        folderName.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeFolderName());
                        header.add(folderName);
                        {
                            name.setText(label);
                            name.setTitle(value);
                            folderName.add(name);
                        }
                    }
                    header.addDomHandler(new ClickHandler() {
                                             @Override
                                             public void onClick(ClickEvent event) {
                                                 if (!isSelected) {
                                                     updateSelected();
                                                 }
                                                 if (state.equals(State.CLOSE)) {
                                                     setState(State.OPEN,
                                                              true);
                                                 } else {
                                                     setState(State.CLOSE,
                                                              true);
                                                 }
                                             }
                                         },
                                         ClickEvent.getType());
                }
                {
                    content.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeFolderContent());
                    content.getElement().getStyle().setDisplay(Style.Display.NONE);
                    folder.add(content);
                }
                initWidget(folder);
            }
        } else if (type.equals(Type.ITEM)) {
            this.state = State.NONE;
            this.item = contentProvider.get();
            item.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeItem());
            {
                this.icon = icon;
                final FlowPanel itemName = new FlowPanel();
                final Anchor name = new Anchor();
                {
                    item.add(icon);
                }
                {
                    itemName.setStylePrimaryName(TreeNavigatorResources.INSTANCE.css().treeItemName());
                    item.add(itemName);
                    {
                        name.setText(label);
                        name.setTitle(value);

                        itemName.add(name);
                    }
                }
                item.addDomHandler(new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           tree.onSelection((I) TreeItem.this,
                                                            true);
                                       }
                                   },
                                   ClickEvent.getType());
            }
            initWidget(item);
        } else {
            final FlowPanel loader = new FlowPanel();
            {
                final SimplePanel loading = new SimplePanel();
                loading.getElement().setInnerText(value);
                loader.add(loading);
            }
            initWidget(loader);
        }
    }

    @SuppressWarnings("unchecked")
    public I getItemByUuid(final String uuid) {
        if (getUuid().equals(uuid)) {
            return (I) this;
        }
        final I[] selectedItem = (I[]) new TreeItem[1];
        getChildren().forEach(c -> {
            if (selectedItem[0] == null) {
                selectedItem[0] = (I) c.getItemByUuid(uuid);
            }
        });
        return selectedItem[0];
    }

    private void updateSelected() {
        tree.onSelection((I) this,
                         true);
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        setState(state,
                 false,
                 true);
    }

    public void setState(final State state,
                         boolean fireEvents) {
        setState(state,
                 false,
                 fireEvents);
    }

    public void setState(final State state,
                         boolean propagateParent,
                         boolean fireEvents) {
        if (notFolder()) {
            return;
        }
        if (!this.state.equals(state)) {
            this.state = state;
            updateState(state);
            if (fireEvents && tree != null) {
                tree.fireStateChanged((I) this,
                                      state);
            }
        }
        if (propagateParent && parentItem != null) {
            parentItem.setState(state,
                                true,
                                false);
        }
    }

    private boolean notFolder() {
        return !type.equals(Type.CONTAINER);
    }

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }

    public Type getType() {
        return this.type;
    }

    public I addItem(final I item) {
        return addChild(item, t -> content.add(t));
    }

    public I insertItem(final I item, final int index) {
        return addChild(item, t -> content.insert(t, index));
    }

    public I addItem(final Type type,
                     final String value,
                     final String label,
                     final IsWidget icon) {
        return addChild(type,
                        value,
                        label,
                        icon,
                        this::addItem);
    }

    public I insertItem(final Type type,
                        final String value,
                        final String label,
                        final IsWidget icon,
                        final int index) {
        return addChild(type,
                        value,
                        label,
                        icon,
                        t -> insertItem(t, index));
    }

    @SuppressWarnings("unchecked")
    private I addChild(final I item,
                       final Consumer<I> addItemFunction) {
        checkContainerType();
        addItemFunction.accept(item);
        item.setTree(tree);
        item.setParentItem(this);
        return item;
    }

    private void checkContainerType() {
        if (null == content) {
            throw new IllegalStateException("This tree item instance is not a container.");
        }
    }

    @SuppressWarnings("unchecked")
    private I addChild(final Type type,
                       final String value,
                       final String label,
                       final IsWidget icon,
                       final Consumer<I> addItemFunction) {
        final I child = makeChild(type,
                                  value,
                                  label,
                                  icon);
        addItemFunction.accept(child);
        return child;
    }

    @SuppressWarnings("unchecked")
    private I makeChild(final Type type,
                        final String value,
                        final String label,
                        final IsWidget icon) {
        return (I) new TreeItem(type,
                                value,
                                label,
                                icon);
    }

    public void removeItems() {
        checkContainerType();
        content.clear();
    }

    public int getChildCount() {
        return null != content ? content.getWidgetCount() : 0;
    }

    @SuppressWarnings("unchecked")
    public I getChild(final int i) {
        checkContainerType();
        if (i + 1 > content.getWidgetCount()) {
            return null;
        }
        return (I) content.getWidget(i);
    }

    public Iterable<I> getChildren() {
        return () -> new TreeItemIterator<I>(content);
    }

    void setTree(final Tree<I> tree) {
        this.tree = tree;
    }

    void updateState(final State state) {
        // If the tree hasn't been set, there is no visual state to update.
        // If the tree is not attached, then update will be called on attach.
        if (tree == null) {
            return;
        }
        switch (state) {
            case OPEN:
                onOpenState();
                break;
            case CLOSE:
                onCloseState();
        }
    }

    protected void onOpenState() {
        content.getElement().getStyle().setDisplay(Style.Display.BLOCK);
    }

    protected void onCloseState() {
        content.getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    @SuppressWarnings("unchecked")
    public void remove() {
        if (parentItem != null) {
            // If this item has a parent, remove self from it.
            parentItem.removeItem(this);
        } else if (tree != null) {
            // If the item has no parent, but is in the Tree, it must be a top-level
            // element.
            tree.removeItem((I) this);
        }
    }

    public void removeItem(final I treeItem) {
        checkContainerType();
        content.remove(treeItem);
    }

    public String getText() {
        return getElement().getInnerText();
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        isSelected = selected;
        if (selected) {
            if (header != null) {
                header.addStyleName(TreeNavigatorResources.INSTANCE.css().treeSelected());
            } else {
                item.addStyleName(TreeNavigatorResources.INSTANCE.css().treeSelected());
            }
        } else {
            if (header != null) {
                header.removeStyleName(TreeNavigatorResources.INSTANCE.css().treeSelected());
            } else {
                item.removeStyleName(TreeNavigatorResources.INSTANCE.css().treeSelected());
            }
        }
    }

    public TreeItem getParentItem() {
        return parentItem;
    }

    void setParentItem(final I parent) {
        this.parentItem = parent;
    }

    public boolean isEmpty() {
        return null == content || content.getWidgetCount() == 0;
    }

    IsWidget getIconWidget() {
        return icon;
    }

    FlowPanel getContent() {
        return content;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TreeItem)) {
            return false;
        }
        TreeItem that = (TreeItem) other;
        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public enum Type {
        ROOT,
        CONTAINER,
        ITEM,
    }

    public enum State {
        NONE,
        OPEN,
        CLOSE
    }

    protected static class TreeItemIterator<T> implements Iterator<T> {

        private final ComplexPanel container;
        private int index = 0;

        TreeItemIterator(ComplexPanel container) {
            this.container = container;
        }

        @Override
        public boolean hasNext() {
            if (container == null) {
                return false;
            }
            return index < container.getWidgetCount();
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            return (T) container.getWidget(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}