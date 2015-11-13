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

import com.ait.lienzo.client.core.shape.BezierCurve;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.AbstractBaseFactory;
import org.uberfire.ext.wires.core.client.factories.categories.ConnectorCategory;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;
import org.uberfire.ext.wires.core.scratchpad.client.shapes.connectors.WiresBezierCurve;

@ApplicationScoped
public class BezierCurveFactory extends AbstractBaseFactory<BezierCurve> {

    private static final String DESCRIPTION = "Curve";

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
        return new WiresBezierCurve( makeShape() );
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof WiresBezierCurve;
    }

    @Override
    protected BezierCurve makeShape() {
        final BezierCurve curve = new BezierCurve( 0 - SHAPE_SIZE_X,
                                                   0 - SHAPE_SIZE_Y,
                                                   0 - SHAPE_SIZE_X,
                                                   SHAPE_SIZE_Y,
                                                   SHAPE_SIZE_X,
                                                   0 - SHAPE_SIZE_Y,
                                                   SHAPE_SIZE_X,
                                                   SHAPE_SIZE_Y );
        curve.setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setDraggable( false );
        return curve;
    }

    @Override
    protected double getWidth() {
        return ( SHAPE_SIZE_X + ShapesUtils.RGB_STROKE_WIDTH_SHAPE ) * 2;
    }

    @Override
    protected double getHeight() {
        return ( SHAPE_SIZE_Y + ShapesUtils.RGB_STROKE_WIDTH_SHAPE ) * 2;
    }

}
