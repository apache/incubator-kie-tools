package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.google.gwt.dom.client.Style;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;

import static org.kie.lienzo.client.util.WiresUtils.connect;

public class BasicWiresExample extends BaseExample implements Example {

    public static final String RED_RECTANGLE = "redRectangle";
    public static final String BLUE_RECTANGLE = "blueRectangle";
    public static final String CIRCLE = "circle";
    public static final String PARENT = "parent";
    private static final String LABEL_PARENT = "parent";
    private static final String LABEL_CHILD = "child";

    private HTMLButtonElement logPointsButton;
    private HTMLButtonElement refreshLineButton;
    private HTMLButtonElement leftButton;
    private HTMLButtonElement rightButton;
    private HTMLButtonElement upButton;
    private HTMLButtonElement downButton;
    private HTMLButtonElement testButton;
    private WiresManager wiresManager;
    private WiresShape rectangleRedShape;
    private WiresShape circleRedShape;
    private WiresConnector connector;
    private WiresShape rectangleBlueShape;
    private WiresShape rectangleBlackShape;

    public BasicWiresExample(final String title) {
        super(title);
    }

    @Override
    public void init(LienzoPanel panel,
                     HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Style.Display.INLINE.getCssName();

        logPointsButton = createButton("Log", this::onLogPointsButtonClick);
        topDiv.appendChild(logPointsButton);

        refreshLineButton = createButton("Refresh", this::onRefreshLineButtonClick);
        topDiv.appendChild(refreshLineButton);

        leftButton = createButton("Left", this::onLeftButtonClick);
        topDiv.appendChild(leftButton);

        rightButton = createButton("Right", this::onRightButtonClick);
        topDiv.appendChild(rightButton);

        upButton = createButton("Up", this::onUpButtonClick);
        topDiv.appendChild(upButton);

        downButton = createButton("Down", this::onDownButtonClick);
        topDiv.appendChild(downButton);

        testButton = createButton("Test", this::onTestButtonClick);
        topDiv.appendChild(testButton);
    }

    private static final double MOVE_DELTA = 5;

    private void onDownButtonClick() {
        deltaMoveShape(0, MOVE_DELTA);
    }

    private void onUpButtonClick() {
        deltaMoveShape(0, -MOVE_DELTA);
    }

    private void onRightButtonClick() {
        deltaMoveShape(MOVE_DELTA, 0);
    }

    private void onLeftButtonClick() {
        deltaMoveShape(-MOVE_DELTA, 0);
    }

    private void onRefreshLineButtonClick() {
        connector.getLine().refresh();
        layer.batch();
    }

    private void onLogPointsButtonClick() {
        DomGlobal.console.log(connector.getLine().getPoint2DArray());
    }

    private void onTestButtonClick() {
        onLogPointsButtonClick();
        if (null != connector) {
            connector.getControl().hideControlPoints();
            connector.getControl().showControlPoints();
            connector.getLine().getLayer().batch();
        }
    }

    private void deltaMoveShape(double dx, double dy) {
        Point2D location = rectangleRedShape.getLocation();
        testMoveShape(location.getX() + dx, location.getY() + dy);
    }

    private void testMoveShape(double x, double y) {
        rectangleRedShape.setLocation(new Point2D(x, y));
        rectangleRedShape.shapeMoved();
        layer.batch();
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

        // Rectangle - Red
        rectangleRedShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                   .setStrokeColor("#FF0000")
                                                   .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(100, 100));
        rectangleRedShape.getGroup().setID("redRectangle");
        rectangleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleRedShape);
        createMagnets(rectangleRedShape);

        // Circle - Red
        circleRedShape = new WiresShape(new MultiPath().circle(50)
                                                .setStrokeColor("#FF0000")
                                                .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(400, 100));
        circleRedShape.getGroup().setID("redCircle");
        circleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(circleRedShape);
        createMagnets(circleRedShape);

        // Rectangle - Blue
        rectangleBlueShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                    .setStrokeColor("#0000FF")
                                                    .setFillColor("#0000FF"))
                .setDraggable(true)
                .setLocation(new Point2D(450, 600));
        rectangleBlueShape.getGroup().setID("blueRectangle");
        rectangleBlueShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleBlueShape);
        createMagnets(rectangleBlueShape);

        // Rectangle - Black
        rectangleBlackShape = new WiresShape(new MultiPath().rect(0, 0, 350, 300)
                                                     .setStrokeColor("#000000")
                                                     .setFillColor("#FFFFFF"))
                .setDraggable(true)
                .setLocation(new Point2D(50, 400));
        rectangleBlackShape.getGroup().setID("blackRectangle");
        rectangleBlackShape.getGroup().setUserData(LABEL_PARENT);
        wiresManager.register(rectangleBlackShape);
        createMagnets(rectangleBlackShape);

        // Connection
        int srcMagnet = 2;
        int targetMagnet = 4;
        connector = connect(rectangleRedShape.getMagnets(),
                            srcMagnet,
                            circleRedShape.getMagnets(),
                            targetMagnet,
                            wiresManager,
                            true);

        WiresConnection rectangleConnection = rectangleRedShape.getMagnets().getMagnet(srcMagnet).getConnections().get(0);
        WiresConnection circleConnection = circleRedShape.getMagnets().getMagnet(targetMagnet).getConnections().get(0);
        rectangleConnection.setAutoConnection(true);
        circleConnection.setAutoConnection(true);
    }

    @Override
    public void destroy() {
        super.destroy();
        logPointsButton.remove();
        refreshLineButton.remove();
        leftButton.remove();
        rightButton.remove();
        upButton.remove();
        downButton.remove();
        testButton.remove();
    }

    private MagnetManager.Magnets createMagnets(final WiresShape wiresShape) {
        return wiresManager.getMagnetManager().createMagnets(wiresShape, MagnetManager.FOUR_CARDINALS);
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
        return true;
    }
}
