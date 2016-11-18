/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view;

/**
 * The view for a shape instance.
 * @param <T> The view type.
 */
public interface ShapeView<T> {

    String UUID_PREFIX = "stunner:";

    /**
     * Set the unique view uuid.
     */
    T setUUID( String uuid );

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
    double[] getShapeAbsoluteLocation();

    /**
     * Set the cartesian coordinate value on the X axis.
     */
    T setShapeX( double x );

    /**
     * Set the cartesian coordinate value on the Y axis.
     */
    T setShapeY( double y );

    /**
     * Returns the RGB fill color value.
     */
    String getFillColor();

    /**
     * Set the RGB fill color value.
     */
    T setFillColor( String color );

    /**
     * Returns the fill alpha value.
     */
    double getFillAlpha();

    /**
     * Set the fill alpha value.
     */
    T setFillAlpha( double alpha );

    /**
     * Returns the RGB stroke color value.
     */
    String getStrokeColor();

    /**
     * Set the RGB stroke color value.
     */
    T setStrokeColor( String color );

    /**
     * Returns the stroke aplha value.
     */
    double getStrokeAlpha();

    /**
     * Set the stroke alpha value.
     */
    T setStrokeAlpha( double alpha );

    /**
     * Returns the stroke width value.
     */
    double getStrokeWidth();

    /**
     * Set the stroke width value.
     */
    T setStrokeWidth( double width );

    /**
     * Set the drag bounds.
     */
    T setDragBounds( double x1, double y1, double x2, double y2 );

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
     * Set the ordering index value.
     */
    T setZIndex( int zindez );

    /**
     * Returns ordering index value.
     */
    int getZIndex();

    /**
     * Removes this shape view from the parent.
     */
    void removeFromParent();

    /**
     * Destroy this shape view instance and
     * removes it from the tree.
     */
    void destroy();

}
