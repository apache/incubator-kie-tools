/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
public class WorkbenchMenuCompactNavBarView extends NavbarNav implements WorkbenchMenuBarView.NavBarView, HasMenuItems {

    private final AnchorButton anchor = new AnchorButton();
    private final Text text = new Text();
    private final DropDownMenu dropDownMenu = new DropDownMenu();
    private final ListDropDown listDropDown = new ListDropDown();

    private final Map<MenuItemPerspective, Widget> listItemMap = new HashMap<MenuItemPerspective, Widget>();

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @PostConstruct
    protected void setup() {
        this.addStyleName( "uf-navbar-nav-compact" );
        anchor.setDataToggle( Toggle.DROPDOWN );
        anchor.add( text );
        listDropDown.add( anchor );
        listDropDown.add( dropDownMenu );
        this.add( listDropDown );
    }

    @Override
    public void addMenuItem( final MenuPosition position, final Widget menuContent ) {
        dropDownMenu.add( menuContent );
    }

    @Override
    public void addMenus( final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, identity, new CompactMenuVisitor( this ) ) );
    }

    @Override
    public void selectMenu( final MenuItem menu ) {
        selectElement( menu.getCaption(), listItemMap.get( menu ) );
    }

    private void selectElement( final String caption, final Widget item ) {
        final Iterator<Widget> iterator = dropDownMenu.iterator();
        while ( iterator.hasNext() ) {
            iterator.next().removeStyleName( Styles.ACTIVE );
        }
        if ( item != null ) {
            item.addStyleName( Styles.ACTIVE );
        }
        text.setText( caption );
    }

    private class CompactMenuVisitor extends DropdownMenuVisitor {

        public CompactMenuVisitor( final HasMenuItems hasMenuItems ) {
            super( hasMenuItems );
        }

        @Override
        protected AnchorListItem createListItem( final MenuItem menuItem, final HasMenuItems hasMenuItems ) {
            final AnchorListItem item = super.createListItem( menuItem, hasMenuItems );
            item.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    selectElement( menuItem.getCaption(), item );
                }
            } );
            return item;
        }

        @Override
        protected Widget buildMenuPerspective( final MenuItemPerspective menuItemPerspective, final HasMenuItems hasMenuItems ) {
            final Widget item = super.buildMenuPerspective( menuItemPerspective, hasMenuItems );
            listItemMap.put( menuItemPerspective, item );
            return item;
        }
    }
}
