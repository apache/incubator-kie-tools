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
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.toolbox.Positions;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.SizeConstrainedGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ItemsToolbox;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;

/**
 * An ItemsToolbox implementation.
 * It's implemented as an ItemGrid wrapper, which can be placed at
 * certain locations as from a given shape.
 */
public class ToolboxImpl
        extends WrappedItem<ItemsToolbox>
        implements ItemsToolbox {

    private final ItemGridImpl items;
    private Supplier<BoundingBox> shapeBoundingBoxSupplier;
    private Direction at;
    private Point2D offset;

    private final Runnable refreshExecutor = new Runnable() {
        @Override
        public void run() {
            if (null != items.getPrimitive().getLayer()) {
                items.getPrimitive().getLayer().batch();
            }
        }
    };

    public ToolboxImpl(final Supplier<BoundingBox> shapeBoundingBoxSupplier) {
        this(shapeBoundingBoxSupplier,
             new ItemGridImpl());
    }

    ToolboxImpl(final Supplier<BoundingBox> shapeBoundingBoxSupplier,
                final ItemGridImpl items) {
        this.shapeBoundingBoxSupplier = shapeBoundingBoxSupplier;
        this.items = items.onRefresh(refreshExecutor);
        this.at = Direction.NORTH_EAST;
        this.offset = new Point2D(0d,
                                  0d);
    }

    @Override
    public ToolboxImpl at(final Direction at) {
        this.at = at;
        return checkReposition();
    }

    @Override
    public ToolboxImpl offset(final Point2D offset) {
        this.offset = offset;
        return checkReposition();
    }

    @Override
    public ToolboxImpl grid(final Point2DGrid grid) {
        items.grid(grid);
        updateGridSize();
        return checkReposition();
    }

    public ToolboxImpl setGridSize(final double width,
                                   final double height) {
        // If the grid is constrained by size, update it.
        if (getGrid() instanceof SizeConstrainedGrid) {
            ((SizeConstrainedGrid) getGrid())
                    .setSize(width,
                             height);
        }
        return this;
    }

    @Override
    public ToolboxImpl add(final DecoratedItem... items) {
        this.items.add(items);
        return this;
    }

    @Override
    public Iterator<DecoratedItem> iterator() {
        return items.iterator();
    }

    @Override
    public ItemsToolbox show(final Runnable before,
                             final Runnable after) {
        return super.show(() -> {
                              ToolboxImpl.this.reposition();
                              before.run();
                          },
                          () -> {
                              ToolboxImpl.this.fireRefresh();
                              after.run();
                          });
    }

    @Override
    public ItemsToolbox hide(final Runnable before,
                             final Runnable after) {
        return super.hide(before,
                          () -> {
                              ToolboxImpl.this.fireRefresh();
                              after.run();
                          });
    }

    public ToolboxImpl useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ToolboxImpl useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useHideExecutor(executor);
        return this;
    }

    public ToolboxImpl refresh() {
        checkReposition();
        items.refresh();
        return this;
    }

    public Direction getAt() {
        return at;
    }

    public Point2D getOffset() {
        return offset;
    }

    public Point2DGrid getGrid() {
        return items.getGrid();
    }

    @Override
    public void destroy() {
        super.destroy();
        at = null;
        this.shapeBoundingBoxSupplier = null;
    }

    @Override
    ItemGridImpl getWrapped() {
        return items;
    }

    private ToolboxImpl updateGridSize() {
        final BoundingBox boundingBox = shapeBoundingBoxSupplier.get();
        final double margin = getGrid().getMargin() * 2;
        return setGridSize(boundingBox.getWidth() + margin,
                           boundingBox.getHeight() + margin);
    }

    private ToolboxImpl checkReposition() {
        if (isVisible()) {
            reposition();
        }
        return this;
    }

    private void reposition() {
        // Obtain the toolbox's location relative to the cardinal direction.
        final Point2D location = Positions.anchorFor(shapeBoundingBoxSupplier.get(),
                                                     this.at);
        // Set the toolbox primitive's location
        // and ensure it's on top of any node or connector present in the layer below.
        asPrimitive()
                .setLocation(location.offset(this.offset.getX(), this.offset.getY()))
                .moveToTop();
        fireRefresh();
    }

    private void fireRefresh() {
        refreshExecutor.run();
    }
}
