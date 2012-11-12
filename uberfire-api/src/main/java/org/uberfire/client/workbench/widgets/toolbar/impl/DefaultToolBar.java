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
package org.uberfire.client.workbench.widgets.toolbar.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * Default implementation of ToolBar
 */
public class DefaultToolBar
    implements
    ToolBar {

    private List<ToolBarItem> items = new ArrayList<ToolBarItem>();

    @Override
    public void addItem(ToolBarItem item) {
        checkNotNull("item",
                item);
        this.items.add( item );
    }

    @Override
    public List<ToolBarItem> getItems() {
        return this.items;
    }

    @Override
    public String getSignatureId() {
        return DefaultToolBar.class.getName();
    }

    @Override
    public Collection<String> getRoles() {
        return null;
    }

    @Override
    public Collection<String> getTraits() {
        return null;
    }

}
