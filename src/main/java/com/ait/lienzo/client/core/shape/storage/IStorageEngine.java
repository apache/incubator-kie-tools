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

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.tools.client.collection.MetaData;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public interface IStorageEngine<M> extends IStorageEngineIterable<M>
{
    int size();

    void clear();

    boolean isEmpty();

    void add(M item);

    void remove(M item);

    void refresh();

    void refresh(M item);

    boolean contains(M item);

    NFastArrayList<M> getChildren();

    NFastArrayList<M> getChildren(BoundingBox bounds);

    boolean isSpatiallyIndexed();

    MetaData getMetaData();

    StorageEngineType getStorageEngineType();

    void moveUp(M item);

    void moveDown(M item);

    void moveToTop(M item);

    void moveToBottom(M item);

    void migrate(IStorageEngine<M> storage);
}
