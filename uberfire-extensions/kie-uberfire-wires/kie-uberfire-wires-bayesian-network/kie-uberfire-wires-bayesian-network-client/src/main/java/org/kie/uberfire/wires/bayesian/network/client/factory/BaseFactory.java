package org.kie.uberfire.wires.bayesian.network.client.factory;

import com.emitrom.lienzo.client.core.shape.Rectangle;
import com.emitrom.lienzo.client.core.shape.Shape;
import com.emitrom.lienzo.client.core.shape.Text;
import com.emitrom.lienzo.shared.core.types.Color;
import org.kie.uberfire.wires.core.client.util.ShapesUtils;

public class BaseFactory {

    private static final String defaultFillColor = ShapesUtils.RGB_FILL_SHAPE;
    private static final String defaultBorderColor = ShapesUtils.RGB_STROKE_SHAPE;

    protected void setAttributes( final Shape<?> shape,
                                  final String fillColor,
                                  final double x,
                                  final double y,
                                  final String borderColor ) {
        String fill = ( fillColor == null ) ? defaultFillColor : fillColor;
        String border = ( borderColor == null ) ? defaultBorderColor : borderColor;

        shape.setX( x ).setY( y ).setStrokeColor( border ).setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE ).setFillColor( fill ).setDraggable( false );
    }

    protected Rectangle drawComponent( final String color,
                                       final int positionX,
                                       final int positionY,
                                       final int width,
                                       final int height,
                                       String borderColor,
                                       double radius ) {
        if ( borderColor == null ) {
            borderColor = Color.rgbToBrowserHexColor( 0, 0, 0 );
        }
        Rectangle component = new Rectangle( width,
                                             height );
        setAttributes( component,
                       color,
                       positionX,
                       positionY,
                       borderColor );
        component.setCornerRadius( radius );
        return component;
    }

    protected Text drawText( final String description,
                             final int fontSize,
                             final int positionX,
                             final int positionY ) {
        return new Text( description,
                         "Times",
                         fontSize ).setX( positionX ).setY( positionY );
    }

}
