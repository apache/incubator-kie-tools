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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.inbox.client.InboxPresenter;
import org.kie.workbench.common.screens.projecteditor.client.menu.ProjectMenu;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Perspective for Rule authors. Note the @WorkbenchPerspective has the same identifier as kie-drools-wb
 * since org.kie.workbench.common.screens.projecteditor.client.messages.ProblemsService "white-lists" a
 * set of Perspectives for which to show the Problems Panel
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "AuthoringPerspective", isTransient = false)
public class AuthoringPerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private ProjectMenu projectMenu;

    @Inject
    private RepositoryMenu repositoryMenu;

    @Inject
    private PlaceManager placeManager;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinitionImpl perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Author" );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 400 );
        west.setMinWidth( 350 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           west );

        return perspective;
    }

    @WorkbenchMenu
    public Menus buildMenuBar() {
        return MenuFactory
                .newTopLevelMenu( AppConstants.INSTANCE.Explore() )
                .withItems( getExploreMenuItems() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.New() )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Project() )
                .withItems( projectMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Repository() )
                .withItems( repositoryMenu.getMenuItems() )
                .endMenu().build();
    }

    private List<? extends MenuItem> getExploreMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.Projects() ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.guvnor.explorer" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.IncomingChanges() ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "Inbox" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.RecentlyEdited() ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                        p.addParameter( "inboxname", InboxPresenter.RECENT_EDITED_ID );
                        placeManager.goTo( p );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.RecentlyOpened() ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                        p.addParameter( "inboxname", InboxPresenter.RECENT_VIEWED_ID );
                        placeManager.goTo( p );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        return menuItems;
    }

}
