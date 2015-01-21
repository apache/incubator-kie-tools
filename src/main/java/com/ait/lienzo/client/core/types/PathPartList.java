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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.util.Curves;
import com.ait.lienzo.client.core.util.Geometry;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;

public final class PathPartList
{
    private static final double   TWO_PI = (2.000 * Math.PI);

    private static final double   PI_180 = (Math.PI / 180.0);

    private double                m_cpx;

    private double                m_cpy;

    private boolean               m_fin;

    private boolean               m_mov;

    private BoundingBox           m_box;

    private final PathPartListJSO m_jso;

    public PathPartList()
    {
        this(PathPartListJSO.make(), false);
    }

    public PathPartList(final PathPartListJSO jso, final boolean serialized)
    {
        m_jso = jso;

        if (serialized)
        {
            m_mov = true;

            m_fin = true;
        }
    }

    public final void push(final PathPartEntryJSO part)
    {
        m_box = null;

        if (false == m_mov)
        {
            M(0, 0);
        }
        m_jso.push(part);
    }

    public final PathPartEntryJSO get(final int i)
    {
        return m_jso.get(i);
    }

    public final int size()
    {
        return m_jso.length();
    }

    public final void clear()
    {
        m_box = null;
        
        m_mov = false;
        
        m_fin = false;

        m_jso.setLength(0);
    }

    public final PathPartListJSO getJSO()
    {
        return m_jso;
    }

    public final PathPartList M(final double x, final double y)
    {
        m_mov = true;

        push(PathPartEntryJSO.make(PathPartEntryJSO.MOVETO_ABSOLUTE, NFastDoubleArrayJSO.make(m_cpx = x, m_cpy = y)));

        return this;
    }

    public final PathPartList M(final Point2D p)
    {
        return M(p.getX(), p.getY());
    }

    public final PathPartList L(final double x, final double y)
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, NFastDoubleArrayJSO.make(m_cpx = x, m_cpy = y)));

        return this;
    }

    public final PathPartList L(final Point2D p)
    {
        return L(p.getX(), p.getY());
    }

    public final PathPartList H(final double x)
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, NFastDoubleArrayJSO.make(m_cpx = x, m_cpy)));

        return this;
    }

    public final PathPartList V(final double y)
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, NFastDoubleArrayJSO.make(m_cpx, m_cpy = y)));

        return this;
    }

    public final PathPartList Q(final double cx, final double cy, final double x, final double y)
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE, NFastDoubleArrayJSO.make(cx, cy, m_cpx = x, m_cpy = y)));

        return this;
    }

    public final PathPartList Q(final Point2D cp, final Point2D ep)
    {
        return Q(cp.getX(), cp.getY(), ep.getX(), ep.getY());
    }

    public final PathPartList C(final double x1, final double y1, final double x2, final double y2, final double x, final double y)
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE, NFastDoubleArrayJSO.make(x1, y1, x2, y2, m_cpx = x, m_cpy = y)));

        return this;
    }

    public final PathPartList C(final Point2D c1, final Point2D c2, final Point2D ep)
    {
        return C(c1.getX(), c1.getY(), c2.getX(), c2.getY(), ep.getX(), ep.getY());
    }

    public final PathPartList A(final double rx, final double ry, final double ps, final double fa, final double fs, final double x, final double y)
    {
        final NFastDoubleArrayJSO points = PathPartList.convertEndpointToCenterParameterization(m_cpx, m_cpy, x, y, fa, fs, rx, ry, ps);

        points.push(m_cpx = x);

        points.push(m_cpy = y);

        push(PathPartEntryJSO.make(PathPartEntryJSO.ARCTO_ABSOLUTE, points));

        return this;
    }

    public final PathPartList Z()
    {
        push(PathPartEntryJSO.make(PathPartEntryJSO.CLOSE_PATH_PART, NFastDoubleArrayJSO.make()));

        return close();
    }

    public final PathPartList close()
    {
        m_fin = true;

        m_mov = false;

        return this;
    }

    public final boolean isClosed()
    {
        return m_fin;
    }

    public final JSONArray toJSONArray()
    {
        return new JSONArray(getJSO());
    }

    public final String toJSONString()
    {
        return toJSONArray().toString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    public final static NFastDoubleArrayJSO convertEndpointToCenterParameterization(final double x1, final double y1, final double x2, final double y2, final double fa, final double fs, double rx, double ry, final double pv)
    {
        final NFastDoubleArrayJSO points = NFastDoubleArrayJSO.make();

        convertEndpointToCenterParameterization(points, x1, y1, x2, y2, fa, fs, rx, ry, pv);

        return points;
    }

    public final static void convertEndpointToCenterParameterization(final NFastDoubleArrayJSO points, final double x1, final double y1, final double x2, final double y2, final double fa, final double fs, double rx, double ry, final double pv)
    {
        final double ps = pv * PI_180;

        final double cp = Math.cos(ps);

        final double sp = Math.sin(ps);

        final double xp = cp * (x1 - x2) / 2.0 + sp * (y1 - y2) / 2.0;

        final double yp = -1 * sp * (x1 - x2) / 2.0 + cp * (y1 - y2) / 2.0;

        final double lambda = (xp * xp) / (rx * rx) + (yp * yp) / (ry * ry);

        if (lambda > 1)
        {
            double sq = Math.sqrt(lambda);

            rx *= sq;

            ry *= sq;
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
        final double cxp = f * rx * yp / ry;

        final double cyp = f * -ry * xp / rx;

        final double cx = (x1 + x2) / 2.0 + cp * cxp - sp * cyp;

        final double cy = (y1 + y2) / 2.0 + sp * cxp + cp * cyp;

        final double th = Geometry.getVectorAngle(new double[] { 1, 0 }, new double[] { (xp - cxp) / rx, (yp - cyp) / ry });

        final double[] u = new double[] { (xp - cxp) / rx, (yp - cyp) / ry };

        final double[] v = new double[] { (-1 * xp - cxp) / rx, (-1 * yp - cyp) / ry };

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
            dt -= TWO_PI;
        }
        if (fs == 1 && dt < 0)
        {
            dt += TWO_PI;
        }
        points.clear();

        points.push(cx, cy, rx, ry, th, dt, ps, fs);
    }

    public BoundingBox getBoundingBox()
    {
        final int size = size();

        if (size < 1)
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        if (m_box != null)
        {
            return m_box;
        }
        m_box = new BoundingBox();

        double oldx = 0;

        double oldy = 0;

        for (int i = 0; i < size; i++)
        {
            final PathPartEntryJSO part = get(i);

            final NFastDoubleArrayJSO p = part.getPoints();

            switch (part.getCommand())
            {
                case PathPartEntryJSO.LINETO_ABSOLUTE:
                    m_box.add(oldx = p.get(0), oldy = p.get(1));
                    break;
                case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    m_box.add(oldx = p.get(0), oldy = p.get(1));
                    break;
                case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                    m_box.add(Curves.getBoundingBox(new Point2DArray(new Point2D(oldx, oldy), new Point2D(p.get(0), p.get(1)), new Point2D(p.get(2), p.get(3)), new Point2D(oldx = p.get(4), oldy = p.get(5)))));
                    break;
                case PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE:
                    m_box.add(Curves.getBoundingBox(new Point2DArray(new Point2D(oldx, oldy), new Point2D(p.get(0), p.get(1)), new Point2D(oldx = p.get(2), oldy = p.get(3)))));
                    break;
                case PathPartEntryJSO.ARCTO_ABSOLUTE:
                    double cx = p.get(0);
                    double cy = p.get(1);
                    double rx = p.get(2);
                    double ry = p.get(3);
                    m_box.addX(cx + rx);
                    m_box.addX(cx - rx);
                    m_box.addY(cy + ry);
                    m_box.addY(cy - ry);
                    oldx = p.get(8);
                    oldy = p.get(9);
                    break;
            }
        }
        return m_box;
    }

    public static final class PathPartListJSO extends JsArray<PathPartEntryJSO>
    {
        public static final PathPartListJSO make()
        {
            return JsArray.createArray().cast();
        }

        protected PathPartListJSO()
        {
        }
    }
}
