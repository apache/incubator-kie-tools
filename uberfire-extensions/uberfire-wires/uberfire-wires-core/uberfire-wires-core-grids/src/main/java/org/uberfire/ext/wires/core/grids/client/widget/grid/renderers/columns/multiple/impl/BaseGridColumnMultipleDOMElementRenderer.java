/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.multiple.impl;

import java.util.Objects;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.MultipleDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.multiple.GridColumnMultipleDOMElementRenderer;

public abstract class BaseGridColumnMultipleDOMElementRenderer<T, W extends Widget, E extends BaseDOMElement> extends BaseGridColumnRenderer<T> implements GridColumnMultipleDOMElementRenderer<T> {

    protected final MultipleDOMElementFactory<W, E> factory;

    public BaseGridColumnMultipleDOMElementRenderer(final MultipleDOMElementFactory<W, E> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    @Override
    public void initialiseResources() {
        factory.initialiseResources();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }

    @Override
    public void freeUnusedResources() {
        factory.freeUnusedResources();
    }
}
