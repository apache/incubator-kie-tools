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
package org.drools.workbench.screens.guided.dtree.client.widget.elements;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A UI element that connects two other UI elements together
 */
public class ConnectorElement implements UIElement {

    private UIModelElement element1;
    private UIModelElement element2;

    public ConnectorElement( final UIModelElement element1,
                             final UIModelElement element2 ) {
        this.element1 = PortablePreconditions.checkNotNull( "element1",
                                                            element1 );
        this.element2 = PortablePreconditions.checkNotNull( "element2",
                                                            element2 );
    }

    @Override
    public void draw( final Canvas canvas ) {
        final double deltaX = element2.getCoordinate().getX() - element1.getCoordinate().getX();
        final double deltaY = element2.getCoordinate().getY() - element1.getCoordinate().getY();
        final double theta = Math.atan2( deltaY, deltaX );

        final double offsetX1 = Math.cos( theta ) * getElementRadius( element1 );
        final double offsetY1 = Math.sin( theta ) * getElementRadius( element1 );

        final double offsetX2 = Math.cos( theta ) * getElementRadius( element2 );
        final double offsetY2 = Math.sin( theta ) * getElementRadius( element2 );

        final Context2d context = canvas.getContext2d();
        context.moveTo( element1.getCoordinate().getX() + offsetX1,
                        element1.getCoordinate().getY() + offsetY1 );
        context.lineTo( element2.getCoordinate().getX() - offsetX2,
                        element2.getCoordinate().getY() - offsetY2 );
        context.setStrokeStyle( CssColor.make( 100, 100, 100 ) );
        context.stroke();
    }

    private double getElementRadius( final UIElement element ) {
        if ( element instanceof TypeNodeElement ) {
            return ( (TypeNodeElement) element ).getRadius();
        } else if ( element instanceof ConstraintNodeElement ) {
            return ( (ConstraintNodeElement) element ).getRadius();
        }
        throw new IllegalArgumentException( "Unexpected node type" );
    }

}
