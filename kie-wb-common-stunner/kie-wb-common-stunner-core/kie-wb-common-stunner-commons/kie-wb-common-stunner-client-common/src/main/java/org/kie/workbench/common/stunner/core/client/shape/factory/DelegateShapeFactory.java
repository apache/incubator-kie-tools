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

package org.kie.workbench.common.stunner.core.client.shape.factory;

import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.core.util.DefinitionIdMap;

@Dependent
public class DelegateShapeFactory<W, S extends Shape> extends AbstractShapeFactory<W, S> {

    private final DefinitionIdMap<DefinitionTypeBindings> definitionTypeBindings;

    public DelegateShapeFactory() {
        this.definitionTypeBindings = new DefinitionIdMap<>();
    }

    public DelegateShapeFactory<W, S> delegate(final Class<? extends W> definitionType,
                                               final ShapeDef<? extends W> shapeDef,
                                               final Supplier<? extends ShapeDefFactory> factory) {
        definitionTypeBindings.put(definitionType,
                                   new DefinitionTypeBindings(definitionType,
                                                              shapeDef,
                                                              factory));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S newShape(final W definition) {
        final DefinitionTypeBindings bindings = getBindings(definition.getClass());
        final ShapeDef<? extends W> shapeDef = bindings.shapeDef;
        return (S) bindings.factory.get()
                .newShape(definition,
                          shapeDef);
    }

    @PreDestroy
    public void destroy() {
        definitionTypeBindings.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Glyph getGlyphFor(final String definitionId) {
        final DefinitionTypeBindings bindings = definitionTypeBindings.get(definitionId);
        final Class defType = bindings.defType;
        return bindings.shapeDef.getGlyph(defType,
                                          definitionId);
    }

    private class DefinitionTypeBindings {

        final Class<?> defType;
        final ShapeDef<? extends W> shapeDef;
        final Supplier<? extends ShapeDefFactory> factory;

        private DefinitionTypeBindings(final Class<?> defType,
                                       final ShapeDef<? extends W> shapeDef,
                                       final Supplier<? extends ShapeDefFactory> factory) {
            this.defType = defType;
            this.shapeDef = shapeDef;
            this.factory = factory;
        }
    }

    private DefinitionTypeBindings getBindings(final Class<?> type) {
        final DefinitionTypeBindings bindings = this.definitionTypeBindings.get(type);
        if (null == bindings) {
            throw new RuntimeException("No ShapeDefinition or ShapeFactory binding found for " +
                                               "the Definition type [" + type + "]");
        }
        return bindings;
    }
}
