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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.types.BoundingBox;

public abstract class WrappedItem<T extends DecoratedItem>
        extends AbstractDecoratedItem<T> {

    abstract AbstractDecoratedItem<?> getWrapped();

    @Override
    public T show(final Runnable before,
                  final Runnable after) {
        getWrapped().show(before,
                          after);
        return cast();
    }

    @Override
    public T hide(final Runnable before,
                  final Runnable after) {
        getWrapped().hide(before,
                          after);
        return cast();
    }

    @Override
    public IPrimitive<?> getPrimitive() {
        return getWrapped().getPrimitive();
    }

    @Override
    public Supplier<BoundingBox> getBoundingBox() {
        return getWrapped().getBoundingBox();
    }

    @Override
    public Group asPrimitive() {
        return (Group) getWrapped().asPrimitive();
    }

    @Override
    public boolean isVisible() {
        return getWrapped().isVisible();
    }

    @Override
    public T decorate(final DecoratorItem<?> decorator) {
        getWrapped().decorate(decorator);
        return cast();
    }

    @Override
    public T tooltip(final TooltipItem<?> tooltip) {
        getWrapped().tooltip(tooltip);
        return cast();
    }

    @Override
    public T onMouseEnter(final NodeMouseEnterHandler handler) {
        getWrapped().onMouseEnter(handler);
        return cast();
    }

    @Override
    public T onMouseExit(final NodeMouseExitHandler handler) {
        getWrapped().onMouseExit(handler);
        return cast();
    }

    @Override
    public void destroy() {
        getWrapped().destroy();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
