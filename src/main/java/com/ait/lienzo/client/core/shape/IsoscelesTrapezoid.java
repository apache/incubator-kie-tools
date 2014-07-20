/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class IsoscelesTrapezoid extends Shape<IsoscelesTrapezoid>
{
    public IsoscelesTrapezoid(double topwidth, double bottomwidth, double height)
    {
        super(ShapeType.ISOSCELES_TRAPEZOID);

        setTopWidth(topwidth).setBottomWidth(bottomwidth).setHeight(height);
    }

    protected IsoscelesTrapezoid(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ISOSCELES_TRAPEZOID, node, ctx);
    }

    @Override
    protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        final double hig = getHeight();

        final double top = getTopWidth();

        final double bot = getBottomWidth();

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

    public IsoscelesTrapezoid setTopWidth(double topwidth)
    {
        getAttributes().setTopWidth(topwidth);

        return this;
    }

    public double getTopWidth()
    {
        return getAttributes().getTopWidth();
    }

    public IsoscelesTrapezoid setBottomWidth(double bottomwidth)
    {
        getAttributes().setBottomWidth(bottomwidth);

        return this;
    }

    public double getBottomWidth()
    {
        return getAttributes().getBottomWidth();
    }

    public IsoscelesTrapezoid setHeight(double height)
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
        public IsoscelesTrapezoid create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new IsoscelesTrapezoid(node, ctx);
        }
    }
}