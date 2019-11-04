/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Node;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TreeList {

    private final View view;

    private List<TreeListItem> currentItems;

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

    public interface View extends UberElemental<TreeList> {

        void add(final TreeListItem item);

        void clear();
    }
}
