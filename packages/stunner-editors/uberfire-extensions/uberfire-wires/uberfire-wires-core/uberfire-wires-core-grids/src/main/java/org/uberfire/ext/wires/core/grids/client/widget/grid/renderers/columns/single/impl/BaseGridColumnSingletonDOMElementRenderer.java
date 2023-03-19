/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl;

import java.util.Objects;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.GridColumnSingletonDOMElementRenderer;

public abstract class BaseGridColumnSingletonDOMElementRenderer<T, W extends Widget, E extends BaseDOMElement> extends BaseGridColumnRenderer<T> implements GridColumnSingletonDOMElementRenderer<T> {

    protected final SingletonDOMElementFactory<W, E> factory;

    public BaseGridColumnSingletonDOMElementRenderer(final SingletonDOMElementFactory<W, E> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }
}
