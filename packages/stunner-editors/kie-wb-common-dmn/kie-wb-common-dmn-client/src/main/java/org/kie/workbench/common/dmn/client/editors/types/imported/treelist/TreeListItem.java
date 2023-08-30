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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Node;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TreeListItem {

    private View view;
    private final List<TreeListSubItem> subItems;
    private String description;
    private boolean isSelected;
    private DataObject dataSource;
    private Consumer<TreeListItem> onIsSelectedChanged;

    @Inject
    public TreeListItem(final View view) {
        this.view = view;
        this.subItems = new ArrayList<>();
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public Node getElement() {
        return view.getElement();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void addSubItem(final TreeListSubItem subItem) {
        subItems.add(subItem);
    }

    public List<TreeListSubItem> getSubItems() {
        return subItems;
    }

    public void updateView() {
        this.view.populate(this);
    }

    public void setIsSelected(final boolean value) {
        this.isSelected = value;
        callOnIsSelectedChanged();
    }

    void callOnIsSelectedChanged() {
        if (!Objects.isNull(getOnIsSelectedChanged())) {
            getOnIsSelectedChanged().accept(this);
        }
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setDataSource(final DataObject dataSource) {
        this.dataSource = dataSource;
    }

    public DataObject getDataSource() {
        return this.dataSource;
    }

    public void setOnIsSelectedChanged(final Consumer<TreeListItem> onIsSelectedChanged) {
        this.onIsSelectedChanged = onIsSelectedChanged;
    }

    public Consumer<TreeListItem> getOnIsSelectedChanged() {
        return onIsSelectedChanged;
    }

    public interface View extends UberElemental<TreeListItem> {

        void populate(final TreeListItem treeListItem);
    }
}
