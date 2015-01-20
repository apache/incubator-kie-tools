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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.java.util.function.Predicate;
import com.google.gwt.json.client.JSONObject;

/**
 * ContainerNode acts as a Collection holder for primitives.
 * 
 * <ul>
 * <li>A ContainerNode may contain {@link Layer} or {@link Group}.</li>
 * <li>A Container handles collection operations such as add, remove and removeAll.</li>
 * </ul>
 * 
 * @param <T>
 */
public abstract class ContainerNode<M extends IDrawable<?>, T extends ContainerNode<M, T>> extends Node<T> implements IContainer<T, M>, IDrawable<T>, Iterable<M>
{
    private final NFastArrayList<M> m_list = new NFastArrayList<M>();

    protected ContainerNode(final NodeType type)
    {
        super(type);
    }

    protected ContainerNode(final NodeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public T copy()
    {
        final Node<?> node = copyUnchecked();

        if (null == node)
        {
            return null;
        }
        if (getNodeType() != node.getNodeType())
        {
            return null;
        }
        return node.cast();
    }

    /**
     * Returns a {@link NFastArrayList} containing all children.
     */
    @Override
    public NFastArrayList<M> getChildNodes()
    {
        return m_list;
    }

    @Override
    public int length()
    {
        return m_list.size();
    }

    /**
     * Adds a primitive to the collection.
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public T add(final M child)
    {
        final Node<?> node = child.asNode();

        node.setParent(this);

        m_list.add(child);

        return cast();
    }

    /**
     * Removes a primitive from the container.
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public T remove(final M child)
    {
        final Node<?> node = child.asNode();

        node.setParent(null);

        m_list.remove(child);

        return cast();
    }

    /**
     * Removes all primitives from the collection.
     * <p>
     * It should be noted that this operation will not have an apparent effect for an already rendered (drawn) Container.
     * In other words, if the Container has already been drawn and a new primitive is added, you'll need to invoke draw() on the
     * Container. This is done to enhance performance, otherwise, for every add we would have draws impacting performance.
     */
    @Override
    public T removeAll()
    {
        m_list.clear();

        return cast();
    }

    /**
     * Used internally. Draws the node in the current Context2D
     * without applying the transformation-related attributes 
     * (e.g. X, Y, ROTATION, SCALE, SHEAR, OFFSET and TRANSFORM.)
     * <p> 
     * Groups should draw their children in the current context.
     */
    @Override
    protected void drawWithoutTransforms(final Context2D context, double alpha)
    {
        if ((context.isSelection()) && (false == isListening()))
        {
            return;
        }
        alpha = alpha * getAttributes().getAlpha();

        if (alpha <= 0)
        {
            return;
        }
        final int size = m_list.size();

        for (int i = 0; i < size; i++)
        {
            m_list.get(i).drawWithTransforms(context, alpha);
        }
    }

    /**
     * Moves the {@link Layer} up
     */
    @Override
    public T moveUp(final M node)
    {
        getChildNodes().moveUp(node);

        return cast();
    }

    /**
     * Moves the {@link Layer} down
     */
    @Override
    public T moveDown(final M node)
    {
        getChildNodes().moveDown(node);

        return cast();
    }

    /**
     * Moves the {@link Layer} to the top
     */
    @Override
    public T moveToTop(final M node)
    {
        getChildNodes().moveToTop(node);

        return cast();
    }

    /**
     * Moves the {@link Layer} to the bottom
     */
    @Override
    public T moveToBottom(final M node)
    {
        getChildNodes().moveToBottom(node);

        return cast();
    }

    @Override
    public final Iterable<Node<?>> findByID(String id)
    {
        if ((null == id) || ((id = id.trim()).isEmpty()))
        {
            return new ArrayList<Node<?>>(0);
        }
        final String look = id;

        return find(new Predicate<Node<?>>()
        {
            @Override
            public boolean test(Node<?> node)
            {
                if (null == node)
                {
                    return false;
                }
                String id = node.getAttributes().getID();

                if ((null != id) && (false == (id = id.trim()).isEmpty()))
                {
                    return id.equals(look);
                }
                return false;
            }
        });
    }

    @Override
    public final Iterable<Node<?>> find(final Predicate<Node<?>> predicate)
    {
        final LinkedHashSet<Node<?>> buff = new LinkedHashSet<Node<?>>();

        find(predicate, buff);

        return buff;
    }

    @Override
    public Iterator<M> iterator()
    {
        return new ContainerNodeIterator();
    }

    private class ContainerNodeIterator implements Iterator<M>
    {
        private int m_indx = 0;

        private int m_last = -1;

        @Override
        public boolean hasNext()
        {
            return (m_indx != length());
        }

        @Override
        public M next()
        {
            if (m_indx >= length())
            {
                throw new NoSuchElementException();
            }
            M next = getChildNodes().get(m_indx);

            m_last = m_indx++;

            return next;
        }

        @Override
        public void remove()
        {
            if (m_last == -1)
            {
                throw new IllegalStateException();
            }
            if (m_last >= length())
            {
                throw new NoSuchElementException();
            }
            M last = getChildNodes().get(m_last);

            ContainerNode.this.remove(last);

            if (m_last < m_indx)
            {
                m_indx--;
            }
            m_last = -1;
        }
    }
}