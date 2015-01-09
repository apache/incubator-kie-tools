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

import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.GroupType;
import com.google.gwt.json.client.JSONObject;

public class Group extends GroupOf<IPrimitive<?>, Group>
{
    public Group()
    {
        super(GroupType.GROUP);
    }

    protected Group(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(GroupType.GROUP, node, ctx);
    }

    @Override
    public IFactory<Group> getFactory()
    {
        return new GroupFactory();
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

        @Override
        public Group container(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Group(node, ctx);
        }
    }
}
