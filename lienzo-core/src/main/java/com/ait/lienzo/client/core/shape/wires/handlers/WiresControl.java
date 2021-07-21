package com.ait.lienzo.client.core.shape.wires.handlers;

/**
 * Control that performs operations in wires objects (shapes, connectors, etc).
 */
public interface WiresControl {

    /**
     * Execute the control's logic.
     */
    void execute();

    /**
     * Clear the control's state.
     * No operations expected for wires objects.
     */
    void clear();

    /**
     * Return the wires object/s to it's initial state
     * and clears current control's state.
     */
    void reset();

    /**
     * Destroys the control instance
     */
    void destroy();
}
