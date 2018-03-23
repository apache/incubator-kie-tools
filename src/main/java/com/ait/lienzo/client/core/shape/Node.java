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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TweeningAnimation;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeGestureChangeEvent;
import com.ait.lienzo.client.core.event.NodeGestureChangeHandler;
import com.ait.lienzo.client.core.event.NodeGestureEndEvent;
import com.ait.lienzo.client.core.event.NodeGestureEndHandler;
import com.ait.lienzo.client.core.event.NodeGestureStartEvent;
import com.ait.lienzo.client.core.event.NodeGestureStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.client.core.event.NodeMouseWheelHandler;
import com.ait.lienzo.client.core.event.NodeTouchCancelEvent;
import com.ait.lienzo.client.core.event.NodeTouchCancelHandler;
import com.ait.lienzo.client.core.event.NodeTouchEndEvent;
import com.ait.lienzo.client.core.event.NodeTouchEndHandler;
import com.ait.lienzo.client.core.event.NodeTouchMoveEvent;
import com.ait.lienzo.client.core.event.NodeTouchMoveHandler;
import com.ait.lienzo.client.core.event.NodeTouchStartEvent;
import com.ait.lienzo.client.core.event.NodeTouchStartHandler;
import com.ait.lienzo.client.core.shape.guides.IGuidePrimitive;
import com.ait.lienzo.client.core.shape.json.AbstractFactory;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2D.Point2DJSO;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.tooling.common.api.java.util.UUID;
import com.ait.tooling.nativetools.client.NObject;
import com.ait.tooling.nativetools.client.NObjectJSO;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * Node is the base class for {@link ContainerNode} and {@link Shape}.
 * This class provides a lot of the scaffolding for drawable nodes.
 * 
 * @param <T>
 */
public abstract class Node<T extends Node<T>> implements IDrawable<T>
{
    private static final HashSet<Type<?>> ALL_EVENTS = new HashSet<Type<?>>();

    private final Attributes              m_attr;

    private NodeType                      m_type;

    private Node<?>                       m_parent;

    private final OptionalNodeFields      m_opts     = OptionalNodeFields.make();

    @SafeVarargs
    public static final <T> List<T> asList(final T... list)
    {
        return Collections.unmodifiableList(Arrays.asList(list));
    }

    public static final List<Attribute> asAttributes(final Attribute... list)
    {
        return asList(list);
    }

    public static final List<Attribute> asAttributes(final List<Attribute> base, final Attribute... list)
    {
        final ArrayList<Attribute> make = new ArrayList<Attribute>(base);

        make.addAll(asList(list));

        return Collections.unmodifiableList(make);
    }

    public static final boolean isEventHandledGlobally(final Type<?> type)
    {
        if (null != type)
        {
            return ALL_EVENTS.contains(type);
        }
        return false;
    }

    protected Node(final NodeType type)
    {
        m_type = type;

        m_attr = new Attributes(this);
    }

    /**
     * Only sub-classes that wish to extend a Shape should use this.
     * 
     * @param type
     */
    protected void setNodeType(final NodeType type)
    {
        m_type = type;
    }

    /**
     * Constructor used by de-serialization code.
     * 
     * @param type
     * @param node
     */
    protected Node(final NodeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        m_type = type;

        if (null == node)
        {
            m_attr = new Attributes(this);

            return;
        }
        final JSONValue aval = node.get("attributes");

        if (null == aval)
        {
            m_attr = new Attributes(this);
        }
        else
        {
            final JSONObject aobj = aval.isObject();

            if (null == aobj)
            {
                m_attr = new Attributes(this);
            }
            else
            {
                final JavaScriptObject ajso = aobj.getJavaScriptObject();

                if (null == ajso)
                {
                    m_attr = new Attributes(this);
                }
                else
                {
                    m_attr = new Attributes(ajso, this);
                }
            }
        }
        final JSONValue mval = node.get("meta");

        if (null != mval)
        {
            final JSONObject mobj = mval.isObject();

            if (null != mobj)
            {
                final JavaScriptObject mjso = mobj.getJavaScriptObject();

                if (null != mjso)
                {
                    final NObjectJSO jso = mjso.cast();

                    m_opts.setMetaData(new MetaData(jso));
                }
            }
        }
    }

    @Override
    public IFactory<?> getFactory()
    {
        return LienzoCore.get().getFactory(m_type);
    }

    @Override
    public IMultiPointShape<?> asMultiPointShape()
    {
        return null;
    }

    @Override
    public ContainerNode<?, ?> asContainerNode()
    {
        return null;
    }

    protected final <M> M cast()
    {
        return shade(this);
    }

    private final native <M> M shade(Node<T> self)
    /*-{
		return self;
    }-*/;

    protected final Node<?> copyUnchecked()
    {
        return (Node<?>) JSONDeserializer.get().fromString(toJSONString(), false);// don't validate
    }

    @Override
    public final String uuid()
    {
        return m_opts.uuid();
    }

    /**
     * Serializes this Node as a JSON string.
     * The JSON string can be deserialized with 
     * {@link JSONDeserializer#fromString(String)}.
     * 
     * @return JSON string
     */
    @Override
    public String toJSONString()
    {
        final JSONObject object = toJSONObject();

        if (null != object)
        {
            return object.toString();
        }
        return null;
    }

    @Override
    public NObject onWire()
    {
        final JSONObject object = toJSONObject();

        if (null != object)
        {
            final NObjectJSO njso = object.getJavaScriptObject().cast();

            return new NObject(njso);
        }
        return new NObject();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public T refresh()
    {
        return cast();
    }

    @Override
    public final boolean hasMetaData()
    {
        return m_opts.hasMetaData();
    }

    @Override
    public final MetaData getMetaData()
    {
        return m_opts.getMetaData();
    }

    /**
     * Returns the collection of {@link Attribute} for this object.
     * 
     * @return Collection&lt;Attribute&gt;
     */
    @Override
    public Collection<Attribute> getAttributeSheet()
    {
        return getFactory().getAttributeSheet();
    }

    /**
     * Returns the collection of required {@link Attribute} for this object.
     * 
     * @return Collection&lt;Attribute&gt;
     */
    @Override
    public Collection<Attribute> getRequiredAttributes()
    {
        return getFactory().getRequiredAttributes();
    }

    protected void setParent(final Node<?> parent)
    {
        m_parent = parent;
    }

    public final boolean isAnimating()
    {
        return m_opts.isAnimating();
    }

    public final void doAnimating()
    {
        m_opts.doAnimating();
    }

    public final void unAnimating()
    {
        m_opts.unAnimating();
    }

    @Override
    public Node<?> getParent()
    {
        return m_parent;
    }

    /**
     * Returns the Layer that this Node is on.
     * 
     * @return {@link Layer}
     */
    @Override
    public Layer getLayer()
    {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Layer, and Layer returns itself, CYCLES!!!

        if (null != parent)
        {
            return parent.getLayer();
        }
        return null;
    }

    /**
     * Returns the Scene that this Node is on.
     * 
     * @return Scene
     */
    @Override
    public Scene getScene()
    {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Scene, and Scene returns itself, CYCLES!!!

        if (null != parent)
        {
            return parent.getScene();
        }
        return null;
    }

    /**
     * Returns the Viewport that this Node is on.
     */
    @Override
    public Viewport getViewport()
    {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Viewport, and Viewport returns itself, CYCLES!!!

        if (null != parent)
        {
            return parent.getViewport();
        }
        return null;
    }

    /**
     * Gets the viewport's Over Layer {@link Layer}
     * 
     * @return Layer
     */
    @Override
    public Layer getOverLayer()
    {
        final Viewport viewport = getViewport();

        if (null != viewport)
        {
            return viewport.getOverLayer();
        }
        return null;
    }

    /**
     * Gets the object's {@link ScratchPad}
     * 
     * @return ScratchPad
     */
    @Override
    public ScratchPad getScratchPad()
    {
        final Viewport viewport = getViewport();

        if (null != viewport)
        {
            return viewport.getScratchPad();
        }
        return null;
    }

    /**
     * Returns the node's {@link NodeType}.
     * @return {@link NodeType}
     */
    @Override
    public NodeType getNodeType()
    {
        return m_type;
    }

    @Override
    public final Attributes getAttributes()
    {
        return m_attr;
    }

    /**
     * Used internally. Applies the node's transform-related attributes
     * to the current context, draws the node (and it's children, if any)
     * and restores the context.
     */
    @Override
    public void drawWithTransforms(final Context2D context, final double alpha, final BoundingBox bounds)
    {
        if ((context.isSelection()) && (false == isListening()))
        {
            return;
        }
        if (context.isDrag() || isVisible())
        {
            context.saveContainer();

            final Transform xfrm = getPossibleNodeTransform();

            if (null != xfrm)
            {
                context.transform(xfrm);
            }
            drawWithoutTransforms(context, alpha, bounds);

            context.restoreContainer();
        }
    }

    /**
     * Used internally. Draws the node in the current Context2D
     * without applying the transformation-related attributes 
     * (e.g. X, Y, ROTATION, SCALE, SHEAR, OFFSET and TRANSFORM.)
     * <p> 
     * Shapes should apply the non-Transform related attributes (such a colors, strokeWidth etc.)
     * and draw the Shape's details (such as the the actual lines and fills) 
     * and Groups should draw their children.
     * 
     * @param context
     */
    abstract protected void drawWithoutTransforms(Context2D context, double alpha, BoundingBox bounds);

    @Override
    public Point2D getAbsoluteLocation()
    {
        final Point2D p = new Point2D(0, 0);

        getAbsoluteTransform().transform(p, p);

        return p;
    }

    @Override
    public Point2D getComputedLocation()
    {
        final Point2D locn = new Point2D();

        addParentsLocations(locn.getJSO());

        return locn;
    }

    protected void addParentsLocations(final Point2DJSO locn)
    {
        final Node<?> node = getParent();

        if (null != node)
        {
            node.addParentsLocations(locn);
        }
        locn.offset(m_attr.getX(), m_attr.getY());
    }

    /**
     * Returns the absolute transform by concatenating the transforms
     * of all its ancestors from the Viewport down to this node's parent.
     * 
     * @return {@link Transform}
     */

    @Override
    public Transform getAbsoluteTransform()
    {
        final Transform xfrm = new Transform();

        getAbsoluteTransformFromParents(this, xfrm);

        return xfrm;
    }

    private final void getAbsoluteTransformFromParents(final Node<?> root, final Transform xfrm)
    {
        /*
         * recursive walk up till parent is null
         */

        if (null == root)
        {
            return;
        }
        getAbsoluteTransformFromParents(root.getParent(), xfrm);

        final Transform temp = root.getPossibleNodeTransform();

        if (temp != null)
        {
            xfrm.multiply(temp);
        }
    }

    protected Transform getPossibleNodeTransform()
    {
        if (false == m_attr.hasAnyTransformAttributes())
        {
            return null;
        }
        final Transform xfrm = Transform.fromXY(m_attr.getX(), m_attr.getY());

        if (false == m_attr.hasComplexTransformAttributes())
        {
            return xfrm;
        }
        // Otherwise use ROTATION, SCALE, OFFSET and SHEAR

        double ox = 0;

        double oy = 0;

        final Point2D offset = m_attr.getOffset();

        if (null != offset)
        {
            ox = offset.getX();

            oy = offset.getY();
        }
        final double r = m_attr.getRotation();

        if (r != 0)
        {
            if ((ox != 0) || (oy != 0))
            {
                xfrm.translate(ox, oy);

                xfrm.rotate(r);

                xfrm.translate(-ox, -oy);
            }
            else
            {
                xfrm.rotate(r);
            }
        }
        final Point2D scale = m_attr.getScale();

        if (null != scale)
        {
            final double sx = scale.getX();

            final double sy = scale.getY();

            if ((sx != 1) || (sy != 1))
            {
                if ((ox != 0) || (oy != 0))
                {
                    xfrm.translate(ox, oy);

                    xfrm.scale(sx, sy);

                    xfrm.translate(-ox, -oy);
                }
                else
                {
                    xfrm.scale(sx, sy);
                }
            }
        }
        final Point2D shear = m_attr.getShear();

        if (null != shear)
        {
            final double sx = shear.getX();

            final double sy = shear.getY();

            if ((sx != 0) || (sy != 0))
            {
                xfrm.shear(sx, sy);
            }
        }
        return xfrm;
    }

    @Override
    public BoundingPoints getComputedBoundingPoints()
    {
        double computedXOffset = 0;
        double computedYOffset = 0;
        Node parent = getParent();
        if (parent != null)
        {
            Point2D computedLocation = parent.getComputedLocation();
            computedXOffset = computedLocation.getX();
            computedYOffset = computedLocation.getY();

        }

        return getBoundingPoints(computedXOffset, computedYOffset);
    }

    @Override
    public BoundingPoints getBoundingPoints()
    {
        return getBoundingPoints(0, 0);
    }

    public BoundingPoints getBoundingPoints(final double computedOffsetX, final double computedOffsetY)
    {
        final BoundingBox bbox = getBoundingBox();

        if (null != bbox)
        {
            final Transform transform = getPossibleNodeTransform();

            if (null != transform)
            {
                return new BoundingPoints(bbox).transform(computedOffsetX, computedOffsetY, transform);
            }
            return new BoundingPoints(bbox);
        }
        return null;
    }

    @Override
    public T setUserData(final Object data)
    {
        m_opts.setUserData(data);

        return cast();
    }

    @Override
    public Object getUserData()
    {
        return m_opts.getUserData();
    }

    /**
     * Sets whether the node is visible.
     * 
     * @param visible
     * @return this Node
     */
    @Override
    public T setVisible(final boolean visible)
    {
        m_attr.setVisible(visible);

        return cast();
    }

    @Override
    public boolean isVisible()
    {
        return m_attr.isVisible();
    }

    /**
     * Sets whether this node is listening for events.
     * 
     * @param listening
     * @return this Node
     */
    @Override
    public T setListening(final boolean listening)
    {
        m_attr.setListening(listening);

        return cast();
    }

    @Override
    public boolean isListening()
    {
        return m_attr.isListening();
    }

    /**
     * Sets the name of this Node.
     * 
     * @param name
     * @return this Node
     */
    @Override
    public T setName(final String name)
    {
        m_attr.setName(name);

        return cast();
    }

    /**
     * Returns the name of this Node.
     * @return String
     */
    @Override
    public String getName()
    {
        return m_attr.getName();
    }

    /**
     * Sets the ID of this node.
     * 
     * @param id
     * @return
     */
    @Override
    public T setID(final String id)
    {
        m_attr.setID(id);

        return cast();
    }

    /**
     * Returns the ID of this node.
     * @return String
     */
    @Override
    public String getID()
    {
        return m_attr.getID();
    }

    /**
     * Returns this Node
     * @return Node
     */
    @Override
    public Node<?> asNode()
    {
        return this;
    }

    @Override
    public IContainer<?, ?> asContainer()
    {
        return null;
    }

    @Override
    public IPrimitive<?> asPrimitive()
    {
        return null;
    }

    @Override
    public IGuidePrimitive<?> asGuide()
    {
        return null;
    }

    @Override
    public Scene asScene()
    {
        return null;
    }

    @Override
    public Viewport asViewport()
    {
        return null;
    }

    @Override
    public Layer asLayer()
    {
        return null;
    }

    @Override
    public GroupOf<IPrimitive<?>, ?> asGroupOf()
    {
        return null;
    }

    @Override
    public Group asGroup()
    {
        return null;
    }

    @Override
    public Shape<?> asShape()
    {
        return null;
    }

    @Override
    public boolean isEventHandled(final Type<?> type)
    {
        final HandlerManager hand = m_opts.getHandlerManager();

        if ((null != hand) && (isEventHandledGlobally(type)) && (isListening()))
        {
            if (false == isVisible())
            {
                final IPrimitive<?> prim = asPrimitive();

                if ((null != prim) && (prim.isDragging()) && ((type == NodeDragStartEvent.getType()) || (type == NodeDragMoveEvent.getType()) || (type == NodeDragEndEvent.getType())))
                {
                    return hand.isEventHandled(type);
                }
                return false;
            }
            return hand.isEventHandled(type);
        }
        return false;
    }

    @Override
    public void fireEvent(final GwtEvent<?> event)
    {
        if (isEventHandled(event.getAssociatedType()))
        {
            m_opts.getHandlerManager().fireEvent(event);
        }
    }

    protected final <H extends EventHandler> HandlerRegistration addEnsureHandler(final Type<H> type, final H handler)
    {
        Objects.requireNonNull(type);

        Objects.requireNonNull(handler);

        HandlerManager hand = m_opts.getHandlerManager();

        if (null == hand)
        {
            hand = new HandlerManager(this);

            m_opts.setHandlerManager(hand);
        }
        ALL_EVENTS.add(type);

        return hand.addHandler(type, handler);
    }

    @Override
    public final T setAttributesChangedBatcher(final IAttributesChangedBatcher batcher)
    {
        m_attr.setAttributesChangedBatcher(batcher);

        return cast();
    }

    @Override
    public final HandlerRegistration addAttributesChangedHandler(final Attribute attribute, final AttributesChangedHandler handler)
    {
        return m_attr.addAttributesChangedHandler(attribute, handler);
    }

    @Override
    public final T cancelAttributesChangedBatcher()
    {
        m_attr.cancelAttributesChangedBatcher();

        return cast();
    }

    @Override
    public HandlerRegistration addNodeMouseClickHandler(final NodeMouseClickHandler handler)
    {
        return addEnsureHandler(NodeMouseClickEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseDoubleClickHandler(final NodeMouseDoubleClickHandler handler)
    {
        return addEnsureHandler(NodeMouseDoubleClickEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseDownHandler(final NodeMouseDownHandler handler)
    {
        return addEnsureHandler(NodeMouseDownEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseMoveHandler(final NodeMouseMoveHandler handler)
    {
        return addEnsureHandler(NodeMouseMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseOutHandler(final NodeMouseOutHandler handler)
    {
        return addEnsureHandler(NodeMouseOutEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseOverHandler(final NodeMouseOverHandler handler)
    {
        return addEnsureHandler(NodeMouseOverEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseExitHandler(final NodeMouseExitHandler handler)
    {
        return addEnsureHandler(NodeMouseExitEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseEnterHandler(final NodeMouseEnterHandler handler)
    {
        return addEnsureHandler(NodeMouseEnterEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseUpHandler(final NodeMouseUpHandler handler)
    {
        return addEnsureHandler(NodeMouseUpEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseWheelHandler(final NodeMouseWheelHandler handler)
    {
        return addEnsureHandler(NodeMouseWheelEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchCancelHandler(final NodeTouchCancelHandler handler)
    {
        return addEnsureHandler(NodeTouchCancelEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchEndHandler(final NodeTouchEndHandler handler)
    {
        return addEnsureHandler(NodeTouchEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchMoveHandler(final NodeTouchMoveHandler handler)
    {
        return addEnsureHandler(NodeTouchMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchStartHandler(final NodeTouchStartHandler handler)
    {
        return addEnsureHandler(NodeTouchStartEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureStartHandler(final NodeGestureStartHandler handler)
    {
        return addEnsureHandler(NodeGestureStartEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureEndHandler(final NodeGestureEndHandler handler)
    {
        return addEnsureHandler(NodeGestureEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureChangeHandler(final NodeGestureChangeHandler handler)
    {
        return addEnsureHandler(NodeGestureChangeEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragEndHandler(final NodeDragEndHandler handler)
    {
        return addEnsureHandler(NodeDragEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragMoveHandler(final NodeDragMoveHandler handler)
    {
        return addEnsureHandler(NodeDragMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragStartHandler(final NodeDragStartHandler handler)
    {
        return addEnsureHandler(NodeDragStartEvent.getType(), handler);
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
    public final boolean equals(final Object other)
    {
        return (this == other);
    }

    @Override
    public final int hashCode()
    {
        return m_opts.hashCode();
    }

    public static abstract class NodeFactory<N extends IJSONSerializable<N>>extends AbstractFactory<N>
    {
        protected NodeFactory(final NodeType type)
        {
            this(type.getValue());
        }

        protected NodeFactory(final String typeName)
        {
            super(typeName);

            addAttribute(Attribute.ID);

            addAttribute(Attribute.NAME);

            addAttribute(Attribute.VISIBLE);

            addAttribute(Attribute.LISTENING);
        }

        /**
         * Only factories that wish to extend other factories should use this.
         * 
         * @param type {@link NodeType}
         */
        protected void setNodeType(final NodeType type)
        {
            setTypeName(type.getValue());
        }
    }

    private static class OptionalNodeFields extends JavaScriptObject
    {
        public static final OptionalNodeFields make()
        {
            return JavaScriptObject.createObject().cast();
        }

        protected OptionalNodeFields()
        {
        }

        protected final String uuid()
        {
            final String uuid = uuid_0();

            if (null != uuid)
            {
                return uuid;
            }
            return uuid_0(UUID.uuid());
        }

        protected final boolean hasMetaData()
        {
            return (null != meta_0());
        }

        protected final MetaData getMetaData()
        {
            final MetaData meta = meta_0();

            if (null != meta)
            {
                return meta;
            }
            return setMetaData(new MetaData());
        }

        protected final MetaData setMetaData(final MetaData meta)
        {
            return meta_0(meta);
        }

        private final native String uuid_0()
        /*-{
			return this.uuid;
        }-*/;

        private final native String uuid_0(String uuid)
        /*-{
			if (null == uuid) {
				delete this["uuid"];
			} else {
				this.uuid = uuid;
			}
			return uuid;
        }-*/;

        private final native MetaData meta_0()
        /*-{
			return this.meta;
        }-*/;

        private final native MetaData meta_0(MetaData meta)
        /*-{
			if (null == meta) {
				delete this["meta"];
			} else {
				this.meta = meta;
			}
			return meta;
        }-*/;

        protected final native Object getUserData()
        /*-{
			return this.data;
        }-*/;

        protected final native void setUserData(Object data)
        /*-{
			if (null == data) {
				delete this["data"];
			} else {
				this.data = data;
			}
        }-*/;

        protected final native HandlerManager getHandlerManager()
        /*-{
			return this.hand;
        }-*/;

        protected final native void setHandlerManager(HandlerManager hand)
        /*-{
			if (null == hand) {
				delete this["hand"];
			} else {
				this.hand = hand;
			}
        }-*/;

        protected final native boolean isAnimating()
        /*-{
			if (this.anim !== undefined) {
				return (this.anim > 0);
			}
			return false;
        }-*/;

        protected final native void doAnimating()
        /*-{
			if (this.anim !== undefined) {
				this.anim = this.anim + 1;
			} else {
				this.anim = 1;
			}
        }-*/;

        protected final native void unAnimating()
        /*-{
			if (this.anim !== undefined) {
				this.anim = this.anim - 1;
				if (this.anim < 1) {
					delete this["anim"];
				}
			}
        }-*/;
    }
}