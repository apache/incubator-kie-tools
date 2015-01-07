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
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class IsoscelesTrapezoid extends Shape<IsoscelesTrapezoid>
{
    public IsoscelesTrapezoid(final double topwidth, final double bottomwidth, final double height)
    {
        super(ShapeType.ISOSCELES_TRAPEZOID);

        setTopWidth(topwidth).setBottomWidth(bottomwidth).setHeight(height);
    }

    protected IsoscelesTrapezoid(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ISOSCELES_TRAPEZOID, node, ctx);
    }

    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double hig = attr.getHeight();

        final double top = attr.getTopWidth();

        final double bot = attr.getBottomWidth();

        if ((hig > 0) && (top > 0) && (bot > 0))
        {
            context.beginPath();

            final double sub = Math.abs(top - bot);

            if (0 == sub)
            {
                context.rect(0, 0, top, hig);
            }
            else
            {
                if (top > bot)
                {
                    context.moveTo(0, 0);

                    context.lineTo(top, 0);

                    context.lineTo((sub / 2.0) + bot, hig);

                    context.lineTo((sub / 2.0), hig);
                }
                else
                {
                    context.moveTo((sub / 2.0), 0);

                    context.lineTo((sub / 2.0) + top, 0);

                    context.lineTo(bot, hig);

                    context.lineTo(0, hig);
                }
            }
            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, Math.max(getTopWidth(), getBottomWidth()), getHeight());
    }

    public IsoscelesTrapezoid setTopWidth(final double topwidth)
    {
        getAttributes().setTopWidth(topwidth);

        return this;
    }

    public double getTopWidth()
    {
        return getAttributes().getTopWidth();
    }

    public IsoscelesTrapezoid setBottomWidth(final double bottomwidth)
    {
        getAttributes().setBottomWidth(bottomwidth);

        return this;
    }

    public double getBottomWidth()
    {
        return getAttributes().getBottomWidth();
    }

    public IsoscelesTrapezoid setHeight(final double height)
    {
        getAttributes().setHeight(height);

        return this;
    }

    public double getHeight()
    {
        return getAttributes().getHeight();
    }

    @Override
    public IFactory<IsoscelesTrapezoid> getFactory()
    {
        return new IsoscelesTrapezoidFactory();
    }

    public static class IsoscelesTrapezoidFactory extends ShapeFactory<IsoscelesTrapezoid>
    {
        public IsoscelesTrapezoidFactory()
        {
            super(ShapeType.ISOSCELES_TRAPEZOID);

            addAttribute(Attribute.TOP_WIDTH, true);

            addAttribute(Attribute.BOTTOM_WIDTH, true);

            addAttribute(Attribute.HEIGHT, true);
        }

        @Override
        public IsoscelesTrapezoid create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new IsoscelesTrapezoid(node, ctx);
        }
    }
}