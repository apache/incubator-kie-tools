/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.cms.perspective;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.screen.explorer.ContentExplorerScreen;
import org.dashbuilder.client.cms.screen.home.ContentManagerHomeScreen;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "ContentManagerPerspective")
public class ContentManagerPerspective {

    @Inject
    ContentManagerI18n i18n;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return buildPerspective();
    }

    private PerspectiveDefinition buildPerspective() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(ContentManagerConstants.INSTANCE.contentManagerHome());
        perspective.getRoot().addPart(ContentManagerHomeScreen.SCREEN_ID);
        final PanelDefinition west = new PanelDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());
        west.setWidth(330);
        west.setMinWidth(330);
        west.addPart(ContentExplorerScreen.SCREEN_ID);
        perspective.getRoot().insertChild(CompassPosition.WEST, west);
        return perspective;
    }
}
