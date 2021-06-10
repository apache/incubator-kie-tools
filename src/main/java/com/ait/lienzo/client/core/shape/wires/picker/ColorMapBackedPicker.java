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

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

public class ColorMapBackedPicker
{
    public static final ColorKeyRotor          m_colorKeyRotor = new ColorKeyRotor();


    private final Context2D                    m_ctx;

    private final ScratchPad                   m_scratchPad;

    private final Map<String, PickerPart>      m_colorMap = new HashMap();

    private final NFastArrayList<WiresShape>   m_shapesMap = new NFastArrayList<>();

    private final PickerOptions                m_options;

    public ColorMapBackedPicker(final ScratchPad scratchPad,
                                final PickerOptions options)
    {
        m_scratchPad = scratchPad;
        m_ctx = scratchPad.getContext();
        m_options = options;
    }

    public void build(final NFastArrayList<WiresShape> shapes) {
        clear();
        processShapes(shapes);
    }

    void processShapes(final NFastArrayList<WiresShape> shapes)
    {
        computeShapes(shapes);
        drawShapes();
    }

    private void computeShapes(final NFastArrayList<WiresShape> shapes)
    {
        for (int j = 0; j < shapes.size(); j++)
        {
            WiresShape prim = shapes.get(j);

            if ( m_options.shapesToSkip.contains( prim ) )
            {
                continue;
            }

            m_shapesMap.add(prim);

            if (prim.getChildShapes() != null && !prim.getChildShapes().isEmpty())
            {
                computeShapes(prim.getChildShapes());
            }
        }
    }

    private void drawShapes()
    {
        // Draw all shapes (and children) into the scratchPad instance.
        for (int j = 0; j < m_shapesMap.size(); j++)
        {
            WiresShape prim = m_shapesMap.get(j);
            MultiPath multiPath = prim.getPath();
            drawShape(m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BODY), true);
            addSupplementaryPaths(prim);

            if (m_options.hotspotsEnabled)
            {
                // TODO: lienzo-to-native: type was BORDER_HOTSPOT before!!
                drawShape(m_colorKeyRotor.next(), m_options.hotspotWidth, new PickerPart(prim, PickerPart.ShapePart.BORDER), false);

                // need to be able to detect the difference between the actual border selection and the border hotspot
                drawShape(m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BORDER), false);
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
        ImageDataPixelColor color = m_ctx.getImageDataPixelColor(x, y);
        if (color != null)
        {
            PickerPart pickerPart = m_colorMap.get(color.toBrowserRGB());
            if (pickerPart != null)
            {
                return pickerPart;
            }
        }
        return null;
    }

    public void clear() {
        m_scratchPad.clear();
        m_colorMap.clear();
        m_shapesMap.clear();
    }

    public PickerOptions getPickerOptions() {
        return m_options;
    }

    public static final class PickerOptions {
        private final NFastArrayList<WiresContainer> shapesToSkip;
        private final boolean                        hotspotsEnabled;
        private final double                         hotspotWidth;

        public PickerOptions(final boolean hotspotsEnabled,
                             final double hotspotWidth) {
            this.shapesToSkip = new NFastArrayList<>();
            this.hotspotsEnabled = hotspotsEnabled;
            this.hotspotWidth = hotspotWidth;
        }

        public NFastArrayList<WiresContainer> getShapesToSkip() {
            return shapesToSkip;
        }

        public boolean isHotspotsEnabled() {
            return hotspotsEnabled;
        }

        public double getHotspotWidth() {
            return hotspotWidth;
        }
    }


}
