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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TweeningAnimation;
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
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.INodeEvent;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;
import com.ait.lienzo.tools.common.api.java.util.UUID;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * Node is the base class for {@link ContainerNode} and {@link Shape}.
 * This class provides a lot of the scaffolding for drawable nodes.
 * @param <T>
 */
public abstract class Node<T extends Node<T>> implements IDrawable<T> {

    private static final HashSet<Type<?>> ALL_EVENTS = new HashSet<>();

    private final OptionalNodeFields m_opts = OptionalNodeFields.make();

    private NodeType m_type;

    private Node<?> m_parent;

    @JsProperty
    private double x;

    @JsProperty
    private double y;

    @JsProperty
    private double rotation;

    @JsProperty
    private Point2D scale;

    @JsProperty
    private Point2D shear;

    @JsProperty
    private Point2D offset;

    @JsProperty
    private DragConstraint dragConstraint;

    @JsProperty
    private Transform transform;

    /**
     * 1.0 is the default
     */
    @JsProperty
    private double alpha = 1;

    /**
     * 1.0 is the default
     */
    @JsProperty
    private double strokeAlpha = 1;

    /**
     * 1.0 is the default
     */
    @JsProperty
    private double fillAlpha = 1;

    @JsProperty
    private boolean visible = true;

    @JsProperty
    private String id;

    @JsProperty
    private boolean draggable;

    private DragBounds dragBounds;

    @JsProperty
    private DragMode dragMode;

    @JsProperty
    private boolean listening = true;

    /**
     * This is cached, to avoid recreating each draw
     */
    private Transform cachedXfrm;

    @JsProperty
    private EventPropagationMode eventPropagationMode = EventPropagationMode.lookup(null);

    @SafeVarargs
    public static final <T> List<T> asList(final T... list) {
        return Collections.unmodifiableList(Arrays.asList(list));
    }

    public static final List<Attribute> asAttributes(final Attribute... list) {
        return asList(list);
    }

    public static final List<Attribute> asAttributes(final List<Attribute> base, final Attribute... list) {
        final ArrayList<Attribute> make = new ArrayList<>(base);

        make.addAll(asList(list));

        return Collections.unmodifiableList(make);
    }

    public static final boolean isEventHandledGlobally(final Type<?> type) {
        if (null != type) {
            return ALL_EVENTS.contains(type);
        }
        return false;
    }

    @JsIgnore
    protected Node(final NodeType type) {
        m_type = type;
    }

    /**
     * Only sub-classes that wish to extend a Shape should use this.
     * @param type
     */
    protected void setNodeType(final NodeType type) {
        m_type = type;
    }

    public EventPropagationMode getEventPropagationMode() {
        return this.eventPropagationMode;
    }

    public T setEventPropagationMode(final EventPropagationMode mode) {
        this.eventPropagationMode = mode;

        return cast();
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return this.y;
    }

    public final T setX(final double x) {
        this.x = x;
        return cast();
    }

    public final T setY(final double y) {
        this.y = y;
        return cast();
    }

    /**
     * Sets the X and Y attributes to P.x and P.y
     * @param p Point2D
     * @return this Shape
     */
    public T setLocation(final Point2D p) {
        setX(p.getX());

        setY(p.getY());

        return cast();
    }

    public final T setRotation(final double radians) {
        this.rotation = radians;
        return cast();
    }

    public final double getRotation() {
        return rotation;
    }

    public final T setRotationDegrees(final double degrees) {
        this.rotation = Geometry.toRadians(degrees);
        return cast();
    }

    public final T setAlpha(double alpha) {
        this.alpha = alpha;
        return cast();
    }

    public final double getAlpha() {
        return alpha;
    }

    public final T setStrokeAlpha(double alpha) {
        this.strokeAlpha = alpha;
        return cast();
    }

    public final double getStrokeAlpha() {
        return this.strokeAlpha;
    }

    public final T setFillAlpha(double alpha) {
        this.fillAlpha = alpha;
        return cast();
    }

    public final double getFillAlpha() {
        return this.fillAlpha;
    }

    public final double getRotationDegrees() {
        return Geometry.toDegrees(this.rotation);
    }

    public final T setScale(final Point2D scale) {
        this.scale = scale;
        return cast();
    }

    public final T setScale(final double scalex, final double scaley) {
        setScale(new Point2D(scalex, scaley));
        return cast();
    }

    public final T setScale(final double value) {
        setScale(new Point2D(value, value));
        return cast();
    }

    public final Point2D getScale() {
        return this.scale;
    }

    public final T setShear(final double shearX, final double shearY) {
        setShear(new Point2D(shearX, shearY));
        return cast();
    }

    public final T setShear(final Point2D shear) {
        this.shear = shear;
        return cast();
    }

    public final Point2D getShear() {
        return shear;
    }

    public final T setOffset(final Point2D offset) {
        this.offset = offset;
        return cast();
    }

    public final T setOffset(final double x, final double y) {
        setOffset(new Point2D(x, y));
        return cast();
    }

    public final T setOffset(final double xy) {
        setOffset(new Point2D(xy, xy));
        return cast();
    }

    public final Point2D getOffset() {
        return this.offset;
    }

    /**
     * Returns the X and Y attributes as a Point2D
     * @return Point2D
     */
    public Point2D getLocation() {
        return new Point2D(getX(), getY());
    }

    /**
     * Returns true if this shape can be dragged; false otherwise.
     * @return boolean
     */
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Sets if this shape can be dragged or not.
     * @return T
     */
    public T setDraggable(final boolean draggable) {
        this.draggable = draggable;

        return cast();
    }

    /**
     * Gets this node's {@link com.ait.lienzo.shared.core.types.DragConstraint}
     * @return DragConstraint
     */
    public DragConstraint getDragConstraint() {
        return this.dragConstraint;
    }

    /**
     * Sets this node's drag constraint; e.g., horizontal, vertical or none (default)
     * @param constraint
     * @return T
     */
    public T setDragConstraint(final DragConstraint constraint) {
        this.dragConstraint = constraint;

        return cast();
    }

    /**
     * Gets the {@link DragBounds} for this node.
     * @return DragBounds
     */
    public DragBounds getDragBounds() {
        return this.dragBounds;
    }

    /**
     * Sets this nodes's drag bounds.
     * @param bounds
     * @return T
     */
    public T setDragBounds(final DragBounds bounds) {
        this.dragBounds = bounds;

        return cast();
    }

    /**
     * Gets the {@link com.ait.lienzo.shared.core.types.DragMode} for this node.
     * @return DragMode
     */
    public DragMode getDragMode() {
        return this.dragMode;
    }

    /**
     * Sets this node's drag mode.
     * @param mode
     * @return T
     */
    public T setDragMode(final DragMode mode) {
        this.dragMode = mode;

        return cast();
    }

    @Override
    public IMultiPointShape<?> asMultiPointShape() {
        return null;
    }

    @Override
    public ContainerNode<?, ?> asContainerNode() {
        return null;
    }

    protected final <M> M cast() {
        return Js.uncheckedCast(this);
    }

    protected Node<T> copyTo(Node<T> other) {
        other.m_type = this.m_type.copy();
        other.x = this.x;
        other.y = this.y;
        other.rotation = this.rotation;
        other.scale = null != this.scale ? this.scale.copy() : null;
        other.shear = null != this.shear ? this.shear.copy() : null;
        other.offset = null != this.offset ? this.offset.copy() : null;
        other.dragConstraint = this.dragConstraint;
        other.transform = null != this.transform ? this.transform.copy() : null;
        other.alpha = this.alpha;
        other.strokeAlpha = this.strokeAlpha;
        other.fillAlpha = this.fillAlpha;
        other.visible = this.visible;
        other.draggable = this.draggable;
        other.dragBounds = this.dragBounds;
        other.dragMode = this.dragMode;
        other.listening = this.listening;
        return other;
    }

    @Override
    public final String uuid() {
        return m_opts.uuid();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getUserData() + " : " + getX() + " : " + getY();
    }

    @Override
    public T refresh() {
        return cast();
    }

    protected void setParent(final Node<?> parent) {
        m_parent = parent;
    }

    public final boolean isAnimating() {
        return m_opts.isAnimating();
    }

    public final void doAnimating() {
        m_opts.doAnimating();
    }

    public final void unAnimating() {
        m_opts.unAnimating();
    }

    @Override
    public Node<?> getParent() {
        return m_parent;
    }

    /**
     * Returns the Layer that this Node is on.
     * @return {@link Layer}
     */
    @Override
    public Layer getLayer() {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Layer, and Layer returns itself, CYCLES!!!

        if (null != parent) {
            return parent.getLayer();
        }
        return null;
    }

    /**
     * Returns the Scene that this Node is on.
     * @return Scene
     */
    @Override
    public Scene getScene() {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Scene, and Scene returns itself, CYCLES!!!

        if (null != parent) {
            return parent.getScene();
        }
        return null;
    }

    /**
     * Returns the Viewport that this Node is on.
     */
    @Override
    public Viewport getViewport() {
        final Node<?> parent = getParent();// change, no iteration, no testing, no casting, recurses upwards to a Viewport, and Viewport returns itself, CYCLES!!!

        if (null != parent) {
            return parent.getViewport();
        }
        return null;
    }

    /**
     * Gets the viewport's Over Layer {@link Layer}
     * @return Layer
     */
    @Override
    public Layer getOverLayer() {
        final Viewport viewport = getViewport();

        if (null != viewport) {
            return viewport.getOverLayer();
        }
        return null;
    }

    /**
     * Gets the object's {@link ScratchPad}
     * @return ScratchPad
     */
    @Override
    public ScratchPad getScratchPad() {
        final Viewport viewport = getViewport();

        if (null != viewport) {
            return viewport.getScratchPad();
        }
        return null;
    }

    /**
     * Returns the node's {@link NodeType}.
     * @return {@link NodeType}
     */
    @Override
    public NodeType getNodeType() {
        return m_type;
    }

    /**
     * Used internally. Applies the node's transform-related attributes
     * to the current context, draws the node (and it's children, if any)
     * and restores the context.
     */
    @Override
    public void drawWithTransforms(final Context2D context, final double alpha, final BoundingBox bounds) {
        drawWithTransforms(context,
                           alpha,
                           bounds,
                           this::getPossibleNodeTransform);
    }

    public void drawWithTransforms(final Context2D context, final double alpha, final BoundingBox bounds, final Supplier<Transform> transformSupplier) {
        if ((context.isSelection()) && (!isListening())) {
            return;
        }
        if (context.isDrag() || isVisible()) {
            context.saveContainer(getID());

            final Transform xfrm = transformSupplier.get();

            if (null != xfrm) {
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
     * @param context
     */
    protected abstract void drawWithoutTransforms(Context2D context, double alpha, BoundingBox bounds);

    @Override
    public Point2D getAbsoluteLocation() {
        final Point2D p = new Point2D(0, 0);

        getAbsoluteTransform().transform(p, p);

        return p;
    }

    @Override
    public Point2D getComputedLocation() {
        final Point2D locn = new Point2D(0, 0);

        addParentsLocations(locn);

        return locn;
    }

    protected void addParentsLocations(final Point2D locn) {
        final Node<?> node = getParent();

        if (null != node) {
            node.addParentsLocations(locn);
        }

        locn.offset(getX(), getY());
    }

    /**
     * Returns the absolute transform by concatenating the transforms
     * of all its ancestors from the Viewport down to this node's parent.
     * @return {@link Transform}
     */

    @Override
    public Transform getAbsoluteTransform() {
        final Transform xfrm = new Transform();

        getAbsoluteTransformFromParents(this, xfrm);

        return xfrm;
    }

    private final void getAbsoluteTransformFromParents(final Node<?> root, final Transform xfrm) {
        /*
         * recursive walk up till parent is null
         */

        if (null == root) {
            return;
        }
        getAbsoluteTransformFromParents(root.getParent(), xfrm);

        final Transform temp = root.getPossibleNodeTransform();

        if (temp != null) {
            xfrm.multiply(temp);
        }
    }

    public final boolean hasAnyTransformAttributes() {
        return x != 0 || y != 0 || getRotation() != 0 || getScale() != null || getShear() != null;
    }

    public final boolean hasComplexTransformAttributes() {
        return getRotation() != 0 || getScale() != null || getShear() != null;
    }

    protected Transform getPossibleNodeTransform() {

        if (!hasAnyTransformAttributes() && null == transform) {
            return null;
        }
        cachedXfrm = Transform.fromXY(cachedXfrm, getX(), getY());

        Transform t2 = getTransform();

        if (t2 != null) // Use the Transform if it's defined
        {
            cachedXfrm.multiply(t2);
            return cachedXfrm;
        }

        if (!hasComplexTransformAttributes()) {
            return cachedXfrm;
        }
        // Otherwise use ROTATION, SCALE, OFFSET and SHEAR

        double ox = 0;

        double oy = 0;

        final Point2D offset = getOffset();

        if (null != offset) {
            ox = offset.getX();

            oy = offset.getY();
        }
        final double r = getRotation();

        if (r != 0) {
            if ((ox != 0) || (oy != 0)) {
                cachedXfrm.translate(ox, oy);

                cachedXfrm.rotate(r);

                cachedXfrm.translate(-ox, -oy);
            } else {
                cachedXfrm.rotate(r);
            }
        }
        final Point2D scale = getScale();

        if (null != scale) {
            final double sx = scale.getX();

            final double sy = scale.getY();

            if ((sx != 1) || (sy != 1)) {
                if ((ox != 0) || (oy != 0)) {
                    cachedXfrm.translate(ox, oy);

                    cachedXfrm.scaleWithXY(sx, sy);

                    cachedXfrm.translate(-ox, -oy);
                } else {
                    cachedXfrm.scaleWithXY(sx, sy);
                }
            }
        }
        final Point2D shear = getShear();

        if (null != shear) {
            final double sx = shear.getX();

            final double sy = shear.getY();

            if ((sx != 0) || (sy != 0)) {
                cachedXfrm.shear(sx, sy);
            }
        }
        return cachedXfrm;
    }

    @Override
    public BoundingPoints getComputedBoundingPoints() {
        double computedXOffset = 0;
        double computedYOffset = 0;
        Node parent = getParent();
        if (parent != null) {
            Point2D computedLocation = parent.getComputedLocation();
            computedXOffset = computedLocation.getX();
            computedYOffset = computedLocation.getY();
        }

        return getBoundingPoints(computedXOffset, computedYOffset);
    }

    @Override
    public BoundingPoints getBoundingPoints() {
        return getBoundingPoints(0, 0);
    }

    public BoundingPoints getBoundingPoints(final double computedOffsetX, final double computedOffsetY) {
        final BoundingBox bbox = getBoundingBox();

        if (null != bbox) {
            final Transform transform = getPossibleNodeTransform();

            if (null != transform) {
                return new BoundingPoints(bbox).transform(computedOffsetX, computedOffsetY, transform);
            }
            return new BoundingPoints(bbox);
        }
        return null;
    }

    @Override
    public T setUserData(final Object data) {
        m_opts.setUserData(data);

        return cast();
    }

    @Override
    public Object getUserData() {
        return m_opts.getUserData();
    }

    /**
     * Sets whether the node is visible.
     * @param visible
     * @return this Node
     */
    @Override
    public T setVisible(final boolean visible) {
        this.visible = visible;

        return cast();
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

//    public final void setListening(final boolean listening)
//    {
//        put(Attribute.LISTENING.getProperty(), listening);
//    }
//
//    public final boolean isListening()
//    {
//        if (m_jso.has(Attribute.LISTENING.getProperty()))
//        {
//            return getBoolean(Attribute.LISTENING.getProperty());
//        }
//        return true;
//    }

    /**
     * Sets whether this node is listening for events.
     * @param listening
     * @return this Node
     */
    @Override
    public T setListening(final boolean listening) {
        this.listening = listening;

        return cast();
    }

    @Override
    public boolean isListening() {
        return listening;
    }

    /**
     * Sets the ID of this node.
     * @param id
     * @return
     */
    @Override
    public T setID(final String id) {
        this.id = id;

        return cast();
    }

    /**
     * Returns the ID of this node.
     * @return String
     */
    @Override
    public String getID() {
        return this.id;
    }

    public Transform getTransform() {
        return transform;
    }

    public T setTransform(final Transform transform) {
        this.transform = transform;
        return cast();
    }

    /**
     * Returns this Node
     * @return Node
     */
    @Override
    public Node<?> asNode() {
        return this;
    }

    @Override
    public IContainer<?, ?> asContainer() {
        return null;
    }

    @Override
    public IPrimitive<?> asPrimitive() {
        return null;
    }

    @Override
    public IGuidePrimitive<?> asGuide() {
        return null;
    }

    @Override
    public Scene asScene() {
        return null;
    }

    @Override
    public Viewport asViewport() {
        return null;
    }

    @Override
    public Layer asLayer() {
        return null;
    }

    @Override
    public GroupOf<IPrimitive<?>, ?> asGroupOf() {
        return null;
    }

    @Override
    public Group asGroup() {
        return null;
    }

    @Override
    public Shape<?> asShape() {
        return null;
    }

    @Override
    public <H extends EventHandler> boolean isEventHandled(final Type<H> type) {
        final HandlerManager hand = m_opts.getHandlerManager();

        if ((null != hand) && (isEventHandledGlobally(type)) && (isListening())) {
            if (!isVisible()) {
                final IPrimitive<?> prim = asPrimitive();

                if ((null != prim) && (prim.isDragging()) && ((type == NodeDragStartEvent.getType()) || (type == NodeDragMoveEvent.getType()) || (type == NodeDragEndEvent.getType()))) {
                    return hand.isEventHandled(type);
                }
                return false;
            }
            return hand.isEventHandled(type);
        }
        return false;
    }

    @Override
    public <H extends EventHandler, S> void fireEvent(final INodeEvent<H, S> event) {
        if (isEventHandled(event.getAssociatedType())) {
            m_opts.getHandlerManager().fireEvent(event);
        }
    }

    protected final <H extends EventHandler> HandlerRegistration addEnsureHandler(final Type<H> type, final H handler) {
        Objects.requireNonNull(type);

        Objects.requireNonNull(handler);

        HandlerManager hand = m_opts.getHandlerManager();

        if (null == hand) {
            hand = new HandlerManager(this);

            m_opts.setHandlerManager(hand);
        }
        ALL_EVENTS.add(type);

        return hand.addHandler(type, handler);
    }

    @Override
    public final T cancelAttributesChangedBatcher() {
        //m_attr.cancelAttributesChangedBatcher();

        return cast();
    }

    @Override
    public HandlerRegistration addNodeMouseClickHandler(final NodeMouseClickHandler handler) {
        return addEnsureHandler(NodeMouseClickEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseDoubleClickHandler(final NodeMouseDoubleClickHandler handler) {
        return addEnsureHandler(NodeMouseDoubleClickEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseDownHandler(final NodeMouseDownHandler handler) {
        return addEnsureHandler(NodeMouseDownEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseMoveHandler(final NodeMouseMoveHandler handler) {
        return addEnsureHandler(NodeMouseMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseOutHandler(final NodeMouseOutHandler handler) {
        return addEnsureHandler(NodeMouseOutEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseOverHandler(final NodeMouseOverHandler handler) {
        return addEnsureHandler(NodeMouseOverEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseExitHandler(final NodeMouseExitHandler handler) {
        return addEnsureHandler(NodeMouseExitEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseEnterHandler(final NodeMouseEnterHandler handler) {
        return addEnsureHandler(NodeMouseEnterEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseUpHandler(final NodeMouseUpHandler handler) {
        return addEnsureHandler(NodeMouseUpEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeMouseWheelHandler(final NodeMouseWheelHandler handler) {
        return addEnsureHandler(NodeMouseWheelEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchCancelHandler(final NodeTouchCancelHandler handler) {
        return addEnsureHandler(NodeTouchCancelEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchEndHandler(final NodeTouchEndHandler handler) {
        return addEnsureHandler(NodeTouchEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchMoveHandler(final NodeTouchMoveHandler handler) {
        return addEnsureHandler(NodeTouchMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeTouchStartHandler(final NodeTouchStartHandler handler) {
        return addEnsureHandler(NodeTouchStartEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureStartHandler(final NodeGestureStartHandler handler) {
        return addEnsureHandler(NodeGestureStartEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureEndHandler(final NodeGestureEndHandler handler) {
        return addEnsureHandler(NodeGestureEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeGestureChangeHandler(final NodeGestureChangeHandler handler) {
        return addEnsureHandler(NodeGestureChangeEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragEndHandler(final NodeDragEndHandler handler) {
        return addEnsureHandler(NodeDragEndEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragMoveHandler(final NodeDragMoveHandler handler) {
        return addEnsureHandler(NodeDragMoveEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addNodeDragStartHandler(final NodeDragStartHandler handler) {
        return addEnsureHandler(NodeDragStartEvent.getType(), handler);
    }

    @Override
    public IAnimationHandle animate(final AnimationTweener tweener, final AnimationProperties properties, final double duration /* milliseconds */) {
        return new TweeningAnimation(this, tweener, properties, duration, null).run();
    }

    @Override
    public IAnimationHandle animate(final AnimationTweener tweener, final AnimationProperties properties, final double duration /* milliseconds */, final IAnimationCallback callback) {
        return new TweeningAnimation(this, tweener, properties, duration, callback).run();
    }

    @Override
    public final boolean equals(final Object other) {
        return (this == other);
    }

    @Override
    public final int hashCode() {
        return m_opts.hashCode();
    }

    @JsType
    private static class OptionalNodeFields {

        @JsProperty
        private String uuid;

        @JsProperty
        private Object userData;

        @JsIgnore
        private HandlerManager hand;

        @JsProperty
        private int anim;

        public static final OptionalNodeFields make() {
            return new OptionalNodeFields();
        }

        protected OptionalNodeFields() {
        }

        protected final String uuid() {
            if (null == this.uuid) {
                this.uuid = UUID.uuid();
            }
            return uuid;
        }

        protected final Object getUserData() {
            return this.userData;
        }

        protected final void setUserData(Object userData) {
            this.userData = userData;
        }

        protected final HandlerManager getHandlerManager() {
            return this.hand;
        }

        protected final void setHandlerManager(HandlerManager hand) {
            this.hand = hand;
        }

        protected final boolean isAnimating() {
            return anim > 0;
        }

        protected final void doAnimating() {
            this.anim = this.anim + 1;
        }

        protected final void unAnimating() {
            if (this.anim > 0) {
                this.anim = this.anim - 1;
            }
        }
    }
}