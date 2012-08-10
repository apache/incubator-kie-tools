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

/**
 * Meta-data for a Workbench MenuItem including permissions. The default is that
 * all users have permission to access a MenuItem and that it is enabled.
 */
public abstract class AbstractMenuItem
    implements
    RequiresPermission {

    private boolean      hasPermission = true;

    private boolean      enabled       = true;

    private final String caption;

    public AbstractMenuItem(final String caption) {
        if ( caption == null ) {
            throw new NullPointerException( "caption cannot be null" );
        }
        this.caption = caption;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @return the hasPermission
     */
    public boolean hasPermission() {
        return hasPermission;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param hasPermission
     *            the hasPermission to set
     */
    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

}
