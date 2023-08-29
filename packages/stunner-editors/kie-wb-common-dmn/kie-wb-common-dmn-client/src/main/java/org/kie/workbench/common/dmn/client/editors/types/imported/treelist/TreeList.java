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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Node;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TreeList {

    private final View view;

    private List<TreeListItem> currentItems;

    private Consumer<List<TreeListItem>> onSelectionChanged;

    @Inject
    public TreeList(final View view) {
        this.view = view;
    }

    public void populate(final List<TreeListItem> items) {
        currentItems = items;
        refresh();
    }

    public void refresh() {
        clear();
        for (final TreeListItem item : getCurrentItems()) {
            item.updateView();
            view.add(item);
            item.setOnIsSelectedChanged(this::selectionChanged);
        }
    }

    @SuppressWarnings("unused")
    void selectionChanged(final TreeListItem treeListItem) {
        callOnSelectionChanged();
    }

    void callOnSelectionChanged() {
        if (!Objects.isNull(getOnSelectionChanged())) {
            getOnSelectionChanged().accept(getSelectedItems());
        }
    }

    public List<TreeListItem> getCurrentItems() {
        return currentItems;
    }

    public void clear() {
        view.clear();
    }

    public Node getElement() {
        return view.getElement();
    }

    public void clearSelection() {
        getCurrentItems().stream().forEach(item -> item.setIsSelected(false));
    }

    public List<TreeListItem> getSelectedItems() {
        return getCurrentItems().stream()
                .filter(item -> item.getIsSelected())
                .collect(Collectors.toList());
    }

    public void setOnSelectionChanged(final Consumer<List<TreeListItem>> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }

    Consumer<List<TreeListItem>> getOnSelectionChanged() {
        return onSelectionChanged;
    }

    public interface View extends UberElemental<TreeList> {

        void add(final TreeListItem item);

        void clear();
    }
}
