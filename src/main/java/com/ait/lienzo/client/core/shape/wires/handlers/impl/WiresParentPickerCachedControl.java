package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;

public class WiresParentPickerCachedControl extends WiresParentPickerControlImpl {

    private double m_x;
    private double m_y;
    private boolean adjusted;

    public WiresParentPickerCachedControl(WiresShape m_shape,
                                          ColorMapBackedPicker.PickerOptions pickerOptions) {
        super(m_shape,
              pickerOptions);
        clear();
    }

    public WiresParentPickerCachedControl(WiresShapeLocationControlImpl shapeLocationControl,
                                          ColorMapBackedPicker.PickerOptions pickerOptions) {
        super(shapeLocationControl, pickerOptions);
        clear();
    }

    public WiresParentPickerCachedControl(WiresShapeLocationControlImpl shapeLocationControl,
                                          ColorMapBackedPickerProvider colorMapBackedPickerProvider) {
        super(shapeLocationControl, colorMapBackedPickerProvider);
    }

    @Override
    public void onMoveStart(double x,
                            double y) {
        if (x != m_x || y != m_y) {
            clear();
            super.onMoveStart(x,
                              y);
            m_x = x;
            m_y = y;
        }
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        if (dx != m_x || dy != m_y) {
            adjusted = super.onMove(dx,
                                    dy);
            m_x = dx;
            m_y = dy;
        }
        return adjusted;
    }

    @Override
    public boolean onMoveComplete() {
        return true;
    }

    public void clear() {
        super.clear();
        m_x = 0;
        m_y = 0;
        adjusted = false;
    }
}
