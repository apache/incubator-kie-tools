/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizard;
import org.guvnor.common.services.shared.security.AppRoles;
import org.guvnor.structure.client.editors.repository.clone.CloneRepositoryPresenter;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.annotations.Roles;
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

    @Inject
    private CloneRepositoryPresenter cloneRepositoryPresenter;

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( AppConstants.INSTANCE.AdministrationPerspectiveName() );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           west );

        return perspective;
    }

    @WorkbenchMenu
    public Menus buildMenuBar() {
        return MenuFactory
                .newTopLevelMenu( AppConstants.INSTANCE.MenuExplore() )
                .withItems( getExploreMenuItems() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.MenuOrganizationalUnits() )
                .withItems( getOrganizationalUnitsMenuItem() )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.MenuRepositories() )
                .withItems( getRepositoriesMenuItems() )
                .endMenu()
                .newTopLevelMenu( "Editor Properties" )
                .withItems( getEditorsMenuItem() )
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
                new Command() {

                    @Override
                    public void execute() {
                        cloneRepositoryPresenter.showForm();
                    }

                } ).endMenu().build().getItems().get( 0 ) );
        menuItems.add( MenuFactory.newSimpleItem( AppConstants.INSTANCE.MenuNewRepository() ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        final CreateRepositoryWizard newRepositoryWizard = iocManager.lookupBean( CreateRepositoryWizard.class ).getInstance();
                        //When pop-up is closed destroy bean to avoid memory leak
                        newRepositoryWizard.onCloseCallback( new Callback<Void>() {
                            @Override
                            public void callback( Void result ) {
                                iocManager.destroyBean( newRepositoryWizard );
                            }
                        } );
                        newRepositoryWizard.start();
                    }
                } ).endMenu().build().getItems().get( 0 ) );

        return menuItems;
    }

    private List<? extends MenuItem> getEditorsMenuItem() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add( MenuFactory.newSimpleItem( "Test Scenario Editor" ).withRoles( PERMISSIONS_ADMIN ).respondsWith(
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "WiresPropertiesScreen" );
                    }
                } ).endMenu().build().getItems().get( 0 ) );

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

}
