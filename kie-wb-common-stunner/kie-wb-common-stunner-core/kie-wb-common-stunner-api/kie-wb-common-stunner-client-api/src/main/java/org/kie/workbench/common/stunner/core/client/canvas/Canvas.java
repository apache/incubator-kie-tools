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
import org.uberfire.mvp.Command;

/**
 * A canvas type for displaying shapes.
 * <p>
 * This default Canvas type provides methods for managing shapes using a single Layer for the diagram's representation.
 * Implementations could provide a multi-layer solution, but most of the Stunner's components and beans consider
 * a single layer approach.
 */
public interface Canvas<S extends Shape> {

    /**
     * Get all Shapes on the Canvas
     */
    Collection<S> getShapes();

    /**
     * Returns the shape with the given identifier.
     */
    S getShape(String id);

    /**
     * Add a Shape to the Canvas
     */
    Canvas addShape(S shape);

    /**
     * Delete a Shape from the Canvas. Implementations may prompt the User for confirmation.
     */
    Canvas deleteShape(S shape);

    /**
     * Gets the Shape at the specified Canvas coordinates
     * @param x The X canvas coordinate
     * @param y The Y canvas coordinate
     * @return Element at the coordinate
     */
    Optional<S> getShapeAt(double x,
                           double y);

    /**
     * Clears the canvas.
     */
    Canvas clear();

    /**
     * TODO: Remove - never called?
     * A command fired after drawing the canvas.
     */
    void onAfterDraw(Command callback);

    /**
     * Returns the canvas width.
     */
    int getWidthPx();

    /**
     * Returns the canvas height.
     */
    int getHeightPx();

    /**
     * Returns the canvas transform attributes.
     */
    Transform getTransform();

    /**
     * Gives focus to the canvas.
     */
    void focus();

    /**
     * Destroy whatever canvas state present, it will be no longer used.
     */
    void destroy();
}
