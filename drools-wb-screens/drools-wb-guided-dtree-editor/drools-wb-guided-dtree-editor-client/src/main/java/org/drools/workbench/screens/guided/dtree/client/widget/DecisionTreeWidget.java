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
package org.drools.workbench.screens.guided.dtree.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.ConnectorElement;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.ConstraintNodeElement;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.TypeNodeElement;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.UIElement;
import org.drools.workbench.screens.guided.dtree.client.widget.model.Coordinate;
import org.drools.workbench.screens.guided.dtree.client.widget.model.UIModelUtilities;

public class DecisionTreeWidget extends Composite {

    private final int width = 400;
    private final int height = 400;

    private Canvas canvas;

    private List<UIElement> uiModel = new ArrayList<UIElement>();
    private UIModelUtilities uiModelUtilities = new UIModelUtilities( uiModel );

    public DecisionTreeWidget() {
        if ( !Canvas.isSupported() ) {
            initWidget( new Label( "Canvas is not supported" ) );
            return;
        }

        canvas = Canvas.createIfSupported();
        canvas.setWidth( width + "px" );
        canvas.setHeight( height + "px" );
        canvas.setCoordinateSpaceWidth( width );
        canvas.setCoordinateSpaceHeight( height );

        initWidget( canvas );
    }

    public void setModel( final GuidedDecisionTree model ) {
        final Coordinate c = new Coordinate( width / 2,
                                             50 );

        //Walk model creating UIModel
        final TypeNode root = model.getRoot();
        uiModel.add( new TypeNodeElement( c,
                                          root ) );
        processChildren( c,
                         root );

        //Render UIModel
        for ( UIElement element : uiModel ) {
            element.draw( canvas );
        }
    }

    private void processChildren( final Coordinate c,
                                  final Node node ) {
        final int childCount = node.getChildren().size();
        for ( int childIndex = 0; childIndex < childCount; childIndex++ ) {
            final Node child = node.getChildren().get( childIndex );
            final Coordinate cc = new Coordinate( c.getX() - ( ( ( childCount - 1 ) * 100 ) / 2 ) + childIndex * 100,
                                                  c.getY() + 100 );
            if ( child instanceof TypeNode ) {
                uiModel.add( new TypeNodeElement( cc,
                                                  (TypeNode) child ) );
            } else if ( child instanceof ConstraintNode ) {
                uiModel.add( new ConstraintNodeElement( cc,
                                                        (ConstraintNode) child ) );
            }
            uiModel.add( new ConnectorElement( uiModelUtilities.getUIElement( node ),
                                               uiModelUtilities.getUIElement( child ) ) );
            processChildren( cc,
                             child );
        }
    }

}
