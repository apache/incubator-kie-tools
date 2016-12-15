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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.client.shape.Shape;

import java.util.List;

public interface Canvas<S extends Shape> {

    /**
     * Initializes a canvas with the given size.
     */
    Canvas initialize( int width, int height );

    /**
     * Draws or batches the updates on the canvas.
     */
    Canvas draw();

    /**
     * Set the background grid for the canvas.
     */
    Canvas setGrid( CanvasGrid grid );

    /**
     * Get a list of all Shapes on the Canvas
     */
    List<S> getShapes();

    /**
     * Returns the shape with the given identifier.
     */
    S getShape( String id );

    /**
     * Add a Shape to the Canvas
     */
    Canvas addShape( S shape );

    /**
     * Delete a Shape from the Canvas. Implementations may prompt the User for confirmation.
     */
    Canvas deleteShape( S shape );

    /**
     * Clears the canvas.
     */
    Canvas clear();

    /**
     * Returns the underlying layer.
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
