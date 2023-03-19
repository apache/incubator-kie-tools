/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.model;

/**
 * Defines a rectangular boundary.
 */
public interface Bounds {

    /**
     * Returns the Canvas (untransformed) x-coordinate of the bounds.
     * @return
     */
    double getX();

    /**
     * Sets the Canvas (untransformed) x-coordinate of the bounds.
     * @param x
     */
    void setX(final double x);

    /**
     * Returns the Canvas (untransformed) y-coordinate of the bounds.
     * @return
     */
    double getY();

    /**
     * Sets the Canvas (untransformed) y-coordinate of the bounds.
     * @param y
     */
    void setY(final double y);

    /**
     * Returns the Canvas (untransformed) width of the bounds.
     * @return
     */
    double getWidth();

    /**
     * Sets the Canvas (untransformed) width of the bounds.
     * @param width Must be positive.
     */
    void setWidth(final double width);

    /**
     * Returns the Canvas (untransformed) height of the bounds.
     * @return
     */
    double getHeight();

    /**
     * Sets the Canvas (untransformed) height of the bounds.
     * @param height Must be positive.
     */
    void setHeight(final double height);
}
