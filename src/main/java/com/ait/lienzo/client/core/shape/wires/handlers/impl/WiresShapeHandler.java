package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;

public interface WiresShapeHandler extends DragConstraintEnforcer,
                                           NodeDragEndHandler,
                                           NodeMouseDownHandler,
                                           NodeMouseUpHandler,
                                           NodeMouseClickHandler {

    WiresShapeControl getControl();

}
