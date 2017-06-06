/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.workbench.model.impl;

import java.util.Optional;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.workbench.model.CustomPanelDefinition;

public class CustomPanelDefinitionImpl extends PanelDefinitionImpl implements CustomPanelDefinition {

    private HTMLElement htmlElementContainer;
    private HasWidgets hasWidgetsContainer;

    public CustomPanelDefinitionImpl(String panelType,
                                     HasWidgets hasWidgetsContainer) {

        super(panelType);
        this.hasWidgetsContainer = hasWidgetsContainer;
    }

    public CustomPanelDefinitionImpl(String panelType,
                                     HTMLElement htmlElementContainer) {
        super(panelType);
        this.htmlElementContainer = htmlElementContainer;
    }

    @Override
    public Optional<HTMLElement> getHtmlElementContainer() {
        return Optional.ofNullable(htmlElementContainer);
    }

    @Override
    public Optional<HasWidgets> getHasWidgetsContainer() {
        return Optional.ofNullable(hasWidgetsContainer);
    }
}
