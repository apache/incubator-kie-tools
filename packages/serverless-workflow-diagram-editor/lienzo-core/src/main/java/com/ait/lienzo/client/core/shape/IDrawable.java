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

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.EventReceiver;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeGestureChangeHandler;
import com.ait.lienzo.client.core.event.NodeGestureEndHandler;
import com.ait.lienzo.client.core.event.NodeGestureStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.ait.lienzo.client.core.event.NodeMouseOverHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.event.NodeMouseWheelHandler;
import com.ait.lienzo.client.core.event.NodeTouchCancelHandler;
import com.ait.lienzo.client.core.event.NodeTouchEndHandler;
import com.ait.lienzo.client.core.event.NodeTouchMoveHandler;
import com.ait.lienzo.client.core.event.NodeTouchStartHandler;
import com.ait.lienzo.client.core.shape.guides.IGuidePrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.INodeEvent.Type;

/**
 * Interface to be implemented by drawable objects.
 */
public interface IDrawable<T extends IDrawable<T>> extends EventReceiver {

    T draw();

    T batch();

    Node<?> getParent();

    NodeType getNodeType();

    List<Attribute> getBoundingBoxAttributes();

    T refresh();

    T setID(String id);

    String getID();

    T setUserData(Object data);

    Object getUserData();

    T cancelAttributesChangedBatcher();

    BoundingBox getBoundingBox();

    BoundingPoints getBoundingPoints();

    BoundingPoints getComputedBoundingPoints();

    Point2D getComputedLocation();

    Point2D getAbsoluteLocation();

    Transform getAbsoluteTransform();

    HandlerRegistration addNodeMouseClickHandler(NodeMouseClickHandler handler);

    HandlerRegistration addNodeMouseDoubleClickHandler(NodeMouseDoubleClickHandler handler);

    HandlerRegistration addNodeMouseDownHandler(NodeMouseDownHandler handler);

    HandlerRegistration addNodeMouseMoveHandler(NodeMouseMoveHandler handler);

    HandlerRegistration addNodeMouseOutHandler(NodeMouseOutHandler handler);

    HandlerRegistration addNodeMouseOverHandler(NodeMouseOverHandler handler);

    HandlerRegistration addNodeMouseExitHandler(NodeMouseExitHandler handler);

    HandlerRegistration addNodeMouseEnterHandler(NodeMouseEnterHandler handler);

    HandlerRegistration addNodeMouseUpHandler(NodeMouseUpHandler handler);

    HandlerRegistration addNodeMouseWheelHandler(NodeMouseWheelHandler handler);

    HandlerRegistration addNodeTouchCancelHandler(NodeTouchCancelHandler handler);

    HandlerRegistration addNodeTouchEndHandler(NodeTouchEndHandler handler);

    HandlerRegistration addNodeTouchMoveHandler(NodeTouchMoveHandler handler);

    HandlerRegistration addNodeTouchStartHandler(NodeTouchStartHandler handler);

    HandlerRegistration addNodeGestureStartHandler(NodeGestureStartHandler handler);

    HandlerRegistration addNodeGestureEndHandler(NodeGestureEndHandler handler);

    HandlerRegistration addNodeGestureChangeHandler(NodeGestureChangeHandler handler);

    HandlerRegistration addNodeDragEndHandler(NodeDragEndHandler handler);

    HandlerRegistration addNodeDragMoveHandler(NodeDragMoveHandler handler);

    HandlerRegistration addNodeDragStartHandler(NodeDragStartHandler handler);

    /**
     * Gets the object's {@link Layer}
     *
     * @return Layer
     */
    Layer getLayer();

    /**
     * Gets the object's {@link Scene}
     *
     * @return Scene
     */
    Scene getScene();

    /**
     * Gets the object's {@link Viewport}
     *
     * @return Viewport
     */
    Viewport getViewport();

    /**
     * Gets the viewport's Over Layer {@link Layer}
     *
     * @return Layer
     */
    Layer getOverLayer();

    /**
     * Gets the object's {@link ScratchPad}
     *
     * @return ScratchPad
     */
    ScratchPad getScratchPad();

    /**
     * Returns this object as a {@link Node}
     *
     * @return Node
     */
    Node<?> asNode();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    Viewport asViewport();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    Scene asScene();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    Layer asLayer();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    GroupOf<IPrimitive<?>, ?> asGroupOf();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    Group asGroup();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     *
     * @return Scene
     */
    Shape<?> asShape();

    IMultiPointShape<?> asMultiPointShape();

    /**
     * Returns this object as an {@link IContainer}
     * or null if it is not an IContainer.
     *
     * @return IContainer
     */
    IContainer<?, ?> asContainer();

    ContainerNode<?, ?> asContainerNode();

    /**
     * Returns this object as an {@link IPrimitive}
     * or null if it is not an IPrimitive.
     *
     * @return IPrimitive
     */
    IPrimitive<?> asPrimitive();

    IGuidePrimitive<?> asGuide();

    T setVisible(boolean visible);

    /**
     * Returns whether the object is visible.
     *
     * @return boolean
     */
    boolean isVisible();

    T setListening(boolean listening);

    /**
     * Returns whether the object is listening (i.e. not ignoring) for events
     *
     * @return boolean
     */
    boolean isListening();

    /**
     * Returns whether the given event type has a handler implementation in this
     * object.
     *
     * @param type the event type
     * @return boolean
     */
    <H extends EventHandler> boolean isEventHandled(Type<H> type);

    /**
     * Applies transformations to the object and draws it.
     *
     * @param context
     */
    void drawWithTransforms(Context2D context, double alpha, BoundingBox bounds);

    /**
     * Move the object's {@link Layer} one level up
     *
     * @return T instance of the drawn object
     */
    T moveUp();

    /**
     * Move the object's {@link Layer} one level down
     *
     * @return T instance of the drawn object
     */
    T moveDown();

    /**
     * Move the object's {@link Layer} to the top of the layer stack
     *
     * @return T instance of the drawn object
     */
    T moveToTop();

    /**
     * Move the object's {@link Layer} to the bottom of the layer stack
     *
     * @return T instance of the drawn object
     */
    T moveToBottom();

    boolean removeFromParent();

    /**
     * Animates this node using a tweening animation that runs for the specified duration.
     * <p>
     * Basically invokes {@link #animate(AnimationTweener, AnimationProperties, int, IAnimationCallback)} with a callback of <code>null</code>
     * See that method for more details.
     *
     * @param tweener    {@link AnimationTweener} - determines how the attributes will be changed over time
     * @param properties {@link AnimationProperties} - attributes that will be modified over time
     * @param duration   in milliseconds
     * @return {@link IAnimationHandle}
     */
    IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */);

    /**
     * Animates this node using a tweening animation that runs for the specified duration.
     * The attributes of this node are gradually modified over time.
     * The tweener defines how the attributes are changed over time, e.g. LINEAR or not.
     * See {@link AttributeTweener} for the various non-linear transitions.
     * <p>
     * If a callback is specified, it is called whenever the animation starts, ends and once for every animation frame.
     *
     * @param tweener    {@link AnimationTweener} - determines how the attributes will be changed over time
     * @param properties {@link AnimationProperties} - attributes that will be modified over time
     * @param duration   in milliseconds
     * @param callback   {@link IAnimationCallback}
     * @return {@link IAnimationHandle}
     * @see {@link AnimationManager#add(IPrimitive, AnimationTweener, AnimationProperties, int, IAnimationCallback)}
     */
    IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */, IAnimationCallback callback);

    List<Attribute> getTransformingAttributes();

    String uuid();
}
