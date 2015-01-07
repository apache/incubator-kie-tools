/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.LinkedHashSet;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TweeningAnimation;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.java.util.function.Predicate;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * A Container capable of holding a collection of {@link IPrimitive} objects
 */
public class Group extends ContainerNode<IPrimitive<?>, Group> implements IPrimitive<Group>, IJSONSerializable<Group>
{
    private DragConstraintEnforcer m_dragConstraintEnforcer;

    /**
     * Constructor. Creates an instance of a group.
     */
    public Group()
    {
        super(NodeType.GROUP);
    }

    /**
     * Constructor. Creates an instance of a group.
     */
    protected Group(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(NodeType.GROUP, node, ctx);
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
    public Group setX(final double x)
    {
        getAttributes().setX(x);

        return this;
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
    public Group setY(final double y)
    {
        getAttributes().setY(y);

        return this;
    }

    /**
     * Sets the X and Y attributes to P.x and P.y
     * 
     * @param p Point2D
     * @return Group this Group
     */
    @Override
    public Group setLocation(final Point2D p)
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
    public Group setAlpha(final double alpha)
    {
        getAttributes().setAlpha(alpha);

        return this;
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
    public Group setFillAlpha(final double alpha)
    {
        getAttributes().setFillAlpha(alpha);

        return this;
    }

    /**
     * Sets the alpha color on this shape.
     * 
     * @param alpha
     * @return T
     */
    @Override
    public Group setStrokeAlpha(final double alpha)
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
    public Group setDraggable(final boolean draggable)
    {
        getAttributes().setDraggable(draggable);

        return this;
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
    public Group setScale(final Point2D scale)
    {
        getAttributes().setScale(scale);

        return this;
    }

    /**
     * Sets this group's scale, with the same value for x and y.
     * 
     * @param xy
     * @return Group this Group
     */
    @Override
    public Group setScale(final double xy)
    {
        getAttributes().setScale(xy);

        return this;
    }

    /**
     * Sets this gruop's scale, starting at the given x and y
     * 
     * @param x
     * @param y
     * @return Group this Group
     */
    @Override
    public Group setScale(final double x, final double y)
    {
        getAttributes().setScale(x, y);

        return this;
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
    public Group setRotation(final double radians)
    {
        getAttributes().setRotation(radians);

        return this;
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
    public Group setRotationDegrees(final double degrees)
    {
        getAttributes().setRotationDegrees(degrees);

        return this;
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
    public Group setShear(final Point2D shear)
    {
        getAttributes().setShear(shear);

        return this;
    }

    /**
     * Sets this group's shear
     * 
     * @param offset
     * @return T
     */
    @Override
    public Group setShear(final double x, final double y)
    {
        getAttributes().setShear(x, y);

        return this;
    }

    /**
     * Sets this group's offset
     * 
     * @param offset
     * @return Group this Group
     */
    @Override
    public Group setOffset(final Point2D offset)
    {
        getAttributes().setOffset(offset);

        return this;
    }

    /**
     * Sets this group's offset, with the same value for x and y.
     * 
     * @param xy
     * @return Group this Group
     */
    @Override
    public Group setOffset(final double xy)
    {
        getAttributes().setOffset(xy);

        return this;
    }

    /**
     * Sets this group's offset, at the given x and y coordinates.
     * 
     * @param x
     * @param y
     * @return Group this Group
     */
    @Override
    public Group setOffset(final double x, final double y)
    {
        getAttributes().setOffset(x, y);

        return this;
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
    public Group setDragConstraint(final DragConstraint constraint)
    {
        getAttributes().setDragConstraint(constraint);

        return this;
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
    public Group setDragBounds(final DragBounds bounds)
    {
        getAttributes().setDragBounds(bounds);

        return this;
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
    public Group setDragMode(final DragMode mode)
    {
        getAttributes().setDragMode(mode);

        return this;
    }

    /**
     * Returns this group as a {@link IContainer}
     * 
     * @return IContainer<IPrimitive>
     */
    @Override
    public IContainer<Group, IPrimitive<?>> asContainer()
    {
        return this;
    }

    @Override
    public Group asGroup()
    {
        return this;
    }

    /**
     * Adds a primitive to the collection. Override to ensure primitive is put in Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public Group add(final IPrimitive<?> child)
    {
        child.removeFromParent();

        super.add(child);

        child.attachToLayerColorMap();

        return this;
    }

    @Override
    public Group add(final IPrimitive<?> child, final IPrimitive<?>... children)
    {
        add(child);

        for (IPrimitive<?> node : children)
        {
            add(node);
        }
        return this;
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
            Group group = parent.asGroup();

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
    public Group remove(final IPrimitive<?> child)
    {
        child.detachFromLayerColorMap();

        super.remove(child);

        return this;
    }

    /**
     * Removes all primitives from the collection. Override to ensure all primitives are removed from Layers Color Map
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public Group removeAll()
    {
        detachFromLayerColorMap();

        super.removeAll();

        return this;
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
            final NFastArrayList<IPrimitive<?>> list = getChildNodes();

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
            final NFastArrayList<IPrimitive<?>> list = getChildNodes();

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

        object.put("type", new JSONString(getNodeType().getValue()));

        if (false == getMetaData().isEmpty())
        {
            object.put("meta", new JSONObject(getMetaData().getJSO()));
        }
        object.put("attributes", new JSONObject(getAttributes().getJSO()));

        final NFastArrayList<IPrimitive<?>> list = getChildNodes();

        final JSONArray children = new JSONArray();

        if (list != null)
        {
            final int size = list.size();

            for (int i = 0; i < size; i++)
            {
                final IPrimitive<?> prim = list.get(i);

                if (null != prim)
                {
                    final Node<?> node = prim.asNode();

                    if (null != node)
                    {
                        JSONObject make = node.toJSONObject();

                        if (null != make)
                        {
                            children.set(children.size(), make);
                        }
                    }
                }
            }
        }
        object.put("children", children);

        return object;
    }

    /**
     * Moves this group's {@link Layer} one level up
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public Group moveUp()
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
        return this;
    }

    /**
     * Moves this group's {@link Layer} one level down
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public Group moveDown()
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
        return this;
    }

    /**
     * Moves this group's {@link Layer} to the top of the layer stack.
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public Group moveToTop()
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
        return this;
    }

    /**
     * Moves this group's {@link Layer} to the bottom of the layer stack.
     * 
     * @return Group this Group
     */
    @SuppressWarnings("unchecked")
    @Override
    public Group moveToBottom()
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
        return this;
    }

    @Override
    public void find(final Predicate<Node<?>> predicate, final LinkedHashSet<Node<?>> buff)
    {
        if (predicate.test(this))
        {
            buff.add(this);
        }
        final NFastArrayList<IPrimitive<?>> list = getChildNodes();

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            final IPrimitive<?> prim = list.get(i);

            if (null != prim)
            {
                final Node<?> node = prim.asNode();

                if (null != node)
                {
                    if (predicate.test(node))
                    {
                        buff.add(node);
                    }
                    final IContainer<?, ?> cont = node.asContainer();

                    if (null != cont)
                    {
                        cont.find(predicate, buff);
                    }
                }
            }
        }
    }

    @Override
    public IAnimationHandle animate(final AnimationTweener tweener, final AnimationProperties properties, final double duration /* milliseconds */)
    {
        return new TweeningAnimation(this, tweener, properties, duration, null).run();
    }

    @Override
    public IAnimationHandle animate(final AnimationTweener tweener, final AnimationProperties properties, final double duration /* milliseconds */, final IAnimationCallback callback)
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
    public void setDragConstraints(final DragConstraintEnforcer enforcer)
    {
        m_dragConstraintEnforcer = enforcer;
    }

    @Override
    public IFactory<Group> getFactory()
    {
        return new GroupFactory();
    }

    public static class GroupFactory extends ContainerNodeFactory<Group>
    {
        public GroupFactory()
        {
            super(NodeType.GROUP);

            addAttribute(Attribute.X);

            addAttribute(Attribute.Y);

            addAttribute(Attribute.ALPHA);

            addAttribute(Attribute.FILL_ALPHA);

            addAttribute(Attribute.STROKE_ALPHA);

            addAttribute(Attribute.DRAGGABLE);

            addAttribute(Attribute.SCALE);

            addAttribute(Attribute.SHEAR);

            addAttribute(Attribute.ROTATION);

            addAttribute(Attribute.OFFSET);

            addAttribute(Attribute.DRAG_CONSTRAINT);

            addAttribute(Attribute.DRAG_BOUNDS);

            addAttribute(Attribute.DRAG_MODE);
        }

        @Override
        public Group create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            final Group container = new Group(node, ctx);

            JSONDeserializer.getInstance().deserializeChildren(container, node, this, ctx);

            return container;
        }

        @Override
        public boolean addNodeForContainer(final IContainer<?, ?> container, final Node<?> node, final ValidationContext ctx)
        {
            final IPrimitive<?> prim = node.asPrimitive();

            if (null != prim)
            {
                container.asGroup().add(prim);

                return true;
            }
            else
            {
                try
                {
                    ctx.addBadTypeError(node.getClass().getName() + " is not a Primitive");
                }
                catch (ValidationException e)
                {
                    return false;
                }
            }
            return false;
        }
    }
}
