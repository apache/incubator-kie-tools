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

import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import java.util.function.Predicate;

/**
 * Interface to be implemented by all primitive collections. 
 */
public interface IContainer<T extends IContainer<T, M>, M> extends IDrawable<T>
{
    /**
     * Gets all nodes in this container.
     * 
     * @return FastArrayList
     */
    NFastArrayList<M> getChildNodes();

    /**
     * Gets all nodes in this container.
     * 
     * @return FastArrayList
     */
    NFastArrayList<M> getChildNodes(BoundingBox bounds);

    T setStorageEngine(IStorageEngine<M> storage);

    IStorageEngine<M> getStorageEngine();

    IStorageEngine<M> getDefaultStorageEngine();

    T setStorageBounds(BoundingBox bounds);

    BoundingBox getStorageBounds();

    T setPathClipper(IPathClipper clipper);

    IPathClipper getPathClipper();

    /**
     * Adds a node to this container
     * 
     * @param node
     */
    T add(M node);

    /**
     * Adds a node to this container
     * 
     * @param node
     */
    @SuppressWarnings("unchecked")
    T add(M node, M... list);

    /**
     * Removes the given node from the container.
     * 
     * @param node
     */
    T remove(M node);

    /**
     * Removes all nodes from this cotainer.
     */
    T removeAll();

    /**
     * Moves the node one layer up.
     * 
     * @param node
     */
    T moveUp(M node);

    /**
     * Modes the node one layer down
     * 
     * @param node
     */
    T moveDown(M node);

    /**
     * Moves the node to the top of the layer stack
     * 
     * @param node
     */
    T moveToTop(M node);

    /**
     * Moves the node to the bottom of the layer stack
     * @param node
     */
    T moveToBottom(M node);

    /**
     * Searches and returns all {@link Node} that match the {@link INodeFilter}
     * 
     * @param filter
     * @return ArrayList
     */
    Iterable<Node<?>> find(Predicate<Node<?>> predicate);

    /**
     * Searches and returns all {@link Node} that have a matching ID {@link INodeFilter}
     * 
     * @param filter
     * @return ArrayList
     */
    Iterable<Node<?>> findByID(String id);

    /**
     * Returns the number of items in this container
     * 
     * @return int
     */
    int length();
}
