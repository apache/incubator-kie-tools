/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.explorer.client;

import java.util.function.Consumer;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenter {

    private final static String INIT_PATH = "init_path";
    private final static String PATH = "path";
    public ExplorerMenu menu;
    private ExplorerView view;
    private BusinessViewPresenter businessViewPresenter;
    private TechnicalViewPresenter technicalViewPresenter;
    private WorkspaceProjectContext context;
    private ActiveContextOptions activeOptions;

    public ExplorerPresenter() {
    }

    @Inject
    public ExplorerPresenter(final ExplorerView view,
                             final BusinessViewPresenter businessViewPresenter,
                             final TechnicalViewPresenter technicalViewPresenter,
                             final WorkspaceProjectContext context,
                             final ActiveContextOptions activeOptions,
                             final ExplorerMenu menu) {
        this.view = view;
        this.businessViewPresenter = businessViewPresenter;
        this.technicalViewPresenter = technicalViewPresenter;
        this.context = context;
        this.activeOptions = activeOptions;
        this.menu = menu;

        menu.addRefreshCommand(new Command() {
            @Override
            public void execute() {
                refresh();
            }
        });
        menu.addUpdateCommand(new Command() {
            @Override
            public void execute() {
                update();
            }
        });
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        activeOptions.init(placeRequest,
                           new Command() {
                               @Override
                               public void execute() {
                                   String path = placeRequest.getParameter(PATH,
                                                                           null);
                                   path = placeRequest.getParameter(INIT_PATH,
                                                                    path);
                                   init(path);
                               }
                           });
    }

    private void init(final String initPath) {

        menu.refresh();

        getActiveView().setVisible(true);
        getInactiveView().setVisible(false);

        if (initPath == null) {
            technicalViewPresenter.initialiseViewForActiveContext(context);
            businessViewPresenter.initialiseViewForActiveContext(context);
        } else {
            technicalViewPresenter.initialiseViewForActiveContext(initPath);
            businessViewPresenter.initialiseViewForActiveContext(initPath);
        }

        update();
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenter> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu.asMenu());
    }

    private void refresh() {
        getActiveView().refresh();
    }

    private void update() {
        getActiveView().update();
    }

    private BaseViewPresenter getActiveView() {
        if (activeOptions.isTechnicalViewActive()) {
            return technicalViewPresenter;
        } else {
            return businessViewPresenter;
        }
    }

    private BaseViewPresenter getInactiveView() {
        if (activeOptions.isTechnicalViewActive()) {
            return businessViewPresenter;
        } else {
            return technicalViewPresenter;
        }
    }
}
