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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class AbstractOffsetMultiPointShape<T extends AbstractOffsetMultiPointShape<T> & IOffsetMultiPointShape<T>> extends AbstractMultiPointShape<T> implements IOffsetMultiPointShape<T>
{
    protected AbstractOffsetMultiPointShape(final ShapeType type)
    {
        super(type);
    }

    protected AbstractOffsetMultiPointShape(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    @Override
    public IOffsetMultiPointShape<?> asOffsetMultiPointShape()
    {
        return this;
    }

    @Override
    public double getTailOffset()
    {
        return getAttributes().getTailOffset();
    }

    @Override
    public T setTailOffset(final double offset)
    {
        getAttributes().setTailOffset(offset);

        return refresh();
    }

    @Override
    public double getHeadOffset()
    {
        return getAttributes().getHeadOffset();
    }

    @Override
    public T setHeadOffset(final double offset)
    {
        getAttributes().setHeadOffset(offset);

        return refresh();
    }

    @Override
    public double getCorrectionOffset()
    {
        return getAttributes().getCorrectionOffset();
    }

    @Override
    public T setCorrectionOffset(final double offset)
    {
        getAttributes().setCorrectionOffset(offset);

        return refresh();
    }

    protected static abstract class AbstractOffsetMultiPointShapeFactory<T extends AbstractOffsetMultiPointShape<T>> extends ShapeFactory<T>
    {
        protected AbstractOffsetMultiPointShapeFactory(final ShapeType type)
        {
            super(type);

            addAttribute(Attribute.HEAD_OFFSET);

            addAttribute(Attribute.TAIL_OFFSET);

            addAttribute(Attribute.CORRECTION_OFFSET);
        }
    }
}
