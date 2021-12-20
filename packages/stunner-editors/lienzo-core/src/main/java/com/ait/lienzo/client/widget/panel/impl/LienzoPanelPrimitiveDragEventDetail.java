package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CustomEvent;
import elemental2.dom.Event;

public class LienzoPanelPrimitiveDragEventDetail extends LienzoPanelEventDetail {

    private final double dragX;

    private final double dragY;

    private final IPrimitive<?> primitive;

    public static LienzoPanelPrimitiveDragEventDetail getDragDetail(Event event) {
        return (LienzoPanelPrimitiveDragEventDetail) ((CustomEvent) event).detail;
    }

    public LienzoPanelPrimitiveDragEventDetail(LienzoPanel<? extends LienzoPanel> panel,
                                               IPrimitive<?> primitive,
                                               double dragX,
                                               double dragY) {
        super(panel);
        this.primitive = primitive;
        this.dragX = dragX;
        this.dragY = dragY;
    }

    public IPrimitive getPrimitive() {
        return primitive;
    }

    public double getDragX() {
        return dragX;
    }

    public double getDragY() {
        return dragY;
    }
}
