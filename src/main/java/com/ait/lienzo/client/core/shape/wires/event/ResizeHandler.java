package com.ait.lienzo.client.core.shape.wires.event;


public interface ResizeHandler extends WiresEventHandler {
    
    public void onResizeStart(ResizeEvent resizeEvent);

    public void onResizeStep(ResizeEvent resizeEvent);
    
    public void onResizeEnd(ResizeEvent resizeEvent);
    
}
