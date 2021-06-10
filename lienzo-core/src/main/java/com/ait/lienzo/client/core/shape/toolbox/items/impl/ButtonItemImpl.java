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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

public class ButtonItemImpl
        extends WrappedItem<ButtonItem>
        implements ButtonItem {

    static final double ALPHA_ENABLED = 1d;
    static final double ALPHA_DISABLED = 0.5d;

    private final AbstractFocusableGroupItem<?> item;
    private Consumer<NodeMouseClickEvent> onClickEvent;
    private Consumer<NodeMouseMoveEvent> onMoveStartEvent;
    private HandlerRegistration clickHandlerRegistration;
    private HandlerRegistration downHandlerRegistration;
    private HandlerRegistration moveHandlerRegistration;
    private HandlerRegistration exitHandlerRegistration;
    private boolean isMouseDown;

    protected ButtonItemImpl(final Shape<?> prim) {
        this(new ItemImpl(prim));
    }

    protected ButtonItemImpl(final Group group) {
        this(new FocusableGroup(group));
    }

    ButtonItemImpl(final AbstractFocusableGroupItem<?> item) {
        this.item = item;
        this.isMouseDown = false;
        initEventHandling();
    }

    public ButtonItemImpl useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ButtonItemImpl useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        this.getWrapped().useHideExecutor(executor);
        return this;
    }

    @Override
    public ButtonItem enable() {
        return setEnabled(true);
    }

    @Override
    public ButtonItem disable() {
        return setEnabled(false);
    }

    private void initEventHandling() {
        // Ensure the shape is "selectable", rather its given attributes
        ensureShapeSelection(getPrimitive());

        // Register handlers.
        clickHandlerRegistration =
                getPrimitive().addNodeMouseClickHandler(clickEvent -> {
                    isMouseDown = false;
                    if (null != onClickEvent) {
                        onClickEvent.accept(clickEvent);
                    }
                });

        downHandlerRegistration =
                getPrimitive().addNodeMouseDownHandler(downEvent -> isMouseDown = true);

        moveHandlerRegistration =
                getPrimitive().addNodeMouseMoveHandler(moveEvent -> {
                    if (isMouseDown) {
                        isMouseDown = false;
                        if (null != onMoveStartEvent) {
                            onMoveStartEvent.accept(moveEvent);
                        }
                    }
                });

        exitHandlerRegistration =
                getPrimitive().addNodeMouseExitHandler(exitEvent -> isMouseDown = false);
    }

    @Override
    public ButtonItem onClick(final Consumer<NodeMouseClickEvent> onEvent) {
        this.onClickEvent = onEvent;
        return this;
    }

    @Override
    public ButtonItem onMoveStart(final Consumer<NodeMouseMoveEvent> onEvent) {
        this.onMoveStartEvent = onEvent;
        return this;
    }

    @Override
    public void destroy() {
        super.destroy();
        removeClickHandlersRegistration();
        removeDownHandlersRegistration();
        removeMoveHandlerRegistration();
        removeExitHandlerRegistration();
    }

    @Override
    AbstractFocusableGroupItem<?> getWrapped() {
        return item;
    }

    private ButtonItem setEnabled(final boolean enabled) {
        setEnabled(getWrapped().getGroupItem().asPrimitive(), enabled);
        return this;
    }

    private void removeClickHandlersRegistration() {
        if (null != clickHandlerRegistration) {
            clickHandlerRegistration.removeHandler();
            clickHandlerRegistration = null;
        }
    }

    private void removeDownHandlersRegistration() {
        if (null != downHandlerRegistration) {
            downHandlerRegistration.removeHandler();
            downHandlerRegistration = null;
        }
    }

    private void removeMoveHandlerRegistration() {
        if (null != moveHandlerRegistration) {
            moveHandlerRegistration.removeHandler();
            moveHandlerRegistration = null;
        }
    }

    private void removeExitHandlerRegistration() {
        if (null != exitHandlerRegistration) {
            exitHandlerRegistration.removeHandler();
            exitHandlerRegistration = null;
        }
    }

    private static void setEnabled(final IPrimitive<?> primitive,
                                   final boolean enabled) {
        final double alpha = enabled ? ALPHA_ENABLED : ALPHA_DISABLED;
        primitive.setListening(enabled);
        primitive.setAlpha(alpha);
    }

    private static void ensureShapeSelection(IPrimitive<?> prim) {
        if (prim instanceof Shape) {
            final Shape<?> shape = (Shape<?>) prim;
            shape.setFillBoundsForSelection(true);
            shape.setFillShapeForSelection(true);
        }
    }
}
