package org.kie.uberfire.wires.core.client.util;

import com.emitrom.lienzo.shared.core.types.Color;

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