/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.widget;

import java.util.ArrayList;

import com.ait.lienzo.client.core.LienzoGlobals;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeGestureChangeEvent;
import com.ait.lienzo.client.core.event.NodeGestureEndEvent;
import com.ait.lienzo.client.core.event.NodeGestureStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.client.core.event.NodeTouchCancelEvent;
import com.ait.lienzo.client.core.event.NodeTouchEndEvent;
import com.ait.lienzo.client.core.event.NodeTouchMoveEvent;
import com.ait.lienzo.client.core.event.NodeTouchStartEvent;
import com.ait.lienzo.client.core.event.TouchPoint;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.NodeType;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * This class is the center for all canvas events.
 * <p>
 * It can not and should not be used directly as it is used internally by the toolkit to handle and dispatch {@link Shape} events.
 */
final class LienzoHandlerManager
{
    private final LienzoPanel m_lienzo;

    private boolean           m_dragging               = false;

    private boolean           m_dragging_using_touches = false;

    private boolean           m_dragging_dispatch_move = false;

    private boolean           m_dragging_ignore_clicks = false;

    private boolean           m_dragging_mouse_pressed = false;

    private DragMode          m_drag_mode              = null;

    private IPrimitive<?>     m_drag_node              = null;

    private IPrimitive<?>     m_over_prim              = null;

    private DragContext       m_dragContext;

    ArrayList<TouchPoint>     m_touches                = null;

    private Mediators         m_mediators;

    public LienzoHandlerManager(LienzoPanel lienzo)
    {
        m_lienzo = lienzo;

        m_mediators = lienzo.getViewport().getMediators();

        if (null != m_lienzo)
        {
            addHandlers();
        }
    }

    private final ArrayList<TouchPoint> getTouches(TouchEvent<?> event)
    {
        ArrayList<TouchPoint> touches = new ArrayList<TouchPoint>();

        JsArray<Touch> jsarray = event.getTouches();

        Element element = event.getRelativeElement();

        if ((null != jsarray) && (jsarray.length() > 0))
        {
            int size = jsarray.length();

            for (int i = 0; i < size; i++)
            {
                Touch touch = jsarray.get(i);

                touches.add(new TouchPoint(touch.getRelativeX(element), touch.getRelativeY(element)));
            }
        }
        else
        {
            int x = event.getNativeEvent().getClientX() - element.getAbsoluteLeft() + element.getScrollLeft() + element.getOwnerDocument().getScrollLeft();

            int y = event.getNativeEvent().getClientY() - element.getAbsoluteTop() + element.getScrollTop() + element.getOwnerDocument().getScrollTop();

            touches.add(new TouchPoint(x, y));
        }
        return touches;
    }

    private final void addHandlers()
    {
        m_lienzo.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                event.preventDefault();

                onNodeMouseClick(new NodeMouseClickEvent(event));
            }
        });
        m_lienzo.addDoubleClickHandler(new DoubleClickHandler()
        {
            @Override
            public void onDoubleClick(DoubleClickEvent event)
            {
                onNodeMouseDoubleClick(new NodeMouseDoubleClickEvent(event));
            }
        });
        m_lienzo.addMouseMoveHandler(new MouseMoveHandler()
        {
            @Override
            public void onMouseMove(MouseMoveEvent event)
            {
                event.preventDefault();

                NodeMouseMoveEvent nodeEvent = new NodeMouseMoveEvent(event);

                if ((m_dragging) && (m_dragging_using_touches))
                {
                    return; // Ignore weird Mouse Move (0,0) in the middle of a Touch Drag on iOS/Safari
                }
                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseMove(nodeEvent);
            }
        });
        m_lienzo.addMouseUpHandler(new MouseUpHandler()
        {
            @Override
            public void onMouseUp(MouseUpEvent event)
            {
                NodeMouseUpEvent nodeEvent = new NodeMouseUpEvent(event);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseUp(nodeEvent);
            }
        });
        m_lienzo.addMouseDownHandler(new MouseDownHandler()
        {
            @Override
            public void onMouseDown(MouseDownEvent event)
            {
                event.preventDefault();

                NodeMouseDownEvent nodeEvent = new NodeMouseDownEvent(event);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseDown(nodeEvent);
            }
        });
        m_lienzo.addMouseOutHandler(new MouseOutHandler()
        {
            @Override
            public void onMouseOut(MouseOutEvent event)
            {
                NodeMouseOutEvent nodeEvent = new NodeMouseOutEvent(event);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseOut(nodeEvent);
            }
        });
        m_lienzo.addMouseOverHandler(new MouseOverHandler()
        {
            @Override
            public void onMouseOver(MouseOverEvent event)
            {
                NodeMouseOverEvent nodeEvent = new NodeMouseOverEvent(event);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseOver(nodeEvent);
            }
        });
        m_lienzo.addMouseWheelHandler(new MouseWheelHandler()
        {
            @Override
            public void onMouseWheel(MouseWheelEvent event)
            {
                NodeMouseWheelEvent nodeEvent = new NodeMouseWheelEvent(event);

                if (false == m_mediators.handleEvent(nodeEvent))
                {
                    fireEvent(nodeEvent);
                }
                else
                {
                    event.preventDefault();

                    event.stopPropagation();
                }
            }
        });
        m_lienzo.addTouchCancelHandler(new TouchCancelHandler()
        {
            @Override
            public void onTouchCancel(TouchCancelEvent event)
            {
                event.preventDefault();

                NodeTouchCancelEvent nodeEvent = new NodeTouchCancelEvent(getTouches(event));

                if (m_mediators.handleEvent(event))
                {
                    return;
                }
                onNodeMouseOut(nodeEvent);
            }
        });
        m_lienzo.addTouchEndHandler(new TouchEndHandler()
        {
            @Override
            public void onTouchEnd(TouchEndEvent event)
            {
                event.preventDefault();

                NodeTouchEndEvent nodeEvent = new NodeTouchEndEvent(m_touches);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseUp(nodeEvent);
            }
        });
        m_lienzo.addTouchMoveHandler(new TouchMoveHandler()
        {
            @Override
            public void onTouchMove(TouchMoveEvent event)
            {
                event.preventDefault();

                m_touches = getTouches(event);

                NodeTouchMoveEvent nodeEvent = new NodeTouchMoveEvent(m_touches);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseMove(nodeEvent);
            }
        });
        m_lienzo.addTouchStartHandler(new TouchStartHandler()
        {
            @Override
            public void onTouchStart(TouchStartEvent event)
            {
                event.preventDefault();

                m_touches = getTouches(event);

                NodeTouchStartEvent nodeEvent = new NodeTouchStartEvent(m_touches);

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                onNodeMouseDown(nodeEvent);
            }
        });
        m_lienzo.addGestureStartHandler(new GestureStartHandler()
        {
            @Override
            public void onGestureStart(GestureStartEvent event)
            {
                event.preventDefault();

                NodeGestureStartEvent nodeEvent = new NodeGestureStartEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                fireEvent(nodeEvent);
            }
        });
        m_lienzo.addGestureEndHandler(new GestureEndHandler()
        {
            @Override
            public void onGestureEnd(GestureEndEvent event)
            {
                event.preventDefault();

                NodeGestureEndEvent nodeEvent = new NodeGestureEndEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                fireEvent(nodeEvent);
            }
        });
        m_lienzo.addGestureChangeHandler(new GestureChangeHandler()
        {
            @Override
            public void onGestureChange(GestureChangeEvent event)
            {
                event.preventDefault();

                NodeGestureChangeEvent nodeEvent = new NodeGestureChangeEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nodeEvent))
                {
                    return;
                }
                fireEvent(nodeEvent);
            }
        });
    }

    private final Shape<?> findShapeAtPoint(int x, int y)
    {
        return m_lienzo.getViewport().findShapeAtPoint(x, y);
    }

    private final void doDragCancel(INodeXYEvent event)
    {
        if (m_dragging)
        {
            doDragMove(event);

            Cursor cursor = m_lienzo.getNormalCursor();

            if (null == cursor)
            {
                cursor = LienzoGlobals.get().getDefaultNormalCursor();

                if (null == cursor)
                {
                    cursor = m_lienzo.getWidgetCursor();

                    if (null == cursor)
                    {
                        cursor = Cursor.DEFAULT;
                    }
                }
            }
            m_lienzo.setCursor(cursor);

            if (DragMode.DRAG_LAYER == m_drag_mode)
            {
                m_drag_node.setVisible(true);

                m_dragContext.dragDone();

                m_drag_node.getLayer().draw();

                m_lienzo.getDragLayer().clear();
            }
            else
            {
                m_dragContext.dragDone();
            }
            m_drag_node.fireEvent(new NodeDragEndEvent(m_dragContext));

            m_drag_node = null;

            m_drag_mode = null;

            m_dragging = false;

            m_dragging_dispatch_move = false;

            m_dragging_using_touches = false;
        }
    }

    private final void doDragStart(IPrimitive<?> node, INodeXYEvent event)
    {
        if (m_dragging)
        {
            doDragCancel(event);
        }
        Cursor cursor = m_lienzo.getSelectCursor();

        if (null == cursor)
        {
            cursor = LienzoGlobals.get().getDefaultSelectCursor();

            if (null == cursor)
            {
                cursor = Cursor.CROSSHAIR;
            }
        }
        m_lienzo.setCursor(cursor);

        m_drag_node = node;

        m_drag_mode = node.getDragMode();

        m_dragContext = new DragContext(event, node);

        m_drag_node.fireEvent(new NodeDragStartEvent(m_dragContext));

        m_dragging = true;

        if (DragMode.DRAG_LAYER == m_drag_mode)
        {
            m_drag_node.setVisible(false);

            m_dragContext.drawNodeWithTransforms(m_lienzo.getDragLayer().getContext());

            m_drag_node.getLayer().batch();
        }
        m_dragging_dispatch_move = m_drag_node.isEventHandled(NodeDragMoveEvent.getType());

        m_dragging_using_touches = ((event.getNodeEvent().getAssociatedType() == NodeTouchMoveEvent.getType()) || (event.getNodeEvent().getAssociatedType() == NodeTouchStartEvent.getType()));
    }

    private final void doDragMove(INodeXYEvent event)
    {
        m_dragContext.dragUpdate(event);

        if (m_dragging_dispatch_move)
        {
            m_drag_node.fireEvent(new NodeDragMoveEvent(m_dragContext));
        }
        if (DragMode.DRAG_LAYER == m_drag_mode)
        {
            m_lienzo.getDragLayer().draw();

            m_dragContext.drawNodeWithTransforms(m_lienzo.getDragLayer().getContext());
        }
        else
        {
            m_drag_node.getLayer().batch();
        }
    }

    private final void onNodeMouseClick(INodeXYEvent event)
    {
        if (m_dragging_ignore_clicks)
        {
            m_dragging_ignore_clicks = false;

            return;
        }
        IPrimitive<?> prim = findPrimitiveForEvent(event, NodeMouseClickEvent.getType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseDoubleClick(INodeXYEvent event)
    {
        IPrimitive<?> prim = findPrimitiveForEvent(event, NodeMouseDoubleClickEvent.getType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final IPrimitive<?> findPrimitiveForEvent(INodeXYEvent event, Type<?> type)
    {
        IPrimitive<?> find = null;

        Node<?> node = findShapeAtPoint(event.getX(), event.getY());

        while ((null != node) && (node.getNodeType() != NodeType.LAYER))
        {
            IPrimitive<?> prim = node.asPrimitive();

            if ((null != prim) && (prim.isListening()) && (prim.isVisible()) && (prim.isEventHandled(type)))
            {
                find = prim; // find the topmost event matching node, not necessarily the first ancestor
            }
            node = node.getParent();
        }
        return find;
    }

    private final void doPrepareDragging(INodeXYEvent event)
    {
        IPrimitive<?> find = null;

        Node<?> node = findShapeAtPoint(event.getX(), event.getY());

        while ((null != node) && (node.getNodeType() != NodeType.LAYER))
        {
            IPrimitive<?> prim = node.asPrimitive();

            if ((null != prim) && (prim.isDraggable()) && (prim.isListening()) && (prim.isVisible()))
            {
                find = prim; // find the topmost draggable node, not necessarily the first ancestor
            }
            node = node.getParent();
        }
        if (null != find)
        {
            doDragStart(find, event);
        }
    }

    private final void onNodeMouseDown(INodeXYEvent event)
    {
        if (m_dragging_mouse_pressed)
        {
            return;
        }
        if (m_dragging)
        {
            doDragCancel(event);
        }
        m_dragging_mouse_pressed = true;

        IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void doCancelEnterExitShape(INodeXYEvent event)
    {
        if ((null != m_over_prim) && (m_over_prim.isEventHandled(NodeMouseExitEvent.getType())))
        {
            m_over_prim.fireEvent(new NodeMouseExitEvent(event.getX(), event.getY()));
        }
        m_over_prim = null;
    }

    // This will also return the shape under the cursor, for some optimization on Mouse Move

    private final Shape<?> doCheckEnterExitShape(INodeXYEvent event)
    {
        Shape<?> shape = findShapeAtPoint(event.getX(), event.getY());

        if (shape != null)
        {
            IPrimitive<?> prim = shape.asPrimitive();

            if (null != m_over_prim)
            {
                if (prim != m_over_prim)
                {
                    if (m_over_prim.isEventHandled(NodeMouseExitEvent.getType()))
                    {
                        m_over_prim.fireEvent(new NodeMouseExitEvent(event.getX(), event.getY()));
                    }
                }
            }
            if (prim != m_over_prim)
            {
                if ((null != prim) && (prim.isEventHandled(NodeMouseEnterEvent.getType())))
                {
                    prim.fireEvent(new NodeMouseEnterEvent(event.getX(), event.getY()));
                }
                m_over_prim = prim;
            }
        }
        else
        {
            doCancelEnterExitShape(event);
        }
        return shape;
    }

    private final void onNodeMouseMove(INodeXYEvent event)
    {
        if (m_dragging_mouse_pressed)
        {
            if (false == m_dragging)
            {
                doPrepareDragging(event);

                if (false == m_dragging)
                {
                    // Don't pick up any draggable objects along the way - LIENZO-88
                    //
                    // Not sure about this, it may interfere with deferred mouse click handling

                    m_dragging_mouse_pressed = false;
                }
            }
        }
        if (m_dragging)
        {
            doDragMove(event);

            return;
        }
        doCheckEnterExitShape(event);

        IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseUp(INodeXYEvent event)
    {
        m_dragging_mouse_pressed = false;

        if (m_dragging)
        {
            doDragCancel(event);

            m_dragging_ignore_clicks = true;

            return;
        }
        IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseOut(INodeXYEvent event)
    {
        m_dragging_mouse_pressed = false; // in case someone does a pop up ( Window.alert() ), this causes technically a MouseDown cancel

        if (m_dragging)
        {
            doDragCancel(event);
        }
        doCancelEnterExitShape(event);

        fireEvent(event.getNodeEvent());
    }

    private final void onNodeMouseOver(INodeXYEvent event)
    {
        Node<?> node = doCheckEnterExitShape(event);

        if ((null != node) && (node.isListening()) && (node.isVisible()) && (node.isEventHandled(NodeMouseOverEvent.getType())))
        {
            node.fireEvent(event.getNodeEvent());
        }
        fireEvent(event.getNodeEvent());
    }

    private final void fireEvent(GwtEvent<?> event)
    {
        m_lienzo.getViewport().fireEvent(event);
    }
}