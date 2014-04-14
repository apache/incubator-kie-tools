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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import org.drools.workbench.client.resources.i18n.AppConstants;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.services.security.AppRoles;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.editors.repository.clone.CloneRepositoryForm;
import org.uberfire.client.editors.repository.create.CreateRepositoryForm;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.annotations.Roles;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;

import static org.uberfire.workbench.model.toolbar.IconType.*;

/**
 * A Perspective for Administrators
 */
@Roles({ "admin" })
@ApplicationScoped
@WorkbenchPerspective(identifier = "org.drools.workbench.client.perspectives.AdministrationPerspective")
public class AdministrationPerspective {

    private static String[] PERMISSIONS_ADMIN = new String[]{ AppRoles.ADMIN.getName() };

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private Command newRepoCommand = null;
    private Command cloneRepoCommand = null;

    private PerspectiveDefinition perspective;
    private Menus menus;
    private ToolBar toolBar;

    @PostConstruct
    public void init() {
        buildCommands();
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

    private void buildCommands() {
        this.cloneRepoCommand = new Command() {

            @Override
            public void execute() {
                final CloneRepositoryForm cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                cloneRepositoryWizard.addCloseHandler( new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose( CloseEvent<PopupPanel> event ) {
                        iocManager.destroyBean( cloneRepositoryWizard );
                    }

                } );
                cloneRepositoryWizard.show();
            }

        };

        this.newRepoCommand = new Command() {
            @Override
            public void execute() {
                final CreateRepositoryForm newRepositoryWizard = iocManager.lookupBean( CreateRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.addCloseHandler( new CloseHandler<CreateRepositoryForm>() {
                    @Override
                    public void onClose( CloseEvent<CreateRepositoryForm> event ) {
                        iocManager.destroyBean( newRepositoryWizard );
                    }
                } );
                newRepositoryWizard.show();
            }
        };

    }

    private void buildPerspective() {
        this.perspective = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        this.perspective.setName( AppConstants.INSTANCE.AdministrationPerspectiveName() );

        this.perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl( PanelType.SIMPLE );
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST,
                                                west );
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu( AppConstants.INSTANCE.MenuExplore() )
                .withItems( getExploreMenuItems() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.MenuOrganizationalUnits() )
                .withItems( getOrganizationalUnitsMenuItem() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.MenuRepositories() )
                .withItems( getRepositoriesMenuItems() )
                .endMenu()
                .build();
    }

    private List<? extends MenuItem> getRepositoriesMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuListRepositories() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "RepositoriesEditor" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuCloneRepository() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                cloneRepoCommand ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuNewRepository() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                newRepoCommand ).endMenu().build().getItems().get( 0 ) );
        return menuItems;
    }

    private List<? extends MenuItem> getOrganizationalUnitsMenuItem() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuManageOrganizationalUnits() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.workbench.common.screens.organizationalunit.manager.OrganizationalUnitManager" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        return menuItems;
    }

    private List<? extends MenuItem> getExploreMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuExploreFiles() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "FileExplorer" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );
        return menuItems;
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "file.explorer" );
        final DefaultToolBarItem i1 = new DefaultToolBarItem( FOLDER_CLOSE_ALT,
                                                              AppConstants.INSTANCE.MenuNewRepository(),
                                                              newRepoCommand );
        final DefaultToolBarItem i2 = new DefaultToolBarItem( DOWNLOAD_ALT,
                                                              AppConstants.INSTANCE.MenuCloneRepository(),
                                                              cloneRepoCommand );
        i1.setRoles( PERMISSIONS_ADMIN );
        i2.setRoles( PERMISSIONS_ADMIN );
        toolBar.addItem( i1 );
        toolBar.addItem( i2 );
    }

}
