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

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.event.EventReceiver;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
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
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelDragLimitEventDetail.LimitDirections;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.ait.lienzo.tools.client.Timer;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import com.ait.lienzo.tools.client.event.INodeEvent;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.AddEventListenerOptions;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.Touch;
import elemental2.dom.TouchEvent;
import elemental2.dom.TouchList;
import elemental2.dom.UIEvent;
import jsinterop.base.Js;

public final class LienzoPanelHandlerManager {

    private static final double DRAG_BOUNDS_INCREMENT = 15;
    private static final int DRAG_TIMER_INTERVAL = 50;

    private final LienzoPanel m_lienzo;

    private final HTMLElement m_lienzoElm;

    private final Viewport m_viewport;

    private final Mediators m_mediators;

    private final HandlerRegistrationManager handlerRegistrationManager;

    private boolean m_dragging = false;

    private boolean m_dragging_using_touches = false;

    private boolean m_dragging_dispatch_move = false;

    private boolean m_dragging_ignore_clicks = false;

    private boolean m_dragging_mouse_pressed = false;

    private boolean m_mouse_button_left = false;

    private boolean m_mouse_button_middle = false;

    private boolean m_mouse_button_right = false;

    private IPrimitive<?> m_over_prim = null;

    private DragMode m_drag_mode = null;

    private int lastDragMoveX;

    private int lastDragMoveY;

    private MouseEvent lastDragMouseEvent;

    private TouchEvent lastDragTouchEvent;

    private IPrimitive<?> m_drag_node = null;

    private Timer dragLimitsTimer;

    private Set<LimitDirections> dragLimitsDirection;

    private DragContext m_dragContext;

    private List<TouchPoint> m_touches = null;

    public LienzoPanelHandlerManager(final LienzoPanel panel) {
        m_lienzo = panel;
        m_lienzoElm = Js.uncheckedCast(panel.getElement());

        m_viewport = m_lienzo.getViewport();

        m_mediators = m_viewport.getMediators();

        handlerRegistrationManager = new HandlerRegistrationManager();
        initializeDragBoundsTimer();
        addHandlers();
    }

    public HTMLElement getHTMLElement() {
        return m_lienzoElm;
    }

    public void destroy() {
        if (null != m_mediators) {
            while (null != m_mediators.pop()) {
            }
        }
        if (null != m_touches) {
            m_touches.clear();
        }
        handlerRegistrationManager.removeHandler();
        m_drag_mode = null;
        m_drag_node = null;
        m_over_prim = null;
        m_dragContext = null;
    }

    private NodeMouseDownEvent nodeMouseDownEvent;
    private NodeMouseMoveEvent nodeMouseMoveEvent;
    private NodeMouseUpEvent nodeMouseUpEvent;

    private NodeMouseClickEvent nodeMouseClickEvent;
    private NodeMouseDoubleClickEvent nodeMouseDoubleClickEvent;

    private NodeMouseEnterEvent nodeMouseEnterEvent;
    private NodeMouseExitEvent nodeMouseExitEvent;

    private NodeMouseOutEvent nodeMouseOutEvent;
    private NodeMouseOverEvent nodeMouseOverEvent;

    private NodeMouseWheelEvent nodeMouseWheelEvent;

    private NodeTouchStartEvent nodeTouchStartEvent;
    private NodeTouchMoveEvent nodeTouchMoveEvent;
    private NodeTouchEndEvent nodeTouchEndEvent;
    private NodeTouchCancelEvent nodeTouchCancelEvent;

    private NodeDragStartEvent nodeDragStartEvent;
    private NodeDragMoveEvent nodeDragMoveEvent;
    private NodeDragEndEvent nodeDragEndEvent;

    private final void addHandlers() {
        // TODO: @FIXME I have tried to copy existing lienzo code for preventDefault and stopPropagation. But we should double check it was correct in the first place.
        // TODO: @FIXME Both the order they are called in, and the instance they are called on event vs nevent. NEvent seems to do very little, maybe a GWTEvent porting error (mdp)
        nodeMouseDownEvent = new NodeMouseDownEvent(m_lienzoElm);
        nodeMouseMoveEvent = new NodeMouseMoveEvent(m_lienzoElm);
        nodeMouseUpEvent = new NodeMouseUpEvent(m_lienzoElm);

        nodeMouseClickEvent = new NodeMouseClickEvent(m_lienzoElm);
        nodeMouseDoubleClickEvent = new NodeMouseDoubleClickEvent(m_lienzoElm);

        nodeMouseEnterEvent = new NodeMouseEnterEvent(m_lienzoElm);
        nodeMouseExitEvent = new NodeMouseExitEvent(m_lienzoElm);

        nodeMouseOutEvent = new NodeMouseOutEvent(m_lienzoElm);
        nodeMouseOverEvent = new NodeMouseOverEvent(m_lienzoElm);

        nodeMouseWheelEvent = new NodeMouseWheelEvent(m_lienzoElm);

        nodeTouchStartEvent = new NodeTouchStartEvent(m_lienzoElm);
        nodeTouchMoveEvent = new NodeTouchMoveEvent(m_lienzoElm);
        nodeTouchEndEvent = new NodeTouchEndEvent(m_lienzoElm);
        nodeTouchCancelEvent = new NodeTouchCancelEvent(m_lienzoElm);

        nodeDragStartEvent = new NodeDragStartEvent(m_lienzoElm);
        nodeDragMoveEvent = new NodeDragMoveEvent(m_lienzoElm);
        nodeDragEndEvent = new NodeDragEndEvent(m_lienzoElm);

        nodeDragStartEvent = new NodeDragStartEvent(m_lienzoElm);

        addEventListener(EventType.CLICKED, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (!m_viewport.getOnEventHandlers().getOnMouseClickEventHandle().onMouseEventBefore(mouseEvent)) {
                m_dragging_mouse_pressed = false; // could have been set previously by a mousedown, it will need cleaning up
                m_dragging_ignore_clicks = false;
                m_dragging = false;
                return;
            }

            onNodeMouseClick(mouseEvent, x, y);

            checkPressedMouseButton(mouseEvent.button);

            m_viewport.getOnEventHandlers().getOnMouseClickEventHandle().onMouseEventAfter(mouseEvent);
        });

        addEventListener(EventType.DOUBLE_CLICKED, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (!m_viewport.getOnEventHandlers().getOnMouseDoubleClickEventHandle().onMouseEventBefore(mouseEvent)) {
                return;
            }

            onNodeMouseDoubleClick(mouseEvent, x, y);

            checkPressedMouseButton(mouseEvent.button);

            event.preventDefault();

            m_viewport.getOnEventHandlers().getOnMouseDoubleClickEventHandle().onMouseEventAfter(mouseEvent);
        });

        addEventListener(EventType.MOUSE_MOVE, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (!m_viewport.getOnEventHandlers().getOnMouseMoveEventHandle().onMouseEventBefore(mouseEvent)) {
                return;
            }

            if ((m_dragging) && (m_dragging_using_touches)) {
                event.preventDefault();

                return;// Ignore weird Mouse Move (0,0) in the middle of a Touch Drag on iOS/Safari
            }

            if (m_mediators.handleEvent(nodeMouseMoveEvent.getAssociatedType(), mouseEvent, x, y)) {
                event.preventDefault();

                return;
            }

            checkPressedMouseButton(mouseEvent.button);

            onNodeMouseMoveTouchMove(mouseEvent, null, x, y, nodeMouseMoveEvent);

            event.preventDefault();

            m_viewport.getOnEventHandlers().getOnMouseMoveEventHandle().onMouseEventAfter(mouseEvent);
        });

        addEventListener(EventType.MOUSE_UP, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (!m_viewport.getOnEventHandlers().getOnMouseUpEventHandle().onMouseEventBefore(mouseEvent)) {
                m_dragging_mouse_pressed = false; // could have been set previously by a mousedown, it will need cleaning up
                m_dragging_ignore_clicks = false;
                m_dragging = false;
                return;
            }

            if (m_mediators.handleEvent(nodeMouseUpEvent.getAssociatedType(), mouseEvent, x, y)) {
                return;
            }

            checkPressedMouseButton(mouseEvent.button);

            onNodeMouseUpTouchEnd(mouseEvent, null, x, y, nodeMouseUpEvent);

            m_viewport.getOnEventHandlers().getOnMouseUpEventHandle().onMouseEventAfter(mouseEvent);
        });

        addEventListener(EventType.MOUSE_DOWN, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (!m_viewport.getOnEventHandlers().getOnMouseDownEventHandle().onMouseEventBefore(mouseEvent)) {
                return;
            }

            if (m_mediators.handleEvent(nodeMouseDownEvent.getAssociatedType(), mouseEvent, x, y)) {
                event.preventDefault();

                return;
            }

            checkPressedMouseButton(mouseEvent.button);

            onNodeMouseDownTouchStart(mouseEvent, null, x, y, nodeMouseDownEvent);

            m_viewport.getOnEventHandlers().getOnMouseDownEventHandle().onMouseEventAfter(mouseEvent);
        });

        addEventListener(EventType.MOUSE_OUT, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeMouseOutEvent.getAssociatedType(), mouseEvent, x, y)) {
                return;
            }

            onNodeMouseOutTouchCancel(mouseEvent, null, x, y, nodeMouseOutEvent);
            //onNodeMouseOut(, x, y);
        });

        addEventListener(EventType.MOUSE_OVER, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeMouseOverEvent.getAssociatedType(), mouseEvent, x, y)) {
                return;
            }

            final Shape<?> shape = doCheckEnterExitShape(mouseEvent, null, x, y);

            if (shape != null) {
                fireEvent(mouseEvent, null, x, y, null, shape, nodeMouseOverEvent);
            }
        });

        AddEventListenerOptions opt = AddEventListenerOptions.create();
        // Cannot be passive otherwise the event will leak and be handled by browser e.g ctrl + wheel
        opt.setPassive(false);

        addEventListener(EventType.MOUSE_WHEEL, (Event event) ->
        {
            MouseEvent mouseEvent = (MouseEvent) event;

            int x = MouseEventUtil.getRelativeX(mouseEvent.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(mouseEvent.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeMouseWheelEvent.getAssociatedType(), mouseEvent, x, y)) {
                event.stopPropagation();
                event.preventDefault();
            } else {
                fireEvent(mouseEvent, null, x, y, null, null, nodeMouseWheelEvent);
            }
        }, opt);

        addEventListener(EventType.TOUCH_END, (Event event) ->
        {
            TouchEvent touchEvent = (TouchEvent) event;

            // @FIXME assuming (double check) touches is not null, and we only look at the first touch, which is also not null (mdp)
            TouchList touches = touchEvent.touches;
            Touch touch = touches.getAt(0);

            int x = MouseEventUtil.getRelativeX(touch.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(touch.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeTouchEndEvent.getAssociatedType(), touchEvent, x, y)) {
                event.preventDefault();

                return;
            }

            onNodeMouseUpTouchEnd(null, touchEvent, x, y, nodeTouchEndEvent);

            event.preventDefault();
        });

        addEventListener(EventType.TOUCH_MOVE, (Event event) ->
        {
            TouchEvent touchEvent = (TouchEvent) event;

            // @FIXME assuming (double check) touches is not null, and we only look at the first touch, which is also not null (mdp)
            TouchList touches = touchEvent.touches;
            Touch touch = touches.getAt(0);

            int x = MouseEventUtil.getRelativeX(touch.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(touch.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeTouchMoveEvent.getAssociatedType(), touchEvent, x, y)) {
                event.preventDefault();

                return;
            }

            onNodeMouseMoveTouchMove(null, touchEvent, x, y, nodeTouchMoveEvent);

            event.preventDefault();
        });

        addEventListener(EventType.TOUCH_START, (Event event) ->
        {
            TouchEvent touchEvent = (TouchEvent) event;
            // @FIXME assuming (double check) touches is not null, and we only look at the first touch, which is also not null (mdp)
            TouchList touches = touchEvent.touches;
            Touch touch = touches.getAt(0);
            int x = MouseEventUtil.getRelativeX(touch.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(touch.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeTouchStartEvent.getAssociatedType(), touchEvent, x, y)) {
                event.preventDefault();

                return;
            }

            onNodeMouseDownTouchStart(null, touchEvent, x, y, nodeTouchStartEvent);

            event.preventDefault();
        });

        addEventListener(EventType.TOUCH_CANCEL, (Event event) ->
        {
            TouchEvent touchEvent = (TouchEvent) event;
            // @FIXME assuming (double check) touches is not null, and we only look at the first touch, which is also not null (mdp)
            TouchList touches = touchEvent.touches;
            Touch touch = touches.getAt(0);
            int x = MouseEventUtil.getRelativeX(touch.clientX, m_lienzoElm);
            int y = MouseEventUtil.getRelativeY(touch.clientY, m_lienzoElm);

            if (m_mediators.handleEvent(nodeTouchCancelEvent.getAssociatedType(), touchEvent, x, y)) {
                event.preventDefault();

                return;
            }

            onNodeMouseOutTouchCancel(null, touchEvent, x, y, nodeTouchCancelEvent);

            event.preventDefault();
        });

        LienzoPanelEvents.addDragLimitsOverEventListener(m_lienzo, (Event event) -> {
            LienzoPanelDragLimitEventDetail detail = LienzoPanelDragLimitEventDetail.getDragLimitDetail(event);
            dragLimitsDirection = detail.getLimitDirection();
            if (!dragLimitsTimer.isRunning()) {
                dragLimitsTimer.scheduleRepeating(DRAG_TIMER_INTERVAL);
            }
        });

        LienzoPanelEvents.addDragLimitsOutEventListener(m_lienzo, (Event event) -> {
            dragLimitsDirection.clear();
            if (dragLimitsTimer.isRunning()) {
                dragLimitsTimer.cancel();
            }
        });

        // TODO: @FIXME Elemental2 does not provide Gesture support, so disabling this for now (mdp)
//        handlerRegistrationManager.register (
//            m_lienzo.addGestureStartHandler(new GestureStartHandler()
//        {
//            @Override
//            public void onGestureStart(final GestureStartEvent event)
//            {
//                    final NodeGestureStartEvent nevent = new NodeGestureStartEvent(event.getScale(), event.getRotation());
//
//                    if (m_mediators.handleEvent(nevent))
//                    {
//                        event.preventDefault();
//
//                        return;
//                    }
//                    fireEvent(nevent);
//
//                    event.preventDefault();
//                }
//            })
//        );
//        handlerRegistrationManager.register (
//            m_lienzo.addGestureEndHandler(new GestureEndHandler()
//        {
//            @Override
//            public void onGestureEnd(final GestureEndEvent event)
//            {
//                    final NodeGestureEndEvent nevent = new NodeGestureEndEvent(event.getScale(), event.getRotation());
//
//                    if (m_mediators.handleEvent(nevent))
//                    {
//                        event.preventDefault();
//
//                        return;
//                    }
//                    fireEvent(nevent);
//
//                    event.preventDefault();
//                }
//            })
//        );
//        handlerRegistrationManager.register (
//            m_lienzo.addGestureChangeHandler(new GestureChangeHandler() {
//                @Override
//                public void onGestureChange(final GestureChangeEvent event)
//                {
//                    final NodeGestureChangeEvent nevent = new NodeGestureChangeEvent(event.getScale(), event.getRotation());
//
//                    if (m_mediators.handleEvent(nevent))
//                    {
//                        event.preventDefault();
//
//                        return;
//                    }
//                    fireEvent(nevent);
//
//                    event.preventDefault();
//                }
//            })
//        );
    }

    private void addEventListener(final EventType eventType, final EventListener listener) {
        m_lienzoElm.addEventListener(eventType.getType(), listener, (AddEventListenerOptions) null);
    }

    private void addEventListener(final EventType eventType, final EventListener listener, AddEventListenerOptions opt) {
        if (opt == null) {
            m_lienzoElm.addEventListener(eventType.getType(), listener);
        } else {
            m_lienzoElm.addEventListener(eventType.getType(), listener, opt);
        }
    }

    private final Shape<?> findShapeAtPoint(final int x, final int y) {
        return m_viewport.findShapeAtPoint(x, y);
    }

    private final void initializeDragBoundsTimer() {
        dragLimitsTimer = new Timer() {
            @Override
            public void run() {
                double offsetX = 0;

                double offsetY = 0;

                if (dragLimitsDirection.contains(LimitDirections.LEFT)) {
                    offsetX = -DRAG_BOUNDS_INCREMENT;
                }
                if (dragLimitsDirection.contains(LimitDirections.RIGHT)) {
                    offsetX = DRAG_BOUNDS_INCREMENT;
                }
                if (dragLimitsDirection.contains(LimitDirections.TOP)) {
                    offsetY = -DRAG_BOUNDS_INCREMENT;
                }
                if (dragLimitsDirection.contains(LimitDirections.DOWN)) {
                    offsetY = DRAG_BOUNDS_INCREMENT;
                }

                doDragOffset(offsetX, offsetY);
            }
        };
    }

    private final void doDragCancel(int x, int y, final MouseEvent mouseEvent, final TouchEvent touchEvent) {
        if (m_dragging) {
            doDragMove(x, y, mouseEvent, touchEvent);

            if (dragLimitsTimer.isRunning()) {
                dragLimitsDirection.clear();
                dragLimitsTimer.cancel();
            }

            // TODO: Cursor stuff   .
            /*Cursor cursor = m_lienzo.getNormalCursor();

            if (null == cursor)
            {
                cursor = LienzoCore.get().getDefaultNormalCursor();

                if (null == cursor)
                {
                    cursor = m_lienzo.getWidgetCursor();

                    if (null == cursor)
                    {
                        cursor = Style.Cursor.DEFAULT;
                    }
                }
            }
            m_lienzo.setCursor(cursor);*/

            fireEvent(mouseEvent, touchEvent, x, y, m_dragContext, m_drag_node.asNode(), nodeDragEndEvent);

            LienzoPanelEvents.firePrimitiveDragEndEvent(m_lienzo, m_drag_node, x, y);

            m_dragContext.dragDone();

            m_drag_node.setDragging(false);

            if (DragMode.DRAG_LAYER == m_drag_mode) {
                m_drag_node.setVisible(true);

                m_drag_node.getLayer().draw();

                m_viewport.getDragLayer().clear();
            }

            m_drag_node = null;

            m_drag_mode = null;

            m_dragging = false;

            m_dragging_dispatch_move = false;

            m_dragging_using_touches = false;
        }
    }

    private final void doDragStart(final int x, final int y, final Node<?> node, final MouseEvent mouseEvent, final TouchEvent touchEvent) {
        if (m_dragging) {
            doDragCancel(x, y, mouseEvent, touchEvent);
        }

        // TODO: Cursor stuff.
        /*Cursor cursor = m_lienzo.getSelectCursor();

        if (null == cursor)
        {
            cursor = LienzoCore.get().getDefaultSelectCursor();

            if (null == cursor)
            {
                cursor = Style.Cursor.CROSSHAIR;
            }
        }
        m_lienzo.setCursor(cursor);*/

        m_drag_node = (IPrimitive<?>) node;

        m_drag_mode = node.getDragMode();

        m_dragContext = new DragContext(x, y, (IPrimitive) node, m_viewport.getTransform().getInverse());

        m_drag_node.setDragging(true);

        LienzoPanelEvents.firePrimitiveDragStartEvent(m_lienzo, m_drag_node, x, y);

        fireEvent(mouseEvent, touchEvent, x, y, m_dragContext, node, nodeDragStartEvent);

        m_dragging = true;

        if (DragMode.DRAG_LAYER == m_drag_mode) {
            m_drag_node.setVisible(false);

            m_drag_node.getLayer().draw();

            m_dragContext.drawNodeWithTransforms(m_viewport.getDragLayer().getContext());
        }
        m_dragging_dispatch_move = m_drag_node.isEventHandled(NodeDragMoveEvent.getType());

        m_dragging_using_touches = touchEvent != null;
    }

    private final void doDragMove(final int x, final int y, final MouseEvent mouseEvent, final TouchEvent touchEvent) {
        lastDragMoveX = x;

        lastDragMoveY = y;

        lastDragMouseEvent = mouseEvent;

        lastDragTouchEvent = touchEvent;

        m_dragContext.dragMoveUpdate(x, y);

        if (dragLimitsDirection != null) {
            dragLimitsDirection.clear();
        }
        if (dragLimitsTimer.isRunning()) {
            dragLimitsTimer.cancel();
        }

        LienzoPanelEvents.firePrimitiveDragMoveUpdateEvent(m_lienzo, m_drag_node, x, y);

        if (m_dragging_dispatch_move) {
            fireEvent(mouseEvent, touchEvent, x, y, m_dragContext, m_drag_node.asNode(), nodeDragMoveEvent);
        }

        if (DragMode.DRAG_LAYER == m_drag_mode) {
            m_viewport.getDragLayer().draw();

            m_dragContext.drawNodeWithTransforms(m_viewport.getDragLayer().getContext());
        } else {
            m_drag_node.getLayer().batch();
        }
    }

    private final void doDragOffset(final double offsetX, final double offsetY) {
        m_dragContext.dragOffsetUpdate(offsetX, offsetY);

        LienzoPanelEvents.firePrimitiveDragOffsetUpdateEvent(m_lienzo, m_drag_node, offsetX, offsetY);

        if (m_dragging_dispatch_move) {
            fireEvent(lastDragMouseEvent,
                      lastDragTouchEvent,
                      lastDragMoveX,
                      lastDragMoveY,
                      m_dragContext,
                      m_drag_node.asNode(),
                      nodeDragMoveEvent);
        }

        if (DragMode.DRAG_LAYER == m_drag_mode) {
            m_viewport.getDragLayer().draw();

            m_dragContext.drawNodeWithTransforms(m_viewport.getDragLayer().getContext());
        } else {
            m_drag_node.getLayer().batch();
        }
    }

    private final void onNodeMouseClick(final MouseEvent event, int x, int y) {
        if (m_dragging_ignore_clicks) {
            m_dragging_ignore_clicks = false;

            return;
        }

        Node<?> node = findPrimitiveForEventType(x, y, NodeMouseClickEvent.getType());
        fireEvent(event, null, x, y, null, node, nodeMouseClickEvent);
    }

    private final void onNodeMouseDoubleClick(final MouseEvent event, int x, int y) {
        Node<?> node = findPrimitiveForEventType(x, y, NodeMouseDoubleClickEvent.getType());
        fireEvent(event, null, x, y, null, node, nodeMouseDoubleClickEvent);
    }

    private final <H extends EventHandler> Node<?> findPrimitiveForEventType(final int x, final int y, final INodeEvent.Type<H> type) {
        return findPrimitiveForPredicate(x, y, prim -> prim.isEventHandled(type));
    }

    private final Node<?> findPrimitiveForPredicate(final int x, final int y, final Predicate<Node<?>> pred) {
        NFastArrayList<Node<?>> list = null;

        EventPropagationMode stop = EventPropagationMode.LAST_ANCESTOR;

        Node<?> node = findShapeAtPoint(x, y);

        while ((null != node) && (null != node.asPrimitive())) {
            if (pred.test(node)) {
                final EventPropagationMode mode = node.getEventPropagationMode();

                if (null == list) {
                    list = new NFastArrayList<>();
                }
                list.add(node);

                if (mode == EventPropagationMode.NO_ANCESTORS) {
                    return node;
                }
                if (mode.getOrder() < stop.getOrder()) {
                    stop = mode;

                    break;
                }
            }
            node = node.getParent();
        }
        if ((list != null) && (!list.isEmpty())) {
            final int size = list.size();

            if (stop == EventPropagationMode.FIRST_ANCESTOR) {
                if (size > 1) {
                    return list.get(1);
                }
            } else {
                if (size > 1) {
                    return list.get(size - 1);
                }
            }
            return list.get(0);
        }
        return null;
    }

    private final void doPrepareDragging(final int x, final int y, final MouseEvent mouseEvent, final TouchEvent touchEvent) {
        final Node<?> find = findPrimitiveForPredicate(x, y, prim -> prim.isDraggable());

        if (null != find) {
            doDragStart(x, y, find, mouseEvent, touchEvent);
        }
    }

    // TODO: lienzo-to-native: create a test case for this, otherwise events for layer are not being fired.
    private <H extends EventHandler, S extends EventReceiver> void fireEvent(final MouseEvent mouseEvent,
                                                                             final TouchEvent touchEvent,
                                                                             final int x,
                                                                             final int y,
                                                                             final DragContext drag, Node<?> node,
                                                                             final AbstractNodeHumanInputEvent<H, S> nodeEvent) {
        boolean canBeHandled;
        if (node == null) {
            node = m_viewport;
            canBeHandled = true;
        } else {
            canBeHandled = node.isListening() && node.isVisible() && node.isEventHandled(nodeEvent.getAssociatedType());
        }

        if (canBeHandled) {
            if (!nodeEvent.isAlive()) {
                nodeEvent.revive();
            }
            S oldNode = nodeEvent.getSource();
            UIEvent oldEvent = nodeEvent.getNativeEvent();
            int oldX = nodeEvent.getX();
            int oldY = nodeEvent.getY();

            try {
                nodeEvent.override((S) node, mouseEvent, touchEvent, x, y, drag);
                node.fireEvent(nodeEvent);
            } finally {
                if (oldNode == null) {
                    nodeEvent.kill();
                } else {
                    if (mouseEvent != null) {
                        nodeEvent.override(oldNode, (MouseEvent) oldEvent, null, oldX, oldY, drag);
                    } else {
                        nodeEvent.override(oldNode, null, (TouchEvent) oldEvent, oldX, oldY, drag);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private final void doCancelEnterExitShape(final MouseEvent mouseEvent, final TouchEvent touchEvent, final int x, int y) {
        if ((null != m_over_prim) && (m_over_prim.isEventHandled(NodeMouseExitEvent.getType()))) {
            fireEvent(mouseEvent, touchEvent, x, y, null, (Node<?>) m_over_prim, nodeMouseExitEvent);
        }
        m_over_prim = null;
    }

    // This will also return the shape under the cursor, for some optimization on Mouse Move
    @SuppressWarnings("unchecked")
    private final Shape<?> doCheckEnterExitShape(final MouseEvent mouseEvent, final TouchEvent touchEvent, final int x, final int y) {
        final Shape<?> shape = findShapeAtPoint(x, y);

        if (shape != null) {
            if (null != m_over_prim) {
                if (shape != m_over_prim) {
                    if (m_over_prim.isEventHandled(NodeMouseExitEvent.getType())) {
                        fireEvent(mouseEvent, touchEvent, x, y, null, (Node<?>) m_over_prim, nodeMouseExitEvent);
                    }
                }
            }

            if (shape != m_over_prim) {
                if ((null != shape) && (shape.isEventHandled(NodeMouseEnterEvent.getType()))) {
                    fireEvent(mouseEvent, touchEvent, x, y, null, (Node<?>) shape, nodeMouseEnterEvent);
                }
                m_over_prim = shape;
            }
        } else {
            doCancelEnterExitShape(mouseEvent, touchEvent, x, y);
        }
        return shape;
    }

    private final void onNodeMouseDownTouchStart(final MouseEvent mouseEvent, final TouchEvent touchEvent, int x, int y, final AbstractNodeHumanInputEvent nodeEvent) {
        if (m_dragging_mouse_pressed) {
            return;
        }
        if (m_dragging) {
            doDragCancel(x, y, mouseEvent, touchEvent);
        }

        if (m_viewport.getDragMouseButtons().allowDrag(m_mouse_button_left, m_mouse_button_middle, m_mouse_button_right)) {
            m_dragging_mouse_pressed = true;
        }

        Node<?> node = findPrimitiveForEventType(x, y, nodeEvent.getAssociatedType());

        fireEvent(mouseEvent, touchEvent, x, y, null, node, nodeEvent);
    }

    private final void onNodeMouseMoveTouchMove(final MouseEvent mouseEvent, final TouchEvent touchEvent, int x, int y, final AbstractNodeHumanInputEvent nodeEvent) {
        if (m_dragging_mouse_pressed) {
            if (!m_dragging) {
                doPrepareDragging(x, y, mouseEvent, touchEvent);

                if (!m_dragging) {
                    // Don't pick up any draggable objects along the way - LIENZO-88
                    // Not sure about this, it may interfere with deferred mouse click handling
                    m_dragging_mouse_pressed = false;
                }
            }
        }
        if (m_dragging) {
            doDragMove(x, y, mouseEvent, touchEvent);

            return;
        }
        doCheckEnterExitShape(mouseEvent, touchEvent, x, y);

        Node<?> node = findPrimitiveForEventType(x, y, nodeEvent.getAssociatedType());
        fireEvent(mouseEvent, touchEvent, x, y, null, node, nodeEvent);
    }

    private final void onNodeMouseUpTouchEnd(final MouseEvent mouseEvent, final TouchEvent touchEvent, int x, int y, final AbstractNodeHumanInputEvent nodeEvent) {
        m_dragging_mouse_pressed = false;

        if (m_dragging) {
            doDragCancel(x, y, mouseEvent, touchEvent);

            m_dragging_ignore_clicks = true;

            return;
        }
        Node<?> node = findPrimitiveForEventType(x, y, nodeEvent.getAssociatedType());
        fireEvent(mouseEvent, touchEvent, x, y, null, node, nodeEvent);
    }

    private final void onNodeMouseOutTouchCancel(final MouseEvent mouseEvent, final TouchEvent touchEvent, int x, int y, final AbstractNodeHumanInputEvent nodeEvent) {
        m_dragging_mouse_pressed = false;// in case someone does a pop up ( Window.alert() ), this causes technically a MouseDown cancel

        if (m_dragging) {
            doDragCancel(x, y, mouseEvent, touchEvent);
        }
        doCancelEnterExitShape(mouseEvent, touchEvent, x, y);

        Node<?> node = findPrimitiveForEventType(x, y, nodeEvent.getAssociatedType());
        fireEvent(mouseEvent, touchEvent, x, y, null, node, nodeEvent); // @FIXME was the only ever meant to fire on scene->layers? and not nodes?
    }

    /**
     * Stores state of pressed mouse button
     *
     * @param nativeButtonCode
     */
    private void checkPressedMouseButton(final int nativeButtonCode) {
        m_mouse_button_left = nativeButtonCode == MouseEventUtil.BUTTON_LEFT;
        m_mouse_button_middle = nativeButtonCode == MouseEventUtil.BUTTON_MIDDLE;
        m_mouse_button_right = nativeButtonCode == MouseEventUtil.BUTTON_RIGHT;
    }
}