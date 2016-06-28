package com.ait.lienzo.client.core.shape.wires.event;


public interface WiresMoveHandler extends WiresEventHandler {
    
    void onShapeMoved(WiresMoveEvent event);

}
