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
package org.uberfire.client.workbench;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.workbench.security.RequiresPermission;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A Workbench MenuBar adding permissions to GWT's MenuBar
 */
@ApplicationScoped
public class WorkbenchMenuBar extends MenuBar
    implements
    RequiresPermission {

    /**
     * Creates an empty horizontal menu bar.
     */
    public WorkbenchMenuBar() {
        super( false );
    }

    /**
     * Creates an empty menu bar.
     * 
     * @param vertical
     *            <code>true</code> to orient the menu bar vertically
     */
    public WorkbenchMenuBar(boolean vertical) {
        super( vertical );
    }

    /**
     * Creates an empty menu bar that uses the specified ClientBundle for menu
     * images.
     * 
     * @param vertical
     *            <code>true</code> to orient the menu bar vertically
     * @param resources
     *            a bundle that provides images for this menu
     */
    public WorkbenchMenuBar(boolean vertical,
                            Resources resources) {
        super( vertical,
               resources );
    }

    /**
     * Creates an empty horizontal menu bar that uses the specified ClientBundle
     * for menu images.
     * 
     * @param resources
     *            a bundle that provides images for this menu
     */
    public WorkbenchMenuBar(Resources resources) {
        super( resources );
    }

    @Override
    public List<MenuItem> getItems() {
        return super.getItems();
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

}
