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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
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
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArray;
import com.ait.tooling.nativetools.client.util.Console;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * The SelectionManager is quite an intricate class and changes should be made with care.
 * It uses the global event tracker, OnMouseEventHandler, because it needs to track both when a selection creation starts and ends.
 * It starts on the layer mouseDown, at which point it tracks the MouseMove, as there is no Layer drag event. Further the mouseup
 * can occur over any shape, that might consume the event, so it should intercept that globally first to correct handle. It also has special handling
 * to ignore the click event during selection creation.
 *
 * The other intricate part is the differentiation between normally selected connectors and externally connected ones. When a connector, in a selection,
 * is connected to a shape outside of the selection then that connection cannot be moved with the drag. However we want the BoundingBox to correctly indicate this.
 * So the normal shapes and connections draw an initial BoundingBox that is just offset during drag, however any externally connected connection has its BoundingBox
 * redetermined and added to that starting BoundingBox. This must be redetected and handled each time a shape is added or removed from the selection.
 *
 * There is also additional complexity due to nested shapes and connectors to those nested shapes. Currently a selection only ever contains the outer most parent and all children
 * are implicitely in the selection. So we need to remove children when adding a parent, adding of children is ignored if the parent already exists and checking for externally
 * connected connectors must check for all shapes and their children.
 *
 * Finally it should be noted the SelectionManager controls what is in and out of the selection, and drawing the actual visible rectangle. It is up to the SelectionListener
 * implementation to handle showing nad hiding of handles.
 */
public class SelectionManager implements NodeMouseDoubleClickHandler, NodeMouseClickHandler, NodeMouseDownHandler
{
    public static int           SELECTION_PADDING = 10;


    private HandlerRegistration        m_selectMouseDownHandlerReg;

    private HandlerRegistration        m_selectMouseClickHandlerReg;

    private HandlerRegistration        m_selectMouseDoubleClickHandlerReg;

    private final Layer                m_layer;

    private final WiresManager         m_wiresManager;

    private final SelectedItems        m_selected;

    private       Rectangle            m_rect;

    private       BoundingBox          m_startBoundingBox;

    private       double               m_startX;

    private       double               m_startY;

    private       HandlerRegistration  m_dragSelectionStartReg;

    private       HandlerRegistration  m_dragSelectionMoveReg;

    private       HandlerRegistration  m_dragSelectionEndReg;

    private       HandlerRegistration  m_dragSelectionMouseClickReg;

    private       SelectionDragHandler m_selectionDragHandler;

    private       boolean              m_selectionCreationInProcess;

    private       boolean              m_ignoreMouseClick;

    private       SelectionListener    m_selectionListener;

    public SelectionManager(WiresManager wiresManager)
    {
        m_wiresManager = wiresManager;
        m_layer = wiresManager.getLayer().getLayer();
        m_selectMouseDownHandlerReg = m_layer.addNodeMouseDownHandler(this);
        m_selectMouseClickHandlerReg = m_layer.addNodeMouseClickHandler(this);
        m_selectMouseDoubleClickHandlerReg = m_layer.addNodeMouseDoubleClickHandler(this);
        m_selected = new SelectedItems(this, m_layer);

        OnMouseXEventHandler onMouseXEventHandler = new OnMouseXEventHandler();
        OnEventHandlers      onEventHandlers      = m_layer.getViewport().getOnEventHandlers();
        onEventHandlers.setOnMouseClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDoubleClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDownEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseUpEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseMoveEventHandle(onMouseXEventHandler);

        m_selectionListener = new DefaultSelectionListener(m_layer, m_selected);
    }

    public void selected(WiresShape shape, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
    {
        m_selected.selected(shape, event);
    }

    public void selected(WiresConnector connector, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
    {
        m_selected.selected(connector, event);
    }

    public void setSelectionListener(SelectionListener selectionListener)
    {
        m_selectionListener = selectionListener;
    }

    public class OnMouseXEventHandler implements OnMouseEventHandler
    {
        public void down(MouseEvent<? extends EventHandler> event)
        {
            if ( m_rect != null  && !event.isShiftKeyDown())
            {
                // if the mousedown is any where other than the rectangle, and shift was not held,  clear it.
                // this way, if necessary, a new selection can begin
                Node<?> node = m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
                if (node != m_rect)
                {
                    clearIfSelection();
                }
            }
        }

        @Override
        public boolean onMouseEventBefore(MouseEvent<? extends EventHandler> event)
        {
            // CLICK
            if (event.getAssociatedType() == ClickEvent.getType())
            {
                // this is to differentiate on a drag's mouseup event. It must come before the m_rect null
                // as it must always cleanup a m_ignoreMouseClick after a mouse down
                if (m_ignoreMouseClick)
                {
                    m_ignoreMouseClick = false; // drag has finished, so reset
                    return false;
                }
                else
                {
                    return true;
                }
            }

            // DOWN
            if (event.getAssociatedType() == MouseDownEvent.getType())
            {
                down(event);
                return true;
            }

            // No rectangle and not about to create and create one
            if (m_rect == null && !m_selectionCreationInProcess)
            {
                // do nothing
                return true;
            }

            // MOVE
            if (event.getAssociatedType() == MouseMoveEvent.getType())
            {
                if (m_selectionCreationInProcess)
                {
                    double width = event.getX() - m_startX;
                    double height = event.getY() - m_startY;
                    // if either width or height is zero, you won't see the line being drawn, so ensure atleast 1px separation
                    if ( width == 0 )
                    {
                        width += 1;
                    }
                    if ( height == 0 )
                    {
                        height += 1;
                    }
                    drawRectangle(m_startX, m_startY, width, height, m_layer.getViewport().getOverLayer(), 0);
                    m_layer.getViewport().getOverLayer().draw();
                    return false;
                }
                else
                {
                    return true;
                }
            }

            // UP
            if (event.getAssociatedType() == MouseUpEvent.getType())
            {
                if(m_selectionCreationInProcess)
                {
                    m_selected.clear();
                    // rectangle is null, for a layer mouse down without any drag
                    if ( m_rect != null)
                    {
                        m_ignoreMouseClick = true; // only ignore a mouse click, if there was an actual drag and thus m_rect creation

                        int   x     = event.getRelativeX(event.getRelativeElement());
                        int   y     = event.getRelativeY(event.getRelativeElement());
                        Layer layer = m_rect.getLayer(); // this is in the drag layer, so also redraw there
                        if (x != m_startX && y != m_startY)
                        {
                            getItemsInBoundingBox(m_rect.getComputedBoundingPoints().getBoundingBox());
                            // can be null if there was no mousemove
                            if (!m_selected.isEmpty())
                            {
                                m_rect.removeFromParent();
                                m_rect = null; // null as it'll be created again on the main layer
                                drawSelectionAsRectangle(SELECTION_PADDING);

                                m_layer.draw();
                            }
                            else
                            {
                                // destroy the selection if it's empty
                                destroySelectionRectangle();
                            }
                        }
                        else
                        {
                            // destroy the selection if it's empty
                            destroySelectionRectangle();
                        }
                        layer.draw();
                    }
                    m_selected.notifyListener();

                    m_selectionCreationInProcess = false;
                    return false;
                }
                else
                {
                    return true;
                }
            }


            throw new IllegalStateException("Code should not  be able reach here");
        }

        @Override public void onMouseEventAfter(MouseEvent<? extends EventHandler> event)
        {

        }
    }

    private void drawSelectionAsRectangle(double padding)
    {
        if (m_selected.isEmpty())
        {
            return;
        }

        if(!m_selected.m_shapes.isEmpty() || m_selected.m_connectors.size() != m_selected.m_externallyConnected.size())
        {
            BoundingBox bbox = m_selected.getBoundingBox();
            m_startBoundingBox = new BoundingBox(bbox);
            drawRectangle(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight(), m_layer, padding );
        }
        else
        {
            // There are no shapes and no non-externally connected connectors, so set startBoundingBox to null
            // use an initial arbitrary rectangle x,y,w,h the updateRectangleForExternallyConnectedConnectors will correct this, as it'll ignore the existing x,y,w,h
            drawRectangle(0, 0, 100, 100, m_layer, padding );
        }

        m_selectionDragHandler.updateRectangleForExternallyConnectedConnectors(0,0, m_startBoundingBox);
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event)
    {
        clearIfSelection();
    }

    @Override
    public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event)
    {
        clearIfSelection();
    }

    @Override public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        Node<?> node = m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
        if (node == null)
        {
            // only start the select if there is no shape at the current mouse xy/
            // as events bubble up to root, if there are no handlers for this specific event type, so need to detect that.
            m_startX = event.getX();
            m_startY = event.getY();
            m_selectionCreationInProcess = true;
            destroySelectionRectangle();
            m_layer.draw();
        }

    }

    private void clearIfSelection()
    {
        m_selected.clear();
        m_selected.notifyListener();
        destroySelectionRectangle();
        m_layer.draw();
    }

    public void drawRectangle(double x, double y, double width, double height, Layer layer, double padding)
    {

        if (m_rect == null)
        {
            m_rect = new Rectangle(0,0).setStrokeWidth(1).setDashArray(5, 5).setStrokeColor("#0000CC");
            if (layer == m_layer)
            {
                // don't do this if it was added to the overlay layer
                m_selectionDragHandler = new SelectionDragHandler(SelectionManager.this);
                m_dragSelectionStartReg = m_rect.addNodeDragStartHandler(m_selectionDragHandler);
                m_dragSelectionMoveReg = m_rect.addNodeDragMoveHandler(m_selectionDragHandler);
                m_dragSelectionEndReg = m_rect.addNodeDragEndHandler(m_selectionDragHandler);
                m_dragSelectionMouseClickReg = m_rect.addNodeMouseClickHandler(m_selectionDragHandler);
                m_rect.setDraggable(true);
                m_rect.setFillBoundsForSelection(true);
            }

            layer.add(m_rect);
        }

        m_rect.setX(x - padding).setY(y - padding);

        if (width < 0)
        {
            width = Math.abs(width);
            m_rect.setX(m_startX - width);
        }

        if (height < 0)
        {
            height = Math.abs(height);
            m_rect.setY(m_startY - height);
        }

        m_rect.setWidth(width + (padding * 2));
        m_rect.setHeight(height + (padding * 2));
    }

    public static class ChangedItems
    {
        private final NFastArrayList<WiresShape>     m_removedShapes = new NFastArrayList<WiresShape>();
        private final NFastArrayList<WiresShape>     m_addedShapes = new NFastArrayList<WiresShape>();

        private final NFastArrayList<WiresConnector> m_removedConnectors = new NFastArrayList<WiresConnector>();
        private final NFastArrayList<WiresConnector> m_addedConnectors = new NFastArrayList<WiresConnector>();

        public NFastArrayList<WiresShape> getRemovedShapes()
        {
            return m_removedShapes;
        }

        public NFastArrayList<WiresShape> getAddedShapes()
        {
            return m_addedShapes;
        }

        public NFastArrayList<WiresConnector> getRemovedConnectors()
        {
            return m_removedConnectors;
        }

        public NFastArrayList<WiresConnector> getAddedConnectors()
        {
            return m_addedConnectors;
        }

        public int removedSize()
        {
            return m_removedConnectors.size() + m_removedShapes.size();
        }

        public int addedSize()
        {
            return m_addedConnectors.size() + m_addedShapes.size();
        }

        public void clear()
        {
            m_removedShapes.clear();
            m_addedShapes.clear();
            m_removedConnectors.clear();
            m_addedConnectors.clear();
        }
    }

    public static class SelectedItems
    {
        private boolean             selectionGroup;
        private Set<WiresShape>     m_shapes;
        private Set<WiresConnector> m_connectors;
        // external coupled connectors are connectors who are connected to a shape, not part of the selection
        // these will not be part of selection rectangle BoundingBox, but would be part of any copy/paste
        private Set<WiresConnector> m_externallyConnected;
        private BoundingBox         m_bbox;
        private SelectionManager    m_selManager;
        private Layer               m_layer;

        private ChangedItems m_changed = new ChangedItems();

        public SelectedItems(SelectionManager selManager, Layer layer)
        {
            m_selManager = selManager;
            m_layer = layer;
            m_shapes = new HashSet<WiresShape>();
            m_connectors = new HashSet<WiresConnector>();
            m_externallyConnected = new HashSet<WiresConnector>();
            m_bbox = new BoundingBox();
        }

        public ChangedItems getChanged()
        {
            return m_changed;
        }

        public void selected(WiresShape shape, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
        {
            if (!event.isShiftKeyDown())
            {
                // clear all shapes and re-add current to just select the current
                // if only current is selected, it will just remove and re-add it, user should not notice any change.
                m_selManager.destroySelectionRectangle();
                clear();

                add(shape);
            }
            else
            {
                if (m_shapes.contains(shape))
                {
                    remove(shape);
                }
                else  if (findParentIfInSelection(shape)==null)
                {
                    removeChildShape(shape.getChildShapes());
                    add(shape);
                }
            }
            notifyListener(event);
        }

        private WiresShape findParentIfInSelection(WiresShape subjectShape)
        {
            for (WiresShape existingShape : m_shapes)
            {
                if (existingShape == subjectShape || hasChild(existingShape.getChildShapes(), subjectShape))
                {
                    return existingShape;
                }
            }
            return null;
        }

        private boolean hasChild(NFastArrayList<WiresShape> shapes, WiresShape subjectShape)
        {
            for (WiresShape childShape : shapes)
            {
                if ( childShape == subjectShape)
                {
                    return true;
                }
                hasChild(childShape.getChildShapes(), subjectShape);
            }
            return false;
        }

        private boolean removeChildShape(NFastArrayList<WiresShape> shapes)
        {
            for (WiresShape childShape : shapes)
            {
                for (WiresShape existingShape : m_shapes)
                {
                    if ( childShape == existingShape)
                    {
                        // must remove any existing child shape of the parent currently being added
                        remove(existingShape);
                        return true;
                    }
                    removeChildShape(childShape.getChildShapes());
                }
            }
            return false;
        }

        public void selected(WiresConnector connector, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
        {
            if (!event.isShiftKeyDown())
            {
                // clear all shapes and re-add current to just select the current
                // if only current is selected, it will just remove and re-add it, user should not notice any change.
                m_selManager.destroySelectionRectangle();
                clear();

                add(connector);
                if (m_selManager.isExternallyConnected(connector))
                {
                    addExternallyConnected(connector);
                }
            }
            else
            {
                if (m_connectors.contains(connector))
                {
                    remove(connector);
                }
                else
                {
                    add(connector);
                    if (m_selManager.isExternallyConnected(connector))
                    {
                        addExternallyConnected(connector);
                    }
                }
            }
            notifyListener(event);
        }

        public void rebuildBoundingBox()
        {
            m_selManager.m_startBoundingBox = null;
            m_bbox = new BoundingBox();

            for(WiresShape shape : m_shapes)
            {
                BoundingBox nodeBox = shape.getContainer().getComputedBoundingPoints().getBoundingBox();
                m_bbox.add(nodeBox);
            }

            for(WiresConnector connector : m_connectors)
            {
                if ( !m_externallyConnected.contains(connector))
                {
                    BoundingBox nodeBox = connector.getGroup().getComputedBoundingPoints().getBoundingBox();
                    m_bbox.add(nodeBox);
                }
            }
        }

        public boolean add(WiresShape shape)
        {

            // For any Shape being added that also has an added a WiresConnector we must check if the WiresConnector is still connected externally or not
            // At this stage we know it must be in m_externallyConnected, if the other connection is in the selection, then it should be removed from m_externallyConnected
            if ( shape.getMagnets() != null)
            {
                for (int i = 0; i < shape.getMagnets().size(); i++)
                {
                    WiresMagnet magnet = shape.getMagnets().getMagnet(i);
                    if (magnet.getConnections() != null)
                    {
                        for (WiresConnection connection : magnet.getConnections())
                        {
                            WiresConnector wiresConnector = connection.getConnector();
                            if (m_connectors.contains(wiresConnector) && m_externallyConnected.contains(wiresConnector))
                            {
                                WiresConnection otherConnection =  connection.getOppositeConnection();
                                if (otherConnection != null && otherConnection.getMagnet() != null)
                                {
                                    WiresShape otherShape = otherConnection.getMagnet().getMagnets().getWiresShape();
                                    if (findParentIfInSelection(otherShape) != null)
                                    {
                                        m_externallyConnected.remove(wiresConnector);
                                    }
                                }
                                else if (otherConnection.getMagnet() == null)
                                {
                                    m_externallyConnected.remove(wiresConnector);
                                }

                            }
                        }
                    }
                }
            }

            m_changed.getAddedShapes().add(shape);
            return m_shapes.add(shape);
        }

        public boolean remove(WiresShape shape)
        {
            updateExternallyConnectedOnShapeRemove(shape);

            m_changed.getRemovedShapes().add(shape);
            return m_shapes.remove(shape);
        }

        /**I*  if a shape is removed, any connectors need to be checked in case they are not externally connected
         *  this must be done for the current shape and all children shapes, as the child could have a connector in the selection
         * @param shape
         */
        private void updateExternallyConnectedOnShapeRemove(WiresShape shape)
        {
            if ( shape.getMagnets() != null)
            {
                for (int i = 0; i < shape.getMagnets().size(); i++)
                {
                    WiresMagnet magnet = shape.getMagnets().getMagnet(i);
                    if (magnet.getConnections() != null)
                    {
                        for (WiresConnection connection : magnet.getConnections())
                        {
                            WiresConnector wiresConnector = connection.getConnector();
                            if (m_connectors.contains(wiresConnector) && !m_externallyConnected.contains(wiresConnector))
                            {
                                m_externallyConnected.add(wiresConnector);
                            }
                        }
                    }
                }
            }
            for (WiresShape child : shape.getChildShapes())
            {
                updateExternallyConnectedOnShapeRemove(child);
            }
        }

        public boolean add(WiresConnector connector)
        {
            m_changed.getAddedConnectors().add(connector);
            return m_connectors.add(connector);
        }

        public boolean addExternallyConnected(WiresConnector connector)
        {
            return m_externallyConnected.add(connector);
        }


        public boolean remove(WiresConnector connector)
        {
            m_changed.getRemovedConnectors().add(connector);
            m_externallyConnected.remove(connector); // it may or may not be in here, but attempt remove in case it is
            return m_connectors.remove(connector);
        }

        public Set<WiresShape> getShapes()
        {
            return m_shapes;
        }

        public Set<WiresConnector> getConnectors()
        {
            return m_connectors;
        }

        public Set<WiresConnector> getExternallyConnected()
        {
            return m_externallyConnected;
        }

        public boolean isExternallyConnector(WiresConnector connector)
        {
            return m_externallyConnected.contains(connector);
        }

        public boolean isSelectionGroup()
        {
            return selectionGroup;
        }

        public void setSelectionGroup(boolean selectionGroup)
        {
            this.selectionGroup = selectionGroup;
        }

        public int size()
        {
            return m_shapes.size() + m_connectors.size();
        }

        public boolean isEmpty()
        {
            return m_shapes.isEmpty() && m_connectors.isEmpty();
        }

        public void clear()
        {
            // selection controls can only exist, if there is single entry
            m_changed.clear(); // clear first
            recordPrevious();

            selectionGroup  = false;

            m_shapes.clear();
            m_connectors.clear();
            m_externallyConnected.clear();
            m_bbox = new BoundingBox();
            m_selManager.m_startBoundingBox = null;
        }

        public void recordPrevious()
        {
            for (WiresShape shape : m_shapes)
            {
                m_changed.getRemovedShapes().add(shape);
            }

            for (WiresConnector connector : m_connectors)
            {
                m_changed.getRemovedConnectors().add(connector);
            }
        }

        public BoundingBox getBoundingBox()
        {
            return m_bbox;
        }

        public void notifyListener(AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
        {
            if (event.isShiftKeyDown())
            {
                if ( !isEmpty() )
                {
                    selectionGroup = true;
                    rebuildBoundingBox();
                    m_selManager.drawSelectionAsRectangle(SELECTION_PADDING);
                }
                else
                {
                    selectionGroup = false;
                }
            }
            else
            {
                selectionGroup = false;
            }

            notifyListener();
        }

        public void notifyListener()
        {
            m_selManager.m_selectionListener.onChanged(this);
            m_changed.clear();
            if (isEmpty())
            {
                selectionGroup = false;
                // nothign left, so properly clean things up.
                m_selManager.destroySelectionRectangle();
                clear();
            }
            m_layer.draw();
        }
    }

    public void getItemsInBoundingBox(BoundingBox selectionBox)
    {
        m_selected.setSelectionGroup(true);
        BoundingBox box = m_selected.getBoundingBox();

        BoundingBox      nodeBox    = null;
        List<WiresShape> shapesList = new ArrayList<WiresShape>();
        List<WiresShape> toBeRemoved = new ArrayList<WiresShape>();

        Map<String, WiresShape> shapesMap = new HashMap<String, WiresShape>();
        Map<String, BoundingBox> uuidMap = new HashMap<String, BoundingBox>();

        // first build a map of all shapes that intersect with teh selection rectangle. Nested shapes will be used later.
        for (WiresShape shape : m_wiresManager.getShapesMap().values())
        {
            nodeBox = shape.getContainer().getComputedBoundingPoints().getBoundingBox();
            if (selectionBox.intersects(nodeBox))
            {
                shapesList.add(shape);
                shapesMap.put(shape.getContainer().uuid(), shape);
                uuidMap.put(shape.getContainer().uuid(), nodeBox);
            }
        }

        // add to removal list any shape whose parent is also in the selection
        for (WiresShape shape : shapesMap.values())
        {
            if (shapesMap.containsKey(shape.getParent().getContainer().uuid()))
            {
                toBeRemoved.add(shape); // can't remove yet, as it may have selected children itself, which will also need to be removed
            }
        }

        // now the list is built, safely remove the shapes
        for (WiresShape shape : toBeRemoved)
        {
            shapesMap.remove(shape.getContainer().uuid());
        }

        for (WiresShape shape : shapesMap.values())
        {
            nodeBox = uuidMap.get(shape.getContainer().uuid());
            m_selected.add(shape);
            box.add(nodeBox);
        }

        for (WiresConnector connector : m_wiresManager.getConnectorList())
        {
            boolean externallyConnected = isExternallyConnected(connector);

            Point2DArray points = new Point2DArray();
            Point2D      loc    = m_rect.getLocation();
            points.push(loc.getX(), loc.getY());
            points.push(loc.getX() + m_rect.getWidth(), loc.getY() );
            points.push(loc.getX() + m_rect.getWidth(), loc.getY() + m_rect.getHeight() );
            points.push(loc.getX(), loc.getY() + m_rect.getHeight() );

            nodeBox = connector.getGroup().getComputedBoundingPoints().getBoundingBox();
            if (selectionBox.contains(nodeBox))
            {
                addConnector(connector, externallyConnected, box, nodeBox);
            }
            else
            {
                Point2DArray intersections = Geometry.getIntersectPolyLinePath(points, connector.getLine().getPathPartList(), true);
                if (intersections!=null && intersections.size()> 0)
                {
                    addConnector(connector, externallyConnected, box, nodeBox);
                }
                else
                {
                    // the above checked the line, also check the head and tail.

                    // head is rotated around an offset with also set. The reverse of this must be applied to the
                    // selection rectangle, to ensure thinsg are all in the same space,  for intersection to work
                    MultiPath path = connector.getHead();
                    Transform xfrm = new Transform();
                    xfrm.translate(path.getOffset().getX(),path.getOffset().getY());
                    xfrm.rotate(0-path.getRotation());
                    xfrm.translate(0 - path.getX(), 0 - path.getY());
                    xfrm.translate(0 - path.getOffset().getX(), 0 - path.getOffset().getY());

                    Point2DArray transformedPoints = points.copy();
                    for (Point2D p : transformedPoints)
                    {
                        xfrm.transform(p, p);
                    }

                    intersections = Geometry.getIntersectPolyLinePath(transformedPoints, connector.getHead().getPathPartListArray().get(0), true);
                    if (intersections!=null && intersections.size()> 0)
                    {
                        addConnector(connector, externallyConnected, box, nodeBox);
                    }
                    else
                    {
                        // tail is rotated around an offset with also set. The reverse of this must be applied to the
                        // selection rectangle, to ensure thinsg are all in the same space,  for intersection to work
                        path = connector.getTail();
                        xfrm = new Transform();
                        xfrm.translate(path.getOffset().getX(),path.getOffset().getY());
                        xfrm.rotate(0-path.getRotation());
                        xfrm.translate(0 - path.getX(), 0 - path.getY());
                        xfrm.translate(0 - path.getOffset().getX(), 0 - path.getOffset().getY());

                        transformedPoints = points.copy();
                        for (Point2D p : transformedPoints)
                        {
                            xfrm.transform(p, p);
                        }

                        intersections = Geometry.getIntersectPolyLinePath(transformedPoints, connector.getTail().getPathPartListArray().get(0), true);
                        if (intersections!=null && intersections.size()> 0)
                        {
                            addConnector(connector, externallyConnected, box, nodeBox);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns wheher the connector is connected to a shape not in the selection.
     * As this could be connected to a nested shape, it iterates from that shape (not in the selection) until it finds it's parent in the selection or it returns null.
     * @param connector
     * @return
     */
    private boolean isExternallyConnected(WiresConnector connector)
    {
        WiresShape headShape = null;
        WiresShape tailShape = null;
        if ( connector.getHeadConnection().getMagnet() != null)
        {
            headShape = connector.getHeadConnection().getMagnet().getMagnets().getWiresShape();
        }
        if ( connector.getTailConnection().getMagnet() != null)
        {
            tailShape = connector.getTailConnection().getMagnet().getMagnets().getWiresShape();
        }
        boolean hasHeadShapeNotInSelection = headShape != null && m_selected.findParentIfInSelection(headShape) == null;
        boolean hasTailShapeNotInSelection = tailShape != null && m_selected.findParentIfInSelection(tailShape) == null;

        return hasHeadShapeNotInSelection || hasTailShapeNotInSelection;
    }

    private void addConnector(WiresConnector connector, boolean externallyCoupled, BoundingBox box, BoundingBox nodeBox)
    {
        m_selected.add(connector);

        if (!externallyCoupled)
        {
            box.add(nodeBox);
        }
        else
        {
            m_selected.addExternallyConnected(connector);
        }

    }

    private void destroySelectionRectangle()
    {
        if (m_rect != null )
        {
            if (m_dragSelectionStartReg != null)
            {
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
            m_rect.removeFromParent();
            m_rect = null;
            m_selectionDragHandler = null;
        }
    }

    public void destroy()
    {
        if (m_selectMouseDownHandlerReg != null)
        {
            m_selectMouseDownHandlerReg.removeHandler();
            m_selectMouseClickHandlerReg.removeHandler();
            m_selectMouseDoubleClickHandlerReg.removeHandler();

            m_selectMouseDownHandlerReg = null;
            m_selectMouseClickHandlerReg = null;
            m_selectMouseDoubleClickHandlerReg = null;
        }

        destroySelectionRectangle();
    }

    public static class SelectionDragHandler implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler, NodeMouseClickHandler
    {
        private SelectionManager m_selectionManager;

        private NFastDoubleArray m_shapeStartLocations;

        private WiresConnector[] m_connectorsWithSpecialConnections;

        public SelectionDragHandler(SelectionManager selectionManager)
        {
            this.m_selectionManager = selectionManager;
        }

        @Override
        public void onNodeMouseClick(NodeMouseClickEvent event)
        {
            // temporarily remove the rect, use findAt to find the underlying shape and trigger the click event
            m_selectionManager.m_rect.removeFromParent();
            m_selectionManager.m_layer.draw();
            Node<?> node = m_selectionManager.m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
            m_selectionManager.m_layer.add(m_selectionManager.m_rect);
            m_selectionManager.m_layer.draw();
            while (node!=null)
            {
                if(node.isEventHandled(event.getAssociatedType()))
                {
                    node.fireEvent(event);
                    break;
                }
                node = node.getParent();
            }
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_shapeStartLocations = new NFastDoubleArray();
            Map<String, WiresConnector> connectors = new HashMap<String, WiresConnector>();

            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                WiresShapeControlImpl.collectionSpecialConnectors(shape, connectors);

                if ( shape.getMagnets() != null)
                {
                    shape.getMagnets().onNodeDragStart(event); // Must do magnets first, to avoid attribute change updates being processed.
                                                               // Don't need to do this for nested objects, as those just move with their containers, without attribute changes
                }
                m_shapeStartLocations.push(shape.getX(), shape.getY());

                ((WiresShapeControlImpl) shape.getHandler().getControl()).dragStart(event.getDragContext());
            }

            m_connectorsWithSpecialConnections = connectors.values().toArray(new WiresConnector[connectors.size()]);

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                handler.onNodeDragStart(event); // records the start position of all the points
                WiresConnector.updateHeadTailForRefreshedConnector(connector);
            }
        }

        @Override public void onNodeDragMove(NodeDragMoveEvent event)
        {
            int dx = event.getDragContext().getDx();
            int dy = event.getDragContext().getDy();

            int i = 0;
            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                shape.setX(m_shapeStartLocations.get(i++) + dx);
                shape.setY(m_shapeStartLocations.get(i++) + dy);

                if ( shape.getMagnets() != null)
                {
                    shape.getMagnets().onNodeDragMove(event);  // Don't need to do this for nested objects, as those just move with their containers, without attribute changes
                }

                WiresShapeControlImpl.updateNestedShapes(shape);

                ((WiresShapeControlImpl)shape.getHandler().getControl()).dragMove(event.getDragContext());
            }

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                ((WiresConnectorControlImpl)handler.getControl()).move(dx, dy, true, true);
                WiresConnector.updateHeadTailForRefreshedConnector(connector);
            }

            WiresShapeControlImpl.updateSpecialConnections(m_connectorsWithSpecialConnections,
                                                           false);

            updateRectangleForExternallyConnectedConnectors(dx, dy, m_selectionManager.m_startBoundingBox);

            m_selectionManager.m_layer.batch();
        }

        @Override public void onNodeDragEnd(NodeDragEndEvent event)
        {
            int dx = event.getDragContext().getDx();
            int dy = event.getDragContext().getDy();

            int i = 0;
            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                shape.setX(m_shapeStartLocations.get(i++) + dx);
                shape.setY(m_shapeStartLocations.get(i++) + dy);

                if ( shape.getMagnets() != null)
                {
                    shape.getMagnets().onNodeDragEnd(event);
                }

                WiresShapeControlImpl.updateNestedShapes(shape);

                ((WiresShapeControlImpl)shape.getHandler().getControl()).dragEnd(event.getDragContext());
            }

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                ((WiresConnectorControlImpl)handler.getControl()).move(dx, dy, true, true);
                ((WiresConnectorControlImpl)handler.getControl()).dragEnd(); // must be called to null the  points array
                WiresConnector.updateHeadTailForRefreshedConnector(connector);
            }

            WiresShapeControlImpl.updateSpecialConnections(m_connectorsWithSpecialConnections,
                                                           true);

            updateRectangleForExternallyConnectedConnectors(dx, dy, m_selectionManager.m_startBoundingBox);
            if ( m_selectionManager.m_startBoundingBox != null)
            {
                m_selectionManager.m_startBoundingBox.offset(dx, dy);
            }

            m_connectorsWithSpecialConnections = null;
            m_shapeStartLocations = null;
            m_selectionManager.m_ignoreMouseClick = true; // need to ignore the click event after



            m_selectionManager.m_layer.batch();
        }

        private void updateRectangleForExternallyConnectedConnectors(int dx, int dy, BoundingBox originalBox)
        {
            if (m_selectionManager.m_selected.m_externallyConnected.isEmpty())
            {
                return;
            }
            BoundingBox box = new BoundingBox();
            if (originalBox!=null)
            {
                box.add(originalBox);
                box.offset(dx, dy);
            }

            for (WiresConnector connector : m_selectionManager.m_selected.m_externallyConnected)
            {
                box.add(connector.getHead().getComputedBoundingPoints().getBoundingBox());
                box.add(connector.getTail().getComputedBoundingPoints().getBoundingBox());
                box.add(connector.getLine().getComputedBoundingPoints().getBoundingBox());
            }

            m_selectionManager.m_rect.setX(box.getMinX());
            m_selectionManager.m_rect.setY(box.getMinY());
            m_selectionManager.m_rect.setWidth(box.getMaxX() - box.getMinX());
            m_selectionManager.m_rect.setHeight(box.getMaxY() - box.getMinY());
        }
    }
}
