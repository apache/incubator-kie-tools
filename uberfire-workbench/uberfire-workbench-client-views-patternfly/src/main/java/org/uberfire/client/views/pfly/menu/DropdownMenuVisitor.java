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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

/**
 * Created by Cristiano Nicolai.
 */
public class DropdownMenuVisitor extends BaseMenuVisitor {

    protected final HasMenuItems hasMenuItems;

    public DropdownMenuVisitor( final HasMenuItems hasMenuItems ) {
        this.hasMenuItems = hasMenuItems;
    }

    @Override
    public boolean visitEnter( final MenuGroup menuGroup ) {
        buildMenuGroup( menuGroup, this.hasMenuItems );
        return true;
    }

    protected Widget buildMenuGroup( final MenuGroup menuGroup, final HasMenuItems hasMenuItems ) {
        final DropDownHeader group = new DropDownHeader();
        group.setText( menuGroup.getCaption() );
        hasMenuItems.addMenuItem( menuGroup.getPosition(), group );
        return group;
    }

    @Override
    public void visit( final MenuCustom<?> menuCustom ) {
        final IsWidget customMenuItem = ( (IsWidget) menuCustom.build() ).asWidget();
        if ( customMenuItem instanceof AnchorListItem ) {
            final AnchorListItem view = (AnchorListItem) customMenuItem;
            setupEnableDisable( menuCustom, view );
            this.hasMenuItems.addMenuItem( menuCustom.getPosition(), view );
        } else {
            buildMenuCustom( menuCustom, this.hasMenuItems );
        }
    }

    protected Widget buildMenuCustom( final MenuCustom<?> menuCustom, final HasMenuItems hasMenuItems ) {
        return createListItem( menuCustom, hasMenuItems );
    }

    @Override
    public void visit( final MenuItemCommand menuItemCommand ) {
        buildMenuCommand( menuItemCommand, this.hasMenuItems );
    }

    protected Widget buildMenuCommand( final MenuItemCommand menuItemCommand, final HasMenuItems hasMenuItems ) {
        final AnchorListItem item = createListItem( menuItemCommand, hasMenuItems );
        item.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                menuItemCommand.getCommand().execute();
            }
        } );
        return item;
    }

    @Override
    public void visit( final MenuItemPerspective menuItemPerspective ) {
        buildMenuPerspective( menuItemPerspective, this.hasMenuItems );
    }

    protected Widget buildMenuPerspective( final MenuItemPerspective menuItemPerspective, final HasMenuItems hasMenuItems ) {
        final AnchorListItem item = createListItem( menuItemPerspective, hasMenuItems );
        item.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance().goTo( menuItemPerspective.getPlaceRequest() );
            }
        } );
        return item;
    }

    @Override
    public void visit( final MenuItemPlain menuItemPlain ) {
        buildMenuPlain( menuItemPlain, this.hasMenuItems );
    }

    protected Widget buildMenuPlain( final MenuItemPlain menuItemPlain, final HasMenuItems hasMenuItems ) {
        return createListItem( menuItemPlain, hasMenuItems );
    }

    protected AnchorListItem createListItem( final MenuItem menuItem, final HasMenuItems hasMenuItems ) {
        final AnchorListItem option = new AnchorListItem();
        option.setText( menuItem.getCaption() );
        setupEnableDisable( menuItem, option );
        hasMenuItems.addMenuItem( menuItem.getPosition(), option );
        return option;
    }

    /**
     * Sets up the enabled/disabled state of the view widget, and installs a listener on the model to keep the
     * widget's enabled state in sync with it.
     * @param model the description of the menu item to get the current enabled state from, and to subscibe to for
     * future changes.
     * @param view the widget that provides a view of the given model.
     */
    protected void setupEnableDisable( final MenuItem model, final AnchorListItem view ) {
        view.setEnabled( model.isEnabled() );
        model.addEnabledStateChangeListener( new EnabledStateChangeListener() {
            @Override
            public void enabledStateChanged( final boolean enabled ) {
                view.setEnabled( enabled );
            }
        } );
    }
}
