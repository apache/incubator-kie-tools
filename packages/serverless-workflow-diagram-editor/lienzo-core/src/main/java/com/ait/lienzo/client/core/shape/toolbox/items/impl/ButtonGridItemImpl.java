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
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
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
public class ButtonGridItemImpl
        extends WrappedItem<ButtonGridItem>
        implements ButtonGridItem {

    static final int TIMER_DELAY_MILLIS = 500;

    private static final Runnable NO_OP = () -> {
    };

    Set<HandlerRegistration> focusHandlerRegistrations = new HashSet<>();
    Set<HandlerRegistration> decoratorHandlerRegistrations = new HashSet<>();

    private final ButtonItemImpl button;
    private final ToolboxImpl toolbox;
    private final MultiPath arrow;
    private final Timer unFocusTimer =
            new Timer() {
                @Override
                public void run() {
                    hideGrid(NO_OP,
                             () -> {
                                 immediateUnFocus();
                                 showArrow();
                                 batch();
                             });
                }
            };

    protected ButtonGridItemImpl(final Shape<?> prim) {
        this.button = new ButtonItemImpl(prim);
        this.toolbox = new ToolboxImpl(new DecoratedButtonBoundingBoxSupplier());
        this.arrow = buildArrow();
        init();
    }

    protected ButtonGridItemImpl(final Group group) {
        this.button = new ButtonItemImpl(group);
        this.toolbox = new ToolboxImpl(new DecoratedButtonBoundingBoxSupplier());
        this.arrow = buildArrow();
        init();
    }

    ButtonGridItemImpl(final ButtonItemImpl buttonItem,
                       final ToolboxImpl toolbox) {
        this.button = buttonItem;
        this.toolbox = toolbox;
        this.arrow = buildArrow();
        init();
    }

    public ButtonGridItemImpl at(final Direction at) {
        toolbox.at(at);
        // Rotate the arrow to the drop-direction.
        rotateArrow();
        // Re-position the arrow.
        positionArrow();
        return this;
    }

    public ButtonGridItemImpl offset(final Point2D offset) {
        toolbox.offset(offset);
        return this;
    }

    @Override
    public ButtonGridItemImpl grid(final Point2DGrid grid) {
        toolbox.grid(grid);
        return this;
    }

    @Override
    public ButtonGridItemImpl decorateGrid(final DecoratorItem<?> decorator) {
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
    public ButtonGridItemImpl show(final Runnable before,
                                   final Runnable after) {
        button.show(before,
                    after);
        return this;
    }

    @Override
    public ButtonGridItemImpl hide(final Runnable before,
                                   final Runnable after) {
        hideGrid(before,
                 () -> {
                     button.hide();
                     after.run();
                     ButtonGridItemImpl.this.batch();
                 });
        return this;
    }

    @Override
    public ButtonGridItemImpl showGrid() {
        toolbox.show();
        return this;
    }

    @Override
    public ButtonGridItemImpl hideGrid() {
        return hideGrid(NO_OP, NO_OP);
    }

    private ButtonGridItemImpl hideGrid(final Runnable before,
                                        final Runnable after) {
        toolbox.hide(before,
                     after);
        return this;
    }

    @Override
    public ButtonGridItemImpl add(final DecoratedItem... items) {
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
        arrow.removeFromParent();
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

    public ButtonGridItemImpl useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ButtonGridItemImpl useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.getWrapped().useHideExecutor(executor);
        return this;
    }

    MultiPath getArrow() {
        return arrow;
    }

    private void init() {
        button.getWrapped().setUnFocusDelay(TIMER_DELAY_MILLIS);
        // Register custom focus/un-focus behaviors.
        focusHandlerRegistrations.add(registerItemFocusHandler(button,
                                                               focusCallback));
        focusHandlerRegistrations.add(registerItemUnFocusHandler(button,
                                                                 unFocusCallback));
        // Attach the toolbox's primiitive and the arrow into the button group.
        this.button.asPrimitive()
                .setDraggable(false)
                .add(toolbox.asPrimitive())
                .add(arrow);
        // Position the arrow.
        positionArrow();
    }

    private HandlerRegistration registerItemFocusHandler(final AbstractDecoratedItem item,
                                                         final Runnable callback) {
        return item.getPrimitive().addNodeMouseEnterHandler(event -> callback.run());
    }

    private HandlerRegistration registerItemUnFocusHandler(final AbstractDecoratedItem item,
                                                           final Runnable callback) {
        return item.getPrimitive().addNodeMouseExitHandler(event -> callback.run());
    }

    ButtonGridItemImpl focus() {
        stopTimer();
        button.getWrapped().focus();
        hideArrow();
        showGrid();
        return this;
    }

    ButtonGridItemImpl immediateUnFocus() {
        button.getWrapped().setUnFocusDelay(0);
        button.getWrapped().unFocus();
        button.getWrapped().setUnFocusDelay(TIMER_DELAY_MILLIS);
        return this;
    }

    ButtonGridItemImpl unFocus() {
        scheduleTimer();
        return this;
    }

    private final Runnable focusCallback = new Runnable() {
        @Override
        public void run() {
            ButtonGridItemImpl.this.focus();
        }
    };

    private final Runnable unFocusCallback = new Runnable() {
        @Override
        public void run() {
            ButtonGridItemImpl.this.unFocus();
        }
    };

    private final Runnable itemFocusCallback = new Runnable() {
        @Override
        public void run() {
            ButtonGridItemImpl.this.focus();
        }
    };

    private final Runnable itemUnFocusCallback = new Runnable() {
        @Override
        public void run() {
            ButtonGridItemImpl.this.unFocus();
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

    private void showArrow() {
        arrow.setAlpha(1);
    }

    private void hideArrow() {
        arrow.setAlpha(0);
    }

    private boolean isDropDown() {
        switch (toolbox.getAt()) {
            case NORTH:
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
                return true;
        }
        return false;
    }

    private void rotateArrow() {
        if (isDropDown()) {
            arrow.setRotationDegrees(0);
        } else {
            arrow.setRotationDegrees(-90);
        }
    }

    private void positionArrow() {
        // Attach the arrow that indicates drop direction, into the button group.
        final BoundingBox buttonbb = this.button.getBoundingBox().get();
        final BoundingBox arrowbb = this.arrow.getBoundingBox();
        final double buttonw = buttonbb.getWidth();
        final double buttonh = buttonbb.getHeight();
        final double arroww = arrowbb.getWidth();
        final double arrowh = arrowbb.getHeight();
        this.arrow
                .setX(isDropDown() ?
                              (buttonw / 2) - (arroww / 2) :
                              buttonw + arroww)
                .setY(isDropDown() ?
                              buttonh + (arrowh * 2) :
                              (buttonh / 2) + arrowh);
    }

    private MultiPath buildArrow() {
        final BoundingBox bb = this.button.getBoundingBox().get();
        final double l = bb.getWidth() * 0.4;
        final double h = bb.getHeight() * 0.2;
        return buildArrow(l,
                          h);
    }

    private static MultiPath buildArrow(final double l,
                                        final double h) {
        return new MultiPath()
                .L(l,
                   0)
                .L(l / 2,
                   h)
                .L(0,
                   0)
                .z();
    }
}
