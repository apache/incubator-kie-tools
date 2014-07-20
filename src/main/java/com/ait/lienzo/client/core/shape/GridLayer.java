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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.JSONDeserializer;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.NodeType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * GridLayer is a layer that draws a grid behind its child nodes.
 * For each direction (X for vertical lines) and (Y for horizontal lines)
 * you may define a primary line and a secondary line.
 * <p>
 * For instance, you could draw a primary line every 100 pixels 
 * and a secondary line every 10 pixels. 
 * <p>
 * We're assuming that the primary cell size is a multiple of the secondary cell size 
 * (in the same direction.)
 * <p>
 * The strokeWidth of the lines is impervious to any transforms defined on the Layer or its Viewport,
 * i.e. a 1 pixel line will always show as a 1 pixel line, regardless of how far you zoomed in or out.
 * <p>
 * Note that the empty GridLayer constructor does not add any Lines, so you will not see a grid unless you add some Lines.
 * 
 * @since 1.1
 */
public class GridLayer extends Layer
{
    private static final int X           = 0;

    private static final int Y           = 1;

    private static final int PRIMARY_X   = 0;

    private static final int PRIMARY_Y   = 1;

    private static final int SECONDARY_X = 2;

    private static final int SECONDARY_Y = 3;

    private double[]         m_sizes     = { 10, 10, 5, 5 };

    private Line[]           m_lines     = new Line[4];

    // NOTE: we can't put Lines in Attributes

    /**
     * Creates an empty GridLayer with no lines.
     * Horizontal and/or vertical lines can be added with 
     * {@link #setPrimaryLineX(Line)}, {@link #setPrimaryLineY(Line)},
     * {@link #setSecondaryLineX(Line)} and {@link #setSecondaryLineY(Line)}.
     */
    public GridLayer()
    {
        setNodeType(NodeType.GRID_LAYER);
    }

    /**
     * Creates a GridLayer with primary lines only.
     * 
     * @param size Width/height of the primary grid cells
     * @param line Defines how primary lines are drawn
     */
    public GridLayer(double size, Line line)
    {
        setNodeType(NodeType.GRID_LAYER);

        setPrimarySizeX(size);

        setPrimarySizeY(size);

        setPrimaryLineX(line);

        setPrimaryLineY(line);
    }

    /**
     * Creates a GridLayer with primary and secondary lines.
     * The lines look the same in the vertical and horizontal directions.
     * 
     * @param primarySize Width/height of the primary grid cells
     * @param primaryLine Defines how primary lines are drawn
     * @param secondarySize Width/height of the secondary grid cells
     * @param secondaryLine Defines how secondary lines are drawn
     */
    public GridLayer(double primarySize, Line primaryLine, double secondarySize, Line secondaryLine)
    {
        this(primarySize, primaryLine);

        setSecondarySizeX(secondarySize);

        setSecondarySizeY(secondarySize);

        setSecondaryLineX(secondaryLine);

        setSecondaryLineY(secondaryLine);
    }

    protected GridLayer(JSONObject node, ValidationContext ctx, Line[] lines, double[] sizes) throws ValidationException
    {
        super(node, ctx);

        setNodeType(NodeType.GRID_LAYER);

        m_lines = lines;

        m_sizes = sizes;
    }

    /**
     * Returns the width of the primary grid cells.
     * The default value is 10.
     * 
     * @return double
     */
    public double getPrimarySizeX()
    {
        return m_sizes[PRIMARY_X];
    }

    /**
     * Sets the width of the primary grid cells.
     * The default value is 10.
     * 
     * @param primaryX
     * @return this GridLayer
     */
    public GridLayer setPrimarySizeX(double primaryX)
    {
        m_sizes[PRIMARY_X] = primaryX;

        return this;
    }

    /**
     * Returns the height of the primary grid cells.
     * The default value is 10.
     * 
     * @return double
     */
    public double getPrimarySizeY()
    {
        return m_sizes[PRIMARY_Y];
    }

    /**
     * Sets the width of the primary grid cells.
     * The default value is 10.
     * 
     * @param primaryY
     * @return this GridLayer
     */
    public GridLayer setPrimarySizeY(double primaryY)
    {
        m_sizes[PRIMARY_Y] = primaryY;

        return this;
    }

    /**
     * Returns the {@link Line} that defines how vertical primary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @return Line
     */
    public Line getPrimaryLineX()
    {
        return m_lines[PRIMARY_X];
    }

    /**
     * Sets the {@link Line} that defines how vertical primary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @param primaryLineX Line
     * @return GridLayer
     */
    public GridLayer setPrimaryLineX(Line primaryLineX)
    {
        m_lines[PRIMARY_X] = primaryLineX;

        return this;
    }

    /**
     * Returns the {@link Line} that defines how horizontal primary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @return Line
     */
    public Line getPrimaryLineY()
    {
        return m_lines[PRIMARY_Y];
    }

    /**
     * Sets the {@link Line} that defines how horizontal primary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @param primaryLineY Line
     * @return GridLayer
     */
    public GridLayer setPrimaryLineY(Line primaryLineY)
    {
        m_lines[PRIMARY_Y] = primaryLineY;

        return this;
    }

    /**
     * Returns the width of the secondary grid cells.
     * The default value is 5.
     * 
     * @return double
     */
    public double getSecondarySizeX()
    {
        return m_sizes[SECONDARY_X];
    }

    /**
     * Sets the width of the secondary grid cells.
     * The default value is 5.
     * 
     * @param secondaryX
     * @return this GridLayer
     */
    public GridLayer setSecondarySizeX(double secondaryX)
    {
        m_sizes[SECONDARY_X] = secondaryX;

        return this;
    }

    /**
     * Returns the height of the secondary grid cells.
     * The default value is 5.
     * 
     * @return double
     */
    public double getSecondarySizeY()
    {
        return m_sizes[SECONDARY_Y];
    }

    /**
     * Sets the height of the secondary grid cells.
     * The default value is 5.
     * 
     * @param secondaryY
     * @return this GridLayer
     */
    public GridLayer setSecondarySizeY(double secondaryY)
    {
        m_sizes[SECONDARY_Y] = secondaryY;

        return this;
    }

    /**
     * Returns the {@link Line} that defines how vertical secondary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @return Line
     */
    public Line getSecondaryLineX()
    {
        return m_lines[SECONDARY_X];
    }

    /**
     * Sets the {@link Line} that defines how vertical secondary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @param secondaryLineX Line
     * @return GridLayer
     */
    public GridLayer setSecondaryLineX(Line secondaryLineX)
    {
        m_lines[SECONDARY_X] = secondaryLineX;

        return this;
    }

    /**
     * Returns the {@link Line} that defines how horizontal secondary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @return Line
     */
    public Line getSecondaryLineY()
    {
        return m_lines[SECONDARY_Y];
    }

    /**
     * Sets the {@link Line} that defines how horizontal secondary lines are drawn.
     * The default value is null, which means they are not drawn.
     * 
     * @param secondaryLineY Line
     * @return GridLayer
     */
    public void setSecondaryLineY(Line secondaryLineY)
    {
        m_lines[SECONDARY_Y] = secondaryLineY;
    }

    @Override
    protected void drawWithoutTransforms(Context2D context)
    {
        if (false == isVisible())
        {
            return;
        }
        Viewport vp = getViewport();

        int vw = vp.getWidth();

        int vh = vp.getHeight();

        Point2D a = new Point2D(0, 0);

        Point2D b = new Point2D(vw, vh);

        double scaleX = 1, scaleY = 1;

        Transform t = isTransformable() ? vp.getTransform() : getTransform();

        if (t != null)
        {
            scaleX = t.getScaleX();

            scaleY = t.getScaleY();

            t = t.getInverse();

            t.transform(a, a);

            t.transform(b, b);
        }
        double x1 = a.getX();

        double y1 = a.getY();

        double x2 = b.getX();

        double y2 = b.getY();

        for (int direction = X; direction <= Y; direction++)
        {
            boolean vertical = (direction == X);

            double scale = vertical ? scaleX : scaleY;

            double min = vertical ? x1 : y1;

            double max = vertical ? x2 : y2;

            for (int primSec = 0; primSec <= 1; primSec++)
            {
                int index = primSec * 2 + direction;

                boolean isSecondary = (primSec == 1);

                if (m_lines[index] == null)
                {
                    continue;
                }
                int n = 0;

                if (isSecondary)
                {
                    // n = primarySize div secondary
                    // ASSUMPTION: primarySize is a multiple of secondarySize

                    n = (int) Math.round(m_sizes[direction] / m_sizes[index]);
                }
                Line line = m_lines[index];

                double size = m_sizes[index];

                double previousLineWidth = line.getStrokeWidth();

                line.setStrokeWidth(previousLineWidth / scale);

                DashArray previousDashes = line.getDashArray();

                if (previousDashes != null)
                {
                    double[] d = previousDashes.getNormalizedArray();

                    DashArray dashes = new DashArray();

                    for (int i = 0; i < d.length; i++)
                    {
                        dashes.push(d[i] / scale);
                    }
                    line.setDashArray(dashes);
                }
                long n1 = Math.round(min / size);

                if (n1 * size < min)
                {
                    n1++;
                }
                long n2 = Math.round(max / size);

                if (n2 * size > max)
                {
                    n2--;
                }
                Point2DArray points = line.getPoints();

                Point2D p1 = points.getPoint(0);

                Point2D p2 = points.getPoint(1);

                if (vertical)
                {
                    p1.setY(y1);

                    p2.setY(y2);
                }
                else
                {
                    p1.setX(x1);

                    p2.setX(x2);
                }
                for (long ni = n1; ni <= n2; ni++)
                {
                    if (isSecondary && (ni % n == 0)) // skip primary lines
                    {
                        continue;
                    }
                    if (vertical)
                    {
                        double x = ni * size;

                        p1.setX(x);

                        p2.setX(x);
                    }
                    else
                    {
                        double y = ni * size;

                        p1.setY(y);

                        p2.setY(y);
                    }
                    line.drawWithTransforms(context);
                }
                line.setStrokeWidth(previousLineWidth); // restore stroke width

                if (previousDashes != null)
                {
                    line.setDashArray(previousDashes);
                }
            }
        }
        // Draw children (if any)
        super.drawWithoutTransforms(context);
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject obj = super.toJSONObject();

        JSONArray lines = new JSONArray();

        JSONArray sizes = new JSONArray();

        for (int i = 0; i < 4; i++)
        {
            if (m_lines[i] == null)
            {
                lines.set(i, JSONNull.getInstance());
            }
            else
            {
                lines.set(i, m_lines[i].toJSONObject());
            }
            sizes.set(i, new JSONNumber(m_sizes[i]));
        }
        obj.put("lines", lines);

        obj.put("sizes", sizes); // TODO could put sizes in Attributes

        return obj;
    }

    public static class GridLayerFactory extends LayerFactory
    {
        public GridLayerFactory()
        {
            setNodeType(NodeType.GRID_LAYER);
        }

        @Override
        public GridLayer create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            Line[] lines = new Line[4];

            double[] sizes = { 10, 10, 5, 5 };

            JSONValue aval = node.get("lines");

            if (aval != null)
            {
                JSONArray arr = aval.isArray();

                if (arr != null)
                {
                    for (int i = 0; i < 4 && i < arr.size(); i++)
                    {
                        JSONValue jval = arr.get(i);

                        if (jval != null)
                        {
                            JSONObject jobj = jval.isObject();

                            if (jobj != null)
                            {
                                Line line = (Line) JSONDeserializer.getInstance().fromJSON(jobj, ctx);

                                lines[i] = line;
                            }
                        }
                    }
                }
            }
            aval = node.get("sizes");

            if (aval != null)
            {
                JSONArray arr = aval.isArray();

                if (arr != null)
                {
                    for (int i = 0; i < 4 && i < arr.size(); i++)
                    {
                        JSONValue jval = arr.get(i);

                        if (jval != null)
                        {
                            JSONNumber jnum = jval.isNumber();

                            if (jnum != null)
                            {
                                sizes[i] = jnum.doubleValue();
                            }
                        }
                    }
                }
            }
            return new GridLayer(node, ctx, lines, sizes);
        }
    }
}
