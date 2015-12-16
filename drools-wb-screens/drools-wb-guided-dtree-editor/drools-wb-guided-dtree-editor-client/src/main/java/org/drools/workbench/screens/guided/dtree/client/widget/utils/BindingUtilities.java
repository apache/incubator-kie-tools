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
package org.drools.workbench.screens.guided.dtree.client.widget.utils;

import org.drools.workbench.models.guided.dtree.shared.model.nodes.BoundNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;

/**
 * Utilities to validate bindings
 */
public class BindingUtilities {

    /**
     * Check whether the binding is unique on the path ascending the tree from the given node to root.
     * Type and/or Field bindings only need to be unique along each path from leaf to root.
     * @param binding Binding to check
     * @param node Node that is to be bound. The check is ran from this node's parent upwards.
     * @return true if the binding is unique
     */
    public static boolean isUniqueInPath( final String binding,
                                          final Node node ) {
        Node parent = node.getParent();
        while ( parent != null ) {
            if ( parent instanceof BoundNode ) {
                final BoundNode bn = (BoundNode) parent;
                if ( bn.isBound() ) {
                    if ( bn.getBinding().equals( binding ) ) {
                        return false;
                    }
                }
            }
            parent = parent.getParent();
        }
        return true;
    }

}
