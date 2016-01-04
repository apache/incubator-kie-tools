/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.Collection;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
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
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.tooling.nativetools.client.collection.MetaData;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Interface to be implemented by drawable objects.
 */
public interface IDrawable<T extends IDrawable<T>> extends IJSONSerializable<T>
{
    public T copy();

    public T draw();

    public T batch();

    public MetaData getMetaData();

    public Collection<Attribute> getAttributeSheet();

    public Collection<Attribute> getRequiredAttributes();

    public Node<?> getParent();

    public NodeType getNodeType();

    public Attributes getAttributes();

    public List<Attribute> getBoundingBoxAttributes();
    
    public T refresh();

    public T setName(String name);

    public String getName();

    public T setID(String id);

    public String getID();
    
    public T setUserData(Object data);
    
    public Object getUserData();

    public T setAttributesChangedBatcher(IAttributesChangedBatcher batcher);

    public T cancelAttributesChangedBatcher();

    public BoundingBox getBoundingBox();

    public BoundingPoints getBoundingPoints();

    public Point2D getAbsoluteLocation();

    public HandlerRegistration addAttributesChangedHandler(Attribute attribute, AttributesChangedHandler handler);

    public HandlerRegistration addNodeMouseClickHandler(NodeMouseClickHandler handler);

    public HandlerRegistration addNodeMouseDoubleClickHandler(NodeMouseDoubleClickHandler handler);

    public HandlerRegistration addNodeMouseDownHandler(NodeMouseDownHandler handler);

    public HandlerRegistration addNodeMouseMoveHandler(NodeMouseMoveHandler handler);

    public HandlerRegistration addNodeMouseOutHandler(NodeMouseOutHandler handler);

    public HandlerRegistration addNodeMouseOverHandler(NodeMouseOverHandler handler);

    public HandlerRegistration addNodeMouseExitHandler(NodeMouseExitHandler handler);

    public HandlerRegistration addNodeMouseEnterHandler(NodeMouseEnterHandler handler);

    public HandlerRegistration addNodeMouseUpHandler(NodeMouseUpHandler handler);

    public HandlerRegistration addNodeMouseWheelHandler(NodeMouseWheelHandler handler);

    public HandlerRegistration addNodeTouchCancelHandler(NodeTouchCancelHandler handler);

    public HandlerRegistration addNodeTouchEndHandler(NodeTouchEndHandler handler);

    public HandlerRegistration addNodeTouchMoveHandler(NodeTouchMoveHandler handler);

    public HandlerRegistration addNodeTouchStartHandler(NodeTouchStartHandler handler);

    public HandlerRegistration addNodeGestureStartHandler(NodeGestureStartHandler handler);

    public HandlerRegistration addNodeGestureEndHandler(NodeGestureEndHandler handler);

    public HandlerRegistration addNodeGestureChangeHandler(NodeGestureChangeHandler handler);

    public HandlerRegistration addNodeDragEndHandler(NodeDragEndHandler handler);

    public HandlerRegistration addNodeDragMoveHandler(NodeDragMoveHandler handler);

    public HandlerRegistration addNodeDragStartHandler(NodeDragStartHandler handler);

    /**
     * Gets the object's {@link Layer} 
     * 
     * @return Layer
     */
    public Layer getLayer();

    /**
     * Gets the object's {@link Scene}
     * 
     * @return Scene
     */
    public Scene getScene();

    /**
     * Gets the object's {@link Viewport}
     * 
     * @return Viewport
     */
    public Viewport getViewport();

    /**
     * Gets the viewport's Over Layer {@link Layer}
     * 
     * @return Layer
     */
    public Layer getOverLayer();

    /**
     * Gets the object's {@link ScratchPad}
     * 
     * @return ScratchPad
     */
    public ScratchPad getScratchPad();

    /**
     * Returns this object as a {@link Node}
     * 
     * @return Node
     */
    public Node<?> asNode();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public Viewport asViewport();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public Scene asScene();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public Layer asLayer();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public GroupOf<IPrimitive<?>, ?> asGroupOf();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public Group asGroup();

    /**
     * Returns this object as a {@link Scene}
     * or null if it not a Scene.
     * 
     * @return Scene
     */
    public Shape<?> asShape();

    public IMultiPointShape<?> asMultiPointShape();

    /**
     * Returns this object as an {@link IContainer}
     * or null if it is not an IContainer.
     * 
     * @return IContainer
     */
    public IContainer<?, ?> asContainer();

    public ContainerNode<?, ?> asContainerNode();

    /**
     * Returns this object as an {@link IPrimitive}
     * or null if it is not an IPrimitive.
     * 
     * @return IPrimitive
     */
    public IPrimitive<?> asPrimitive();

    public IGuidePrimitive<?> asGuide();

    public T setVisible(boolean visible);

    /**
     * Returns whether the object is visible.
     * 
     * @return boolean
     */
    public boolean isVisible();

    public T setListening(boolean listening);

    /**
     * Returns whether the object is listening (i.e. not ignoring) for events
     * 
     * @return boolean
     */
    public boolean isListening();

    /**
     * Returns whether the given event type has a handler implementation in this
     * object.
     * 
     * @param type the event type
     * @return boolean
     */
    public boolean isEventHandled(Type<?> type);

    /**
     * Fires off the given GWT event.
     * 
     * @param event
     */
    public void fireEvent(GwtEvent<?> event);

    /**
     * Applies transformations to the object and draws it.
     * 
     * @param context
     */
    public void drawWithTransforms(Context2D context, double alpha, BoundingBox bounds);

    /**
     * Move the object's {@link Layer} one level up
     * 
     * @return T instance of the drawn object
     */
    public T moveUp();

    /**
     * Move the object's {@link Layer} one level down
     * 
     * @return T instance of the drawn object
     */
    public T moveDown();

    /**
     * Move the object's {@link Layer} to the top of the layer stack
     * 
     * @return T instance of the drawn object
     */
    public T moveToTop();

    /**
     * Move the object's {@link Layer} to the bottom of the layer stack
     * 
     * @return T instance of the drawn object
     */
    public T moveToBottom();

    public boolean removeFromParent();

    /**
     * Animates this node using a tweening animation that runs for the specified duration.
     * <p>
     * Basically invokes {@link #animate(AnimationTweener, AnimationProperties, int, IAnimationCallback)} with a callback of <code>null</code>
     * See that method for more details.
     * 
     * @param tweener {@link AnimationTweener} - determines how the attributes will be changed over time
     * @param properties {@link AnimationProperties} - attributes that will be modified over time
     * @param duration in milliseconds
     * @return {@link IAnimationHandle}
     */
    public IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */);

    /**
     * Animates this node using a tweening animation that runs for the specified duration.
     * The attributes of this node are gradually modified over time. 
     * The tweener defines how the attributes are changed over time, e.g. LINEAR or not.
     * See {@link AttributeTweener} for the various non-linear transitions. 
     * <p>
     * If a callback is specified, it is called whenever the animation starts, ends and once for every animation frame.
     * 
     * @param tweener {@link AnimationTweener} - determines how the attributes will be changed over time
     * @param properties {@link AnimationProperties} - attributes that will be modified over time
     * @param duration in milliseconds
     * @param callback {@link IAnimationCallback}
     * @return {@link IAnimationHandle}
     * 
     * @see {@link AnimationManager#add(IPrimitive, AnimationTweener, AnimationProperties, int, IAnimationCallback)}
     */
    public IAnimationHandle animate(AnimationTweener tweener, AnimationProperties properties, double duration /* milliseconds */, IAnimationCallback callback);

    public List<Attribute> getTransformingAttributes();

    public String uuid();
}
