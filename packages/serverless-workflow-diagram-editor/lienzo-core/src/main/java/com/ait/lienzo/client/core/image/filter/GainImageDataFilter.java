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
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;

public class GainImageDataFilter extends AbstractTableImageDataFilter<GainImageDataFilter> {

    @JsProperty
    private double gain;

    @JsProperty
    private double bias;

    private double m_ngain = Double.NaN;

    private double m_nbias = Double.NaN;

    private FilterTableArray m_table = null;

    public GainImageDataFilter() {
        this(0.5, 0.5);
    }

    public GainImageDataFilter(double gain, double bias) {
        super(ImageFilterType.GainImageDataFilterType);

        setGain(gain);

        setBias(bias);
    }

    protected GainImageDataFilter(Object node) {
        super(ImageFilterType.GainImageDataFilterType, node);
    }

    public final GainImageDataFilter setGain(double gain) {
        this.gain = Math.max(Math.min(gain, getMaxGain()), getMinGain());

        return this;
    }

    public final double getGain() {
        return Math.max(Math.min(this.gain, getMaxGain()), getMinGain());
    }

    public final double getMinGain() {
        return 0;
    }

    public final double getMaxGain() {
        return 1;
    }

    public final GainImageDataFilter setBias(double bias) {
        this.bias = Math.max(Math.min(bias, getMaxBias()), getMinBias());

        return this;
    }

    public final double getBias() {
        return Math.max(Math.min(this.bias, getMaxBias()), getMinBias());
    }

    public final double getMinBias() {
        return 0;
    }

    public final double getMaxBias() {
        return 1;
    }

    @Override
    protected final FilterTableArray getTable() {
        double gain = getGain();

        double bias = getBias();

        if ((gain != m_ngain) || (bias != m_nbias)) {
            m_ngain = gain;

            m_nbias = bias;

            m_table = getTable_();
        }
        return m_table;
    }

    private final FilterTableArray getTable_() {
        int[] table = new int[256];
        double gain = m_ngain;
        double bias = m_nbias;
        for (int i = 0; i < 256; i++) {
            double v = i / 255;
            double k = (1 / gain - 2) * (1 - 2 * v);
            v = (v < 0.5) ? v / (k + 1) : (k - v) / (k - 1);
            v /= (1 / bias - 2) * (1 - v) + 1;
            table[i] = Js.coerceToInt(255 * v);
        }
        return new FilterTableArray(table);
    }
}
