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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.PathPartList.PathPartListJSO;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import java.util.List;

public class MultiPath extends AbstractMultiPathPartShape<MultiPath>
{
    public MultiPath()
    {
        super(ShapeType.MULTI_PATH);
    }

    public MultiPath(String path)
    {
        super(ShapeType.MULTI_PATH);

        PathPartList list = getOrIncrementList();

        SVGPath.parse(list, path);
    }

    public MultiPath(String[] paths)
    {
        super(ShapeType.MULTI_PATH);

        for (String path : paths)
        {
            PathPartList list = getOrIncrementList();

            SVGPath.parse(list, path);

            list.close();
        }
    }

    protected MultiPath(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.MULTI_PATH, node, ctx);

        JSONValue pval = node.get("path-list");

        if (null != pval)
        {
            final JSONArray list = pval.isArray();

            if (null != list)
            {
                final int size = list.size();

                for (int i = 0; i < size; i++)
                {
                    final JSONValue lval = list.get(i);

                    if (null != lval)
                    {
                        final JSONArray path = lval.isArray();

                        if (null != path)
                        {
                            PathPartListJSO pjso = path.getJavaScriptObject().cast();

                            add(new PathPartList(pjso, true));
                        }
                    }
                }
            }
        }
    }

    public MultiPath M(final double x, final double y)
    {
        getOrIncrementList().M(x, y);

        return this;
    }

    public MultiPath M(final Point2D p)
    {
        return M(p.getX(), p.getY());
    }

    public MultiPath L(final double x, final double y)
    {
        getOrIncrementList().L(x, y);

        return this;
    }

    public MultiPath L(final Point2D p)
    {
        return L(p.getX(), p.getY());
    }

    public MultiPath H(final double x)
    {
        getOrIncrementList().H(x);

        return this;
    }

    public MultiPath V(final double y)
    {
        getOrIncrementList().V(y);

        return this;
    }

    public MultiPath Q(final double cx, final double cy, final double x, final double y)
    {
        getOrIncrementList().Q(cx, cy, x, y);

        return this;
    }

    public MultiPath Q(final Point2D cp, final Point2D ep)
    {
        return Q(cp.getX(), cp.getY(), ep.getX(), ep.getY());
    }

    public MultiPath C(final double x1, final double y1, final double x2, final double y2, final double x, final double y)
    {
        getOrIncrementList().C(x1, y1, x2, y2, x, y);

        return this;
    }

    public MultiPath C(final Point2D c1, final Point2D c2, final Point2D ep)
    {
        return C(c1.getX(), c1.getY(), c2.getX(), c2.getY(), ep.getX(), ep.getY());
    }

    public MultiPath A(final double x0, final double y0, double x1, final double y1, double radius)
    {
        getOrIncrementList().A(x0, y0, x1, y1, radius);

        return this;
    }

    public MultiPath A(final double rx, final double ry, final double ps, final double fa, final double fs, final double x, final double y)
    {
        getOrIncrementList().A(rx, ry, ps, fa, fs, x, y);

        return this;
    }

    public MultiPath Z()
    {
        getOrIncrementList().Z();

        return this;
    }

    public MultiPath z()
    {
        return Z();
    }

    public final MultiPath circle(final double radius)
    {
        getOrIncrementList().circle(radius);

        return this;
    }

    public final MultiPath rect(final double x, final double y, final double w, final double h)
    {
        getOrIncrementList().rect(x, y, w, h);

        return this;
    }

    public MultiPath close()
    {
        final NFastArrayList<PathPartList> list = getPathPartListArray();

        if (list.size() > 0)
        {
            list.get(list.size() - 1).close();
        }
        return this;
    }

    @Override
    public MultiPath refresh()
    {
        return this;
    }

    @Override
    public JSONObject toJSONObject()
    {
        final JSONObject object = super.toJSONObject();

        final NFastArrayList<PathPartList> list = getPathPartListArray();

        final JSONArray path = new JSONArray();

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            path.set(i, list.get(i).toJSONArray());
        }
        object.put("path-list", path);

        return object;
    }

    private final PathPartList getOrIncrementList()
    {
        resetBoundingBox(); // null the cache, as the BB will change

        final NFastArrayList<PathPartList> list = getPathPartListArray();

        if (list.size() < 1)
        {
            PathPartList path = new PathPartList();

            list.add(path);

            return path;
        }
        PathPartList path = list.get(list.size() - 1);

        if (path.size() < 1)
        {
            return path;
        }
        if (path.isClosed())
        {
            path = new PathPartList();

            list.add(path);
        }
        return path;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes();
    }

    public static class MultiPathFactory extends ShapeFactory<MultiPath>
    {
        public MultiPathFactory()
        {
            super(ShapeType.MULTI_PATH);
        }

        @Override
        public MultiPath create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new MultiPath(node, ctx);
        }
    }
}