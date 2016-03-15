package com.ait.lienzo.client.core.shape.wires;


public class PickerPart
{
    private final WiresShape shape;

    private final ShapePart part;

    public WiresShape getShape()
    {
        return shape;
    }

    public ShapePart getShapePart()
    {
        return part;
    }

    public enum ShapePart {
        BORDER, BORDER_HOTSPOT, BODY
    }

    public PickerPart(WiresShape shape, ShapePart part)
    {
        this.shape = shape;
        this.part = part;
    }

    @Override public String toString()
    {
        return this.part.toString() + " for " + this.shape.toString();
    }
}
