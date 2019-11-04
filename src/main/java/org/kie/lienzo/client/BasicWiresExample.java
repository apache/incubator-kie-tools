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
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.google.gwt.dom.client.Style;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;

import static org.kie.lienzo.client.util.WiresUtils.connect;

public class BasicWiresExample extends BaseExample implements Example {

    private static final String LABEL_PARENT = "parent";
    private static final String LABEL_CHILD = "child";

    private HTMLButtonElement button1;
    private WiresManager wiresManager;
    private WiresShape rectangleRedShape;
    private WiresShape circleRedShape;
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

        button1 = createButton("Button1", this::onButton1Click);
        topDiv.appendChild(button1);
    }

    @Override
    public void run() {
        // Wires setup
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
                .setLocation(new Point2D(100, 600));
        rectangleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleRedShape);
        wiresManager.getMagnetManager().createMagnets(rectangleRedShape);


        // Circle - Red
        circleRedShape = new WiresShape(new MultiPath().circle(50)
                                                   .setStrokeColor("#FF0000")
                                                   .setFillColor("#FF0000"))
                .setDraggable(true)
                .setLocation(new Point2D(400, 600));
        circleRedShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(circleRedShape);
        wiresManager.getMagnetManager().createMagnets(circleRedShape);

        // Rectangle - Blue
        rectangleBlueShape = new WiresShape(new MultiPath().rect(0, 0, 100, 100)
                                                               .setStrokeColor("#0000FF")
                                                               .setFillColor("#0000FF"))
                .setDraggable(true)
                .setLocation(new Point2D(650, 600));
        rectangleBlueShape.getGroup().setUserData(LABEL_CHILD);
        wiresManager.register(rectangleBlueShape);
        wiresManager.getMagnetManager().createMagnets(rectangleBlueShape);

        // Rectangle - Black
        rectangleBlackShape = new WiresShape(new MultiPath().rect(0, 0, 350, 450)
                                                    .setStrokeColor("#000000")
                                                    .setFillColor("#FFFFFF"))
                .setDraggable(true)
                .setLocation(new Point2D(50, 50));
        rectangleBlackShape.getGroup().setUserData(LABEL_PARENT);
        wiresManager.register(rectangleBlackShape);
        wiresManager.getMagnetManager().createMagnets(rectangleBlackShape);

        // Connection
        connect(rectangleRedShape.getMagnets(),
                3,
                circleRedShape.getMagnets(),
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
        if (LABEL_PARENT.equals(parent.getGroup().getUserData())) {
            for (WiresShape child : children) {
                Object data = child.getGroup().getUserData();
                if (!LABEL_CHILD.equals(data)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        button1.remove();
    }

    private void onButton1Click() {
        DomGlobal.alert("Button1 clicked!");
    }

}
