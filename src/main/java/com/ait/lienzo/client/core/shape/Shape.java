/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TweeningAnimation;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Shapes are objects that can be drawn on a canvas.
 * A Shape can be added to a {@link Group} or to a {@link Layer}.
 * @param <T>
 */

public abstract class Shape<T extends Shape<T>> extends Node<T> implements IPrimitive<T>, IJSONSerializable<T>
{
    private ShapeType              m_type;

    private boolean                m_apsh = false;

    private boolean                m_fill = false;

    private final String           m_hkey = Color.getHEXColorKey();

    private DragConstraintEnforcer m_dragConstraintEnforcer;

    protected Shape(ShapeType type)
    {
        super(NodeType.SHAPE);

        m_type = type;
    }

    public Shape(ShapeType type, JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(NodeType.SHAPE, node, ctx);

        m_type = type;
    }

    /**
     * Only sub-classes that wish to extend a Shape should use this.
     * 
     * @param type
     */
    protected void setShapeType(ShapeType type)
    {
        m_type = type;
    }

    @Override
    public T copy()
    {
        Node<?> node = copyUnchecked();

        if (null == node)
        {
            return null;
        }
        if (NodeType.SHAPE != node.getNodeType())
        {
            return null;
        }
        Shape<?> shape = ((Shape<?>) node);

        if (getShapeType() != shape.getShapeType())
        {
            return null;
        }
        return shape.cast();
    }

    /**
     * Used internally. Draws the node in the current Context2D
     * without applying the transformation-related attributes 
     * (e.g. X, Y, ROTATION, SCALE, SHEAR, OFFSET and TRANSFORM.)
     * <p>
     * Shapes should apply the non-Transform related attributes (such a colors, strokeWidth etc.)
     * and draw the Shape's details (such as the the actual lines and fills.)
     */
    protected void drawWithoutTransforms(Context2D context)
    {
        double alpha = getGlobalAlpha();

        if (alpha <= 0)
        {
            return;
        }
        m_apsh = false;

        m_fill = false;

        Attributes attr = getAttributes();

        if (prepare(context, attr, alpha))
        {
            fill(context, attr, alpha);

            stroke(context, attr, alpha);
        }
    }

    public abstract BoundingBox getBoundingBox();

    protected final void setWasFilledFlag(boolean fill)
    {
        m_fill = fill;
    }

    protected final boolean getWasFilledFlag()
    {
        return m_fill;
    }

    protected abstract boolean prepare(Context2D context, Attributes attr, double alpha);

    /**
     * Fills the Shape using the passed attributes.
     * This method will silently also fill the Shape to its unique rgb color if the context is a buffer.
     * 
     * @param context
     * @param attr
     */
    protected void fill(Context2D context, Attributes attr, double alpha)
    {
        boolean filled = attr.isDefined(Attribute.FILL);

        if ((filled) || (attr.isFillShapeForSelection()))
        {
            if (context.isSelection())
            {
                context.save();

                context.setGlobalAlpha(1);

                context.setFillColor(getColorKey());

                context.fill();

                context.restore();

                setWasFilledFlag(true);

                return;
            }
            if (false == filled)
            {
                return;
            }
            context.save();

            doApplyShadow(context, attr);

            context.setGlobalAlpha(alpha * getFillAlpha());

            String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                context.fill();

                setWasFilledFlag(true);
            }
            else
            {
                FillGradient grad = attr.getFillGradient();

                if (null != grad)
                {
                    if (LinearGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asLinearGradient());

                        context.fill();

                        setWasFilledFlag(true);
                    }
                    else if (RadialGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asRadialGradient());

                        context.fill();

                        setWasFilledFlag(true);
                    }
                    else if (PatternGradient.TYPE.equals(grad.getType()))
                    {
                        context.setFillGradient(grad.asPatternGradient());

                        context.fill();

                        setWasFilledFlag(true);
                    }
                }
            }
            context.restore();
        }
    }

    /**
     * Sets the Shape Stroke parameters.
     * 
     * @param context
     * @param attr
     * @return boolean
     */
    protected boolean setStrokeParams(Context2D context, Attributes attr, double alpha)
    {
        double width = attr.getStrokeWidth();

        String color = attr.getStrokeColor();

        if (null == color)
        {
            if (width > 0)
            {
                color = LienzoCore.get().getDefaultStrokeColor();
            }
        }
        else if (width <= 0)
        {
            width = LienzoCore.get().getDefaultStrokeWidth();
        }
        if ((null == color) && (width <= 0))
        {
            if (getWasFilledFlag())
            {
                return false;
            }
            color = LienzoCore.get().getDefaultStrokeColor();

            width = LienzoCore.get().getDefaultStrokeWidth();
        }
        if (context.isSelection())
        {
            color = getColorKey();

            context.setGlobalAlpha(1);
        }
        else
        {
            context.setGlobalAlpha(alpha * getStrokeAlpha());
        }
        context.setStrokeColor(color);

        context.setStrokeWidth(width);

        boolean isdashed = false;

        if (attr.isDefined(Attribute.DASH_ARRAY))
        {
            if (LienzoCore.get().isLineDashSupported())
            {
                DashArray dash = attr.getDashArray();

                if ((null != dash) && (dash.size() > 0))
                {
                    context.setLineDash(dash);

                    if (attr.isDefined(Attribute.DASH_OFFSET))
                    {
                        context.setLineDashOffset(attr.getDashOffset());
                    }
                    isdashed = true;
                }
            }
        }
        if ((isdashed) || (doStrokeExtraProperties()))
        {
            if (attr.isDefined(Attribute.LINE_JOIN))
            {
                context.setLineJoin(attr.getLineJoin());
            }
            if (attr.isDefined(Attribute.LINE_CAP))
            {
                context.setLineCap(attr.getLineCap());
            }
            if (attr.isDefined(Attribute.MITER_LIMIT))
            {
                context.setMiterLimit(attr.getMiterLimit());
            }
        }
        return true;
    }

    protected boolean doStrokeExtraProperties()
    {
        return true;
    }

    /**
     * Sets the Shape stroke.
     * 
     * @param context
     * @param attr
     */
    protected void stroke(Context2D context, Attributes attr, double alpha)
    {
        context.save();

        if (setStrokeParams(context, attr, alpha))
        {
            if (context.isSelection())
            {
                context.stroke();
            }
            else
            {
                doApplyShadow(context, attr);

                context.stroke();
            }
        }
        context.restore();
    }

    /**
     * Applies this shape's Shadow.
     * 
     * @param context
     * @param attr
     * @return boolean
     */
    protected final void doApplyShadow(Context2D context, Attributes attr)
    {
        if ((m_apsh == false) && (attr.isDefined(Attribute.SHADOW)))
        {
            m_apsh = true;

            Shadow shadow = attr.getShadow();

            if (null != shadow)
            {
                context.setShadow(shadow);
            }
        }
    }

    /**
     * Gets the {@link DashArray}. If this is a solid line, the dash array is empty.
     * 
     * @return {@link DashArray} if this line is not dashed, there will be no elements in the {@link DashArray}
     */
    public DashArray getDashArray()
    {
        return getAttributes().getDashArray();
    }

    /**
     * Sets the dash array. 
     * 
     * @param array contains dash lengths
     * @return this Line
     */
    public T setDashArray(DashArray array)
    {
        getAttributes().setDashArray(array);

        return cast();
    }

    public double getDashOffset()
    {
        return getAttributes().getDashOffset();
    }

    public T setDashOffset(double offset)
    {
        getAttributes().setDashOffset(offset);

        return cast();
    }

    /**
     * Sets the dash array with individual dash lengths.
     * 
     * @param dash length of dash
     * @param dashes if specified, length of remaining dashes
     * @return this Line
     */
    public T setDashArray(double dash, double... dashes)
    {
        getAttributes().setDashArray(new DashArray(dash, dashes));

        return cast();
    }

    /**
     * Returns this shape cast as an {@link IPrimitive}
     * 
     * @return IPrimitive
     */
    @Override
    public IPrimitive<?> asPrimitive()
    {
        return this;
    }

    @Override
    public Shape<?> asShape()
    {
        return this;
    }

    /**
     * Returns the parent.
     * 
     * @return Node
     */
    @Override
    public Node<?> getParent()
    {
        return super.getParent();
    }

    /**
     * Returns the Shape type.
     * 
     * @return {@link ShapeType}
     */
    public ShapeType getShapeType()
    {
        return m_type;
    }

    /**
     * Returns global alpha value.
     * 
     * @return double
     */
    public final double getGlobalAlpha()
    {
        double alpha = 1;

        Node<?> node = this;

        while (null != node)
        {
            Attributes attr = node.getAttributes();

            if (attr.isDefined(Attribute.ALPHA))
            {
                alpha = alpha * attr.getAlpha();
            }
            node = node.getParent();
        }
        return alpha;
    }

    @Override
    public final double getFillAlpha()
    {
        return getAttributes().getFillAlpha();
    }

    /**
     * Returns unique RGB color assigned to the off-set Shape.
     * 
     * @return String
     */
    public String getColorKey()
    {
        return m_hkey;
    }

    /**
     * Moves this shape one layer up.
     * 
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public T moveUp()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container)
            {
                container.moveUp(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape one layer down.
     * 
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public T moveDown()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container)
            {
                container.moveDown(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape to the top of the layers stack.
     * 
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public T moveToTop()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container)
            {
                container.moveToTop(this);
            }
        }
        return cast();
    }

    /**
     * Moves this shape to the bottomw of the layers stack.
     * 
     * @return T
     */
    @SuppressWarnings("unchecked")
    @Override
    public T moveToBottom()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

            if (null != container)
            {
                container.moveToBottom(this);
            }
        }
        return cast();
    }

    /**
     * Gets the x coordinate for this shape.
     * 
     * @return double
     */
    @Override
    public double getX()
    {
        return getAttributes().getX();
    }

    /**
     * Sets the x coordinate for this shape.
     * 
     * @param x
     * @return T
     */
    @Override
    public T setX(double x)
    {
        getAttributes().setX(x);

        return cast();
    }

    /**
     * Gets the y coordinate for this shape.
     * 
     * @return double
     */
    @Override
    public double getY()
    {
        return getAttributes().getY();
    }

    /**
     * Sets the y coordinate for this shape.
     * 
     * @param y
     * @return T
     */
    @Override
    public T setY(double y)
    {
        getAttributes().setY(y);

        return cast();
    }

    /**
     * Sets the X and Y attributes to P.x and P.y
     * 
     * @param p Point2D
     * @return this Shape
     */
    @Override
    public T setLocation(Point2D p)
    {
        setX(p.getX());

        setY(p.getY());

        return cast();
    }

    /**
     * Returns the X and Y attributes as a Point2D
     * 
     * @return Point2D
     */
    public Point2D getLocation()
    {
        return new Point2D(getX(), getY());
    }

    /**
     * Returns true if this shape can be dragged; false otherwise.
     * 
     * @return boolean
     */
    @Override
    public boolean isDraggable()
    {
        return getAttributes().isDraggable();
    }

    /**
     * Sets if this shape can be dragged or not.
     * 
     * @return T
     */
    @Override
    public T setDraggable(boolean draggable)
    {
        getAttributes().setDraggable(draggable);

        return cast();
    }

    public boolean isEditable()
    {
        return getAttributes().isEditable();
    }

    public T setEditable(boolean editable)
    {
        getAttributes().setEditable(editable);

        return cast();
    }

    public boolean isFillShapeForSelection()
    {
        return getAttributes().isFillShapeForSelection();
    }

    public T setFillShapeForSelection(boolean selection)
    {
        getAttributes().setFillShapeForSelection(selection);

        return cast();
    }

    /**
     * Gets this shape's scale.
     * 
     * @return double
     */
    @Override
    public Point2D getScale()
    {
        return getAttributes().getScale();
    }

    /**
     * Sets this shape's scale, starting at the given point.
     * 
     * @param scale
     * @return T
     */
    @Override
    public T setScale(Point2D scale)
    {
        getAttributes().setScale(scale);

        return cast();
    }

    /**
     * Sets this shape's scale, with the same value for x and y.
     * 
     * @param xy
     * @return T
     */
    @Override
    public T setScale(double xy)
    {
        getAttributes().setScale(xy);

        return cast();
    }

    /**
     * Sets this shape's scale, starting at the given x and y
     * 
     * @param x
     * @param y
     * @return T
     */
    @Override
    public T setScale(double x, double y)
    {
        getAttributes().setScale(x, y);

        return cast();
    }

    /**
     * Gets this shape's rotation, in radians.
     * 
     * @return double
     */
    @Override
    public double getRotation()
    {
        return getAttributes().getRotation();
    }

    /**
     * Sets this group's rotation, in radians.
     * 
     * @param radians
     * @return T
     */
    @Override
    public T setRotation(double radians)
    {
        getAttributes().setRotation(radians);

        return cast();
    }

    /**
     * Gets this group's rotation, in degrees.
     * 
     * @return double
     */
    @Override
    public double getRotationDegrees()
    {
        return getAttributes().getRotationDegrees();
    }

    /**
     * Sets this group's rotation, in degrees.
     * 
     * @param degrees
     * @return T
     */
    @Override
    public T setRotationDegrees(double degrees)
    {
        getAttributes().setRotationDegrees(degrees);

        return cast();
    }

    /**
     * Gets this shape's shear as a {@link Point2D}
     * 
     * @return Point2D
     */
    @Override
    public Point2D getShear()
    {
        return getAttributes().getShear();
    }

    /**
     * Sets this shape's shear
     * 
     * @param offset
     * @return T
     */
    @Override
    public T setShear(Point2D shear)
    {
        getAttributes().setShear(shear);

        return cast();
    }

    /**
     * Sets this shape's shear
     * 
     * @param offset
     * @return T
     */
    @Override
    public T setShear(double shearX, double shearY)
    {
        getAttributes().setShear(shearX, shearY);

        return cast();
    }

    /**
     * Gets this shape's offset as a {@link Point2D}
     * 
     * @return Point2D
     */
    @Override
    public Point2D getOffset()
    {
        return getAttributes().getOffset();
    }

    /**
     * Sets this shape's offset
     * 
     * @param offset
     * @return T
     */
    @Override
    public T setOffset(Point2D offset)
    {
        getAttributes().setOffset(offset);

        return cast();
    }

    /**
     * Sets this shape's offset, with the same value for x and y.
     * 
     * @param xy
     * @return T
     */
    @Override
    public T setOffset(double xy)
    {
        getAttributes().setOffset(xy);

        return cast();
    }

    /**
     * Sets this shape's offset, at the given x and y coordinates.
     * 
     * @param x
     * @param y
     * @return T
     */
    @Override
    public T setOffset(double x, double y)
    {
        getAttributes().setOffset(x, y);

        return cast();
    }

    /**
     * Gets this shape's {@link DragConstraint}
     * 
     * @return DragConstraint
     */
    @Override
    public DragConstraint getDragConstraint()
    {
        return getAttributes().getDragConstraint();
    }

    /**
     * Sets this shape's drag constraint; e.g., horizontal, vertical or none (default)
     * 
     * @param constraint
     * @return T
     */
    @Override
    public T setDragConstraint(DragConstraint constraint)
    {
        getAttributes().setDragConstraint(constraint);

        return cast();
    }

    /**
     * Gets the {@link DragBounds} for this shape.
     * 
     * @return DragBounds
     */
    @Override
    public DragBounds getDragBounds()
    {
        return getAttributes().getDragBounds();
    }

    /**
     * Sets this shape's drag bounds.
     * 
     * @param bounds
     * @return T
     */
    @Override
    public T setDragBounds(DragBounds bounds)
    {
        getAttributes().setDragBounds(bounds);

        return cast();
    }

    /**
     * Gets the {@link DragMode} for this node.
     * 
     * @return DragMode
     */
    @Override
    public DragMode getDragMode()
    {
        return getAttributes().getDragMode();
    }

    /**
     * Sets this node's drag mode.
     * 
     * @param mode
     * @return T
     */
    @Override
    public T setDragMode(DragMode mode)
    {
        getAttributes().setDragMode(mode);

        return cast();
    }

    /**
     * Gets the alpha value for this shape.
     * 
     * @return double
     */
    @Override
    public double getAlpha()
    {
        return getAttributes().getAlpha();
    }

    /**
     * Sets the alpha color on this shape.
     * 
     * @param alpha
     * @return T
     */
    @Override
    public T setAlpha(double alpha)
    {
        getAttributes().setAlpha(alpha);

        return cast();
    }

    /**
     * Sets the alpha color on this shape.
     * 
     * @param alpha
     * @return T
     */
    @Override
    public T setFillAlpha(double alpha)
    {
        getAttributes().setFillAlpha(alpha);

        return cast();
    }

    /**
     * Sets the alpha color on this shape.
     * 
     * @param alpha
     * @return T
     */
    @Override
    public T setStrokeAlpha(double alpha)
    {
        getAttributes().setStrokeAlpha(alpha);

        return cast();
    }

    /**
     * Gets the alpha value for this shape.
     * 
     * @return double
     */
    @Override
    public double getStrokeAlpha()
    {
        return getAttributes().getStrokeAlpha();
    }

    /**
     * Gets the fill color in hex.
     * 
     * @return String
     */
    public String getFillColor()
    {
        return getAttributes().getFillColor();
    }

    /**
     * Sets the fill color.
     * 
     * @param color in hex
     * @return T
     */
    public T setFillColor(String color)
    {
        getAttributes().setFillColor(color);

        return cast();
    }

    /**
     * Sets the fill color.
     * 
     * @param color ColorName
     * @return T
     */
    public T setFillColor(IColor color)
    {
        return setFillColor(null == color ? null : color.getColorString());
    }

    /**
     * Returns the fill gradient.
     * 
     * @return FillGradient i.e. {@link LinearGradient}, {@link RadialGradient}
     *                  or {@link PatternGradient}
     */
    public FillGradient getFillGradient()
    {
        return getAttributes().getFillGradient();
    }

    /**
     * Sets the gradient fill.
     * 
     * @param gradient a {@link LinearGradient}
     * @return T
     */
    public T setFillGradient(LinearGradient gradient)
    {
        getAttributes().setFillGradient(gradient);

        return cast();
    }

    /**
     * Sets the gradient fill.
     * 
     * @param gradient a {@link RadialGradient}
     * @return T
     */
    public T setFillGradient(RadialGradient gradient)
    {
        getAttributes().setFillGradient(gradient);

        return cast();
    }

    /**
     * Sets the gradient fill.
     * 
     * @param gradient a {@link PatternGradient}
     * @return T
     */

    public T setFillGradient(PatternGradient gradient)
    {
        getAttributes().setFillGradient(gradient);

        return cast();
    }

    /**
     * Gets the stroke color for this shape.
     * 
     * @return String color in hex
     */
    public String getStrokeColor()
    {
        return getAttributes().getStrokeColor();
    }

    /**
     * Sets the stroke color.
     * 
     * @param color in hex
     * @return T
     */
    public T setStrokeColor(String color)
    {
        getAttributes().setStrokeColor(color);

        return cast();
    }

    /**
     * Sets the stroke color.
     * 
     * @param color Color or ColorName
     * @return T
     */
    public T setStrokeColor(IColor color)
    {
        return setStrokeColor(null == color ? null : color.getColorString());
    }

    /**
     * Gets the stroke width.
     * 
     * @return double
     */
    public double getStrokeWidth()
    {
        return getAttributes().getStrokeWidth();
    }

    /**
     * Sets the stroke width for this shape.
     * 
     * @param width
     * @return T
     */
    public T setStrokeWidth(double width)
    {
        getAttributes().setStrokeWidth(width);

        return cast();
    }

    /**
     * Gets the type of {@link LineJoin} for this shape.
     * 
     * @return {@link LineJoin}
     */
    public LineJoin getLineJoin()
    {
        return getAttributes().getLineJoin();
    }

    /**
     * Sets the type of {@link LineJoin} for this shape.
     * 
     * @param linejoin
     * @return T
     */
    public T setLineJoin(LineJoin linejoin)
    {
        getAttributes().setLineJoin(linejoin);

        return cast();
    }

    /**
     * Sets the value of Miter Limit for this shape.
     * 
     * @param limit
     * @return T
     */

    public T setMiterLimit(double limit)
    {
        getAttributes().setMiterLimit(limit);

        return cast();
    }

    /**
     * Gets the type of Miter Limit for this shape.
     * 
     * @return double
     */

    public double getMiterLimit()
    {
        return getAttributes().getMiterLimit();
    }

    /**
     * Gets the type of {@link LineCap} for this shape.
     * 
     * @return {@link LineCap}
     */
    public LineCap getLineCap()
    {
        return getAttributes().getLineCap();
    }

    /**
     * Sets the type of {@link LineCap} for this shape.
     * 
     * @param linecap
     * @return T
     */
    public T setLineCap(LineCap linecap)
    {
        getAttributes().setLineCap(linecap);

        return cast();
    }

    /**
     * Gets this shape's {@link Shadow}
     * 
     * @return Shadow
     */
    public Shadow getShadow()
    {
        return getAttributes().getShadow();
    }

    /**
     * Sets this shape's {@link Shadow}
     * 
     * @param shadow
     * @return T
     */
    public T setShadow(Shadow shadow)
    {
        getAttributes().setShadow(shadow);

        return cast();
    }

    /**
     * Attaches this Shape to the Layers Color Map
     */
    @Override
    public void attachToLayerColorMap()
    {
        Layer layer = getLayer();

        if (null != layer)
        {
            layer.attachShapeToColorMap(this);
        }
    }

    /**
     * Detaches this Shape from the Layers Color Map
     */
    @Override
    public void detachFromLayerColorMap()
    {
        Layer layer = getLayer();

        if (null != layer)
        {
            layer.detachShapeFromColorMap(this);
        }
    }

    /**
     * Serializes this shape as a {@link JSONObject}
     * 
     * @return JSONObject
     */
    @Override
    public JSONObject toJSONObject()
    {
        JSONObject object = new JSONObject();

        object.put("type", new JSONString(getShapeType().getValue()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        return object;
    }

    @Override
    public IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */)
    {
        return new TweeningAnimation(this, tweener, properties, duration, null).run();
    }

    @Override
    public IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */, IAnimationCallback callback)
    {
        return new TweeningAnimation(this, tweener, properties, duration, callback).run();
    }

    @Override
    public DragConstraintEnforcer getDragConstraints()
    {
        if (m_dragConstraintEnforcer == null)
        {
            return new DefaultDragConstraintEnforcer();
        }
        else
        {
            return m_dragConstraintEnforcer;
        }
    }

    @Override
    public void setDragConstraints(DragConstraintEnforcer enforcer)
    {
        m_dragConstraintEnforcer = enforcer;
    }

    public static abstract class ShapeFactory<S extends Shape<S>> extends NodeFactory<S>
    {
        protected ShapeFactory(ShapeType type)
        {
            super(type.getValue());

            addAttribute(Attribute.X);

            addAttribute(Attribute.Y);

            addAttribute(Attribute.ALPHA);

            addAttribute(Attribute.FILL);

            addAttribute(Attribute.FILL_ALPHA);

            addAttribute(Attribute.STROKE);

            addAttribute(Attribute.STROKE_WIDTH);

            addAttribute(Attribute.STROKE_ALPHA);

            addAttribute(Attribute.DRAGGABLE);

            addAttribute(Attribute.EDITABLE);

            addAttribute(Attribute.SCALE);

            addAttribute(Attribute.SHEAR);

            addAttribute(Attribute.ROTATION);

            addAttribute(Attribute.OFFSET);

            addAttribute(Attribute.SHADOW);

            addAttribute(Attribute.LINE_CAP);

            addAttribute(Attribute.LINE_JOIN);

            addAttribute(Attribute.MITER_LIMIT);

            addAttribute(Attribute.DRAG_CONSTRAINT);

            addAttribute(Attribute.DRAG_BOUNDS);

            addAttribute(Attribute.DRAG_MODE);

            addAttribute(Attribute.DASH_ARRAY);

            addAttribute(Attribute.DASH_OFFSET);

            addAttribute(Attribute.FILL_SHAPE_FOR_SELECTION);
        }

        /**
         * Only factories that wish to extend other factories should use this.
         * 
         * @param type {@link ShapeType}
         */
        protected void setShapeType(ShapeType type)
        {
            setTypeName(type.getValue());
        }
    }
}
