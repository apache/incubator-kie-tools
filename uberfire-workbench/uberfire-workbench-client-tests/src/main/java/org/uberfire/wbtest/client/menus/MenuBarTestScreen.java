/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.menus;

import static org.uberfire.debug.Debug.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

@ApplicationScoped
@Named("org.uberfire.wbtest.client.menus.MenuBarTestScreen")
public class MenuBarTestScreen extends AbstractTestScreenActivity {

    public static final String UNRESTRICTED_MENU_LABEL = "Always Visible";
    public static final String STAFF_AND_ADMIN_MENU_LABEL = "Requires Either Staff Or Admin";
    public static final String STAFF_MENU_LABEL = "Requires Staff";
    public static final String ADMIN_MENU_LABEL = "Requires Admin";

    @Inject
    private WorkbenchMenuBarPresenter menuBarPresenter;

    @Inject
    private User user;

    private final VerticalPanel panel = new VerticalPanel();

    @Inject
    public MenuBarTestScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @PostConstruct
    private void setupMenus() {
        panel.getElement().setId( shortName( getClass() ) );
        Menus menus = MenuFactory
          .newTopLevelMenu( ADMIN_MENU_LABEL ).withRoles( "admin" )
            .menus()
              .menu( "Admin Item" ).endMenu()
            .endMenus()
          .endMenu()
          .newTopLevelMenu( STAFF_MENU_LABEL ).withRoles( "staff" )
            .menus()
              .menu( "Staff Item" ).endMenu()
            .endMenus()
          .endMenu()
          .newTopLevelMenu( STAFF_AND_ADMIN_MENU_LABEL ).withRoles( "staff", "admin" )
            .menus()
              .menu( "Staff + Admin Item" ).endMenu()
            .endMenus()
          .endMenu()
          .newTopLevelMenu( UNRESTRICTED_MENU_LABEL )
            .menus()
              .menu( "Unsecured Item" ).endMenu()
            .endMenus()
          .endMenu()
        .build();

        menuBarPresenter.addMenus( menus );

        panel.add( new Label( "Your user information: " + user ) );
        panel.add( new Label( "The menus you can see:" ) );
        panel.add( menuBarPresenter.getView() );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
