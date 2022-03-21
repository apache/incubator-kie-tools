package com.ait.lienzo.test.stub.overlays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;

@StubClass("com.ait.lienzo.client.core.types.Point2DArray")
public class Point2DArray {

    LinkedList<Point2D> holder = new LinkedList<>();

    public static final Point2DArray fromArrayOfDouble(final double... array) {
        final Point2DArray points = new Point2DArray();

        if (null == array) {
            return points;
        }
        final int size = Math.abs(array.length);

        if (0 == size) {
            return points;
        }
        if ((size % 2) == 1) {
            throw new IllegalArgumentException("size of array is not a multiple of 2");
        }
        for (int i = 0; i < size; i += 2) {
            points.pushXY(array[i], array[i + 1]);
        }
        return points;
    }

    public static final Point2DArray fromArrayOfPoint2D(final Point2D... inArray) {
        final Point2DArray points = new Point2DArray();

        if (null == inArray) {
            return points;
        }
        final int size = Math.abs(inArray.length);

        if (0 == size) {
            return points;
        }
        for (Point2D point2D : inArray) {
            points.push(point2D);
        }

        return points;
    }

    public static Point2DArray make() {
        return new Point2DArray();
    }

    public static final com.ait.lienzo.client.core.types.Point2DArray fromNFastDoubleArray(final NFastDoubleArray array) {
        final com.ait.lienzo.client.core.types.Point2DArray points = new com.ait.lienzo.client.core.types.Point2DArray();

        if (null == array) {
            return points;
        }
        final int size = Math.abs(array.size());

        if (size < 1) {
            return points;
        }
        if ((size % 2) == 1) {
            throw new IllegalArgumentException("size of array is not a multiple of 2");
        }
        for (int i = 0; i < size; i += 2) {
            points.pushXY(array.get(i), array.get(i + 1));
        }
        return points;
    }

    public void init() {

    }

    public boolean isEmpty() {
        return holder.isEmpty();
    }

    public int push(Point2D... point2D) {
        for (Point2D point2D1 : point2D) {
            holder.add(point2D1);
        }
        return holder.size();
    }

    public void pop() {
        holder.pop();
    }

    public final Point2DArray pushXY(final double x, final double y) {
        push(new Point2D(x, y));
        return this;
    }

    public final Point2D get(final int i) {
        return holder.get(i);
    }

    public Point2D getAt(int index) {
        return holder.get(index);
    }

    public Point2DArray unshift(final Point2D p) {
        holder.add(0, p);
        return this;
    }

    public int getLength() {
        return holder.size();
    }

    public Point2DArray noAdjacentPoints() {
        final Point2DArray no = Point2DArray.make();
        final int sz = holder.size();
        if (sz < 1) {
            return no;
        }
        Point2D p1 = holder.get(0);
        no.holder.push(new Point2D(p1.getX(), p1.getY()));
        if (sz < 2) {
            return no;
        }
        for (int i = 1; i < sz; i++) {
            final Point2D p2 = holder.get(i);
            if (!((p1.getX() == p2.getX()) && (p1.getY() == p2.getY()))) {
                no.holder.push(new Point2D(p2.getX(), p2.getY()));
            }
            p1 = p2;
        }
        return no;
    }

    public final Point2DArray copy() {
        Point2DArray no = new Point2DArray();
        int sz = holder.size();
        if (sz < 1) {
            return no;
        }
        for (int i = 0; i < sz; i++) {
            Point2D p = getAt(i);
            no.set(i, p.copy());
        }
        return no;
    }

    private com.ait.lienzo.client.core.types.Point2DArray _toOriginal(Point2DArray holder) {
        final com.ait.lienzo.client.core.types.Point2DArray no = new com.ait.lienzo.client.core.types.Point2DArray();
        final int sz = holder.size();
        if (sz < 1) {
            return no;
        }
        for (int i = 0; i < sz; i++) {
            final Point2D p = holder.get(i);

            no.push(new Point2D(p.getX(), p.getY()));
        }
        return no;
    }

    private com.ait.lienzo.client.core.types.Point2DArray _toOriginal() {
        return _toOriginal(this);
    }

    public final Point2DArray set(final int i, final Point2D p) {


/*        if(i >= holder.size()) {
            throw new UnsupportedOperationException(i + " " + holder.size());
        }*/

        if (holder.size() > i) {
            holder.set(i, p);
        } else {
            holder.add(i, p);
        }
        return this;
    }

    public final Collection<Point2D> getPoints() {
        final int size = size();

        final ArrayList<Point2D> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(get(i));
        }
        return Collections.unmodifiableCollection(list);
    }

    public final BoundingBox getBoundingBox() {
        return BoundingBox.fromPoint2DArray(_toOriginal());
    }

    public final Point2D[] asArray() {
        Point2D[] itemsArray = new Point2D[holder.size()];
        return holder.stream().collect(Collectors.toList()).toArray(itemsArray);
    }

    public int size() {
        return holder.size();
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }

    public final String toJSONString() {
        return "{}";
    }
}