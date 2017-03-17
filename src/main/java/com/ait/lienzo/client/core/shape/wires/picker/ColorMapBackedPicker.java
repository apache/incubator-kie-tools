/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class ColorMapBackedPicker
{
    public static final ColorKeyRotor          m_colorKeyRotor = new ColorKeyRotor();

    protected final Context2D                  m_ctx;

    protected final ScratchPad                 m_scratchPad;

    protected final NFastStringMap<PickerPart> m_colorMap = new NFastStringMap<>();

    protected final NFastArrayList<WiresShape> m_shapesToSkip = new NFastArrayList<>();

    protected ImageData                        m_imageData;

    protected boolean                          m_addHotspots;

    protected double                           m_borderWidth;

    public ColorMapBackedPicker(NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, WiresShape shapeToSkip)
    {
        this(shapes, scratchPad, shapeToSkip, false, 0);
    }

    public ColorMapBackedPicker( NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, final WiresShape shapeToSkip, boolean addHotspots, double borderWidth )
    {
        m_scratchPad = scratchPad;
        m_ctx = scratchPad.getContext();
        m_shapesToSkip.add(shapeToSkip);
        init( shapes,
              addHotspots,
              borderWidth);
    }

    public ColorMapBackedPicker(NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, NFastArrayList<WiresShape> shapesToSkip, boolean addHotspots, double borderWidth)
    {
        m_scratchPad = scratchPad;
        m_ctx = scratchPad.getContext();
        for(int j = 0; j < shapesToSkip.size(); j++) {
            m_shapesToSkip.add(shapesToSkip.get(j));
        }
        init( shapes,
              addHotspots,
              borderWidth);
    }

    private void init(NFastArrayList<WiresShape> shapes, boolean addHotspots, double borderWidth) {
        this.m_addHotspots = addHotspots;
        this.m_borderWidth = borderWidth;
        this.m_scratchPad.clear();

        addShapes(shapes);

        this.m_imageData = m_ctx.getImageData(0, 0, m_scratchPad.getWidth(), m_scratchPad.getHeight());
    }

    protected void addShapes(NFastArrayList<WiresShape> shapes)
    {
        for (int j = 0; j < shapes.size(); j++)
        {
            WiresShape prim = shapes.get(j);
            if ( m_shapesToSkip.contains( prim ) )
            {
                continue;
            }

            MultiPath multiPath = prim.getPath();
            drawShape(m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BODY), true);
            addSupplementaryPaths(prim);

            if (m_addHotspots)
            {
                drawShape(m_colorKeyRotor.next(), m_borderWidth, new PickerPart(prim, PickerPart.ShapePart.BORDER_HOTSPOT), false);

                // need to be able to detect the difference between the actual border selection and the border hotspot
                drawShape(m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BORDER), false);
            }

            if (prim.getChildShapes() != null && !prim.getChildShapes().isEmpty())
            {
                addShapes(prim.getChildShapes());
            }
        }
    }

    @SuppressWarnings("unused")
    protected void addSupplementaryPaths(WiresShape prim) {
        //No supplementary paths for a WiresShape by default
    }

    protected void drawShape(String color, double strokeWidth, PickerPart pickerPart, boolean fill) {
        m_colorMap.put(color, pickerPart);

        BackingColorMapUtils.drawShapeToBacking(m_ctx, pickerPart.getShape(), color, strokeWidth, fill);
    }

    protected void drawShape(String color, double strokeWidth, MultiPath multiPath, PickerPart pickerPart, boolean fill) {
        m_colorMap.put(color, pickerPart);

        BackingColorMapUtils.drawShapeToBacking(m_ctx, multiPath, color, strokeWidth, fill);
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
