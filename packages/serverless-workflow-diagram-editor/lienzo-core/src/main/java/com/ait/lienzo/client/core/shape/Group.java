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
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class Group extends GroupOf<IPrimitive<?>, Group> {

    public Group() {
        super(GroupType.GROUP, new PrimitiveFastArrayStorageEngine());
    }

    @Override
    public Group asGroup() {
        return this;
    }

    @Override
    public final IStorageEngine<IPrimitive<?>> getDefaultStorageEngine() {
        return new PrimitiveFastArrayStorageEngine();
    }

    public NFastArrayList<IPrimitive<?>> getChildren() {
        return this.getChildNodes();
    }
}
