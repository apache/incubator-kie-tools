/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

/**
 * A factory for Shapes based on Shape Definition inputs.
 * <p>
 * Rather than a ShapeFactory, which relies a concrete Definition instance for
 * building a Shape, this factory is based on Shape Definition types, so instead
 * of using the concrete bean public API it relies on the concrete API for
 * the Shape Definition,
 */
public interface ShapeDefFactory<W, D extends ShapeDef, S extends Shape> {

    /**
     * Creates a new Shape instance for the specified Definition and ShapeDefinitions.
     * @param instance The definition (bean) instance used by MutableShapes, if applies.
     * @param shapeDef The ShapeDefinition instance that provides the bridge between the
     * view and the specified Definition (bean) argument.
     * @return A new Shape instance.
     */
    S newShape(final W instance,
               final D shapeDef);
}
