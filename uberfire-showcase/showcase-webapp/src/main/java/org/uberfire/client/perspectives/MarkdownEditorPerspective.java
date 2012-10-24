/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * Markdown Live editor
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "MarkdownEditorPerspective")
public class MarkdownEditorPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName("Markdown Editor");

        p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("MarkdownLiveViewer")));

        final PanelDefinition westPanel = new PanelDefinitionImpl();
        westPanel.setWidth(600);
        westPanel.setMinWidth(300);
        westPanel.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("MarkdownLiveEditor")));
        p.getRoot().setChild(Position.WEST, westPanel);

        return p;
    }

}
