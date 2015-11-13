/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.views.pfly.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.PerspectiveContextMenusPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
public class WorkbenchMenuStandardNavBarView extends NavbarNav implements WorkbenchMenuBarView.NavBarView, HasMenuItems {

    private final Map<MenuItemPerspective, Widget> listItemMap = new HashMap<MenuItemPerspective, Widget>();

    @Inject
    private PerspectiveContextMenusPresenter.View perspectiveContextMenuView;

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @PostConstruct
    protected void setup() {
        this.addStyleName( "navbar-primary" );
    }

    @Override
    public void addMenus( final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new StandardMenuVisitor( this ) ) );
    }

    @Override
    public void addMenuItem( MenuPosition position, final Widget menuContent ) {
        if ( position == null ) {
            position = MenuPosition.CENTER;
        }
        switch ( position ) {
            case LEFT:
                this.add( menuContent );
                break;
            case CENTER:
                this.insert( menuContent, WorkbenchMenuStandardNavBarView.this.getWidgetCount() - 1 );
            case RIGHT:
                menuContent.addStyleName( Styles.PULL_RIGHT );
                this.add( menuContent );
                break;
        }
    }

    @Override
    public void selectMenu( final MenuItem menu ) {
        selectElement( listItemMap.get( menu ) );
    }

    private void selectElement( final Widget item ) {
        iterateWidgets( this );

        if ( item != null ) {
            item.addStyleName( Styles.ACTIVE );
        }

        if ( item.getParent().getParent() instanceof ListDropDown ) {
            item.getParent().getParent().addStyleName( Styles.ACTIVE );
        }
    }

    private void iterateWidgets( final Widget widget ) {
        widget.removeStyleName( Styles.ACTIVE );
        if ( widget instanceof ComplexPanel ) {
            final Iterator<Widget> iterator = ( (ComplexPanel) widget ).iterator();
            while ( iterator.hasNext() ) {
                iterateWidgets( iterator.next() );
            }
        }
    }

    private class StandardMenuVisitor extends StackedDropdownMenuVisitor {

        public StandardMenuVisitor( final HasMenuItems hasMenuItems ) {
            super( hasMenuItems );
        }

        @Override
        protected Widget buildMenuPerspective( final MenuItemPerspective menuItemPerspective, final HasMenuItems hasMenuItems ) {
            final Widget item = super.buildMenuPerspective( menuItemPerspective, hasMenuItems );
            listItemMap.put( menuItemPerspective, item );
            return item;
        }
    }
}

