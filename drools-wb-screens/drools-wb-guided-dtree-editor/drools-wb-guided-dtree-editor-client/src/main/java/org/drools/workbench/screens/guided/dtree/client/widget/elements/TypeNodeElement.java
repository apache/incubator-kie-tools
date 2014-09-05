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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.screens.guided.dtree.client.widget.model.Coordinate;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A UI element that represents a Type
 */
public class TypeNodeElement implements UIModelElement {

    private static final int NODE_RADIUS = 25;

    private Coordinate c;
    private TypeNode node;

    public TypeNodeElement( final Coordinate c,
                            final TypeNode node ) {
        this.c = PortablePreconditions.checkNotNull( "c",
                                                     c );
        this.node = PortablePreconditions.checkNotNull( "node",
                                                        node );
    }

    @Override
    public void draw( final Canvas canvas ) {
        final Context2d context = canvas.getContext2d();
        context.setFillStyle( CssColor.make( 60, 200, 126 ) );
        context.beginPath();
        context.arc( c.getX(),
                     c.getY(),
                     NODE_RADIUS,
                     0,
                     Math.PI * 2.0,
                     true );
        context.closePath();
        context.fill();

        context.setFont( "10pt sans-serif" );
        context.setTextAlign( Context2d.TextAlign.CENTER );
        context.setTextBaseline( Context2d.TextBaseline.MIDDLE );
        context.setFillStyle( CssColor.make( 0, 0, 0 ) );
        context.fillText( getTypeNodeText( node ),
                          c.getX(),
                          c.getY() );
    }

    @Override
    public Coordinate getCoordinate() {
        return this.c;
    }

    @Override
    public Node getNode() {
        return this.node;
    }

    public int getRadius() {
        return NODE_RADIUS;
    }

    private String getTypeNodeText( final TypeNode node ) {
        return node.getClassName();
    }

}
