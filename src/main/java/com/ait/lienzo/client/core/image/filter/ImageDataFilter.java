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

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.types.ImageData;

/**
 * Interface to be used to create {@link Picture} and {@link Movie} filters.
 */
public interface ImageDataFilter
{
    public static int                            R_OFFSET        = 0;

    public static int                            G_OFFSET        = 1;

    public static int                            B_OFFSET        = 2;

    public static int                            A_OFFSET        = 3;

    public static int                            PIXEL_SZ        = 4;

    public static final ImageDataFilterCommonOps FilterCommonOps = ImageDataFilterCommonOps.make();

    public ImageData filter(ImageData source, boolean copy);

    public boolean isTransforming();

    public boolean isActive();

    public void setActive(boolean active);

    public String getName();

    public void setName(String name);
}
