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

package org.kie.workbench.common.dmn.client.docks.navigator.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DecisionNavigatorTreePresenter {

    private final View view;

    private final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<>();

    private String activeParentUUID;

    @Inject
    public DecisionNavigatorTreePresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setupItems(final List<DecisionNavigatorItem> items) {
        getIndexedItems().clear();
        index(items);
        view.clean();
        view.setup(items);
    }

    public void removeAllItems() {
        view.clean();
        getIndexedItems().clear();
    }

    public DecisionNavigatorItem getActiveParent() {
        return getIndexedItems().get(activeParentUUID);
    }

    public void setActiveParentUUID(final String activeParentUUID) {
        this.activeParentUUID = activeParentUUID;
    }

    public void selectItem(final String uuid) {
        view.select(uuid);
    }

    public void deselectItem() {
        view.deselect();
    }

    void index(final Collection<DecisionNavigatorItem> items) {
        items.forEach(this::index);
    }

    void index(final DecisionNavigatorItem item) {
        getIndexedItems().put(item.getUUID(), item);
        index(item.getChildren());
    }

    Map<String, DecisionNavigatorItem> getIndexedItems() {
        return indexedItems;
    }

    public interface View extends UberElemental<DecisionNavigatorTreePresenter> {

        void clean();

        void setup(final List<DecisionNavigatorItem> items);

        void select(final String uuid);

        void deselect();
    }
}
