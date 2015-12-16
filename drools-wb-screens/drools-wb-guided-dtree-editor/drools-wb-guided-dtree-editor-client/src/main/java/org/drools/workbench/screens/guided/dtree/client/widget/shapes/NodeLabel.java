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
package org.drools.workbench.screens.guided.dtree.client.widget.shapes;

import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.event.shared.HandlerRegistration;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

public class NodeLabel extends Group {

    private final Rectangle container = new Rectangle( 0, 0, 5 );

    private final Text text = new Text( "",
                                        ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                                        ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );

    public NodeLabel() {
        this( " " );
    }

    public NodeLabel( final String label ) {
        container.setFillColor( Color.rgbToBrowserHexColor( 255,
                                                            255,
                                                            255 ) );
        container.setStrokeWidth( 1.0 );
        container.setStrokeColor( Color.rgbToBrowserHexColor( 180,
                                                              180,
                                                              180 ) );
        container.setAlpha( 0.50 );
        text.setTextAlign( TextAlign.CENTER );
        text.setTextBaseLine( TextBaseLine.MIDDLE );
        text.setFillColor( Color.rgbToBrowserHexColor( 0,
                                                       0,
                                                       0 ) );

        add( container );
        add( text );

        setLabel( label );
    }

    public void setLabel( final String label ) {
        final Layer scratchLayer = new Layer();
        final LienzoPanel scratchPanel = new LienzoPanel( 100, 100 );
        scratchPanel.add( scratchLayer );

        text.setText( label );
        final TextMetrics tm = text.measure( scratchLayer.getContext() );

        final double cw = tm.getWidth() + 10;
        final double ch = tm.getHeight() + 10;
        container.setWidth( cw );
        container.setHeight( ch );
        container.setLocation( new Point2D( -cw / 2,
                                            -ch / 2 ) );
    }

    @Override
    public HandlerRegistration addNodeMouseClickHandler( final NodeMouseClickHandler handler ) {
        final HandlerRegistration ch = container.addNodeMouseClickHandler( handler );
        final HandlerRegistration th = text.addNodeMouseClickHandler( handler );
        final HandlerRegistration nh = super.addNodeMouseClickHandler( handler );
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                ch.removeHandler();
                th.removeHandler();
                nh.removeHandler();
            }
        };
    }

    public double getWidth() {
        return container.getWidth();
    }

    public double getHeight() {
        return container.getHeight();
    }

}
