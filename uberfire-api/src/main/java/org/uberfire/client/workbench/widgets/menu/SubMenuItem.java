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

/**
 * Meta-data for a menu item that represents a sub-menu
 */
public class SubMenuItem extends AbstractMenuItem {

    private final WorkbenchMenuBar subMenu;

    public SubMenuItem(final String caption,
                       final WorkbenchMenuBar subMenu) {
        super( caption );
        if ( subMenu == null ) {
            throw new NullPointerException( "subMenu cannot be null" );
        }
        this.subMenu = subMenu;
    }

    /**
     * @return the subMenu
     */
    public WorkbenchMenuBar getSubMenu() {
        return subMenu;
    }

    @Override
    public String getSignatureId() {
        return SubMenuItem.class.getName();
    }
}
