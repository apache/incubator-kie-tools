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
import jsinterop.annotations.JsProperty;

public abstract class AbstractConvolveImageDataFilter<T extends AbstractConvolveImageDataFilter<T>> extends AbstractImageDataFilter<T> {

    @JsProperty
    private FilterConvolveMatrix filterConvolveMatrix;

    protected AbstractConvolveImageDataFilter(final ImageFilterType type, final double... matrix) {
        super(type);

        setMatrix(matrix);
    }

    protected AbstractConvolveImageDataFilter(final ImageFilterType type, final FilterConvolveMatrix matrix) {
        super(type);

        setMatrix(matrix);
    }

    protected AbstractConvolveImageDataFilter(final ImageFilterType type, final Object node) {
        super(type, node);
    }

    public final T setMatrix(final double... matrix) {
        this.filterConvolveMatrix = new FilterConvolveMatrix();
        for (int i = 0, length = matrix.length; i < length; i++) {
            this.filterConvolveMatrix.push(matrix[i]);
        }

        return cast();
    }

    public final T setMatrix(final FilterConvolveMatrix matrix) {
        this.filterConvolveMatrix = matrix;

        return cast();
    }

    public final FilterConvolveMatrix getMatrix() {
        return this.filterConvolveMatrix;
    }

    @Override
    public final boolean isTransforming() {
        return true;
    }

    @Override
    public ImageData filter(ImageData source, final boolean copy) {
        if (null == source) {
            return null;
        }
        if (copy) {
            source = ImageDataUtil.copy(source);
        }
        if (false == isActive()) {
            return source;
        }
        final Uint8ClampedArray data = source.data;

        if (null == data) {
            return source;
        }
        final FilterConvolveMatrix matrix = getMatrix();

        if (matrix.getLength() < 1) {
            return source;
        }
        final ImageData result = ImageDataUtil.create(source);

        FilterCommonOps.doFilterConvolve(data, result.data, matrix, source.width, source.height);

        return result;
    }
}
