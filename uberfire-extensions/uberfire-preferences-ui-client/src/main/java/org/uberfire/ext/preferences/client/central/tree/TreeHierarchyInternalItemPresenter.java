/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.preferences.client.central.tree;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyInternalItemPresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyItemPresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyItemView;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;

public class TreeHierarchyInternalItemPresenter implements HierarchyInternalItemPresenter {

    private final View view;
    private final ManagedInstance<TreeHierarchyInternalItemPresenter> treeHierarchyInternalItemPresenterProvider;
    private final ManagedInstance<TreeHierarchyLeafItemPresenter> treeHierarchyLeafItemPresenterProvider;
    private final Event<HierarchyItemSelectedEvent> hierarchyItemSelectedEvent;
    private List<HierarchyItemPresenter> hierarchyItems;
    private PreferenceHierarchyElement<?> hierarchyElement;
    private int level;

    @Inject
    public TreeHierarchyInternalItemPresenter(final View view,
                                              final ManagedInstance<TreeHierarchyInternalItemPresenter> treeHierarchyInternalItemPresenterProvider,
                                              final ManagedInstance<TreeHierarchyLeafItemPresenter> treeHierarchyLeafItemPresenterProvider,
                                              final Event<HierarchyItemSelectedEvent> hierarchyItemSelectedEvent) {
        this.view = view;
        this.treeHierarchyInternalItemPresenterProvider = treeHierarchyInternalItemPresenterProvider;
        this.treeHierarchyLeafItemPresenterProvider = treeHierarchyLeafItemPresenterProvider;
        this.hierarchyItemSelectedEvent = hierarchyItemSelectedEvent;
    }

    @Override
    public <T> void init(final PreferenceHierarchyElement<T> preference,
                         final int level,
                         boolean tryToSelectChild) {
        hierarchyElement = preference;
        this.level = level;

        hierarchyItems = new ArrayList<>();

        for (PreferenceHierarchyElement<?> child : preference.getChildren()) {
            HierarchyItemPresenter hierarchyItem;

            if (child.hasChildren()) {
                hierarchyItem = treeHierarchyInternalItemPresenterProvider.get();
            } else {
                hierarchyItem = treeHierarchyLeafItemPresenterProvider.get();
            }

            hierarchyItem.init(child,
                               level + 1,
                               tryToSelectChild && !child.isSelectable());
            if (child.isSelectable()) {
                hierarchyItem.fireSelect();
                tryToSelectChild = false;
            }

            hierarchyItems.add(hierarchyItem);
        }

        view.init(this);
    }

    @Override
    public void fireSelect() {
        view.select();
    }

    public void select() {
        if (hierarchyElement.isSelectable()) {
            final HierarchyItemSelectedEvent event = new HierarchyItemSelectedEvent(hierarchyElement);
            hierarchyItemSelectedEvent.fire(event);
            view.selectElement();
        }
    }

    public void hierarchyItemSelectedEvent(@Observes HierarchyItemSelectedEvent hierarchyItemSelectedEvent) {
        if (!hierarchyElement.getId().equals(hierarchyItemSelectedEvent.getHierarchyElement().getId())) {
            view.deselect();
        }
    }

    @Override
    public View getView() {
        return view;
    }

    public PreferenceHierarchyElement<?> getHierarchyElement() {
        return hierarchyElement;
    }

    public List<HierarchyItemPresenter> getHierarchyItems() {
        return hierarchyItems;
    }

    public int getLevel() {
        return level;
    }

    public interface View extends HierarchyItemView,
                                  UberElement<TreeHierarchyInternalItemPresenter> {

        void select();

        void selectElement();
    }
}
