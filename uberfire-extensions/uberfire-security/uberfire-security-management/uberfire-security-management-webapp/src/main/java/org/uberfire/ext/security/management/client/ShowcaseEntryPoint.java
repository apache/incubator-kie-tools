/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.security.management.client;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.acl.PermissionTreeSetup;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

/**
 * GWT's entry-point for users management showcase.
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private ClientUserSystemManager userSystemManager;

    @Inject
    private PermissionTreeSetup permissionTreeSetup;

    @Inject
    private AdminPage adminPage;

    @AfterInitialization
    public void startApp() {
        // Wait for user management services to be initialized, if any.
        if ( null != userSystemManager ) {
            userSystemManager.waitForInitialization( () -> {
                permissionTreeSetup.configureTree();
                setupMenu();
                setupAdminPage();
                hideLoadingPopup();
            } );
        }
    }

    private void setupMenu() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> builder =
                newTopLevelMenu( "Home" )
                        .perspective( "HomePerspective" )
                        .endMenu()
                        .newTopLevelMenu( "Security" )
                        .perspective( "SecurityManagementPerspective" )
                        .endMenu()
                        .newTopLevelMenu( "Admin" )
                        .respondsWith( () -> {
                            Map<String, String> params = new HashMap<>();
                            params.put( "screen", "root" );
                            placeManager.goTo( new DefaultPlaceRequest( AdminPagePerspective.IDENTIFIER, params ) );
                        } )
                        .endMenu();

        Menus logoutMenus = MenuFactory.newSimpleItem( "Logout" )
                .respondsWith( new LogoutCommand() )
                .endMenu().build();

        final Menus menus = builder.build();
        menubar.addMenus( menus );
        menubar.addMenus( logoutMenus );
    }

    private void setupAdminPage() {
        adminPage.addScreen( "root", "Settings" );

        adminPage.addTool( "root",
                           "Roles",
                           "fa-unlock-alt",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "RolesTab" );
                               placeManager.goTo( new DefaultPlaceRequest( "SecurityManagementPerspective", params ) );
                           },
                           command -> userSystemManager.roles( ( AbstractEntityManager.SearchResponse<Role> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 15, null ) ) );

        adminPage.addTool( "root",
                           "Groups",
                           "fa-users",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "GroupsTab" );
                               placeManager.goTo( new DefaultPlaceRequest( "SecurityManagementPerspective", params ) );
                           },
                           command -> userSystemManager.groups( ( AbstractEntityManager.SearchResponse<Group> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 15, null ) ) );

        adminPage.addTool( "root",
                           "Users",
                           "fa-user",
                           "security",
                           () -> {
                               Map<String, String> params = new HashMap<>();
                               params.put( "activeTab", "UsersTab" );
                               placeManager.goTo( new DefaultPlaceRequest( "SecurityManagementPerspective", params ) );
                           },
                           command -> userSystemManager.users( ( AbstractEntityManager.SearchResponse<User> response ) -> {
                               if ( response != null ) {
                                   command.execute( response.getTotal() );
                               }
                           }, ( o, throwable ) -> false ).search( new SearchRequestImpl( "", 1, 1, null ) ) );
    }

    private class LogoutCommand implements Command {

        @Override
        public void execute() {
            authService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    final String location = GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "/logout.jsp" );
                    redirect( location );
                }
            } ).logout();
        }
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

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

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}