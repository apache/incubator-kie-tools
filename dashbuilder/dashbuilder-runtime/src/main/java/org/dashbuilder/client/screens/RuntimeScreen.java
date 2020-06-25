/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.client.ClientRuntimeModelLoader;
import org.dashbuilder.client.navbar.NavBarHelper;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.shared.event.RuntimeModelEvent;
import org.dashbuilder.shared.model.RuntimeModel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.model.menu.Menus;

/**
 * The Main application screen that contains dashboards from a RuntimeModel. 
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = RuntimeScreen.ID)
public class RuntimeScreen {

    public static final String ID = "RuntimeScreen";

    private static AppConstants i18n = AppConstants.INSTANCE;

    public interface View extends UberElemental<RuntimeScreen> {

        void addMenus(Menus menus);

        void errorLoadingDashboards(Throwable throwable);

        void loading();

        void stopLoading();

    }

    @Inject
    View view;

    @Inject
    NavigationManager navigationManager;

    @Inject
    NavBarHelper menusHelper;

    @Inject
    PlaceManager placeManager;

    @Inject
    Event<RuntimeModelEvent> runtimeModelEvent;

    @Inject
    ClientRuntimeModelLoader modelLoader;
    private RuntimeModel currentRuntimeModel;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return i18n.runtimeScreenTitle();
    }

    @WorkbenchPartView
    public View workbenchPart() {
        return this.view;
    }

    @OnOpen
    public void onOpen() {
        openRuntimeModel(null);
    }

    public void openRuntimeModel(String importID) {
        if (this.currentRuntimeModel != null) {
            return;
        }
        if (importID == null || importID.trim().isEmpty()) {
            modelLoader.loadModel(this::loadDashboards,
                                  this::showEmptyContent,
                                  this::errorLoadingModel);
        } else {
            modelLoader.loadModel(importID,
                                  this::loadDashboards,
                                  this::showEmptyContent,
                                  this::errorLoadingModel);
        }
    }

    private void loadDashboards(RuntimeModel runtimeModel) {
        this.currentRuntimeModel = runtimeModel;
        view.stopLoading();
        NavTree navTree = runtimeModel.getNavTree();
        Menus menus = menusHelper.buildMenusFromNavTree(navTree).build();
        view.addMenus(menus);
        navigationManager.setDefaultNavTree(navTree);
        runtimeModelEvent.fire(new RuntimeModelEvent(runtimeModel));
    }

    private void showEmptyContent() {
        view.stopLoading();
        placeManager.goTo(UploadDashboardsScreen.ID);
    }

    private void errorLoadingModel(Object e, Throwable t) {
        view.stopLoading();
        view.errorLoadingDashboards(t);
    }

}