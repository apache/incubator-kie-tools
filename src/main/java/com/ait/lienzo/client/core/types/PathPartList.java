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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.util.Curves;
import com.google.gwt.core.client.JsArray;

public final class PathPartList
{
    private BoundingBox           m_box = null;

    private final PathPartListJSO m_jso = PathPartListJSO.make();

    public PathPartList()
    {
    }

    public final void push(PathPartEntryJSO part)
    {
        m_box = null;

        m_jso.push(part);
    }

    public final PathPartEntryJSO get(int i)
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

        m_jso.clear();
    }

    public final PathPartListJSO getJSO()
    {
        return m_jso;
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

        public final native void clear()
        /*-{
            this.length = length;
        }-*/;
    }
}
