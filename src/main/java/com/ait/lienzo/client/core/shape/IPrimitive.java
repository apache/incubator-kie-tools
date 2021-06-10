/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;

/**
 * A common interface for all primitives, i.e. {@link Shape} and {@link Group}. 
 */
public interface IPrimitive<T extends IPrimitive<T>> extends IDrawable<T>, IControlHandleFactory
{
    /**
     * Gets the X coordinate.
     * 
     * @return double
     */
    double getX();

    /**
     * Sets the X coordinate.
     * 
     * @param x
     * @return this IPrimitive
     */
    T setX(double x);

    /**
     * Gets the Y coordinate.
     * 
     * @return double
     */
    double getY();

    /**
     * Sets the Y coordinate.
     * 
     * @param y
     * @return this IPrimitive
     */
    T setY(double y);

    /**
     * Sets the X and Y attributes to P.x and P.y
     * 
     * @param p Point2D
     * @return this IPrimitive
     */
    T setLocation(Point2D p);

    /**
     * Returns the X and Y attributes as a Point2D
     * 
     * @return Point2D
     */
    Point2D getLocation();

    /**
     * Returns whether this node can be dragged.
     * 
     * @return boolean
     */
    boolean isDraggable();

    /**
     * Sets whether this node can be dragged.
     * 
     * @param draggable
     * @return this IPrimitive
     */
    T setDraggable(boolean draggable);

    /**
     * Gets this node's scaleWithXY as a Point2D
     * 
     * @return Point2D
     */
    Point2D getScale();

    /**
     * Sets the node's scaleWithXY
     * 
     * @param scale Point2D
     * @return this IPrimitive
     */
    T setScale(Point2D scale);

    /**
     * Sets the node's scaleWithXY, with the same value for x and y
     * 
     * @param xy
     * @return this IPrimitive
     */
    T setScale(double xy);

    /**
     * Sets the node's scaleWithXY, with the given x and y coordinates
     * 
     * @param x
     * @param y
     * @return this IPrimitive
     */
    T setScale(double x, double y);

    /**
     * Gets this node's rotation, in radians.
     * 
     * @return double
     */
    double getRotation();

    /**
     * Sets this node's rotation, in radians.
     * 
     * @param radians
     * @return this IPrimitive
     */
    T setRotation(double radians);

    /**
     * Gets this node's rotation, in degrees.
     * 
     * @return double
     */
    double getRotationDegrees();

    /**
     * Sets this node's rotation, in degrees.
     * 
     * @param degrees
     * @return this IPrimitive
     */
    T setRotationDegrees(double degrees);

    /**
     * Gets this node's shear as a {@link Point2D}
     * 
     * @return Point2D
     */
    Point2D getShear();

    /**
     * Sets this node's shear
     * 
     * @param shear
     * @return this IPrimitive
     */
    T setShear(Point2D shear);

    /**
     * Sets this node's shear
     * 
     * @param shear
     * @return this IPrimitive
     */
    T setShear(double shearX, double shearY);

    /**
     * Gets this node's offset as a {@link Point2D}
     * 
     * @return Point2D
     */
    Point2D getOffset();

    /**
     * Sets this node's offset
     * 
     * @param offset
     * @return this IPrimitive
     */
    T setOffset(Point2D offset);

    /**
     * Sets this node's offset, with the same value for x and y.
     * 
     * @param xy
     * @return this IPrimitive
     */
    T setOffset(double xy);

    /**
     * Sets this node's offset, at the given x and y coordinates.
     * 
     * @param x
     * @param y
     * @return this IPrimitive
     */
    T setOffset(double x, double y);

    /**
     * Gets this node's {@link DragConstraint}
     * 
     * @return DragConstraint
     */
    DragConstraint getDragConstraint();

    /**
     * Sets this node's drag constraint; e.g., horizontal, vertical or none (default)
     * 
     * @param constraint
     * @return this IPrimitive
     */
    T setDragConstraint(DragConstraint constraint);

    /**
     * Gets the {@link DragBounds} for this node.
     * 
     * @return DragBounds
     */
    DragBounds getDragBounds();

    /**
     * Sets this node's drag bounds.
     * 
     * @param bounds
     * @return this IPrimitive
     */
    T setDragBounds(DragBounds bounds);

    /**
     * Gets the {@link DragMode} for this node.
     * 
     * @return DragMode
     */
    DragMode getDragMode();

    /**
     * Sets this node's drag mode.
     * 
     * @param mode
     * @return this IPrimitive
     */
    T setDragMode(DragMode mode);

    /**
     * Gets the alpha for this node.
     * 
     * @return double
     */
    double getAlpha();

    /**
     * Sets alpha
     * 
     * @param alpha
     * @return this IPrimitive
     */
    T setAlpha(double alpha);

    /**
     * Gets the alpha for this node.
     * 
     * @return double
     */
    double getStrokeAlpha();

    /**
     * Sets alpha
     * 
     * @param alpha
     * @return this IPrimitive
     */
    T setStrokeAlpha(double alpha);

    /**
    * Gets the alpha for this node.
    * 
    * @return double
    */
    double getFillAlpha();

    /**
     * Sets alpha
     * 
     * @param alpha
     * @return this IPrimitive
     */
    T setFillAlpha(double alpha);

    /**
     * Returns the DragConstraintEnforcer for this node.
     * This may adjust the node's location during drag operations.
     * The default implementation enforces the constraints defined by
     * the dragConstraint and dragBounds properties.
     * 
     * @return DragConstraintEnforcer
     */
    DragConstraintEnforcer getDragConstraints();

    /**
     * Sets the DragConstraintEnforcer for this node.
     * This may adjust the node's location during drag operations.
     * The default implementation enforces the constraints defined by
     * the dragConstraint and dragBounds properties.
     * 
     * @param enforcer DragConstraintEnforcer
     */
    T setDragConstraints(DragConstraintEnforcer enforcer);

    /**
     * Attaches all primitives to the Layers Color Map
     */
    void attachToLayerColorMap();

    /**
     * Detaches all primitives from the Layers Color Map
     */
    void detachFromLayerColorMap();

    IControlHandleFactory getControlHandleFactory();

    T setControlHandleFactory(IControlHandleFactory factory);

    EventPropagationMode getEventPropagationMode();

    T setEventPropagationMode(EventPropagationMode mode);

    boolean isDragging();

    T setDragging(boolean dragging);
}