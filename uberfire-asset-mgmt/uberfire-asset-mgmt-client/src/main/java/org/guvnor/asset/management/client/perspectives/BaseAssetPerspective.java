/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.asset.management.client.perspectives;

import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

public abstract class BaseAssetPerspective {

    @Inject
    protected PlaceManager placeManager;

    public BaseAssetPerspective() {
        super();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName("Asset Management");

        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Repository Configuration")));

        return perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu("Screens")
                .menus()
                .menu("Repository Configuration")
                .respondsWith(
                        new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo("Repository Configuration");
                            }
                        })
                .endMenu()
                .endMenus()
                .endMenu()
                .build();
    }
}
