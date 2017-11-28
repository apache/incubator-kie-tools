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
package org.dashbuilder.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.client.resources.i18n.AppConstants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * The gallery perspective.
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "DisplayerGalleryPerspective")
public class DisplayerGalleryPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {

        PanelDefinition west = new PanelDefinitionImpl( StaticWorkbenchPanelPresenter.class.getName() );
        west.setWidth(200);
        west.setMinWidth(150);
        west.addPart("GalleryTreeScreen");

        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiTabWorkbenchPanelPresenter.class.getName());
        perspective.setName(AppConstants.INSTANCE.menu_gallery());
        perspective.getRoot().insertChild(CompassPosition.WEST, west);
        perspective.getRoot().addPart("GalleryHomeScreen");
        return perspective;
    }
}