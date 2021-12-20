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

package org.kie.workbench.common.stunner.core.client.components.proxies;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;

public abstract class AbstractShapeProxyView<S extends ElementShape>
        implements ShapeProxyView<S> {

    private Canvas canvas;
    private Supplier<S> shapeBuilder;
    private Consumer<S> shapeAcceptor;
    private Consumer<S> shapeDestroyer;

    @Override
    public AbstractShapeProxyView<S> onCreate(final Supplier<S> shape) {
        this.shapeBuilder = shape;
        return this;
    }

    @Override
    public AbstractShapeProxyView<S> onAccept(final Consumer<S> shape) {
        this.shapeAcceptor = shape;
        return this;
    }

    @Override
    public AbstractShapeProxyView<S> onDestroy(final Consumer<S> shape) {
        this.shapeDestroyer = shape;
        return this;
    }

    @Override
    public AbstractShapeProxyView<S> setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    public void destroy() {
        doDestroy();
        canvas = null;
        shapeBuilder = null;
        shapeAcceptor = null;
        shapeDestroyer = null;
    }

    protected abstract void doDestroy();

    protected Canvas getCanvas() {
        return canvas;
    }

    protected Supplier<S> getShapeBuilder() {
        return shapeBuilder;
    }

    protected Consumer<S> getShapeAcceptor() {
        return shapeAcceptor;
    }

    protected Consumer<S> getShapeDestroyer() {
        return shapeDestroyer;
    }
}
