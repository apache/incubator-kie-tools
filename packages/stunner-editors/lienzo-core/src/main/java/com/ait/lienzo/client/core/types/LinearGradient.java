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

import com.ait.lienzo.shared.core.types.IColor;
import elemental2.core.Global;
import elemental2.core.JsArray;
import jsinterop.annotations.JsType;

/**
 * LinearGradient defines the fill style for a {@link Shape} as a Linear Gradient.
 */
public final class LinearGradient implements FillGradient {

    public static final String TYPE = "LinearGradient";

    private final LinearGradientJSO m_jso;

    public LinearGradient(final LinearGradientJSO jso) {
        m_jso = jso;
    }

    public LinearGradient(final double sx, final double sy, final double ex, final double ey) {
        this(LinearGradientJSO.make(sx, sy, ex, ey));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public final LinearGradient addColorStop(final double stop, final String color) {
        m_jso.addColorStop(stop, color);

        return this;
    }

    public final LinearGradient addColorStop(final double stop, final IColor color) {
        m_jso.addColorStop(stop, color.getColorString());

        return this;
    }

    @Override
    public LinearGradient asLinearGradient() {
        return this;
    }

    @Override
    public RadialGradient asRadialGradient() {
        return null;
    }

    @Override
    public PatternGradient asPatternGradient() {
        return null;
    }

    public final LinearGradientJSO getJSO() {
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
        if ((other == null) || (!(other instanceof LinearGradient))) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return ((LinearGradient) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode() {
        return toJSONString().hashCode();
    }

    @JsType
    public static class LinearGradientJSO extends GradientJSO {

        public double sx;
        public double sy;
        public double ex;
        public double ey;
        public JsArray<Object[]> colorStops;

        public LinearGradientJSO() {
        }

        public static final LinearGradientJSO make(double sx, double sy, double ex, double ey) {
            LinearGradientJSO grad = new LinearGradientJSO();
            setValues(sx, sy, ex, ey, grad);
            grad.type = "LinearGradient";
            return grad;
        }

        protected static void setValues(final double sx, final double sy, final double ex, final double ey, final LinearGradientJSO grad) {
            grad.sx = sx;
            grad.sy = sy;

            grad.ex = ex;
            grad.ey = ey;

            grad.colorStops = new JsArray<>();
        }

        public final void addColorStop(double stop, String color) {
            this.colorStops.push(new Object[]{stop, color});
        }
    }
}
