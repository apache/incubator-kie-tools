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

import java.util.Objects;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public final class BoundingBox {

    @JsProperty
    private double minx;

    @JsProperty
    private double miny;

    @JsProperty
    private double maxx;

    @JsProperty
    private double maxy;

    public BoundingBox() {

        minx = Double.MAX_VALUE;
        miny = Double.MAX_VALUE;
        maxx = -Double.MAX_VALUE;
        maxy = -Double.MAX_VALUE;
    }

    public static BoundingBox fromBoundingBox(final BoundingBox bbox) {
        return fromDoubles(bbox.minx, bbox.miny, bbox.maxx, bbox.maxy);
    }

    public static BoundingBox fromDoubles(final double minx, final double miny, final double maxx, final double maxy) {
        BoundingBox box = new BoundingBox();
        box.addX(minx);
        box.addY(miny);
        box.addX(maxx);
        box.addY(maxy);

        return box;
    }

    public boolean isEmpty() {
        return getWidth() == 0 && getHeight() == 0;
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }

    public static BoundingBox fromArrayOfPoint2D(final Point2D... points) {
        BoundingBox box = new BoundingBox();
        box.addDoubles(points);

        return box;
    }

    public static BoundingBox fromPoint2DArray(final Point2DArray points) {
        BoundingBox box = new BoundingBox();
        box.addPoint2DArray(points);

        return box;
    }

//    public BoundingBox(final BoundingBoxJSO jso)
//    {
//        m_jso = Objects.requireNonNull(jso);
//    }

    public final boolean isValid() {
        final double minx = getMinX();

        final double maxx = getMaxX();

        if ((maxx <= minx) || (maxx == -Double.MAX_VALUE) || (minx == Double.MAX_VALUE)) {
            return false;
        }
        final double miny = getMinY();

        final double maxy = getMaxY();

        if ((maxy <= miny) || (maxy == -Double.MAX_VALUE) || (miny == Double.MAX_VALUE)) {
            return false;
        }
        return true;
    }

    public final BoundingBox addX(final double x) {
        if (x < this.minx) {
            this.minx = x;
        }

        if (x > this.maxx) {
            this.maxx = x;
        }

        return this;
    }

    public final BoundingBox addY(final double y) {
        if (y < this.miny) {
            this.miny = y;
        }

        if (y > this.maxy) {
            this.maxy = y;
        }

        return this;
    }

    public final BoundingBox add(final double x, final double y) {
        addX(x);

        addY(y);

        return this;
    }

    public final BoundingBox addBoundingBox(final BoundingBox bbox) {
        if (null != bbox) {
            addX(bbox.getMinX());

            addY(bbox.getMinY());

            addX(bbox.getMaxX());

            addY(bbox.getMaxY());
        }
        return this;
    }

    public final BoundingBox addDoubles(final Point2D... points) {
        if (null != points) {
            final int size = points.length;

            for (int i = 0; i < size; i++) {
                final Point2D p = points[i];

                if (null != p) {
                    addX(p.getX());

                    addY(p.getY());
                }
            }
        }
        return this;
    }

    public final BoundingBox addPoint2DArray(final Point2DArray points) {
        if (null != points) {
            final int size = points.size();

            for (int i = 0; i < size; i++) {
                final Point2D p = points.get(i);

                if (null != p) {
                    addX(p.getX());

                    addY(p.getY());
                }
            }
        }
        return this;
    }

    public final BoundingBox addPoint2D(final Point2D point) {
        if (null != point) {
            addX(point.getX());

            addY(point.getY());
        }
        return this;
    }

    public final double getX() {
        return this.minx;
    }

    public final double getY() {
        return this.miny;
    }

    public final double getWidth() {
        return Math.abs(getMaxX() - getMinX());
    }

    public final double getHeight() {
        return Math.abs(getMaxY() - getMinY());
    }

    public final double getMinX() {
        return this.minx;
    }

    public final double getMaxX() {
        return this.maxx;
    }

    public final double getMinY() {
        return this.miny;
    }

    public final double getMaxY() {
        return this.maxy;
    }

    public final boolean intersects(BoundingBox other) {
        if (getMaxX() < other.getMinX()) {
            return false; // this is left of other
        }
        if (getMinX() > other.getMaxX()) {
            return false; // this is right of other
        }
        if (getMaxY() < other.getMinY()) {
            return false; // this is above other
        }
        if (getMinY() > other.getMaxY()) {
            return false; // this is below other
        }
        return true; // boxes overlap
    }

    public final boolean containsBoundingBox(BoundingBox other) {
        if (getMinX() <= other.getMinX() && getMaxX() >= other.getMaxX() && getMinY() <= other.getMinY() && getMaxY() >= other.getMaxY()) {
            return true;
        } else {
            return false;
        }
    }

    public final boolean containsPoint(Point2D p) {
        return getMinX() <= p.getX() && getMaxX() >= p.getX() &&
                getMinY() <= p.getY() && getMaxY() >= p.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(minx, miny, maxx, maxy);
    }

    @Override
    public final boolean equals(final Object other) {
        if ((other == null) || (!(other instanceof BoundingBox))) {
            return false;
        }
        if (this == other) {
            return true;
        }
        final BoundingBox that = ((BoundingBox) other);

        return ((that.getX() == getX()) && (that.getY() == getY()) && (that.getWidth() == getWidth()) && (that.getHeight() == getHeight()));
    }

    public BoundingBox copy() {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.minx = this.minx;
        boundingBox.miny = this.miny;
        boundingBox.maxx = this.maxx;
        boundingBox.maxy = this.maxy;
        return boundingBox;
    }

    public void offset(int dx, int dy) {
        this.minx = this.minx + dx;
        this.maxx = this.maxx + dx;
        this.miny = this.miny + dy;
        this.maxy = this.maxy + dy;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "minx=" + minx +
                ", miny=" + miny +
                ", maxx=" + maxx +
                ", maxy=" + maxy +
                '}';
    }
}
