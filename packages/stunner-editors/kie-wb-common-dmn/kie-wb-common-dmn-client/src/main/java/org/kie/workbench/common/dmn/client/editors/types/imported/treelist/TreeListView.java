/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;

@Dependent
@Templated
public class TreeListView implements TreeList.View {

    private TreeList presenter;

    @DataField("tree-items-container")
    private final HTMLDivElement treeItemsContainer;

    @Inject
    public TreeListView(final HTMLDivElement treeItemsContainer) {
        this.treeItemsContainer = treeItemsContainer;
    }

    @Override
    public void init(final TreeList presenter) {
        this.presenter = presenter;
    }

    @Override
    public void add(final TreeListItem item) {
        treeItemsContainer.appendChild(item.getElement());
    }

    @Override
    public void clear() {
        RemoveHelper.removeChildren(treeItemsContainer);
    }

    @Override
    public HTMLElement getElement() {
        return treeItemsContainer;
    }
}
