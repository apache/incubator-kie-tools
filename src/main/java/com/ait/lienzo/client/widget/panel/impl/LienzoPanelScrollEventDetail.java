package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.Event;

public class LienzoPanelScrollEventDetail extends LienzoPanelEventDetail {

    private final double px;
    private final double py;

    public static LienzoPanelScrollEventDetail getScrollDetail(Event event) {
        return (LienzoPanelScrollEventDetail) ((CustomEvent) event).detail;
    }
    public LienzoPanelScrollEventDetail(LienzoPanel panel,
                                        double px,
                                        double py) {
        super(panel);
        this.px = px;
        this.py = py;
    }

    public double getPx() {
        return px;
    }

    public double getPy() {
        return py;
    }
}
