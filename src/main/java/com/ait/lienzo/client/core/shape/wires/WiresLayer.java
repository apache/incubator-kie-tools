package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;

public class WiresLayer extends WiresContainer
{
    public WiresLayer(Layer layer)
    {
        super(layer);
    }

    public Layer getLayer()
    {
        return (Layer) getContainer();
    }

}
