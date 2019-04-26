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

import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.explorer.global.GlobalDataSourceExplorer;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchScreen(identifier = DataSourceDefExplorerScreen.SCREEN_ID)
public class DataSourceDefExplorerScreen
        implements DataSourceDefExplorerScreenView.Presenter {

    public static final String SCREEN_ID = "DataSourceDefExplorerScreen";

    private DataSourceDefExplorerScreenView view;

    private GlobalDataSourceExplorer globalDataSourceExplorer;

    private TranslationService translationService;

    private Menus menu;

    public DataSourceDefExplorerScreen() {
    }

    @Inject
    public DataSourceDefExplorerScreen(DataSourceDefExplorerScreenView view,
                                       GlobalDataSourceExplorer globalDataSourceExplorer,
                                       TranslationService translationService) {

        this.view = view;
        this.globalDataSourceExplorer = globalDataSourceExplorer;
        this.translationService = translationService;
        view.init(this);
    }

    @PostConstruct
    public void init() {
        view.setGlobalExplorer(globalDataSourceExplorer);
    }

    @OnStartup
    public void onStartup() {
        this.menu = makeMenuBar();
        globalDataSourceExplorer.refresh();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation(DataSourceManagementConstants.DataSourceDefExplorerScreen_Title);
    }

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu(
                        translationService.getTranslation(
                                DataSourceManagementConstants.DataSourceDefExplorerScreen_Refresh))
                .respondsWith(getRefreshCommand())
                .endMenu()
                .build();
    }

    protected Command getRefreshCommand() {
        return new Command() {
            @Override
            public void execute() {
                globalDataSourceExplorer.refresh();
            }
        };
    }
}