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

import static org.uberfire.workbench.model.PanelType.*;
import static org.uberfire.workbench.model.toolbar.IconType.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.editors.repository.clone.CloneRepositoryForm;
import org.uberfire.client.editors.repository.create.CreateRepositoryForm;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A Perspective to show File Explorer
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "FileExplorerPerspective", isTransient = false)
public class FileExplorerPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private Command newRepoCommand = null;

    private Command cloneRepoCommand = null;

    private static String[] PERMISSIONS_ADMIN = new String[]{ "ADMIN" };

    @PostConstruct
    public void init() {
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

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( ROOT_LIST );
        p.setName( "File Explorer" );
        p.setContextDefinition( new ContextDefinitionImpl( new DefaultPlaceRequest( "fileNavContext" ) ) );

        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileNavigator" ) ) );

        return p;
    }

    @WorkbenchToolBar
    public ToolBar buildToolBar() {
        return new DefaultToolBar( "file.explorer" ) {{
            addItem( new DefaultToolBarItem( FOLDER_CLOSE_ALT, "New Repository", newRepoCommand ) {{
                setRoles( PERMISSIONS_ADMIN );
            }} );
            addItem( new DefaultToolBarItem( DOWNLOAD_ALT, "Clone Repository", cloneRepoCommand ) );
        }};
    }

    @WorkbenchMenu
    public Menus buildMenuBar() {
        return MenuFactory
                .newTopLevelMenu( "Navigator" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "FileNavigator" ) );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( "Repositories" )
                .menus()
                .menu( "Clone Repo" )
                .respondsWith( cloneRepoCommand )
                .endMenu()
                .menu( "New Repo" )
                .withRoles( PERMISSIONS_ADMIN )
                .respondsWith( newRepoCommand )
                .endMenu()
                .endMenus().
                endMenu()
                .build();
    }
}
