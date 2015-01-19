/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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
import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.config.LienzoCore;
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
import com.ait.lienzo.client.core.shape.Viewport;
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

    private final Viewport    m_viewport;

    private final Mediators   m_mediators;

    private boolean           m_dragging               = false;

    private boolean           m_dragging_using_touches = false;

    private boolean           m_dragging_dispatch_move = false;

    private boolean           m_dragging_ignore_clicks = false;

    private boolean           m_dragging_mouse_pressed = false;

    private DragMode          m_drag_mode              = null;

    private IPrimitive<?>     m_drag_node              = null;

    private IPrimitive<?>     m_over_prim              = null;

    private DragContext       m_dragContext;

    private List<TouchPoint>  m_touches                = null;

    public LienzoHandlerManager(final LienzoPanel lienzo)
    {
        m_lienzo = lienzo;

        m_viewport = m_lienzo.getViewport();

        m_mediators = m_viewport.getMediators();

        addHandlers();
    }

    private final List<TouchPoint> getTouches(final TouchEvent<?> event)
    {
        final JsArray<Touch> jsarray = event.getTouches();

        final Element element = event.getRelativeElement();

        if ((null != jsarray) && (jsarray.length() > 0))
        {
            final int size = jsarray.length();

            final ArrayList<TouchPoint> touches = new ArrayList<TouchPoint>(size);

            for (int i = 0; i < size; i++)
            {
                final Touch touch = jsarray.get(i);

                touches.add(new TouchPoint(touch.getRelativeX(element), touch.getRelativeY(element)));
            }
            return touches;
        }
        else
        {
            int x = event.getNativeEvent().getClientX() - element.getAbsoluteLeft() + element.getScrollLeft() + element.getOwnerDocument().getScrollLeft();

            int y = event.getNativeEvent().getClientY() - element.getAbsoluteTop() + element.getScrollTop() + element.getOwnerDocument().getScrollTop();

            return Arrays.asList(new TouchPoint(x, y));
        }
    }

    private final void addHandlers()
    {
        m_lienzo.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                event.preventDefault();

                onNodeMouseClick(new NodeMouseClickEvent(event));
            }
        });
        m_lienzo.addDoubleClickHandler(new DoubleClickHandler()
        {
            @Override
            public void onDoubleClick(final DoubleClickEvent event)
            {
                event.preventDefault();

                onNodeMouseDoubleClick(new NodeMouseDoubleClickEvent(event));
            }
        });
        m_lienzo.addMouseMoveHandler(new MouseMoveHandler()
        {
            @Override
            public void onMouseMove(final MouseMoveEvent event)
            {
                event.preventDefault();

                if ((m_dragging) && (m_dragging_using_touches))
                {
                    return; // Ignore weird Mouse Move (0,0) in the middle of a Touch Drag on iOS/Safari
                }
                final NodeMouseMoveEvent nevent = new NodeMouseMoveEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseMove(nevent);
            }
        });
        m_lienzo.addMouseUpHandler(new MouseUpHandler()
        {
            @Override
            public void onMouseUp(final MouseUpEvent event)
            {
                final NodeMouseUpEvent nevent = new NodeMouseUpEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseUp(nevent);
            }
        });
        m_lienzo.addMouseDownHandler(new MouseDownHandler()
        {
            @Override
            public void onMouseDown(final MouseDownEvent event)
            {
                event.preventDefault();

                final NodeMouseDownEvent nevent = new NodeMouseDownEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseDown(nevent);
            }
        });
        m_lienzo.addMouseOutHandler(new MouseOutHandler()
        {
            @Override
            public void onMouseOut(final MouseOutEvent event)
            {
                final NodeMouseOutEvent nevent = new NodeMouseOutEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseOut(nevent);
            }
        });
        m_lienzo.addMouseOverHandler(new MouseOverHandler()
        {
            @Override
            public void onMouseOver(final MouseOverEvent event)
            {
                final NodeMouseOverEvent nevent = new NodeMouseOverEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseOver(nevent);
            }
        });
        m_lienzo.addMouseWheelHandler(new MouseWheelHandler()
        {
            @Override
            public void onMouseWheel(final MouseWheelEvent event)
            {
                final NodeMouseWheelEvent nevent = new NodeMouseWheelEvent(event);

                if (m_mediators.handleEvent(nevent))
                {
                    event.preventDefault();

                    event.stopPropagation();
                }
                else
                {
                    fireEvent(nevent);
                }
            }
        });
        m_lienzo.addTouchCancelHandler(new TouchCancelHandler()
        {
            @Override
            public void onTouchCancel(final TouchCancelEvent event)
            {
                event.preventDefault();

                final NodeTouchCancelEvent nevent = new NodeTouchCancelEvent(event, getTouches(event));

                if (m_mediators.handleEvent(event))
                {
                    return;
                }
                onNodeMouseOut(nevent);
            }
        });
        m_lienzo.addTouchEndHandler(new TouchEndHandler()
        {
            @Override
            public void onTouchEnd(final TouchEndEvent event)
            {
                event.preventDefault();

                final NodeTouchEndEvent nevent = new NodeTouchEndEvent(event, m_touches);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseUp(nevent);
            }
        });
        m_lienzo.addTouchMoveHandler(new TouchMoveHandler()
        {
            @Override
            public void onTouchMove(final TouchMoveEvent event)
            {
                event.preventDefault();

                m_touches = getTouches(event);

                final NodeTouchMoveEvent nevent = new NodeTouchMoveEvent(event, m_touches);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseMove(nevent);
            }
        });
        m_lienzo.addTouchStartHandler(new TouchStartHandler()
        {
            @Override
            public void onTouchStart(final TouchStartEvent event)
            {
                event.preventDefault();

                m_touches = getTouches(event);

                final NodeTouchStartEvent nevent = new NodeTouchStartEvent(event, m_touches);

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                onNodeMouseDown(nevent);
            }
        });
        m_lienzo.addGestureStartHandler(new GestureStartHandler()
        {
            @Override
            public void onGestureStart(final GestureStartEvent event)
            {
                event.preventDefault();

                final NodeGestureStartEvent nevent = new NodeGestureStartEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                fireEvent(nevent);
            }
        });
        m_lienzo.addGestureEndHandler(new GestureEndHandler()
        {
            @Override
            public void onGestureEnd(final GestureEndEvent event)
            {
                event.preventDefault();

                final NodeGestureEndEvent nevent = new NodeGestureEndEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                fireEvent(nevent);
            }
        });
        m_lienzo.addGestureChangeHandler(new GestureChangeHandler()
        {
            @Override
            public void onGestureChange(final GestureChangeEvent event)
            {
                event.preventDefault();

                final NodeGestureChangeEvent nevent = new NodeGestureChangeEvent(event.getScale(), event.getRotation());

                if (m_mediators.handleEvent(nevent))
                {
                    return;
                }
                fireEvent(nevent);
            }
        });
    }

    private final Shape<?> findShapeAtPoint(final int x, final int y)
    {
        return m_viewport.findShapeAtPoint(x, y);
    }

    private final void doDragCancel(final INodeXYEvent event)
    {
        if (m_dragging)
        {
            doDragMove(event);

            Cursor cursor = m_lienzo.getNormalCursor();

            if (null == cursor)
            {
                cursor = LienzoCore.get().getDefaultNormalCursor();

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

    private final void doDragStart(final IPrimitive<?> node, final INodeXYEvent event)
    {
        if (m_dragging)
        {
            doDragCancel(event);
        }
        Cursor cursor = m_lienzo.getSelectCursor();

        if (null == cursor)
        {
            cursor = LienzoCore.get().getDefaultSelectCursor();

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

            m_drag_node.getLayer().draw();

            m_dragContext.drawNodeWithTransforms(m_lienzo.getDragLayer().getContext());
        }
        m_dragging_dispatch_move = m_drag_node.isEventHandled(NodeDragMoveEvent.getType());

        m_dragging_using_touches = ((event.getNodeEvent().getAssociatedType() == NodeTouchMoveEvent.getType()) || (event.getNodeEvent().getAssociatedType() == NodeTouchStartEvent.getType()));
    }

    private final void doDragMove(final INodeXYEvent event)
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

    private final void onNodeMouseClick(final INodeXYEvent event)
    {
        if (m_dragging_ignore_clicks)
        {
            m_dragging_ignore_clicks = false;

            return;
        }
        final IPrimitive<?> prim = findPrimitiveForEvent(event, NodeMouseClickEvent.getType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseDoubleClick(final INodeXYEvent event)
    {
        final IPrimitive<?> prim = findPrimitiveForEvent(event, NodeMouseDoubleClickEvent.getType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final IPrimitive<?> findPrimitiveForEvent(final INodeXYEvent event, final Type<?> type)
    {
        IPrimitive<?> find = null;

        Node<?> node = findShapeAtPoint(event.getX(), event.getY());

        while ((null != node) && (node.getNodeType() != NodeType.LAYER))
        {
            final IPrimitive<?> prim = node.asPrimitive();

            if ((null != prim) && (prim.isListening()) && (prim.isVisible()) && (prim.isEventHandled(type)))
            {
                find = prim; // find the topmost event matching node, not necessarily the first ancestor
            }
            node = node.getParent();
        }
        return find;
    }

    private final void doPrepareDragging(final INodeXYEvent event)
    {
        IPrimitive<?> find = null;

        Node<?> node = findShapeAtPoint(event.getX(), event.getY());

        while ((null != node) && (node.getNodeType() != NodeType.LAYER))
        {
            final IPrimitive<?> prim = node.asPrimitive();

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

    private final void onNodeMouseDown(final INodeXYEvent event)
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

        final IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void doCancelEnterExitShape(final INodeXYEvent event)
    {
        if ((null != m_over_prim) && (m_over_prim.isEventHandled(NodeMouseExitEvent.getType())))
        {
            m_over_prim.fireEvent(new NodeMouseExitEvent(null, event.getX(), event.getY()));
        }
        m_over_prim = null;
    }

    // This will also return the shape under the cursor, for some optimization on Mouse Move

    private final Shape<?> doCheckEnterExitShape(final INodeXYEvent event)
    {
        final int x = event.getX();

        final int y = event.getY();

        final Shape<?> shape = findShapeAtPoint(x, y);

        if (shape != null)
        {
            final IPrimitive<?> prim = shape.asPrimitive();

            if (null != m_over_prim)
            {
                if (prim != m_over_prim)
                {
                    if (m_over_prim.isEventHandled(NodeMouseExitEvent.getType()))
                    {
                        m_over_prim.fireEvent(new NodeMouseExitEvent(null, x, y));
                    }
                }
            }
            if (prim != m_over_prim)
            {
                if ((null != prim) && (prim.isEventHandled(NodeMouseEnterEvent.getType())))
                {
                    prim.fireEvent(new NodeMouseEnterEvent(null, x, y));
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

    private final void onNodeMouseMove(final INodeXYEvent event)
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

        final IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseUp(final INodeXYEvent event)
    {
        m_dragging_mouse_pressed = false;

        if (m_dragging)
        {
            doDragCancel(event);

            m_dragging_ignore_clicks = true;

            return;
        }
        final IPrimitive<?> prim = findPrimitiveForEvent(event, event.getNodeEvent().getAssociatedType());

        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void onNodeMouseOut(final INodeXYEvent event)
    {
        m_dragging_mouse_pressed = false; // in case someone does a pop up ( Window.alert() ), this causes technically a MouseDown cancel

        if (m_dragging)
        {
            doDragCancel(event);
        }
        doCancelEnterExitShape(event);

        fireEvent(event.getNodeEvent());
    }

    private final void onNodeMouseOver(final INodeXYEvent event)
    {
        final Node<?> node = doCheckEnterExitShape(event);

        if ((null != node) && (node.isListening()) && (node.isVisible()) && (node.isEventHandled(NodeMouseOverEvent.getType())))
        {
            node.fireEvent(event.getNodeEvent());
        }
        fireEvent(event.getNodeEvent());
    }

    private final void fireEvent(final GwtEvent<?> event)
    {
        m_viewport.fireEvent(event);
    }
}