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
package org.uberfire.client.workbench.widgets.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.client.mvp.Command;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The MenuBar widget
 */
public class WorkbenchMenuBarView extends Composite
    implements
    WorkbenchMenuBarPresenter.View {

    private final MenuBar                         menuBar      = new MenuBar();

    //Map of UberFire's AbstractMenuItems to GWT MenuItems
    private final Map<AbstractMenuItem, MenuItem> menuItemsMap = new HashMap<AbstractMenuItem, MenuItem>();

    public WorkbenchMenuBarView() {
        initWidget( menuBar );
    }

    /**
     * Add a Presenter Menu item to the view. This simply converts Presenter
     * Menu items to GWT MenuItems. Filtering of menu items for permissions is
     * conducted by the Presenter.
     */
    @Override
    public void addMenuItem(final AbstractMenuItem item) {
        final MenuItem gwtItem = makeMenuItem( item );
        menuItemsMap.put( item,
                          gwtItem );
        menuBar.addItem( gwtItem );
    }

    /**
     * Remove a Presenter Menu item from the view.
     */
    @Override
    public void removeMenuItem(final AbstractMenuItem item) {
        final MenuItem gwtItem = menuItemsMap.remove( item );
        if ( gwtItem != null ) {
            menuBar.removeItem( gwtItem );
        }
    }

    //Recursively converts a Presenter Menu item to a GWT MenuItem
    private MenuItem makeMenuItem(final AbstractMenuItem item) {
        if ( item instanceof CommandMenuItem ) {
            final CommandMenuItem cmdItem = (CommandMenuItem) item;
            final MenuItem gwtItem = new MenuItem( cmdItem.getCaption(),
                                                   wrapCommand( cmdItem.getCommand() ) );
            gwtItem.setEnabled( item.isEnabled() );
            return gwtItem;

        } else if ( item instanceof SubMenuItem ) {
            final SubMenuItem subMenuItem = (SubMenuItem) item;
            final MenuBar gwtMenuBar = makeMenuBar( makeMenuItems( subMenuItem.getSubMenu().getItems() ) );
            final MenuItem gwtItem = new MenuItem( subMenuItem.getCaption(),
                                                   gwtMenuBar );
            gwtItem.setEnabled( item.isEnabled() );
            return gwtItem;
        }
        throw new IllegalArgumentException( "item type [" + item.getClass().getName() + "] is not recognised." );
    }

    //Wrap a Present's Menu item's command into a GWT Command for use in GWT's MenuItem
    private com.google.gwt.user.client.Command wrapCommand(final Command command) {
        final com.google.gwt.user.client.Command wrappedCommand = new com.google.gwt.user.client.Command() {

            @Override
            public void execute() {
                command.execute();
            }

        };
        return wrappedCommand;
    }

    private List<MenuItem> makeMenuItems(final List<AbstractMenuItem> items) {
        final List<MenuItem> gwtItems = new ArrayList<MenuItem>();
        for ( AbstractMenuItem item : items ) {
            final MenuItem gwtItem = makeMenuItem( item );
            gwtItems.add( gwtItem );
        }
        return gwtItems;
    }

    private MenuBar makeMenuBar(final List<MenuItem> items) {
        final MenuBar gwtMenuBar = new MenuBar( true );
        for ( MenuItem item : items ) {
            gwtMenuBar.addItem( item );
        }
        return gwtMenuBar;
    }

}
