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
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class OrthogonalPolyLine extends Shape<OrthogonalPolyLine>
{
    private static final int   NE     = 1;

    private static final int   SE     = 2;

    private static final int   SW     = 3;

    private static final int   NW     = 4;

    private static final int   N      = 1;

    private static final int   E      = 2;

    private static final int   S      = 3;

    private static final int   W      = 4;

    private final PathPartList m_list = new PathPartList();

    public OrthogonalPolyLine(Point2D start, Point2D... points)
    {
        this(new Point2DArray(start, points));
    }

    public OrthogonalPolyLine(Point2DArray points)
    {
        super(ShapeType.ORTHOGONAL_POLYLINE);

        setControlPoints(points);
    }

    public OrthogonalPolyLine(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ORTHOGONAL_POLYLINE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        if (m_list.size() < 1)
        {
            parse(getAttributes());
        }
        return m_list.getBoundingBox();
    }

    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            parse(attr);
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    protected void parse(Attributes attr)
    {
        Point2DArray points = attr.getControlPoints();

        Point2D p1 = points.get(0);

        Point2D p2 = points.get(1);

        m_list.M(p1.getX(), p1.getY());

        if (points.size() == 2)
        {
            m_list.L(p2.getX(), p2.getY());
        }
        else
        {
            NFastDoubleArrayJSO newPoints1 = getOrthonalLinePoints(points, false);

            NFastDoubleArrayJSO newPoints2 = getOrthonalLinePoints(points, true);

            if (newPoints2 == null || (newPoints1 != null && newPoints1.size() < newPoints2.size()))
            {
                addLinePoints(newPoints1);
            }
            else if (newPoints1 == null || (newPoints2 != null && newPoints2.size() < newPoints1.size()))
            {
                addLinePoints(newPoints2);
            }
            else if (newPoints1 != null && newPoints2 != null && newPoints1.size() == newPoints2.size())
            {
                addLinePoints(newPoints1);
            }
            else
            {
                throw new RuntimeException("Defensive Programming. The else should not drop through, we should not have two invalid paths");
            }
        }
    }

    protected void addLinePoints(final NFastDoubleArrayJSO points)
    {
        final int size = points.size();

        for (int i = 0; i < size; i += 2)
        {
            m_list.L(points.get(i), points.get(i + 1));
        }
    }

    private NFastDoubleArrayJSO getOrthonalLinePoints(Point2DArray points, boolean alternative)
    {
        NFastDoubleArrayJSO newPoints = NFastDoubleArrayJSO.make();

        Point2D p1 = points.get(0);

        Point2D p2 = points.get(1);

        int direction = getOrthonalLinePoints(newPoints, p1, p2, points.get(2), alternative);

        p1 = p2;

        final int size = points.size();
        
        for (int i = 2; i < size; i++)
        {
            p2 = points.get(i);

            direction = getOrthonalLinePoints(newPoints, direction, p1, p2);

            if (direction == -1)
            {
                System.out.println(newPoints);
                
                return null;
            }
            p1 = p2;
        }
        return newPoints;
    }

    public int getOrthonalLinePoints(NFastDoubleArrayJSO newPoints, Point2D p1, Point2D p2, Point2D p3, boolean alternative)
    {
        int direction = direction(p1, p2, p3, alternative);

        int secondDirection;

        if (direction == N || direction == S)
        {
            newPoints.push(p1.getX());

            newPoints.push(p2.getY());

            if (p1.getX() == p2.getX())
            {
                secondDirection = direction;
            }
            else if (p1.getX() < p2.getX())
            {
                secondDirection = E;
            }
            else
            {
                secondDirection = W;
            }
        }
        else
        {
            newPoints.push(p2.getX());

            newPoints.push(p1.getY());

            if (p1.getY() == p2.getY())
            {
                secondDirection = direction;
            }
            else if (p1.getY() > p2.getY())
            {
                secondDirection = N;
            }
            else
            {
                secondDirection = S;
            }
        }
        newPoints.push(p2.getX());

        newPoints.push(p2.getY());

        return secondDirection;
    }

    /**
     * Based on the initial three points it determines the direction, and alternative direction,
     * to ensure the orthgonal line can be drawn between the three points.
     *
     * It first determines the quadrant NE, SE, SW and NE. For each quadrant there are two possible
     * directions it can chose. The returned direction is determined by the alternative argument.
     * @param p1
     * @param p2
     * @param p3
     * @param alternative
     * @return
     */
    private int direction(Point2D p1, Point2D p2, Point2D p3, boolean alternative)
    {
        double dx = p2.getX() - p1.getX();

        double dy = p2.getY() - p1.getX();

        int quadrant;

        if (dx > 0 && dy < 0)
        {
            quadrant = NE;
        }
        else if (dx > 0 && dy > 0)
        {
            quadrant = SE;
        }
        else if (dx < 0 && dy > 0)
        {
            quadrant = SW;
        }
        else
        { //if ( dx < 0 && dy > 0 )
            quadrant = NW;
        }
        int direction;

        switch (quadrant)
        {
            case NE:
                if (p3.getX() > p2.getX() && !alternative)
                {
                    direction = N;
                }
                else
                {
                    direction = E;
                }
                break;
            case SE:
                if (p3.getX() > p2.getX() && !alternative)
                {
                    direction = S;
                }
                else
                {
                    direction = E;
                }
                break;
            case SW:
                if (p3.getX() < p2.getX() && !alternative)
                {
                    direction = S;
                }
                else
                {
                    direction = W;
                }
                break;
            case NW:
                if (p3.getX() < p2.getX() && !alternative)
                {
                    direction = N;
                }
                else
                {
                    direction = W;
                }
                break;
            default:
                throw new IllegalStateException("Invalid Direction :" + quadrant);
        }
        return direction;
    }

    /**
     * Draws an orthogonal line between two points, it uses the previous direction to determine the new direction.
     * It will always attempt to continue the line in the same direction if it can do so, without requiring a corner.
     * This helps ensure
     * @param points
     * @param priorDestination
     * @param p1
     * @param p2
     * @return
     */
    public int getOrthonalLinePoints(NFastDoubleArrayJSO points, int priorDestination, Point2D p1, Point2D p2)
    {
        int firstDirection;

        switch (priorDestination)
        {
            case N:
                if (p2.getY() > p1.getY() && p2.getX() == p1.getX())
                {
                    // a line cannot go back on itself
                    return -1;
                }
                if (p2.getY() < p1.getY())
                {
                    firstDirection = N;
                }
                else if (p2.getX() > p1.getX())
                {
                    firstDirection = E;
                }
                else
                {
                    firstDirection = W;
                }
                break;
            case E:
                if (p2.getX() < p1.getX() && p2.getY() == p1.getY())
                {
                    // a line cannot go back on itself
                    return -1;
                }
                if (p2.getX() > p1.getX())
                {
                    firstDirection = E;
                }
                else if (p2.getY() < p1.getY())
                {
                    firstDirection = N;
                }
                else
                {
                    firstDirection = S;
                }
                break;
            case S:
                if (p2.getY() < p1.getY() && p2.getX() == p1.getX())
                {
                    // a line cannot go back on itself
                    return -1;
                }
                if (p2.getY() > p1.getY())
                {
                    firstDirection = S;
                }
                else if (p2.getX() > p1.getX())
                {
                    firstDirection = E;
                }
                else
                {
                    firstDirection = W;
                }
                break;
            case W:
                if (p2.getX() > p1.getX() && p2.getY() == p1.getY())
                {
                    // a line cannot go back on itself
                    return -1;
                }
                if (p2.getX() < p1.getX())
                {
                    firstDirection = W;
                }
                else if (p2.getY() < p1.getY())
                {
                    firstDirection = N;
                }
                else
                {
                    firstDirection = S;
                }
                break;
            default:
                throw new IllegalStateException("Invalid Direction :" + priorDestination);
        }
        int secondDirection;

        if (firstDirection == N || firstDirection == S)
        {
            points.push(p1.getX());

            points.push(p2.getY());

            if (p1.getX() == p2.getX())
            {
                secondDirection = firstDirection;
            }
            else if (p1.getX() < p2.getX())
            {
                secondDirection = E;
            }
            else
            {
                secondDirection = W;
            }
        }
        else
        {
            points.push(p2.getX());

            points.push(p1.getY());

            if (p1.getY() == p2.getY())
            {
                secondDirection = firstDirection;
            }
            else if (p1.getY() > p2.getY())
            {
                secondDirection = N;
            }
            else
            {
                secondDirection = S;
            }
        }
        points.push(p2.getX());

        points.push(p2.getY());

        return secondDirection;
    }

    @Override
    public void fill(Context2D context, Attributes attr, double alpha)
    {
    }

    /**
     * Returns this PolyLine's points.
     * @return {@link Point2DArray}
     */
    public Point2DArray getControlPoints()
    {
        return getAttributes().getControlPoints();
    }

    /**
     * Sets this PolyLine's points.
     * @param points {@link Point2DArray}
     * @return this PolyLine
     */
    public OrthogonalPolyLine setControlPoints(Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        m_list.clear();

        return this;
    }

    @Override
    public IFactory<OrthogonalPolyLine> getFactory()
    {
        return new OrthogonaPolylLineFactory();
    }

    public static class OrthogonaPolylLineFactory extends ShapeFactory<OrthogonalPolyLine>
    {
        public OrthogonaPolylLineFactory()
        {
            super(ShapeType.ORTHOGONAL_POLYLINE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public OrthogonalPolyLine create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new OrthogonalPolyLine(node, ctx);
        }
    }
}
