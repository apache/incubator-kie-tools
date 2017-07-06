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

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
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
import com.ait.tooling.nativetools.client.collection.NFastDoubleArray;
import com.ait.tooling.nativetools.client.util.Console;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class SelectionManager implements NodeMouseDownHandler
{
    public static int           SELECTION_PADDING = 10;


    private HandlerRegistration m_selectMouseDownHandlerReg;

    private Layer               m_layer;

    private Rectangle           m_rect;

    private double              m_startX;

    private double              m_startY;

    private WiresManager        m_wiresManager;

    private SelectedItems       m_selected;

    private HandlerRegistration m_dragSelectionStartReg;

    private HandlerRegistration m_dragSelectionMoveReg;

    private HandlerRegistration m_dragSelectionEndReg;

    private boolean             m_selectionCreationInProcess;

    private boolean             m_selectionDragInProcess;

    private boolean             m_ignoreMouseClick;

    public SelectionManager(WiresManager wiresManager)
    {
        m_wiresManager = wiresManager;
        m_layer = wiresManager.getLayer().getLayer();
        m_selectMouseDownHandlerReg = m_layer.addNodeMouseDownHandler(this);

        OnMouseXEventHandler onMouseXEventHandler = new OnMouseXEventHandler();
        OnEventHandlers      onEventHandlers      = m_layer.getViewport().getOnEventHandlers();
        onEventHandlers.setOnMouseClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDoubleClickEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseDownEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseUpEventHandle(onMouseXEventHandler);
        onEventHandlers.setOnMouseMoveEventHandle(onMouseXEventHandler);
    }

    public class OnMouseXEventHandler implements OnMouseEventHandler
    {
        @Override
        public boolean onMouseEventBefore(MouseEvent<? extends EventHandler> event)
        {
            if ( m_rect == null && !m_selectionCreationInProcess )
            {
                // do nothing, if the user clicks on the layer, this will be picked up by the Layer's OnNodeMouseDown
                return true;
            }

            boolean returnValue = true;
            if (event.getAssociatedType() == MouseDownEvent.getType() && m_rect != null)
            {
                boolean contains = m_rect.getComputedBoundingPoints().getBoundingBox().contains(new Point2D(event.getRelativeX(event.getRelativeElement()), event.getRelativeY(event.getRelativeElement())));
                // selection was outside of the box or m_selectionCreationInProcess could be true, if mouse went out before mouseup, so it needs cleaning up too
                if (!contains || m_selectionCreationInProcess)
                {
                    destroySelectionRectangle();
                }
                else
                {
                    m_selectionDragInProcess = true;
                    m_ignoreMouseClick = true;
                }
                returnValue = true;
            }
            else if (event.getAssociatedType() == MouseMoveEvent.getType())
            {
                if (m_selectionDragInProcess)
                {
                    Console.get().info("drag in process");
                }
                if (m_selectionCreationInProcess)
                {
                    drawRectangle((MouseMoveEvent) event);
                    returnValue =  false;
                }
                else
                {
                    return true;
                }

            }
            else if (event.getAssociatedType() == MouseUpEvent.getType())
            {
                if(m_selectionCreationInProcess)
                {
                    m_ignoreMouseClick = true;
                    int x = event.getRelativeX(event.getRelativeElement());
                    int y = event.getRelativeY(event.getRelativeElement());
                    if (x != m_startX && y != m_startY)
                    {
                        m_selected = getItemsInBoundingBox(m_rect.getComputedBoundingPoints().getBoundingBox());
                        // can be null if there was no mousemove
                        if (!m_selected.isEmpty())
                        {

                            for (WiresShape shape : m_selected.m_shapes)
                            {
                                Console.get().info(shape.getContainer().getUserData().toString());
                            }

                            for (WiresConnector connector : m_selected.m_connectors)
                            {
                                Console.get().info("connector");
                            }
                            m_rect.removeFromParent();
                            BoundingBox bbox = m_selected.getBoundingBox();
                            m_rect.setX(bbox.getX() - SELECTION_PADDING);
                            m_rect.setY(bbox.getY() - SELECTION_PADDING);
                            m_rect.setWidth(bbox.getWidth() + (SELECTION_PADDING * 2));
                            m_rect.setHeight(bbox.getHeight() + (SELECTION_PADDING * 2));
                            m_rect.setDraggable(true);

                            m_rect.setFillBoundsForSelection(true);
                            m_layer.add(m_rect); // need to add it to the main layer, so that normal drag works

                            m_layer.draw();
                            m_layer.getViewport().getDragLayer().draw();
                        }
                        else
                        {
                            // destroy the selection if it's empty
                            destroySelectionRectangle();
                        }
                    }
                    m_selectionCreationInProcess = false;
                    returnValue = false;
                }
            }
            else
            {
                if (!m_ignoreMouseClick)
                {
                    destroySelectionRectangle();
                    returnValue =  true;
                }
                else
                {
                    returnValue = false;
                    m_ignoreMouseClick = false;
                }
            }



            return returnValue;
        }

        @Override public void onMouseEventAfter(MouseEvent<? extends EventHandler> event)
        {

        }
    }

    @Override public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        Node<?> node = m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
        if ( node != null)
        {
            // only start the select if there is no shape at the current mouse xy/
            // events bubble up to root, if there are no handlers for this specific event type.
            return;
        }

        m_selectionCreationInProcess = true;
        m_startX = event.getX();
        m_startY = event.getY();
    }

    public void drawRectangle(MouseMoveEvent event)
    {
        if ( m_rect == null )
        {
            m_rect = new Rectangle(0,0).setStrokeWidth(1).setDashArray(5, 5).setStrokeColor("#0000CC").setX(m_startX).setY(m_startY);
            SelectionDragHandler selectionHandler = new SelectionDragHandler(SelectionManager.this);
            m_dragSelectionStartReg = m_rect.addNodeDragStartHandler(selectionHandler);
            m_dragSelectionMoveReg =  m_rect.addNodeDragMoveHandler(selectionHandler);
            m_dragSelectionEndReg =  m_rect.addNodeDragEndHandler(selectionHandler);

            m_layer.getViewport().getDragLayer().add(m_rect);
        }

        double width = event.getX() - m_startX;
        double height = event.getY() - m_startY;

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

        m_rect.setWidth(width);
        m_rect.setHeight(height);

        m_layer.getViewport().getDragLayer().draw();
    }

    public void moveRectangle(MouseMoveEvent event)
    {
        //m_selectionDragInProcess
    }

    private static class SelectedItems
    {
        Set<WiresShape> m_shapes;
        Set<WiresConnector> m_connectors;
        BoundingBox m_bbox;

        public SelectedItems()
        {
            m_shapes = new HashSet<WiresShape>();
            m_connectors = new HashSet<WiresConnector>();
            m_bbox = new BoundingBox();
        }

        public boolean isEmpty()
        {
            return m_shapes.isEmpty() && m_connectors.isEmpty();
        }

        public void clear()
        {
            m_shapes.clear();
            m_connectors.clear();
        }

        public BoundingBox getBoundingBox()
        {
            return m_bbox;
        }
    }

    public SelectedItems getItemsInBoundingBox(BoundingBox selectionBox)
    {
        SelectedItems selectedItems = new SelectedItems();
        BoundingBox box = selectedItems.getBoundingBox();

        BoundingBox      nodeBox    = null;
        List<WiresShape> shapesList = new ArrayList<WiresShape>();
        List<WiresShape> toBeRemoved = new ArrayList<WiresShape>();

        Map<String, WiresShape> shapesMap = new HashMap<String, WiresShape>();
        Map<String, BoundingBox> uuidMap = new HashMap<String, BoundingBox>();
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

        for (WiresShape shape : shapesMap.values())
        {
            if (shapesMap.containsKey(shape.getParent().getContainer().uuid()))
            {
                toBeRemoved.add(shape); // can't remove yet, as it may have selected children itself, which will also need to be removed
            }
        }

        for (WiresShape shape : toBeRemoved)
        {
            shapesMap.remove(shape.getContainer().uuid());
        }

        for (WiresShape shape : shapesMap.values())
        {
            nodeBox = uuidMap.get(shape.getContainer().uuid());
            selectedItems.m_shapes.add(shape);
            box.add(nodeBox);
        }

        for (WiresConnector connector : m_wiresManager.getConnectorList())
        {
            Point2DArray points = new Point2DArray();
            Point2D      loc    = m_rect.getLocation();
            points.push(loc.getX(), loc.getY());
            points.push(loc.getX() + m_rect.getWidth(), loc.getY() );
            points.push(loc.getX() + m_rect.getWidth(), loc.getY() + m_rect.getHeight() );
            points.push(loc.getX(), loc.getY() + m_rect.getHeight() );

            nodeBox = connector.getGroup().getComputedBoundingPoints().getBoundingBox();
            if (selectionBox.contains(nodeBox))
            {
                selectedItems.m_connectors.add(connector);
                box.add(nodeBox);
            }
            else
            {
                Point2DArray intersections = Geometry.getIntersectPolyLinePath(points, connector.getLine().getPathPartList(), true);
                if (intersections!=null && intersections.size()> 0)
                {
                    selectedItems.m_connectors.add(connector);
                    box.add(nodeBox);
                }
                else
                {
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
                        selectedItems.m_connectors.add(connector);
                        box.add(nodeBox);
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
                            selectedItems.m_connectors.add(connector);
                            box.add(nodeBox);
                        }
                    }
                }
            }
        }

        return selectedItems;
    }

    private void destroySelectionRectangle()
    {

        if (m_rect != null )
        {
            m_dragSelectionStartReg.removeHandler();
            m_dragSelectionMoveReg.removeHandler();
            m_dragSelectionEndReg.removeHandler();

            Layer player = m_rect.getLayer();
            m_rect.removeFromParent();
            m_rect = null;

            player.draw();
        }

        if (m_selected != null)
        {
            m_selected.clear();
            m_selected = null;
        }

    }

    public void destroy()
    {
        if (m_selectMouseDownHandlerReg != null)
        {
            m_selectMouseDownHandlerReg.removeHandler();
            m_selectMouseDownHandlerReg = null;
        }

        destroySelectionRectangle();
    }

    public static class SelectionDragHandler implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler
    {
        private SelectionManager m_selectionManager;

        private NFastDoubleArray m_shapeStartLocations;

        private WiresConnector[] m_connectorsWithSpecialConnections;

        public SelectionDragHandler(SelectionManager selectionManager)
        {
            this.m_selectionManager = selectionManager;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event)
        {
            m_shapeStartLocations = new NFastDoubleArray();
            Map<String, WiresConnector> connectors = new HashMap<String, WiresConnector>();


            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                WiresShapeControlImpl.collectionSpecialConnectors(shape, connectors);

                shape.getMagnets().onNodeDragStart(event); // must do magnets first, to avoid attribute change updates being processed
                m_shapeStartLocations.push(shape.getX(), shape.getY());

                ((WiresShapeControlImpl)shape.getHandler().getControl()).dragStart(event.getDragContext());
            }

            m_connectorsWithSpecialConnections = connectors.values().toArray(new WiresConnector[connectors.size()]);

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                handler.onNodeDragStart(event); // records the start position of all the points
            }
        }

        @Override public void onNodeDragMove(NodeDragMoveEvent event)
        {
            int i = 0;
            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                shape.setX(m_shapeStartLocations.get(i++) + event.getDragContext().getDx());
                shape.setY(m_shapeStartLocations.get(i++) + event.getDragContext().getDy());

                shape.getMagnets().onNodeDragMove(event);

                ((WiresShapeControlImpl)shape.getHandler().getControl()).dragMove(event.getDragContext());
            }

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                ((WiresConnectorControlImpl)handler.getControl()).move(event.getDragContext().getDx(), event.getDragContext().getDy(), true, true);
            }

            WiresShapeControlImpl.updateSpecialConnections(m_connectorsWithSpecialConnections);
        }

        @Override public void onNodeDragEnd(NodeDragEndEvent event)
        {
            int i = 0;
            for (WiresShape shape : m_selectionManager.m_selected.m_shapes)
            {
                shape.setX(m_shapeStartLocations.get(i++) + event.getDragContext().getDx());
                shape.setY(m_shapeStartLocations.get(i++) + event.getDragContext().getDy());

                shape.getMagnets().onNodeDragEnd(event);

                ((WiresShapeControlImpl)shape.getHandler().getControl()).dragEnd(event.getDragContext());
            }

            for (WiresConnector connector : m_selectionManager.m_selected.m_connectors)
            {
                WiresConnector.WiresConnectorHandler handler = connector.getWiresConnectorHandler();
                ((WiresConnectorControlImpl)handler.getControl()).move(event.getDragContext().getDx(), event.getDragContext().getDy(), true, true);
                ((WiresConnectorControlImpl)handler.getControl()).dragEnd(); // must be called to null the  points array
            }

            WiresShapeControlImpl.updateSpecialConnections(m_connectorsWithSpecialConnections);

            m_connectorsWithSpecialConnections = null;
            m_shapeStartLocations = null;
        }
    }
}
