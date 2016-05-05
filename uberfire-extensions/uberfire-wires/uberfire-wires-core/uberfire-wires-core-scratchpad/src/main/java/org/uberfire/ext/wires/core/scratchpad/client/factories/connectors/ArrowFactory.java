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
package org.uberfire.ext.wires.core.scratchpad.client.factories.connectors;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.AbstractBaseFactory;
import org.uberfire.ext.wires.core.client.factories.categories.ConnectorCategory;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;
import org.uberfire.ext.wires.core.scratchpad.client.shapes.connectors.WiresArrow;

@ApplicationScoped
public class ArrowFactory extends AbstractBaseFactory<Arrow> {

    private static final String DESCRIPTION = "Arrow";

    private static final int BASE_WIDTH = 10;
    private static final int HEAD_WIDTH = 20;
    private static final int ARROW_ANGLE = 45;
    private static final int BASE_ANGLE = 30;

    private static final int SHAPE_SIZE_X = 50;
    private static final int SHAPE_SIZE_Y = 50;

    @Override
    public String getShapeDescription() {
        return DESCRIPTION;
    }

    @Override
    public Category getCategory() {
        return ConnectorCategory.CATEGORY;
    }

    @Override
    public WiresBaseShape getShape( final FactoryHelper helper ) {
        return new WiresArrow( makeShape() );
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof WiresArrow;
    }

    @Override
    protected Arrow makeShape() {
        final Arrow arrow = new Arrow( new Point2D( 0 - ( SHAPE_SIZE_X / 2 ),
                                                    0 - ( SHAPE_SIZE_Y / 2 ) ),
                                       new Point2D( SHAPE_SIZE_X / 2,
                                                    SHAPE_SIZE_Y / 2 ),
                                       BASE_WIDTH,
                                       HEAD_WIDTH,
                                       ARROW_ANGLE,
                                       BASE_ANGLE,
                                       ArrowType.AT_END );
        arrow.setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( 1 )
                .setFillColor( "#ffff00" )
                .setDraggable( false );
        return arrow;
    }

    @Override
    protected double getWidth() {
        return SHAPE_SIZE_X + 10;
    }

    @Override
    protected double getHeight() {
        return SHAPE_SIZE_Y + 10;
    }
}