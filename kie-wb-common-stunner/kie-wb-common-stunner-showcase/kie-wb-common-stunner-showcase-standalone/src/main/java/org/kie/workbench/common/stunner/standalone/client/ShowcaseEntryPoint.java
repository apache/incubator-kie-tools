/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.standalone.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.stunner.standalone.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.stunner.standalone.client.perspectives.HomePerspective;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

/**
 * GWT's entry-point for Stunner showcase.
 */
@EntryPoint
public class ShowcaseEntryPoint {

    private static Logger LOGGER = Logger.getLogger( ShowcaseEntryPoint.class.getName() );

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private UserMenu userMenu;

    @Inject
    private UtilityMenuBar utilityMenuBar;

    @Inject
    public User identity;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private ClientUserSystemManager userSystemManager;

    @Inject
    private ErrorPopupPresenter errorPopupPresenter;

    @AfterInitialization
    public void startApp() {
        userSystemManager.waitForInitialization( new Command() {
            @Override
            public void execute() {
                setupGlobalErrorHandler();
                setupMenus();
                hideLoadingPopup();

            }
        } );

    }

    private void setupGlobalErrorHandler() {
        GWT.setUncaughtExceptionHandler( throwable -> {
            final String message = "Uncaught error on client side: " + throwable.getMessage();
            errorPopupPresenter.showMessage( message );
            log( Level.SEVERE, throwable.getMessage() );

        } );

    }

    private void setupMenus() {
        for ( Menus roleMenus : getRoles() ) {
            userMenu.addMenus( roleMenus );
        }
        refreshMenus();
    }

    private void refreshMenus() {
        menubar.clear();
        menubar.addMenus( createMenuBar() );
        final Menus utilityMenus =
                MenuFactory.newTopLevelCustomMenu( userMenu )
                        .endMenu()
                        .build();
        utilityMenuBar.addMenus( utilityMenus );
    }

    private Menus createMenuBar() {
        return newTopLevelMenu( "Home" )
                .perspective( HomePerspective.PERSPECTIVE_ID )
                .endMenu()
                .newTopLevelMenu( "Authoring" )
                .perspective( AuthoringPerspective.PERSPECTIVE_ID )
                .endMenu()
                .build();
    }

    private List<Menus> getRoles() {
        final List<Menus> result = new ArrayList<Menus>( identity.getRoles().size() );
        result.add( MenuFactory.newSimpleItem( "Logout" ).respondsWith( new LogoutCommand() ).endMenu().build() );
        for ( Role role : identity.getRoles() ) {
            if ( !role.getName().equals( "IS_REMEMBER_ME" ) ) {
                result.add( MenuFactory.newSimpleItem( "Role: " + role.getName() ).endMenu().build() );
            }
        }
        return result;
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

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}