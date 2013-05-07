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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.editors.repository.clone.CloneRepositoryForm;
import org.uberfire.client.editors.repository.create.CreateRepositoryForm;
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
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.uberfire.client.workbench.widgets.toolbar.IconType.DOWNLOAD_ALT;
import static org.uberfire.client.workbench.widgets.toolbar.IconType.FOLDER_CLOSE_ALT;

/**
 * A Perspective for Administrators
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "org.kie.guvnor.client.perspectives.administration")
public class AdministrationPerspective {

    private static String[] PERMISSIONS_ADMIN = new String[]{ "ADMIN" };

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IOCBeanManager iocManager;

    private Command newRepoCommand   = null;
    private Command cloneRepoCommand = null;

    private PerspectiveDefinition perspective;
    private Menus                 menus;
    private ToolBar               toolBar;

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
        this.perspective = new PerspectiveDefinitionImpl();
        this.perspective.setName( "Administration" );

        this.perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST, west );
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu( "Explore" )
                    .menus()
                        .menu( "Files" )
                            .withRoles( PERMISSIONS_ADMIN )
                            .respondsWith( new Command() {
                                @Override
                                public void execute() {
                                    placeManager.goTo( "FileExplorer" );
                                }
                            }  )
                        .endMenu()
                    .endMenus()
                .endMenu()
                .newTopLevelMenu( "Repositories" )
                    .menus()
                        .menu( "Clone Repo" )
                            .withRoles( PERMISSIONS_ADMIN )
                            .respondsWith( cloneRepoCommand  )
                        .endMenu()
                        .menu( "New Repo" )
                            .withRoles( PERMISSIONS_ADMIN )
                            .respondsWith( newRepoCommand  )
                        .endMenu()
                    .endMenus()
                .endMenu().build();
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "file.explorer" );
        final DefaultToolBarItem i1 = new DefaultToolBarItem( FOLDER_CLOSE_ALT,
                                                              "New Repository",
                                                              newRepoCommand );
        final DefaultToolBarItem i2 = new DefaultToolBarItem( DOWNLOAD_ALT,
                                                              "Clone Repository",
                                                              cloneRepoCommand );
        i1.setRoles( PERMISSIONS_ADMIN );
        i2.setRoles( PERMISSIONS_ADMIN );
        toolBar.addItem( i1 );
        toolBar.addItem( i2 );
    }

}
