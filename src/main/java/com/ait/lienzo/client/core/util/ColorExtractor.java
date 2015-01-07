/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.shared.core.types.Color;

public final class ColorExtractor
{
    private static final ScratchCanvas s_canvas = new ScratchCanvas(2, 2);

    public static final Color extract(String color)
    {
        s_canvas.clear();

        Context2D context = s_canvas.getContext();

        context.setFillColor(color);

        context.fillRect(0, 0, 2, 2);

        ImageData data = context.getImageData(0, 0, 2, 2);

        return new Color(data.getRedAt(1, 1), data.getGreenAt(1, 1), data.getBlueAt(1, 1), (((double) data.getAlphaAt(1, 1)) / 255.0));
    }
}
