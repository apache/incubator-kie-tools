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

package com.ait.lienzo.client.core.shape.storage;

import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public abstract class AbstractFastArrayStorageEngine<M> extends AbstractStorageEngine<M>
{
    private final NFastArrayList<M> m_list = new NFastArrayList<M>();

    protected AbstractFastArrayStorageEngine(final StorageEngineType type)
    {
        super(type);
    }

    protected AbstractFastArrayStorageEngine(final StorageEngineType type, final Object node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public int size()
    {
        return m_list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_list.isEmpty();
    }

    @Override
    public void clear()
    {
        m_list.clear();
    }

    @Override
    public boolean contains(final M item)
    {
        return m_list.contains(item);
    }

    @Override
    public void add(final M item)
    {
        m_list.add(item);
    }

    @Override
    public void remove(final M item)
    {
        m_list.remove(item);
    }

    @Override
    public void refresh(M item)
    {
    }

    @Override
    public void refresh()
    {
    }

    @Override
    public NFastArrayList<M> getChildren()
    {
        return m_list;
    }

    @Override
    public NFastArrayList<M> getChildren(BoundingBox bounds)
    {
        return m_list;
    }

    @Override
    public boolean isSpatiallyIndexed()
    {
        return false;
    }

    @Override
    public void moveUp(final M item)
    {
        m_list.moveUp(item);
    }

    @Override
    public void moveDown(final M item)
    {
        m_list.moveDown(item);
    }

    @Override
    public void moveToTop(final M item)
    {
        m_list.moveToTop(item);
    }

    @Override
    public void moveToBottom(final M item)
    {
        m_list.moveToBottom(item);
    }

    public abstract static class FastArrayStorageEngineFactory<S extends IJSONSerializable<S>> extends AbstractStorageEngineFactory<S>
    {
        protected FastArrayStorageEngineFactory(final StorageEngineType type)
        {
            super(type);
        }
    }
}
