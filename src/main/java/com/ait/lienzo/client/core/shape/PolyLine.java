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
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * PolyLine is a continuous line composed of one or more line segments.
 * To create a dashed PolyLine, use one of the setDashArray() methods. 
 */
public class PolyLine extends Shape<PolyLine>
{
    /**
     * Constructor. Creates an instance of a polyline.
     * 
     * @param points a {@link Point2DArray} containing 2 or more points.
     */
    public PolyLine(Point2DArray points)
    {
        super(ShapeType.POLYLINE);

        setPoints(points);
    }

    protected PolyLine(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.POLYLINE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getPoints().getBoundingBox();
    }

    /**
     * Draws this polyline.
     * 
     * @param context
     */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        Point2DArray list = getPoints();

        if ((null != list) && (list.size() >= 2))
        {
            final int leng = list.size();

            Point2D point = list.getPoint(0);

            context.beginPath();

            context.moveTo(point.getX(), point.getY());

            for (int i = 1; i < leng; i++)
            {
                point = list.getPoint(i);

                context.lineTo(point.getX(), point.getY());
            }
            return true;
        }
        return false;
    }

    @Override
    public void fill(Context2D context, Attributes attr, double alpha)
    {

    }

    /**
     * Returns this PolyLine's points.
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    /**
     * Sets this PolyLine's points.
     * @param points {@link Point2DArray}
     * @return this PolyLine
     */
    public PolyLine setPoints(Point2DArray points)
    {
        getAttributes().setPoints(points);

        return this;
    }

    @Override
    public IFactory<PolyLine> getFactory()
    {
        return new PolyLineFactory();
    }

    public static class PolyLineFactory extends ShapeFactory<PolyLine>
    {
        public PolyLineFactory()
        {
            super(ShapeType.POLYLINE);

            addAttribute(Attribute.POINTS, true);
        }

        @Override
        public PolyLine create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new PolyLine(node, ctx);
        }
    }
}
