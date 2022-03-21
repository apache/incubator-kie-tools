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

import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.FillRepeat;
import elemental2.core.Global;
import elemental2.dom.HTMLImageElement;
import jsinterop.annotations.JsType;

/**
 * PatternGradient defines the fill style for a {@link Shape} as a Pattern Gradient.
 */
public final class PatternGradient implements FillGradient {

    public static final String TYPE = "PatternGradient";

    private final PatternGradientJSO m_jso;

    public PatternGradient(final PatternGradientJSO jso) {
        m_jso = jso;
    }

    public PatternGradient(final HTMLImageElement image) {
        this(PatternGradientJSO.make(image, ScratchPad.toDataURL(image), FillRepeat.REPEAT.getValue()));
    }

    public PatternGradient(final HTMLImageElement image, final FillRepeat repeat) {
        this(PatternGradientJSO.make(image, ScratchPad.toDataURL(image), repeat.getValue()));
    }

    @Override
    public LinearGradient asLinearGradient() {
        return null;
    }

    @Override
    public RadialGradient asRadialGradient() {
        return null;
    }

    @Override
    public PatternGradient asPatternGradient() {
        return this;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public String getSrc() {
        return m_jso.src;
    }

    public FillRepeat getRepeat() {
        return FillRepeat.lookup(m_jso.repeat);
    }

    public final PatternGradientJSO getJSO() {
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
        if ((other == null) || (!(other instanceof PatternGradient))) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return ((PatternGradient) other).toJSONString().equals(toJSONString());
    }

    @Override
    public int hashCode() {
        return toJSONString().hashCode();
    }

    @JsType
    public static final class PatternGradientJSO extends GradientJSO {

        public String src;
        public String repeat;
        public HTMLImageElement image;

        protected PatternGradientJSO() {
        }

        public static final PatternGradientJSO make(HTMLImageElement e, String s, String r) {
            PatternGradientJSO grad = new PatternGradientJSO();
            grad.src = s;
            grad.repeat = r;
            grad.type = "PatternGradient";
            grad.image = e;
            return grad;
        }

        ;
    }
}
