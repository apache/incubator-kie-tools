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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.widgets.client.menu.AboutMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.AppLauncherMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.workbench.client.library.LibraryMonitor;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherMenuBuilder;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;
import static org.uberfire.workbench.model.menu.MenuFactory.*;

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
    protected LibraryMonitor libraryMonitor;

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
                                               LibraryMonitor libraryMonitor) {
        this.iocManager = iocManager;
        this.activityManager = activityManager;
        this.perspectiveManager = perspectiveManager;
        this.authService = authService;
        this.identity = identity;
        this.userMenu = userMenu;
        this.utilityMenuBar = utilityMenuBar;
        this.libraryMonitor = libraryMonitor;
    }

    public List<? extends MenuItem> getHomeViews(final boolean socialEnabled ) {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        final List<MenuItem> result = new ArrayList<>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.HomePage() )
                            .perspective( defaultPerspective.getIdentifier() )
                            .endMenu()
                            .build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.Admin() )
                            .perspective( ADMIN )
                            .endMenu()
                            .build().getItems().get( 0 ) );

        result.addAll( getSocialViews( socialEnabled ) );

        return result;
    }

    protected List<MenuItem> getSocialViews( final boolean socialEnabled ) {
        if ( !socialEnabled ) {
            return Collections.emptyList();
        }

        final List<MenuItem> result = new ArrayList<>( 2 );

        result.add( MenuFactory.newSimpleItem( constants.Timeline() ).perspective( SOCIAL_HOME ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.People() ).perspective( SOCIAL_USER_HOME ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    public List<MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<>( 4 );

        result.add( MenuFactory.newSimpleItem( constants.ProjectAuthoring() ).perspective( LIBRARY ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Contributors() ).perspective( CONTRIBUTORS ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.ArtifactRepository() ).perspective( GUVNOR_M2REPO ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Administration() ).perspective( ADMINISTRATION ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    public List<? extends MenuItem> getProcessManagementViews() {
        final List<MenuItem> result = new ArrayList<>( 2 );

        result.add( MenuFactory.newSimpleItem( constants.ProcessDefinitions() ).perspective( PROCESS_DEFINITIONS ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.ProcessInstances() ).perspective(PROCESS_INSTANCES).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    public List<? extends MenuItem> getExtensionsViews() {
        final List<MenuItem> result = new ArrayList<>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.Plugins() ).perspective( PLUGIN_AUTHORING ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Apps() ).perspective( APPS ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.DataSets() ).perspective( DATASET_AUTHORING ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.DataSources() ).perspective( DATASOURCE_MANAGEMENT ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    public void addRolesMenuItems() {
        for ( Menus roleMenus : getRoles() ) {
            userMenu.addMenus( roleMenus );
        }
    }

    public void addGroupsMenuItems() {
        for ( Menus groups : getGroups() ) {
            userMenu.addMenus( groups );
        }
    }

    public void addWorkbenchViewModeSwitcherMenuItem() {
        userMenu.addMenus( MenuFactory.newTopLevelCustomMenu( iocManager.lookupBean( WorkbenchViewModeSwitcherMenuBuilder.class ).getInstance() ).endMenu().build() );
    }

    public void addWorkbenchConfigurationMenuItem() {
        utilityMenuBar.addMenus( MenuFactory.newTopLevelCustomMenu( iocManager.lookupBean( WorkbenchConfigurationMenuBuilder.class ).getInstance() ).endMenu().build() );
    }

    public void addUtilitiesMenuItems() {
        final Menus utilityMenus =
                MenuFactory
                        .newTopLevelCustomMenu( iocManager.lookupBean( AppLauncherMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( CustomSplashHelp.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( AboutMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( ResetPerspectivesMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( userMenu )
                        .endMenu()
                        .build();

        utilityMenuBar.addMenus( utilityMenus );
    }

    public void addLogoutMenuItem() {
        final Menus userMenus = MenuFactory.newTopLevelMenu( constants.LogOut() )
                .respondsWith( new LogoutCommand() )
                .endMenu()
                .build();

        userMenu.addMenus( userMenus );
    }

    public AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();

        while ( perspectivesIterator.hasNext() ) {
            final SyncBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break;
            } else {
                iocManager.destroyBean( instance );
            }
        }

        return defaultPerspective;
    }

    public List<PerspectiveActivity> getPerspectiveActivities() {
        final Set<PerspectiveActivity> activities = activityManager.getActivities( PerspectiveActivity.class );

        List<PerspectiveActivity> sortedActivitiesForDisplay = new ArrayList<>( activities );
        Collections.sort( sortedActivitiesForDisplay,
                          ( o1, o2 ) -> o1.getDefaultPerspectiveLayout().getName().compareTo( o2.getDefaultPerspectiveLayout().getName() ) );

        return sortedActivitiesForDisplay;
    }

    public List<MenuItem> getPerspectivesMenuItems() {
        final List<MenuItem> perspectives = new ArrayList<>();
        for ( final PerspectiveActivity perspective : getPerspectiveActivities() ) {
            final String name = perspective.getDefaultPerspectiveLayout().getName();
            final MenuItem item = newSimpleItem( name ).perspective( perspective.getIdentifier() ).endMenu().build().getItems().get( 0 );
            perspectives.add( item );
        }

        return perspectives;
    }

    public List<Menus> getRoles() {
        final Set<Role> roles = identity.getRoles();
        final List<Menus> result = new ArrayList<>( roles.size() );

        result.add( MenuFactory.newSimpleItem( constants.LogOut() ).respondsWith( new LogoutCommand() ).endMenu().build() );
        for ( final Role role : roles ) {
            if ( !role.getName().equals( "IS_REMEMBER_ME" ) ) {
                result.add( MenuFactory.newSimpleItem( constants.Role() + ": " + role.getName() ).endMenu().build() );
            }
        }

        return result;
    }

    public List<Menus> getGroups() {
        final Set<Group> groups = identity.getGroups();
        final List<Menus> result = new ArrayList<Menus>( groups.size() );

        for ( final Group group : groups ) {
            result.add( MenuFactory.newSimpleItem( constants.Group() + ": " + group.getName() ).endMenu().build() );
        }

        return result;
    }

    public TopLevelMenusBuilder<MenuBuilder> buildMenusFromNavTree(NavTree navTree) {
        MenuBuilder<TopLevelMenusBuilder<MenuBuilder>> builder = null;
        for (NavItem navItem : navTree.getRootItems()) {

            // Skip dividers
            if (navItem instanceof NavDivider) {
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
        switch (perspectiveId) {
            case AUTHORING:
                return new ConditionalPlaceRequest(AUTHORING)
                        .when(p -> libraryMonitor.thereIsAtLeastOneProjectAccessible())
                        .orElse(new DefaultPlaceRequest(LIBRARY));
            default:
                return new DefaultPlaceRequest(perspectiveId);
        }
    }

    protected class LogoutCommand implements Command {

        @Override
        public void execute() {
            perspectiveManager.savePerspectiveState( () -> authService.call( ( o ) -> doRedirect( getRedirectURL() ) ).logout() );
        }

        void doRedirect( final String url ) {
            redirect( url );
        }

        String getRedirectURL() {
            final String gwtModuleBaseURL = getGWTModuleBaseURL();
            final String gwtModuleName = getGWTModuleName();
            final String locale = getLocale();
            final String url = gwtModuleBaseURL.replaceFirst( "/" + gwtModuleName + "/", "/logout.jsp?locale=" + locale );
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

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;
}
