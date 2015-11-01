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
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public abstract class Decorator<T extends Decorator<T>> extends AbstractMultiPointShape<T>
{
    public static final double DEFAULT_DECORATOR_LENGTH = 30;

    public Decorator(final ShapeType type)
    {
        this(type, DEFAULT_DECORATOR_LENGTH);
    }

    public Decorator(final ShapeType type, final double length)
    {
        super(type);

        setDecoratorLength(length);
    }

    public Decorator(final ShapeType type, final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(type, node, ctx);
    }

    /**
     * Sets the base and head points of the decorator
     * @param base
     * @param head
     */
    public abstract T setDecoratorPoints(Point2D base, Point2D head);

    public double getDecoratorLength()
    {
        return getAttributes().getDecoratorLength();
    }

    public T setDecoratorLength(final double length)
    {
        getAttributes().setDecoratorLength(length);

        return cast();
    }

    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final boolean prepared = isPathPartListPrepared(context, attr, alpha);

        if (prepared)
        {
            context.path(getPathPartList());
        }
        return prepared;
    }

    protected boolean isPathPartListPrepared(final Context2D context, final Attributes attr, final double alpha)
    {
        if (getPathPartList().size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (getPathPartList().size() < 1)
        {
            return false;
        }
        return true;
    }

    protected abstract boolean parse(Attributes attr);

    protected static abstract class AbstractDecoratorFactory<S extends Decorator<S>> extends ShapeFactory<S>
    {
        protected AbstractDecoratorFactory(final ShapeType type)
        {
            super(type);

            addAttribute(Attribute.DECORATOR_LENGTH);
        }
    }
}
