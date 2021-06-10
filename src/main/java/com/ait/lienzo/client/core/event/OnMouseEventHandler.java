package com.ait.lienzo.client.core.event;

import elemental2.dom.MouseEvent;

public interface OnMouseEventHandler
{
    boolean  onMouseEventBefore(MouseEvent event);

    void  onMouseEventAfter(MouseEvent event);
}
