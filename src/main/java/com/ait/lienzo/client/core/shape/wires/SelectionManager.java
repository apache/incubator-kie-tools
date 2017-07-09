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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class SelectionManager implements NodeMouseDownHandler, NodeMouseDoubleClickHandler, NodeMouseClickHandler
{
    public static int           SELECTION_PADDING = 10;


    private HandlerRegistration m_selectMouseDownHandlerReg;
    private HandlerRegistration m_selectMouseClickHandlerReg;
    private HandlerRegistration m_selectMouseDoubleClickHandlerReg;

    private final Layer          m_layer;

    private final WiresManager        m_wiresManager;

    private final SelectedItems       m_selected;

    private       Rectangle           m_rect;

    private       double              m_startX;

    private       double              m_startY;

    private       HandlerRegistration m_dragSelectionStartReg;

    private       HandlerRegistration m_dragSelectionMoveReg;

    private       HandlerRegistration m_dragSelectionEndReg;

    private       boolean             m_selectionCreationInProcess;

    private       boolean             m_ignoreMouseClick;

    private       SelectionListener   m_selectionListener;

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
        @Override
        public boolean onMouseEventBefore(MouseEvent<? extends EventHandler> event)
        {
            // CLICK
            if (event.getAssociatedType() == ClickEvent.getType())
            {
                // this is to differentiate on a drag's mouseup event. It must come before the m_rect null
                // as it must always cleanup a m_ignoreMouseClick after a mouse down
                if ( m_ignoreMouseClick)
                {
                    m_ignoreMouseClick = false; // drag has finished, so reset
                    return false;
                }
                else
                {
                    return true;
                }
            }

            // No rectangle and not about to create and create one
            if ( m_rect == null && !m_selectionCreationInProcess )
            {
                // do nothing, if the user clicks on the layer, this will be picked up by the Layer's OnNodeMouseDown
                return true;
            }

            // MOVE
            if (event.getAssociatedType() == MouseMoveEvent.getType() )
            {
                if (m_selectionCreationInProcess)
                {
                    drawRectangle((MouseMoveEvent) event);
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
                return true;
            }

            // UP
            if (event.getAssociatedType() == MouseUpEvent.getType())
            {
                if(m_selectionCreationInProcess)
                {
                    m_ignoreMouseClick = true;

                    m_selected.clear();
                    // rectangle is null, for a layer mouse down without any drag
                    if ( m_rect != null)
                    {
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
                                BoundingBox bbox = m_selected.getBoundingBox();
                                m_rect.setX(bbox.getX() - SELECTION_PADDING);
                                m_rect.setY(bbox.getY() - SELECTION_PADDING);
                                m_rect.setWidth(bbox.getWidth() + (SELECTION_PADDING * 2));
                                m_rect.setHeight(bbox.getHeight() + (SELECTION_PADDING * 2));
                                m_rect.setDraggable(true);

                                m_rect.setFillBoundsForSelection(true);
                                m_layer.add(m_rect); // need to add it to the main layer, so that normal drag works

                                m_layer.draw();
                            }
                            else
                            {
                                // destroy the selection if it's empty
                                destroySelectionRectangle();
                            }
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

    @Override public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        Node<?> node = m_layer.getViewport().findShapeAtPoint(event.getX(), event.getY());
        if ( node != null)
        {
            // only start the select if there is no shape at the current mouse xy/
            // events bubble up to root, if there are no handlers for this specific event type.
            return;
        }

        m_startX = event.getX();
        m_startY = event.getY();
        m_selectionCreationInProcess = true;
        destroySelectionRectangle();
        m_layer.draw();
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event)
    {
        m_selected.clear();
        m_selected.notifyListener();
        destroySelectionRectangle();
        m_layer.draw();
    }

    @Override
    public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event)
    {
        m_selected.clear();
        m_selected.notifyListener();
        destroySelectionRectangle();
        m_layer.draw();
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
        private Set<WiresShape>     m_shapes;
        private Set<WiresConnector> m_connectors;
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
            m_bbox = new BoundingBox();
        }

        public ChangedItems getChanged()
        {
            return m_changed;
        }

        public void selected(WiresShape shape, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
        {
            if (m_shapes.contains(shape))
            {
                if (m_shapes.size() == 1)
                {
                    // it's selected itself, with only itself, so do nothing.
                    return;
                }

                clear();
                remove(shape);
            }
            else
            {
                m_selManager.destroySelectionRectangle();
                clear();
                add(shape);
            }
            notifyListener();
        }

        public void selected(WiresConnector connector, AbstractNodeMouseEvent<MouseEvent<?>, ?> event)
        {

            if (m_connectors.contains(connector))
            {
                if (m_connectors.size() == 1)
                {
                    // it's selected itself, with only itself, so do nothing.
                    return;
                }

                clear();
                remove(connector);
            }
            else
            {
                m_selManager.destroySelectionRectangle();
                clear();
                add(connector);
            }
            notifyListener();
        }

        public boolean add(WiresShape shape)
        {
            m_changed.getAddedShapes().add(shape);
            return m_shapes.add(shape);
        }

        public boolean remove(WiresShape shape)
        {
            m_changed.getRemovedShapes().add(shape);
            return m_shapes.remove(shape);
        }

        public boolean add(WiresConnector connector)
        {
            m_changed.getAddedConnectors().add(connector);
            return m_connectors.add(connector);
        }

        public boolean remove(WiresConnector connector)
        {
            m_changed.getRemovedConnectors().add(connector);
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

            m_shapes.clear();
            m_connectors.clear();
            m_bbox = new BoundingBox();
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

        public void notifyListener()
        {
            m_selManager.m_selectionListener.onChanged(this);
            m_layer.draw();
        }
    }

    public void getItemsInBoundingBox(BoundingBox selectionBox)
    {
        BoundingBox box = m_selected.getBoundingBox();

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
            m_selected.add(shape);
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
                m_selected.add(connector);
                box.add(nodeBox);
            }
            else
            {
                Point2DArray intersections = Geometry.getIntersectPolyLinePath(points, connector.getLine().getPathPartList(), true);
                if (intersections!=null && intersections.size()> 0)
                {
                    m_selected.add(connector);
                    box.add(nodeBox);
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
                        m_selected.add(connector);
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
                            m_selected.add(connector);
                            box.add(nodeBox);
                        }
                    }
                }
            }
        }
    }

    private void destroySelectionRectangle()
    {
        if (m_rect != null )
        {
            m_dragSelectionStartReg.removeHandler();
            m_dragSelectionMoveReg.removeHandler();
            m_dragSelectionEndReg.removeHandler();
            m_rect.removeFromParent();
            m_rect = null;
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
            m_selectionManager.m_ignoreMouseClick = true; // need to ignore the click event after
        }
    }
}
