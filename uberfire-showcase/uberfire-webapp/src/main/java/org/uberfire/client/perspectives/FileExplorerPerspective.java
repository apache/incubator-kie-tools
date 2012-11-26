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
package org.uberfire.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.editors.repository.clone.CloneRepositoryWizard;
import org.uberfire.client.editors.repository.create.CreateNewRepositoryWizard;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "FileExplorerPerspective")
public class FileExplorerPerspective {

    @Inject
    private IOCBeanManager iocManager;

    private Command newRepoCommand = null;

    private Command cloneRepoCommand = null;

    private static String[] PERMISSIONS_ADMIN = new String[]{ "ADMIN" };

    @PostConstruct
    public void init() {
        this.cloneRepoCommand = new Command() {

            @Override
            public void execute() {
                final CloneRepositoryWizard cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryWizard.class ).getInstance();
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
                final CreateNewRepositoryWizard newRepositoryWizard = iocManager.lookupBean( CreateNewRepositoryWizard.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.addCloseHandler( new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose( CloseEvent<PopupPanel> event ) {
                        iocManager.destroyBean( newRepositoryWizard );
                    }
                } );
                newRepositoryWizard.show();
            }
        };
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "File Explorer" );

        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 200 );
        west.setMinWidth( 150 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        p.getRoot().insertChild( Position.WEST, west );

        return p;
    }

    @WorkbenchToolBar
    public ToolBar buildToolBar() {
        final ToolBar toolBar = new DefaultToolBar();
        final ToolBarItem clone = new DefaultToolBarItem( "images/clone_repo.png",
                                                          "Clone Repo", cloneRepoCommand );
        toolBar.addItem( clone );

        final ToolBarItem newRepo = new DefaultToolBarItem( "images/new_repo.png",
                                                            "New Repository", newRepoCommand );
        newRepo.setRoles( PERMISSIONS_ADMIN );
        toolBar.addItem( newRepo );
        return toolBar;
    }

    @WorkbenchMenu
    public MenuBar buildMenuBar() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();
        menuBar.addItem( new DefaultMenuItemSubMenu( "Repositories", subMenuBar ) );

        final MenuItem cloneRepo = new DefaultMenuItemCommand( "Clone Repo", cloneRepoCommand );
        final MenuItem newRepo = new DefaultMenuItemCommand( "New Repo", newRepoCommand );
        newRepo.setRoles( PERMISSIONS_ADMIN );

        subMenuBar.addItem( cloneRepo );
        subMenuBar.addItem( newRepo );

        return menuBar;
    }
}
