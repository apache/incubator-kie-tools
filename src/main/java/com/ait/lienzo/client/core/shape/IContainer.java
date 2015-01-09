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

import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.shared.java.util.function.Predicate;

/**
 * Interface to be implemented by all primitive collections. 
 */
public interface IContainer<T extends IContainer<T, M>, M> extends Iterable<M>
{
    /**
     * Gets all nodes in this container.
     * 
     * @return FastArrayList
     */
    public NFastArrayList<M> getChildNodes();

    /**
     * Adds a node to this container
     * 
     * @param node
     */
    public T add(M node);
    
    /**
     * Adds a node to this container
     * 
     * @param node
     */
    @SuppressWarnings("unchecked")
    public T add(M node, M... list);

    /**
     * Removes the given node from the container.
     * 
     * @param node
     */
    public T remove(M node);

    /**
     * Removes all nodes from this cotainer.
     */
    public T removeAll();

    /**
     * Moves the node one layer up.
     * 
     * @param node
     */
    public T moveUp(M node);

    /**
     * Modes the node one layer down
     * 
     * @param node
     */
    public T moveDown(M node);

    /**
     * Moves the node to the top of the layer stack
     * 
     * @param node
     */
    public T moveToTop(M node);

    /**
     * Moves the node to the bottom of the layer stack
     * @param node
     */
    public T moveToBottom(M node);

    /**
     * Searches and returns all {@link Node} that match the {@link INodeFilter}
     * 
     * @param filter
     * @return ArrayList
     */
    public Iterable<Node<?>> find(Predicate<Node<?>> predicate);
    
    /**
     * Searches and returns all {@link Node} that match the {@link INodeFilter}
     * 
     * @param filter
     * @return ArrayList
     */
    public void find(Predicate<Node<?>> predicate, LinkedHashSet<Node<?>> buff);
    
    /**
     * Searches and returns all {@link Node} that have a matching ID {@link INodeFilter}
     * 
     * @param filter
     * @return ArrayList
     */
    public Iterable<Node<?>> findByID(String id);

    /**
     * Returns the number of items in this container
     * 
     * @return int
     */
    public int length();

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
}
