/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.client.util;

import com.ait.lienzo.shared.core.types.Color;

public class ShapeFactoryUtil {

    // stencil
    public static final int BOUNDINGS_BY_STENCIL = 5;
    public static final int WIDTH_STENCIL = ( ShapeFactoryUtil.BOUNDINGS_BY_STENCIL * ShapeFactoryUtil.WIDTH_BOUNDING )
            + ( ShapeFactoryUtil.BOUNDINGS_BY_STENCIL * ShapeFactoryUtil.SPACE_BETWEEN_BOUNDING );

    // panel
    public static final int WIDTH_PANEL = ShapeFactoryUtil.WIDTH_STENCIL + 5;
    public static final int HEIGHT_PANEL = 300;

    // bounding Shape
    public static final int WIDTH_BOUNDING = 75;
    public static final int HEIGHT_BOUNDING = 100;
    public static final String RGB_FILL_BOUNDING = Color.rgbToBrowserHexColor( 255, 255, 255 );
    public static final String RGB_STROKE_BOUNDING = Color.rgbToBrowserHexColor( 219, 217, 217 );
    public static final int SPACE_BETWEEN_BOUNDING = 5;

    // bounding Layer
    public static final int WIDTH_BOUNDING_LAYER = 250;
    public static final int HEIGHT_BOUNDING_LAYER = 30;

    // text
    public static final String RGB_TEXT_DESCRIPTION = Color.rgbToBrowserHexColor( 188, 187, 189 );
    public static final String FONT_FAMILY_DESCRIPTION = "oblique normal";
    public static final double FONT_SIZE_DESCRIPTION = 10;
    public static final double FONT_SIZE_WIDTH = 6;

}