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
package org.uberfire.ext.wires.core.scratchpad.client.factories.dynamic;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Circle;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.AbstractBaseFactory;
import org.uberfire.ext.wires.core.client.factories.categories.ShapeCategory;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;
import org.uberfire.ext.wires.core.scratchpad.client.shapes.dynamic.WiresCircle;

@ApplicationScoped
public class CircleFactory extends AbstractBaseFactory<Circle> {

    private static final String DESCRIPTION = "Circle";

    private static final int SHAPE_RADIUS = 25;

    @Override
    public String getShapeDescription() {
        return DESCRIPTION;
    }

    @Override
    public Category getCategory() {
        return ShapeCategory.CATEGORY;
    }

    @Override
    public WiresBaseShape getShape( final FactoryHelper helper ) {
        return new WiresCircle( makeShape() );
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof WiresCircle;
    }

    @Override
    protected Circle makeShape() {
        final Circle circle = new Circle( SHAPE_RADIUS );
        circle.setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setFillColor( "#00ff00" )
                .setDraggable( false );
        return circle;
    }

    @Override
    protected double getWidth() {
        return ( SHAPE_RADIUS + ShapesUtils.RGB_STROKE_WIDTH_SHAPE ) * 2;
    }

    @Override
    protected double getHeight() {
        return ( SHAPE_RADIUS + ShapesUtils.RGB_STROKE_WIDTH_SHAPE ) * 2;
    }

}
