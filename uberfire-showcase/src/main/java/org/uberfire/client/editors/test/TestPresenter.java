/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.editors.test;

import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.user.client.Window;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchScreen(identifier = "Test")
public class TestPresenter {

    public interface View
        extends
        UberView<TestPresenter> {
    }

    @Inject
    public UberView<TestPresenter> view;

    @Inject
    private PlaceManager           placeManager;

    private static String[]        PERMISSIONS_NIL   = new String[]{};

    private static String[]        PERMISSIONS_ADMIN = new String[]{"ADMIN"};

    public TestPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Test";
    }

    @WorkbenchPartView
    public UberView<TestPresenter> getView() {
        return view;
    }

    public void launchWithPlaceRequestAndCallback() {
        final PlaceRequest place = new DefaultPlaceRequest( "Test2" );
        final Command callback = new Command() {

            @Override
            public void execute() {
                Window.alert( "Launched Test2 with callback" );
            }

        };
        placeManager.goTo( place,
                           callback );
    }

    public void launchPerspectiveWithPlaceRequest() {
        final PlaceRequest place = new DefaultPlaceRequest( "salaboy2" );
        place.addParameter( "test",
                            "testValue" );
        placeManager.goTo( place );
    }

    @WorkbenchMenu
    public MenuBar getMenuBar() {
        final MenuBar menuBar = new DefaultMenuBar();

        //Sub-menu#1 - No sub-menu
        menuBar.addItem( new DefaultMenuItemCommand( "TestPresenter menu-1",
                                                     new Command() {

                                                         @Override
                                                         public void execute() {
                                                             Window.alert( "You clicked me" );
                                                         }

                                                     } ) );

        //Sub-menu#2 - All items have permission and are enabled
        final MenuBar subMenuBar2 = new DefaultMenuBar();
        menuBar.addItem( new DefaultMenuItemSubMenu( "TestPresenter menu-2",
                                                     subMenuBar2 ) );
        for ( int i = 0; i < 3; i++ ) {
            final String caption = "TestPresenter menu-2:Item:" + i;
            final MenuItem item = new DefaultMenuItemCommand( caption,
                                                              new Command() {

                                                                  @Override
                                                                  public void execute() {
                                                                      Window.alert( "You clicked " + caption );
                                                                  }

                                                              } );
            subMenuBar2.addItem( item );
        }

        //Sub-menu#3 - Last two items have permission, last one item is enabled
        final MenuBar subMenuBar3a = new DefaultMenuBar();
        menuBar.addItem( new DefaultMenuItemSubMenu( "TestPresenter menu-3a",
                                                     subMenuBar3a ) );
        for ( int i = 0; i < 3; i++ ) {
            final String caption = "TestPresenter menu-3a:Item:" + i;
            final MenuItem item = new DefaultMenuItemCommand( caption,
                                                              new Command() {

                                                                  @Override
                                                                  public void execute() {
                                                                      Window.alert( "You clicked " + caption );
                                                                  }

                                                              } );
            item.setRoles( i > 0 ? PERMISSIONS_NIL : PERMISSIONS_ADMIN );
            item.setEnabled( i > 1 );
            subMenuBar3a.addItem( item );
        }

        //Sub-menu#4 - Append as child of subMenu3a and enable first item
        final MenuBar subMenuBar3b = new DefaultMenuBar();
        subMenuBar3a.addItem( new DefaultMenuItemSubMenu( "TestPresenter menu-3b",
                                                          subMenuBar3b ) );
        for ( int i = 0; i < 3; i++ ) {
            final String caption = "TestPresenter menu-3b:Item:" + i;
            final MenuItem item = new DefaultMenuItemCommand( caption,
                                                              new Command() {

                                                                  @Override
                                                                  public void execute() {
                                                                      Window.alert( "You clicked " + caption );
                                                                  }

                                                              } );
            item.setRoles( i == 0 ? PERMISSIONS_NIL : PERMISSIONS_ADMIN );
            subMenuBar3b.addItem( item );
        }

        return menuBar;
    }

}