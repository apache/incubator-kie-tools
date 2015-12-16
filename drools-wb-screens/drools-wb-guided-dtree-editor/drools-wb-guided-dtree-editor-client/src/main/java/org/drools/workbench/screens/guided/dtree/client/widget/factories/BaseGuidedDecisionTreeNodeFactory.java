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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.categories.GuidedDecisionTreeEditorCategory;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.ConstraintShape;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxy;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyCompleteCallback;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyPreviewCallback;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.AbstractBaseFactory;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

public abstract class BaseGuidedDecisionTreeNodeFactory<T extends Shape<T>> extends AbstractBaseFactory<T> {

    @Override
    public Category getCategory() {
        return GuidedDecisionTreeEditorCategory.CATEGORY;
    }

    @Override
    public ShapeDragProxy getDragProxy( final FactoryHelper helper,
                                        final ShapeDragProxyPreviewCallback dragPreviewCallback,
                                        final ShapeDragProxyCompleteCallback dragEndCallBack ) {
        final T shape = makeShape();
        final Group group = new Group();
        group.add( shape );

        //Add a label to the drag proxy if needed
        double dragProxyWidth = getWidth();
        double dragProxyHeight = getHeight();
        final String description = getNodeLabel( helper );

        if ( description != null ) {
            final Text nodeLabel = new Text( description,
                                             ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                                             ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );
            nodeLabel.setTextAlign( TextAlign.CENTER );
            nodeLabel.setTextBaseLine( TextBaseLine.MIDDLE );
            nodeLabel.setFillColor( Color.rgbToBrowserHexColor( 0,
                                                                0,
                                                                0 ) );
            dragProxyWidth = getDragProxyWidth( nodeLabel );
            dragProxyHeight = getDragProxyHeight( nodeLabel );
            group.add( nodeLabel );
        }

        final double _dragProxyWidth = dragProxyWidth;
        final double _dragProxyHeight = dragProxyHeight;

        return new ShapeDragProxy() {
            @Override
            public Group getDragGroup() {
                return group;
            }

            @Override
            public void onDragPreview( final double x,
                                       final double y ) {
                dragPreviewCallback.callback( x,
                                              y );
            }

            @Override
            public void onDragComplete( final double x,
                                        final double y ) {
                dragEndCallBack.callback( x,
                                          y );
            }

            @Override
            public double getWidth() {
                return _dragProxyWidth;
            }

            @Override
            public double getHeight() {
                return _dragProxyHeight;
            }

        };
    }

    protected abstract String getNodeLabel( final FactoryHelper helper );

    private double getDragProxyWidth( final Text nodeLabel ) {
        final LienzoPanel panel = new LienzoPanel( 100,
                                                   100 );
        final Layer layer = new Layer();
        panel.add( layer );
        final TextMetrics tm = nodeLabel.measure( layer.getContext() );
        return Math.max( getWidth(),
                         tm.getWidth() );
    }

    private double getDragProxyHeight( final Text nodeLabel ) {
        final LienzoPanel panel = new LienzoPanel( 100,
                                                   100 );
        final Layer layer = new Layer();
        panel.add( layer );
        final TextMetrics tm = nodeLabel.measure( layer.getContext() );
        return Math.max( getHeight(),
                         tm.getHeight() );
    }

    @Override
    public boolean builds( final WiresBaseShape shapeType ) {
        return shapeType instanceof ConstraintShape;
    }

}
