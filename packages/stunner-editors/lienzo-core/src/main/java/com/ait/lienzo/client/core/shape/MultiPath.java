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

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class MultiPath extends AbstractMultiPathPartShape<MultiPath> {

    @JsIgnore
    public MultiPath() {
        this(new String[] {});
    }

    @JsIgnore
    public MultiPath(String path) {
        this(new String[] {path});
    }

    public MultiPath(String[] paths) {
        super(ShapeType.MULTI_PATH);

        for (String path : paths) {
            PathPartList list = getOrIncrementList();

            SVGPath.parse(list, path);

            list.close();
        }
    }

    public static MultiPath clonePath(final MultiPath multiPath) {
        return (MultiPath) multiPath.copyTo(new MultiPath());
    }

    public MultiPath M(final double x, final double y) {
        getOrIncrementList().M(x, y);

        return this;
    }

    @JsIgnore
    public MultiPath M(final Point2D p) {
        return M(p.getX(), p.getY());
    }

    public MultiPath L(final double x, final double y) {
        getOrIncrementList().L(x, y);

        return this;
    }

    @JsIgnore
    public MultiPath L(final Point2D p) {
        return L(p.getX(), p.getY());
    }

    public MultiPath H(final double x) {
        getOrIncrementList().H(x);

        return this;
    }

    public MultiPath V(final double y) {
        getOrIncrementList().V(y);

        return this;
    }

    public MultiPath Q(final double cx, final double cy, final double x, final double y) {
        getOrIncrementList().Q(cx, cy, x, y);

        return this;
    }

    @JsIgnore
    public MultiPath Q(final Point2D cp, final Point2D ep) {
        return Q(cp.getX(), cp.getY(), ep.getX(), ep.getY());
    }

    public MultiPath C(final double x1, final double y1, final double x2, final double y2, final double x, final double y) {
        getOrIncrementList().C(x1, y1, x2, y2, x, y);

        return this;
    }

    @JsIgnore
    public MultiPath C(final Point2D c1, final Point2D c2, final Point2D ep) {
        return C(c1.getX(), c1.getY(), c2.getX(), c2.getY(), ep.getX(), ep.getY());
    }

    public MultiPath A(final double x0, final double y0, double x1, final double y1, double radius) {
        getOrIncrementList().A(x0, y0, x1, y1, radius);

        return this;
    }

    @JsIgnore
    public MultiPath A(final double rx, final double ry, final double ps, final double fa, final double fs, final double x, final double y) {
        getOrIncrementList().A(rx, ry, ps, fa, fs, x, y);

        return this;
    }

    public MultiPath Z() {
        getOrIncrementList().Z();

        return this;
    }

    public MultiPath z() {
        return Z();
    }

    public final MultiPath circle(final double radius) {
        getOrIncrementList().circle(radius);

        return this;
    }

    public final MultiPath rect(final double x, final double y, final double w, final double h) {
        getOrIncrementList().rect(x, y, w, h);

        return this;
    }

    public MultiPath close() {
        final NFastArrayList<PathPartList> list = getPathPartListArray();

        if (list.size() > 0) {
            list.get(list.size() - 1).close();
        }
        return this;
    }

    @Override
    public MultiPath refresh() {
        return this;
    }

    private final PathPartList getOrIncrementList() {
        resetBoundingBox(); // null the cache, as the BB will change

        final NFastArrayList<PathPartList> list = getPathPartListArray();

        if (list.size() < 1) {
            PathPartList path = new PathPartList();

            list.add(path);

            return path;
        }
        PathPartList path = list.get(list.size() - 1);

        if (path.size() < 1) {
            return path;
        }
        if (path.isClosed()) {
            path = new PathPartList();

            list.add(path);
        }
        return path;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes();
    }
}