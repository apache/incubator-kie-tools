/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.perspective;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.jboss.errai.ioc.client.api.Shared;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchPerspective(identifier = "DynamicPerspective", isDynamic = true)
public class DynamicPerspective {

    @Inject
    @Shared
    private PlaceManager placeManager;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl(SimpleWorkbenchPanelPresenter.class.getName());
        p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("DynamicScreen")));
        p.setName("Dynamic Perspective");
        return p;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu("Dynamic Menu").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        Window.alert("Hello from a dynamic menu!");
                    }
                })
                .endMenu()
                .newTopLevelMenu("Open Dynamic Editor").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo("DynamicEditor");
                    }
                })
                .endMenu()
                .newTopLevelMenu("Open Dynamic Screen")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo("DynamicScreen");
                    }
                })
                .endMenu()
                .newTopLevelMenu("Create New")
                .menus()
                // Test that an editor from a dynamic plugin can be opened for a dynamic resource type
                .menu("File matching dynamically loaded resource type")
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(PathFactory.newPath("test.csa",
                                                              "default://project/"));
                    }
                })
                .endMenu()
                .endMenus()
                .endMenu()
                .build();
    }
}
