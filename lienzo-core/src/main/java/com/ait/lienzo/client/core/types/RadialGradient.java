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

package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.types.LinearGradient.LinearGradientJSO;
import com.ait.lienzo.shared.core.types.IColor;
import elemental2.core.Global;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * RadialGradient defines the fill style for a {@link Shape} as a Radial Gradient.
 */
public final class RadialGradient implements FillGradient {

    public static final String TYPE = "RadialGradient";

    private final RadialGradientJSO m_jso;

    public RadialGradient(final RadialGradientJSO jso) {
        m_jso = jso;
    }

    public RadialGradient(final double sx, final double sy, final double sr, final double ex, final double ey, final double er) {
        this(RadialGradientJSO.make(sx, sy, sr, ex, ey, er));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Add color stop
     *
     * @param stop
     * @param color
     * @return {@link RadialGradient}
     */
    public final RadialGradient addColorStop(final double stop, final String color) {
        m_jso.addColorStop(stop, color);

        return this;
    }

    /**
     * Add color stop
     *
     * @param stop
     * @param color {@link ColorName} or {@link Color}
     * @return {@link RadialGradient}
     */
    public final RadialGradient addColorStop(final double stop, final IColor color) {
        m_jso.addColorStop(stop, color.getColorString());

        return this;
    }

    @Override
    public LinearGradient asLinearGradient() {
        return null;
    }

    @Override
    public RadialGradient asRadialGradient() {
        return this;
    }

    @Override
    public PatternGradient asPatternGradient() {
        return null;
    }

    public final RadialGradientJSO getJSO() {
        return m_jso;
    }

    public final String toJSONString() {
        return Global.JSON.stringify(m_jso);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public boolean equals(final Object other) {
        if ((other == null) || (!(other instanceof RadialGradient))) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return ((RadialGradient) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode() {
        return toJSONString().hashCode();
    }

    @JsType
    public static final class RadialGradientJSO extends LinearGradientJSO {

        public double sr;
        public double er;

        @JsConstructor
        protected RadialGradientJSO() {
        }

        public static final RadialGradientJSO make(double sx, double sy, double sr, double ex, double ey, double er) {
            RadialGradientJSO grad = new RadialGradientJSO();
            setValues(sx, sy, ex, ey, grad);
            grad.sr = sr;
            grad.er = er;
            grad.type = "RadialGradient";
            return grad;
        }
    }
}
