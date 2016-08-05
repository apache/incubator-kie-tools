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

package org.kie.workbench.common.screens.datasource.management.client.explorer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.explorer.global.GlobalDataSourceExplorer;
import org.kie.workbench.common.screens.datasource.management.client.explorer.project.ProjectDataSourceExplorer;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchScreen( identifier = "DataSourceDefExplorer" )
public class DataSourceDefExplorerScreen
        implements DataSourceDefExplorerScreenView.Presenter {

    private DataSourceDefExplorerScreenView view;

    private ProjectDataSourceExplorer projectDataSourceExplorer;

    private GlobalDataSourceExplorer globalDataSourceExplorer;

    private TranslationService translationService;

    private PlaceRequest placeRequest;

    private Menus menu;

    private boolean projectExplorerSelected = true;

    public DataSourceDefExplorerScreen() {
    }

    @Inject
    public DataSourceDefExplorerScreen( DataSourceDefExplorerScreenView view,
            ProjectDataSourceExplorer projectDataSourceExplorer,
            GlobalDataSourceExplorer globalDataSourceExplorer,
            TranslationService translationService ) {

        this.view = view;
        this.projectDataSourceExplorer = projectDataSourceExplorer;
        this.globalDataSourceExplorer = globalDataSourceExplorer;
        this.translationService = translationService;
        view.init( this );
    }

    @PostConstruct
    public void init() {
        view.setProjectExplorer( projectDataSourceExplorer );
        view.setGlobalExplorer( globalDataSourceExplorer );
    }

    @OnStartup
    public void onStartup( PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        this.menu = makeMenuBar();

        projectDataSourceExplorer.setActiveOrganizationalUnit( null );
        projectDataSourceExplorer.setActiveRepository( null );
        projectDataSourceExplorer.setActiveProject( null );

        onProjectExplorerSelected();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation( DataSourceManagementConstants.DataSourceDefExplorerScreen_Title );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return menu;
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu(
                        translationService.getTranslation(
                                DataSourceManagementConstants.DataSourceDefExplorerScreen_Refresh ) )
                .respondsWith( getRefreshCommand() )
                .endMenu()
                .build();
    }

    private Command getRefreshCommand() {
        return new Command() {
            @Override
            public void execute() {
                if ( projectExplorerSelected ) {
                    onProjectExplorerSelected();
                } else {
                    onGlobalExplorerSelected();
                }
            }
        };
    }

    @Override
    public void onProjectExplorerSelected() {
        projectExplorerSelected = true;
        projectDataSourceExplorer.refresh();
    }

    @Override
    public void onGlobalExplorerSelected() {
        projectExplorerSelected = false;
        globalDataSourceExplorer.refresh();
    }
}