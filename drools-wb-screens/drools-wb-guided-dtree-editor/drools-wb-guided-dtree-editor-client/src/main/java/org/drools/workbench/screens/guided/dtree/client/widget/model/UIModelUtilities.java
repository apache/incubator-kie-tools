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
package org.drools.workbench.screens.guided.dtree.client.widget.model;

import java.util.List;

import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.UIElement;
import org.drools.workbench.screens.guided.dtree.client.widget.elements.UIModelElement;
import org.uberfire.commons.validation.PortablePreconditions;

public class UIModelUtilities {

    private final List<UIElement> uiModel;

    public UIModelUtilities( final List<UIElement> uiModel ) {
        this.uiModel = PortablePreconditions.checkNotNull( "uiModel",
                                                           uiModel );
    }

    public UIModelElement getUIElement( final Node node ) {
        for ( UIElement element : uiModel ) {
            if ( element instanceof UIModelElement ) {
                final UIModelElement uiModelElement = (UIModelElement) element;
                if ( uiModelElement.getNode().equals( node ) ) {
                    return uiModelElement;
                }
            }
        }
        return null;
    }

}
