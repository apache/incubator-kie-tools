/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

/**
 * Factory for building shape definitions.
 */
public interface ShapeDefFactory<W, C, S extends Shape, P extends ShapeDef<W>> extends ShapeFactory<W, C, S> {

    // TODO: Rename by "registerShapedef" - term "add" is not the correct one.
    void addShapeDef(final Class<?> clazz,
                     final P def);

    // TODO: Add method build(instance, def, context)
}
