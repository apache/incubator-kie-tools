package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

public interface WiresParentPickerControl extends WiresShapeLocationControl, WiresMouseControl {

    WiresContainer getInitialParent();

    WiresContainer getParent();

    PickerPart.ShapePart getParentShapePart();

    WiresLayerIndex getIndex();
}
