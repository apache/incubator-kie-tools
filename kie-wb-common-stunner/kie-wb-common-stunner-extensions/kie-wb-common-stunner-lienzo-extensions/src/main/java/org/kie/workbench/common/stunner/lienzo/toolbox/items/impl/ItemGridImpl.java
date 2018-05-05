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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.stunner.lienzo.toolbox.Item;
import org.kie.workbench.common.stunner.lienzo.toolbox.ItemGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.Point2DGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratedItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractPrimitiveItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratedItem;
import org.uberfire.mvp.Command;

public class ItemGridImpl
        extends WrappedItem<ItemGridImpl>
        implements ItemGrid<ItemGridImpl, Point2DGrid, DecoratedItem> {

    private final AbstractGroupItem groupPrimitiveItem;
    private final List<AbstractDecoratedItem> items = new LinkedList<>();
    private Point2DGrid grid;
    private Command refreshCallback;
    private BoundingBox boundingBox;

    private Supplier<BoundingBox> boundingBoxSupplier = new Supplier<BoundingBox>() {
        @Override
        public BoundingBox get() {
            return boundingBox;
        }
    };

    public ItemGridImpl() {
        this(new GroupImpl(new Group()));
    }

    ItemGridImpl(final AbstractGroupItem groupPrimitiveItem) {
        this.groupPrimitiveItem =
                groupPrimitiveItem
                        .setBoundingBox(boundingBoxSupplier);
        this.boundingBox = new BoundingBox(0,
                                           0,
                                           1,
                                           1);
        this.refreshCallback = () -> {
            if (null != groupPrimitiveItem.asPrimitive().getLayer()) {
                groupPrimitiveItem.asPrimitive().getLayer().batch();
            }
        };
    }

    @Override
    public ItemGridImpl grid(final Point2DGrid grid) {
        this.grid = grid;
        return checkReposition();
    }

    @Override
    public ItemGridImpl add(final DecoratedItem... items) {
        for (final DecoratedItem item : items) {
            try {
                addItem((AbstractDecoratedItem) item);
            } catch (final ClassCastException e) {
                throw new UnsupportedOperationException("This item only supports subtypes " +
                                                                "of " + AbstractDecoratedItem.class.getName());
            }
        }
        return itemsUpdated();
    }

    @Override
    public Iterator<DecoratedItem> iterator() {
        return new ListItemsIterator<AbstractDecoratedItem>(items) {
            @Override
            protected void remove(final AbstractDecoratedItem item) {
                items.remove(item);
                itemsUpdated();
            }
        };
    }

    @Override
    public ItemGridImpl show(final Command before,
                             final Command after) {
        return super.show(() -> {
                              repositionItems();
                              for (final DecoratedItem button : items) {
                                  button.show();
                              }
                              before.execute();
                          },
                          after);
    }

    @Override
    public ItemGridImpl hide(final Command before,
                             final Command after) {
        return super.hide(before,
                          () -> {
                              for (final DecoratedItem button : items) {
                                  button.hide();
                              }
                              after.execute();
                              fireRefresh();
                          });
    }

    public ItemGridImpl onRefresh(final Command refreshCallback) {
        this.refreshCallback = refreshCallback;
        return this;
    }

    public ItemGridImpl refresh() {
        return checkReposition();
    }

    public int size() {
        return items.size();
    }

    public ItemGridImpl useShowExecutor(final BiConsumer<Group, Command> executor) {
        this.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ItemGridImpl useHideExecutor(final BiConsumer<Group, Command> executor) {
        this.getWrapped().useHideExecutor(executor);
        return this;
    }

    @Override
    public void destroy() {
        items.forEach(Item::destroy);
        items.clear();
        getWrapped().destroy();
        grid = null;
        refreshCallback = null;
        boundingBoxSupplier = null;
        boundingBox = null;
    }

    public Point2DGrid getGrid() {
        return grid;
    }

    void addItem(final AbstractDecoratedItem button) {
        this.items.add(button);
        if (isVisible()) {
            button.show();
        } else {
            button.hide();
        }
        getWrapped().getGroupItem().add(button.asPrimitive());
    }

    private ItemGridImpl itemsUpdated() {
        // Reposition items as for the given grid.
        repositionItems();
        // Calculate BB.
        double maxw = 0;
        double maxh = 0;
        for (final AbstractDecoratedItem item : items) {
            final Point2D location = item.asPrimitive().getLocation();
            final BoundingBox itemBB = (BoundingBox) item.getBoundingBox().get();
            final double itemw = itemBB.getWidth() + location.getX();
            final double itemh = itemBB.getHeight() + location.getY();
            if (itemw > maxw) {
                maxw = itemw;
            }
            if (itemh > maxh) {
                maxh = itemh;
            }
        }

        boundingBox = new BoundingBox(0,
                                      0,
                                      maxw,
                                      maxh);
        // Update decorator.
        if (null != getWrapped().getDecorator()) {
            getWrapped().getDecorator().setBoundingBox(getBoundingBox().get());
            if (getWrapped().getDecorator() instanceof AbstractPrimitiveItem) {
                ((AbstractPrimitiveItem) getWrapped().getDecorator()).asPrimitive().moveToBottom();
            }
        }
        return this;
    }

    @Override
    public Supplier<BoundingBox> getBoundingBox() {
        return boundingBoxSupplier;
    }

    private ItemGridImpl checkReposition() {
        if (isVisible()) {
            return repositionItems();
        }
        return this;
    }

    private ItemGridImpl repositionItems() {
        final Iterator<Point2D> gridIterator = grid.iterator();
        for (final AbstractDecoratedItem button : items) {
            final Point2D point = gridIterator.next();
            button.asPrimitive().setLocation(point);
        }
        fireRefresh();
        return this;
    }

    private void fireRefresh() {
        if (null != refreshCallback) {
            refreshCallback.execute();
        }
    }

    @Override
    protected AbstractGroupItem<?> getWrapped() {
        return groupPrimitiveItem;
    }

    private abstract static class ListItemsIterator<I extends AbstractDecoratedItem> implements Iterator<DecoratedItem> {

        private final List<I> items;
        private int index;

        private ListItemsIterator(final List<I> items) {
            this.items = new LinkedList<>(items);
            this.index = 0;
        }

        protected abstract void remove(I item);

        @Override
        public boolean hasNext() {
            return index < items.size();
        }

        @Override
        public I next() {
            return items.get(index++ - 1);
        }

        @Override
        public void remove() {
            I item = items.get(index);
            remove(item);
        }
    }
}
