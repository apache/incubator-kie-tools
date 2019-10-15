package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.lienzo.client.BaseExample;

public class CardinalIntersectsExample extends BaseExample implements Example
{
    public CardinalIntersectsExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        layer.add(makeBoundingBoxWithLineIntersects());

        layer.add(makeAllCardinalIntersectionPoints());
    }

    private final Group makeAllCardinalIntersectionPoints()
    {
        final Group container = new Group().setX(-60).setY(450);

        // clockwise
        MultiPath path = new MultiPath();
        Group g    = new Group();
        g.setX(50);
        g.setY(20);
        container.add(g);
        path.M(100, 0);
        path.A(150, 0, 150, 50, 50);
        path.A(150, 100, 100, 100, 50);
        path.A(50, 100, 50, 50, 50);
        path.A(50, 0, 100, 0, 50);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(200);
        g.setY(20);
        container.add(g);
        path.M(100, 0);
        path.A(150, 0, 150, 50, 50);
        path.L(150, 100);
        path.L(50, 100);
        path.L(50, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(350);
        g.setY(20);
        container.add(g);
        path.M(100, 0);
        path.L(150, 0);
        path.L(150, 50);
        path.A(150, 100, 100, 100, 50);
        path.L(50, 100);
        path.L(50, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(500);
        g.setY(20);
        container.add(g);
        path.M(100, 0);
        path.L(150, 0);
        path.L(150, 100);
        path.A(50, 100, 50, 50, 50);
        path.L(50, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(650);
        g.setY(20);
        container.add(g);
        path.M(100, 0);
        path.L(150, 0);
        path.L(150, 100);
        path.L(50, 100);
        path.L(50, 50);
        path.A(50, 0, 100, 0, 50);
        drawPath(path, g);

        // counterclockwise
        path = new MultiPath();
        g = new Group();
        g.setX(50);
        g.setY(150);
        container.add(g);
        path.M(100, 0);
        path.A(50, 0, 50, 50, 50);
        path.A(50, 100, 100, 100, 50);
        path.A(150, 100, 150, 50, 50);
        path.A(150, 0, 100, 0, 50);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(200);
        g.setY(150);
        container.add(g);
        path.M(100, 0);
        path.A(50, 0, 50, 50, 50);
        path.L(50, 100);
        path.L(150, 100);
        path.L(150, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(350);
        g.setY(150);
        container.add(g);
        path.M(100, 0);
        path.L(50, 0);
        path.L(50, 50);
        path.A(50, 100, 100, 100, 50);
        path.L(150, 100);
        path.L(150, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(500);
        g.setY(150);
        container.add(g);
        path.M(100, 0);
        path.L(50, 0);
        path.L(50, 100);
        path.L(100, 100);
        path.A(150, 100, 150, 50, 50);
        path.L(150, 0);
        path.L(100, 0);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(650);
        g.setY(150);
        container.add(g);
        path.M(100, 0);
        path.L(50, 0);
        path.L(50, 100);
        path.L(150, 100);
        path.L(150, 50);
        path.A(150, 0, 100, 0, 50);
        path.L(100, 0);
        drawPath(path, g);

        // inner and out paths
        path = new MultiPath();
        g = new Group();
        g.setX(50);
        g.setY(300);
        container.add(g);
        path.M(100, 0);
        path.L(50, 0);
        path.L(50, 100);
        path.L(150, 100);
        path.L(150, 0);
        path.L(100, 0);
        path.L(100, 20);
        path.L(70, 20);
        path.L(70, 80);
        path.L(130, 80);
        path.L(130, 20);
        path.L(100, 20);
        drawPath(path, g);

        path = new MultiPath();
        g = new Group();
        g.setX(200);
        g.setY(300);
        container.add(g);
        path.M(100, 0);
        path.A(150, 0, 150, 50, 50);
        path.A(150, 100, 100, 100, 50);
        path.A(50, 100, 50, 50, 50);
        path.A(50, 0, 100, 0, 50);
        path.L(100, 20);
        path.A(130, 20, 130, 45, 30);
        path.A(130, 80, 100, 80, 30);
        path.A(70, 80, 70, 45, 30);
        path.A(70, 20, 100, 20, 30);
        drawPath(path, g);

        return container;
    }

    private final void drawPath(final MultiPath path, final Group g)
    {
        g.add(path);

        final PathPartList list = path.getPathPartListArray().get(0);

        final Point2DArray array = Geometry.getCardinalIntersects(list, MagnetManager.EIGHT_CARDINALS);

        for (final Point2D p : array.asArray())
        {
            if (null != p)
            {
                drawCircle(g, p);
            }
        }
    }

    private final Group makeBoundingBoxWithLineIntersects()
    {
        final Group container = new Group().setX(15).setY(-100);

        Group g = new Group();
        g.setX(25);
        container.add(g);
        Point2D arc0 = new Point2D(0, 200);
        Point2D arc1 = new Point2D(100, 0);
        Point2D arc2 = new Point2D(200, 200);
        Point2D lineStart = new Point2D(-10, 130);
        Point2D lineEnd = new Point2D(200, 160);
        double radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setX(25);
        g.setX(250);
        container.add(g);
        arc0 = new Point2D(200, 200);
        arc1 = new Point2D(100, 0);
        arc2 = new Point2D(0, 200);
        lineStart = new Point2D(0, 130);
        lineEnd = new Point2D(210, 180);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setY(250);
        g.setX(25);
        container.add(g);
        arc0 = new Point2D(0, 0);
        arc1 = new Point2D(100, 200);
        arc2 = new Point2D(200, 0);
        lineStart = new Point2D(-30, 0);
        lineEnd = new Point2D(200, 60);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setY(250);
        g.setX(250);
        container.add(g);
        arc0 = new Point2D(200, 0);
        arc1 = new Point2D(100, 200);
        arc2 = new Point2D(0, 0);
        lineStart = new Point2D(-10, 30);
        lineEnd = new Point2D(210, 70);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setX(75);
        g.setY(300);
        container.add(g);
        arc0 = new Point2D(0, 50);
        arc1 = new Point2D(50, 0);
        arc2 = new Point2D(100, 100);
        lineStart = new Point2D(-60, 55);
        lineEnd = new Point2D(125, 110);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setX(275);
        g.setY(325);
        container.add(g);
        arc0 = new Point2D(130, 30);
        arc1 = new Point2D(100, -50);
        arc2 = new Point2D(50, 0);
        lineStart = new Point2D(-20, 40);
        lineEnd = new Point2D(200, 60);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setX(75);
        g.setY(475);
        container.add(g);
        arc0 = new Point2D(0, 50);
        arc1 = new Point2D(50, 100);
        arc2 = new Point2D(100, 0);
        lineStart = new Point2D(-60, 40);
        lineEnd = new Point2D(125, 0);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        g = new Group();
        g.setX(320);
        g.setY(490);
        container.add(g);
        arc0 = new Point2D(80, 30);
        arc1 = new Point2D(50, 100);
        arc2 = new Point2D(0, 50);
        lineStart = new Point2D(-60, 20);
        lineEnd = new Point2D(125, 0);
        radius = 100;
        drawArcWithLineIntersect(g, arc0, arc1, arc2, lineStart, lineEnd, radius);

        return container;
    }

    private final void drawArcWithLineIntersect(final Group g, final Point2D arc0, final Point2D arc1, final Point2D arc2, final Point2D lineStart, final Point2D lineEnd, final double radius)
    {
        MultiPath path = new MultiPath();

        path.M(arc0);
        path.A(arc1.getX(), arc1.getY(), arc2.getX(), arc2.getY(), radius);
        g.add(path);

        final BoundingBox bbox = Geometry.getBoundingBoxOfArcTo(arc0, arc1, arc2, radius);

        path = new MultiPath();
        path.M(bbox.getX(), bbox.getY());
        path.L(bbox.getX() + bbox.getWidth(), bbox.getY());
        path.L(bbox.getX() + bbox.getWidth(), bbox.getY() + bbox.getHeight());
        path.L(bbox.getX(), bbox.getY() + bbox.getHeight());
        path.L(bbox.getX(), bbox.getY());
        g.add(path);

        path = new MultiPath();
        path.M(lineStart);
        path.L(lineEnd);
        g.add(path);

        final Point2DArray list = Geometry.intersectLineArcTo(lineStart, lineEnd, arc0, arc1, arc2, radius);

        final int size = list.size();

        for (int i = 0; i < size; i++)
        {
            drawCircle(g, list.get(i));
        }
    }

    private final void drawCircle(final Group container, final Point2D p)
    {
        container.add(new Circle(3).setLocation(p).setFillColor(ColorName.RED).setStrokeColor(ColorName.RED));
    }
}
