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
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class SVGPath extends Shape<SVGPath>
{
    private static final String[] COMMANDS = { "m", "M", "l", "L", "v", "V", "h", "H", "z", "Z", "c", "C", "q", "Q", "t", "T", "s", "S", "a", "A" };

    private String                m_path;

    private boolean               m_fill   = false;

    private final PathPartList    m_list   = new PathPartList();

    public SVGPath(String path)
    {
        super(ShapeType.SVG_PATH);

        setPath(path);
    }

    protected SVGPath(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SVG_PATH, node, ctx);

        setPath(getPath());
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, 10, 10);
    }

    @Override
    protected boolean prepare(final Context2D context, Attributes attr, double alpha)
    {
        m_fill = context.path(m_list);

        return true;
    }

    private final void parse(String path)
    {
        m_list.clear();

        path = path.replaceAll(" ", ",");

        for (int n = 0; n < COMMANDS.length; n++)
        {
            path = path.replaceAll(COMMANDS[n], "#" + COMMANDS[n]);
        }
        String[] list = path.split("#");

        double cpx = 0;

        double cpy = 0;

        for (int n = 1; n < list.length; n++)
        {
            String str = list[n];

            char chr = str.charAt(0);

            str = str.substring(1).replaceAll(",-", "-").replaceAll("-", ",-").replaceAll("e,-", "e-");

            String[] pts = str.split(",");

            int beg = 0;

            if ((pts.length > 0) && (pts[0].isEmpty()))
            {
                beg = 1;
            }
            NFastDoubleArrayJSO source = NFastDoubleArrayJSO.make();

            for (int i = beg; i < pts.length; i++)
            {
                source.add(Double.valueOf(pts[i]).doubleValue());
            }
            PathPartEntryJSO prev;

            double ctx, cty;

            double rx, ry, ps, fa, fs, x1, y1;

            while (source.size() > 0)
            {
                int cmd = PathPartEntryJSO.UNDEFINED_PATH_PART;

                NFastDoubleArrayJSO points = NFastDoubleArrayJSO.make();

                switch (chr)
                {
                    case 'l':
                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'L':
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'm':
                        double dx = source.shift();

                        double dy = source.shift();

                        cpx += dx;

                        cpy += dy;

                        final int size = m_list.size();

                        if (size > 2 && m_list.get(size - 1).getCommand() == PathPartEntryJSO.CLOSE_PATH_PART)
                        {
                            for (int idx = size - 2; idx >= 0; idx--)
                            {
                                prev = m_list.get(idx);

                                if (prev.getCommand() == PathPartEntryJSO.MOVETO_ABSOLUTE)
                                {
                                    cpx = prev.getPoints().get(0) + dx;

                                    cpy = prev.getPoints().get(1) + dy;

                                    break;
                                }
                            }
                        }
                        points.add(cpx);

                        points.add(cpy);

                        chr = 'l';

                        cmd = PathPartEntryJSO.MOVETO_ABSOLUTE;
                        break;
                    case 'M':
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        chr = 'L';

                        cmd = PathPartEntryJSO.MOVETO_ABSOLUTE;
                        break;
                    case 'h':
                        cpx += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'H':
                        cpx = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'v':
                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'V':
                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.LINETO_ABSOLUTE;
                        break;
                    case 'C':
                        points.add(source.shift());

                        points.add(source.shift());

                        points.add(source.shift());

                        points.add(source.shift());

                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE;
                        break;
                    case 'c':
                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE;
                        break;
                    case 'S':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.getCommand() == PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE)
                        {
                            ctx = cpx + (cpx - prev.getPoints().get(2));

                            cty = cpy + (cpy - prev.getPoints().get(3));
                        }
                        points.add(ctx);

                        points.add(cty);

                        points.add(source.shift());

                        points.add(source.shift());

                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE;
                        break;
                    case 's':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.getCommand() == PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE)
                        {
                            ctx = cpx + (cpx - prev.getPoints().get(2));

                            cty = cpy + (cpy - prev.getPoints().get(3));
                        }
                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE;
                        break;
                    case 'Q':
                        points.add(source.shift());

                        points.add(source.shift());

                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE;
                        break;
                    case 'q':
                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE;
                        break;
                    case 'T':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.getCommand() == PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE)
                        {
                            ctx = cpx + (cpx - prev.getPoints().get(0));

                            cty = cpy + (cpy - prev.getPoints().get(1));
                        }
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE;
                        break;
                    case 't':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.getCommand() == PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE)
                        {
                            ctx = cpx + (cpx - prev.getPoints().get(0));

                            cty = cpy + (cpy - prev.getPoints().get(1));
                        }
                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx);

                        points.add(cpy);

                        cmd = PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE;
                        break;
                    case 'A':
                        rx = source.shift();

                        ry = source.shift();

                        ps = source.shift();

                        fa = source.shift();

                        fs = source.shift();

                        x1 = cpx;

                        y1 = cpy;

                        cpx = source.shift();

                        cpy = source.shift();

                        points = convertEndpointToCenterParameterization(x1, y1, cpx, cpy, fa, fs, rx, ry, ps);

                        cmd = PathPartEntryJSO.ARCTO_ABSOLUTE;
                        break;
                    case 'a':
                        rx = source.shift();

                        ry = source.shift();

                        ps = source.shift();

                        fa = source.shift();

                        fs = source.shift();

                        x1 = cpx;

                        y1 = cpy;

                        cpx += source.shift();

                        cpy += source.shift();

                        points = convertEndpointToCenterParameterization(x1, y1, cpx, cpy, fa, fs, rx, ry, ps);

                        cmd = PathPartEntryJSO.ARCTO_ABSOLUTE;
                        break;
                }
                if (cmd != PathPartEntryJSO.UNDEFINED_PATH_PART)
                {
                    m_list.push(PathPartEntryJSO.make(cmd, points));
                }
            }
            if ((chr == 'z') || (chr == 'Z'))
            {
                m_list.push(PathPartEntryJSO.make(PathPartEntryJSO.CLOSE_PATH_PART, NFastDoubleArrayJSO.make()));
            }
        }
    }

    private final NFastDoubleArrayJSO convertEndpointToCenterParameterization(double x1, double y1, double x2, double y2, double fa, double fs, double rx, double ry, double ps)
    {
        ps = ps * (Math.PI / 180.0);

        double xp = Math.cos(ps) * (x1 - x2) / 2.0 + Math.sin(ps) * (y1 - y2) / 2.0;

        double yp = -1 * Math.sin(ps) * (x1 - x2) / 2.0 + Math.cos(ps) * (y1 - y2) / 2.0;

        double lambda = (xp * xp) / (rx * rx) + (yp * yp) / (ry * ry);

        if (lambda > 1)
        {
            rx *= Math.sqrt(lambda);

            ry *= Math.sqrt(lambda);
        }
        double f = Math.sqrt((((rx * rx) * (ry * ry)) - ((rx * rx) * (yp * yp)) - ((ry * ry) * (xp * xp))) / ((rx * rx) * (yp * yp) + (ry * ry) * (xp * xp)));

        if (fa == fs)
        {
            f *= -1;
        }
        if (Double.isNaN(f))
        {
            f = 0;
        }
        double cxp = f * rx * yp / ry;

        double cyp = f * -ry * xp / rx;

        double cx = (x1 + x2) / 2.0 + Math.cos(ps) * cxp - Math.sin(ps) * cyp;

        double cy = (y1 + y2) / 2.0 + Math.sin(ps) * cxp + Math.cos(ps) * cyp;

        double th = Geometry.getVectorAngle(new double[] { 1, 0 }, new double[] { (xp - cxp) / rx, (yp - cyp) / ry });

        double[] u = new double[] { (xp - cxp) / rx, (yp - cyp) / ry };

        double[] v = new double[] { (-1 * xp - cxp) / rx, (-1 * yp - cyp) / ry };

        double dt = Geometry.getVectorAngle(u, v);

        if (Geometry.getVectorRatio(u, v) <= -1)
        {
            dt = Math.PI;
        }
        if (Geometry.getVectorRatio(u, v) >= 1)
        {
            dt = 0;
        }
        if (fs == 0 && dt > 0)
        {
            dt = dt - 2 * Math.PI;
        }
        if (fs == 1 && dt < 0)
        {
            dt = dt + 2 * Math.PI;
        }
        NFastDoubleArrayJSO points = NFastDoubleArrayJSO.make();

        points.add(cx);

        points.add(cy);

        points.add(rx);

        points.add(ry);

        points.add(th);

        points.add(dt);

        points.add(ps);

        points.add(fs);

        return points;
    }

    @Override
    protected void fill(Context2D context, Attributes attr, double alpha)
    {
        if (m_fill)
        {
            super.fill(context, attr, alpha);
        }
    }

    public String getPath()
    {
        return getAttributes().getPath();
    }

    public SVGPath setPath(String path)
    {
        getAttributes().setPath(path);

        if (false == path.equals(m_path))
        {
            parse(m_path = path);
        }
        return this;
    }

    @Override
    public IFactory<SVGPath> getFactory()
    {
        return new SVGPathFactory();
    }

    public static class SVGPathFactory extends ShapeFactory<SVGPath>
    {
        public SVGPathFactory()
        {
            super(ShapeType.SVG_PATH);

            addAttribute(Attribute.PATH, true);
        }

        @Override
        public SVGPath create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new SVGPath(node, ctx);
        }
    }
}
