/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.lienzo.palette;

import java.util.Iterator;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import org.kie.workbench.common.stunner.lienzo.Decorator;
import org.kie.workbench.common.stunner.lienzo.Decorator.ItemCallback;
import org.kie.workbench.common.stunner.lienzo.grid.Grid;
import org.kie.workbench.common.stunner.lienzo.util.LienzoGroupUtils;

public abstract class AbstractPalette<T> extends Group {

    public static class Item {

        private final IPrimitive<?> primitive;
        private final ItemDecorator decorator;

        public Item(final IPrimitive<?> primitive,
                    final ItemDecorator decorator) {
            this.primitive = primitive;
            this.decorator = decorator;
        }

        public IPrimitive<?> getPrimitive() {
            return primitive;
        }

        public ItemDecorator getDecorator() {
            return decorator;
        }
    }

    public enum ItemDecorator {
        DEFAULT;
    }

    public interface Callback {

        void onItemHover(final int index,
                         final double eventX,
                         final double eventY,
                         final double itemX,
                         final double itemY);

        void onItemOut(final int index);

        void onItemMouseDown(final int index,
                             final double eventX,
                             final double eventY,
                             final double itemX,
                             final double itemY);

        void onItemClick(final int index,
                         final double eventX,
                         final double eventY,
                         final double itemX,
                         final double itemY);
    }

    protected int iconSize;
    protected int padding;
    protected int x;
    protected int y;
    protected int rows = -1;
    protected int cols = -1;
    protected Callback callback;
    protected Group itemsGroup = new Group();
    protected final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();

    public AbstractPalette() {
    }

    public T setItemCallback(final Callback callback) {
        this.callback = callback;
        return (T) this;
    }

    public T setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return (T) this;
    }

    public T setPadding(final int padding) {
        this.padding = padding;
        return (T) this;
    }

    public T setX(final int x) {
        this.x = x;
        return (T) this;
    }

    public T setY(final int y) {
        this.y = y;
        return (T) this;
    }

    public T setRows(final int rows) {
        this.rows = rows;
        return (T) this;
    }

    public T setColumns(final int cols) {
        this.cols = cols;
        return (T) this;
    }

    public T build(final Item... items) {
        clear();
        this.add(itemsGroup);
        beforeBuild();
        final Grid grid = createGrid(items.length);
        final Iterator<Grid.Point> pointIterator = grid.iterator();
        for (int c = 0; c < items.length; c++) {
            final Grid.Point point = pointIterator.next();
            final Item item = items[c];
            final int index = c;
            final double px = x + point.getX();
            final double py = y + point.getY();
            final Decorator itemDecorator = item.getDecorator() != null ?
                    createDecorator(index,
                                    px,
                                    py) : null;
            final IPrimitive<?> i = null != itemDecorator ?
                    itemDecorator.build(item.getPrimitive(),
                                        toDouble(iconSize),
                                        toDouble(iconSize)) :
                    item.getPrimitive();
            i.setX(px).setY(py).moveToTop();
            this.itemsGroup.add(i);
            handlerRegistrationManager.register(
                    i.addNodeMouseDownHandler(event -> onItemMouseDown(index,
                                                                       event.getX(),
                                                                       event.getY(),
                                                                       px,
                                                                       py))
            );
            handlerRegistrationManager.register(
                    i.addNodeMouseClickHandler(event -> onItemClick(index,
                                                                    event.getX(),
                                                                    event.getY(),
                                                                    px,
                                                                    py))
            );
        }
        afterBuild();
        return (T) this;
    }

    protected void doRedraw() {
    }

    public void redraw() {
        doRedraw();
        this.batch();
    }

    protected void beforeBuild() {
    }

    protected void afterBuild() {
    }

    protected Grid createGrid(final int itemsSize) {
        final int r = rows > 0 ? rows : itemsSize;
        final int c = cols > 0 ? cols : itemsSize;
        return new Grid(padding,
                        iconSize,
                        r,
                        c);
    }

    protected Decorator createDecorator(final int index,
                                        final double itemX,
                                        final double itemY) {
        return new Decorator(createDecoratorCallback(index,
                                                     itemX,
                                                     itemY));
    }

    protected ItemCallback createDecoratorCallback(final int index,
                                                   final double itemX,
                                                   final double itemY) {
        return new ItemCallback() {

            @Override
            public void onShow(final double x,
                               final double y) {
                doShowItem(index,
                           x,
                           y,
                           itemX,
                           itemY);
            }

            @Override
            public void onHide() {
                doItemOut(index);
            }
        };
    }

    public T clearItems() {
        handlerRegistrationManager.removeHandler();
        LienzoGroupUtils.removeChildren(itemsGroup);
        return (T) this;
    }

    public T clear() {
        clearItems();
        LienzoGroupUtils.removeChildren(this);
        return (T) this;
    }

    protected void doShowItem(final int index,
                              final double x,
                              final double y,
                              final double itemX,
                              final double itemY) {
        if (null != callback) {
            callback.onItemHover(index,
                                 x,
                                 y,
                                 itemX,
                                 itemY);
        }
    }

    protected void doItemOut(final int index) {
        if (null != callback) {
            callback.onItemOut(index);
        }
    }

    protected void onItemMouseDown(final int index,
                                   final double eventX,
                                   final double eventY,
                                   final double itemX,
                                   final double itemY) {
        if (null != callback) {
            callback.onItemMouseDown(index,
                                     eventX,
                                     eventY,
                                     itemX,
                                     itemY);
        }
    }

    protected void onItemClick(final int index,
                               final double eventX,
                               final double eventY,
                               final double itemX,
                               final double itemY) {
        if (null != callback) {
            callback.onItemClick(index,
                                 eventX,
                                 eventY,
                                 itemX,
                                 itemY);
        }
    }

    private double toDouble(final int i) {
        return (double) i;
    }
}
