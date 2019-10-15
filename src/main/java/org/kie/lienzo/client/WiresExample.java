package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import org.kie.lienzo.client.BaseExample;

import static com.ait.lienzo.client.core.shape.wires.LayoutContainer.Layout.CENTER;

public class WiresExample extends BaseExample implements Example
{
    public WiresExample(String title)
    {
        super(title);
    }

    public void run()
    {

        WiresManager wires_manager = WiresManager.get(layer);

        double w = 100;

        double h = 100;

        wires_manager.setConnectionAcceptor(new IConnectionAcceptor() {
            @Override
            public boolean headConnectionAllowed(WiresConnection head,
                                                 WiresShape shape) {
                WiresConnection tail = head.getConnector().getTailConnection();

                WiresMagnet m = tail.getMagnet();

                if (m == null) {
                    return true;
                }
                if (shape == null) {
                    return true;
                }
                return accept(shape.getContainer(),
                              tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean tailConnectionAllowed(WiresConnection tail,
                                                 WiresShape shape) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null) {
                    return true;
                }
                if (shape == null) {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(),
                              shape.getContainer());
            }

            @Override
            public boolean acceptHead(WiresConnection head,
                                      WiresMagnet magnet) {
                WiresConnection tail = head.getConnector().getTailConnection();

                WiresMagnet m = tail.getMagnet();

                if (m == null) {
                    return true;
                }
                if (magnet == null) {
                    return true;
                }
                return accept(magnet.getMagnets().getGroup(),
                              tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean acceptTail(WiresConnection tail,
                                      WiresMagnet magnet) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null) {
                    return true;
                }
                if (magnet == null) {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(),
                              magnet.getMagnets().getGroup());
            }

            private boolean accept(IContainer<?, ?> head,
                                   IContainer<?, ?> tail) {
                return head.getUserData().equals(tail.getUserData());
            }
        });

        // A shape can only contain shapes of different letters for UserData

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor() {
            @Override
            public boolean containmentAllowed(WiresContainer parent,
                                              WiresShape[] children) {
                return acceptContainment(parent,
                                         children);
            }

            @Override
            public boolean acceptContainment(WiresContainer parent,
                                             WiresShape[] children) {
                if (parent.getParent() == null) {
                    return true;
                }
                return !parent.getContainer().getUserData().equals(children[0].getContainer().getUserData());
            }
        });

        WiresShape wiresShape0 = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     w,
                                                                     h).setStrokeColor("#CC0000")).setDraggable(true);
        wiresShape0.setLocation(new Point2D(400, 400));

        wires_manager.register(wiresShape0);
        wiresShape0.getContainer().setUserData("A");
        wiresShape0.addChild(new Circle(30),
                             CENTER);

        WiresShape wiresShape1 = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     w,
                                                                     h).setStrokeColor("#00CC00")).setDraggable(true);
        wiresShape1.setLocation(new Point2D(50, 50));

        wires_manager.register(wiresShape1);
        wiresShape1.getContainer().setUserData("A");
        wiresShape1.addChild(new Star(5,
                                      15,
                                      40),
                             CENTER);

        WiresShape wiresShape2 = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     300,
                                                                     200).setStrokeColor("#0000CC")).setDraggable(true);
        wiresShape2.setLocation(new Point2D(50, 100));

        wires_manager.register(wiresShape2);
        wiresShape2.getContainer().setUserData("B");

        // bolt
        String svg = "M 0 100 L 65 115 L 65 105 L 120 125 L 120 115 L 200 180 L 140 160 L 140 170 L 85 150 L 85 160 L 0 140 Z";
        WiresShape wiresShape3 = new WiresShape(new MultiPath(svg).setStrokeColor("#0000CC")).setDraggable(true);
        wiresShape3.setLocation(new Point2D(50, 300));

        wires_manager.register(wiresShape3);
        wiresShape3.getContainer().setUserData("B");

        WiresShape wiresShape4 = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     w,
                                                                     h).setStrokeColor("#CC0000")).setDraggable(true);
        wiresShape4.setLocation(new Point2D(600, 400));

        wires_manager.register(wiresShape4);
        wiresShape4.getContainer().setUserData("A");
        wiresShape4.addChild(new Circle(30),
                             CENTER);

        WiresShape wiresShape5 = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     w,
                                                                     h).setStrokeColor("#00CC00")).setDraggable(true);
        wiresShape5.setLocation(new Point2D(250, 50));

        wires_manager.register(wiresShape5);
        wiresShape5.getContainer().setUserData("A");
        wiresShape5.addChild(new Star(5,
                                      15,
                                      40),
                             CENTER);

        wires_manager.getMagnetManager().createMagnets(wiresShape0);
        wires_manager.getMagnetManager().createMagnets(wiresShape1);
        wires_manager.getMagnetManager().createMagnets(wiresShape2);
        wires_manager.getMagnetManager().createMagnets(wiresShape3);
        wires_manager.getMagnetManager().createMagnets(wiresShape4);
        wires_manager.getMagnetManager().createMagnets(wiresShape5);

        connect(wiresShape1.getMagnets(),
                3,
                wiresShape0.getMagnets(),
                7,
                wires_manager,
                true);

        connect(wiresShape5.getMagnets(),
                3,
                wiresShape4.getMagnets(),
                7,
                wires_manager,
                false);
    }

    private void connect(MagnetManager.Magnets magnets0,
                         int i0_1,
                         MagnetManager.Magnets magnets1,
                         int i1_1,
                         WiresManager wiresManager,
                         boolean orthogonalPolyline) {
        WiresMagnet m0_1 = (WiresMagnet) magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = (WiresMagnet) magnets1.getMagnet(i1_1);

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

        if (orthogonalPolyline) {
            line = createOrthogonalPolyline((x0 + ((x1 - x0) / 2)),
                                            (y0 + ((y1 - y0) / 2)),
                                            x1,
                                            y1);
        } else {
            line = createPolyline((x0 + ((x1 - x0) / 2)),
                                  (y0 + ((y1 - y0) / 2)),
                                  x1,
                                  y1);
        }

        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());
        line.setSelectionStrokeOffset(25);

        WiresConnector connector = new WiresConnector(m0_1,
                                                      m1_1,
                                                      line,
                                                      new MultiPathDecorator(head),
                                                      new MultiPathDecorator(tail));
        wiresManager.register(connector);

        head.setStrokeWidth(5).setStrokeColor("#0000CC");
        tail.setStrokeWidth(5).setStrokeColor("#0000CC");
        line.setStrokeWidth(5).setStrokeColor("#0000CC");
    }

    private final OrthogonalPolyLine createOrthogonalPolyline(final double... points) {
        return new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }

    private final PolyLine createPolyline(final double... points) {
        return new PolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }
}