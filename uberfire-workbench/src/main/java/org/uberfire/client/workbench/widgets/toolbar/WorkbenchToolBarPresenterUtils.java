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
package org.uberfire.client.workbench.widgets.toolbar;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * Utilities for WorkbenchMenuBarPresenter to filter Tool Bar items
 */
@ApplicationScoped
public class WorkbenchToolBarPresenterUtils {

    private final RuntimeAuthorizationManager authzManager;

    private final Identity                    identity;

    @Inject
    public WorkbenchToolBarPresenterUtils(final RuntimeAuthorizationManager authzManager,
                                          final Identity identity) {
        this.authzManager = authzManager;
        this.identity = identity;
    }

    //Remove menu bar items for which there are insufficient permissions
    public List<ToolBarItem> filterToolBarItemsByPermission(final List<ToolBarItem> items) {
        final List<ToolBarItem> itemsClone = new ArrayList<ToolBarItem>();
        for ( ToolBarItem item : items ) {
            final ToolBarItem itemClone = filterToolBarItemByPermission( item );
            if ( itemClone != null ) {
                itemsClone.add( itemClone );
            }
        }
        return itemsClone;
    }

    //Remove Tool Bar items for which there are insufficient permissions
    public ToolBarItem filterToolBarItemByPermission(final ToolBarItem item) {
        if ( !authzManager.authorize( item,
                                      identity ) ) {
            return null;
        }
        return item;
    }

}
