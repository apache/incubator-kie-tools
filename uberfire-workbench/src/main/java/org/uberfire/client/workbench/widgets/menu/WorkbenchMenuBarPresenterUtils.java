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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * Utilities for WorkbenchMenuBarPresenter to filter menu items
 */
@ApplicationScoped
public class WorkbenchMenuBarPresenterUtils {

    private final RuntimeAuthorizationManager authzManager;

    private final Identity identity;

    @Inject
    public WorkbenchMenuBarPresenterUtils( final RuntimeAuthorizationManager authzManager,
                                           final Identity identity ) {
        this.authzManager = authzManager;
        this.identity = identity;
    }

//    //Remove menu bar items for which there are insufficient permissions
//    public List<MenuItem> filterMenuItemsByPermission(final List<MenuItem> items) {
//        final List<MenuItem> itemsClone = new ArrayList<MenuItem>();
//        for ( MenuItem item : items ) {
//            final MenuItem itemClone = filterMenuItemByPermission( item );
//            if ( itemClone != null ) {
//                itemsClone.add( itemClone );
//            }
//        }
//        return itemsClone;
//    }
//
//    //Remove menu bar items for which there are insufficient permissions
//    public MenuItem filterMenuItemByPermission(final MenuItem item) {
//        if ( !authzManager.authorize( item,
//                                      identity ) ) {
//            return null;
//        }
//        if ( item instanceof MenuItemCommand ) {
//            return item;
//
//        } else if ( item instanceof MenuGroup ) {
//            final MenuGroup subMenuItem = (MenuGroup) item;
////            final MenuBar menuBarClone = cloneMenuBar( filterMenuItemsByPermission( subMenuItem.getSubMenu().getItems() ) );
////            final MenuItemSubMenu itemClone = new DefaultMenuItemSubMenu( subMenuItem.getCaption(),
////                                                                          menuBarClone );
////            return itemClone;
//        }
//        throw new IllegalArgumentException( "item type [" + item.getClass().getName() + "] is not recognised." );
//    }

    public Menus filterMenus( final Menus menus ) {
        return menus;
    }

//    private static MenuBar cloneMenuBar(final List<MenuItem> items) {
//        final MenuBar menuBar = new DefaultMenus();
//        for ( MenuItem item : items ) {
//            menuBar.addItem( item );
//        }
//        return menuBar;
//    }

}
