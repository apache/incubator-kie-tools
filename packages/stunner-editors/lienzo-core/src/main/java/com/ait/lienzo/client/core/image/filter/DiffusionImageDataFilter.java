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

import com.ait.lienzo.shared.core.types.ImageFilterType;
import jsinterop.base.Js;

public class DiffusionImageDataFilter extends AbstractValueTransformImageDataFilter<DiffusionImageDataFilter> {

    public DiffusionImageDataFilter() {
        super(ImageFilterType.DiffusionImageDataFilterType, 4);
    }

    public DiffusionImageDataFilter(double value) {
        super(ImageFilterType.DiffusionImageDataFilterType, value);
    }

    protected DiffusionImageDataFilter(Object node) {
        super(ImageFilterType.DiffusionImageDataFilterType, node);
    }

    @Override
    public double getMinValue() {
        return 1;
    }

    @Override
    public double getMaxValue() {
        return 100;
    }

    @Override
    public double getRefValue() {
        return 4;
    }

    @Override
    protected final FilterTransformFunction getTransform(double value) {
        int[] stabl = new int[256];
        int[] ctabl = new int[256];
        for (int i = 0; i < 256; i++) {
            double a = Math.PI * 2 * i / 256;
            stabl[i] = Js.coerceToInt(value * Math.sin(a));
            ctabl[i] = Js.coerceToInt(value * Math.cos(a));
        }

        return new DiffusionImageDataFilterFilterTransformer(stabl, ctabl);
    }

    public static class DiffusionImageDataFilterFilterTransformer implements FilterTransformFunction {

        int[] stabl;
        int[] ctabl;

        public DiffusionImageDataFilterFilterTransformer(final int[] stabl, final int[] ctabl) {
            this.stabl = stabl;
            this.ctabl = ctabl;
        }

        @Override
        public void transform(final int x, final int y, final int[] out) {
            int a = Js.coerceToInt(Math.random() * 255);
            double d = Math.random();
            out[0] = Js.coerceToInt(x + d * stabl[a]);
            out[1] = Js.coerceToInt(y + d * ctabl[a]);
        }
    }
}
