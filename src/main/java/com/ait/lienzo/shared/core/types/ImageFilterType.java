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

package com.ait.lienzo.shared.core.types;

import com.ait.lienzo.tools.common.api.types.IStringValued;

public class ImageFilterType implements IStringValued
{
    public static final ImageFilterType AlphaScaleColorImageDataFilterType     = new ImageFilterType("AlphaScaleColorImageDataFilter");

    public static final ImageFilterType AverageGrayScaleImageDataFilterType    = new ImageFilterType("AverageGrayScaleImageDataFilter");

    public static final ImageFilterType BrightnessImageDataFilterType          = new ImageFilterType("BrightnessImageDataFilter");

    public static final ImageFilterType BumpImageDataFilterType                = new ImageFilterType("BumpImageDataFilter");

    public static final ImageFilterType ColorDeltaAlphaImageDataFilterType     = new ImageFilterType("ColorDeltaAlphaImageDataFilter");

    public static final ImageFilterType ColorLuminosityImageDataFilterType     = new ImageFilterType("ColorLuminosityImageDataFilter");

    public static final ImageFilterType ContrastImageDataFilterType            = new ImageFilterType("ContrastImageDataFilter");

    public static final ImageFilterType DiffusionImageDataFilterType           = new ImageFilterType("DiffusionImageDataFilter");

    public static final ImageFilterType EdgeDetectImageDataFilterType          = new ImageFilterType("EdgeDetectImageDataFilter");

    public static final ImageFilterType EmbossImageDataFilterType              = new ImageFilterType("EmbossImageDataFilter");

    public static final ImageFilterType ExposureImageDataFilterType            = new ImageFilterType("ExposureImageDataFilter");

    public static final ImageFilterType GainImageDataFilterType                = new ImageFilterType("GainImageDataFilter");

    public static final ImageFilterType GammaImageDataFilterType               = new ImageFilterType("GammaImageDataFilter");

    public static final ImageFilterType HueImageDataFilterType                 = new ImageFilterType("HueImageDataFilter");

    public static final ImageFilterType ImageDataFilterChainType               = new ImageFilterType("ImageDataFilterChain");

    public static final ImageFilterType InvertColorImageDataFilterType         = new ImageFilterType("InvertColorImageDataFilter");

    public static final ImageFilterType LightnessGrayScaleImageDataFilterType  = new ImageFilterType("LightnessGrayScaleImageDataFilter");

    public static final ImageFilterType LuminosityGrayScaleImageDataFilterType = new ImageFilterType("LuminosityGrayScaleImageDataFilter");

    public static final ImageFilterType PosterizeImageDataFilterType           = new ImageFilterType("PosterizeImageDataFilter");

    public static final ImageFilterType RGBIgnoreAlphaImageDataFilterType      = new ImageFilterType("RGBIgnoreAlphaImageDataFilter");

    public static final ImageFilterType SharpenImageDataFilterType             = new ImageFilterType("SharpenImageDataFilter");

    public static final ImageFilterType SolarizeImageDataFilterType            = new ImageFilterType("SolarizeImageDataFilter");

    public static final ImageFilterType StackBlurImageDataFilterType           = new ImageFilterType("StackBlurImageDataFilter");

    private final String                m_value;

    protected ImageFilterType(final String value)
    {
        m_value = value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof ImageFilterType))
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        return ((ImageFilterType) other).getValue().equals(getValue());
    }

    @Override
    public int hashCode()
    {
        return getValue().hashCode();
    }
}
