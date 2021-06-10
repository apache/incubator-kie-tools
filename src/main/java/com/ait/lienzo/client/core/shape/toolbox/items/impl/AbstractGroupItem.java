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
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractPrimitiveItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

public abstract class AbstractGroupItem<T extends AbstractGroupItem>
        extends AbstractDecoratedItem<T> {

    private final GroupItem groupItem;
    private DecoratorItem<?> decorator;
    private TooltipItem<?> tooltip;
    private HandlerRegistration mouseEnterHandlerRegistration;
    private HandlerRegistration mouseExitHandlerRegistration;

    private Supplier<BoundingBox> boundingBoxSupplier =
            () -> AbstractGroupItem.this.getPrimitive().getComputedBoundingPoints().getBoundingBox();

    protected AbstractGroupItem(final GroupItem groupItem) {
        this.groupItem = groupItem;
    }

    @Override
    public T decorate(final DecoratorItem<?> decorator) {
        if (isDecorated()) {
            this.decorator.destroy();
        }
        this.decorator = decorator;
        attachDecorator();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T tooltip(final TooltipItem tooltip) {
        initTooltip(tooltip);
        return cast();
    }

    @Override
    public boolean isVisible() {
        return groupItem.isVisible();
    }

    public void showDecorator() {
        if (isDecorated()) {
            this.decorator.show();
            final IPrimitive<?> primitive = getDecoratorPrimitive();
            if (null != primitive) {
                primitive.moveToBottom();
            }
        }
    }

    public void showTooltip() {
        if (hasTooltip()) {
            this.tooltip.show();
        }
    }

    public void hideDecorator() {
        if (isDecorated()) {
            this.decorator.hide();
        }
    }

    public void hideTooltip() {
        if (hasTooltip()) {
            this.tooltip.hide();
        }
    }

    public T useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        this.groupItem.useShowExecutor(executor);
        return cast();
    }

    public T useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        this.groupItem.useHideExecutor(executor);
        return cast();
    }

    public boolean isDecorated() {
        return null != this.decorator;
    }

    public boolean hasTooltip() {
        return null != this.tooltip;
    }

    @Override
    public void destroy() {
        groupItem.destroy();
        decorate(null);
        tooltip(null);
        destroyHandlers();
        getPrimitive().removeFromParent();
    }

    @Override
    public Group asPrimitive() {
        return groupItem.asPrimitive();
    }

    @Override
    public Supplier<BoundingBox> getBoundingBox() {
        return boundingBoxSupplier;
    }

    @Override
    public T onMouseEnter(final NodeMouseEnterHandler handler) {
        if (null != mouseEnterHandlerRegistration) {
            mouseEnterHandlerRegistration.removeHandler();
        }
        mouseEnterHandlerRegistration = registerMouseEnterHandler(handler);
        return cast();
    }

    @Override
    public T onMouseExit(final NodeMouseExitHandler handler) {
        assert null != handler;
        if (null != mouseExitHandlerRegistration) {
            mouseExitHandlerRegistration.removeHandler();
        }
        mouseExitHandlerRegistration = registerMouseExitHandler(handler);
        return cast();
    }

    protected T setBoundingBox(final Supplier<BoundingBox> supplier) {
        this.boundingBoxSupplier = supplier;
        return cast();
    }

    protected HandlerRegistration registerMouseEnterHandler(final NodeMouseEnterHandler handler) {
        assert null != handler;
        HandlerRegistration reg =
                getPrimitive()
                        .setListening(true)
                        .addNodeMouseEnterHandler(handler);
        return reg;
    }

    protected HandlerRegistration registerMouseExitHandler(final NodeMouseExitHandler handler) {
        assert null != handler;
        HandlerRegistration reg =
                getPrimitive()
                        .setListening(true)
                        .addNodeMouseExitHandler(handler);
        return reg;
    }

    protected GroupItem getGroupItem() {
        return groupItem;
    }

    public DecoratorItem<?> getDecorator() {
        return decorator;
    }

    protected TooltipItem<?> getTooltip() {
        return tooltip;
    }

    @SuppressWarnings("unchecked")
    private void initTooltip(final TooltipItem<?> tooltipItem) {
        if (hasTooltip()) {
            this.tooltip.destroy();
        }
        this.tooltip = tooltipItem;
        if (hasTooltip()) {
            attachTooltip();
            updateAddOnsVisibility();
        }
    }

    private void attachDecorator() {
        if (isDecorated()) {
            decorator.setBoundingBox(getBoundingBox().get());
            final IPrimitive<?> primitive = getDecoratorPrimitive();
            if (null != primitive) {
                add(primitive);
            }
            updateAddOnsVisibility();
        }
    }

    void add(IPrimitive<?> primitive) {
        groupItem.add(primitive);
    }

    private IPrimitive<?> getDecoratorPrimitive() {
        if (null != decorator && decorator instanceof AbstractPrimitiveItem) {
            return ((AbstractPrimitiveItem) decorator).asPrimitive();
        }
        return null;
    }

    private void attachTooltip() {
        tooltip.forComputedBoundingBox(new Supplier<BoundingBox>() {
            @Override
            public BoundingBox get() {
                return AbstractGroupItem.this.computeAbsoluteBoundingBox(5);
            }
        });
        if (tooltip instanceof AbstractPrimitiveItem) {
            add(((AbstractPrimitiveItem) tooltip).asPrimitive());
        }
    }

    private BoundingBox computeAbsoluteBoundingBox(final double pad) {
        final BoundingBox bb = getBoundingBox().get();
        final Point2D computedLocation = asPrimitive().getComputedLocation();
        return BoundingBox.fromDoubles(computedLocation.getX() - pad,
                               computedLocation.getY() - pad,
                               computedLocation.getX() + bb.getWidth() + pad,
                               computedLocation.getY() + bb.getHeight() + pad);
    }

    protected void updateAddOnsVisibility() {
        if (isVisible()) {
            showAddOns();
        } else {
            hideAddOns();
        }
    }

    protected void showAddOns() {
        showDecorator();
        showTooltip();
    }

    protected void hideAddOns() {
        hideDecorator();
        hideTooltip();
    }

    private void destroyHandlers() {
        if (null != mouseEnterHandlerRegistration) {
            mouseEnterHandlerRegistration.removeHandler();
            mouseEnterHandlerRegistration = null;
        }
        if (null != mouseExitHandlerRegistration) {
            mouseExitHandlerRegistration.removeHandler();
            mouseExitHandlerRegistration = null;
        }
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
