package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.IShapeDecorator.ShapeState;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArray;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

import static com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.R0;
import static com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.R1;
import static com.ait.lienzo.client.core.shape.AbstractMultiPointShape.DefaultMultiPointShapeHandleFactory.SELECTION_OFFSET;

/**
 * This class can be a little confusing, due to the way that drag works.
 * All lines have a Group that is x=0, y=0. when you drag a line, you
 * actually drag a group. So the group x,y changes, the line does not.
 * For this reason the CPs are moved with the group, during drag. When
 * the drag ends, the Group is re-adjusted back to 0,0 and the lines
 * have their points adjusted to reflect the final position.
 * However if the lines are part selection that is being dragged
 * then the move behaviour is different, the group is not moving, so the
 * line points must move. This differing behaviour is controlled by
 * booleans on the relevant classes.
 */
public class WiresConnectorControlImpl implements WiresConnectorControl {

    private final WiresConnector m_connector;
    private final WiresManager m_wiresManager;

    private NFastDoubleArray m_startPointHandles;
    private HandlerRegistrationManager m_HandlerRegistrationManager;
    private Point2DArray m_startLinePoints;
    private WiresConnectionControl m_headConnectionControl;
    private WiresConnectionControl m_tailConnectionControl;
    private PointHandleDecorator m_pointHandleDecorator;
    private Shape<?> transientControlHandle;
    private final Collection<HandlerRegistration> transientControlHandleRegistrations;
    private AddControlPointTimer addControlPointTimer;

    public WiresConnectorControlImpl(final WiresConnector connector,
                                     final WiresManager wiresManager) {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
        this.m_pointHandleDecorator = new PointHandleDecorator();
        this.transientControlHandleRegistrations = new ArrayList<>();
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        m_startLinePoints = m_connector.getLine().getPoint2DArray().copy();
        m_startPointHandles = new NFastDoubleArray();
        IControlHandleList handles = m_connector.getPointHandles();
        for (int i = 0; i < handles.size(); i++) {
            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            m_startPointHandles.push(prim.getX());
            m_startPointHandles.push(prim.getY());
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        move(dx,
             dy);
        return false;
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0,
                           0);
    }

    private void move(double dx,
                     double dy) {
        final boolean isConnected = isConnected(m_connector.getHeadConnection()) || isConnected(m_connector.getTailConnection());

        IControlHandleList handles = m_connector.getPointHandles();

        int start = 0;
        int end = handles.size();
        if (isConnected) {
            if (m_connector.getHeadConnection().getMagnet() != null) {
                start++;
            }
            if (m_connector.getTailConnection().getMagnet() != null) {
                end--;
            }
        }

        Point2DArray points = m_connector.getLine().getPoint2DArray();

        for (int i = start, j = (start == 0) ? start : 2; i < end; i++, j += 2) {
            if (isConnected) {
                Point2D p = points.get(i);
                p.setX(m_startPointHandles.get(j) + dx);
                p.setY(m_startPointHandles.get(j + 1) + dy);
            }

            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            prim.setX(m_startPointHandles.get(j) + dx);
            prim.setY(m_startPointHandles.get(j + 1) + dy);
        }

        if (isConnected) {
            m_connector.getLine().refresh();
        }

        WiresConnector.updateHeadTailForRefreshedConnector(m_connector);

        batch();
    }

    @Override
    public boolean onMoveComplete() {
        m_connector.getGroup().setX(0).setY(0);
        batch();
        return true;
    }

    @Override
    public void execute()
    {
        WiresConnector.updateHeadTailForRefreshedConnector(m_connector);
        getControlPointsAcceptor().move(m_connector,
                                        m_connector.getControlPoints().copy());
    }

    @Override
    public void clear()
    {
        hideControlPoints();
        m_startPointHandles = null;
        m_startLinePoints = null;
    }

    @Override
    public void reset() {
        boolean done = false;
        if (null != m_startPointHandles) {
            final IControlHandleList handles = m_connector.getPointHandles();
            for (int i = 0, j = 0; i < handles.size(); i++, j += 2) {
                final double px = m_startPointHandles.get(j);
                final double py = m_startPointHandles.get(j + 1);
                final IControlHandle h = handles.getHandle(i);
                final IPrimitive<?> prim = h.getControl();
                prim.setX(px);
                prim.setY(py);
            }
            done = true;
        }

        if (null != m_startLinePoints) {
            Point2DArray points = m_connector.getLine().getPoint2DArray();
            for (int i = 0; i < points.size(); i++)
            {
                final Point2D point = points.get(i);
                final Point2D startPoint = m_startLinePoints.get(i);
                point.setX(startPoint.getX());
                point.setY(startPoint.getY());
            }
            done = true;
        }

        if (done) {
            m_connector.getLine().refresh();
            batch();
        }

        m_startPointHandles = null;
        m_startLinePoints = null;
    }

    @Override
    public int addControlPoint(final double x,
                                final double y) {

        final int index = m_connector.getControlPointIndex(x, y);
        if (index < 0)
        {
            final int pointIndex = m_connector.getIndexForSelectedSegment((int) x,
                                                                          (int) y);
            if (pointIndex > -1 && addControlPoint(x, y, pointIndex))
            {
                refreshControlPoints();
            }
            return pointIndex;
        }
        return index;
    }

    public boolean addControlPoint(final double x,
                                      final double y,
                                      final int index) {
        if (getControlPointsAcceptor().add(m_connector, index, new Point2D(x, y))) {
            m_connector.addControlPoint(x, y, index);
            return true;
        }
        return false;
    }

    @Override
    public void destroyControlPoint(final int index) {
        // Connection (line) need at least 2 points to be drawn
        if (m_connector.getPointHandles().size() <= 2) {
            return;
        }
        if (getControlPointsAcceptor().delete(m_connector,
                                              index)) {
            m_connector.destroyControlPoints(new int[] { index });
        }
        refreshControlPoints();
    }

    @Override
    public void showControlPoints() {
        showPointHandles();
    }

    @Override
    public void hideControlPoints() {
        destroyPointHandles();
    }

    @Override
    public boolean areControlPointsVisible()
    {
        return null != m_connector.getPointHandles() && m_connector.getPointHandles().isVisible();
    }

    @Override
    public boolean moveControlPoint(final int index,
                                           final Point2D location)
    {
        final Point2DArray controlPoints = m_connector.getControlPoints();
        // Notice that control points [0] and [controlPoints.size - 1] are being used for the connections as well,
        // so they're being updated anyway on other event handlers - it must return "true" in that case.
        if (index > 0 && index < (controlPoints.size() -1)) {
            final double tx = location.getX();
            final double ty = location.getY();
            final boolean accept = getControlPointsAcceptor().move(m_connector,
                                                                   controlPoints
                                                                              .copy()
                                                                              .set(index, location));
            // Check rollback
            if (accept) {
                m_connector.moveControlPoint(index, new Point2D(tx, ty));
            }
            return accept;
        }
        return true;
    }

    @Override
    public WiresConnectionControl getHeadConnectionControl() {
        return m_headConnectionControl;
    }

    @Override
    public WiresConnectionControl getTailConnectionControl() {
        return m_tailConnectionControl;
    }

    public void showPointHandles() {
        if (m_HandlerRegistrationManager == null) {

            m_HandlerRegistrationManager = m_connector.getPointHandles().getHandlerRegistrationManager();

            m_connector.getPointHandles().show();

            final WiresControlPointHandler controlPointsHandler =
                    m_wiresManager.getWiresHandlerFactory().newControlPointHandler(m_connector, m_wiresManager);
            final ControlPointDecoratorHandler controlHandleDecoratorHandler = new ControlPointDecoratorHandler();
            for (int i = 1; i < m_connector.getPointHandles().size() - 1; i++) {
                final IControlHandle handle = m_connector.getPointHandles().getHandle(i);
                final Shape<?> shape = handle.getControl().asShape();
                m_HandlerRegistrationManager.register(shape.addNodeMouseClickHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeMouseDoubleClickHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragStartHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragEndHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragMoveHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragStartHandler(controlHandleDecoratorHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragEndHandler(controlHandleDecoratorHandler));
                m_pointHandleDecorator.decorate(shape, ShapeState.VALID);
                //enforce drag constraints on the point handles
                shape.setDragConstraints(new DefaultDragConstraintEnforcer());
                shape.setDragBounds(m_connector.getGroup().getDragBounds());
                shape.moveToTop();
            }

            m_headConnectionControl = m_wiresManager.getControlFactory()
                                                    .newConnectionControl(m_connector,
                                                                          true,
                                                                          m_wiresManager);
            Shape<?> head = m_connector.getHeadConnection().getControl().asShape();
            ConnectionHandler headConnectionHandler = new ConnectionHandler(m_headConnectionControl, head, m_connector.getHeadConnection());
            head.setDragConstraints(headConnectionHandler);
            m_HandlerRegistrationManager.register(head.addNodeDragEndHandler(headConnectionHandler));

            m_tailConnectionControl = m_wiresManager.getControlFactory()
                                                    .newConnectionControl(m_connector,
                                                                          false,
                                                                          m_wiresManager);
            Shape<?> tail = m_connector.getTailConnection().getControl().asShape();
            ConnectionHandler tailConnectionHandler = new ConnectionHandler(m_tailConnectionControl, tail, m_connector.getTailConnection());
            tail.setDragConstraints(tailConnectionHandler);
            m_HandlerRegistrationManager.register(tail.addNodeDragEndHandler(tailConnectionHandler));
        }
    }

    private void destroyPointHandles() {
        if (null != m_HandlerRegistrationManager) {
            m_HandlerRegistrationManager.destroy();
            m_HandlerRegistrationManager = null;
            m_headConnectionControl = null;
            m_tailConnectionControl = null;
        }
        m_connector.destroyPointHandles();
        destroyTransientControlHandle();
    }

    protected final class ControlPointDecoratorHandler implements NodeDragStartHandler,
                                                                NodeDragEndHandler {

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            decorateShape(event.getDragContext(), ShapeState.INVALID);
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            decorateShape(event.getDragContext(), ShapeState.VALID);
        }

        private void decorateShape(DragContext dragContext, ShapeState state) {
            final Shape node = (Shape) dragContext.getNode();
            m_pointHandleDecorator.decorate(node, state);
        }
    }

    protected final class ConnectionHandler implements DragConstraintEnforcer,
                                                     NodeDragEndHandler {
        private final WiresConnectionControl connectionControl;
        private final Shape<?> shape;
        private final WiresConnection connection;

        private ConnectionHandler(final WiresConnectionControl connectionControl, Shape<?> shape, WiresConnection connection) {
            this.connectionControl = connectionControl;
            this.shape = shape;
            this.connection = connection;
            m_pointHandleDecorator.decorate(shape, ShapeState.VALID);
        }

        @Override
        public void startDrag(final DragContext dragContext) {
            connectionControl.onMoveStart(dragContext.getDragStartX(),
                                          dragContext.getDragStartY());
            m_pointHandleDecorator.decorate(this.shape, ShapeState.INVALID);
        }

        @Override
        public boolean adjust(final Point2D dxy) {
            boolean adjusted = false;
            if (connectionControl.onMove(dxy.getX(),
                                         dxy.getY())) {
                // Check if need for drag adjustments.
                final Point2D adjustPoint = connectionControl.getAdjust();
                if (!adjustPoint.equals(new Point2D(0,
                                                    0))) {
                    dxy.set(adjustPoint);
                    adjusted = true;
                }
            }
            m_pointHandleDecorator.decorate(shape, adjusted ? ShapeState.VALID : ShapeState.INVALID);
            return adjusted;
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            final boolean allowed = connectionControl.onMoveComplete();

            // Cancel the drag operation if the connection operation is not allowed.
            if (!allowed) {
                event.getDragContext().reset();
            }
            //decorate connection shape
            final ShapeState shapeState = (connection.isAutoConnection() || connection.getMagnet() != null ?
                    ShapeState.VALID : ShapeState.INVALID);
            m_pointHandleDecorator.decorate(shape, shapeState);
        }
    }

    private void refreshControlPoints() {
        hideControlPoints();
        showPointHandles();
    }

    private void batch() {
        if (null != m_wiresManager.getLayer().getLayer()) {
            m_wiresManager.getLayer().getLayer().batch();
        }
    }

    private IControlPointsAcceptor getControlPointsAcceptor() {
        return m_wiresManager.getControlPointsAcceptor();
    }

    private static boolean isConnected(final WiresConnection connection) {
        return null != connection && null != connection.getMagnet();
    }

    @Override
    public void destroy() {
        clear();
        destroyPointHandles();
    }

    public void setPointHandleDecorator(PointHandleDecorator pointHandleDecorator) {
        this.m_pointHandleDecorator = pointHandleDecorator;
    }

    /**
     * Timer class responsible to delay simulating a long press when click + drag to add a new
     * control point. Besides that the delay is necessary to preserve the selection on connector
     * when there is only a click, in this case this time should be cancelled.
     */
    protected class AddControlPointTimer extends Timer {

        Consumer<Point2D> addControlHandleConsumer;
        Point2D point;

        public AddControlPointTimer(Consumer<Point2D> addControlHandleConsumer, Point2D point) {
            this.addControlHandleConsumer = addControlHandleConsumer;
            this.point = point;
        }

        @Override
        public void run() {
            addControlHandleConsumer.accept(point);
        }
    }

    @Override
    public Shape<?> createTransientControlHandle(final Consumer<Point2D> addControlHandleConsumer) {
        final Shape<?> pointHandleShape = new Circle(R0);
        m_pointHandleDecorator.decorate(pointHandleShape, IShapeDecorator.ShapeState.INVALID);
        //increase the selection area to make it easier do drag
        pointHandleShape.getAttributes().setSelectionBoundsOffset(SELECTION_OFFSET);
        pointHandleShape.getAttributes().setSelectionStrokeOffset(SELECTION_OFFSET);
        pointHandleShape.setFillBoundsForSelection(true);

        //adding the handlers on the transient control handle
        transientControlHandleRegistrations.add(pointHandleShape.addNodeMouseDownHandler(new NodeMouseDownHandler() {
            @Override
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                if(addControlPointTimer == null){
                    addControlPointTimer = createAddControlPointTimer(new Point2D(event.getX(), event.getY()), addControlHandleConsumer);
                    addControlPointTimer.schedule(150);
                    event.preventDefault();
                }
            }
        }));

        transientControlHandleRegistrations.add(pointHandleShape.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(NodeMouseClickEvent event) {
                if(addControlPointTimer != null){
                    //when clicking then cancel the add connector action and fire the click on the connector line
                    addControlPointTimer.cancel();
                    addControlPointTimer = null;
                    event.preventDefault();
                    m_connector.getLine().fireEvent(event);
                }
            }
        }));

        //add the shape on the connector line
        m_connector.getLine().getLayer().add(pointHandleShape);

        this.transientControlHandle = pointHandleShape;
        return pointHandleShape;
    }

    protected AddControlPointTimer createAddControlPointTimer(Point2D point, Consumer<Point2D> addControlHandleConsumer) {
        return new AddControlPointTimer(addControlHandleConsumer, point);
    }

    @Override
    public void destroyTransientControlHandle() {
        for (HandlerRegistration registration : transientControlHandleRegistrations) {
            registration.removeHandler();
        }
        transientControlHandleRegistrations.clear();
        if (transientControlHandle != null) {
            transientControlHandle.removeFromParent();
            transientControlHandle = null;
            batchConnector();
        }
        addControlPointTimer = null;
    }

    @Override
    public Shape<?> getTransientControlHandle() {
        return transientControlHandle;
    }

    private void batchConnector() {
        m_connector.getLine().getLayer().batch();
    }
}