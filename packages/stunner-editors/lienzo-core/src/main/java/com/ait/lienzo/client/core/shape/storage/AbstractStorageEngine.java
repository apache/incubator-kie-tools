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

import java.util.Iterator;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.NFastArrayListIterator;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public abstract class AbstractStorageEngine<M> implements IStorageEngine<M> {

    private final StorageEngineType m_type;

    protected AbstractStorageEngine(final StorageEngineType type) {
        m_type = type;
    }

    protected AbstractStorageEngine(final StorageEngineType type, final Object node) {
        m_type = type;
    }

    @Override
    public void migrate(final IStorageEngine<M> storage) {
        if (null != storage) {
            final NFastArrayList<M> list = storage.getChildren();

            if (null != list) {
                final int size = list.size();

                for (int i = 0; i < size; i++) {
                    add(list.get(i));
                }
            }
        }
    }

    @Override
    public StorageEngineType getStorageEngineType() {
        return m_type;
    }

    @Override
    public Iterator<M> iterator(final BoundingBox bounds) {
        return new NFastArrayListIterator<M>(getChildren(bounds));
    }

    @Override
    public Iterator<M> iterator() {
        return new NFastArrayListIterator<M>(getChildren());
    }
}
