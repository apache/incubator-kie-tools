package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

public interface WiresParentPickerControl extends WiresShapeLocationControl {

    public WiresContainer getParent();

    public PickerPart.ShapePart getParentShapePart();

    public Index getIndex();

    public interface Index {

        public void addShapeToSkip(WiresContainer shape);

        public void clear();
    }
}
