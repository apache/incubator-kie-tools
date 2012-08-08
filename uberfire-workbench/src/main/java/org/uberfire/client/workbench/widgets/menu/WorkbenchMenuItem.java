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

import org.uberfire.client.workbench.security.RequiresPermission;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A Workbench MenuItem adding permissions to GWT's MenuItem
 */
public class WorkbenchMenuItem extends MenuItem
    implements
    RequiresPermission {

    //TODO {manstis} This will be replaced with a Set of permissions required for the command
    //to be executed. How this might look is undecided but will be re-visited when a Workbench
    //SecurityManager is developed.
    private boolean hasPermission = false;

    /**
     * Constructs a new menu item that fires a command when it is selected.
     * 
     * @param html
     *            the item's html text
     */
    public WorkbenchMenuItem(SafeHtml html) {
        super( html );
    }

    /**
     * Constructs a new menu item that fires a command when it is selected.
     * 
     * @param html
     *            the item's text
     * @param cmd
     *            the command to be fired when it is selected
     */
    public WorkbenchMenuItem(SafeHtml html,
                             Command cmd) {
        super( html,
               cmd );
    }

    /**
     * Constructs a new menu item that cascades to a sub-menu when it is
     * selected.
     * 
     * @param html
     *            the item's text
     * @param subMenu
     *            the sub-menu to be displayed when it is selected
     */
    public WorkbenchMenuItem(SafeHtml html,
                             MenuBar subMenu) {
        super( html,
               subMenu );
    }

    /**
     * Constructs a new menu item that fires a command when it is selected.
     * 
     * @param text
     *            the item's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as html
     * @param cmd
     *            the command to be fired when it is selected
     */
    public WorkbenchMenuItem(String text,
                             boolean asHTML,
                             Command cmd) {
        super( text,
               asHTML,
               cmd );
    }

    /**
     * Constructs a new menu item that cascades to a sub-menu when it is
     * selected.
     * 
     * @param text
     *            the item's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as html
     * @param subMenu
     *            the sub-menu to be displayed when it is selected
     */
    public WorkbenchMenuItem(String text,
                             boolean asHTML,
                             MenuBar subMenu) {
        super( text,
               asHTML,
               subMenu );
    }

    /**
     * Constructs a new menu item that fires a command when it is selected.
     * 
     * @param text
     *            the item's text
     * @param cmd
     *            the command to be fired when it is selected
     */
    public WorkbenchMenuItem(String text,
                             Command cmd) {
        super( text,
               cmd );
    }

    /**
     * Constructs a new menu item that cascades to a sub-menu when it is
     * selected.
     * 
     * @param text
     *            the item's text
     * @param subMenu
     *            the sub-menu to be displayed when it is selected
     */
    public WorkbenchMenuItem(String text,
                             MenuBar subMenu) {
        super( text,
               subMenu );
    }

    /**
     * @return the hasPermission
     */
    public boolean hasPermission() {
        return hasPermission;
    }

    /**
     * @param hasPermission
     *            the hasPermission to set
     */
    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

}
