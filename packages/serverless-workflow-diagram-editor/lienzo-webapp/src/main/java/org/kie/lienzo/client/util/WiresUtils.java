package org.kie.lienzo.client.util;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.PolyMorphicLine;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;

public class WiresUtils {

    public static WiresConnector connect(MagnetManager.Magnets magnets0,
                                         int i0_1,
                                         MagnetManager.Magnets magnets1,
                                         int i1_1,
                                         WiresManager wiresManager,
                                         boolean orthogonalPolyline) {
        WiresMagnet m0_1 = magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = magnets1.getMagnet(i1_1);

        double x0, x1, y0, y1;

        MultiPath head = new MultiPath();
        head.M(15,
               20);
        head.L(0,
               20);
        head.L(15 / 2,
               0);
        head.Z();

        MultiPath tail = new MultiPath();
        tail.M(15,
               20);
        tail.L(0,
               20);
        tail.L(15 / 2,
               0);
        tail.Z();

        AbstractDirectionalMultiPointShape<?> line;
        x0 = m0_1.getControl().getX();
        y0 = m0_1.getControl().getY();
        x1 = m1_1.getControl().getX();
        y1 = m1_1.getControl().getY();

        double ox0 = x0 + ((x1 - x0) / 2);
        double oy0 = y0 + ((y1 - y0) / 2);

        if (orthogonalPolyline) {
            line = createPolymorphicLine(x0, y0, x1, y1);
        } else {
            line = createPolyline(ox0, oy0, x1, y1);
        }

        line.setHeadDirection(Direction.NONE);
        line.setTailDirection(Direction.NONE);

        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());
        line.setSelectionStrokeOffset(25);

        WiresConnector connector = new WiresConnector(m0_1,
                                                      m1_1,
                                                      line,
                                                      new MultiPathDecorator(head),
                                                      new MultiPathDecorator(tail));
        wiresManager.register(connector);
        wiresManager.addHandlers(connector);
        head.setStrokeWidth(5).setStrokeColor("#0000CC");
        tail.setStrokeWidth(5).setStrokeColor("#0000CC");
        line.setStrokeWidth(5).setStrokeColor("#0000CC");

        return connector;
    }

    public static OrthogonalPolyLine createOrthogonalPolyline(final double... points) {
        return createOrthogonalPolyline(Point2DArray.fromArrayOfDouble(points));
    }

    public static OrthogonalPolyLine createOrthogonalPolyline(final Point2DArray points) {
        return new OrthogonalPolyLine(points).setCornerRadius(5).setDraggable(true);
    }

    public static PolyMorphicLine createPolymorphicLine(final double... points) {
        return createPolymorphicLine(Point2DArray.fromArrayOfDouble(points));
    }

    public static PolyMorphicLine createPolymorphicLine(final Point2DArray points) {
        return new PolyMorphicLine(points).setCornerRadius(5).setDraggable(true);
    }

    public static PolyLine createPolyline(final double... points) {
        return createPolyline(Point2DArray.fromArrayOfDouble(points));
    }

    public static PolyLine createPolyline(final Point2DArray points) {
        return new PolyLine(points).setCornerRadius(5).setDraggable(true);
    }
}
