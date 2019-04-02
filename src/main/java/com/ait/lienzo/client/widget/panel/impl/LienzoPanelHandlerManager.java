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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.tooling.common.api.java.util.function.Predicate;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the center for all canvas events.
 * <p/>
 * It can not and should not be used directly as it is used internally by the toolkit to handle and dispatch {@link
 * com.ait.lienzo.client.core.shape.Shape} events.
 */
public final class LienzoPanelHandlerManager
{
    private final LienzoPanelImpl            m_lienzo;

    private final Viewport                   m_viewport;

    private final Mediators                  m_mediators;

    private final HandlerRegistrationManager handlerRegistrationManager;

    private boolean       m_dragging               = false;

    private boolean       m_dragging_using_touches = false;

    private boolean       m_dragging_dispatch_move = false;

    private boolean       m_dragging_ignore_clicks = false;

    private boolean       m_dragging_mouse_pressed = false;

    private boolean       m_mouse_button_left      = false;

    private boolean       m_mouse_button_middle    = false;

    private boolean       m_mouse_button_right     = false;

    private DragMode      m_drag_mode              = null;

    private IPrimitive<?> m_drag_node              = null;

    private IPrimitive<?> m_over_prim              = null;

    private DragContext m_dragContext;

    private List<TouchPoint> m_touches = null;

    public LienzoPanelHandlerManager(final LienzoPanelImpl lienzo)
    {
        m_lienzo = lienzo;

        m_viewport = m_lienzo.getViewport();

        m_mediators = m_viewport.getMediators();

        handlerRegistrationManager = new HandlerRegistrationManager();
        addHandlers();
    }

    public void destroy()
    {
        if (null != m_mediators)
        {
            while (null != m_mediators.pop())
            {
            }
        }
        if (null != m_touches)
        {
            m_touches.clear();
        }
        handlerRegistrationManager.removeHandler();
        m_drag_mode = null;
        m_drag_node = null;
        m_over_prim = null;
        m_dragContext = null;
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
        handlerRegistrationManager.register(
                m_lienzo.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        if (!m_viewport.getOnEventHandlers().getOnMouseClickEventHandle().onMouseEventBefore(event))
                        {
                            m_dragging_mouse_pressed = false; // could have been set previously by a mousedown, it will need cleaning up
                            m_dragging_ignore_clicks = false;
                            m_dragging = false;
                            return;
                        }

                        onNodeMouseClick(new NodeMouseClickEvent(event));
                        checkPressedMouseButton(event.getNativeButton());

                        m_viewport.getOnEventHandlers().getOnMouseClickEventHandle().onMouseEventAfter(event);
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addDoubleClickHandler(new DoubleClickHandler()
                {
                    @Override
                    public void onDoubleClick(final DoubleClickEvent event)
                    {
                        if (!m_viewport.getOnEventHandlers().getOnMouseDoubleClickEventHandle().onMouseEventBefore(event))
                        {
                            return;
                        }

                        onNodeMouseDoubleClick(new NodeMouseDoubleClickEvent(event));
                        checkPressedMouseButton(event.getNativeButton());

                        event.preventDefault();

                        m_viewport.getOnEventHandlers().getOnMouseDoubleClickEventHandle().onMouseEventBefore(event);
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addMouseMoveHandler(new MouseMoveHandler()
                {
                    @Override
                    public void onMouseMove(final MouseMoveEvent event)
                    {
                        if (!m_viewport.getOnEventHandlers().getOnMouseMoveEventHandle().onMouseEventBefore(event))
                        {
                            return;
                        }

                        if ((m_dragging) && (m_dragging_using_touches))
                        {
                            event.preventDefault();

                            return;// Ignore weird Mouse Move (0,0) in the middle of a Touch Drag on iOS/Safari
                        }
                        final NodeMouseMoveEvent nevent = new NodeMouseMoveEvent(event);

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        checkPressedMouseButton(event.getNativeButton());

                        onNodeMouseMove(nevent);

                        event.preventDefault();

                        m_viewport.getOnEventHandlers().getOnMouseMoveEventHandle().onMouseEventBefore(event);
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addMouseUpHandler(new MouseUpHandler()
                {
                    @Override
                    public void onMouseUp(final MouseUpEvent event)
                    {
                        if (!m_viewport.getOnEventHandlers().getOnMouseUpEventHandle().onMouseEventBefore(event))
                        {
                            m_dragging_mouse_pressed = false; // could have been set previously by a mousedown, it will need cleaning up
                            m_dragging_ignore_clicks = false;
                            m_dragging = false;
                            return;
                        }

                        final NodeMouseUpEvent nevent = new NodeMouseUpEvent(event);

                        if (m_mediators.handleEvent(nevent))
                        {
                            return;
                        }

                        checkPressedMouseButton(event.getNativeButton());

                        onNodeMouseUp(nevent);

                        m_viewport.getOnEventHandlers().getOnMouseUpEventHandle().onMouseEventBefore(event);
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addMouseDownHandler(new MouseDownHandler()
                {
                    @Override
                    public void onMouseDown(final MouseDownEvent event)
                    {
                        if (!m_viewport.getOnEventHandlers().getOnMouseDownEventHandle().onMouseEventBefore(event))
                        {
                            return;
                        }

                        final NodeMouseDownEvent nevent = new NodeMouseDownEvent(event);

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }

                        checkPressedMouseButton(event.getNativeButton());

                        onNodeMouseDown(nevent);

                        m_viewport.getOnEventHandlers().getOnMouseDownEventHandle().onMouseEventBefore(event);
                    }
                })
                                           );
        handlerRegistrationManager.register(
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
                })
                                           );
        handlerRegistrationManager.register(
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
                })
                                           );
        handlerRegistrationManager.register(
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
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addTouchCancelHandler(new TouchCancelHandler()
                {
                    @Override
                    public void onTouchCancel(final TouchCancelEvent event)
                    {
                        final NodeTouchCancelEvent nevent = new NodeTouchCancelEvent(event, getTouches(event));

                        if (m_mediators.handleEvent(event))
                        {
                            event.preventDefault();

                            return;
                        }
                        onNodeMouseOut(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addTouchEndHandler(new TouchEndHandler()
                {
                    @Override
                    public void onTouchEnd(final TouchEndEvent event)
                    {
                        final NodeTouchEndEvent nevent = new NodeTouchEndEvent(event, m_touches);

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        onNodeMouseUp(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addTouchMoveHandler(new TouchMoveHandler()
                {
                    @Override
                    public void onTouchMove(final TouchMoveEvent event)
                    {
                        m_touches = getTouches(event);

                        final NodeTouchMoveEvent nevent = new NodeTouchMoveEvent(event, m_touches);

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        onNodeMouseMove(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addTouchStartHandler(new TouchStartHandler()
                {
                    @Override
                    public void onTouchStart(final TouchStartEvent event)
                    {
                        m_touches = getTouches(event);

                        final NodeTouchStartEvent nevent = new NodeTouchStartEvent(event, m_touches);

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        onNodeMouseDown(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addGestureStartHandler(new GestureStartHandler()
                {
                    @Override
                    public void onGestureStart(final GestureStartEvent event)
                    {
                        final NodeGestureStartEvent nevent = new NodeGestureStartEvent(event.getScale(), event.getRotation());

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        fireEvent(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addGestureEndHandler(new GestureEndHandler()
                {
                    @Override
                    public void onGestureEnd(final GestureEndEvent event)
                    {
                        final NodeGestureEndEvent nevent = new NodeGestureEndEvent(event.getScale(), event.getRotation());

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        fireEvent(nevent);

                        event.preventDefault();
                    }
                })
                                           );
        handlerRegistrationManager.register(
                m_lienzo.addGestureChangeHandler(new GestureChangeHandler()
                {
                    @Override
                    public void onGestureChange(final GestureChangeEvent event)
                    {
                        final NodeGestureChangeEvent nevent = new NodeGestureChangeEvent(event.getScale(), event.getRotation());

                        if (m_mediators.handleEvent(nevent))
                        {
                            event.preventDefault();

                            return;
                        }
                        fireEvent(nevent);

                        event.preventDefault();
                    }
                })
                                           );
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

            m_lienzo.setCursor(LienzoCore.get().getDefaultNormalCursor());

            m_drag_node.fireEvent(new NodeDragEndEvent(m_dragContext));

            m_dragContext.dragDone();

            m_drag_node.setDragging(false);

            if (DragMode.DRAG_LAYER == m_drag_mode)
            {
                m_drag_node.setVisible(true);

                m_drag_node.getLayer().draw();

                m_lienzo.getDragLayer().clear();
            }

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

        m_lienzo.setCursor(LienzoCore.get().getDefaultSelectCursor());

        m_drag_node = node;

        m_drag_mode = node.getDragMode();

        m_dragContext = new DragContext(event, node, m_viewport.getTransform().getInverse());

        m_drag_node.setDragging(true);

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
        fireEventForPrimitive(findPrimitiveForEventType(event, NodeMouseClickEvent.getType()), event);
    }

    private final void onNodeMouseDoubleClick(final INodeXYEvent event)
    {
        fireEventForPrimitive(findPrimitiveForEventType(event, NodeMouseDoubleClickEvent.getType()), event);
    }

    private final IPrimitive<?> findPrimitiveForEventType(final INodeXYEvent event, final Type<?> type)
    {
        return findPrimitiveForPredicate(event, new Predicate<IPrimitive<?>>()
        {
            @Override
            public boolean test(final IPrimitive<?> prim)
            {
                return prim.isEventHandled(type);
            }
        });
    }

    private final IPrimitive<?> findPrimitiveForPredicate(final INodeXYEvent event, final Predicate<IPrimitive<?>> pred)
    {
        NFastArrayList<IPrimitive<?>> list = null;

        EventPropagationMode stop = EventPropagationMode.LAST_ANCESTOR;

        Node<?> node = findShapeAtPoint(event.getX(), event.getY());

        while ((null != node) && (null != node.asPrimitive()))
        {
            final IPrimitive<?> prim = node.asPrimitive();

            if (pred.test(prim))
            {
                final EventPropagationMode mode = prim.getEventPropagationMode();

                if (null == list)
                {
                    list = new NFastArrayList<IPrimitive<?>>();
                }
                list.add(prim);

                if (mode == EventPropagationMode.NO_ANCESTORS)
                {
                    return prim;
                }
                if (mode.getOrder() < stop.getOrder())
                {
                    stop = mode;

                    break;
                }
            }
            node = node.getParent();
        }
        if ((list != null) && (list.isEmpty() == false))
        {
            final int size = list.size();

            if (stop == EventPropagationMode.FIRST_ANCESTOR)
            {
                if (size > 1)
                {
                    return list.get(1);
                }
            }
            else
            {
                if (size > 1)
                {
                    return list.get(size - 1);
                }
            }
            return list.get(0);
        }
        return null;
    }

    private final void doPrepareDragging(final INodeXYEvent event)
    {
        final IPrimitive<?> find = findPrimitiveForPredicate(event, new Predicate<IPrimitive<?>>()
        {
            @Override
            public boolean test(final IPrimitive<?> prim)
            {
                return prim.isDraggable();
            }
        });
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
        if (m_lienzo.getDragMouseButtons().allowDrag(m_mouse_button_left, m_mouse_button_middle, m_mouse_button_right))
        {
            m_dragging_mouse_pressed = true;
        }

        fireEventForPrimitive(findPrimitiveForEventType(event, event.getNodeEvent().getAssociatedType()), event);
    }

    @SuppressWarnings("unchecked")
    private final void doCancelEnterExitShape(final INodeXYEvent event)
    {
        if ((null != m_over_prim) && (m_over_prim.isEventHandled(NodeMouseExitEvent.getType())))
        {
            if (event instanceof AbstractNodeHumanInputEvent)
            {
                m_over_prim.fireEvent(new NodeMouseExitEvent(((AbstractNodeHumanInputEvent<MouseEvent<?>, ?>) event).getHumanInputEvent(), event.getX(), event.getY()));
            }
            else
            {
                m_over_prim.fireEvent(new NodeMouseExitEvent(null, event.getX(), event.getY()));
            }
        }
        m_over_prim = null;
    }

    // This will also return the shape under the cursor, for some optimization on Mouse Move

    @SuppressWarnings("unchecked")
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
                        if (event instanceof AbstractNodeHumanInputEvent)
                        {
                            m_over_prim.fireEvent(new NodeMouseExitEvent(((AbstractNodeHumanInputEvent<MouseEvent<?>, ?>) event).getHumanInputEvent(), x, y));
                        }
                        else
                        {
                            m_over_prim.fireEvent(new NodeMouseExitEvent(null, x, y));
                        }
                    }
                }
            }
            if (prim != m_over_prim)
            {
                if ((null != prim) && (prim.isEventHandled(NodeMouseEnterEvent.getType())))
                {
                    if (event instanceof AbstractNodeHumanInputEvent)
                    {
                        prim.fireEvent(new NodeMouseEnterEvent(((AbstractNodeHumanInputEvent<MouseEvent<?>, ?>) event).getHumanInputEvent(), x, y));
                    }
                    else
                    {
                        prim.fireEvent(new NodeMouseEnterEvent(null, x, y));
                    }
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

        fireEventForPrimitive(findPrimitiveForEventType(event, event.getNodeEvent().getAssociatedType()), event);
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
        fireEventForPrimitive(findPrimitiveForEventType(event, event.getNodeEvent().getAssociatedType()), event);
    }

    private final void onNodeMouseOut(final INodeXYEvent event)
    {
        m_dragging_mouse_pressed = false;// in case someone does a pop up ( Window.alert() ), this causes technically a MouseDown cancel

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

    private final void fireEventForPrimitive(final IPrimitive<?> prim, final INodeXYEvent event)
    {
        if (null != prim)
        {
            prim.fireEvent(event.getNodeEvent());
        }
        else
        {
            fireEvent(event.getNodeEvent());
        }
    }

    private final void fireEvent(final GwtEvent<?> event)
    {
        m_viewport.fireEvent(event);
    }

    /**
     * Stores state of pressed mouse button
     *
     * @param nativeButtonCode
     */
    private void checkPressedMouseButton(final int nativeButtonCode)
    {
        m_mouse_button_left = nativeButtonCode == NativeEvent.BUTTON_LEFT;
        m_mouse_button_middle = nativeButtonCode == NativeEvent.BUTTON_MIDDLE;
        m_mouse_button_right = nativeButtonCode == NativeEvent.BUTTON_RIGHT;
    }
}