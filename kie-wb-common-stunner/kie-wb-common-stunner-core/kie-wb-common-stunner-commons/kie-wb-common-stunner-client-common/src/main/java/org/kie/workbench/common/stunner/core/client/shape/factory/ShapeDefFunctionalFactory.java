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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

/**
 * A shape definition factory that can be aggregated by other shape definition factories and
 * allows setting a factory (functional) interface for each Shape Definition type. This functional interface
 * is the responsible * for creating the shape instance as for the given definition and shape definition instances.
 */
@Dependent
public class ShapeDefFunctionalFactory<W, D extends ShapeDef, S extends Shape> implements ShapeDefFactory<W, D, S> {

    private final Map<Class<?>, BiFunction<W, D, S>> shapeBuilders = new HashMap<>();

    /**
     * Sets the factory function for the specified Shape Definition type.
     */
    @SuppressWarnings("unchecked")
    public ShapeDefFunctionalFactory<W, D, S> set(final Class<? extends D> shapeDefType,
                                                  final BiFunction<W, D, S> shapeBuilderFunction) {
        shapeBuilders.put(shapeDefType,
                          shapeBuilderFunction);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S newShape(final W instance,
                      final D shapeDef) {
        return (S) newShape(instance,
                            shapeDef,
                            shapeDef.getType());
    }

    @PreDestroy
    public void destroy() {
        shapeBuilders.clear();
    }

    protected S newShape(final W instance,
                       final D shapeDef,
                       final Class<? extends D> shapeDefType) {
        return getShapeBuilder(shapeDefType)
                .apply(instance,
                       shapeDef);
    }

    private BiFunction<W, D, S> getShapeBuilder(final Class<? extends D> shapeDefType) {
        final BiFunction<W, D, S> builder = shapeBuilders.get(shapeDefType);
        if (null == builder) {
            throw new RuntimeException("No builder function specified for " +
                                               "Shape Definition type [" + shapeDefType.getName() + "]");
        }
        return builder;
    }
}
