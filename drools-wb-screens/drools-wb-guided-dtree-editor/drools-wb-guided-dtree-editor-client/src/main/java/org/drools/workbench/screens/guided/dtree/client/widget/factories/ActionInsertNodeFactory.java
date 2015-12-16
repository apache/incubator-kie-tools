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
package org.drools.workbench.screens.guided.dtree.client.widget.factories;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionInsertNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.ActionInsertShape;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.TypeShape;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

@ApplicationScoped
public class ActionInsertNodeFactory extends BaseGuidedDecisionTreeNodeFactory<Circle> {

    public static final String DESCRIPTION = "Insert";

    private static final int SHAPE_RADIUS = 25;

    @Override
    public String getShapeDescription() {
        return DESCRIPTION;
    }

    /**
     * This returns a new Shape following a drag operation from the palette
     * @param helper
     * @return
     */
    @Override
    public WiresBaseShape getShape( final FactoryHelper helper ) {
        final ActionInsertFactoryHelper anHelper = (ActionInsertFactoryHelper) helper;
        final ActionInsertNode node = anHelper.getContext();

        //The ActionUpdateNode associated with the FactoryHelper is used to show the action's description on the
        //drag proxy. We need to create a new instance of the TypeNode for use in the Decision Tree Widget
        return new ActionInsertShape( makeShape(),
                                      new ActionInsertNodeImpl( node.getClassName() ),
                                      anHelper.isReadOnly() );
    }

    /**
     * This returns a new Shape representing an existing Node
     * @param node
     * @param isReadOnly
     * @return
     */
    public ActionInsertShape getShape( final ActionInsertNode node,
                                       final boolean isReadOnly ) {
        return new ActionInsertShape( makeShape(),
                                      node,
                                      isReadOnly );
    }

    @Override
    protected String getNodeLabel( final FactoryHelper helper ) {
        return DESCRIPTION;
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof TypeShape;
    }

    @Override
    protected Circle makeShape() {
        final Circle circle = new Circle( SHAPE_RADIUS );
        circle.setStrokeColor( Color.rgbToBrowserHexColor( 100,
                                                           100,
                                                           100 ) )
                .setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE )
                .setFillColor( Color.rgbToBrowserHexColor( 255,
                                                           255,
                                                           0 ) )
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
