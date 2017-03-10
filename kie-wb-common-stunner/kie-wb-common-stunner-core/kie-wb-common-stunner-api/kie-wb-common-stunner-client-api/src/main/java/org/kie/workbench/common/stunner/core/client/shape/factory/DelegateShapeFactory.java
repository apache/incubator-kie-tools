/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.factory;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

public abstract class DelegateShapeFactory<W, C, S extends Shape> implements ShapeFactory<W, C, S> {

    private final List<ShapeDefFactory> factories = new LinkedList<>();

    protected abstract DefinitionManager getDefinitionManager();

    public DelegateShapeFactory<W, C, S> addDelegate(final ShapeDefFactory factory) {
        factories.add(factory);
        return this;
    }

    @Override
    public boolean accepts(final String definitionId) {
        return null != getFactory(definitionId);
    }

    @Override
    public String getDescription(final String definitionId) {
        return getFactory(definitionId).getDescription(definitionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public S build(final W definition,
                   final C context) {
        return (S) getFactoryForDefinition(definition).build(definition,
                                                             context);
    }

    @Override
    public Glyph glyph(final String definitionId,
                       final double width,
                       final double height) {
        return getFactory(definitionId).glyph(definitionId,
                                              width,
                                              height);
    }

    private ShapeDefFactory getFactoryForDefinition(final Object definition) {
        final String defId = getDefinitionManager().adapters().forDefinition().getId(definition);
        return getFactory(defId);
    }

    private ShapeDefFactory getFactory(final String definitionId) {
        return factories.stream()
                .filter(f -> f.accepts(definitionId))
                .findFirst()
                .orElse(null);
    }
}
