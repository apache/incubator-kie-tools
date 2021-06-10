package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.EventListener;

// TODO: Use singleton instance for each event type.
public class LienzoPanelEvents {

    static final String LIENZO_PANEL_BOUNDS_CHANGED_EVENT = "lienzoPanelBoundsChangedEvent";
    static final String LIENZO_PANEL_RESIZE_EVENT = "lienzoPanelResizeEvent";
    static final String LIENZO_PANEL_SCALE_EVENT = "lienzoPanelScaleEvent";
    static final String LIENZO_PANEL_SCROLL_EVENT = "lienzoPanelScrollEvent";

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

    private static void fireCustomEvent(String eventType,
                                        LienzoPanel panel) {
        LienzoPanelEventDetail detail = new LienzoPanelEventDetail(panel);
        fireCustomEvent(eventType, panel, detail);
    }

    private static void fireCustomEvent(String eventType,
                                        LienzoPanel panel,
                                        LienzoPanelEventDetail detail) {
        CustomEvent event = new CustomEvent(eventType);
        event.initCustomEvent(eventType, true, false, detail);
        panel.getElement().dispatchEvent(event);
    }
}
