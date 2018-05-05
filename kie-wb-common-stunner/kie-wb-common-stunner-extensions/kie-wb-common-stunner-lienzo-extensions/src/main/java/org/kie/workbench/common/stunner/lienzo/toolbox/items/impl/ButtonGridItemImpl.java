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
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.Point2DGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratedItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratorItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractPrimitiveItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.ButtonGridItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratedItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratorItem;
import org.uberfire.mvp.Command;

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

    private final HandlerRegistration[] decoratorHandlers = new HandlerRegistration[2];
    private final ButtonItemImpl button;
    private final ToolboxImpl toolbox;
    private final MultiPath arrow;
    private final Timer unFocusTimer =
            new Timer() {
                @Override
                public void run() {
                    hideGrid(() -> {
                             },
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
        removeDecoratorHandlers();
        toolbox.decorate(decorator);
        if (decorator instanceof AbstractDecoratorItem) {
            final AbstractDecoratorItem instance = (AbstractDecoratorItem) decorator;
            decoratorHandlers[0] = instance
                    .asPrimitive()
                    .setListening(true)
                    .addNodeMouseEnterHandler(event -> itemFocusCallback.execute());
            decoratorHandlers[1] = instance.asPrimitive().addNodeMouseExitHandler(event -> itemUnFocusCallback.execute());
            registrations().register(decoratorHandlers[0]);
            registrations().register(decoratorHandlers[1]);
        }
        return this;
    }

    @Override
    public ButtonGridItemImpl show(final Command before,
                                   final Command after) {
        button.show(before,
                    after);
        return this;
    }

    @Override
    public ButtonGridItemImpl hide(final Command before,
                                   final Command after) {
        hideGrid(before,
                 () -> {
                     button.hide();
                     after.execute();
                     batch();
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
        return hideGrid(() -> {
                        },
                        () -> {
                        });
    }

    private ButtonGridItemImpl hideGrid(final Command before,
                                        final Command after) {
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
                registerItemFocusHandler(primitiveItem,
                                         itemFocusCallback);
                registerItemUnFocusHandler(primitiveItem,
                                           itemUnFocusCallback);
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
    public ButtonGridItemImpl onClick(final NodeMouseClickHandler handler) {
        button.onClick(handler);
        return this;
    }

    @Override
    public ButtonGridItemImpl onDragStart(final NodeDragStartHandler handler) {
        button.onDragStart(handler);
        return this;
    }

    @Override
    public ButtonGridItemImpl onDragMove(final NodeDragMoveHandler handler) {
        button.onDragMove(handler);
        return this;
    }

    @Override
    public ButtonGridItemImpl onDragEnd(final NodeDragEndHandler handler) {
        button.onDragEnd(handler);
        return this;
    }

    @Override
    public void destroy() {
        removeDecoratorHandlers();
        button.destroy();
        toolbox.destroy();
        arrow.removeFromParent();
        super.destroy();
    }

    @Override
    protected AbstractGroupItem<?> getWrapped() {
        return button.getWrapped();
    }

    public ButtonGridItemImpl useShowExecutor(final BiConsumer<Group, Command> executor) {
        toolbox.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ButtonGridItemImpl useHideExecutor(final BiConsumer<Group, Command> executor) {
        toolbox.getWrapped().useHideExecutor(executor);
        return this;
    }

    MultiPath getArrow() {
        return arrow;
    }

    private void init() {
        button.getWrapped().setUnFocusDelay(TIMER_DELAY_MILLIS);
        // Register custom focus/un-focus behaviors.
        registerItemFocusHandler(button,
                                 focusCallback);
        registerItemUnFocusHandler(button,
                                   unFocusCallback);
        // Attach the toolbox's primiitive and the arrow into the button group.
        this.button.asPrimitive()
                .setDraggable(false)
                .add(toolbox.asPrimitive())
                .add(arrow);
        // Position the arrow.
        positionArrow();
    }

    private void registerItemFocusHandler(final AbstractDecoratedItem item,
                                          final Command callback) {
        registrations()
                .register(
                        item.getPrimitive().addNodeMouseEnterHandler(event -> callback.execute())
                );
    }

    private void registerItemUnFocusHandler(final AbstractDecoratedItem item,
                                            final Command callback) {
        registrations()
                .register(
                        item.getPrimitive().addNodeMouseExitHandler(event -> callback.execute())
                );
    }

    private HandlerRegistrationManager registrations() {
        return button.getWrapped()
                .registrations();
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

    private final Command focusCallback = this::focus;

    private final Command unFocusCallback = this::unFocus;

    private final Command itemFocusCallback = this::focus;

    private final Command itemUnFocusCallback = this::unFocus;

    private void scheduleTimer() {
        unFocusTimer.schedule(TIMER_DELAY_MILLIS);
    }

    private void stopTimer() {
        unFocusTimer.cancel();
    }

    private void batch() {
        button.asPrimitive().batch();
    }

    private void removeDecoratorHandlers() {
        if (null != decoratorHandlers[0]) {
            decoratorHandlers[0].removeHandler();
        }
        if (null != decoratorHandlers[1]) {
            decoratorHandlers[1].removeHandler();
        }
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
