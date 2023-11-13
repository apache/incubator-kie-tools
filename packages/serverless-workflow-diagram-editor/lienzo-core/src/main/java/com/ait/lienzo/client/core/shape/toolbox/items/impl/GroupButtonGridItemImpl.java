/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractPrimitiveItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonGridItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.gwtproject.timer.client.Timer;

/**
 * A ButtonGridItem implementation.
 * It's composed by:
 * - A button item, which is listening for mouse enter/exit events, which fire the itemGrid show/hide
 * - An item grid that is being displayed/hidden and provides the grid items.
 */
public class GroupButtonGridItemImpl
        extends WrappedItem<ButtonGridItem>
        implements ButtonGridItem {

    static final int TIMER_DELAY_MILLIS = 150; //fix delay

    private static final Runnable NO_OP = () -> {
    };

    Set<HandlerRegistration> focusHandlerRegistrations = new HashSet<>();
    Set<HandlerRegistration> decoratorHandlerRegistrations = new HashSet<>();

    private final ButtonItemImpl button;
    private final ToolboxImpl toolbox;

    private final Timer unFocusTimer =
            new Timer() {
                @Override
                public void run() {
                    hideGrid(NO_OP,
                             () -> {
                                 immediateUnFocus();
                                 batch();
                             });
                }
            };

    protected GroupButtonGridItemImpl(final Group group) {
        this.button = new ButtonItemImpl(group);
        this.toolbox = new ToolboxImpl(new DecoratedButtonBoundingBoxSupplier());
        init();
    }

    GroupButtonGridItemImpl(final ButtonItemImpl buttonItem,
                            final ToolboxImpl toolbox) {
        this.button = buttonItem;
        this.toolbox = toolbox;
        init();
    }

    public GroupButtonGridItemImpl at(final Direction at) {
        toolbox.at(at);
        return this;
    }

    public GroupButtonGridItemImpl offset(final Point2D offset) {
        toolbox.offset(offset);
        return this;
    }

    @Override
    public GroupButtonGridItemImpl grid(final Point2DGrid grid) {
        toolbox.grid(grid);
        return this;
    }

    @Override
    public GroupButtonGridItemImpl decorateGrid(final DecoratorItem<?> decorator) {
        removeHandlers(decoratorHandlerRegistrations);
        toolbox.decorate(decorator);
        if (decorator instanceof AbstractDecoratorItem) {
            final AbstractDecoratorItem instance = (AbstractDecoratorItem) decorator;
            decoratorHandlerRegistrations.add(instance.asPrimitive()
                                                      .setListening(true)
                                                      .addNodeMouseEnterHandler(event -> itemFocusCallback.run()));
            decoratorHandlerRegistrations.add(instance.asPrimitive()
                                                      .addNodeMouseExitHandler(event -> itemUnFocusCallback.run()));
        }
        return this;
    }

    @Override
    public GroupButtonGridItemImpl show(final Runnable before,
                                        final Runnable after) {
        button.show(before,
                    after);
        return this;
    }

    @Override
    public GroupButtonGridItemImpl hide(final Runnable before,
                                        final Runnable after) {
        hideGrid(before,
                 () -> {
                     button.hide();
                     after.run();
                     GroupButtonGridItemImpl.this.batch();
                 });
        return this;
    }

    @Override
    public GroupButtonGridItemImpl showGrid() {
        toolbox.show();
        return this;
    }

    @Override
    public GroupButtonGridItemImpl hideGrid() {
        return hideGrid(NO_OP, NO_OP);
    }

    private GroupButtonGridItemImpl hideGrid(final Runnable before,
                                             final Runnable after) {
        toolbox.hide(before,
                     after);
        return this;
    }

    @Override
    public GroupButtonGridItemImpl add(final DecoratedItem... items) {
        toolbox.add(items);
        for (final DecoratedItem item : items) {
            try {
                final AbstractDecoratedItem primitiveItem = (AbstractDecoratedItem) item;
                focusHandlerRegistrations.add(registerItemFocusHandler(primitiveItem,
                                                                       itemFocusCallback));
                focusHandlerRegistrations.add(registerItemUnFocusHandler(primitiveItem,
                                                                         itemUnFocusCallback));
            } catch (final ClassCastException e) {
                throw new UnsupportedOperationException("The button only supports subtypes " +
                                                                "of " + AbstractDecoratedItem.class.getName());
            }
        }
        return this;
    }

    @Override
    public Iterator<DecoratedItem> iterator() {
        return toolbox.iterator();
    }

    @Override
    public ButtonGridItem enable() {
        button.enable();
        return this;
    }

    @Override
    public ButtonGridItem disable() {
        button.disable();
        return this;
    }

    @Override
    public ButtonGridItem onClick(final Consumer<NodeMouseClickEvent> onEvent) {
        button.onClick(onEvent);
        return this;
    }

    @Override
    public ButtonGridItem onMoveStart(final Consumer<NodeMouseMoveEvent> onEvent) {
        button.onMoveStart(onEvent);
        return this;
    }

    @Override
    public void destroy() {
        removeHandlers(decoratorHandlerRegistrations);
        removeHandlers(focusHandlerRegistrations);
        button.destroy();
        toolbox.destroy();
        super.destroy();
    }

    static void removeHandlers(Set<HandlerRegistration> handlerRegistrations) {
        handlerRegistrations.forEach((handlerRegistration) -> {
            if (null != handlerRegistration) {
                handlerRegistration.removeHandler();
            }
        });
        handlerRegistrations.clear();
    }

    @Override
    AbstractGroupItem<?> getWrapped() {
        return button.getWrapped();
    }

    public GroupButtonGridItemImpl useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.getWrapped().useShowExecutor(executor);
        return this;
    }

    public GroupButtonGridItemImpl useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.getWrapped().useHideExecutor(executor);
        return this;
    }

    private void init() {
        button.getWrapped().setUnFocusDelay(TIMER_DELAY_MILLIS);
        // Register custom focus/un-focus behaviors.
        focusHandlerRegistrations.add(registerItemFocusHandler(button,
                                                               focusCallback));
        focusHandlerRegistrations.add(registerItemUnFocusHandler(button,
                                                                 unFocusCallback));
        // Attach the toolbox's primitive and the arrow into the button group.
        this.button.asPrimitive()
                .setDraggable(false)
                .add(toolbox.asPrimitive());
    }

    private HandlerRegistration registerItemFocusHandler(final AbstractDecoratedItem item,
                                                         final Runnable callback) {
        return item.getPrimitive().addNodeMouseEnterHandler(event -> callback.run());
    }

    private HandlerRegistration registerItemUnFocusHandler(final AbstractDecoratedItem item,
                                                           final Runnable callback) {
        return item.getPrimitive().addNodeMouseExitHandler(event -> callback.run());
    }

    GroupButtonGridItemImpl focus() {
        stopTimer();
        button.getWrapped().focus();
        showGrid();
        return this;
    }

    GroupButtonGridItemImpl immediateUnFocus() {
        button.getWrapped().setUnFocusDelay(0);
        button.getWrapped().unFocus();
        button.getWrapped().setUnFocusDelay(TIMER_DELAY_MILLIS);
        return this;
    }

    GroupButtonGridItemImpl unFocus() {
        scheduleTimer();
        return this;
    }

    private final Runnable focusCallback = new Runnable() {
        @Override
        public void run() {
            GroupButtonGridItemImpl.this.focus();
        }
    };

    private final Runnable unFocusCallback = new Runnable() {
        @Override
        public void run() {
            GroupButtonGridItemImpl.this.unFocus();
        }
    };

    private final Runnable itemFocusCallback = new Runnable() {
        @Override
        public void run() {
            GroupButtonGridItemImpl.this.focus();
        }
    };

    private final Runnable itemUnFocusCallback = new Runnable() {
        @Override
        public void run() {
            GroupButtonGridItemImpl.this.unFocus();
        }
    };

    private void scheduleTimer() {
        unFocusTimer.schedule(TIMER_DELAY_MILLIS);
    }

    private void stopTimer() {
        unFocusTimer.cancel();
    }

    private void batch() {
        button.asPrimitive().batch();
    }

    // Provides the bounding box of the button plus the decorator, as for further toolbox positioning.
    private class DecoratedButtonBoundingBoxSupplier implements Supplier<BoundingBox> {

        @Override
        public BoundingBox get() {
            final DecoratorItem<?> buttonDecorator = button.getWrapped().getDecorator();
            if (null != buttonDecorator && buttonDecorator instanceof AbstractPrimitiveItem) {
                return ((AbstractPrimitiveItem) buttonDecorator).asPrimitive().getBoundingBox();
            }
            return button.getBoundingBox().get();
        }
    }
}
