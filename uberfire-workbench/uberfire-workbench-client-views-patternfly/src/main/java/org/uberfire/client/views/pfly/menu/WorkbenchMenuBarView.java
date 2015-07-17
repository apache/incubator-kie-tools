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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * The Menu Bar widget
 */
@ApplicationScoped
public class WorkbenchMenuBarView extends Composite implements WorkbenchMenuBarPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @Inject
    private Instance<MainBrand> menuBarBrand;

    private final NavbarNav primaryNavBar = new NavbarNav();

    @Inject
    private UtilityNavbar utilityNavbar;

    @PostConstruct
    private void setup() {
        final Navbar root = new Navbar();
        root.addStyleName( "navbar-pf" );

        try {
            final NavbarHeader headerContainer = new NavbarHeader();
            final NavbarBrand brand = new NavbarBrand();
            brand.add(menuBarBrand.get());
            headerContainer.add(brand);
            root.add( headerContainer );
        } catch ( IOCResolutionException e ) {
            // app didn't provide a branded header bean
        }

        NavbarCollapse collapsibleContainer = new NavbarCollapse();

        primaryNavBar.addStyleName("navbar-primary");

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
                return primaryNavBar.getWidgetCount() + utilityNavbar.getWidgetCount();
            }

            @Override
            public void addMenuItem( MenuPosition position,
                                     AbstractListItem menuContent ) {
                if(menuContent instanceof UtilityMenu){
                    utilityNavbar.add( menuContent );
                } else {
                    if ( position == null ) {
                        position = MenuPosition.CENTER;
                    }
                    switch (position) {
                        case LEFT:
                            primaryNavBar.add(menuContent);
                            break;
                        case CENTER:
                            primaryNavBar.insert(menuContent, primaryNavBar.getWidgetCount() - 1);
                        case RIGHT:
                            menuContent.addStyleName(Styles.PULL_RIGHT);
                            primaryNavBar.add(menuContent);
                            break;
                    }
                }
            }
        };
        Bs3Menus.constructMenuView( menus, authzManager, identity, topLevelContainer );
    }

    @Override
    public void clear() {
        primaryNavBar.clear();
        utilityNavbar.clear();
    }

}
