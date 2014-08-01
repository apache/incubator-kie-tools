/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.util.ScratchCanvas;
import com.ait.lienzo.shared.core.types.FillRepeat;
import com.google.gwt.dom.client.ImageElement;

/**
 * PatternGradient defines the fill style for a {@link Shape} as a Pattern Gradient. 
 */
public final class PatternGradient implements FillGradient
{
    public static final String       TYPE = "PatternGradient";

    private final PatternGradientJSO m_jso;

    public PatternGradient(PatternGradientJSO jso)
    {
        m_jso = jso;
    }

    public PatternGradient(ImageElement image)
    {
        this(PatternGradientJSO.make(image, ScratchCanvas.toDataURL(image), FillRepeat.REPEAT.getValue()));
    }

    public PatternGradient(ImageElement image, FillRepeat repeat)
    {
        this(PatternGradientJSO.make(image, ScratchCanvas.toDataURL(image), repeat.getValue()));
    }

    @Override
    public String getType()
    {
        return TYPE;
    }

    public final PatternGradientJSO getJSO()
    {
        return m_jso;
    }

    public static final class PatternGradientJSO extends GradientJSO
    {
        protected PatternGradientJSO()
        {
        }

        public static final native PatternGradientJSO make(ImageElement e, String s, String r)
        /*-{
        	return {
        	    src: s,
        		repeat: r,
        		type: "PatternGradient",
        		image: function() {
        		    return e;
        		}
        	}
        }-*/;
    }
}
