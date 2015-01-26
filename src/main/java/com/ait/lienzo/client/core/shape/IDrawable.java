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

import com.ait.lienzo.client.core.Context2D;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Interface to be implemented by drawable objects.
 */
public interface IDrawable<T>
{
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
    public GroupOf<IPrimitive<?>, ?> asGroup();

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

    /**
     * Returns this object as an {@link IPrimitive}
     * or null if it is not an IPrimitive.
     * 
     * @return IPrimitive
     */
    public IPrimitive<?> asPrimitive();

    /**
     * Returns whether the object is visible.
     * 
     * @return boolean
     */
    public boolean isVisible();

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
    public void drawWithTransforms(Context2D context, double alpha);

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
}
