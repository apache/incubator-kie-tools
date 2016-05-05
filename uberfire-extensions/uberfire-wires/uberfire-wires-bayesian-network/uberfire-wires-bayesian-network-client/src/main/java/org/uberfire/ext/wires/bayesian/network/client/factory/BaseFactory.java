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

package org.uberfire.ext.wires.bayesian.network.client.factory;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

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
