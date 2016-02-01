package com.ait.lienzo.client.core.shape.wires.event;


public interface DragHandler extends WiresEventHandler {
    
    public void onDragStart(DragEvent dragEvent);

    public void onDragMove(DragEvent dragEvent);
    
    public void onDragEnd(DragEvent dragEvent);
    
}
