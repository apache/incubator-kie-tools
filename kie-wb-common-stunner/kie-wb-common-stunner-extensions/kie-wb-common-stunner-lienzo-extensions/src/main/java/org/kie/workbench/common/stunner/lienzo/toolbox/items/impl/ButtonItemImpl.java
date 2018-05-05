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

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.ButtonItem;
import org.uberfire.mvp.Command;

public class ButtonItemImpl
        extends WrappedItem<ButtonItem>
        implements ButtonItem {

    private final AbstractFocusableGroupItem<?> item;
    private HandlerRegistration clickHandlerRegistration;
    private HandlerRegistration dragStartHandlerRegistration;
    private HandlerRegistration dragMoveHandlerRegistration;
    private HandlerRegistration dragEndHandlerRegistration;

    protected ButtonItemImpl(final Shape<?> prim) {
        this(new ItemImpl(prim));
    }

    protected ButtonItemImpl(final Group group) {
        this(new FocusableGroup(group));
    }

    ButtonItemImpl(final AbstractFocusableGroupItem<?> item) {
        this.item = item;
    }

    public ButtonItemImpl useShowExecutor(final BiConsumer<Group, Command> executor) {
        this.getWrapped().useShowExecutor(executor);
        return this;
    }

    public ButtonItemImpl useHideExecutor(final BiConsumer<Group, Command> executor) {
        this.getWrapped().useHideExecutor(executor);
        return this;
    }

    public ButtonItemImpl onDragStart(final NodeDragStartHandler handler) {
        assert null != handler;
        removeDragStartHandlerRegistration();
        dragStartHandlerRegistration = item
                .getPrimitive()
                .setDraggable(true)
                .addNodeDragStartHandler(handler);
        item.registrations().register(dragStartHandlerRegistration);
        return this;
    }

    public ButtonItemImpl onDragMove(final NodeDragMoveHandler handler) {
        assert null != handler;
        removeDragMoveHandlerRegistration();
        dragMoveHandlerRegistration = item
                .getPrimitive()
                .setDraggable(true)
                .addNodeDragMoveHandler(handler);
        item.registrations().register(dragMoveHandlerRegistration);
        return this;
    }

    public ButtonItemImpl onDragEnd(final NodeDragEndHandler handler) {
        assert null != handler;
        removeDragEndHandlerRegistration();
        dragEndHandlerRegistration = item
                .getPrimitive()
                .setDraggable(true)
                .addNodeDragEndHandler(handler);
        item.registrations().register(dragEndHandlerRegistration);
        return this;
    }

    @Override
    public ButtonItemImpl onClick(final NodeMouseClickHandler handler) {
        assert null != handler;
        removeClickHandlerRegistration();
        clickHandlerRegistration = item
                .getPrimitive()
                .setListening(true)
                .addNodeMouseClickHandler(handler);
        item.registrations().register(clickHandlerRegistration);
        return this;
    }

    @Override
    public void destroy() {
        removeClickHandlerRegistration();
        removeDragStartHandlerRegistration();
        removeDragMoveHandlerRegistration();
        removeDragEndHandlerRegistration();
        getWrapped().destroy();
    }

    @Override
    protected AbstractFocusableGroupItem<?> getWrapped() {
        return item;
    }

    private void removeClickHandlerRegistration() {
        if (null != clickHandlerRegistration) {
            clickHandlerRegistration.removeHandler();
        }
    }

    private void removeDragStartHandlerRegistration() {
        if (null != dragStartHandlerRegistration) {
            dragStartHandlerRegistration.removeHandler();
        }
    }

    private void removeDragMoveHandlerRegistration() {
        if (null != dragMoveHandlerRegistration) {
            dragMoveHandlerRegistration.removeHandler();
        }
    }

    private void removeDragEndHandlerRegistration() {
        if (null != dragEndHandlerRegistration) {
            dragEndHandlerRegistration.removeHandler();
        }
    }
}
