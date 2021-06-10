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

package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.ImageDataUtil;
import com.ait.lienzo.shared.core.types.Color;

import elemental2.dom.ImageData;

public final class ColorExtractor
{
    private static final ScratchPad SCRATCH = new ScratchPad(2, 2);

    private ColorExtractor()
    {
    }

    public static final Color extract(final String color)
    {
        SCRATCH.clear();

        final Context2D context = SCRATCH.getContext();

        context.setFillColor(color);

        context.fillRect(0, 0, 2, 2);

        final ImageData data = context.getImageData(0, 0, 2, 2);

        return new Color(ImageDataUtil.getRedAt(data,1, 1), ImageDataUtil.getGreenAt(data,1, 1), ImageDataUtil.getBlueAt(data,1, 1), (((double) ImageDataUtil.getAlphaAt(data, 1, 1)) / 255.0));
    }
}
