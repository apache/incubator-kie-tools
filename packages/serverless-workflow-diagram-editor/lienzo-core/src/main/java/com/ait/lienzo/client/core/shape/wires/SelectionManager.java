/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.event.OnMouseEventHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresCompositeShapeHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;

/**
 * The SelectionManager is quite an intricate class and changes should be made with care.
 * It uses the global event tracker, OnMouseEventHandler, because it needs to track both when a selection creation starts and ends.
 * It starts on the layer mouseDown, at which point it tracks the MouseMove, as there is no Layer drag event. Further the mouseup
 * can occur over any shape, that might consume the event, so it should intercept that globally first to correct handle. It also has special handling
 * to ignore the click event during selection creation.
 * <p>
 * The other intricate part is the differentiation between normally selected connectors and externally connected ones. When a connector, in a selection,
 * is connected to a shape outside of the selection then that connection cannot be moved with the drag. However we want the BoundingBox to correctly indicate this.
 * So the normal shapes and connections draw an initial BoundingBox that is just offset during drag, however any externally connected connection has its BoundingBox
 * redetermined and added to that starting BoundingBox. This must be redetected and handled each time a shape is added or removed from the selection.
 * <p>
 * There is also additional complexity due to nested shapes and connectors to those nested shapes. Currently a selection only ever containsBoundingBox the outer most parent and all children
 * are implicitely in the selection. So we need to remove children when adding a parent, adding of children is ignored if the parent already exists and checking for externally
 * connected connectors must check for all shapes and their children.
 * <p>
 * Finally it should be noted the SelectionManager controls what is in and out of the selection, and drawing the actual visible rectangle. It is up to the SelectionListener
 * implementation to handle showing nad hiding of handles.
 */
public class SelectionManager implements NodeMouseDoubleClickHandler,
                                         NodeMouseClickHandler,
                                         NodeMouseDownHandler {

    public interface SelectionShapeProvider<T extends SelectionShapeProvider> {

        Shape<?> getShape();

        T build();

        T setLocation(Point2D location);

        T setSize(double width,
                  double height);

        boolean isMultipleSelection(MouseEvent event);

        T clear();
    }

    public static int SELECTION_PADDING = 10;

    private final Layer m_layer;

    private final WiresManager m_wiresManager;

    private SelectedItems m_selected;

    private final WiresCompositeControl m_shapeControl;

    private HandlerRegistration m_selectMouseDownHandlerReg;

    private HandlerRegistration m_selectMouseClickHandlerReg;

    private HandlerRegistration m_selectMouseDoubleClickHandlerReg;

    private SelectionShapeProvider m_selectionShapeProvider;

    BoundingBox m_startBoundingBox;

    private Point2D m_start;

    private HandlerRegistration m_dragSelectionStartReg;

    private HandlerRegistration m_dragSelectionMoveReg;

    private HandlerRegistration m_dragSelectionEndReg;

    private HandlerRegistration m_dragSelectionMouseClickReg;

    private SelectionDragHandler m_selectionDragHandler;

    private boolean m_selectionCreationInProcess;

    private boolean m_ignoreMouseClick;

    private SelectionListener m_selectionListener;

    public SelectionManager(final WiresManager wiresManager) {
        m_wiresManager = wiresManager;
        m_layer = wiresManager.getLayer().getLayer();
        m_selectionShapeProvider = new RectangleSelectionProvider();
        m_selectMouseDownHandlerReg = m_layer.addNodeMouseDownHandler(this);
        m_selectMouseClickHandlerReg = m_layer.addNodeMouseClickHandler(this);
        m_selectMouseDoubleClickHandlerReg = m_layer.addNodeMouseDoubleClickHandler(this);
        m_selected = new SelectedItems(this, m_layer);

        OnMouseXEventHandler onMouseXEventHandler = new OnMouseXEventHandler();
        OnEventHandlers onEventHandlers = m_layer.getViewport().getOnEventHandlers();
        onEventHandlers.setOnMouseClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDoubleClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDownEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseUpEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseMoveEventHandle(onMouseXEventHandler);

        m_selectionListener = new DefaultSelectionListener();

        m_shapeControl = wiresManager
                .getControlFactory()
                .newCompositeControl(new WiresCompositeControl.Context() {
                                         @Override
                                         public Collection<WiresShape> getShapes() {
                                             return m_selected.m_shapes;
                                         }

                                         @Override
                                         public Collection<WiresConnector> getConnectors() {
                                             return m_selected.m_connectors;
                                         }
                                     },
                                     wiresManager);
    }

    protected boolean isSelectionCreationInProcess() {
        return m_selectionCreationInProcess;
    }

    protected void setSelectionCreationInProcess(boolean inProgress) {
        this.m_selectionCreationInProcess = inProgress;
    }

    public SelectionManager setSelectionShapeProvider(SelectionShapeProvider m_selectionShapeProvider) {
        this.m_selectionShapeProvider = m_selectionShapeProvider;
        return this;
    }

    public void selected(WiresShape shape, boolean isSelectionMultiple) {
        m_selected.selected(shape, isSelectionMultiple);
    }

    public void selected(WiresConnector connector, boolean isSelectionMultiple) {
        m_selected.selected(connector, isSelectionMultiple);
    }

    public SelectionManager setSelectionListener(SelectionListener selectionListener) {
        m_selectionListener = selectionListener;
        return this;
    }

    public SelectedItems getSelectedItems() {
        return m_selected;
    }

    public void setSelectedItems(SelectedItems items) {
        this.m_selected = items;
    }

    public class OnMouseXEventHandler implements OnMouseEventHandler {

        public void down(MouseEvent event,
                         int x,
                         int y) {
            if (getSelectionShape() != null && !m_selectionShapeProvider.isMultipleSelection(event)) {
                // if the mousedown is any where other than the rectangle, and shift was not held,  clear it.
                // this way, if necessary, a new selection can begin
                Node<?> node = m_layer.getViewport().findShapeAtPoint(x, y);
                if (node != getSelectionShape()) {
                    clearIfSelection();
                }
            }
        }

        @Override
        public boolean onMouseEventBefore(MouseEvent event) {
            boolean isLeftButton = isButtonLeft(event);
            if (!isLeftButton) {
                return true;
            }

            // CLICK
            if (event.type.equals(EventType.CLICKED.getType())) {
                // this is to differentiate on a drag's mouseup event. It must come before the selection shape null
                // as it must always cleanup a m_ignoreMouseClick after a mouse down
                if (m_ignoreMouseClick) {
                    m_ignoreMouseClick = false; // drag has finished, so reset
                    return false;
                } else {
                    return true;
                }
            }

            HTMLElement relativeElement = m_layer.getViewport().getElement();
            final int x = MouseEventUtil.getRelativeX(event.clientX, relativeElement);
            final int y = MouseEventUtil.getRelativeY(event.clientY, relativeElement);

            // DOWN
            if (event.type.equalsIgnoreCase(EventType.MOUSE_DOWN.getType())) {
                down(event, x, y);
                return true;
            }

            // No selection shape and not about to create and create one
            if (getSelectionShape() == null && !m_selectionCreationInProcess) {
                // do nothing
                return true;
            }

            // MOVE
            if (event.type.equalsIgnoreCase(EventType.MOUSE_MOVE.getType())) {
                if (m_selectionCreationInProcess) {
                    drawSelectionShape(event, x, y);
                    return false;
                } else {
                    return true;
                }
            }

            // UP
            if (event.type.equalsIgnoreCase(EventType.MOUSE_UP.getType())) {
                return selectionEventHandlingComplete(event, x, y);
            }
            return true;
        }

        void drawSelectionShape(MouseEvent event,
                                int x,
                                int y) {
            final double relativeStartX = getSelectionManager().relativeStartX();
            final double relativeStartY = getSelectionManager().relativeStartY();
            final Point2D untransformedPoint = getSelectionManager().getUntransformedPoint(new Point2D(x, y));
            final double relativeEventX = untransformedPoint.getX();
            final double relativeEventY = untransformedPoint.getY();
            double width = relativeEventX - relativeStartX;
            double height = relativeEventY - relativeStartY;
            // if either width or height is zero, you won't see the line being drawn, so ensure at least 1px separation
            if (width == 0) {
                width += 1;
            }
            if (height == 0) {
                height += 1;
            }

            final Layer overLayer = m_layer.getViewport().getOverLayer();
            getSelectionManager().drawSelectionShape(relativeStartX, relativeStartY, width, height, overLayer);
            overLayer.draw();
        }

        SelectionManager getSelectionManager() {
            return SelectionManager.this;
        }

        private boolean selectionEventHandlingComplete(MouseEvent event,
                                                       int x,
                                                       int y) {
            if (m_selectionCreationInProcess) {
                m_selected.clear();
                // selection shape is null, for a layer mouse down without any drag
                if (getSelectionShape() != null) {
                    m_ignoreMouseClick = true; // only ignore a mouse click, if there was an actual drag and thus selection shape creation

                    Layer layer = getSelectionShape().getLayer(); // this is in the drag layer, so also redraw there
                    if (x != m_start.getX() && y != m_start.getY()) {
                        getItemsInBoundingBox(getSelectionShape().getComputedBoundingPoints().getBoundingBox());
                        // can be null if there was no mousemove
                        if (!m_selected.isEmpty()) {
                            m_selectionShapeProvider.clear();
                            drawSelectionShapeForSelection();

                            m_layer.draw();
                        } else {
                            // destroy the selection if it's empty
                            destroySelectionShape();
                        }
                    } else {
                        // destroy the selection if it's empty
                        destroySelectionShape();
                    }
                    layer.draw();
                }
                getSelectedItems().selectShapes();

                m_selectionCreationInProcess = false;

                return false;
            }
            getSelectedItems().notifyListener();

            return true;
        }

        @Override
        public void onMouseEventAfter(MouseEvent event) {

        }
    }

    public void rebuildSelectionArea() {
        if (m_selected.isEmpty()) {
            return;
        }
        m_selected.rebuildBoundingBox();
        drawSelectionShapeForSelection();
    }

    public void drawSelectionShapeForSelection() {
        if (m_selected.isEmpty()) {
            return;
        }

        if (!m_selected.m_shapes.isEmpty() || m_selected.m_connectors.size() != m_selected.m_externallyConnected.size()) {
            BoundingBox bbox = m_selected.getBoundingBox();
            m_startBoundingBox = BoundingBox.fromBoundingBox(bbox);
            drawSelectionShape(bbox.getX(),
                               bbox.getY(),
                               bbox.getWidth(),
                               bbox.getHeight(),
                               m_layer);
        } else {
            // There are no shapes and no non-externally connected connectors, so set startBoundingBox to null
            // use an initial arbitrary rectangle x,y,w,h the updateRectangleForExternallyConnectedConnectors will correct this, as it'll ignore the existing x,y,w,h
            drawSelectionShape(0, 0, 20, 20, m_layer);
        }

        if (!m_selected.m_externallyConnected.isEmpty()) {
            m_selectionDragHandler.updateSelectionShapeForExternallyConnectedConnectors(0, 0, m_startBoundingBox);
        }
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event) {
        if (event.isButtonLeft()) {
            clearIfSelection();
        }
    }

    @Override
    public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
        if (event.isButtonLeft()) {
            clearIfSelection();
        }
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event) {
        if (!event.isButtonLeft()) {
            return;
        }

        Node<?> node = m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
        if (node == null) {
            // only start the select if there is no shape at the current mouse xy/
            // as events bubble up to root, if there are no handlers for this specific event type, so need to detect that.
            m_start = new Point2D(event.getX(),
                                  event.getY());
            m_selectionCreationInProcess = true;
            destroySelectionShape();
            m_layer.draw();
        }
    }

    public void clearSelection() {
        clearIfSelection();
    }

    private void clearIfSelection() {
        if (!isSelectionHandlerRunning()) {
            destroySelectionShape();
            m_selected.clear();
            m_selected.notifyListener();
        }
    }

    public void drawSelectionShape(double x,
                                   double y,
                                   double width,
                                   double height,
                                   Layer layer) {
        final double padding = SELECTION_PADDING;
        double sx = x - padding;
        double sy = y - padding;
        double sw = width;
        double sh = height;
        if (sw < 0) {
            sw = Math.abs(sw);
            sx = relativeStartX() - sw;
        }
        if (sh < 0) {
            sh = Math.abs(sh);
            sy = relativeStartY() - sh;
        }
        sw = sw + (padding * 2);
        sh = sh + (padding * 2);
        final Point2D location = new Point2D(sx, sy);
        if (getSelectionShape() == null) {
            // Build the instance.
            m_selectionShapeProvider.build();

            // Register the handlers.
            if (layer == m_layer) {
                // don't do this if it was added to the overlay layer
                getSelectionShape()
                        .setDraggable(true)
                        .setFillBoundsForSelection(true);
                m_selectionDragHandler = new SelectionDragHandler(SelectionManager.this);
                m_dragSelectionMouseClickReg = getSelectionShape().addNodeMouseClickHandler(m_selectionDragHandler);
                getSelectionShape().setDragConstraints(m_selectionDragHandler);
                m_dragSelectionEndReg = getSelectionShape().addNodeDragEndHandler(m_selectionDragHandler);
            }
            layer.add(getSelectionShape());
        }
        // Update location and size.
        m_selectionShapeProvider
                .setLocation(location)
                .setSize(sw, sh);
    }

    double relativeStartX() {
        return getUntransformedStartPoint().getX();
    }

    double relativeStartY() {
        return getUntransformedStartPoint().getY();
    }

    public Point2D getUntransformedStartPoint() {
        return getUntransformedPoint(getStart());
    }

    public Point2D getUntransformedPoint(final Point2D point2D) {
        final Transform transform = getViewportTransform();
        final Point2D untransformed = new Point2D(0, 0);

        if (transform != null) {
            transform.getInverse().transform(point2D, untransformed);
            return untransformed;
        }

        return point2D;
    }

    Point2D getStart() {
        return m_start;
    }

    public Transform getViewportTransform() {
        return m_layer.getViewport().getTransform();
    }

    public static class ChangedItems {

        private final NFastArrayList<WiresShape> m_removedShapes = new NFastArrayList<>();
        private final NFastArrayList<WiresShape> m_addedShapes = new NFastArrayList<>();

        private final NFastArrayList<WiresConnector> m_removedConnectors = new NFastArrayList<>();
        private final NFastArrayList<WiresConnector> m_addedConnectors = new NFastArrayList<>();

        public NFastArrayList<WiresShape> getRemovedShapes() {
            return m_removedShapes;
        }

        public NFastArrayList<WiresShape> getAddedShapes() {
            return m_addedShapes;
        }

        public NFastArrayList<WiresConnector> getRemovedConnectors() {
            return m_removedConnectors;
        }

        public NFastArrayList<WiresConnector> getAddedConnectors() {
            return m_addedConnectors;
        }

        public int removedSize() {
            return m_removedConnectors.size() + m_removedShapes.size();
        }

        public int addedSize() {
            return m_addedConnectors.size() + m_addedShapes.size();
        }

        public void clear() {
            m_removedShapes.clear();
            m_addedShapes.clear();
            m_removedConnectors.clear();
            m_addedConnectors.clear();
        }
    }

    public static class SelectedItems {

        private final Set<WiresShape> m_shapes;
        private final Set<WiresConnector> m_connectors;
        // external coupled connectors are connectors who are connected to a shape, not part of the selection
        // these will not be part of selection rectangle BoundingBox, but would be part of any copy/paste
        private final Set<WiresConnector> m_externallyConnected;
        private boolean selectionGroup;
        private final Layer m_layer;
        private final SelectionManager m_selManager;
        private BoundingBox m_bbox;

        private ChangedItems m_changed = new ChangedItems();

        public SelectedItems(SelectionManager selManager, Layer layer) {
            m_selManager = selManager;
            m_layer = layer;
            m_shapes = new HashSet<>();
            m_connectors = new HashSet<>();
            m_externallyConnected = new HashSet<>();
            m_bbox = new BoundingBox();
        }

        public ChangedItems getChanged() {
            return m_changed;
        }

        public void selected(WiresShape shape, boolean isSelectionMultiple) {
            if (!isSelectionMultiple) {
                // clear all shapes and re-addBoundingBox current to just select the current
                // if only current is selected, it will just remove and re-addBoundingBox it, user should not notice any change.
                m_selManager.destroySelectionShape();
                clear();

                add(shape);
            } else {
                if (isShapeSelected(shape)) {
                    remove(shape);
                } else if (hasSameParentsAsSelection(shape) && shape.getDockedTo() == null) // cannot docked shapes
                {
                    removeChildShape(shape.getChildShapes());
                    add(shape);
                }
            }
            notifyListener(isSelectionMultiple);
        }

        public boolean isShapeSelected(final WiresShape shape) {
            return m_shapes.contains(shape);
        }

        public boolean isConnectorSelected(final WiresConnector connector) {
            return m_connectors.contains(connector);
        }

        private boolean hasSameParentsAsSelection(WiresShape subjectShape) {
            for (WiresShape existingShape : m_shapes) {
                if (existingShape.getParent() != subjectShape.getParent()) {
                    return false;
                }
            }
            return true;
        }

        private WiresShape findParentIfInSelection(WiresShape subjectShape) {
            for (WiresShape existingShape : m_shapes) {
                if (existingShape == subjectShape || hasChild(existingShape.getChildShapes(), subjectShape)) {
                    return existingShape;
                }
            }
            return null;
        }

        private boolean hasChild(NFastArrayList<WiresShape> shapes, WiresShape subjectShape) {
            for (int i = 0, size = shapes.size(); i < size; i++) {
                WiresShape childShape = shapes.get(i);
                if (childShape == subjectShape) {
                    return true;
                }
                hasChild(childShape.getChildShapes(), subjectShape);
            }
            return false;
        }

        private void removeChildShape(NFastArrayList<WiresShape> shapes) {
            for (WiresShape childShape : shapes.asList()) {
                for (WiresShape existingShape : m_shapes) {
                    if (childShape == existingShape) {
                        // must remove any existing child shape of the parent currently being added
                        remove(existingShape);
                    }
                    removeChildShape(childShape.getChildShapes());
                }
            }
        }

        public void selected(WiresConnector connector, boolean isSelectionMultiple) {
            if (!isSelectionMultiple) {
                // clear all shapes and re-addBoundingBox current to just select the current
                // if only current is selected, it will just remove and re-addBoundingBox it, user should not notice any change.
                m_selManager.destroySelectionShape();
                clear();

                add(connector);
                if (m_selManager.isExternallyConnected(connector)) {
                    addExternallyConnected(connector);
                }
            } else {
                if (isConnectorSelected(connector)) {
                    remove(connector);
                } else {
                    add(connector);
                    if (m_selManager.isExternallyConnected(connector)) {
                        addExternallyConnected(connector);
                    }
                }
            }
            notifyListener(isSelectionMultiple);
        }

        public void rebuildBoundingBox() {
            m_selManager.m_startBoundingBox = null;
            m_bbox = new BoundingBox();

            for (WiresShape shape : m_shapes) {
                BoundingBox nodeBox = shape.getContainer().getComputedBoundingPoints().getBoundingBox();
                m_bbox.addBoundingBox(nodeBox);
            }

            for (WiresConnector connector : m_connectors) {
                if (!m_externallyConnected.contains(connector)) {
                    BoundingBox nodeBox = connector.getGroup().getComputedBoundingPoints().getBoundingBox();
                    m_bbox.addBoundingBox(nodeBox);
                }
            }
        }

        public boolean add(WiresShape shape) {
            // For any Shape being added that also has an added a WiresConnector we must check if the WiresConnector is
            // still connected externally or not.
            // At this stage we know it must be in m_externallyConnected, if the other connection is in the selection,
            // then it should be removed from m_externallyConnected.
            if (shape.getMagnets() != null) {
                for (int i = 0; i < shape.getMagnets().size(); i++) {
                    WiresMagnet magnet = shape.getMagnets().getMagnet(i);
                    if (magnet.getConnections() != null) {
                        for (WiresConnection connection : magnet.getConnections().asList()) {
                            WiresConnector wiresConnector = connection.getConnector();
                            if (isConnectorSelected(wiresConnector) && getExternallyConnected().contains(wiresConnector)) {
                                WiresConnection otherConnection = connection.getOppositeConnection();
                                if (otherConnection != null && otherConnection.getMagnet() != null) {
                                    WiresShape otherShape = otherConnection.getMagnet().getMagnets().getWiresShape();
                                    if (findParentIfInSelection(otherShape) != null) {
                                        getExternallyConnected().remove(wiresConnector);
                                    }
                                } else if (otherConnection != null) {
                                    getExternallyConnected().remove(wiresConnector);
                                }
                            }
                        }
                    }
                }
            }

            m_changed.getAddedShapes().add(shape);
            return m_shapes.add(shape);
        }

        public boolean remove(WiresShape shape) {
            updateExternallyConnectedOnShapeRemove(shape);

            m_changed.getRemovedShapes().add(shape);
            return m_shapes.remove(shape);
        }

        /**
         * If a shape is removed, any connectors need to be checked in case they are not externally connected
         * this must be done for the current shape and all children shapes, as the child could have a connector in the selection
         *
         * @param shape
         */
        private void updateExternallyConnectedOnShapeRemove(WiresShape shape) {
            if (shape.getMagnets() != null) {
                for (int i = 0; i < shape.getMagnets().size(); i++) {
                    WiresMagnet magnet = shape.getMagnets().getMagnet(i);
                    if (magnet.getConnections() != null) {
                        for (WiresConnection connection : magnet.getConnections().asList()) {
                            WiresConnector wiresConnector = connection.getConnector();
                            if (isConnectorSelected(wiresConnector) && !m_externallyConnected.contains(wiresConnector)) {
                                m_externallyConnected.add(wiresConnector);
                            }
                        }
                    }
                }
            }
            for (WiresShape child : shape.getChildShapes().asList()) {
                updateExternallyConnectedOnShapeRemove(child);
            }
        }

        public boolean add(WiresConnector connector) {
            m_changed.getAddedConnectors().add(connector);
            return m_connectors.add(connector);
        }

        public boolean addExternallyConnected(WiresConnector connector) {
            return m_externallyConnected.add(connector);
        }

        public boolean remove(WiresConnector connector) {
            m_changed.getRemovedConnectors().add(connector);
            m_externallyConnected.remove(connector); // it may or may not be in here, but attempt remove in case it is
            return m_connectors.remove(connector);
        }

        public Set<WiresShape> getShapes() {
            return m_shapes;
        }

        public Set<WiresConnector> getConnectors() {
            return m_connectors;
        }

        public Set<WiresConnector> getExternallyConnected() {
            return m_externallyConnected;
        }

        public boolean isExternallyConnector(WiresConnector connector) {
            return m_externallyConnected.contains(connector);
        }

        public boolean isSelectionGroup() {
            return selectionGroup;
        }

        public void setSelectionGroup(boolean selectionGroup) {
            this.selectionGroup = selectionGroup;
        }

        public int size() {
            return m_shapes.size() + m_connectors.size();
        }

        public boolean isEmpty() {
            return m_shapes.isEmpty() && m_connectors.isEmpty();
        }

        public void clear() {
            // selection controls can only exist, if there is single entry
            m_changed.clear(); // clear first
            recordPrevious();

            selectionGroup = false;

            m_shapes.clear();
            m_connectors.clear();
            m_externallyConnected.clear();
            m_bbox = new BoundingBox();
            m_selManager.m_startBoundingBox = null;
        }

        public void destroy() {
            selectionGroup = false;
            m_changed.clear();
            m_shapes.clear();
            m_connectors.clear();
            m_externallyConnected.clear();
            m_bbox = null;
            m_selManager.m_startBoundingBox = null;
        }

        public void recordPrevious() {
            for (WiresShape shape : m_shapes) {
                m_changed.getRemovedShapes().add(shape);
            }

            for (WiresConnector connector : m_connectors) {
                m_changed.getRemovedConnectors().add(connector);
            }
        }

        public BoundingBox getBoundingBox() {
            return m_bbox;
        }

        public void notifyListener(boolean isSelectionMultiple) {
            if (isSelectionMultiple) {
                if (!isEmpty()) {
                    selectionGroup = true;
                    m_selManager.rebuildSelectionArea();
                } else {
                    selectionGroup = false;
                }
            } else {
                selectionGroup = false;
            }

            notifyListener();
        }

        public void notifyListener() {
            selectShapes();
            m_changed.clear();
            if (isEmpty()) {
                selectionGroup = false;
                // nothing left, so properly clean things up.
                m_selManager.destroySelectionShape();
                clear();
            }
            m_layer.draw();
        }

        public void selectShapes() {
            m_selManager.m_selectionListener.onChanged(this);
        }
    }

    public void getItemsInBoundingBox(BoundingBox selectionBox) {
        m_selected.setSelectionGroup(true);
        BoundingBox box = m_selected.getBoundingBox();

        BoundingBox nodeBox;
        List<WiresShape> toBeRemoved = new ArrayList<>();

        Map<String, WiresShape> shapesMap = new HashMap<>();
        Map<String, BoundingBox> uuidMap = new HashMap<>();

        // first build a map of all shapes that intersect with teh selection rectangle. Nested shapes will be used later.
        final WiresShape[] shapes = m_wiresManager.getShapes();
        for (WiresShape shape : shapes) {
            if (shape.getDockedTo() != null) {
                // docked items cannot be added to a selection, only their parent they are docked to
                continue;
            }
            nodeBox = shape.getContainer().getComputedBoundingPoints().getBoundingBox();
            if (selectionBox.intersects(nodeBox)) {
                shapesMap.put(shape.getContainer().uuid(), shape);
                uuidMap.put(shape.getContainer().uuid(), nodeBox);
            }
        }

        // addBoundingBox to removal list any shape whose parent is also in the selection
        for (WiresShape shape : shapesMap.values()) {
            if (null != shape.getParent() && shapesMap.containsKey(shape.getParent().getContainer().uuid())) {
                toBeRemoved.add(shape); // can't remove yet, as it may have selected children itself, which will also need to be removed
            }
        }

        // now the list is built, safely remove the shapes
        for (WiresShape shape : toBeRemoved) {
            shapesMap.remove(shape.getContainer().uuid());
        }

        for (WiresShape shape : shapesMap.values()) {
            nodeBox = uuidMap.get(shape.getContainer().uuid());
            m_selected.add(shape);
            box.addBoundingBox(nodeBox);
        }

        for (WiresConnector connector : m_wiresManager.getConnectorList().asList()) {
            boolean externallyConnected = isExternallyConnected(connector);

            Point2DArray points = new Point2DArray();
            Point2D loc = getSelectionShape().getLocation();
            BoundingBox boundingBox = getSelectionShape().getBoundingBox();
            points.pushXY(loc.getX(), loc.getY());
            points.pushXY(loc.getX() + boundingBox.getWidth(), loc.getY());
            points.pushXY(loc.getX() + boundingBox.getWidth(), loc.getY() + boundingBox.getHeight());
            points.pushXY(loc.getX(), loc.getY() + boundingBox.getHeight());

            nodeBox = connector.getGroup().getComputedBoundingPoints().getBoundingBox();
            if (selectionBox.containsBoundingBox(nodeBox)) {
                addConnector(connector, externallyConnected, box, nodeBox);
            } else {
                Point2DArray intersections = Geometry.getIntersectPolyLinePath(points, connector.getLine().asShape().getPathPartList(), true);
                if (intersections != null && intersections.size() > 0) {
                    addConnector(connector, externallyConnected, box, nodeBox);
                } else {
                    // the above checked the line, also check the head and tail.

                    // head is rotated around an offset with also set. The reverse of this must be applied to the
                    // selection rectangle, to ensure thinsg are all in the same space,  for intersection to work
                    MultiPath path = connector.getHead();
                    Transform xfrm = new Transform();
                    xfrm.translate(path.getOffset().getX(), path.getOffset().getY());
                    xfrm.rotate(0 - path.getRotation());
                    xfrm.translate(0 - path.getX(), 0 - path.getY());
                    xfrm.translate(0 - path.getOffset().getX(), 0 - path.getOffset().getY());

                    Point2DArray transformedPoints = points.copy();
                    for (Point2D p : transformedPoints.asArray()) {
                        xfrm.transform(p, p);
                    }

                    intersections = Geometry.getIntersectPolyLinePath(transformedPoints, connector.getHead().getActualPathPartListArray().get(0), true);
                    if (intersections != null && intersections.size() > 0) {
                        addConnector(connector, externallyConnected, box, nodeBox);
                    } else {
                        // tail is rotated around an offset with also set. The reverse of this must be applied to the
                        // selection rectangle, to ensure thinsg are all in the same space,  for intersection to work
                        path = connector.getTail();
                        xfrm = new Transform();
                        xfrm.translate(path.getOffset().getX(), path.getOffset().getY());
                        xfrm.rotate(0 - path.getRotation());
                        xfrm.translate(0 - path.getX(), 0 - path.getY());
                        xfrm.translate(0 - path.getOffset().getX(), 0 - path.getOffset().getY());

                        transformedPoints = points.copy();
                        for (Point2D p : transformedPoints.asArray()) {
                            xfrm.transform(p, p);
                        }

                        intersections = Geometry.getIntersectPolyLinePath(transformedPoints, connector.getTail().getActualPathPartListArray().get(0), true);
                        if (intersections != null && intersections.size() > 0) {
                            addConnector(connector, externallyConnected, box, nodeBox);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns whether the connector is connected to a shape not in the selection.
     * As this could be connected to a nested shape, it iterates from that shape (not in the selection) until it finds it's parent in the selection or it returns null.
     *
     * @param connector
     * @return
     */
    private boolean isExternallyConnected(WiresConnector connector) {
        WiresShape headShape = null;
        WiresShape tailShape = null;
        if (connector.getHeadConnection().getMagnet() != null) {
            headShape = connector.getHeadConnection().getMagnet().getMagnets().getWiresShape();
        }
        if (connector.getTailConnection().getMagnet() != null) {
            tailShape = connector.getTailConnection().getMagnet().getMagnets().getWiresShape();
        }
        boolean hasHeadShapeNotInSelection = headShape != null && m_selected.findParentIfInSelection(headShape) == null;
        boolean hasTailShapeNotInSelection = tailShape != null && m_selected.findParentIfInSelection(tailShape) == null;

        return hasHeadShapeNotInSelection || hasTailShapeNotInSelection;
    }

    private void addConnector(WiresConnector connector, boolean externallyCoupled, BoundingBox box, BoundingBox nodeBox) {
        m_selected.add(connector);

        if (!externallyCoupled) {
            box.addBoundingBox(nodeBox);
        } else {
            m_selected.addExternallyConnected(connector);
        }
    }

    private void destroySelectionShape() {
        if (getSelectionShape() != null) {
            if (m_dragSelectionStartReg != null) {
                // this is not added for the selection creation rectangle.
                m_dragSelectionStartReg.removeHandler();
                m_dragSelectionMoveReg.removeHandler();
                m_dragSelectionEndReg.removeHandler();
                m_dragSelectionMouseClickReg.removeHandler();
                m_dragSelectionStartReg = null;
                m_dragSelectionMoveReg = null;
                m_dragSelectionEndReg = null;
                m_dragSelectionMouseClickReg = null;
            }
            m_selectionShapeProvider.clear();
            m_selectionDragHandler = null;
        }
    }

    private boolean isSelectionHandlerRunning() {
        return null != m_selectionDragHandler &&
                m_selectionDragHandler.isRunning();
    }

    public void destroy() {
        if (m_selectMouseDownHandlerReg != null) {
            m_selectMouseDownHandlerReg.removeHandler();
            m_selectMouseClickHandlerReg.removeHandler();
            m_selectMouseDoubleClickHandlerReg.removeHandler();

            m_selectMouseDownHandlerReg = null;
            m_selectMouseClickHandlerReg = null;
            m_selectMouseDoubleClickHandlerReg = null;
        }

        m_layer.getViewport().getOnEventHandlers().destroy();

        m_selected.destroy();
        destroySelectionShape();
        m_selectionShapeProvider = null;
        m_selectionDragHandler = null;
        m_selectionListener = null;
        m_startBoundingBox = null;
        m_start = null;
    }

    private double[] calculateSelectionShapeForExternallyConnectedConnectors(int dx, int dy, BoundingBox originalBox) {

        BoundingBox box = new BoundingBox();
        if (null != originalBox) {
            box.addBoundingBox(originalBox);
        }
        box.offset(dx, dy);

        if (m_selected.m_externallyConnected.isEmpty()) {
            return new double[]{box.getMinX() - SELECTION_PADDING, box.getMinY() - SELECTION_PADDING};
        }

        double width = null != originalBox ? originalBox.getWidth() : 0d;
        double height = null != originalBox ? originalBox.getHeight() : 0d;
        if (!m_selected.m_externallyConnected.isEmpty()) {
            for (WiresConnector connector : m_selected.m_externallyConnected) {
                box.addBoundingBox(connector.getHead().getComputedBoundingPoints().getBoundingBox());
                box.addBoundingBox(connector.getTail().getComputedBoundingPoints().getBoundingBox());
                box.addBoundingBox(connector.getLine().getComputedBoundingPoints().getBoundingBox());
            }
            width = box.getMaxX() - box.getMinX();
            height = box.getMaxY() - box.getMinY();
        }

        return new double[]{box.getMinX(), box.getMinY(), width, height};
    }

    public WiresCompositeControl getControl() {
        return m_shapeControl;
    }

    public static class SelectionDragHandler implements NodeDragEndHandler,
                                                        DragConstraintEnforcer,
                                                        NodeMouseClickHandler {

        SelectionManager m_selectionManager;
        WiresCompositeShapeHandler multipleShapeHandler;
        private int adjustX = 0;
        private int adjustY = 0;
        private boolean running;

        public SelectionDragHandler(SelectionManager selectionManager) {
            this.m_selectionManager = selectionManager;
            Supplier<WiresLayerIndex> indexBuilder = () -> getControlFactory().newIndex(getWiresManager());
            this.multipleShapeHandler = new WiresCompositeShapeHandler(indexBuilder,
                                                                       m_selectionManager.m_shapeControl,
                                                                       getControlFactory().newShapeHighlight(getWiresManager()),
                                                                       getWiresManager());
            running = false;
        }

        @Override
        public void onNodeMouseClick(NodeMouseClickEvent event) {
            // temporarily remove the rect, use findAt to find the underlying shape and trigger the click event
            if (event.isButtonLeft()) {
                m_selectionManager.getSelectionShape().removeFromParent();
                m_selectionManager.m_layer.draw();
                Node<?> node = m_selectionManager.m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
                m_selectionManager.m_layer.add(m_selectionManager.getSelectionShape());
                m_selectionManager.m_layer.draw();
                while (node != null) {
                    if (node.isEventHandled(event.getAssociatedType())) {
                        node.fireEvent(event);
                        break;
                    }
                    node = node.getParent();
                }
            }
        }

        @Override
        public void startDrag(DragContext dragContext) {
            running = true;
            adjustX = 0;
            adjustY = 0;
            multipleShapeHandler.startDrag(dragContext);
        }

        @Override
        public boolean adjust(Point2D dxy) {
            running = true;
            boolean adjusted = multipleShapeHandler.adjust(dxy);
            if (!adjusted) {
                adjustX = (int) dxy.getX();
                adjustY = (int) dxy.getY();
                updateSelectionShapeForExternallyConnectedConnectors(adjustX, adjustY, m_selectionManager.m_startBoundingBox);
            }
            dxy.setX(0d);
            dxy.setY(0d);
            return adjusted;
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            updateSelectionShapeForExternallyConnectedConnectors(adjustX, adjustY, m_selectionManager.m_startBoundingBox);
            if (m_selectionManager.m_startBoundingBox != null) {
                m_selectionManager.m_startBoundingBox.offset(adjustX, adjustY);
            }
            m_selectionManager.m_ignoreMouseClick = true; // need to ignore the click event after
            multipleShapeHandler.onNodeDragEnd(event);
            m_selectionManager.rebuildSelectionArea();
            running = false;

            reinforceSelectionShapeOnTop();
        }

        void reinforceSelectionShapeOnTop() {
            Shape<?> shape = m_selectionManager.getSelectionShape();
            if (null != shape) {
                shape.getLayer().moveToTop(shape);
            }
        }

        void updateSelectionShapeForExternallyConnectedConnectors(int dx,
                                                                  int dy,
                                                                  BoundingBox originalBox) {
            final double[] attrs = m_selectionManager
                    .calculateSelectionShapeForExternallyConnectedConnectors(dx, dy, originalBox);
            m_selectionManager.m_selectionShapeProvider
                    .setLocation(new Point2D(attrs[0], attrs[1]));
            if (attrs.length == 4) {
                m_selectionManager.m_selectionShapeProvider
                        .setSize(attrs[2], attrs[3]);
            }
        }

        private WiresManager getWiresManager() {
            return m_selectionManager.m_wiresManager;
        }

        private WiresControlFactory getControlFactory() {
            return getWiresManager().getControlFactory();
        }

        private boolean isRunning() {
            return running;
        }
    }

    public Shape<?> getSelectionShape() {
        return null != m_selectionShapeProvider ? m_selectionShapeProvider.getShape() : null;
    }

    public static class RectangleSelectionProvider implements SelectionShapeProvider<RectangleSelectionProvider> {

        private Rectangle shape;

        @Override
        public RectangleSelectionProvider build() {
            shape = new Rectangle(1, 1)
                    .setStrokeWidth(1)
                    .setDashArray(5, 5)
                    .setStrokeColor("#0000CC");
            return this;
        }

        @Override
        public RectangleSelectionProvider setLocation(final Point2D location) {
            shape.setLocation(location);
            return this;
        }

        @Override
        public RectangleSelectionProvider setSize(double width,
                                                  double height) {
            shape.setWidth(width)
                    .setHeight(height);
            return this;
        }

        @Override
        public boolean isMultipleSelection(MouseEvent event) {
            return event.shiftKey;
        }

        @Override
        public RectangleSelectionProvider clear() {
            if (null != shape) {
                shape.removeFromParent();
                shape = null;
            }
            return this;
        }

        @Override
        public Shape<?> getShape() {
            return shape;
        }
    }

    private static boolean isButtonLeft(MouseEvent event) {
        return event.button == MouseEventUtil.BUTTON_LEFT;
    }
}
