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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.inject.Inject;

import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

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

    @AfterInitialization
    public void startApp() {
        setupMenu();
        hideLoadingPopup();

        // Default perspective.
        placeManager.goTo( new DefaultPlaceRequest( "HomePerspective" ) );
    }

    private void setupMenu() {
        final MenuFactory.TopLevelMenusBuilder<MenuFactory.MenuBuilder> builder = newTopLevelMenu("Home").respondsWith(new Command() {
            @Override
            public void execute() {
                placeManager.goTo(new DefaultPlaceRequest("HomePerspective"));
            }
        }).endMenu();

        if ( null != userSystemManager ) {
            // Wait for user management services to be initialized, if any.
            userSystemManager.waitForInitialization(new Command() {
                @Override
                public void execute() {
                    if (userSystemManager.isActive()) {
                        builder.newTopLevelMenu("Users management").respondsWith(new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo(new DefaultPlaceRequest("UsersManagementPerspective"));
                            }
                        }).endMenu().
                                newTopLevelMenu("Groups management")
                                .respondsWith(new Command() {
                                    @Override
                                    public void execute() {
                                        placeManager.goTo(new DefaultPlaceRequest("GroupsManagementPerspective"));
                                    }
                                }).endMenu();

                    } else {
                        GWT.log("Users management is NOT ACTIVE.");
                    }

                    final Menus menus = builder.build();

                    Menus logoutMenus = MenuFactory.newSimpleItem("Logout").respondsWith(new LogoutCommand()).endMenu().build();
                    menubar.addMenus(menus);
                    menubar.addMenus(logoutMenus);
                }
            });
        }
       
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