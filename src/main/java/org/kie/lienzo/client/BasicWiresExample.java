package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;

import static org.kie.lienzo.client.util.WiresUtils.connect;

public class BasicWiresExample extends BaseExample implements Example {

    private WiresManager wiresManager;

    public BasicWiresExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        // Wires setup
        wiresManager = WiresManager.get(layer);
        wiresManager.enableSelectionManager();
        wiresManager.setContainmentAcceptor(IContainmentAcceptor.ALL);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.ALL);
        wiresManager.setDockingAcceptor(IDockingAcceptor.ALL);
        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);


        // Rectangle - Red
        WiresShape rectangleRedShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                              .setStrokeColor("#FF0000")
                                                              .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(100, 400));
        wiresManager.register(rectangleRedShape);
        wiresManager.getMagnetManager().createMagnets(rectangleRedShape);

        // Rectangle - Blue
        WiresShape rectangleBlueShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                               .setStrokeColor("#0000FF")
                                                               .setFillColor("#0000FF"))
                .setDraggable(true)
                .setLocation(new Point2D(500, 400));
        wiresManager.register(rectangleBlueShape);
        wiresManager.getMagnetManager().createMagnets(rectangleBlueShape);

        // Connection: red -> blue
        connect(rectangleRedShape.getMagnets(),
                3,
                rectangleBlueShape.getMagnets(),
                7,
                wiresManager,
                true);
    }
}
