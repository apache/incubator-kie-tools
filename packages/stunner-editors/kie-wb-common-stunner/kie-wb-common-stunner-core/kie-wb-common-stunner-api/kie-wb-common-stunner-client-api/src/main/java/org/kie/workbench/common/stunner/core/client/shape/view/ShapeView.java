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


package org.kie.workbench.common.stunner.core.client.shape.view;

import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

/**
 * The view for a shape instance.
 * @param <T> The view type.
 */
public interface ShapeView<T> {

    /**
     * Set the unique view uuid.
     */
    T setUUID(final String uuid);

    /**
     * Returns the unique view uuid.
     */
    String getUUID();

    /**
     * Returns the cartesian coordinate value on the X axis.
     * This coordinate is relative to the parents, if any.
     */
    double getShapeX();

    /**
     * Returns the cartesian coordinate value on the Y axis.
     * This coordinate is relative to the parents, if any.
     */
    double getShapeY();

    /**
     * Returns the absolute cartesian coordinate values for both X and Y axis..
     * This coordinate is not relative to the shape's parents, if any, neither current layer transforms.
     */
    Point2D getShapeAbsoluteLocation();

    /**
     * Set the location for shape.
     * Note that shape's location is relative to its parent, if any.
     */
    T setShapeLocation(Point2D location);

    /**
     * Returns the shape's opacity.
     */
    double getAlpha();

    /**
     * Set the shape's opacity.
     * This opacity value apply to fill and
     * borders, so setting a value of
     * <code>0</code> will produce the shape being
     * not visible at all.
     */
    T setAlpha(final double alpha);

    /**
     * Returns the RGB fill color value.
     */
    String getFillColor();

    /**
     * Set the RGB fill color value.
     */
    T setFillColor(final String color);

    /**
     * Returns the fill alpha value.
     */
    double getFillAlpha();

    /**
     * Set the fill alpha value.
     */
    T setFillAlpha(final double alpha);

    /**
     * Returns the RGB stroke color value.
     */
    String getStrokeColor();

    /**
     * Set the RGB stroke color value.
     */
    T setStrokeColor(final String color);

    /**
     * Returns the stroke aplha value.
     */
    double getStrokeAlpha();

    /**
     * Set the stroke alpha value.
     */
    T setStrokeAlpha(final double alpha);

    /**
     * Returns the stroke width value.
     */
    double getStrokeWidth();

    /**
     * Set the stroke width value.
     */
    T setStrokeWidth(final double width);

    /**
     * Specifies if the shape can be dragged.
     */
    T setDragEnabled(boolean draggable);

    /**
     * Move shape view to top.
     */
    T moveToTop();

    /**
     * Move shape view to bottom.
     */
    T moveToBottom();

    /**
     * Move shape view up.
     */
    T moveUp();

    /**
     * Move shape view down.
     */
    T moveDown();

    /**
     * Returns the relative bounding box for this view.
     */
    BoundingBox getBoundingBox();

    /**
     * Removes this shape view from the parent.
     */
    void removeFromParent();

    /**
     * Destroy this shape view instance and
     * removes it from the tree.
     */
    void destroy();

    Object getUserData();

    void setUserData(Object userData);
}
