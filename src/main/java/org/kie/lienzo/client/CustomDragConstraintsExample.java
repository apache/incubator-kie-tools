package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.lienzo.client.BaseExample;

public class CustomDragConstraintsExample extends BaseExample implements Example
{
    public CustomDragConstraintsExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {

        int width = 700;
        int height = 700;

        // Note that the API w.r.t. custom drag constraints may change.
        // We'll probably allow you set an attribute, rather than having to override
        // the getDragConstraints() method.

        // The rectangle location snaps to a 10 pixel grid.
        Rectangle r = new Rectangle(40, 50);
        DragConstraintEnforcer enforcer = new DragConstraintEnforcer() {
                    @Override
                    public boolean adjust(Point2D dxy) {
                        dxy.setX(snap(dxy.getX()));
                        dxy.setY(snap(dxy.getY()));

                        return true;
                    }

                    private double snap(double x) {
                        int w = 10;
                        return w * Math.round(x / w);
                    }

                    @Override
                    public void startDrag(DragContext dragContext) {
                        // not used
                    }
        };
        r.setDragConstraints(enforcer);
        r.setX(100).setY(200).setFillColor(ColorName.RED).setDraggable(true);
        layer.add(r);

        // The circle can be dragged in a 50 pixel radius from the anchor point (500,300).
        final Point2D anchor = new Point2D(500, 300);
        final Point2D center = new Point2D(0, 0); // center of the circle when the drag starts
        Circle c      = new Circle(40);
        enforcer = new DragConstraintEnforcer() {

                    @Override
                    public void startDrag(DragContext dragContext) {
                        IPrimitive<?> node = dragContext.getNode();
                        center.setX(node.getX());
                        center.setY(node.getY());
                    }

                    @Override
                    public boolean adjust(Point2D dxy) {
                        Point2D newCenter = center.add(dxy);
                        double maxRadius = 50;
                        Point2D anchorToCenter = newCenter.sub(anchor);
                        double distFromAnchor = anchorToCenter.getLength();
                        if (distFromAnchor > maxRadius)
                        {
                            // Move the center of the circle to the nearest point on the boundary circle
                            newCenter = anchorToCenter.unit().mul(maxRadius).add(anchor);
                            Point2D newDxy = newCenter.sub(center);
                            dxy.set(newDxy);
                        }
                        return true;
                    }
        };
        c.setLocation(anchor).setFillColor(ColorName.BLUE).setDraggable(true);
        c.setDragConstraints(enforcer);
        layer.add(c);

        // The center of the star snaps to the nearest anchor point
        final Point2DArray anchorPoints = new Point2DArray().fromArrayOfPoint2D(
                new Point2D(50, 500), new Point2D(100, 400), new Point2D(150, 450),
                new Point2D(200, 500), new Point2D(250, 400), new Point2D(300, 450));
        final Star star = new Star(5, 20, 40);
        enforcer = new DragConstraintEnforcer() {
                    @Override
                    public void startDrag(DragContext dragContext) {
                        IPrimitive<?> node = dragContext.getNode();
                        center.set(node.getLocation());
                    }

                    @Override
                    public boolean adjust(Point2D dxy) {
                        Point2D newCenter = center.add(dxy);
                        Point2D closestPoint = null;
                        double minDist = Double.MAX_VALUE;
                        for (int i = 0, n = anchorPoints.getLength(); i < n; i++)
                        {
                            Point2D ap = anchorPoints.get(i);
                            double d = newCenter.distance(ap);
                            if (d < minDist)
                            {
                                minDist = d;
                                closestPoint = ap;
                            }
                        }
                        Point2D newDxy = closestPoint.sub(center);
                        dxy.set(newDxy);

                        return true;
                    }
        };
        star.setDragConstraints(enforcer);
        star.setLocation(anchorPoints.get(0));
        star.setFillColor(ColorName.DARKORANGE);
        star.setDraggable(true);
        layer.add(star);

        // Draw the anchor points
        for (int i = 0, n = anchorPoints.getLength(); i < n; i++)
        {
            Point2D ap = anchorPoints.get(i);
            Circle pt = new Circle(2);
            pt.setLocation(ap).setFillColor(ColorName.BLACK);
            layer.add(pt);
        }
    }
}
