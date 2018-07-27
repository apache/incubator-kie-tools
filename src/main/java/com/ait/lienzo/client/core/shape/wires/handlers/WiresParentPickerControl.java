package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

public interface WiresParentPickerControl extends WiresShapeLocationControl {

    public WiresContainer getParent();

    public PickerPart.ShapePart getParentShapePart();

    public Index getIndex();

    /**
     * An indexed shape part picker for the given control's layer.
     */
    public interface Index {
        /**
         * Excludes the given shape from the resulting index.
         */
        public void exclude(WiresContainer shape);

        /**
         * Returns the picker part for the shape located at the given coordinates, if any.
         */
        PickerPart findShapeAt(int x, int y);

        /**
         * Clears the index.
         */
        public void clear();
    }
}
