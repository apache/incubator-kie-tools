/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.wires.bayesian.network.client.factory;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.wires.bayesian.network.client.shapes.EditableBayesianNode;
import com.emitrom.lienzo.client.core.shape.Rectangle;
import org.kie.uberfire.wires.core.api.factories.FactoryHelper;
import org.kie.uberfire.wires.core.api.factories.categories.Category;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseShape;
import org.kie.uberfire.wires.core.client.factories.AbstractBaseFactory;
import org.kie.uberfire.wires.core.client.util.ShapesUtils;

@ApplicationScoped
public class BayesianNodePaletteFactory extends AbstractBaseFactory<Rectangle> {

    private static final String DESCRIPTION = "Bayesian Node";

    private static final int SHAPE_SIZE_X = 40;
    private static final int SHAPE_SIZE_Y = 40;

    @Override
    public String getShapeDescription() {
        return DESCRIPTION;
    }

    @Override
    public Category getCategory() {
        return BayesianNodeCategory.CATEGORY;
    }

    @Override
    public WiresBaseShape getShape( final FactoryHelper helper ) {
        return new EditableBayesianNode( makeShape() );
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof EditableBayesianNode;
    }

    @Override
    protected Rectangle makeShape() {
        final Rectangle rectangle = new Rectangle( SHAPE_SIZE_X,
                                                   SHAPE_SIZE_Y,
                                                   5 );
        rectangle.setOffset( 0 - ( SHAPE_SIZE_X / 2 ),
                             0 - ( SHAPE_SIZE_Y / 2 ) )
                .setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setFillColor( ShapesUtils.RGB_FILL_SHAPE )
                .setDraggable( false );
        return rectangle;
    }

    @Override
    protected double getWidth() {
        return SHAPE_SIZE_X;
    }

    @Override
    protected double getHeight() {
        return SHAPE_SIZE_Y;
    }

}