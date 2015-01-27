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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractDirectionalMultiPointShape<T extends AbstractDirectionalMultiPointShape<T> & IDirectionalMultiPointShape<T>> extends AbstractOffsetMultiPointShape<T> implements IDirectionalMultiPointShape<T>
{
    protected AbstractDirectionalMultiPointShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractDirectionalMultiPointShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public IDirectionalMultiPointShape<?> asDirectionalMultiPointShape()
    {
        return this;
    }

    @Override
    public Direction getHeadDirection()
    {
        return getAttributes().getHeadDirection();
    }

    @Override
    public T setHeadDirection(final Direction direction)
    {
        getAttributes().setHeadDirection(direction);

        return refresh();
    }

    @Override
    public Direction getTailDirection()
    {
        return getAttributes().getTailDirection();
    }

    @Override
    public T setTailDirection(final Direction direction)
    {
        getAttributes().setTailDirection(direction);

        return refresh();
    }

    protected static abstract class AbstractDirectionalMultiPointShapeFactory<T extends AbstractDirectionalMultiPointShape<T>> extends AbstractOffsetMultiPointShapeFactory<T>
    {
        protected AbstractDirectionalMultiPointShapeFactory(final ShapeType type)
        {
            super(type);

            addAttribute(Attribute.HEAD_DIRECTION);

            addAttribute(Attribute.TAIL_DIRECTION);
        }
    }
}
