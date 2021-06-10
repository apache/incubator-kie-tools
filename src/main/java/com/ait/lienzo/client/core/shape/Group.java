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

import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.shared.core.types.GroupType;

public class Group extends GroupOf<IPrimitive<?>, Group>
{
    public Group()
    {
        super(GroupType.GROUP, new PrimitiveFastArrayStorageEngine());
    }

    public Group(final IStorageEngine<IPrimitive<?>> storage)
    {
        super(GroupType.GROUP, storage);
    }

    @Override
    public Group asGroup()
    {
        return this;
    }

    @Override
    public final IStorageEngine<IPrimitive<?>> getDefaultStorageEngine()
    {
        return new PrimitiveFastArrayStorageEngine();
    }

    public static class GroupFactory extends GroupOfFactory<IPrimitive<?>, Group>
    {
        public GroupFactory()
        {
            super(GroupType.GROUP);
        }

        @Override
        public boolean addNodeForContainer(final IContainer<?, ?> container, final Node<?> node, final ValidationContext ctx)
        {
            final IPrimitive<?> prim = node.asPrimitive();

            if (null != prim)
            {
                container.asGroup().add(prim);

                return true;
            }
            else
            {
                try
                {
                    ctx.addBadTypeError(node.getClass().getName() + " is not a Primitive");
                }
                catch (ValidationException e)
                {
                    return false;
                }
            }
            return false;
        }
    }
}
