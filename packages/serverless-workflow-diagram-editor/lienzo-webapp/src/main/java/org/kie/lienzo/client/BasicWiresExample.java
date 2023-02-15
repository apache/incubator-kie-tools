package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

import static org.kie.lienzo.client.util.WiresUtils.connect;

public class BasicWiresExample extends BaseExample implements Example {

    public static final String RED_RECTANGLE = "redRectangle";
    public static final String BLUE_RECTANGLE = "blueRectangle";
    public static final String CIRCLE = "circle";
    public static final String PARENT = "parent";
    private HandlerRegistration redRectangleMouseEnterHandler;
    private HandlerRegistration redRectangleMouseExitHandler;
    private HandlerRegistration parentMouseEnterHandler;
    private HandlerRegistration parentMouseExitHandler;
    private WiresManager wiresManager;

    public BasicWiresExample(final String title) {
        super(title);
    }

    private WiresShape createShape(String id, MultiPath path, Point2D location) {
        WiresShape shape = new WiresShape(path)
                .setID(id)
                .setDraggable(true)
                .setLocation(location);
        shape.getGroup().setUserData(id);
        wiresManager.register(shape);
        wiresManager.getMagnetManager().createMagnets(shape);
        return shape;
    }

    @Override
    public void run() {
        wiresManager = WiresManager.get(layer);
        wiresManager.enableSelectionManager();
        wiresManager.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);
        wiresManager.setDockingAcceptor(DOCKING_ACCEPTOR);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.ALL);
        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);

        MultiPath redRectangle = new MultiPath().rect(0, 0, 100, 100)
                .setStrokeColor("#FF0000")
                .setFillColor("#FF0000");
        redRectangleMouseEnterHandler = redRectangle.addNodeMouseEnterHandler(event -> {
            console.log("red rectangle ENTER");
        });
        redRectangleMouseExitHandler = redRectangle.addNodeMouseExitHandler(event -> {
            console.log("red rectangle EXIT");
        });
        redRectangle.setEventPropagationMode(EventPropagationMode.LAST_ANCESTOR);
        WiresShape shapeRedRectangle = createShape(RED_RECTANGLE,
                                                   redRectangle,
                                                   new Point2D(100, 50));

        WiresShape shapeCircle = createShape(CIRCLE,
                                             new MultiPath().circle(50)
                                                     .setStrokeColor("#FF0000")
                                                     .setFillColor("#FF0000"),
                                             new Point2D(400, 50));

        WiresShape shapeBlueRectangle = createShape(BLUE_RECTANGLE,
                                                    new MultiPath().rect(0, 0, 100, 100)
                                                            .setStrokeColor("#0000FF")
                                                            .setFillColor("#0000FF"),
                                                    new Point2D(650, 50));

        MultiPath parent = new MultiPath().rect(0, 0, 600, 250)
                .setStrokeColor("#000000")
                .setFillColor("#FFFFFF");
        parentMouseEnterHandler = parent.addNodeMouseEnterHandler(event -> {
            console.log("PARENT ENTER");
        });
        parentMouseExitHandler = parent.addNodeMouseExitHandler(event -> {
            console.log("PARENT EXIT");
        });
        WiresShape shapeParent = createShape(PARENT,
                                             parent,
                                             new Point2D(50, 300));

        connect(shapeRedRectangle.getMagnets(),
                3,
                shapeCircle.getMagnets(),
                7,
                wiresManager,
                false);
    }

    private static final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(WiresContainer parent, WiresShape[] children) {
            return test(parent, children);
        }

        @Override
        public boolean acceptContainment(WiresContainer parent, WiresShape[] children) {
            return test(parent, children);
        }
    };

    private static final IDockingAcceptor DOCKING_ACCEPTOR = new IDockingAcceptor() {
        @Override
        public boolean dockingAllowed(WiresContainer parent, WiresShape child) {
            return test(parent, child);
        }

        @Override
        public boolean acceptDocking(WiresContainer parent, WiresShape child) {
            return test(parent, child);
        }

        @Override
        public int getHotspotSize() {
            return 25;
        }
    };

    private static boolean test(WiresContainer parent, WiresShape... children) {
        if (null == parent || null == parent.getGroup()) {
            return true;
        }
        if (PARENT.equals(parent.getGroup().getUserData())) {
            for (WiresShape child : children) {
                Object data = child.getGroup().getUserData();
                if (BLUE_RECTANGLE.equals(data)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        redRectangleMouseEnterHandler.removeHandler();
        redRectangleMouseExitHandler.removeHandler();
        parentMouseEnterHandler.removeHandler();
        parentMouseExitHandler.removeHandler();
    }
}
