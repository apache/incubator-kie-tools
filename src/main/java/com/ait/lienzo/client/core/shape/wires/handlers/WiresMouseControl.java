package com.ait.lienzo.client.core.shape.wires.handlers;

/**
 * Control for mouse interactions with wires objects (shapes, connectors, etc).
 *
 * This type allows decoupling the mouse handlers added by default to wires objects from each control's logic.
 */
public interface WiresMouseControl {

    void onMouseClick(MouseEvent event);

    void onMouseDown(MouseEvent event);

    void onMouseUp(MouseEvent event);
}
