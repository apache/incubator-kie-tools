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


package org.kie.workbench.common.stunner.core.definition.shape;

import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

/**
 * The Shape Definition type.
 * - Shape Definitions act as the bridge between the model structure and properties and a given Shape.
 * - Provides a way for binding a the properties for concrete Definition bean to different kind of Shapes, so
 * different kinds of ShapeViews as well.
 * - Hides the graph complexity and processing behind the diagram to represent and it lets focusing
 * on how the bean must be represented.
 * - Subtypes can add additional methods for binding their beans to Shapes.
 * - Shape Definitions are shared types for both client and server, they cannot contain view or server
 * specific features.
 * @param <W> The bean type.
 */
public interface ShapeDef<W> {

    Class<? extends ShapeDef> getType();

    default Glyph getGlyph(final Class<? extends W> type,
                           final String defId) {
        return ShapeGlyph.create();
    }

    @SuppressWarnings("unused")
    default Glyph getGlyph(final Class<? extends W> type,
                           final Class<? extends ShapeFactory.GlyphConsumer> consumer,
                           final String defId) {
        return getGlyph(type, defId);
    }
}
