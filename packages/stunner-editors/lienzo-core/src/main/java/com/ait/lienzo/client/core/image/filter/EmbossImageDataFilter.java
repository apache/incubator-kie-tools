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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.types.ImageDataUtil;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import elemental2.core.Uint8ClampedArray;
import elemental2.dom.ImageData;
import jsinterop.base.Js;

public class EmbossImageDataFilter extends AbstractImageDataFilter<EmbossImageDataFilter> {

    public EmbossImageDataFilter() {
        super(ImageFilterType.EmbossImageDataFilterType);
    }

    protected EmbossImageDataFilter(Object node) {
        super(ImageFilterType.EmbossImageDataFilterType, node);
    }

    @Override
    public final boolean isTransforming() {
        return true;
    }

    @Override
    public ImageData filter(ImageData source, boolean copy) {
        if (null == source) {
            return null;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        if (!isActive()) {
            return source;
        }
        final Uint8ClampedArray data = source.data;

        if (null == data) {
            return source;
        }
        filter_(data, FilterCommonOps.getLength(source), source.width, FilterCommonOps);

        return source;
    }

    private final void filter_(Uint8ClampedArray dataArray, int length, int width, ImageDataFilterCommonOps fops) {
        int[] data = Js.uncheckedCast(dataArray);
        for (int i = 0; i < length; i++) {
            if (i < (length - width * 4)) {
                if (((i + 1) % 4) != 0) {
                    if (((i + 4) % (width * 4)) == 0) {
                        data[i] = data[i - 4];
                        data[i + 1] = data[i - 3];
                        data[i + 2] = data[i - 2];
                        data[i + 3] = data[i - 1];
                    } else {
                        data[i] = 255 / 2 + 2 * data[i] - data[i + 4] - data[i + width * 4];
                    }
                }
            } else {
                if (((i + 1) % 4) != 0) {
                    data[i] = data[i - width * 4];
                }
            }
        }
        fops.filterLuminosity(dataArray, length);
    }
}
