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
package org.uberfire.ext.wires.core.trees.client.shapes;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

/**
 * Example Node that can have all other types of node added as children
 */
public class WiresExampleTreeNode1 extends WiresBaseTreeNode {

    private static final int BOUNDARY_SIZE = 10;

    private final Circle circle;
    private final Circle bounding;
    private final Text plus = new Text( "+",
                                        "normal",
                                        50 );

    public WiresExampleTreeNode1( final Circle shape ) {
        circle = shape;

        bounding = new Circle( circle.getRadius() + ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        plus.setTextAlign( TextAlign.CENTER );
        plus.setTextBaseLine( TextBaseLine.MIDDLE );
        plus.setFillColor( ColorName.CORNFLOWERBLUE );
        plus.setStrokeColor( ColorName.BLUE );

        add( circle );
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            add( bounding );
        } else {
            remove( bounding );
        }
    }

    @Override
    public void onCollapseStart() {
        add( plus );
        plus.setAlpha( 0.0 );
    }

    @Override
    public void onCollapseProgress( final double pct ) {
        plus.setAlpha( pct );
    }

    @Override
    public void onExpandProgress( double pct ) {
        plus.setAlpha( 1.0 - pct );
    }

    @Override
    public void onExpandEnd() {
        remove( plus );
    }

    @Override
    public double getWidth() {
        return circle.getRadius() * 2;
    }

    @Override
    public double getHeight() {
        return circle.getRadius() * 2;
    }

}
