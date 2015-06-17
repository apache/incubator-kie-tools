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

import java.io.Serializable;

import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public final class PathPartListArray implements Serializable
{
    private static final long                  serialVersionUID = 4550786550917277327L;

    private final NFastArrayList<PathPartList> m_list           = new NFastArrayList<PathPartList>();

    public PathPartListArray()
    {
    }

    public BoundingBox getBoundingBox()
    {
        final int size = m_list.size();

        if (size < 1)
        {
            return new BoundingBox(0, 0, 0, 0);
        }
        final BoundingBox bbox = new BoundingBox();

        for (int i = 0; i < size; i++)
        {
            bbox.add(m_list.get(i).getBoundingBox());
        }
        return bbox;
    }

    public PathPartListArray clear()
    {
        final int size = m_list.size();

        for (int i = 0; i < size; i++)
        {
            m_list.get(i).clear();
        }
        m_list.clear();

        return this;
    }

    public PathPartListArray M(final double x, final double y)
    {
        getOrIncrementList().M(x, y);

        return this;
    }

    public PathPartListArray M(final Point2D p)
    {
        return M(p.getX(), p.getY());
    }

    public PathPartListArray L(final double x, final double y)
    {
        getOrIncrementList().L(x, y);

        return this;
    }

    public PathPartListArray L(final Point2D p)
    {
        return L(p.getX(), p.getY());
    }

    public PathPartListArray H(final double x)
    {
        getOrIncrementList().H(x);

        return this;
    }

    public PathPartListArray V(final double y)
    {
        getOrIncrementList().V(y);

        return this;
    }

    public PathPartListArray Q(final double cx, final double cy, final double x, final double y)
    {
        getOrIncrementList().Q(cx, cy, x, y);

        return this;
    }

    public PathPartListArray Q(final Point2D cp, final Point2D ep)
    {
        return Q(cp.getX(), cp.getY(), ep.getX(), ep.getY());
    }

    public PathPartListArray C(final double x1, final double y1, final double x2, final double y2, final double x, final double y)
    {
        getOrIncrementList().C(x1, y1, x2, y2, x, y);

        return this;
    }

    public PathPartListArray C(final Point2D c1, final Point2D c2, final Point2D ep)
    {
        return C(c1.getX(), c1.getY(), c2.getX(), c2.getY(), ep.getX(), ep.getY());
    }

    public PathPartListArray A(final double x0, final double y0, double x1, final double y1, double radius)
    {
        getOrIncrementList().A(x0, y0, x1, y1, radius);

        return this;
    }

    public PathPartListArray A(final double rx, final double ry, final double ps, final double fa, final double fs, final double x, final double y)
    {
        getOrIncrementList().A(rx, ry, ps, fa, fs, x, y);

        return this;
    }

    public PathPartListArray Z()
    {
        getOrIncrementList().Z();

        return this;
    }

    public PathPartListArray z()
    {
        return Z();
    }

    public PathPartListArray close()
    {
        if (m_list.size() > 0)
        {
            m_list.get(m_list.size() - 1).close();
        }
        return this;
    }

    private final PathPartList getOrIncrementList()
    {
        if (m_list.size() < 1)
        {
            PathPartList path = new PathPartList();

            m_list.add(path);

            return path;
        }
        PathPartList path = m_list.get(m_list.size() - 1);

        if (path.size() < 1)
        {
            return path;
        }
        if (path.isClosed())
        {
            path = new PathPartList();

            m_list.add(path);
        }
        return path;
    }
}
