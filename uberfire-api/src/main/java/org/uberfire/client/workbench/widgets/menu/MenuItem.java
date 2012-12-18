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

import com.google.gwt.user.client.ui.HasEnabled;
import org.uberfire.client.workbench.widgets.menu.impl.HasEnabledStateChangeListeners;
import org.uberfire.security.authz.RuntimeResource;

/**
 * Meta-data for a Workbench MenuItem including permissions. The default is that
 * all users have permission to access a MenuItem and that it is enabled.
 */
public interface MenuItem
        extends
        RuntimeResource,
        HasEnabled,
        HasEnabledStateChangeListeners {

    /**
     * @return the caption
     */
    public String getCaption();

    public void setRoles( final String[] roles );

}
