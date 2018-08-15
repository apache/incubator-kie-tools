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
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.ImageDataPixelColor;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class ColorMapBackedPicker
{
    public static final ColorKeyRotor          m_colorKeyRotor = new ColorKeyRotor();

    protected final Context2D                  m_ctx;

    protected final ScratchPad                 m_scratchPad;

    protected final NFastStringMap<PickerPart> m_colorMap = new NFastStringMap<>();

    private final PickerOptions                m_options;

    protected WiresLayer                       m_layer;

    public ColorMapBackedPicker(WiresLayer layer,
                                NFastArrayList<WiresShape> shapes,
                                ScratchPad scratchPad,
                                PickerOptions options)
    {
        m_scratchPad = scratchPad;
        m_layer = layer;
        m_ctx = scratchPad.getContext();
        m_options = options;
        m_scratchPad.clear();
        addShapes(shapes);
    }


    protected void addShapes(NFastArrayList<WiresShape> shapes)
    {
        for (int j = 0; j < shapes.size(); j++)
        {
            WiresShape prim = shapes.get(j);
            if ( m_options.shapesToSkip.contains( prim ) )
            {
                continue;
            }

            MultiPath multiPath = prim.getPath();
            drawShape(m_colorKeyRotor.next(), multiPath.getStrokeWidth(), new PickerPart(prim, PickerPart.ShapePart.BODY), true);
            addSupplementaryPaths(prim);

            if (m_options.hotspotsEnabled)
            {
                drawShape(m_colorKeyRotor.next(), m_options.hotspotWidth, new PickerPart(prim, PickerPart.ShapePart.BORDER_HOTSPOT), false);

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
        if (null != m_layer) {
            Point2D temp = new Point2D(x,
                                       y);
            m_layer.getLayer().getViewport().getTransform().getInverse().transform(temp,
                                                                                   temp);
            x = (int) Math.round(temp.getX());
            y = (int) Math.round(temp.getY());
        }

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

    public PickerOptions getPickerOptions() {
        return m_options;
    }

    public static final class PickerOptions {
        private final NFastArrayList<WiresContainer> shapesToSkip;
        private final boolean                        hotspotsEnabled;
        private final double                         hotspotWidth;

        public PickerOptions(final boolean hotspotsEnabled,
                             final double hotspotWidth) {
            this.shapesToSkip = new NFastArrayList<WiresContainer>();
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
