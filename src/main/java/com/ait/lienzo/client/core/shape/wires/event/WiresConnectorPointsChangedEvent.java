package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;

import elemental2.dom.Event;
import elemental2.dom.HTMLElement;

public class WiresConnectorPointsChangedEvent extends AbstractWiresEvent<WiresConnectorPointsChangedHandler, WiresConnector>
{
    public static final Type<WiresConnectorPointsChangedHandler> TYPE = new Type<>();

    public WiresConnectorPointsChangedEvent(final HTMLElement relativeElement)
    {
        super(relativeElement);
    }

    public void kill()
    {
        setSource(null);
        setDead(true);
    }

    public void revive()
    {
        setSource(null);
        setDead(false);
    }


    public void override(WiresConnector shape)
    {
        setSource(shape);
    }

    @Override
    public Type<WiresConnectorPointsChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    public void dispatch(WiresConnectorPointsChangedHandler handler)
    {
        handler.onPointsChanged(this);
    }

    public Event getNativeEvent()
    {
        throw new UnsupportedOperationException();
    }
}
