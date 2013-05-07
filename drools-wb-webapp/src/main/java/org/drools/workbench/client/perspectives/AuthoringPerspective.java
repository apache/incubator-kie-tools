/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.client.resources.i18n.Constants;
import org.guvnor.inbox.client.InboxPresenter;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcesMenu;
import org.kie.guvnor.commons.ui.client.menu.ToolsMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.client.workbench.widgets.toolbar.IconType;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * A Perspective for Rule authors
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "org.kie.guvnor.client.perspectives.authoring", isDefault = true)
public class AuthoringPerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private ToolsMenu toolsMenu;

    @Inject
    private PlaceManager placeManager;

    private PerspectiveDefinition perspective;
    private Menus                 menus;
    private ToolBar               toolBar;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
        buildToolBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void buildPerspective() {
        this.perspective = new PerspectiveDefinitionImpl();
        this.perspective.setName( "Author" );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST, west );
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu( "Explore" )
                    .menus()
                        .menu( "Projects" )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    placeManager.goTo( "org.kie.guvnor.explorer" );
                                }
                            }  )
                        .endMenu()
                        .menu( "Incoming Changes" )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    //PlaceRequest p = new PathPlaceRequest("Inbox");
                                    //p.addParameter("inboxname", InboxPresenter.INCOMING_ID);
                                    placeManager.goTo( "Inbox" );
                                }
                            }  )
                        .endMenu()
                        .menu( "Recently Edited" )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                                    p.addParameter( "inboxname", InboxPresenter.RECENT_EDITED_ID );
                                    placeManager.goTo( p );
                                }
                            }  )
                        .endMenu()
                        .menu( "Recently Opened" )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                                    p.addParameter( "inboxname", InboxPresenter.RECENT_VIEWED_ID );
                                    placeManager.goTo( p );
                                }
                            }  )
                        .endMenu()
                    .endMenus()
                .endMenu()
                .newTopLevelMenu( "New" )
                    .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( "Tools" )
                    .withItems( toolsMenu.getMenuItems() )
                .endMenu().build();
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "guvnor.new.item" );
        final String tooltip = Constants.INSTANCE.newItem();
        final Command command = new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show();
            }
        };
        toolBar.addItem( new DefaultToolBarItem( IconType.FILE,
                                                 tooltip,
                                                 command ) );

    }

}
