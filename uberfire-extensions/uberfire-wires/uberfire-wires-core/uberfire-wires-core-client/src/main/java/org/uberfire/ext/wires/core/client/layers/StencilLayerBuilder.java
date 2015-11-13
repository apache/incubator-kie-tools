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
package org.uberfire.ext.wires.core.client.layers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.wires.core.api.events.ShapeSelectedEvent;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.factories.ShapeGlyph;
import org.uberfire.ext.wires.core.api.shapes.OverridesFactoryDescription;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

@ApplicationScoped
public class StencilLayerBuilder extends Composite {

    private static final int GLYPH_WIDTH = 25;
    private static final int GLYPH_HEIGHT = 25;

    @Inject
    private Event<ShapeSelectedEvent> shapeSelectedEvent;

    public LayerShape build( final WiresBaseShape shape,
                             final ShapeFactory factory ) {
        final LayerShape layerShape = new LayerShape();
        final Rectangle bounding = drawBoundingBox();
        final ShapeGlyph glyph = factory.getGlyph();

        //Get display name to show in Panel
        final String name = ( shape instanceof OverridesFactoryDescription ) ? ( (OverridesFactoryDescription) shape ).getDescription() : factory.getShapeDescription();
        final Text description = drawDescription( name );

        //Clicking on the Shape selects it - Lienzo doesn't support bubbling click events down through
        //overlapping items as it uses a bitmap SelectionLayer to detect mouse-clicks. Therefore we need
        //to attach the handler to all elements
        final NodeMouseClickHandler handler = new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent nodeMouseClickEvent ) {
                shapeSelectedEvent.fire( new ShapeSelectedEvent( shape ) );
            }
        };
        layerShape.addNodeMouseClickHandler( handler );

        //Build Layer Shape
        layerShape.setBounding( bounding );
        layerShape.setDescription( description );
        layerShape.setGroup( scaleGlyph( glyph ) );

        return layerShape;
    }

    private Group scaleGlyph( final ShapeGlyph glyph ) {
        final double sx = GLYPH_WIDTH / glyph.getWidth();
        final double sy = GLYPH_HEIGHT / glyph.getHeight();
        final Group group = glyph.getGroup();
        return group.setX( ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER / 2 ).setY( ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER / 2 ).setScale( sx,
                                                                                                                                     sy );
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

    private Text drawDescription( final String description ) {
        Text text = new Text( description,
                              ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                              ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );
        text.setFillColor( ShapeFactoryUtil.RGB_TEXT_DESCRIPTION );
        text.setTextBaseLine( TextBaseLine.MIDDLE );
        text.setX( 40 );
        text.setY( 15 );
        return text;
    }

}