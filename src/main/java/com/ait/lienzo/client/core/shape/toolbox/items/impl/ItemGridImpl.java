/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.toolbox.ItemGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractPrimitiveItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;

public class ItemGridImpl
        extends WrappedItem<ItemGridImpl>
        implements ItemGrid<ItemGridImpl, Point2DGrid, DecoratedItem> {

    private final AbstractGroupItem groupPrimitiveItem;
    private final List<AbstractDecoratedItem> items = new LinkedList<AbstractDecoratedItem>();
    private Point2DGrid grid;
    private Runnable refreshCallback;
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
        this.boundingBox = BoundingBox.fromDoubles(0,
                                           0,
                                           1,
                                           1);
        this.refreshCallback = new Runnable() {
            @Override
            public void run() {
                if (null != groupPrimitiveItem.asPrimitive().getLayer()) {
                    groupPrimitiveItem.asPrimitive().getLayer().batch();
                }
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
    public ItemGridImpl show(final Runnable before,
                             final Runnable after) {
        return super.show(new Runnable() {
                              @Override
                              public void run() {
                                  ItemGridImpl.this.repositionItems();
                                  for (final DecoratedItem button : items) {
                                      button.show();
                                  }
                                  before.run();
                              }
                          },
                          after);
    }

    @Override
    public ItemGridImpl hide(final Runnable before,
                             final Runnable after) {
        return super.hide(before,
                          new Runnable() {
                              @Override
                              public void run() {
                                  for (final DecoratedItem button : items) {
                                      button.hide();
                                  }
                                  after.run();
                                  ItemGridImpl.this.fireRefresh();
                              }
                          });
    }

    public ItemGridImpl onRefresh(final Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
        return this;
    }

    public ItemGridImpl refresh() {
        return checkReposition();
    }

    public int size() {
        return items.size();
    }

    public ItemGridImpl useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ItemGridImpl useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useHideExecutor(executor);
        return this;
    }

    @Override
    public void destroy() {
        for (AbstractDecoratedItem item : items) {
            item.destroy();
        }
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

        boundingBox = BoundingBox.fromDoubles(0,
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
            refreshCallback.run();
        }
    }

    @Override
    AbstractGroupItem<?> getWrapped() {
        return groupPrimitiveItem;
    }

    private abstract static class ListItemsIterator<I extends AbstractDecoratedItem> implements Iterator<DecoratedItem> {

        private final List<I> items;
        private int index;

        private ListItemsIterator(final List<I> items) {
            this.items = new LinkedList<I>(items);
            this.index = 0;
        }

        protected abstract void remove(I item);

        @Override
        public boolean hasNext() {
            return index < items.size();
        }

        @Override
        public I next() {
            return items.get(index++);
        }

        @Override
        public void remove() {
            I item = items.get(index);
            remove(item);
        }
    }
}
