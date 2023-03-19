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

package org.dashbuilder.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.RuntimeEntryPoint;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class GoToDashboardMenuBuilder implements MenuFactory.CustomMenuBuilder {

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    MenuBuilderHelper menuBuilderHelper;

    @Override
    public void push(CustomMenuBuilder element) {
        // do nothing
    }

    @Override
    public MenuItem build() {
        return menuBuilderHelper.buildMenuItem(AppConstants.INSTANCE.dashboardOpenTooltip(),
                                               IconType.EXTERNAL_LINK,
                                               this::openDashboardInNewWindow);
    }

    private void openDashboardInNewWindow() {
        String currentPlace = perspectiveManager.getCurrentPerspective().getIdentifier();
        String standaloneUrl = Window.Location.createUrlBuilder()
                                              .setParameter(RuntimeEntryPoint.DASHBOARD_PARAM, currentPlace)
                                              .setParameter("standalone", "true")
                                              .buildString();
        DomGlobal.window.open(standaloneUrl);
    }

}