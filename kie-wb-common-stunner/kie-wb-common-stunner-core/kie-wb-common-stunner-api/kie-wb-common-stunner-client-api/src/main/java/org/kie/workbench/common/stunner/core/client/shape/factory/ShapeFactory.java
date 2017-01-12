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
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

/**
 * Factory for building shapes available for authoring.
 */
public interface ShapeFactory<W, C, S extends Shape> {

    /**
     * Does the Factory builds the given definition identifier.
     */
    boolean accepts( final String definitionId );

    /**
     * Get description of Shape.
     */
    String getDescription( final String definitionId );

    /**
     * Builds a new Shape instance for the given context.
     */
    S build( final W definition,
             final C context );

    /**
     * Builds a new shape glyph instance with the given size.
     */
    Glyph glyph( final String definitionId,
                 final double width,
                 final double height );
}
