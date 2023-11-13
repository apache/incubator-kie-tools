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
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A Shapes factory.
 * <p>
 * Main goals are:
 * <ul>
 * <li>
 * Create the a Shape instance for a specified Definition bean
 * </li>
 * <li>
 * Provides the glyph for a specified Definition bean
 * </li>
 * </ul>
 * <p>
 * A Definition Set can have multiple Shape Sets associated, so multiple
 * Shape Factories available as well. This way each factory can provide
 * different Shapes/Glyph can for the same Definition.
 */
public interface ShapeFactory<W, S extends Shape> {

    /**
     * Builds a new Shape instance for the specified Defintiion bean.
     * @param instance the Definition (bean) instance.
     * @return a new Shape instance.
     */
    S newShape(W instance);

    /**
     * Returns the glyph (thumbnail/miniature) for the specified Definition identifier.
     * @param definitionId
     * @return
     */
    Glyph getGlyph(String definitionId);

    /**
     * Returns the glyph (thumbnail/miniature) for the specified Definition identifier and consumer.
     * This allows different consumers to receive different Glyphs for the same Definition identifier.
     * @param definitionId
     * @param consumer
     * @return
     */
    @SuppressWarnings("unused")
    default Glyph getGlyph(String definitionId, Class<? extends GlyphConsumer> consumer) {
        return getGlyph(definitionId);
    }

    /**
     * Marker interface for different consumers of Glyphs.
     * Different consumers should extend this marker.
     */
    interface GlyphConsumer {

    }
}
