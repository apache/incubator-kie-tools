/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Parallelogram defined by a width, a height and a skew factor.
 * A skew of 0 draws sides that form a 90 degree angle.
 */
public class Parallelogram extends Shape<Parallelogram>
{
    private final PathPartList m_list = new PathPartList();

    /**
     * Constructor. Creates an instance of a parallelogram.
     * 
     * @param width
     * @param height
     * @param skew a skew of 0 draws sides that form a 90 degree angle
     */
    public Parallelogram(final double width, final double height, final double skew)
    {
        super(ShapeType.PARALLELOGRAM);

        setWidth(width).setHeight(height).setSkew(skew);
    }

    public Parallelogram(final double width, final double height, final double skew, final double corner)
    {
        this(width, height, skew);

        setCornerRadius(corner);
    }

    protected Parallelogram(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.PARALLELOGRAM, node, ctx);
    }

    /**
     * Draws this parallelogram.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private boolean parse(final Attributes attr)
    {
        final double wide = attr.getWidth();

        final double high = attr.getHeight();

        if ((wide > 0) && (high > 0))
        {
            final double skew = attr.getSkew();

            final Point2DArray list = new Point2DArray();

            if (skew >= 0)
            {
                list.push(skew, 0);

                list.push(wide, 0);

                list.push(wide - skew, high);

                list.push(0, high);
            }
            else
            {
                list.push(0, 0);

                list.push(wide - Math.abs(skew), 0);

                list.push(wide, high);

                list.push(Math.abs(skew), high);
            }
            final Point2D p0 = list.get(0);

            m_list.M(p0);

            final double corner = getCornerRadius();

            if (corner <= 0)
            {
                final int size = list.size();

                for (int i = 1; i < size; i++)
                {
                    m_list.L(list.get(i));
                }
                m_list.Z();
            }
            else
            {
                Geometry.drawArcJoinedLines(m_list, list.push(p0), corner);
            }
            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, getWidth(), getHeight());
    }

    @Override
    public Parallelogram refresh()
    {
        m_list.clear();

        return this;
    }

    /**
     * Gets the width of this parallelogram
     * 
     * @return double
     */
    public double getWidth()
    {
        return getAttributes().getWidth();
    }

    /**
     * Sets the width of this parallelogram
     * 
     * @param width
     * @return this Parallelogram
     */
    public Parallelogram setWidth(final double width)
    {
        getAttributes().setWidth(width);

        return refresh();
    }

    /**
     * Gets the height of this parallelogram
     * 
     * @return double
     */
    public double getHeight()
    {
        return getAttributes().getHeight();
    }

    /**
     * Sets the height of this parallelogram
     * 
     * @param height
     * @return this Parallelogram
     */
    public Parallelogram setHeight(final double height)
    {
        getAttributes().setHeight(height);

        return refresh();
    }

    /**
     * Gets the skew of this parallelogram.
     * 
     * @return double
     */
    public double getSkew()
    {
        return getAttributes().getSkew();
    }

    /**
     * Sets the skew of this parallelogram
     * 
     * @param skew
     * @return this Parallelogram
     */
    public Parallelogram setSkew(final double skew)
    {
        getAttributes().setSkew(skew);

        return refresh();
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public Parallelogram setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return Arrays.asList(Attribute.WIDTH, Attribute.HEIGHT, Attribute.SKEW);
    }

    public static class ParallelogramFactory extends ShapeFactory<Parallelogram>
    {
        public ParallelogramFactory()
        {
            super(ShapeType.PARALLELOGRAM);

            addAttribute(Attribute.WIDTH, true);

            addAttribute(Attribute.HEIGHT, true);

            addAttribute(Attribute.SKEW, true);

            addAttribute(Attribute.CORNER_RADIUS);
        }

        @Override
        public Parallelogram create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Parallelogram(node, ctx);
        }
    }
}
