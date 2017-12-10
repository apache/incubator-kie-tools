package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.WiresShape;

public interface WiresShapeHighlight<P> {

    void highlight(final WiresShape shape,
                   P part);

    void restore();

}
