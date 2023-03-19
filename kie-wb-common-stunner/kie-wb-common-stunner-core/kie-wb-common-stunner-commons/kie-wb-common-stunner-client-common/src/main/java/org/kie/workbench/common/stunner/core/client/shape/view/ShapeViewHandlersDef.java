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

package org.kie.workbench.common.stunner.core.client.shape.view;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ShapeViewHandlersDef<W, V extends ShapeView, D extends ShapeViewDef<W, V>>
        implements ShapeViewDef<W, V> {

    private final D delegate;
    private final BiConsumer<String, V> titleHandler;
    private final BiConsumer<View<W>, V> sizeHandler;
    private final BiConsumer<W, V> fontHandler;
    private final BiConsumer<W, V> viewHandler;

    public ShapeViewHandlersDef(final D delegate) {
        this.delegate = delegate;
        this.titleHandler = delegate.titleHandler().orElse(null);
        this.fontHandler = delegate.fontHandler().orElse(null);
        this.sizeHandler = delegate.sizeHandler().orElse(null);
        this.viewHandler = delegate.viewHandler();
    }

    @Override
    public Optional<BiConsumer<String, V>> titleHandler() {
        return Optional.ofNullable(titleHandler);
    }

    @Override
    public Optional<BiConsumer<W, V>> fontHandler() {
        return Optional.ofNullable(fontHandler);
    }

    @Override
    public Optional<BiConsumer<View<W>, V>> sizeHandler() {
        return Optional.ofNullable(sizeHandler);
    }

    @Override
    public BiConsumer<W, V> viewHandler() {
        return viewHandler;
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return delegate.getType();
    }

    @Override
    public Glyph getGlyph(Class<? extends W> type,
                          final String defId) {
        return delegate.getGlyph(type,
                                 defId);
    }

    public D getShapeViewDef() {
        return delegate;
    }
}
