/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires.picker;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.BackingColorMapUtils;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class ColorMapBackedPicker
{
    private final ImageData                  m_imageData;

    private final NFastStringMap<PickerPart> m_colorMap = new NFastStringMap<>();

    public static final ColorKeyRotor        m_colorKeyRotor = new ColorKeyRotor();

    private final boolean                    m_addHotspots;

    private final double                     m_borderWidth;

    public ColorMapBackedPicker(NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, WiresShape shapeToSkip)
    {
        this(shapes, scratchPad, shapeToSkip, false, 0);
    }

    public ColorMapBackedPicker(NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, WiresShape shapeToSkip, boolean addHotspots, double borderWidth)
    {
        this.m_addHotspots = addHotspots;
        this.m_borderWidth = borderWidth;
        scratchPad.clear();

        Context2D ctx = scratchPad.getContext();

        addShapes(ctx, shapes, shapeToSkip);

        this.m_imageData = ctx.getImageData(0, 0, scratchPad.getWidth(), scratchPad.getHeight());
    }

    private void addShapes(Context2D ctx, NFastArrayList<WiresShape> shapes, WiresShape shapeToSkip)
    {
        for (int j = 0; j < shapes.size(); j++)
        {
            WiresShape prim = shapes.get(j);
            if (prim == shapeToSkip)
            {
                continue;
            }

            MultiPath multiPath = prim.getPath();
            drawShape(ctx, m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BODY), true);

            if (m_addHotspots)
            {
                drawShape(ctx, m_colorKeyRotor.next(), m_borderWidth, new PickerPart(prim, PickerPart.ShapePart.BORDER_HOTSPOT), false);

                // need to be able to detect the difference betwen the actual border selection and the border hotspot
                drawShape(ctx, m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BORDER), false);
            }

            if (prim.getChildShapes() != null)
            {
                addShapes(ctx, prim.getChildShapes(), shapeToSkip);
            }
        }
    }

    private void drawShape(Context2D ctx, String color, double strokeWidth, PickerPart pickerPart, boolean fill) {
        m_colorMap.put(color, pickerPart);

        BackingColorMapUtils.drawShapeToBacking(ctx, pickerPart.getShape(), color, strokeWidth, fill);
    }

    public PickerPart findShapeAt(int x, int y)
    {
        String color = BackingColorMapUtils.findColorAtPoint(m_imageData, x, y);
        if (color != null)
        {
            PickerPart pickerPart = m_colorMap.get(color);
            if (pickerPart != null)
            {
                return pickerPart;
            }
        }
        return null;
    }
}
