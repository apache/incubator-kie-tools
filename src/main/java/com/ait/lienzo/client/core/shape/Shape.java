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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.Path2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.image.ImageLoader;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.FillGradient;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PatternGradient;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Shapes are objects that can be drawn on a canvas.
 * A Shape can be added to a {@link Group} or to a {@link Layer}.
 * @param <T>
 */

public abstract class Shape<T extends Shape<T>> extends Node<T> implements IPrimitive<T>
{
    private ShapeType                 m_type;

    private String                    m_ckey;

    private final OptionalShapeFields m_opts = OptionalShapeFields.make();

    protected Shape(final ShapeType type)
    {
        super(NodeType.SHAPE);

        m_type = type;
    }

    public Shape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(NodeType.SHAPE, node, ctx);

        m_type = type;

        final Attributes attr = getAttributes();

        if (attr.hasFill())
        {
            FillGradient grad = attr.getFillGradient();

            if (null != grad)
            {
                final PatternGradient patg = grad.asPatternGradient();

                if (null != patg)
                {
                    new ImageLoader(patg.getSrc())
                    {
                        @Override
                        public void onImageElementLoad(final ImageElement elem)
                        {
                            attr.setFillGradient(new PatternGradient(elem, patg.getRepeat()));

                            batch();
                        }

                        @Override
                        public void onImageElementError(String message)
                        {
                            LienzoCore.get().error(message);
                        }
                    };
                }
            }
        }
    }

    @Override
    public T draw()
    {
        final Layer layer = getLayer();

        if (null != layer)
        {
            layer.draw();
        }
        return cast();
    }

    @Override
    public T batch()
    {
        final Layer layer = getLayer();

        if (null != layer)
        {
            layer.batch();
        }
        return cast();
    }

    /**
     * Only sub-classes that wish to extend a Shape should use this.
     * 
     * @param type
     */
    protected void setShapeType(final ShapeType type)
    {
        m_type = type;
    }

    @Override
    public IFactory<?> getFactory()
    {
        return LienzoCore.get().getFactory(m_type);
    }

    @Override
    public Map<ControlHandleType, IControlHandleList> getControlHandles(ControlHandleType... types)
    {
        return getControlHandles(Arrays.asList(types));
    }

    @Override
    public Map<ControlHandleType, IControlHandleList> getControlHandles(List<ControlHandleType> types)
    {
        if ((null == types) || (types.isEmpty()))
        {
            return null;
        }
        if (types.size() > 1)
        {
            types = new ArrayList<ControlHandleType>(new HashSet<ControlHandleType>(types));
        }
        IControlHandleFactory factory = getControlHandleFactory();

        if (null == factory)
        {
            return null;
        }
        return factory.getControlHandles(types);
    }

    @Override
    public IControlHandleFactory getControlHandleFactory()
    {
        return m_opts.getControlHandleFactory();
    }

    @Override
    public T setControlHandleFactory(IControlHandleFactory factory)
    {
        m_opts.setControlHandleFactory(factory);

        return cast();
    }

    @Override
    public T copy()
    {
        final Node<?> node = copyUnchecked();

        if (null == node)
        {
            return null;
        }
        if (NodeType.SHAPE != node.getNodeType())
        {
            return null;
        }
        final Shape<?> shape = ((Shape<?>) node);

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
    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha, BoundingBox bounds)
    {
        final Attributes attr = getAttributes();

        alpha = alpha * attr.getAlpha();

        if (alpha <= 0)
        {
            return;
        }
        if (context.isSelection())
        {
            if (dofillBoundsForSelection(context, attr, alpha))
            {
                return;
            }
        }
        else
        {
            setAppliedShadow(false);
        }
        if (prepare(context, attr, alpha))
        {
            final boolean fill = fill(context, attr, alpha);

            stroke(context, attr, alpha, fill);
        }
    }

    public PathPartList getPathPartList()
    {
        return null;
    }

    protected final void setAppliedShadow(final boolean apsh)
    {
        m_opts.setAppliedShadow(apsh);
    }

    protected final boolean isAppliedShadow()
    {
        return m_opts.isAppliedShadow();
    }

    protected abstract boolean prepare(Context2D context, Attributes attr, double alpha);

    /**
     * Fills the Shape using the passed attributes.
     * This method will silently also fill the Shape to its unique rgb color if the context is a buffer.
     * 
     * @param context
     * @param attr
     */
    protected boolean fill(final Context2D context, final Attributes attr, double alpha)
    {
        final boolean filled = attr.hasFill();

        if ((filled) || (attr.isFillShapeForSelection()))
        {
            alpha = alpha * attr.getFillAlpha();

            if (alpha <= 0)
            {
                return false;
            }
            if (context.isSelection())
            {
                final String color = getColorKey();

                if (null == color)
                {
                    return false;
                }
                context.save();

                context.setFillColor(color);

                context.fill();

                context.restore();

                return true;
            }
            if (false == filled)
            {
                return false;
            }
            context.save(getID());

            if (attr.hasShadow())
            {
                doApplyShadow(context, attr);
            }
            context.setGlobalAlpha(alpha);

            final String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                context.fill();

                context.restore();

                return true;
            }
            final FillGradient grad = attr.getFillGradient();

            if (null != grad)
            {
                final String type = grad.getType();

                if (LinearGradient.TYPE.equals(type))
                {
                    context.setFillGradient(grad.asLinearGradient());

                    context.fill();

                    context.restore();

                    return true;
                }
                else if (RadialGradient.TYPE.equals(type))
                {
                    context.setFillGradient(grad.asRadialGradient());

                    context.fill();

                    context.restore();

                    return true;
                }
                else if (PatternGradient.TYPE.equals(type))
                {
                    context.setFillGradient(grad.asPatternGradient());

                    context.fill();

                    context.restore();

                    return true;
                }
            }
            context.restore();
        }
        return false;
    }

    protected boolean dofillBoundsForSelection(final Context2D context, final Attributes attr, final double alpha)
    {
        if (attr.isFillBoundsForSelection())
        {
            if ((alpha * attr.getFillAlpha()) > 0)
            {
                final String color = getColorKey();

                if (null != color)
                {
                    final BoundingBox bbox = getBoundingBox();

                    if (null != bbox)
                    {
                        final double wide = bbox.getWidth();

                        if (wide > 0)
                        {
                            final double high = bbox.getHeight();

                            if (high > 0)
                            {
                                context.setFillColor(color);

                                final double offset = getSelectionBoundsOffset();

                                context.fillRect(bbox.getX() - offset, bbox.getY() - offset, wide + offset, high + offset);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean fill(final Context2D context, final Attributes attr, double alpha, final Path2D path)
    {
        final boolean filled = attr.hasFill();

        if ((filled) || (attr.isFillShapeForSelection()))
        {
            alpha = alpha * attr.getFillAlpha();

            if (alpha <= 0)
            {
                return false;
            }
            if (context.isSelection())
            {
                final String color = getColorKey();

                if (null == color)
                {
                    return false;
                }
                context.save();

                context.setFillColor(color);

                context.fill(path);

                context.restore();

                return true;
            }
            if (false == filled)
            {
                return false;
            }
            context.save(getID());

            if (attr.hasShadow())
            {
                doApplyShadow(context, attr);
            }
            context.setGlobalAlpha(alpha);

            final String fill = attr.getFillColor();

            if (null != fill)
            {
                context.setFillColor(fill);

                context.fill(path);

                context.restore();

                return true;
            }
            else
            {
                final FillGradient grad = attr.getFillGradient();

                if (null != grad)
                {
                    final String type = grad.getType();

                    if (LinearGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asLinearGradient());

                        context.fill(path);

                        context.restore();

                        return true;
                    }
                    else if (RadialGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asRadialGradient());

                        context.fill(path);

                        context.restore();

                        return true;
                    }
                    else if (PatternGradient.TYPE.equals(type))
                    {
                        context.setFillGradient(grad.asPatternGradient());

                        context.fill(path);

                        context.restore();

                        return true;
                    }
                }
            }
            context.restore();
        }
        return false;
    }

    /**
     * Sets the Shape Stroke parameters.
     * 
     * @param context
     * @param attr
     * @return boolean
     */
    protected boolean setStrokeParams(final Context2D context, final Attributes attr, double alpha, final boolean filled)
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
            if (filled)
            {
                return false;
            }
            color = LienzoCore.get().getDefaultStrokeColor();

            width = LienzoCore.get().getDefaultStrokeWidth();
        }
        alpha = alpha * attr.getStrokeAlpha();

        if (alpha <= 0)
        {
            return false;
        }
        double offset = 0;
        
        if (context.isSelection())
        {
            color = getColorKey();

            if (null == color)
            {
                return false;
            }
            context.save();

            offset = getSelectionStrokeOffset();
        }
        else
        {
            context.save(getID());

            context.setGlobalAlpha(alpha);
        }
        context.setStrokeColor(color);

        context.setStrokeWidth(width + offset);

        if (false == attr.hasExtraStrokeAttributes())
        {
            return true;
        }
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
    protected void stroke(final Context2D context, final Attributes attr, final double alpha, final boolean filled)
    {
        if (setStrokeParams(context, attr, alpha, filled))
        {
            if ((attr.hasShadow()) && (false == context.isSelection()))
            {
                doApplyShadow(context, attr);
            }
            context.stroke();

            context.restore();
        }
    }

    protected void stroke(final Context2D context, final Attributes attr, final double alpha, final Path2D path, final boolean filled)
    {
        if (setStrokeParams(context, attr, alpha, filled))
        {
            if ((attr.hasShadow()) && (false == context.isSelection()))
            {
                doApplyShadow(context, attr);
            }
            context.stroke(path);

            context.restore();
        }
    }

    /**
     * Applies this shape's Shadow.
     * 
     * @param context
     * @param attr
     * @return boolean
     */
    protected final void doApplyShadow(final Context2D context, final Attributes attr)
    {
        if ((false == isAppliedShadow()) && (attr.hasShadow()))
        {
            setAppliedShadow(true);

            final Shadow shadow = attr.getShadow();

            if (null != shadow)
            {
                context.setShadow(shadow);
            }
        }
    }

    @Override
    public boolean isDragging()
    {
        return m_opts.isDragging();
    }

    @Override
    public T setDragging(final boolean drag)
    {
        m_opts.setDragging(drag);

        return cast();
    }

    @Override
    public EventPropagationMode getEventPropagationMode()
    {
        return getAttributes().getEventPropagationMode();
    }

    @Override
    public T setEventPropagationMode(final EventPropagationMode mode)
    {
        getAttributes().setEventPropagationMode(mode);

        return cast();
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
    public T setDashArray(final DashArray array)
    {
        getAttributes().setDashArray(array);

        return cast();
    }

    public double getDashOffset()
    {
        return getAttributes().getDashOffset();
    }

    public T setDashOffset(final double offset)
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
    public T setDashArray(final double dash, final double... dashes)
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
     * Returns the Shape type.
     * 
     * @return {@link ShapeType}
     */
    public ShapeType getShapeType()
    {
        return m_type;
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
        return m_ckey;
    }

    protected void setColorKey(final String ckey)
    {
        m_ckey = ckey;
    }

    @Override
    public boolean removeFromParent()
    {
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final Layer layer = parent.asLayer();

            if (null != layer)
            {
                layer.remove(this);

                return true;
            }
            final GroupOf<IPrimitive<?>, ?> group = parent.asGroupOf();

            if (null != group)
            {
                group.remove(this);

                return true;
            }
        }
        return false;
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
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

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
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

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
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

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
        final Node<?> parent = getParent();

        if (null != parent)
        {
            final IContainer<?, IPrimitive<?>> container = (IContainer<?, IPrimitive<?>>) parent.asContainer();

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
    public T setX(final double x)
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
    public T setY(final double y)
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
    public T setLocation(final Point2D p)
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
    @Override
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
    public T setDraggable(final boolean draggable)
    {
        getAttributes().setDraggable(draggable);

        return cast();
    }

    @Override
    public boolean isEditable()
    {
        return getAttributes().isEditable();
    }

    @Override
    public T setEditable(final boolean editable)
    {
        getAttributes().setEditable(editable);

        return cast();
    }

    public boolean isFillShapeForSelection()
    {
        return getAttributes().isFillShapeForSelection();
    }

    public T setFillShapeForSelection(final boolean selection)
    {
        getAttributes().setFillShapeForSelection(selection);

        return cast();
    }

    public boolean isFillBoundsForSelection()
    {
        return getAttributes().isFillBoundsForSelection();
    }

    public T setFillBoundsForSelection(final boolean selection)
    {
        getAttributes().setFillBoundsForSelection(selection);

        return cast();
    }

    /**
     * Sets the number of pixels that are used to increase
     * the bounding box on the selection layer.
     */
    public final T setSelectionBoundsOffset(final double offset)
    {
        getAttributes().setSelectionBoundsOffset(offset);

        return cast();
    }

    /**
     * Gets the number of pixels that are used to increase
     * the bounding box on the selection layer.
     */
    public final double getSelectionBoundsOffset()
    {
        return getAttributes().getSelectionBoundsOffset();
    }

    /**
     * Sets the number of pixels that are used to increase
     * stroke size on the selection layer.
     */
    public final T setSelectionStrokeOffset(final double offset)
    {
        getAttributes().setSelectionStrokeOffset(offset);

        return cast();
    }

    /**
     * Gets the number of pixels that are used to increase
     * stroke size on the selection layer.
     */
    public final double getSelectionStrokeOffset()
    {
        return getAttributes().getSelectionStrokeOffset();
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
    public T setScale(final Point2D scale)
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
    public T setScale(final double xy)
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
    public T setScale(final double x, final double y)
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
    public T setRotation(final double radians)
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
    public T setRotationDegrees(final double degrees)
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
    public T setShear(final Point2D shear)
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
    public T setShear(final double x, final double y)
    {
        getAttributes().setShear(x, y);

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
    public T setOffset(final Point2D offset)
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
    public T setOffset(final double xy)
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
    public T setOffset(final double x, final double y)
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
    public T setDragConstraint(final DragConstraint constraint)
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
    public T setDragBounds(final DragBounds bounds)
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
    public T setDragMode(final DragMode mode)
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
    public T setAlpha(final double alpha)
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
    public T setFillAlpha(final double alpha)
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
    public T setStrokeAlpha(final double alpha)
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
    public T setFillColor(final String color)
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
    public T setFillColor(final IColor color)
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
    public T setFillGradient(final LinearGradient gradient)
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
    public T setFillGradient(final RadialGradient gradient)
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

    public T setFillGradient(final PatternGradient gradient)
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
    public T setStrokeColor(final String color)
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
    public T setStrokeColor(final IColor color)
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
    public T setStrokeWidth(final double width)
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
    public T setLineJoin(final LineJoin linejoin)
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

    public T setMiterLimit(final double limit)
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
    public T setLineCap(final LineCap linecap)
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
    public T setShadow(final Shadow shadow)
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
        final Layer layer = getLayer();

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
        final Layer layer = getLayer();

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
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getShapeType().getValue()));

        if (hasMetaData())
        {
            final MetaData meta = getMetaData();

            if (false == meta.isEmpty())
            {
                object.put("meta", new JSONObject(meta.getJSO()));
            }
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        return object;
    }

    @Override
    public DragConstraintEnforcer getDragConstraints()
    {
        final DragConstraintEnforcer enforcer = m_opts.getDragConstraintEnforcer();

        if (enforcer == null)
        {
            return new DefaultDragConstraintEnforcer();
        }
        else
        {
            return enforcer;
        }
    }

    @Override
    public T setDragConstraints(final DragConstraintEnforcer enforcer)
    {
        m_opts.setDragConstraintEnforcer(enforcer);

        return cast();
    }

    @Override
    public List<Attribute> getTransformingAttributes()
    {
        return LienzoCore.STANDARD_TRANSFORMING_ATTRIBUTES;
    }

    protected static abstract class ShapeFactory<S extends Shape<S>>extends NodeFactory<S>
    {
        protected ShapeFactory(final ShapeType type)
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

            addAttribute(Attribute.FILL_BOUNDS_FOR_SELECTION);

            addAttribute(Attribute.SELECTION_BOUNDS_OFFSET);

            addAttribute(Attribute.SELECTION_STROKE_OFFSET);

            addAttribute(Attribute.EVENT_PROPAGATION_MODE);
        }

        /**
         * Only factories that wish to extend other factories should use this.
         * 
         * @param type {@link ShapeType}
         */
        protected void setShapeType(final ShapeType type)
        {
            setTypeName(type.getValue());
        }
    }

    private static class OptionalShapeFields extends JavaScriptObject
    {
        public static final OptionalShapeFields make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected OptionalShapeFields()
        {
        }

        protected final native boolean isDragging()
        /*-{
			return !!this.drag;
        }-*/;

        protected final native void setDragging(boolean drag)
        /*-{
			if (false == drag) {
				delete this["drag"];
			} else {
				this.drag = drag;
			}
        }-*/;

        protected final native boolean isAppliedShadow()
        /*-{
			return !!this.apsh;
        }-*/;

        protected final native void setAppliedShadow(boolean apsh)
        /*-{
			if (false == apsh) {
				delete this["apsh"];
			} else {
				this.apsh = apsh;
			}
        }-*/;

        protected final native DragConstraintEnforcer getDragConstraintEnforcer()
        /*-{
			return this.denf;
        }-*/;

        protected final native void setDragConstraintEnforcer(DragConstraintEnforcer denf)
        /*-{
			if (null == denf) {
				delete this["denf"];
			} else {
				this.denf = denf;
			}
        }-*/;

        protected final native IControlHandleFactory getControlHandleFactory()
        /*-{
			return this.hand;
        }-*/;

        protected final native void setControlHandleFactory(IControlHandleFactory hand)
        /*-{
			if (null == hand) {
				delete this["hand"];
			} else {
				this.hand = hand;
			}
        }-*/;
    }
}
