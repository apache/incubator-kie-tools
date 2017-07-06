package com.ait.lienzo.client.core.event;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;

public interface OnMouseEventHandler
{
    public boolean  onMouseEventBefore(MouseEvent<? extends EventHandler> event);

    public void  onMouseEventAfter(MouseEvent<? extends EventHandler> event);
}
