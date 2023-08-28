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
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.place.Place;
import org.dashbuilder.client.widgets.DashboardCard;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElemental;

/**
 * Screen that shows a list of dashboards available in a MULTI dashboards installation. 
 *
 */
@ApplicationScoped
public class DashboardsListScreen implements Place {

    public static final String ID = "ListDashboardsScreen";

    @Inject
    Router router;

    public interface View extends UberElemental<DashboardsListScreen> {

        void addCard(DashboardCard card);

        void clear();

        void disableUpload();
    }

    @Inject
    View view;

    @Inject
    ManagedInstance<DashboardCard> dashboardCardInstance;

    @Inject
    RuntimeClientLoader clientLoader;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void loadList(List<String> dashboardsNames) {
        onClose();
        dashboardsNames.stream()
                .map(this::createDashboardCard)
                .forEach(view::addCard);
    }

    private DashboardCard createDashboardCard(String id) {
        DashboardCard card = dashboardCardInstance.get();
        card.setDashboardId(id);
        return card;
    }

    @Override
    public void onClose() {
        dashboardCardInstance.destroyAll();
        view.clear();
    }

    public void disableUpload() {
        view.disableUpload();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

}
