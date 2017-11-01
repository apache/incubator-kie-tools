package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;

/**
 * The default WiresCompositeControl implementation.
 * It orchestrates different controls for handling interactions with multiple wires shapes and connectors.
 * Notice that docking capabilities are not being considered when handling multiple wires objects.
 */
public class WiresCompositeControlImpl
        extends AbstractWiresBoundsConstraintControl
        implements WiresCompositeControl {

    private Context selectionContext;
    private BoundingBox shapeBounds;
    private boolean locationsAccepted;
    private boolean containmentAccepted;
    private Point2D delta;
    private Point2D[] locations;
    private WiresConnector[] m_connectorsWithSpecialConnections;

    public WiresCompositeControlImpl(Context selectionContext) {
        this.selectionContext = selectionContext;
    }

    @Override
    public void setContext(Context provider) {
        this.selectionContext = provider;
    }

    @Override
    protected BoundingBox getBounds() {
        return shapeBounds;
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        shapeBounds = selectionContext.getBounds();
        delta = new Point2D(0, 0);
        locationsAccepted = false;
        containmentAccepted = false;
        locations = null;

        Map<String, WiresConnector> connectors = new HashMap<String, WiresConnector>();

        for (WiresShape shape : selectionContext.getShapes()) {

            ShapeControlUtils.collectionSpecialConnectors(shape,
                                                          connectors);

            if (shape.getMagnets() != null) {
                shape.getMagnets().onNodeDragStart(null); // Must do magnets first, to avoid attribute change updates being processed.
                // Don't need to do this for nested objects, as those just move with their containers, without attribute changes
            }

            disableDocking(shape.getControl());

            final Point2D location = shape.getComputedLocation();
            final double sx = location.getX();
            final double sy = location.getY();

            shape.getControl().onMoveStart(sx,
                                           sy);
        }

        m_connectorsWithSpecialConnections = connectors.values().toArray(new WiresConnector[connectors.size()]);

        for (WiresConnector connector : selectionContext.getConnectors()) {
            WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
            handler.getControl().onMoveStart(x,
                                             y); // records the start position of all the points
            WiresConnector.updateHeadTailForRefreshedConnector(connector);
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {

        if (super.onMove(dx, dy)) {
            return true;
        }

        delta = new Point2D(dx, dy);

        // Delegate location deltas to shape controls and obtain current locations for each one.
        final Collection<WiresShape> shapes = selectionContext.getShapes();
        if (!shapes.isEmpty()) {
            final WiresManager wiresManager = shapes.iterator().next().getWiresManager();
            final Point2D[] locs = new Point2D[shapes.size()];
            int i = 0;
            for (WiresShape shape : shapes) {
                shape.getControl().onMove(dx,
                                          dy);
                locs[i++] = getCandidateShapeLocationRelativeToInitialParent(shape);
            }

            // Check if new locations are allowed.
            final WiresShape[] shapesArray = toArray(shapes);
            final boolean locationAllowed = wiresManager.getLocationAcceptor()
                    .allow(shapesArray,
                           locs);

            // Do the updates.
            if (locationAllowed) {
                i = 0;
                for (WiresShape shape : shapes) {
                    shape.getControl().getParentPickerControl().setShapeLocation(locs[i++]);
                    postUpdateShape(shape);
                }
            }
        }

        final Collection<WiresConnector> connectors = selectionContext.getConnectors();
        if (!connectors.isEmpty()) {
            // Update connectors and connections.
            for (WiresConnector connector : selectionContext.getConnectors()) {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                handler.getControl().move(dx,
                                          dy,
                                          true,
                                          true);
                WiresConnector.updateHeadTailForRefreshedConnector(connector);
            }
        }

        ShapeControlUtils.updateSpecialConnections(m_connectorsWithSpecialConnections,
                                                   false);

        return false;
    }

    private Point2D getCandidateShapeLocationRelativeToInitialParent(final WiresShape shape) {
        Point2D candidate = null;
        if (shape.getControl().getContainmentControl().isAllow()) {
            candidate = shape.getControl().getContainmentControl().getCandidateLocation();
        } else if (shape.getControl().getDockingControl().isAllow()) {
            candidate = shape.getControl().getDockingControl().getCandidateLocation();
        }
        // Check if the candidate location is relative to the parent.
        if (null != candidate) {
            final WiresParentPickerControlImpl parentPickerControl =
                    (WiresParentPickerControlImpl) shape.getControl().getParentPickerControl();
            // If the initial parent's shape and the new candidate are different,
            // then calculate candidate location using the relative distance between parents.
            if (!parentPickerControl.getShapeLocationControl().isStartDocked() &&
                    parentPickerControl.getInitialParent() != parentPickerControl.getParent()) {
                final Point2D io = null != parentPickerControl.getInitialParent() ?
                        parentPickerControl.getInitialParent().getComputedLocation() :
                        new Point2D(0, 0);
                final Point2D co = null != parentPickerControl.getParent() ?
                        parentPickerControl.getParent().getComputedLocation() :
                        new Point2D(0, 0);
                return co.add(candidate).minus(io);
            }
            // Otherwise, if parents match, just return the candidate relative location.
            return candidate;
        }
        return shape.getControl().getParentPickerControl().getShapeLocation();
    }

    public boolean isAllowed() {
        // Check parents && allow acceptors.
        final WiresContainer parent = getSharedParent();
        return null != parent &&
                parent.getWiresManager().getContainmentAcceptor()
                        .containmentAllowed(parent,
                                            toArray(selectionContext.getShapes()));
    }

    public WiresContainer getSharedParent() {
        final Collection<WiresShape> shapes = selectionContext.getShapes();
        if (shapes.isEmpty()) {
            return null;
        }
        WiresContainer shared = shapes.iterator().next().getControl().getParentPickerControl().getParent();
        for (WiresShape shape : shapes) {
            WiresContainer parent = shape.getControl().getParentPickerControl().getParent();
            if (parent != shared) {
                return null;
            }
        }
        return shared;
    }

    @Override
    public boolean onMoveComplete() {
        boolean completeResult = true;
        // Propagate events.
        final Collection<WiresShape> shapes = selectionContext.getShapes();
        if (!shapes.isEmpty()) {
            final WiresManager wiresManager = shapes.iterator().next().getWiresManager();
            final Point2D[] shapeLocations = new Point2D[shapes.size()];
            final Point2D[] shapeCandidateLocations = new Point2D[shapes.size()];
            int i = 0;
            for (WiresShape shape : shapes) {
                shape.getControl().onMoveComplete();
                shapeLocations[i] = shape.getControl().getParentPickerControl().getCurrentLocation();
                shapeCandidateLocations[i] = shape.getControl().getContainmentControl().getCandidateLocation();
                i++;
            }

            // Check parents && acceptors' acceptance.
            final WiresShape[] shapesArray = toArray(shapes);
            final WiresContainer parent = getSharedParent();
            containmentAccepted = null != parent &&
                    wiresManager.getContainmentAcceptor()
                            .acceptContainment(parent,
                                               shapesArray);

            locations = containmentAccepted ?
                    shapeCandidateLocations :
                    shapeLocations;

            // Check new locations are accepted.
            locationsAccepted = wiresManager.getLocationAcceptor()
                    .accept(shapesArray,
                            locations);
            completeResult = containmentAccepted && locationsAccepted;
        }

        if (completeResult) {
            final Collection<WiresConnector> connectors = selectionContext.getConnectors();
            if (!connectors.isEmpty()) {
                // Update connectors and connections.
                for (WiresConnector connector : selectionContext.getConnectors()) {
                    WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                    handler.getControl().onMoveComplete();
                    WiresConnector.updateHeadTailForRefreshedConnector(connector);
                }
            }
        }

        delta = new Point2D(0, 0);
        shapeBounds = null;
        return completeResult;
    }

    @Override
    public void execute() {
        int i = 0;
        for (WiresShape shape : selectionContext.getShapes()) {
            if (containmentAccepted) {
                shape.getControl().getContainmentControl().execute();
            } else {
                shape.getControl().getParentPickerControl().setShapeLocation(locations[i++]);
            }
            postUpdateShape(shape);
        }

        for (WiresConnector connector : selectionContext.getConnectors()) {
            WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
            handler.getControl().onMoveComplete(); // must be called to null the  points array
            WiresConnector.updateHeadTailForRefreshedConnector(connector);
        }

        ShapeControlUtils.updateSpecialConnections(m_connectorsWithSpecialConnections,
                                                   true);

        clear();
    }

    @Override
    public void clear() {
        for (WiresShape shape : selectionContext.getShapes()) {
            enableDocking(shape.getControl());
            shape.getControl().clear();
        }
        clearState();
    }

    @Override
    public void reset() {
        for (WiresShape shape : selectionContext.getShapes()) {
            shape.getControl().reset();
            shape.shapeMoved();
        }
        for (WiresConnector connector : selectionContext.getConnectors()) {
            WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
            handler.getControl().reset();
            WiresConnector.updateHeadTailForRefreshedConnector(connector);
        }
        clearState();
    }

    @Override
    public Point2D getAdjust() {
        return delta;
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        for (WiresShape shape : selectionContext.getShapes()) {
            shape.getControl().onMouseClick(event);
        }
    }

    @Override
    public void onMouseDown(MouseEvent event) {
        for (WiresShape shape : selectionContext.getShapes()) {
            shape.getControl().onMouseDown(event);
        }
    }

    @Override
    public void onMouseUp(MouseEvent event) {
        for (WiresShape shape : selectionContext.getShapes()) {
            shape.getControl().onMouseUp(event);
        }
    }

    private void clearState() {
        delta = new Point2D(0, 0);
        locationsAccepted = false;
        containmentAccepted = false;
        locations = null;
        m_connectorsWithSpecialConnections = null;
    }

    private static void disableDocking(WiresShapeControl control) {
        control.getDockingControl().setEnabled(false);
    }

    private static void enableDocking(WiresShapeControl control) {
        control.getDockingControl().setEnabled(true);
    }

    private void postUpdateShape(final WiresShape shape) {
        shape.getControl().getMagnetsControl().shapeMoved();
        ShapeControlUtils.updateNestedShapes(shape);
    }

    private static WiresShape[] toArray(final Collection<WiresShape> shapes) {
        return shapes.toArray(new WiresShape[shapes.size()]);
    }
}
