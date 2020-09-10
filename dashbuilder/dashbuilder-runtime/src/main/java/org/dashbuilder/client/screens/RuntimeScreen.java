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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.navbar.NavBarHelper;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.shared.model.RuntimeModel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.workbench.model.menu.Menus;

/**
 * The Main application screen that contains dashboards from a RuntimeModel. 
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = RuntimeScreen.ID)
public class RuntimeScreen {

    public static final String ID = "RuntimeScreen";

    public static final String INDEX_PAGE_NAME = "index";

    private static AppConstants i18n = AppConstants.INSTANCE;

    public interface View extends UberElemental<RuntimeScreen> {

        void addMenus(Menus menus);

    }

    @Inject
    View view;

    @Inject
    NavBarHelper menusHelper;

    @Inject
    PlaceManager placeManager;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return i18n.runtimeScreenTitle();
    }

    @WorkbenchPartView
    public View workbenchPart() {
        return this.view;
    }

    public void loadDashboards(RuntimeModel runtimeModel) {
        NavTree navTree = runtimeModel.getNavTree();
        Menus menus = menusHelper.buildMenusFromNavTree(navTree).build();
        view.addMenus(menus);
    }

    public void goToIndex(List<LayoutTemplate> templates) {
        if (templates.size() == 1) {
            placeManager.goTo(templates.get(0).getName());
        } else {
            templates.stream()
                     .map(LayoutTemplate::getName)
                     .filter(INDEX_PAGE_NAME::equals)
                     .findFirst()
                     .ifPresent(placeManager::goTo);
        }
    }

}