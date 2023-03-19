package com.ait.lienzo.client.widget.panel.impl;

import java.util.EnumSet;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelDragLimitEventDetail.LimitDirections;
import elemental2.dom.CustomEvent;
import elemental2.dom.EventListener;

// TODO: Use singleton instance for each event type.
public class LienzoPanelEvents {

    static final String LIENZO_PANEL_BOUNDS_CHANGED_EVENT = "lienzoPanelBoundsChangedEvent";
    static final String LIENZO_PANEL_RESIZE_EVENT = "lienzoPanelResizeEvent";
    static final String LIENZO_PANEL_SCALE_EVENT = "lienzoPanelScaleEvent";
    static final String LIENZO_PANEL_SCROLL_EVENT = "lienzoPanelScrollEvent";
    static final String LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT = "lienzoPanelPrimitiveDragStartEvent";
    static final String LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT = "lienzoPanelPrimitiveDragMoveUpdateEvent";
    static final String LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT = "lienzoPanelPrimitiveDragOffsetUpdateEvent";
    static final String LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT = "lienzoPanelPrimitiveDragEndEvent";
    static final String LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT = "lienzoPanelDragLimitsOverEvent";
    static final String LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT = "lienzoPanelDragLimitsOutEvent";

    static void addBoundsChangedEventListener(LienzoPanel panel,
                                              EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_BOUNDS_CHANGED_EVENT, eventListener);
    }

    static void removeBoundsChangedEventListener(LienzoPanel panel,
                                                 EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_BOUNDS_CHANGED_EVENT, eventListener);
    }

    static void fireBoundsChangedEvent(LienzoPanel panel) {
        fireCustomEvent(LIENZO_PANEL_BOUNDS_CHANGED_EVENT, panel);
    }

    static void addResizeEventListener(LienzoPanel panel,
                                       EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_RESIZE_EVENT, eventListener);
    }

    static void removeResizeEventListener(LienzoPanel panel,
                                          EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_RESIZE_EVENT, eventListener);
    }

    static void fireResizeEvent(LienzoPanel panel) {
        fireCustomEvent(LIENZO_PANEL_RESIZE_EVENT, panel);
    }

    static void addScaleEventListener(LienzoPanel panel,
                                      EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_SCALE_EVENT, eventListener);
    }

    static void removeScaleEventListener(LienzoPanel panel,
                                         EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_SCALE_EVENT, eventListener);
    }

    static void fireScaleEvent(LienzoPanel panel) {
        fireCustomEvent(LIENZO_PANEL_SCALE_EVENT, panel);
    }

    static void addScrollEventListener(LienzoPanel panel,
                                       EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_SCROLL_EVENT, eventListener);
    }

    static void removeScrollEventListener(LienzoPanel panel,
                                          EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_SCROLL_EVENT, eventListener);
    }

    static void fireScrollEvent(LienzoPanel panel,
                                double px,
                                double py) {
        LienzoPanelScrollEventDetail detail = new LienzoPanelScrollEventDetail(panel, px, py);
        fireCustomEvent(LIENZO_PANEL_SCROLL_EVENT, panel, detail);
    }

    static void addPrimitiveDragStartEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                   EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT, eventListener);
    }

    static void removePrimitiveDragStartEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                      EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT, eventListener);
    }

    static void firePrimitiveDragStartEvent(LienzoPanel<? extends LienzoPanel> panel,
                                            IPrimitive<?> primitive,
                                            double dragX,
                                            double dragY) {
        LienzoPanelPrimitiveDragEventDetail detail = new LienzoPanelPrimitiveDragEventDetail(panel, primitive, dragX, dragY);
        fireCustomEvent(LIENZO_PANEL_PRIMITIVE_DRAG_START_EVENT, panel, detail);
    }

    static void addPrimitiveDragMoveUpdateEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                        EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT, eventListener);
    }

    static void removePrimitiveDragMoveUpdateEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                           EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT, eventListener);
    }

    static void firePrimitiveDragMoveUpdateEvent(LienzoPanel<? extends LienzoPanel> panel,
                                                 IPrimitive<?> primitive,
                                                 double dragX,
                                                 double dragY) {
        LienzoPanelPrimitiveDragEventDetail detail = new LienzoPanelPrimitiveDragEventDetail(panel, primitive, dragX, dragY);
        fireCustomEvent(LIENZO_PANEL_PRIMITIVE_DRAG_MOVE_UPDATE_EVENT, panel, detail);
    }

    static void addPrimitiveDragOffsetUpdateEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                          EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT, eventListener);
    }

    static void removePrimitiveDragOffsetUpdateEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                             EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT, eventListener);
    }

    static void firePrimitiveDragOffsetUpdateEvent(LienzoPanel<? extends LienzoPanel> panel,
                                                   IPrimitive<?> primitive,
                                                   double dragX,
                                                   double dragY) {
        LienzoPanelPrimitiveDragEventDetail detail = new LienzoPanelPrimitiveDragEventDetail(panel, primitive, dragX, dragY);
        fireCustomEvent(LIENZO_PANEL_PRIMITIVE_DRAG_OFFSET_UPDATE_EVENT, panel, detail);
    }

    static void addPrimitiveDragEndEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                 EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT, eventListener);
    }

    static void removePrimitiveDragEndEventListener(LienzoPanel<? extends LienzoPanel> panel,
                                                    EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT, eventListener);
    }

    static void firePrimitiveDragEndEvent(LienzoPanel<? extends LienzoPanel> panel,
                                          IPrimitive<?> primitive,
                                          double dragX,
                                          double dragY) {
        LienzoPanelPrimitiveDragEventDetail detail = new LienzoPanelPrimitiveDragEventDetail(panel, primitive, dragX, dragY);
        fireCustomEvent(LIENZO_PANEL_PRIMITIVE_DRAG_END_EVENT, panel, detail);
    }

    static void addDragLimitsOverEventListener(LienzoPanel panel,
                                               EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT, eventListener);
    }

    static void removeDragLimitsOverEventListener(LienzoPanel panel,
                                                  EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT, eventListener);
    }

    static void fireDragLimitsOverEvent(LienzoPanel panel,
                                        EnumSet<LimitDirections> limitDirections) {
        LienzoPanelDragLimitEventDetail detail = new LienzoPanelDragLimitEventDetail(panel, limitDirections);
        fireCustomEvent(LIENZO_PANEL_DRAG_LIMITS_OVER_EVENT, panel, detail);
    }

    static void addDragLimitsOutEventListener(LienzoPanel panel,
                                              EventListener eventListener) {
        panel.getElement().addEventListener(LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT, eventListener);
    }

    static void removeDragLimitsOutEventListener(LienzoPanel panel,
                                                 EventListener eventListener) {
        panel.getElement().removeEventListener(LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT, eventListener);
    }

    static void fireDragLimitsOutEvent(LienzoPanel panel) {
        fireCustomEvent(LIENZO_PANEL_DRAG_LIMITS_OUT_EVENT, panel);
    }

    protected static void fireCustomEvent(String eventType,
                                          LienzoPanel panel) {
        LienzoPanelEventDetail detail = new LienzoPanelEventDetail(panel);
        fireCustomEvent(eventType, panel, detail);
    }

    protected static void fireCustomEvent(String eventType,
                                          LienzoPanel panel,
                                          LienzoPanelEventDetail detail) {
        CustomEvent event = new CustomEvent(eventType);
        event.initCustomEvent(eventType, true, false, detail);
        panel.getElement().dispatchEvent(event);
    }
}
