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
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class SVGPath extends Shape<SVGPath>
{
    private static final String[]           COMMANDS = { "m", "M", "l", "L", "v", "V", "h", "H", "z", "Z", "c", "C", "q", "Q", "t", "T", "s", "S", "a", "A" };

    private String                          m_path;

    private boolean                         m_fill   = false;

    private final NFastArrayList<PathEntry> m_list   = new NFastArrayList<PathEntry>();

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
    protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        final int size = m_list.size();

        if (size < 1)
        {
            return false;
        }
        m_fill = false;

        context.beginPath();

        for (int i = 0; i < size; i++)
        {
            PathEntry entry = m_list.get(i);

            double[] p = entry.points;

            switch (entry.command)
            {
                case 'L':
                    context.lineTo(p[0], p[1]);
                break;
                case 'M':
                    context.moveTo(p[0], p[1]);
                break;
                case 'C':
                    context.bezierCurveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
                break;
                case 'Q':
                    context.quadraticCurveTo(p[0], p[1], p[2], p[3]);
                break;
                case 'A':
                    double cx = p[0];

                    double cy = p[1];

                    double rx = p[2];

                    double ry = p[3];

                    double th = p[4];

                    double dt = p[5];

                    double ro = p[6];

                    double fs = p[7];

                    double ra = ((rx > ry) ? rx : ry);

                    double sx = ((rx > ry) ? 1 : (rx / ry));

                    double sy = ((rx > ry) ? (ry / rx) : 1);

                    context.translate(cx, cy);

                    context.rotate(ro);

                    context.scale(sx, sy);

                    context.arc(0, 0, ra, th, th + dt, (1 - fs) > 0);

                    context.scale(1 / sx, 1 / sy);

                    context.rotate(-ro);

                    context.translate(-cx, -cy);
                break;
                case 'z':
                    context.closePath();

                    m_fill = true;
                break;
            }
        }
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
            while (source.size() > 0)
            {
                char cmd = '\0';

                NFastDoubleArrayJSO points = NFastDoubleArrayJSO.make();

                double ctx, cty;

                double rx, ry, ps, fa, fs, x1, y1;

                PathEntry prev;

                switch (chr)
                {
                    case 'l':
                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
                    break;
                    case 'L':
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
                    break;
                    case 'm':
                        double dx = source.shift();

                        double dy = source.shift();

                        cpx += dx;

                        cpy += dy;

                        final int size = m_list.size();

                        if (size > 2 && m_list.get(size - 1).command == 'z')
                        {
                            for (int idx = size - 2; idx >= 0; idx--)
                            {
                                PathEntry pe = m_list.get(idx);

                                if (pe.command == 'M')
                                {
                                    cpx = pe.points[0] + dx;

                                    cpy = pe.points[1] + dy;

                                    break;
                                }
                            }
                        }
                        points.add(cpx);

                        points.add(cpy);

                        chr = 'l';

                        cmd = 'M';
                    break;
                    case 'M':
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        chr = 'L';

                        cmd = 'M';
                    break;
                    case 'h':
                        cpx += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
                    break;
                    case 'H':
                        cpx = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
                    break;
                    case 'v':
                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
                    break;
                    case 'V':
                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'L';
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

                        cmd = 'C';
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

                        cmd = 'C';
                    break;
                    case 'S':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.command == 'C')
                        {
                            ctx = cpx + (cpx - prev.points[2]);

                            cty = cpy + (cpy - prev.points[3]);
                        }
                        points.add(ctx);

                        points.add(cty);

                        points.add(source.shift());

                        points.add(source.shift());

                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'C';
                    break;
                    case 's':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.command == 'C')
                        {
                            ctx = cpx + (cpx - prev.points[2]);

                            cty = cpy + (cpy - prev.points[3]);
                        }
                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'C';
                    break;
                    case 'Q':
                        points.add(source.shift());

                        points.add(source.shift());

                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'Q';
                    break;
                    case 'q':
                        points.add(cpx + source.shift());

                        points.add(cpy + source.shift());

                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'Q';
                    break;
                    case 'T':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.command == 'Q')
                        {
                            ctx = cpx + (cpx - prev.points[0]);

                            cty = cpy + (cpy - prev.points[1]);
                        }
                        cpx = source.shift();

                        cpy = source.shift();

                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'Q';
                    break;
                    case 't':
                        ctx = cpx;

                        cty = cpy;

                        prev = m_list.get(m_list.size() - 1);

                        if (prev.command == 'Q')
                        {
                            ctx = cpx + (cpx - prev.points[0]);

                            cty = cpy + (cpy - prev.points[1]);
                        }
                        cpx += source.shift();

                        cpy += source.shift();

                        points.add(ctx);

                        points.add(cty);

                        points.add(cpx);

                        points.add(cpy);

                        cmd = 'Q';
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

                        cmd = 'A';
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

                        cmd = 'A';
                    break;
                }
                m_list.add(new PathEntry(cmd, points));
            }
            if ((chr == 'z') || (chr == 'Z'))
            {
                m_list.add(new PathEntry('z', null));
            }
        }
    }

    private NFastDoubleArrayJSO convertEndpointToCenterParameterization(double x1, double y1, double x2, double y2, double fa, double fs, double rx, double ry, double ps)
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

    private static final class PathEntry
    {
        public final char     command;

        public final double[] points;

        private PathEntry(char c, NFastDoubleArrayJSO list)
        {
            command = c;

            if (null != list)
            {
                final int size = list.size();

                points = new double[size];

                for (int i = 0; i < size; i++)
                {
                    points[i] = list.get(i);
                }
            }
            else
            {
                points = new double[0];
            }
        }
    }
}
