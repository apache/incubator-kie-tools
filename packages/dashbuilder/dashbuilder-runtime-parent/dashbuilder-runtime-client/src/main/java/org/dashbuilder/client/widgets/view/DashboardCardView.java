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

package org.dashbuilder.client.widgets.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.dashbuilder.client.widgets.DashboardCard;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class DashboardCardView implements DashboardCard.View {

    @Inject
    @DataField
    HTMLDivElement dashboardCardRoot;

    @Inject
    @DataField
    @Named("h2")
    HTMLHeadingElement cardTitle;

    private String dashboardId;

    private DashboardCard presenter;

    @Override
    public HTMLElement getElement() {
        return dashboardCardRoot;
    }

    @Override
    public void init(DashboardCard presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
        cardTitle.textContent = dashboardId;
    }
    
    @EventHandler("dashboardCardRoot")
    public void onCardSelected(ClickEvent e) {
        presenter.onCardSelected(dashboardId);
    }

}