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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.perspective.DashboardsListPerspective;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.widgets.DashboardCard;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.event.UpdatedRuntimeModelEvent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnClose;

/**
 * Screen that shows a list of dashboards available in a MULTI dashboards installation. 
 *
 */
@ApplicationScoped
@WorkbenchScreen(identifier = DashboardsListScreen.ID)
public class DashboardsListScreen {

    public static final String ID = "ListDashboardsScreen";

    private static AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    RouterScreen router;

    @Inject
    PerspectiveManager perspectiveManager;

    public interface View extends UberElemental<DashboardsListScreen> {

        void addCard(DashboardCard card);

        void clear();
        
        void disableUpload();
    }

    @Inject
    View view;

    @Inject
    ManagedInstance<DashboardCard> dashboardCardInstance;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void loadList(List<String> dashboardsNames) {
        clear();
        dashboardsNames.stream()
                       .map(this::createDashboardCard)
                       .forEach(view::addCard);
    }

    private DashboardCard createDashboardCard(String id) {
        DashboardCard card = dashboardCardInstance.get();
        card.setDashboardId(id);
        return card;
    }

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return i18n.dashboardsListScreenTitle();
    }

    @WorkbenchPartView
    public View workbenchPart() {
        return this.view;
    }

    @OnClose
    public void clear() {
        dashboardCardInstance.destroyAll();
        view.clear();
    }

    public void onModelUpdated(@Observes UpdatedRuntimeModelEvent event) {
        reload();
    }

    public void onModelRemoved(@Observes RemovedRuntimeModelEvent event) {
        reload();
    }

    private void reload() {
        String currentPlace = perspectiveManager.getCurrentPerspective().getIdentifier();
        if (DashboardsListPerspective.ID.equals(currentPlace)) {
            router.listDashboards();
        }
    }

    public void disableUpload() {
        view.disableUpload();
    }

}