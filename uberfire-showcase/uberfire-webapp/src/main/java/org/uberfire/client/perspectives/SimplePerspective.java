/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.Window;
import org.uberfire.client.ShowcaseEntryPoint;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A simple perspective with one tabbed panel.
 */
@ApplicationScoped
@WorkbenchPerspective( identifier = "SimplePerspective", isTransient = false )
public class SimplePerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        p.setName( "Simple Perspective" );
        return p;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( "Open" )
                        .withItems( ShowcaseEntryPoint.getScreens() )
                .endMenu()
                .newTopLevelMenu( "Command" ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "Command!" );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Create New" )
                    .menus()
                        .menu( "Command 1" )
                            .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                Window.alert( "Command 1!" );
                            }
                        } )
                        .endMenu()
                        .menu( "Command 2" )
                            .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        Window.alert( "Command 2!" );
                    }
                } )
                        .endMenu()
                    .endMenus()
                .endMenu()
                .newTopLevelMenu( "Find" )
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            Window.alert( "Find!" );
                        }
                    } )
                    .position( MenuPosition.RIGHT )
                .endMenu()
                .build();
    }
}
