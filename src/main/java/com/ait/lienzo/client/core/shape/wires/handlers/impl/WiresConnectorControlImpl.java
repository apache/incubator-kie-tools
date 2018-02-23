package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.BackingColorMapUtils;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArray;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

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

    public static final int MINIMUM_STROKE_WITH = 4;
    private WiresConnector m_connector;

    private HandlerRegistrationManager m_HandlerRegistrationManager;

    private WiresManager m_wiresManager;

    private NFastDoubleArray m_startPoints;

    private WiresConnectionControl m_headConnectionControl;

    private WiresConnectionControl m_tailConnectionControl;

    public WiresConnectorControlImpl(final WiresConnector connector,
                                     final WiresManager wiresManager) {
        this.m_connector = connector;
        this.m_wiresManager = wiresManager;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        IControlHandleList handles = m_connector.getPointHandles();

        m_startPoints = new NFastDoubleArray();

        for (int i = 0; i < handles.size(); i++) {
            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            m_startPoints.push(prim.getX());
            m_startPoints.push(prim.getY());
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        move(dx,
             dy,
             false,
             false);
        return false;
    }

    @Override
    public boolean onMoveComplete() {
        m_connector.getGroup().setX(0).setY(0);

        // move(context.getDx(), context.getDy(), false, true);

        m_wiresManager.getLayer().getLayer().batch();

        dragEnd();

        return true;
    }

    @Override
    public Point2D getAdjust() {
        return new Point2D(0,
                           0);
    }

    public void dragEnd() {
        m_startPoints = null;
        ;
    }

    /**
     * See class javadocs to explain why we have these booleans
     */
    public void move(double dx,
                     double dy,
                     boolean midPointsOnly,
                     boolean moveLinePoints) {
        IControlHandleList handles = m_connector.getPointHandles();

        int start = 0;
        int end = handles.size();
        if (midPointsOnly) {
            if (m_connector.getHeadConnection().getMagnet() != null) {
                start++;
            }
            if (m_connector.getTailConnection().getMagnet() != null) {
                end--;
            }
        }

        Point2DArray points = m_connector.getLine().getPoint2DArray();

        for (int i = start, j = (start == 0) ? start : 2; i < end; i++, j += 2) {
            if (moveLinePoints) {
                Point2D p = points.get(i);
                p.setX(m_startPoints.get(j) + dx);
                p.setY(m_startPoints.get(j + 1) + dy);
            }

            IControlHandle h = handles.getHandle(i);
            IPrimitive<?> prim = h.getControl();
            prim.setX(m_startPoints.get(j) + dx);
            prim.setY(m_startPoints.get(j + 1) + dy);
        }

        if (moveLinePoints) {
            m_connector.getLine().refresh();
        }

        m_wiresManager.getLayer().getLayer().batch();
    }

    /*
        ***************** CONTROL POINTS **********************************
     */

    @Override
    public void reset() {
        if (null != m_startPoints) {
            final IControlHandleList handles = m_connector.getPointHandles();
            Point2DArray points = m_connector.getLine().getPoint2DArray();
            for (int i = 0, j = 0; i < handles.size(); i++, j += 2) {
                final double px = m_startPoints.get(j);
                final double py = m_startPoints.get(j + 1);
                final IControlHandle h = handles.getHandle(i);
                final IPrimitive<?> prim = h.getControl();
                prim.setX(px);
                prim.setY(py);
                final Point2D point = points.get(i);
                point.setX(px);
                point.setY(py);
            }
        }
        m_connector.getLine().refresh();
        m_wiresManager.getLayer().getLayer().batch();
    }

    @Override
    public int addControlPoint(final double x,
                                final double y) {

        hideControlPoints();
        Point2DArray oldPoints = m_connector.getLine().getPoint2DArray();

        int pointIndex = getIndexForSelectedSegment(m_connector,
                                                    (int) x,
                                                    (int) y,
                                                    oldPoints);
        if (pointIndex > 0) {
            Point2D point = new Point2D(x,
                                        y);
            Point2DArray newPoints = new Point2DArray();
            newPoints.push(oldPoints.get(0));
            for (int i = 1; i < pointIndex; i++) {
                newPoints.push(oldPoints.get(i));
            }
            newPoints.push(point);
            for (int i = pointIndex; i < oldPoints.size(); i++) {
                newPoints.push(oldPoints.get(i));
            }
            m_connector.getLine().setPoint2DArray(newPoints);
        }

        showPointHandles();

        m_connector.getLine().getLayer().batch();
        return pointIndex;
    }

    @Override
    public void addControlPointToLine(final double x,
                                      final double y,
                                      final int index) {

        Point2DArray oldPoints = m_connector.getLine().getPoint2DArray();
        if (index <= 0 || index >= oldPoints.size()) {
            throw new IllegalArgumentException("Index should be between head and tail line points. Index: " + index);
        }

        Point2DArray newPoints = new Point2DArray();

        for (int i = 0; i < oldPoints.size(); i++) {
            if (i == index) {
                newPoints.push(new Point2D(x, y));
            }
            newPoints.push(oldPoints.get(i));
        }

        m_connector.getLine().setPoint2DArray(newPoints);
        addControlPoint(x, y);
        m_connector.select();
    }

    @Override
    public void removeControlPoint(final double x, final double y) {
        Point2DArray points = m_connector.getLine().getPoint2DArray();
        Point2DArray newPoints = new Point2DArray();
        for (Point2D point : points) {
            if (point.getX() == x && point.getY() == y) {
                continue;
            }
            newPoints.push(point);
        }
        m_connector.getLine().setPoint2DArray(newPoints);
        m_connector.select();
    }

    @Override
    public void destroyControlPoint(final IPrimitive<?> control) {
        // Connection (line) need at least 2 points to be drawn
        if (m_connector.getPointHandles().size() <= 2) {
            m_wiresManager.deregister(m_connector);
            return;
        }

        IControlHandle selected = null;

        for (IControlHandle handle : m_connector.getPointHandles()) {
            if (handle.getControl() == control) {
                selected = handle;
                break;
            }
        }

        if (null == selected) {
            return;
        }

        Point2DArray oldPoints = m_connector.getLine().getPoint2DArray();
        Point2DArray newPoints = new Point2DArray();
        Point2D selectedPoint2D = selected.getControl().getLocation();
        for (int i = 0; i < oldPoints.size(); i++) {
            Point2D current = oldPoints.get(i);

            if (!current.equals(selectedPoint2D)) {
                newPoints.push(current);
            }
        }

        m_connector.getLine().setPoint2DArray(newPoints);

        destroyPointHandles();
        showPointHandles();
    }

    @Override
    public void showControlPoints() {
        showPointHandles();
    }

    @Override
    public void hideControlPoints() {
        if (m_HandlerRegistrationManager != null) {
            destroyPointHandles();
        }
    }

    @Override
    public Point2D adjustControlPointAt(double x, double y, double deltaX, double deltaY) {
        return m_connector.getLine().adjustPoint(x, y, deltaX, deltaY);
    }

    @Override
    public WiresConnectionControl getHeadConnectionControl() {
        return m_headConnectionControl;
    }

    @Override
    public WiresConnectionControl getTailConnectionControl() {
        return m_tailConnectionControl;
    }

    public HandlerRegistrationManager getHandlerRegistrationManager() {
        return m_HandlerRegistrationManager;
    }

    public static int getIndexForSelectedSegment(final WiresConnector connector,
                                                 final int mouseX,
                                                 final int mouseY,
                                                 final Point2DArray oldPoints) {
        NFastStringMap<Integer> colorMap = new NFastStringMap<Integer>();

        IDirectionalMultiPointShape<?> line = connector.getLine();
        ScratchPad scratch = line.getScratchPad();
        scratch.clear();
        PathPartList path = line.asShape().getPathPartList();
        int pointsIndex = 1;
        String color = MagnetManager.m_c_rotor.next();
        colorMap.put(color,
                     pointsIndex);
        Context2D ctx = scratch.getContext();
        double strokeWidth = line.asShape().getStrokeWidth();
        //setting a minimum stroke width to make finding a close point to the connector easier
        ctx.setStrokeWidth((strokeWidth < MINIMUM_STROKE_WITH ? MINIMUM_STROKE_WITH : strokeWidth));

        Point2D absolutePos = connector.getLine().getComputedLocation();
        double offsetX = absolutePos.getX();
        double offsetY = absolutePos.getY();

        Point2D pathStart = new Point2D(offsetX,
                                        offsetY);
        Point2D segmentStart = pathStart;

        for (int i = 0; i < path.size(); i++) {
            PathPartEntryJSO entry = path.get(i);
            NFastDoubleArrayJSO points = entry.getPoints();

            switch (entry.getCommand()) {
                case PathPartEntryJSO.MOVETO_ABSOLUTE: {
                    double x0 = points.get(0) + offsetX;
                    double y0 = points.get(1) + offsetY;
                    Point2D m = new Point2D(x0,
                                            y0);
                    if (i == 0) {
                        // this is position is needed, if we close the path.
                        pathStart = m;
                    }
                    segmentStart = m;
                    break;
                }
                case PathPartEntryJSO.LINETO_ABSOLUTE: {
                    points = entry.getPoints();
                    double x0 = points.get(0) + offsetX;
                    double y0 = points.get(1) + offsetY;
                    Point2D end = new Point2D(x0,
                                              y0);

                    if (oldPoints.get(pointsIndex).equals(segmentStart)) {
                        pointsIndex++;
                        color = MagnetManager.m_c_rotor.next();
                        colorMap.put(color,
                                     pointsIndex);
                    }
                    ctx.setStrokeColor(color);

                    ctx.beginPath();
                    ctx.moveTo(segmentStart.getX(),
                               segmentStart.getY());
                    ctx.lineTo(x0,
                               y0);
                    ctx.stroke();
                    segmentStart = end;
                    break;
                }
                case PathPartEntryJSO.CLOSE_PATH_PART: {
                    double x0 = pathStart.getX() + offsetX;
                    double y0 = pathStart.getY() + offsetY;
                    Point2D end = new Point2D(x0,
                                              y0);
                    if (oldPoints.get(pointsIndex).equals(segmentStart)) {
                        pointsIndex++;
                        color = MagnetManager.m_c_rotor.next();
                        colorMap.put(color,
                                     pointsIndex);
                    }
                    ctx.setStrokeColor(color);
                    ctx.beginPath();
                    ctx.moveTo(segmentStart.getX(),
                               segmentStart.getY());
                    ctx.lineTo(x0,
                               y0);
                    ctx.stroke();
                    segmentStart = end;
                    break;
                }
                case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE: {
                    points = entry.getPoints();

                    double x0 = points.get(0) + offsetX;
                    double y0 = points.get(1) + offsetY;
                    Point2D p0 = new Point2D(x0,
                                             y0);

                    double x1 = points.get(2) + offsetX;
                    double y1 = points.get(3) + offsetY;
                    double r = points.get(4);
                    Point2D p1 = new Point2D(x1,
                                             y1);
                    Point2D end = p1;

                    if (p0.equals(oldPoints.get(pointsIndex))) {
                        pointsIndex++;
                        color = MagnetManager.m_c_rotor.next();
                        colorMap.put(color,
                                     pointsIndex);
                    }
                    ctx.setStrokeColor(color);
                    ctx.beginPath();
                    ctx.moveTo(segmentStart.getX(),
                               segmentStart.getY());
                    ctx.arcTo(x0,
                              y0,
                              x1,
                              y1,
                              r);
                    ctx.stroke();

                    segmentStart = end;
                    break;
                }
            }
        }

        BoundingBox box = connector.getLine().getBoundingBox();

        // Keep the ImageData small by clipping just the visible line area
        // But remember the mouse must be offset for this clipped area.
        int sx = (int) (box.getX() - strokeWidth - offsetX);
        int sy = (int) (box.getY() - strokeWidth - offsetY);
        ImageData backing = ctx.getImageData(sx,
                                             sy,
                                             (int) (box.getWidth() + strokeWidth + strokeWidth),
                                             (int) (box.getHeight() + strokeWidth + strokeWidth));
        color = BackingColorMapUtils.findColorAtPoint(backing,
                                                      mouseX - sx,
                                                      mouseY - sy);
        return null != color ? colorMap.get(color) : -1;
    }

    public void showPointHandles() {
        if (m_HandlerRegistrationManager == null) {
            m_HandlerRegistrationManager = m_connector.getPointHandles().getHandlerRegistrationManager();
            m_connector.getPointHandles().show();

            m_headConnectionControl = m_wiresManager.getControlFactory()
                    .newConnectionControl(m_connector,
                                          true,
                                          m_wiresManager);
            ConnectionHandler headConnectionHandler = new ConnectionHandler(m_headConnectionControl);
            Shape<?> head = m_connector.getHeadConnection().getControl().asShape();
            head.setDragConstraints(headConnectionHandler);
            m_HandlerRegistrationManager.register(head.addNodeDragEndHandler(headConnectionHandler));

            m_tailConnectionControl = m_wiresManager.getControlFactory()
                    .newConnectionControl(m_connector,
                                          false,
                                          m_wiresManager);
            ConnectionHandler tailConnectionHandler = new ConnectionHandler(m_tailConnectionControl);
            Shape<?> tail = m_connector.getTailConnection().getControl().asShape();
            tail.setDragConstraints(tailConnectionHandler);
            m_HandlerRegistrationManager.register(tail.addNodeDragEndHandler(tailConnectionHandler));

            final WiresControlPointHandler controlPointsHandler =
                    m_wiresManager.getWiresHandlerFactory().newControlPointHandler(m_connector, this);

            for (IControlHandle handle : m_connector.getPointHandles()) {
                Shape<?> shape = handle.getControl().asShape();
                m_HandlerRegistrationManager.register(shape.addNodeMouseDoubleClickHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragStartHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragEndHandler(controlPointsHandler));
                m_HandlerRegistrationManager.register(shape.addNodeDragMoveHandler(controlPointsHandler));
            }
        }
    }

    private void destroyPointHandles() {
        m_HandlerRegistrationManager.destroy();
        m_HandlerRegistrationManager = null;
        m_headConnectionControl = null;
        m_tailConnectionControl = null;
        m_connector.destroyPointHandles();
    }

    private final class ConnectionHandler implements DragConstraintEnforcer,
                                                     NodeDragEndHandler {

        private final WiresConnectionControl connectionControl;

        private ConnectionHandler(final WiresConnectionControl connectionControl) {
            this.connectionControl = connectionControl;
        }

        @Override
        public void startDrag(final DragContext dragContext) {
            connectionControl.onMoveStart(dragContext.getDragStartX(),
                                          dragContext.getDragStartY());
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
            return adjusted;
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            final boolean allowed = connectionControl.onMoveComplete();

            // Cancel the drag operation if the connection operation is not allowed.
            if (!allowed) {
                event.getDragContext().reset();
            }
        }
    }
}