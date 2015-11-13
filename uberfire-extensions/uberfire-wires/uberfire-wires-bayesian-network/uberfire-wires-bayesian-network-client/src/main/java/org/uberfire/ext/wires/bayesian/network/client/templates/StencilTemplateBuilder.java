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
package org.uberfire.ext.wires.bayesian.network.client.templates;

import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

/**
 *
 */
public class StencilTemplateBuilder {

    public TemplateShape build( final String templateName,
                                final NodeMouseClickHandler clickHandler ) {

        final TemplateShape templateShape = new TemplateShape();
        final Shape shape = drawShape();
        final Rectangle bounding = drawBoundingBox();
        final Text description = drawDescription( templateName );

        //Attach handles for drag operation
        shape.addNodeMouseClickHandler( clickHandler );
        bounding.addNodeMouseClickHandler( clickHandler );
        description.addNodeMouseClickHandler( clickHandler );

        //Build Template Shape
        templateShape.setBounding( bounding );
        templateShape.setShape( shape );
        templateShape.setDescription( description );

        return templateShape;
    }

    private Shape drawShape() {
        final Rectangle rectangle = new Rectangle( 20,
                                                   20 );

        rectangle.setX( 5 ).setY( 5 )
                .setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setFillColor( ShapesUtils.RGB_FILL_SHAPE )
                .setDraggable( false );

        return rectangle;
    }

    private Rectangle drawBoundingBox() {
        final Rectangle boundingBox = new Rectangle( ShapeFactoryUtil.WIDTH_BOUNDING_LAYER,
                                                     ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER );
        boundingBox.setStrokeColor( ShapeFactoryUtil.RGB_STROKE_BOUNDING )
                .setStrokeWidth( 1 )
                .setFillColor( ShapeFactoryUtil.RGB_FILL_BOUNDING )
                .setDraggable( false );
        return boundingBox;
    }

    private Text drawDescription( final String templateName ) {
        Text text = new Text( templateName,
                              ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                              ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );
        text.setFillColor( ShapeFactoryUtil.RGB_TEXT_DESCRIPTION );
        text.setTextBaseLine( TextBaseLine.MIDDLE );
        text.setX( 30 );
        text.setY( 10 );
        return text;
    }

}
