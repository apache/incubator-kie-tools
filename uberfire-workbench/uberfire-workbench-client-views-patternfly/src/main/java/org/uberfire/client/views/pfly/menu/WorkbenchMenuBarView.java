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
package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarHeader;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Menu Bar widget
 */
@Dependent
public class WorkbenchMenuBarView extends Composite implements WorkbenchMenuBarPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @Inject @MainBrand
    private Instance<NavbarBrand> menuBarBrand;

    @Inject
    private UserMenu userMenu;
    private final NavbarNav primaryNavBar = new NavbarNav();
    private final UtilityNavbar utilityNavbar = new UtilityNavbar();

    @PostConstruct
    private void setup() {
        Navbar root = new Navbar();
        root.addStyleName( "navbar-pf" );

        try {
            NavbarHeader headerContainer = new NavbarHeader();
            headerContainer.add(menuBarBrand.get());
            root.add( headerContainer );
        } catch ( IOCResolutionException e ) {
            // app didn't provide a branded header bean
        }

        NavbarCollapse collapsibleContainer = new NavbarCollapse();

        utilityNavbar.add( userMenu );
        primaryNavBar.addStyleName( "navbar-primary" );

        collapsibleContainer.add( utilityNavbar );
        collapsibleContainer.add( primaryNavBar );

        root.add( collapsibleContainer );

        initWidget( root );
    }

    @Override
    public void addMenuItems( final Menus menus ) {
        HasMenuItems topLevelContainer = new HasMenuItems() {

            @Override
            public Widget asWidget() {
                return WorkbenchMenuBarView.this;
            }

            @Override
            public int getMenuItemCount() {
                return primaryNavBar.getWidgetCount() + utilityNavbar.getWidgetCount() + userMenu.getWidgetCount();
            }

            @Override
            public void addMenuItem( MenuPosition position,
                                     AbstractListItem menuContent ) {
                if ( position == null ) {
                    position = MenuPosition.CENTER;
                }
                switch ( position ) {
                    case LEFT:
                        primaryNavBar.add( menuContent );
                        break;
                    case CENTER:
                        utilityNavbar.insert( menuContent, utilityNavbar.getWidgetCount() - 1 );
                        break;
                    case RIGHT:
                        userMenu.getMenu().add( menuContent );
                        break;
                }
            }
        };
        Bs3Menus.constructMenuView( menus, authzManager, identity, topLevelContainer );
    }

    @Override
    public void clear() {
        userMenu.clear();
        primaryNavBar.clear();
        utilityNavbar.clear();
        utilityNavbar.add( userMenu );
    }

}
