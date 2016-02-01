package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeEvent;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractWiresEvent<H extends EventHandler> extends GwtEvent<H> implements INodeEvent  {
    
    public enum Type {
        START, STEP, END;
    }
    
    public static final GwtEvent.Type<DragHandler> DRAG = new GwtEvent.Type<DragHandler>();
    public static final GwtEvent.Type<ResizeHandler> RESIZE = new GwtEvent.Type<ResizeHandler>();
    
    
    private final WiresShape shape;
    private boolean m_dead = false;

    public AbstractWiresEvent(WiresShape shape) {
        this.shape = shape;
    }

    public WiresShape getShape() {
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
