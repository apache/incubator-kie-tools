/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.docks.navigator.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ROOT;

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
        setup(items);
    }

    public void addOrUpdateItem(final DecisionNavigatorItem item) {

        if (!isChanged(item) || !hasParent(item)) {
            return;
        }

        if (view.hasItem(item)) {
            updateItem(item);
        } else {
            addItem(item);
        }
    }

    public void updateItem(final DecisionNavigatorItem item) {
        index(item);
        view.update(item, nextItem(item));
    }

    public void addItem(final DecisionNavigatorItem item) {
        index(item);
        view.addItem(item, nextItem(item));
    }

    public void remove(final DecisionNavigatorItem item) {
        unIndex(item);
        view.remove(item);
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

    DecisionNavigatorItem nextItem(final DecisionNavigatorItem item) {

        final DecisionNavigatorItem parent = getIndexedItems().get(item.getParentUUID());
        final TreeSet<DecisionNavigatorItem> children = parent.getChildren();

        return children.higher(item);
    }

    void index(final Collection<DecisionNavigatorItem> items) {
        items.forEach(this::index);
    }

    void index(final DecisionNavigatorItem item) {

        parent(item).ifPresent(parent -> parent.addChild(item));

        getIndexedItems().put(item.getUUID(), item);

        index(item.getChildren());
    }

    void unIndex(final DecisionNavigatorItem item) {

        parent(item).ifPresent(parent -> parent.removeChild(item));

        getIndexedItems().remove(item.getUUID());
    }

    boolean isChanged(final DecisionNavigatorItem item) {
        final DecisionNavigatorItem currentItem = getIndexedItems().get(item.getUUID());
        return !item.equals(currentItem);
    }

    boolean hasParent(final DecisionNavigatorItem item) {
        return parent(item).isPresent();
    }

    Optional<DecisionNavigatorItem> parent(final DecisionNavigatorItem item) {
        final DecisionNavigatorItem parent = getIndexedItems().get(item.getParentUUID());
        return Optional.ofNullable(parent);
    }

    DecisionNavigatorItem findRoot() {
        return getIndexedItems()
                .values()
                .stream()
                .filter(i -> i.getType() == ROOT)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    Map<String, DecisionNavigatorItem> getIndexedItems() {
        return indexedItems;
    }

    private void setup(final List<DecisionNavigatorItem> items) {
        index(items);
        view.clean();
        view.setup(items);
    }

    public interface View extends UberElemental<DecisionNavigatorTreePresenter> {

        void clean();

        void setup(final List<DecisionNavigatorItem> items);

        void addItem(final DecisionNavigatorItem item,
                     final DecisionNavigatorItem nextItem);

        void update(final DecisionNavigatorItem item,
                    final DecisionNavigatorItem nextItem);

        boolean hasItem(final DecisionNavigatorItem item);

        void remove(final DecisionNavigatorItem item);

        void select(final String uuid);

        void deselect();
    }
}
