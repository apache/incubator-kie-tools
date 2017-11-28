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

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.screen.explorer.ContentExplorerScreen;
import org.dashbuilder.client.dashboard.DashboardManager;
import org.dashbuilder.client.dashboard.DashboardPerspectiveActivity;
import org.dashbuilder.client.navbar.TopMenuBar;
import org.dashbuilder.client.navigation.NavTreeDefinitions;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.security.PermissionTreeSetup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.dashbuilder.shared.dashboard.events.DashboardDeletedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

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
    private Event<NotificationEvent> workbenchNotification;

    @Inject
    private PermissionTreeSetup permissionTreeSetup;

    @Inject
    private ContentExplorerScreen contentExplorerScreen;

    @Inject
    private TopMenuBar navBar;

    @Inject
    private ContentManagerI18n contentManagerI18n;

    @PostConstruct
    public void startApp() {
        // OPTIONAL: Rename perspectives to dashboards in CMS
        //customizeCMSTexts();

        userSystemManager.waitForInitialization(() ->
            dashboardManager.loadDashboards(t ->
                navigationManager.init(() -> {
                    permissionTreeSetup.configureTree();
                    initNavBar();
                    hideLoadingPopup();
                })
            ));
    }

    private void customizeCMSTexts() {
        contentManagerI18n.setPerspectiveResourceName(constants.content_manager_dashboard());
        contentManagerI18n.setPerspectivesResourceName(constants.content_manager_dashboards());
        contentManagerI18n.setNoPerspectives(constants.content_manager_noDashboards());
    }

    private void initNavBar() {
        // Set the dashbuilder's default nav tree
        navigationManager.setDefaultNavTree(NavTreeDefinitions.NAV_TREE_DEFAULT);

        // Allow links to core perspectives only under the top menu's nav group
        contentExplorerScreen.getNavTreeEditor()
                .setOnlyRuntimePerspectives(NavTreeDefinitions.GROUP_APP, false)
                .applyToAllChildren();

        // Disable perspective context setup under the top menu nav's group
        contentExplorerScreen.getNavTreeEditor()
                .setPerspectiveContextEnabled(NavTreeDefinitions.GROUP_APP, false)
                .applyToAllChildren();

        // Attach old existing dashboards (created with versions prior to 0.7) under the "dashboards" group
        for (DashboardPerspectiveActivity activity : dashboardManager.getDashboards()) {
            String perspectiveId = activity.getIdentifier();
            navigationManager.getNavTree().addItem(perspectiveId,
                    activity.getDisplayName(),
                    activity.getDisplayName(),
                    NavTreeDefinitions.GROUP_DASHBOARDS, true,
                    NavWorkbenchCtx.perspective(perspectiveId).toString());
        }
        // Show the top menu bar
        navBar.setOnItemSelectedCommand(onItemSelectedCommand);
        navBar.setOnLogoutCommand(onLogoutCommand);
        navBar.show(NavTreeDefinitions.GROUP_APP);
    }

    // Event coming from old dashboards (created with versions prior to 0.7)
    private void onDashboardDeletedEvent(@Observes DashboardDeletedEvent event) {
        NavTree navTree = navigationManager.getNavTree();
        navTree.deleteItem(event.getDashboardId());
        navBar.show(NavTreeDefinitions.GROUP_APP);
        workbenchNotification.fire(new NotificationEvent(constants.notification_dashboard_deleted(event.getDashboardName()), INFO));
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    private Command onItemSelectedCommand = () -> {
        NavItem navItem = navBar.getItemSelected();

        String resourceId = NavWorkbenchCtx.get(navItem).getResourceId();
        if (resourceId != null) {
            placeManager.goTo(resourceId);
        }
    };

    private Command onLogoutCommand = () -> {
        authService.call(r -> {
            final String location = GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "/logout.jsp" );
            redirect( location );
        }).logout();
    };

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;
}