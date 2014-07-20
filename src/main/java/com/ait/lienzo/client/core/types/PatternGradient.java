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

import com.ait.lienzo.client.core.image.ImageLoader;
import com.ait.lienzo.client.core.image.JSImage;
import com.ait.lienzo.shared.core.types.FillRepeat;

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

    private static final PatternGradientJSO make(ImageLoader loader, FillRepeat repeat)
    {
        if (false == loader.isLoaded())
        {
            throw new NullPointerException("image not loaded");
        }
        JSImage image = loader.getJSImage();

        if (null == image)
        {
            throw new NullPointerException("image not loaded");
        }
        return PatternGradientJSO.make(image, repeat.getValue());
    }

    public PatternGradient(JSImage image)
    {
        this(PatternGradientJSO.make(image, FillRepeat.REPEAT.getValue()));
    }

    public PatternGradient(JSImage image, FillRepeat repeat)
    {
        this(PatternGradientJSO.make(image, repeat.getValue()));
    }

    public PatternGradient(ImageLoader loader)
    {
        this(make(loader, FillRepeat.REPEAT));
    }

    public PatternGradient(ImageLoader loader, FillRepeat repeat)
    {
        this(make(loader, repeat));
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

        public static final native PatternGradientJSO make(JSImage image, String repeat)
        /*-{
			return {
				image : image,
				repeat : repeat,
				type : "PatternGradient"
			}
        }-*/;
    }
}
