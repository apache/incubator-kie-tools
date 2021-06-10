package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;

import elemental2.dom.HTMLElement;

public class WiresEventHandlers
{
    private           HTMLElement                      relativeDiv;

    public final      WiresDragStartEvent              dragStartEvent;
    public final      WiresDragMoveEvent               dragMoveEvent;
    public final      WiresDragEndEvent                dragEndEvent;
    public final      WiresMoveEvent                   wiresMoveEvent;

    public final WiresConnectorPointsChangedEvent wiresConnectorPointsChangedEvent;

    public WiresEventHandlers(final HTMLElement relativeDiv)
    {
        this.relativeDiv = relativeDiv;

        this.dragStartEvent = new WiresDragStartEvent(relativeDiv);
        this.dragMoveEvent = new WiresDragMoveEvent(relativeDiv);
        this.dragEndEvent = new WiresDragEndEvent(relativeDiv);
        this.wiresMoveEvent = new WiresMoveEvent(relativeDiv);

        this.wiresConnectorPointsChangedEvent = new WiresConnectorPointsChangedEvent(relativeDiv);
    }
}
