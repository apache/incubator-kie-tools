/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.context;

/**
 * The context of a Grid's boundary during the rendering phase.
 */
public class GridBoundaryRenderContext {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public GridBoundaryRenderContext(final double x,
                                     final double y,
                                     final double width,
                                     final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the X co-ordinate relative to the grid of the boundary.
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y co-ordinate relative to the grid of the boundary.
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width of the Grid boundary.
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the Grid boundary.
     * @return
     */
    public double getHeight() {
        return height;
    }
}
