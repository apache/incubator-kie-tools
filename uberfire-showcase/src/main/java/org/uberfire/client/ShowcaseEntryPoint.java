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
package org.uberfire.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.ShowcaseResources;
import org.uberfire.client.workbench.annotations.DefaultPerspective;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.MenuItemSubMenu;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private IOCBeanManager                  manager;

    @Inject
    private WorkbenchMenuBarPresenter       menubar;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager                    placeManager;

    @Inject
    private IOCBeanManager                  iocManager;

    @Inject
    private ActivityManager                 activityManager;

    private String[]                        menuItems = new String[]{"MyAdminArea", "MyAdminArea2", "Monitoring", "Test", "Test2", "Test6", "FileExplorer", "RepositoriesEditor", "Chart", "chartPopulator", "GoogleGadgetScreen"};

    @AfterInitialization
    public void startApp() {
        loadStyles();
        setupMenu();
        hideLoadingPopup();

        //Register call-backs for demo
        placeManager.registerOnRevealCallback( new DefaultPlaceRequest( "TestPerspective" ),
                                               new Command() {

                                                   @Override
                                                   public void execute() {
                                                       Window.alert( "Callback!" );
                                                   }

                                               } );
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        ShowcaseResources.INSTANCE.CSS().ensureInjected();
    }

    private void setupMenu() {

        //Home
        final AbstractPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        if ( defaultPerspective != null ) {
            menubar.addMenuItem( new DefaultMenuItemCommand( "Home",
                                                             new Command() {
                                                                 @Override
                                                                 public void execute() {
                                                                     placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                                                                 }
                                                             } ) );
        }

        //Perspectives
        final MenuBar perspectivesMenuBar = new DefaultMenuBar();
        final MenuItemSubMenu perspectivesMenu = new DefaultMenuItemSubMenu( "Perspectives",
                                                                             perspectivesMenuBar );
        final List<AbstractPerspectiveActivity> perspectives = getPerspectiveActivities();
        for ( final AbstractPerspectiveActivity perspective : perspectives ) {
            final String name = perspective.getPerspective().getName();
            final Command cmd = new Command() {

                @Override
                public void execute() {
                    placeManager.goTo( new DefaultPlaceRequest( perspective.getIdentifier() ) );
                }

            };
            final MenuItemCommand item = new DefaultMenuItemCommand( name,
                                                                     cmd );
            perspectivesMenuBar.addItem( item );
        }
        menubar.addMenuItem( perspectivesMenu );

        //Static places
        final MenuBar placesMenuBar = new DefaultMenuBar();
        final MenuItemSubMenu placesMenu = new DefaultMenuItemSubMenu( "Places",
                                                                       placesMenuBar );
        Arrays.sort( menuItems );
        for ( final String menuItem : menuItems ) {
            final MenuItemCommand item = new DefaultMenuItemCommand( menuItem,
                                                                     new Command() {

                                                                         @Override
                                                                         public void execute() {
                                                                             placeManager.goTo( new DefaultPlaceRequest( menuItem ) );
                                                                         }

                                                                     } );
            placesMenuBar.addItem( item );
        }
        final MenuItemCommand item = new DefaultMenuItemCommand( "Logout",
                                                                 new Command() {
                                                                     @Override
                                                                     public void execute() {
                                                                         redirect( "/uf_logout" );
                                                                     }
                                                                 } );
        placesMenuBar.addItem( item );
        menubar.addMenuItem( placesMenu );
    }

    private AbstractPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractPerspectiveActivity defaultPerspective = null;
        Collection<IOCBeanDef<AbstractPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractPerspectiveActivity.class );
        Iterator<IOCBeanDef<AbstractPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop : while ( perspectivesIterator.hasNext() ) {
            IOCBeanDef<AbstractPerspectiveActivity> perspective = perspectivesIterator.next();
            Set<Annotation> annotations = perspective.getQualifiers();
            for ( Annotation a : annotations ) {
                if ( a instanceof DefaultPerspective ) {
                    defaultPerspective = perspective.getInstance();
                    break outer_loop;
                }
            }
        }
        return defaultPerspective;
    }

    private List<AbstractPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractPerspectiveActivity> activities = activityManager.getActivities( AbstractPerspectiveActivity.class );

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractPerspectiveActivity> sortedActivities = new ArrayList<AbstractPerspectiveActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractPerspectiveActivity>() {

                              @Override
                              public int compare(AbstractPerspectiveActivity o1,
                                                 AbstractPerspectiveActivity o2) {
                                  return o1.getPerspective().getName().compareTo( o2.getPerspective().getName() );
                              }

                          } );

        return sortedActivities;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect(String url)/*-{
		$wnd.location = url;
    }-*/;

}
