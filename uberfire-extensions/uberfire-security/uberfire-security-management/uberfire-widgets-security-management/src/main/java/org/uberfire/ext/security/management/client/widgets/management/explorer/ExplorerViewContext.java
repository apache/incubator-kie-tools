/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import java.util.Set;

/**
 * <p>Explorer View context implementation that has a parent view context.</p>
 * <p>Just override methods of your interest. By default, they delegate to the parent context.</p>
 */
public class ExplorerViewContext implements EntitiesExplorerView.ViewContext {
    
    private EntitiesExplorerView.ViewContext parent;

    public ExplorerViewContext() {
    }

    @Override
    public boolean canSearch() {
        return parent.canSearch();
    }

    @Override
    public boolean canCreate() {
        return parent.canCreate();
    }

    @Override
    public boolean canRead() {
        return parent.canRead();
    }

    @Override
    public boolean canDelete() {
        return parent.canDelete();
    }

    @Override
    public boolean canSelect() {
        return parent.canSelect();
    }

    @Override
    public Set<String> getSelectedEntities() {
        return parent.getSelectedEntities();
    }

    @Override
    public Set<String> getConstrainedEntities() {
        return parent.getConstrainedEntities();
    }

    public EntitiesExplorerView.ViewContext getParent() {
        return parent;
    }

    public void setParent(EntitiesExplorerView.ViewContext parent) {
        this.parent = parent;
    }
}
