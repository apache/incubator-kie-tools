package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;

public interface MouseFocusControl {

    void onNodeMouseDown(NodeMouseDownEvent e);

    void onNodeMouseUp(NodeMouseUpEvent e);

    void onNodeClick(NodeMouseClickEvent e);

}
