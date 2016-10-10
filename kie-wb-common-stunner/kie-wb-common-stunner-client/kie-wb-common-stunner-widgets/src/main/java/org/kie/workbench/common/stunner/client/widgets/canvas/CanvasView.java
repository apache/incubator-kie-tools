/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

import javax.annotation.PostConstruct;

public class CanvasView extends Composite implements AbstractCanvas.View<com.ait.lienzo.client.widget.LienzoPanel> {

    private static final String Bg_COLOR = "#FFFFFF";

    protected FlowPanel mainPanel = new FlowPanel();
    protected FlowPanel toolsPanel = new FlowPanel();
    protected com.ait.lienzo.client.widget.LienzoPanel panel;
    protected Layer canvasLayer = new Layer();
    protected org.kie.workbench.common.stunner.core.client.canvas.Layer layer;

    @PostConstruct
    public void init() {
        initWidget( mainPanel );
    }

    @Override
    public AbstractCanvas.View show( final com.ait.lienzo.client.widget.LienzoPanel panel,
                                     final org.kie.workbench.common.stunner.core.client.canvas.Layer layer ) {
        this.panel = panel;
        this.layer = layer;
        layer.initialize( canvasLayer );
        panel.getElement().getStyle().setBackgroundColor( Bg_COLOR );
        mainPanel.add( toolsPanel );
        mainPanel.add( panel );
        panel.getScene().add( canvasLayer );
        return this;
    }

    @Override
    public AbstractCanvas.View add( final IsWidget widget ) {
        toolsPanel.add( widget );
        return this;
    }

    @Override
    public AbstractCanvas.View remove( final IsWidget widget ) {
        toolsPanel.remove( widget );
        return this;
    }

    @Override
    public AbstractCanvas.View addShape( final ShapeView<?> shapeView ) {
        layer.addShape( shapeView );
        return this;
    }

    @Override
    public AbstractCanvas.View removeShape( final ShapeView<?> shapeView ) {
        layer.removeShape( shapeView );
        return this;
    }

    @Override
    public AbstractCanvas.View addChildShape( final ShapeView<?> parent, final ShapeView<?> child ) {
        final WiresShape parentShape = ( WiresShape ) parent;
        final WiresShape childShape = ( WiresShape ) child;
        parentShape.add( childShape );
        return this;
    }

    @Override
    public AbstractCanvas.View removeChildShape( final ShapeView<?> parent, final ShapeView<?> child ) {
        final WiresShape parentShape = ( WiresShape ) parent;
        final WiresShape childShape = ( WiresShape ) child;
        parentShape.remove( childShape );
        return this;
    }

    @Override
    public AbstractCanvas.View dock( final ShapeView<?> parent, final ShapeView<?> child ) {
        final WiresShape parentShape = ( WiresShape ) parent;
        final WiresShape childShape = ( WiresShape ) child;
        child.removeFromParent();
        parentShape.add( childShape );
        childShape.setDockedTo( parentShape );
        return this;
    }

    @Override
    public AbstractCanvas.View undock( final ShapeView<?> parent, final ShapeView<?> child ) {
        final WiresShape parentShape = ( WiresShape ) parent;
        final WiresShape childShape = ( WiresShape ) child;
        parentShape.remove( childShape );
        childShape.setDockedTo( null );
        return this;
    }

    @Override
    public double getAbsoluteX() {
        return panel.getAbsoluteLeft();
    }

    @Override
    public double getAbsoluteY() {
        return panel.getAbsoluteTop();
    }

    @Override
    public int getWidth() {
        return panel.getWidth();
    }

    @Override
    public int getHeight() {
        return panel.getHeight();
    }

    @Override
    public AbstractCanvas.View setGrid( final CanvasGrid grid ) {
        if ( null != grid ) {
            // Grid.
            Line line1 = new Line( 0, 0, 0, 0 )
                    .setStrokeColor( grid.getPrimaryColor() )
                    .setAlpha( grid.getPrimaryAlpha() );
            Line line2 = new Line( 0, 0, 0, 0 )
                    .setStrokeColor( grid.getSecondaryColor() )
                    .setAlpha( grid.getSecondaryAlpha() );
            line2.setDashArray( 2,
                    2 );
            GridLayer gridLayer = new GridLayer( grid.getPrimarySize(),
                    line1,
                    grid.getSecondarySize(),
                    line2 );
            panel.setBackgroundLayer( gridLayer );

        } else {
            panel.setBackgroundLayer( null );

        }
        return this;
    }

    @Override
    public org.kie.workbench.common.stunner.core.client.canvas.Layer getLayer() {
        return layer;
    }

    @Override
    public AbstractCanvas.View clear() {
        layer.clear();
        return this;
    }

    @Override
    public void destroy() {
        this.mainPanel.clear();
        this.toolsPanel.clear();
        this.layer.destroy();
        this.canvasLayer.removeFromParent();
        this.panel.removeFromParent();
        this.mainPanel = null;
        this.toolsPanel = null;
        this.panel = null;
        this.layer = null;
        this.canvasLayer = null;

    }

}
