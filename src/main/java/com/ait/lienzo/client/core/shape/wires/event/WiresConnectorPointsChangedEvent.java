package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;

public class WiresConnectorPointsChangedEvent extends AbstractWiresEvent<WiresConnector, WiresConnectorPointsChangedHandler>
{
    public static final Type<WiresConnectorPointsChangedHandler> TYPE = new Type<WiresConnectorPointsChangedHandler>();

    public WiresConnectorPointsChangedEvent(WiresConnector shape)
    {
        super(shape);
    }

    @Override
    public Type<WiresConnectorPointsChangedHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(WiresConnectorPointsChangedHandler handler)
    {
        handler.onPointsChanged(this);
    }
}
