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
import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.drools.workbench.screens.guided.dtree.client.widget.model.Coordinate;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A UI element that represents a Constraint
 */
public class ConstraintNodeElement implements UIModelElement {

    private static final String DROOLS_DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat( DROOLS_DATE_FORMAT );
    private static final int NODE_RADIUS = 15;

    private Coordinate c;
    private ConstraintNode node;

    public ConstraintNodeElement( final Coordinate c,
                                  final ConstraintNode node ) {
        this.c = PortablePreconditions.checkNotNull( "c",
                                                     c );
        this.node = PortablePreconditions.checkNotNull( "node",
                                                        node );
    }

    @Override
    public void draw( final Canvas canvas ) {
        final Context2d context = canvas.getContext2d();
        context.setFillStyle( CssColor.make( 200, 100, 50 ) );
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
        context.fillText( getConstraintNodeText( node ),
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

    private String getConstraintNodeText( final ConstraintNode node ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( node.getFieldName() ).append( " " ).append( node.getOperator() ).append( " " ).append( getConstraintNodeValueText( node.getValue() ) );
        return sb.toString();
    }

    private StringBuilder getConstraintNodeValueText( final Value value ) {
        final StringBuilder sb = new StringBuilder();
        if ( value instanceof StringValue ) {
            final StringValue sv = (StringValue) value;
            return sb.append( "\"" ).append( sv.getValue() ).append( "\"" );
        } else if ( value instanceof DateValue ) {
            final DateValue dv = (DateValue) value;
            return sb.append( "\"" ).append( DATE_FORMAT.format( dv.getValue() ) ).append( "\"" );
        }
        return sb.append( value.getValue() );
    }
}
