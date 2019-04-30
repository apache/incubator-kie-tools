/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.perspective;

import org.uberfire.jsbridge.client.perspective.jsnative.JsNativeContextDisplay;
import org.uberfire.jsbridge.client.perspective.jsnative.JsNativePanel;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

public class JsWorkbenchPanelConverter {

    private final JsNativePanel nativePanel;

    public JsWorkbenchPanelConverter(final JsNativePanel nativePanel) {
        this.nativePanel = nativePanel;
    }

    public PanelDefinition toPanelDefinition() {

        final PanelDefinition newPanel = new PanelDefinitionImpl(nativePanel.panelType());
        newPanel.setPosition(nativePanel.position());

        final JsNativeContextDisplay contextDisplay = nativePanel.contextDisplay();

        newPanel.setContextDisplayMode(contextDisplay.mode());
        if (contextDisplay.contextId() != null) {
            newPanel.setContextDefinition(new ContextDefinitionImpl(new DefaultPlaceRequest(contextDisplay.contextId())));
        }

        if (nativePanel.width() > 0) {
            newPanel.setWidth(nativePanel.width());
        }

        if (nativePanel.minWidth() > 0) {
            newPanel.setMinWidth(nativePanel.minWidth());
        }

        if (nativePanel.height() > 0) {
            newPanel.setHeight(nativePanel.height());
        }

        if (nativePanel.minHeight() > 0) {
            newPanel.setHeight(nativePanel.minHeight());
        }

        nativePanel.view().parts().stream()
                .map(part -> new JsWorkbenchPartConverter(part).toPartDefinition())
                .forEach(newPanel::addPart);

        nativePanel.view().panels().stream()
                .map(panel -> new JsWorkbenchPanelConverter(panel).toPanelDefinition())
                .forEach(panel -> newPanel.insertChild(panel.getPosition(), panel));

        return newPanel;
    }
}
