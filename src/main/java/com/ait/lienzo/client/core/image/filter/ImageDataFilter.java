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

import com.ait.lienzo.client.core.shape.Movie;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
import com.ait.lienzo.shared.core.types.ImageFilterType;

import elemental2.core.JsArray;
import elemental2.dom.ImageData;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Interface to be used to create {@link Picture} and {@link Movie} filters.
 */
public interface ImageDataFilter<T extends ImageDataFilter<T>> extends IJSONSerializable<T>
{
    int                            R_OFFSET        = 0;

    int                            G_OFFSET        = 1;

    int                            B_OFFSET        = 2;

    int                            A_OFFSET        = 3;

    int                            PIXEL_SZ        = 4;

    ImageDataFilterCommonOps FilterCommonOps = ImageDataFilterCommonOps.make();

    ImageData filter(ImageData source, boolean copy);

    boolean isTransforming();

    boolean isActive();

    void setActive(boolean active);

    ImageFilterType getType();

    @JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
    final class FilterTableArray extends JsArray<Integer>
    {
        protected FilterTableArray(int... items)
        {
        }
    }

    interface FilterTransformFunction
    {
        void transform(int x, int y, int[] out);
    }

    @JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
    final class FilterConvolveMatrix extends JsArray<Double>
    {
        public FilterConvolveMatrix()
        {
        }
    }
}
