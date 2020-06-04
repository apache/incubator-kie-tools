/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.cms.screen.explorer.NavigationExplorerScreen;
import org.dashbuilder.client.dashboard.DashboardManager;
import org.dashbuilder.client.navbar.AppHeader;
import org.dashbuilder.client.navigation.NavTreeDefinitions;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.security.PermissionTreeSetup;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeImpl;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mvp.Command;

/**
 * Entry-point for the Dashbuilder showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    private AppConstants constants = AppConstants.INSTANCE;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ClientUserSystemManager userSystemManager;

    @Inject
    private DashboardManager dashboardManager;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private NavigationManager navigationManager;
    
    @Inject
    NavigationExplorerScreen navigationExplorerScreen;

    @Inject
    private PermissionTreeSetup permissionTreeSetup;

    @Inject
    private AppHeader appHeader;

    @PostConstruct
    public void startApp() {
        // OPTIONAL: Rename perspectives to dashboards in CMS
        
        userSystemManager.waitForInitialization(() -> dashboardManager.loadDashboards(t -> navigationManager.init(() -> {
            permissionTreeSetup.configureTree();
            initNavBar();
            initNavigation();
            hideLoadingPopup();
        })));
    }

    private void initNavBar() {
        // Show the top menu bar
        appHeader.setOnLogoutCommand(onLogoutCommand);
        appHeader.setupMenu(NavTreeDefinitions.NAV_TREE_DEFAULT);
    }

    private void initNavigation() {
        // Set the dashbuilder's default nav tree
        navigationManager.setDefaultNavTree(NavTreeDefinitions.INITIAL_EMPTY);

        // Allow links to core perspectives only under the top menu's nav group
        navigationExplorerScreen.getNavTreeEditor()
                                .setOnlyRuntimePerspectives(NavTreeDefinitions.DASHBOARDS_GROUP, true)
                                .applyToAllChildren();

        // Disable perspective context setup under the top menu nav's group
        navigationExplorerScreen.getNavTreeEditor()
                                .setPerspectiveContextEnabled(NavTreeDefinitions.DASHBOARDS_GROUP, false)
                                .applyToAllChildren();
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    private Command onLogoutCommand = () -> {
        authService.call(r -> {
            final String location = GWT.getModuleBaseURL().replaceFirst("/" + GWT.getModuleName() + "/", "/logout.jsp");
            DomGlobal.window.location.assign(location);
        }).logout();
    };

}