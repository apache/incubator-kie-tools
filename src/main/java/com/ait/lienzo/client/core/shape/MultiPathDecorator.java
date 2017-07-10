package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;

public class MultiPathDecorator {
    MultiPath m_path;
    double    m_width;
    double    m_height;

    public MultiPathDecorator(MultiPath path) {
        m_path = path;
        BoundingBox box = path.getBoundingBox();
        m_width = box.getWidth();
        m_height = box.getHeight();
    }

    public MultiPath getPath() {
        return m_path;
    }

    public void draw(Point2DArray points) {
        final Point2D p0 = points.get(0);
        final Point2D p1 = points.get(1);

        Point2D p3 = new Point2D(p1.getX(), p1.getY() + m_height);

        double angle = Geometry.getClockwiseAngleBetweenThreePoints(p3, p1, p0);

        m_path.setX(p1.getX() - (m_width / 2)).setY(p1.getY());
        m_path.setOffset(m_width / 2, 0);
        m_path.setRotation(angle);
    }

    public MultiPathDecorator copy()
    {
       return new MultiPathDecorator(m_path.copy());
    }

}
