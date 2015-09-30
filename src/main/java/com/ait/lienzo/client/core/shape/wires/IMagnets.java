package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Shape;

public interface IMagnets
{
    void shapeMoved();

    void show();

    void hide();

    void destroy();

    void destroy(Magnet magnet);

    Magnet getMagnet(int i);

    int size();

    Shape getShape();
}
