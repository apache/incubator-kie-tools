/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

/**
 * GWT's Entry-point for showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private SyncBeanManager manager;

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private Caller<AppConfigService> appConfigService;

    @AfterInitialization
    public void startApp() {
        loadPreferences();
        hideLoadingPopup();
    }

    private void setupMenu( @Observes final ApplicationReadyEvent event ) {
        final PerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

        final Menus menus =
                newTopLevelMenu( "Home" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                if ( defaultPerspective != null ) {
                                    placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                                } else {
                                    Window.alert( "Default perspective not found." );
                                }
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Contributors" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "ContributorsPerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Server Management" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "ServerManagementPerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Search" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "SearchPerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "User Home" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "UserHomePagePerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Social Home" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "SocialHomePagePerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Project Explorer" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( "ProjectPerspective" );
                            }
                        } )
                        .endMenu()
                        .newTopLevelMenu( "Logout" )
                        .position( MenuPosition.RIGHT )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                logout();
                            }
                        } ).endMenu()
                        .build();

        menubar.addMenus( menus );
    }

    private PerspectiveActivity getDefaultPerspectiveActivity() {
        PerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<PerspectiveActivity>> perspectives = manager.lookupBeans( PerspectiveActivity.class );
        final Iterator<IOCBeanDef<PerspectiveActivity>> perspectivesIterator = perspectives.iterator();

        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<PerspectiveActivity> perspective = perspectivesIterator.next();
            final PerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break;
            } else {
                manager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    private void loadPreferences() {
        appConfigService.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( final Map<String, String> response ) {
                response.put( ApplicationPreferences.DATE_FORMAT, "dd-MM-yyyy" );
                ApplicationPreferences.setUp( response );
            }
        } ).loadPreferences();
    }

    /**
     * Logout user
     */
    public void logout() {
        authService.call().logout();
    }

    //Fade out the "Loading application" pop-up
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

}