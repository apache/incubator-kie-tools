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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.tooling.common.api.java.util.function.Predicate;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * A Container capable of holding a collection of T objects
 */
public abstract class GroupOf <T extends IPrimitive<?>, C extends GroupOf<T, C>> extends ContainerNode<T, C>implements IPrimitive<C>, IDestroyable
{
    private GroupType                   m_type = null;

    private final OptionalGroupOfFields m_opts = OptionalGroupOfFields.make();

    /**
     * Constructor. Creates an instance of a group.
     */
    protected GroupOf(final GroupType type, final IStorageEngine<T> stor)
    {
        super(NodeType.GROUP, stor);

        m_type = type;
    }

    /**
     * Constructor. Creates an instance of a group.
     */
    protected GroupOf(final GroupType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(NodeType.GROUP, node, ctx);

        m_type = type;
    }

    @Override
    public C draw()
    {
        final Layer layer = getLayer();

        if (null != layer)
        {
            layer.draw();
        }
        return cast();
    }

    @Override
    public C batch()
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
    protected void setGroupType(final GroupType type)
    {
        m_type = type;
    }

    public GroupType getGroupType()
    {
        return m_type;
    }

    @Override
    public IFactory<?> getFactory()
    {
        return LienzoCore.get().getFactory(m_type);
    }

    @Override
    public boolean isDragging()
    {
        return m_opts.isDragging();
    }

    @Override
    public C setDragging(final boolean drag)
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
    public C setEventPropagationMode(final EventPropagationMode mode)
    {
        getAttributes().setEventPropagationMode(mode);

        return cast();
    }

    /**
     * Returns this group as an {@link IPrimitive}.
     * 
     * @return IPrimitive
     */
    @Override
    public IPrimitive<?> asPrimitive()
    {
        return this;
    }

    /**
     * Gets the X coordinate for this group.
     * 
     * @return double
     */
    @Override
    public double getX()
    {
        return getAttributes().getX();
    }

    /**
     * Sets the X coordinate for this group.
     * 
     * @param x
     * @return Group this Group
     */
    @Override
    public C setX(final double x)
    {
        getAttributes().setX(x);

        return cast();
    }

    /**
     * Gets the Y coordinate for this group.
     * 
     * @return double
     */
    @Override
    public double getY()
    {
        return getAttributes().getY();
    }

    /**
     * Sets the Y coordinate for this group.
     * 
     * @return Group this Group
     */
    @Override
    public C setY(final double y)
    {
        getAttributes().setY(y);

        return cast();
    }

    /**
     * Sets the X and Y attributes to P.x and P.y
     * 
     * @param p Point2D
     * @return Group this Group
     */
    @Override
    public C setLocation(final Point2D p)
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
     * Gets the alpha value (transparency) for this group.
     * 
     * @return double between 0 and 1
     */
    @Override
    public double getAlpha()
    {
        return getAttributes().getAlpha();
    }

    /**
     * Sets the alpha value (transparency) on this group.
     * 
     * @param alpha between 0 and 1
     * @return Group this Group
     */
    @Override
    public C setAlpha(final double alpha)
    {
        getAttributes().setAlpha(alpha);

        return cast();
    }

    /**
     * Gets the alpha value (transparency) for this group.
     * 
     * @return double between 0 and 1
     */
    @Override
    public double getFillAlpha()
    {
        return getAttributes().getFillAlpha();
    }

    /**
     * Sets the alpha value (transparency) on this group.
     * 
     * @param alpha between 0 and 1
     * @return Group this Group
     */
    @Override
    public C setFillAlpha(final double alpha)
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
    public C setStrokeAlpha(final double alpha)
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
     * Returns whether this group can be dragged.
     * 
     * @return boolean 
     */
    @Override
    public boolean isDraggable()
    {
        return getAttributes().isDraggable();
    }

    /**
     * Sets if this group can be dragged.
     * 
     * @param draggable true if the group can be dragged; false otherwise
     * @return Group this Group
     */
    @Override
    public C setDraggable(final boolean draggable)
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
    public C setEditable(final boolean editable)
    {
        getAttributes().setEditable(editable);

        return cast();
    }

    /**
     * Gets the group's scale.
     * 
     * @return {@link Point2D}
     */
    @Override
    public Point2D getScale()
    {
        return getAttributes().getScale();
    }

    /**
     * Sets the group's scale, starting at the given point.
     * 
     * @param scale
     * @return Group this Group
     */
    @Override
    public C setScale(final Point2D scale)
    {
        getAttributes().setScale(scale);

        return cast();
    }

    /**
     * Sets this group's scale, with the same value for x and y.
     * 
     * @param xy
     * @return Group this Group
     */
    @Override
    public C setScale(final double xy)
    {
        getAttributes().setScale(xy);

        return cast();
    }

    /**
     * Sets this gruop's scale, starting at the given x and y
     * 
     * @param x
     * @param y
     * @return Group this Group
     */
    @Override
    public C setScale(final double x, final double y)
    {
        getAttributes().setScale(x, y);

        return cast();
    }

    /**
     * Gets this group's rotation, in radians.
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
     * @return Group this Group
     */
    @Override
    public C setRotation(final double radians)
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
     * @return Group this Group
     */
    @Override
    public C setRotationDegrees(final double degrees)
    {
        getAttributes().setRotationDegrees(degrees);

        return cast();
    }

    /**
     * Gets this group's offset as a {@link Point2D}
     * 
     * @return Point2D
     */
    @Override
    public Point2D getOffset()
    {
        return getAttributes().getOffset();
    }

    /**
     * Gets this group's shear as a {@link Point2D}
     * 
     * @return Point2D
     */
    @Override
    public Point2D getShear()
    {
        return getAttributes().getShear();
    }

    /**
     * Sets this group's shear
     * 
     * @param offset
     * @return T
     */
    @Override
    public C setShear(final Point2D shear)
    {
        getAttributes().setShear(shear);

        return cast();
    }

    /**
     * Sets this group's shear
     * 
     * @param offset
     * @return T
     */
    @Override
    public C setShear(final double x, final double y)
    {
        getAttributes().setShear(x, y);

        return cast();
    }

    /**
     * Sets this group's offset
     * 
     * @param offset
     * @return Group this Group
     */
    @Override
    public C setOffset(final Point2D offset)
    {
        getAttributes().setOffset(offset);

        return cast();
    }

    /**
     * Sets this group's offset, with the same value for x and y.
     * 
     * @param xy
     * @return Group this Group
     */
    @Override
    public C setOffset(final double xy)
    {
        getAttributes().setOffset(xy);

        return cast();
    }

    /**
     * Sets this group's offset, at the given x and y coordinates.
     * 
     * @param x
     * @param y
     * @return Group this Group
     */
    @Override
    public C setOffset(final double x, final double y)
    {
        getAttributes().setOffset(x, y);

        return cast();
    }

    /**
     * Gets this group's {@link DragConstraint}
     * 
     * @return DragConstraint
     */
    @Override
    public DragConstraint getDragConstraint()
    {
        return getAttributes().getDragConstraint();
    }

    /**
     * Sets this group's drag constraint, 
     * e.g. horizontal, vertical or none (default)
     * 
     * @param constraint
     * @return Group this Group
     */
    @Override
    public C setDragConstraint(final DragConstraint constraint)
    {
        getAttributes().setDragConstraint(constraint);

        return cast();
    }

    /**
     * Gets the {@link DragBounds} for this group.
     * 
     * @return DragBounds
     */
    @Override
    public DragBounds getDragBounds()
    {
        return getAttributes().getDragBounds();
    }

    /**
     * Sets this group's drag bounds.
     * 
     * @param bounds
     * @return Group this Group
     */
    @Override
    public C setDragBounds(final DragBounds bounds)
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
     * @return Group this Group
     */
    @Override
    public C setDragMode(final DragMode mode)
    {
        getAttributes().setDragMode(mode);

        return cast();
    }

    /**
     * Returns this group as a {@link IContainer}
     * 
     * @return IContainer<IPrimitive>
     */
    @Override
    public IContainer<C, T> asContainer()
    {
        return cast();
    }

    @Override
    public GroupOf<IPrimitive<?>, ?> asGroupOf()
    {
        return cast();
    }

    @Override
    public Group asGroup()
    {
        if (this instanceof Group)
        {
            return cast();
        }
        return null;
    }

    /**
     * Adds a primitive to the collection. Override to ensure primitive is put in Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public C add(final T child)
    {
        child.removeFromParent();

        super.add(child);

        child.attachToLayerColorMap();

        return cast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public C add(final T child, final T... children)
    {
        add(child);

        for (T node : children)
        {
            add(node);
        }
        return cast();
    }

    @Override
    public boolean removeFromParent()
    {
        Node<?> parent = getParent();

        if (null != parent)
        {
            Layer layer = parent.asLayer();

            if (null != layer)
            {
                layer.remove(this);

                return true;
            }
            GroupOf<IPrimitive<?>, ?> group = parent.asGroupOf();

            if (null != group)
            {
                group.remove(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Removes a primitive from the container. Override to ensure primitive is removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public C remove(final T child)
    {
        child.detachFromLayerColorMap();

        super.remove(child);

        return cast();
    }

    /**
     * Removes all primitives from the collection. Override to ensure all primitives are removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public C removeAll()
    {
        detachFromLayerColorMap();

        super.removeAll();

        return cast();
    }

    @Override
    public void destroy() {
        destroy(this);
    }

    public static <T extends IPrimitive<?>, C extends GroupOf<T, C>> void destroy(final GroupOf<T, C> group) {
        final NFastArrayList<T> children = group.getChildNodes();
        for (final T child : children) {
            if (child instanceof IDestroyable) {
                ((IDestroyable) child).destroy();
            }
        }
        group.removeAll();
        group.removeFromParent();
    }

    /**
     * Attaches all primitives to the Layers Color Map
     */
    @Override
    public void attachToLayerColorMap()
    {
        final Layer layer = getLayer();

        if (null != layer)
        {
            final NFastArrayList<T> list = getChildNodes();

            if (null != list)
            {
                final int size = list.size();

                for (int i = 0; i < size; i++)
                {
                    list.get(i).attachToLayerColorMap();
                }
            }
        }
    }

    /**
     * Detaches all primitives from the Layers Color Map
     */
    @Override
    public void detachFromLayerColorMap()
    {
        final Layer layer = getLayer();

        if (null != layer)
        {
            final NFastArrayList<T> list = getChildNodes();

            if (null != list)
            {
                final int size = list.size();

                for (int i = 0; i < size; i++)
                {
                    list.get(i).detachFromLayerColorMap();
                }
            }
        }
    }

    /**
     * Serialize this group as a {@link JSONObject}.
     * 
     * @return JSONObject
     */
    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject object = new JSONObject();

        object.put("type", new JSONString(getGroupType().getValue()));

        if (hasMetaData())
        {
            final MetaData meta = getMetaData();

            if (false == meta.isEmpty())
            {
                object.put("meta", new JSONObject(meta.getJSO()));
            }
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        final NFastArrayList<T> list = getChildNodes();

        final JSONArray children = new JSONArray();

        if (list != null)
        {
            final int size = list.size();

            for (int i = 0; i < size; i++)
            {
                final T prim = list.get(i);

                if (null != prim)
                {
                    JSONObject make = prim.toJSONObject();

                    if (null != make)
                    {
                        children.set(children.size(), make);
                    }
                }
            }
        }
        object.put("children", children);

        object.put("storage", getStorageEngine().toJSONObject());

        return object;
    }

    /**
     * Moves this group's {@link Layer} one level up
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveUp()
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
     * Moves this group's {@link Layer} one level down
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveDown()
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
     * Moves this group's {@link Layer} to the top of the layer stack.
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveToTop()
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
     * Moves this group's {@link Layer} to the bottom of the layer stack.
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public C moveToBottom()
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

    @Override
    protected void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff)
    {
        if (predicate.test(this))
        {
            buff.add(this);
        }
        final NFastArrayList<T> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            final T prim = list.get(i);

            if (null != prim)
            {
                final Node<?> node = prim.asNode();

                if (null != node)
                {
                    if (predicate.test(node))
                    {
                        buff.add(node);
                    }
                    final ContainerNode<?, ?> cont = node.asContainerNode();

                    if (null != cont)
                    {
                        cont.find(predicate, buff);
                    }
                }
            }
        }
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
    public C setDragConstraints(final DragConstraintEnforcer enforcer)
    {
        m_opts.setDragConstraintEnforcer(enforcer);

        return cast();
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
    public C setControlHandleFactory(IControlHandleFactory factory)
    {
        m_opts.setControlHandleFactory(factory);

        return cast();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return new ArrayList<Attribute>();
    }

    @Override
    public List<Attribute> getTransformingAttributes()
    {
        return LienzoCore.STANDARD_TRANSFORMING_ATTRIBUTES;
    }

    @Override
    public C refresh()
    {
        final NFastArrayList<T> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            list.get(i).refresh();
        }
        return cast();
    }

    protected static abstract class GroupOfFactory <T extends IPrimitive<?>, C extends GroupOf<T, C>> extends ContainerNodeFactory<C>
    {
        protected GroupOfFactory(final GroupType type)
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

            addAttribute(Attribute.EVENT_PROPAGATION_MODE);
        }

        protected void setGroupType(final GroupType type)
        {
            setTypeName(type.getValue());
        }
    }

    private static class OptionalGroupOfFields extends JavaScriptObject
    {
        public static final OptionalGroupOfFields make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected OptionalGroupOfFields()
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
