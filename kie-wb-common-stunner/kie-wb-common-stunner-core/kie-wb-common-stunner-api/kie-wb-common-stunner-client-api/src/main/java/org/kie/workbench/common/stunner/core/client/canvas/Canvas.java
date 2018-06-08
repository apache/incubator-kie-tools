/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.shape.Shape;

/**
 * A canvas type for displaying shapes.
 * <p/>
 * This default Canvas type provides methods for managing shapes using a single Layer for the diagram's representation.
 * Implementations could provide a multi-layer solution, but most of the Stunner's components and beans consider
 * a single layer approach.
 * @param <S> The supertype for the shapes that this canvas handles.
 */
public interface Canvas<S extends Shape> {

    /**
     * Initializes a canvas with the given size.
     */
    Canvas initialize(final int width,
                      final int height);

    /**
     * Get all Shapes on the Canvas
     */
    Collection<S> getShapes();

    /**
     * Returns the shape with the given identifier.
     */
    S getShape(final String id);

    /**
     * Add a Shape to the Canvas
     */
    Canvas addShape(final S shape);

    /**
     * Delete a Shape from the Canvas. Implementations may prompt the User for confirmation.
     */
    Canvas deleteShape(final S shape);

    /**
     * Gets the Shape at the specified Canvas coordinates
     * @param x The X canvas coordinate
     * @param y The Y canvas coordinate
     * @return Element at the coordinate
     */
    Optional<S> getShapeAt(final double x,
                           final double y);

    /**
     * Clears the canvas.
     */
    Canvas clear();

    /**
     * Returns the canvas' layer.
     */
    Layer getLayer();

    /**
     * Returns the canvas width.
     */
    int getWidth();

    /**
     * Returns the canvas height.
     */
    int getHeight();

    /**
     * Destroy whatever canvas state present, it will be no longer used.
     */
    void destroy();
}
