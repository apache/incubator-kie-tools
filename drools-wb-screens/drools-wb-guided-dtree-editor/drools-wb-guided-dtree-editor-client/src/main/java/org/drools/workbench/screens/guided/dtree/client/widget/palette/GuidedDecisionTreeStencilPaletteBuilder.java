/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtree.client.widget.palette;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ConstraintFactoryHelper;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.TypeFactoryHelper;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.factories.ShapeGlyph;
import org.uberfire.ext.wires.core.client.palette.PaletteShape;
import org.uberfire.ext.wires.core.client.palette.StencilPaletteBuilder;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

public class GuidedDecisionTreeStencilPaletteBuilder extends StencilPaletteBuilder {

    private static final double GLYPH_WIDTH = 30;
    private static final double GLYPH_HEIGHT = 30;
    private static final int STENCIL_CORNER_RADIUS = 5;

    public static final int STENCIL_HEIGHT = 35;
    public static final int STENCIL_WIDTH = 275;

    public PaletteShape build( final LienzoPanel dragProxyParentPanel,
                               final FactoryHelper helper,
                               final ShapeFactory factory,
                               final boolean isReadOnly ) {
        //If the Editor is not Read Only delegate to super class
        if ( !isReadOnly ) {
            return super.build( dragProxyParentPanel,
                                helper,
                                factory );
        }

        //Otherwise make a Read Only Stencil
        final PaletteShape paletteShape = new PaletteShape();
        final ShapeGlyph glyph = drawGlyph( factory,
                                            helper );
        final Text description = drawDescription( factory,
                                                  helper );
        final Rectangle bounding = drawBoundingBox( factory,
                                                    helper );

        //Build Palette Shape
        paletteShape.setBounding( bounding );
        paletteShape.setGroup( scaleGlyph( glyph ) );
        paletteShape.setDescription( description );

        return paletteShape;
    }

    @Override
    protected Group scaleGlyph( final ShapeGlyph glyph ) {
        final double sx = GLYPH_WIDTH / glyph.getWidth();
        final double sy = GLYPH_HEIGHT / glyph.getHeight();
        final Group group = glyph.getGroup();
        return group.setX( STENCIL_HEIGHT / 2 ).setY( STENCIL_HEIGHT / 2 ).setScale( sx,
                                                                                     sy );
    }

    @Override
    protected Text drawDescription( final ShapeFactory factory,
                                    final FactoryHelper helper ) {
        String description = factory.getShapeDescription();
        if ( helper instanceof TypeFactoryHelper ) {
            description = ( (TypeFactoryHelper) helper ).getContext().getClassName();
        } else if ( helper instanceof ConstraintFactoryHelper ) {
            description = ( (ConstraintFactoryHelper) helper ).getContext().getFieldName();
        }
        final Text text = new Text( description,
                                    ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                                    ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );
        text.setFillColor( Color.rgbToBrowserHexColor( 100,
                                                       100,
                                                       100 ) );
        text.setTextBaseLine( TextBaseLine.MIDDLE );
        text.setX( STENCIL_HEIGHT );
        text.setY( STENCIL_HEIGHT / 2 );
        return text;
    }

    @Override
    protected Rectangle drawBoundingBox( final ShapeFactory factory,
                                         final FactoryHelper helper ) {
        final Rectangle boundingBox = new Rectangle( STENCIL_WIDTH,
                                                     STENCIL_HEIGHT,
                                                     STENCIL_CORNER_RADIUS );
        boundingBox.setStrokeColor( ShapeFactoryUtil.RGB_STROKE_BOUNDING )
                .setStrokeWidth( 1 )
                .setFillColor( ShapeFactoryUtil.RGB_FILL_BOUNDING )
                .setDraggable( false );
        return boundingBox;
    }
}