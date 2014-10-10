package org.kie.uberfire.wires.core.client.util;

import java.util.Set;

import org.kie.uberfire.wires.core.api.factories.ShapeFactory;
import org.kie.uberfire.wires.core.api.factories.categories.Category;

public class ShapesUtils {

    // Control Points
    public static final String CP_RGB_FILL_COLOR = "#0000FF";
    public static final int CP_RGB_STROKE_WIDTH_SHAPE = 1;

    // Magnets
    public static final String MAGNET_RGB_FILL_SHAPE = "#F2F2F2";
    public static final String MAGNET_ACTIVE_RGB_FILL_SHAPE = "#00FF00";

    // shapes
    public static final String RGB_STROKE_SHAPE = "#999999";
    public static final String RGB_FILL_SHAPE = "#F2F2F2";
    public static final int RGB_STROKE_WIDTH_SHAPE = 2;

    //Containers
    public static final String RGB_STROKE_CONTAINER = "#FF9900";
    public static final String RGB_FILL_CONTAINER = "#FFFF66";
    public static final String RGB_STROKE_HOVER_CONTAINER = "#33CC33";
    public static final String RGB_FILL_HOVER_CONTAINER = "#33FF33";
    public static final int RGB_STROKE_WIDTH_CONTAINER = 4;
    public static final double RGB_ALPHA_CONTAINER = 0.75;

    //Text
    public static final String RGB_STROKE_TEXT = "#181818";
    public static final String RGB_FILL_TEXT = "#181818";

    public static int getNumberOfShapesInCategory( final Category shapeCategory,
                                                   final Set<ShapeFactory> factories ) {
        int account = 0;
        for ( ShapeFactory factory : factories ) {
            if ( factory.getCategory().equals( shapeCategory ) ) {
                account++;
            }
        }
        return account;
    }

    public static int calculateHeight( int shapes ) {
        int y = shapes > 1 ? getRow( shapes ) : 0;
        y = y > 0 ? ( y * ShapeFactoryUtil.HEIGHT_BOUNDING ) + ShapeFactoryUtil.SPACE_BETWEEN_BOUNDING * y : y
                * ShapeFactoryUtil.HEIGHT_BOUNDING;
        return y + ShapeFactoryUtil.HEIGHT_BOUNDING + 15;
    }

    public static int getRow( int shapes ) {
        return Math.round( ( shapes * ShapeFactoryUtil.WIDTH_BOUNDING ) / ShapeFactoryUtil.WIDTH_STENCIL );
    }

}
