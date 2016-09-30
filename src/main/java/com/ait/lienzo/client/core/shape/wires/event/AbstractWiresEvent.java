package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractWiresEvent<S, H extends EventHandler> extends GwtEvent<H> implements INodeEvent  {
    
    private final S shape;
    private boolean m_dead = false;

    public AbstractWiresEvent(S shape) {
        this.shape = shape;
    }

    public S getShape() {
        return shape;
    }

    @Override
    public final boolean isAlive()
    {
        return (false == m_dead);
    }

    @Override
    public final void preventDefault()
    {
        m_dead = true;
    }

    @Override
    public final void stopPropagation()
    {
        m_dead = true;
    }

    @Override
    public final GwtEvent<?> getNodeEvent()
    {
        return this;
    }
    
}
