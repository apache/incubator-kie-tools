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

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionInsertNodeFactory;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;

public class ActionInsertShape extends BaseGuidedDecisionTreeShape<ActionInsertNode> {

    public ActionInsertShape( final Circle shape,
                              final ActionInsertNode node,
                              final boolean isReadOnly ) {
        super( shape,
               node,
               isReadOnly );
        setNodeLabel( getNodeLabel() );

        plus.setFillColor( Color.rgbToBrowserHexColor( 150,
                                                       150,
                                                       0 ) );
        plus.setStrokeColor( Color.rgbToBrowserHexColor( 200,
                                                         200,
                                                         0 ) );
    }

    @Override
    public String getNodeLabel() {
        final StringBuilder sb = new StringBuilder();
        sb.append( ActionInsertNodeFactory.DESCRIPTION );
        final String className = getModelNode().getClassName();
        if ( className != null ) {
            sb.append( " " ).append( className );
        }
        return sb.toString();
    }

    @Override
    public boolean acceptChildNode( final WiresBaseTreeNode child ) {
        if ( !( child instanceof BaseGuidedDecisionTreeShape ) ) {
            return false;
        }

        //ActionInsertNodes can have other ActionInsertNodes as children
        if ( child instanceof ActionInsertShape ) {
            return true;
        }

        //ActionRetractNodes and ActionUpdateNodes can only be added to paths containing a bound type
        if ( child instanceof ActionRetractShape || child instanceof ActionUpdateShape ) {
            Node node = this.getModelNode();
            while ( node != null ) {
                if ( node instanceof TypeNode ) {
                    if ( ( (TypeNode) node ).isBound() ) {
                        return true;
                    }
                }
                node = node.getParent();
            }
            return false;
        }

        return false;
    }

}
