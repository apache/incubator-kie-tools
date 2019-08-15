/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.menu;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMIN;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMINISTRATION;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.APPS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASET_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASOURCE_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PLUGIN_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DEFINITIONS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.uberfire.workbench.model.menu.MenuFactory.newSimpleItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.widgets.client.menu.AboutCommand;
import org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.workbench.client.menu.custom.AdminCustomMenuBuilder;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherMenuBuilder;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.Builder;
import org.uberfire.workbench.model.menu.MenuFactory.MenuBuilder;
import org.uberfire.workbench.model.menu.MenuFactory.TopLevelMenusBuilder;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;

@ApplicationScoped
public class DefaultWorkbenchFeaturesMenusHelper {

    DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    protected SyncBeanManager iocManager;
    private ActivityManager activityManager;
    private PerspectiveManager perspectiveManager;
    protected Caller<AuthenticationService> authService;
    protected User identity;
    protected UserMenu userMenu;
    protected UtilityMenuBar utilityMenuBar;
    protected WorkbenchMegaMenuPresenter menuBar;
    protected AboutCommand aboutCommand;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;
    private PlaceManager placeManager;

    public DefaultWorkbenchFeaturesMenusHelper() {
    }

    @Inject
    public DefaultWorkbenchFeaturesMenusHelper(SyncBeanManager iocManager,
                                               ActivityManager activityManager,
                                               PerspectiveManager perspectiveManager,
                                               Caller<AuthenticationService> authService,
                                               User identity,
                                               UserMenu userMenu,
                                               UtilityMenuBar utilityMenuBar,
                                               WorkbenchMegaMenuPresenter menuBar,
                                               AboutCommand aboutCommand,
                                               AuthorizationManager authorizationManager,
                                               SessionInfo sessionInfo,
                                               PlaceManager placeManager) {
        this.iocManager = iocManager;
        this.activityManager = activityManager;
        this.perspectiveManager = perspectiveManager;
        this.authService = authService;
        this.identity = identity;
        this.userMenu = userMenu;
        this.utilityMenuBar = utilityMenuBar;
        this.menuBar = menuBar;
        this.aboutCommand = aboutCommand;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.placeManager = placeManager;
    }

    public List<? extends MenuItem> getHomeViews() {
        final List<MenuItem> result = new ArrayList<>(1);

        result.add(MenuFactory.newSimpleItem(constants.HomePage())
                           .perspective(getDefaultPerspectiveIdentifier())
                           .endMenu()
                           .build().getItems().get(0));

        result.add(MenuFactory.newSimpleItem(constants.Admin())
                           .perspective(ADMIN)
                           .endMenu()
                           .build().getItems().get(0));

        return result;
    }

    public List<MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<>(4);

        result.add(MenuFactory.newSimpleItem(constants.ProjectAuthoring()).perspective(LIBRARY).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.ArtifactRepository()).perspective(GUVNOR_M2REPO).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.Administration()).perspective(ADMINISTRATION).endMenu().build().getItems().get(0));

        return result;
    }

    public List<? extends MenuItem> getProcessManagementViews() {
        final List<MenuItem> result = new ArrayList<>(2);
        result.add(MenuFactory.newSimpleItem(constants.ProcessDefinitions()).perspective(PROCESS_DEFINITIONS).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.ProcessInstances()).perspective(PROCESS_INSTANCES).endMenu().build().getItems().get(0));
        return result;
    }

    public List<? extends MenuItem> getExtensionsViews() {
        final List<MenuItem> result = new ArrayList<>(3);

        result.add(MenuFactory.newSimpleItem(constants.Plugins()).perspective(PLUGIN_AUTHORING).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.Apps()).perspective(APPS).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.DataSets()).perspective(DATASET_AUTHORING).endMenu().build().getItems().get(0));
        result.add(MenuFactory.newSimpleItem(constants.DataSources()).perspective(DATASOURCE_MANAGEMENT).endMenu().build().getItems().get(0));

        return result;
    }

    public void addRolesMenuItems() {
        for (Menus roleMenus : getRoles()) {
            userMenu.addMenus(roleMenus);
        }
    }

    public void addGroupsMenuItems() {
        for (Menus groups : getGroups()) {
            userMenu.addMenus(groups);
        }
    }

    public void addWorkbenchViewModeSwitcherMenuItem() {
        userMenu.addMenus(MenuFactory.newTopLevelCustomMenu(iocManager.lookupBean(WorkbenchViewModeSwitcherMenuBuilder.class).getInstance()).endMenu().build());
    }

    public void addWorkbenchConfigurationMenuItem() {
        utilityMenuBar.addMenus(MenuFactory.newTopLevelCustomMenu(iocManager.lookupBean(WorkbenchConfigurationMenuBuilder.class).getInstance()).endMenu().build());
    }

    public void addUtilitiesMenuItems() {
        addUserMenuItems();

        TopLevelMenusBuilder<MenuBuilder> menuBuilder;

        menuBuilder = MenuFactory.newTopLevelCustomMenu(iocManager.lookupBean(AppLauncherMenuBuilder.class).getInstance()).endMenu();
        menuBuilder.newTopLevelCustomMenu(iocManager.lookupBean(CustomSplashHelp.class).getInstance()).endMenu();

        if (hasAccessToPerspective(ADMIN)) {
            menuBuilder.newTopLevelCustomMenu(iocManager.lookupBean(AdminCustomMenuBuilder.class).getInstance()).endMenu();
        }

        menuBuilder.newTopLevelCustomMenu(iocManager.lookupBean(ResetPerspectivesMenuBuilder.class).getInstance()).endMenu();
        menuBuilder.newTopLevelCustomMenu(userMenu).endMenu();

        final Menus utilityMenus = menuBuilder.build();

        menuBar.addMenus(utilityMenus);
    }

    public void addUserMenuItems() {
        userMenu.clear();

        final Menus userMenus = MenuFactory
                .newTopLevelMenu(constants.LogOut())
                .respondsWith(new LogoutCommand())
                .endMenu()
                .newTopLevelMenu("About")
                .respondsWith(aboutCommand)
                .endMenu()
                .build();

        userMenu.addMenus(userMenus);
    }

    public String getDefaultPerspectiveIdentifier() {
        return perspectiveManager.getDefaultPerspectiveIdentifier();
    }

    public List<PerspectiveActivity> getPerspectiveActivities() {
        final Set<PerspectiveActivity> activities = activityManager.getActivities(PerspectiveActivity.class);

        List<PerspectiveActivity> sortedActivitiesForDisplay = new ArrayList<>(activities);
        Collections.sort(sortedActivitiesForDisplay,
                         (o1, o2) -> o1.getDefaultPerspectiveLayout().getName().compareTo(o2.getDefaultPerspectiveLayout().getName()));

        return sortedActivitiesForDisplay;
    }

    public List<MenuItem> getPerspectivesMenuItems() {
        final List<MenuItem> perspectives = new ArrayList<>();
        for (final PerspectiveActivity perspective : getPerspectiveActivities()) {
            final String name = perspective.getDefaultPerspectiveLayout().getName();
            final MenuItem item = newSimpleItem(name).perspective(perspective.getIdentifier()).endMenu().build().getItems().get(0);
            perspectives.add(item);
        }

        return perspectives;
    }

    public List<Menus> getRoles() {
        final Set<Role> roles = identity.getRoles();
        final List<Menus> result = new ArrayList<>(roles.size());

        result.add(MenuFactory.newSimpleItem(constants.LogOut()).respondsWith(new LogoutCommand()).endMenu().build());
        for (final Role role : roles) {
            if (!role.getName().equals("IS_REMEMBER_ME")) {
                result.add(MenuFactory.newSimpleItem(constants.Role() + ": " + role.getName()).endMenu().build());
            }
        }

        return result;
    }

    public List<Menus> getGroups() {
        final Set<Group> groups = identity.getGroups();
        final List<Menus> result = new ArrayList<Menus>(groups.size());

        for (final Group group : groups) {
            result.add(MenuFactory.newSimpleItem(constants.Group() + ": " + group.getName()).endMenu().build());
        }

        return result;
    }

    public TopLevelMenusBuilder<MenuBuilder> buildMenusFromNavTree(NavTree navTree) {
        if (navTree == null) {
            return null;
        }

        MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> builder = null;
        for (NavItem navItem : navTree.getRootItems()) {

            // Skip dividers
            if (navItem instanceof NavDivider) {
                continue;
            }
            // AF-953: Ignore empty groups
            if (navItem instanceof NavGroup && ((NavGroup) navItem).getChildren().isEmpty()) {
                continue;
            }
            // Build a top level menu entry
            if (builder == null) {
                builder = MenuFactory.newTopLevelMenu(navItem.getName());
            } else {
                builder = builder.endMenu().newTopLevelMenu(navItem.getName());
            }
            // Append its children
            if (navItem instanceof NavGroup) {
                List<MenuItem> childItems = buildMenuItemsFromNavGroup((NavGroup) navItem);
                builder.withItems(childItems);
            }
            // Append the place request
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            if (navCtx.getResourceId() != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {
                PlaceRequest placeRequest = resolvePlaceRequest(navCtx.getResourceId());
                builder = builder.place(placeRequest);
            }
        }
        return builder != null ? builder.endMenu() : null;
    }

    public List<MenuItem> buildMenuItemsFromNavGroup(NavGroup navGroup) {
        List<MenuItem> result = new ArrayList<>();
        for (NavItem navItem : navGroup.getChildren()) {

            // Skip dividers
            if (navItem instanceof NavDivider) {
                continue;
            }
            // Append its children
            MenuBuilder<Builder> builder = MenuFactory.newSimpleItem(navItem.getName());
            if (navItem instanceof NavGroup) {
                List<MenuItem> childItems = buildMenuItemsFromNavGroup((NavGroup) navItem);
                builder.withItems(childItems);
            }
            // Append the place request
            NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
            if (navCtx.getResourceId() != null && ActivityResourceType.PERSPECTIVE.equals(navCtx.getResourceType())) {
                PlaceRequest placeRequest = resolvePlaceRequest(navCtx.getResourceId());
                builder.place(placeRequest);
            }
            // Build the menu item & continue with the next one
            MenuItem menuItem = builder.endMenu().build().getItems().get(0);
            result.add(menuItem);
        }
        return result;
    }

    public PlaceRequest resolvePlaceRequest(String perspectiveId) {
        return new DefaultPlaceRequest(perspectiveId);
    }

    boolean hasAccessToPerspective(final String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                  ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              sessionInfo.getIdentity());
    }

    protected class LogoutCommand implements Command {

        @Override
        public void execute() {
            perspectiveManager.savePerspectiveState(() -> {
                placeManager.closePlace(perspectiveManager.getCurrentPerspectivePlaceRequest(),
                                        () -> doRedirect(getRedirectURL()));
            });
            // request.logout() happens as part of the redirected logout.jsp
        }

        void doRedirect(final String url) {
            redirect(url);
        }

        String getRedirectURL() {
            final String gwtModuleBaseURL = getGWTModuleBaseURL();
            final String gwtModuleName = getGWTModuleName();
            final String locale = getLocale();
            final String url = gwtModuleBaseURL.replaceFirst("/" + gwtModuleName + "/",
                                                             "/logout.jsp?locale=" + locale);
            return url;
        }

        String getGWTModuleBaseURL() {
            return GWT.getModuleBaseURL();
        }

        String getGWTModuleName() {
            return GWT.getModuleName();
        }

        String getLocale() {
            return LocaleInfo.getCurrentLocale().getLocaleName();
        }
    }

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;

}
