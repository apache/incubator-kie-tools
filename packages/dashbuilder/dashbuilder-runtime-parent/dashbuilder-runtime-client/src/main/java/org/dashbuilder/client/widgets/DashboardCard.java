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

package org.dashbuilder.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.RouterScreen;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;

/**
 * Allow users to upload new dashboards
 *
 */
@Dependent
public class DashboardCard implements IsElement {

    static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    View view;

    @Inject
    RouterScreen routerScreen;

    @Inject
    PlaceManager placeManager;
    

    public interface View extends UberElemental<DashboardCard> {

        void setDashboardId(String title);

    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return this.view.getElement();
    }

    public void setDashboardId(String dashboardId) {
        view.setDashboardId(dashboardId);
    }

    public void onCardSelected(String dashboardId) {
        routerScreen.loadDashboard(dashboardId);
    }

}