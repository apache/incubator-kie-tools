/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.LienzoPath2D;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;
import elemental2.core.Global;

public class PathPartList {

    private double m_cpx;

    private double m_cpy;

    private boolean m_fin;

    private boolean m_mov;

    private LienzoPath2D m_p2d;

    private BoundingBox m_box;

    private final PathPartListJSO m_jso;

    public PathPartList() {
        m_jso = PathPartListJSO.make();
    }

    public static final PathPartList make(final PathPartListJSO jso, final boolean serialized) {
        PathPartList plist = new PathPartList();

        for (int i = 0; i < jso.getLength(); i++) {
            plist.m_jso.setAt(i, jso.get(i));
        }

        if (serialized) {
            plist.m_mov = true;

            plist.m_fin = true;
        }
        return plist;
    }

    public PathPartList copy() {
        PathPartListJSO jso = PathPartListJSO.make();
        for (int i = 0; i < getJSO().length(); i++) {
            jso.push(getJSO().get(i).copy());
        }
        return make(jso, isSerialized());
    }

    private boolean isSerialized() {
        return m_mov || m_fin;
    }

    public final void push(final PathPartEntryJSO part) {
        resetBoundingBox();

        if (!m_mov) {
            M(0, 0);
        }
        m_jso.push(part);
    }

    public final PathPartEntryJSO get(final int i) {
        return m_jso.get(i);
    }

    public final int size() {
        return m_jso.length();
    }

    public final void clear() {
        m_p2d = null;

        resetBoundingBox();

        m_mov = false;

        m_fin = false;

        m_jso.setLength(0);
    }

    public final LienzoPath2D getPath2D() {
        return m_p2d;
    }

    public final PathPartList setPath2D(final LienzoPath2D path) {
        m_p2d = path;

        return this;
    }

    public final PathPartListJSO getJSO() {
        return m_jso;
    }

    public final PathPartList M(final double x, final double y) {
        m_mov = true;

        push(PathPartEntryJSO.make(PathPartEntryJSO.MOVETO_ABSOLUTE, new double[]{m_cpx = x, m_cpy = y}));

        return this;
    }

    public final PathPartList M(final Point2D p) {
        return M(p.getX(), p.getY());
    }

    public final PathPartList L(final double x, final double y) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, new double[]{m_cpx = x, m_cpy = y}));

        return this;
    }

    public final PathPartList L(final Point2D p) {
        return L(p.getX(), p.getY());
    }

    public final PathPartList H(final double x) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, new double[]{m_cpx = x, m_cpy}));

        return this;
    }

    public final PathPartList V(final double y) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.LINETO_ABSOLUTE, new double[]{m_cpx, m_cpy = y}));

        return this;
    }

    public final PathPartList Q(final double cx, final double cy, final double x, final double y) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE, new double[]{cx, cy, m_cpx = x, m_cpy = y}));

        return this;
    }

    public final PathPartList Q(final Point2D cp, final Point2D ep) {
        return Q(cp.getX(), cp.getY(), ep.getX(), ep.getY());
    }

    public final PathPartList C(final double x1, final double y1, final double x2, final double y2, final double x, final double y) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE, new double[]{x1, y1, x2, y2, m_cpx = x, m_cpy = y}));

        return this;
    }

    public final PathPartList C(final Point2D c1, final Point2D c2, final Point2D ep) {
        return C(c1.getX(), c1.getY(), c2.getX(), c2.getY(), ep.getX(), ep.getY());
    }

    public PathPartList A(final double x0, final double y0, double x1, final double y1, final double radius) {
        push(PathPartEntryJSO.make(PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE, new double[]{x0, y0, m_cpx = x1, m_cpy = y1, radius}));

        return this;
    }

    public final PathPartList A(final double rx, final double ry, final double ps, final double fa, final double fs, final double x, final double y) {
        final NFastDoubleArray points = PathPartList.convertEndpointToCenterParameterization(m_cpx, m_cpy, x, y, fa, fs, rx, ry, ps);

        points.push(m_cpx = x);

        points.push(m_cpy = y);

        push(PathPartEntryJSO.make(PathPartEntryJSO.ARCTO_ABSOLUTE, points.toArray()));

        return this;
    }

    public final PathPartList Z() {
        push(PathPartEntryJSO.make(PathPartEntryJSO.CLOSE_PATH_PART, new double[0]));

        return close();
    }

    public final PathPartList close() {
        m_fin = true;

        m_mov = false;

        if (null != m_p2d) {
            m_p2d.closed = true;
        }
        return this;
    }

    public final PathPartList circle(final double r) {
        final double x = m_cpx;

        final double y = m_cpy;

        final double c = r * 2;

        M(x + r, y);

        A(x + c, y, x + c, y + r, r);

        A(x + c, y + c, x + r, y + c, r);

        A(x, y + c, x, y + r, r);

        A(x, y, x + r, y, r);

        Z();

        return this;
    }

    public final PathPartList rect(final double x, final double y, final double w, final double h) {
        M(x, y);

        L(x + w, y);

        L(x + w, y + h);

        L(x, y + h);

        Z();

        return this;
    }

    public final boolean isClosed() {
        return m_fin;
    }

    public final String toJSONString() {
        return Global.JSON.stringify(m_jso);
    }

    public final PathPartList deep() {
        final PathPartList make = new PathPartList();
        for (int i = 0; i < m_jso.length(); i++) {
            PathPartEntryJSO entry = m_jso.get(i);
            make.push(entry.copy());
        }

        make.m_fin = m_fin;

        make.m_mov = m_mov;

        make.m_cpx = m_cpx;

        make.m_cpy = m_cpy;

        return make;
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public static final NFastDoubleArray convertEndpointToCenterParameterization(final double x1, final double y1, final double x2, final double y2, final double fa, final double fs, double rx, double ry, final double pv) {
        final NFastDoubleArray points = new NFastDoubleArray();

        convertEndpointToCenterParameterization(points, x1, y1, x2, y2, fa, fs, rx, ry, pv);

        return points;
    }

    public final static void convertEndpointToCenterParameterization(final NFastDoubleArray points, final double x1, final double y1, final double x2, final double y2, final double fa, final double fs, double rx, double ry, final double pv) {
        final double ps = pv * Geometry.PI_180;

        final double cp = Math.cos(ps);

        final double sp = Math.sin(ps);

        final double xp = cp * (x1 - x2) / 2.0 + sp * (y1 - y2) / 2.0;

        final double yp = -1 * sp * (x1 - x2) / 2.0 + cp * (y1 - y2) / 2.0;

        final double lambda = (xp * xp) / (rx * rx) + (yp * yp) / (ry * ry);

        if (lambda > 1) {
            double sq = Math.sqrt(lambda);

            rx *= sq;

            ry *= sq;
        }
        double f = Math.sqrt((((rx * rx) * (ry * ry)) - ((rx * rx) * (yp * yp)) - ((ry * ry) * (xp * xp))) / ((rx * rx) * (yp * yp) + (ry * ry) * (xp * xp)));

        if (fa == fs) {
            f *= -1;
        }
        if (Double.isNaN(f)) {
            f = 0;
        }
        final double cxp = f * rx * yp / ry;

        final double cyp = f * -ry * xp / rx;

        final double cx = (x1 + x2) / 2.0 + cp * cxp - sp * cyp;

        final double cy = (y1 + y2) / 2.0 + sp * cxp + cp * cyp;

        final double th = Geometry.getVectorAngle(new double[]{1, 0}, new double[]{(xp - cxp) / rx, (yp - cyp) / ry});

        final double[] u = new double[]{(xp - cxp) / rx, (yp - cyp) / ry};

        final double[] v = new double[]{(-1 * xp - cxp) / rx, (-1 * yp - cyp) / ry};

        double dt = Geometry.getVectorAngle(u, v);

        if (Geometry.getVectorRatio(u, v) <= -1) {
            dt = Math.PI;
        }
        if (Geometry.getVectorRatio(u, v) >= 1) {
            dt = 0;
        }
        if (fs == 0 && dt > 0) {
            dt -= Geometry.TWO_PI;
        }
        if (fs == 1 && dt < 0) {
            dt += Geometry.TWO_PI;
        }
        points.clear();

        points.push(cx, cy, rx, ry, th, dt, ps, fs);
    }

    public void resetBoundingBox() {
        m_box = null;
    }

    public BoundingBox getBoundingBox() {
        if (m_box != null) {
            return m_box;
        }

        final int size = size();

        if (size < 1) {
            m_box = BoundingBox.fromDoubles(0, 0, 0, 0);
            return m_box;
        }

        m_box = new BoundingBox();

        double oldx = 0;

        double oldy = 0;

        int i = skipRedundantLeadingMoveTo(this);

        for (; i < size; i++) {
            final PathPartEntryJSO part = get(i);

            final double[] p = part.getPoints();

            switch (part.getCommand()) {
                case PathPartEntryJSO.LINETO_ABSOLUTE:
                    m_box.add(oldx = p[0], oldy = p[1]);
                    break;
                case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    m_box.add(oldx = p[0], oldy = p[1]);
                    break;
                case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                    final double x0 = oldx;
                    final double y0 = oldy;

                    final double x1 = p[0];
                    final double y1 = p[1];

                    final double x2 = p[2];
                    final double y2 = p[3];

                    final double x3 = p[4];
                    final double y3 = p[5];

                    final double[] xvals = new double[]{x0, x1, x2, x3};
                    final double[] yvals = new double[]{y0, y1, y2, y3};

                    m_box.addBoundingBox(Geometry.getBoundingBoxOfCubicCurve(xvals, yvals));
                    break;
                }
                case PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE:
                    m_box.addBoundingBox(Geometry.getBoundingBoxForQuadraticCurve(Point2DArray.fromArrayOfPoint2D(new Point2D(oldx, oldy), new Point2D(p[0], p[1]), new Point2D(oldx = p[2], oldy = p[3]))));
                    break;
                case PathPartEntryJSO.ARCTO_ABSOLUTE:
                    double cx = p[0];
                    double cy = p[1];
                    double rx = p[2];
                    double ry = p[3];
                    m_box.addX(cx + rx);
                    m_box.addX(cx - rx);
                    m_box.addY(cy + ry);
                    m_box.addY(cy - ry);
                    oldx = p[8];
                    oldy = p[9];
                    break;
                case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                    double x0 = p[0];
                    double y0 = p[1];
                    double x1 = p[2];
                    double y1 = p[3];
                    double ra = p[4];
                    Point2D p0 = new Point2D(oldx, oldy);
                    Point2DArray pa = Geometry.getCanvasArcToPoints(p0, new Point2D(x0, y0), new Point2D(x1, y1), ra);
                    BoundingBox bb = Geometry.getBoundingBoxOfArc(pa.get(0), pa.get(1), pa.get(2), ra);
                    if (false == pa.get(0).equals(p0)) {
                        bb.addPoint2D(p0);//p0 is always the start point of the path, but not necessary of the arc - depending on the radius
                    }
                    m_box.addBoundingBox(bb);
                    Point2D ep = pa.get(2);// this is always the end point of the path
                    oldx = ep.getX();
                    oldy = ep.getY();
                    break;
            }
        }
        return m_box;
    }

    public static int skipRedundantLeadingMoveTo(final PathPartList list) {
        int i = 0;

        for (; i < list.size(); i++) {
            final PathPartEntryJSO part = list.get(i);

            if (part.getCommand() != PathPartEntryJSO.MOVETO_ABSOLUTE) {
                if (i != 0) {
                    // Atleast one M was found, so move back to it
                    i--;
                }
                break;
            }
        }
        return i;
    }

    public Point2DArray getPoints() {
        final int size = size();

        Point2DArray points = new Point2DArray();

        if (size < 1) {
            return points;
        }
        double oldx = 0;

        double oldy = 0;

        for (int i = 0; i < size; i++) {
            final PathPartEntryJSO part = get(i);

            final double[] p = part.getPoints();

            switch (part.getCommand()) {
                case PathPartEntryJSO.LINETO_ABSOLUTE:
                case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    points.pushXY(oldx = p[0], oldy = p[1]);
                    break;
                case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE:
                    points.pushXY(oldx = p[4], oldy = p[5]);
                    break;
                case PathPartEntryJSO.QUADRATIC_CURVETO_ABSOLUTE:
                    points.pushXY(oldx = p[2], oldy = p[3]);
                    break;
                case PathPartEntryJSO.ARCTO_ABSOLUTE:
                    points.pushXY(oldx = p[8], oldy = p[9]);
                    break;
                case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                    double x0 = p[0];
                    double y0 = p[1];
                    double x1 = p[2];
                    double y1 = p[3];
                    double ra = p[4];
                    Point2D p0 = new Point2D(oldx, oldy);
                    Point2DArray pa = Geometry.getCanvasArcToPoints(p0, new Point2D(x0, y0), new Point2D(x1, y1), ra);
                    Point2D ep = pa.get(2);// this is always the end point of the path
                    points.pushXY(oldx = ep.getX(), oldy = ep.getY());
                    break;
            }
        }
        return points;
    }
}
