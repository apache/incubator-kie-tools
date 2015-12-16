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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;

public class TypeShape extends BaseGuidedDecisionTreeShape<TypeNode> {

    public TypeShape( final Circle shape,
                      final TypeNode node,
                      final boolean isReadOnly ) {
        super( shape,
               node,
               isReadOnly );
        setNodeLabel( getNodeLabel() );

        plus.setFillColor( Color.rgbToBrowserHexColor( 150,
                                                       0,
                                                       0 ) );
        plus.setStrokeColor( Color.rgbToBrowserHexColor( 200,
                                                         0,
                                                         0 ) );
    }

    @Override
    public String getNodeLabel() {
        final StringBuilder sb = new StringBuilder();
        if ( node.isBound() ) {
            sb.append( node.getBinding() ).append( " : " );
        }
        sb.append( node.getClassName() );
        return sb.toString();
    }

    @Override
    public boolean acceptChildNode( final WiresBaseTreeNode child ) {
        if ( !( child instanceof BaseGuidedDecisionTreeShape ) ) {
            return false;
        }

        //Constraints can only be added TypeNodes for the same class
        if ( child instanceof ConstraintShape ) {
            final ConstraintShape cs = (ConstraintShape) child;
            if ( !node.getClassName().equals( cs.getModelNode().getClassName() ) ) {
                return false;
            }
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

        return true;
    }

}
