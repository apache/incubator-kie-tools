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

package com.ait.lienzo.client.core.shape.toolbox.items;

public abstract class AbstractDecoratorItem<T extends AbstractDecoratorItem>
        extends AbstractPrimitiveItem<T>
        implements DecoratorItem<T> {

    protected abstract void doShow();

    protected abstract void doHide();

    public abstract T copy();

    @Override
    public final T show() {
        doShow();
        return batch();
    }

    @Override
    public final T hide() {
        doHide();
        return batch();
    }

    @Override
    public void destroy() {
        asPrimitive().removeFromParent();
    }

    @SuppressWarnings("unchecked")
    private T batch() {
        if (null != asPrimitive().getLayer()) {
            asPrimitive().getLayer().batch();
        }
        return (T) this;
    }
}
