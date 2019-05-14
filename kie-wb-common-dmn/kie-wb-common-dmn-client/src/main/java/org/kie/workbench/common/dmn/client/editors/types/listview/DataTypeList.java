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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.uberfire.client.mvp.UberElemental;

@ApplicationScoped
public class DataTypeList {

    private final View view;

    private final ManagedInstance<DataTypeListItem> listItems;

    private final DataTypeManager dataTypeManager;

    private final DataTypeSearchBar searchBar;

    private final DataTypeStackHash dataTypeStackHash;

    private Consumer<DataTypeListItem> onDataTypeListItemUpdate = (e) -> { /* Nothing. */ };

    private List<DataTypeListItem> items;

    private DataTypeListItem currentEditingItem;

    @Inject
    public DataTypeList(final DataTypeList.View view,
                        final ManagedInstance<DataTypeListItem> listItems,
                        final DataTypeManager dataTypeManager,
                        final DataTypeSearchBar searchBar,
                        final DataTypeStackHash dataTypeStackHash) {
        this.view = view;
        this.listItems = listItems;
        this.dataTypeManager = dataTypeManager;
        this.searchBar = searchBar;
        this.dataTypeStackHash = dataTypeStackHash;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setupItems(final List<DataType> dataTypes) {
        setListItems(makeDataTypeListItems(dataTypes));
        setupViewItems();
        collapseItemsInTheFirstLevel();
    }

    List<DataTypeListItem> makeDataTypeListItems(final List<DataType> dataTypes) {
        final List<DataTypeListItem> listItems = new ArrayList<>();
        dataTypes.forEach(dt -> listItems.addAll(makeTreeListItems(dt, 1)));
        return listItems;
    }

    void refreshSubItemsFromListItem(final DataTypeListItem listItem,
                                     final List<DataType> subDataTypes) {

        final DataType dataType = listItem.getDataType();
        final int level = listItem.getLevel();
        final List<DataTypeListItem> listItems = new ArrayList<>();

        for (final DataType subDataType : subDataTypes) {
            listItems.addAll(makeTreeListItems(subDataType, level + 1));
        }

        cleanAndUnIndex(dataType);
        addNewSubItems(dataType, listItems);
        listItems.forEach(this::reIndexDataTypes);

        getItems().addAll(listItems);
    }

    private void reIndexDataTypes(final DataTypeListItem listItem) {
        dataTypeManager.from(listItem.getDataType()).withIndexedItemDefinition();
    }

    private void addNewSubItems(final DataType dataType, final List<DataTypeListItem> gridItems) {
        view.addSubItems(dataType, gridItems);
    }

    private void cleanAndUnIndex(final DataType dataType) {
        view.cleanSubTypes(dataType);
    }

    List<DataTypeListItem> makeTreeListItems(final DataType dataType,
                                             final int level) {

        final DataTypeListItem listItem = makeListItem();
        final List<DataType> subDataTypes = dataType.getSubDataTypes();
        final List<DataTypeListItem> gridItems = new ArrayList<>();

        listItem.setupDataType(dataType, level);
        gridItems.add(listItem);

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeListItems(subDataType, level + 1));
        }

        return gridItems;
    }

    DataTypeListItem makeListItem() {
        final DataTypeListItem listItem = listItems.get();
        listItem.init(this);
        return listItem;
    }

    void removeItem(final DataType dataType) {
        removeItem(dataType.getUUID());
        view.removeItem(dataType);
    }

    void removeItem(final String uuid) {
        getItems().removeIf(listItem -> Objects.equals(uuid, listItem.getDataType().getUUID()));
    }

    void refreshItemsByUpdatedDataTypes(final List<DataType> updateDataTypes) {
        for (final DataType dataType : updateDataTypes) {
            findItem(dataType).ifPresent(listItem -> {
                listItem.refresh();
                refreshSubItemsFromListItem(listItem, dataType.getSubDataTypes());
            });
        }
        searchBar.refresh();
    }

    Optional<DataTypeListItem> findItem(final DataType dataType) {
        return getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), dataType.getUUID()))
                .findFirst();
    }

    void setupViewItems() {
        view.setupListItems(getItems());
        view.showReadOnlyMessage(hasReadOnlyDataTypes());
    }

    private boolean hasReadOnlyDataTypes() {
        return getItems()
                .stream()
                .anyMatch(DataTypeListItem::isReadOnly);
    }

    public List<DataTypeListItem> getItems() {
        return items;
    }

    void setListItems(final List<DataTypeListItem> items) {
        this.items = items;
    }

    void collapseItemsInTheFirstLevel() {
        getItems()
                .stream()
                .filter(typeListItem -> typeListItem.getLevel() == 1)
                .forEach(DataTypeListItem::collapse);
    }

    void addDataType() {

        final DataType dataType = dataTypeManager.fromNew().get();
        final DataTypeListItem listItem = makeListItem(dataType);

        dataType.create();

        searchBar.reset();
        view.addSubItem(listItem);

        listItem.enableEditMode();
        fireOnDataTypeListItemUpdateCallback(listItem);
    }

    void insertBelow(final DataType dataType,
                     final DataType reference) {
        view.insertBelow(makeListItem(dataType), reference);
    }

    void insertAbove(final DataType dataType,
                     final DataType reference) {
        view.insertAbove(makeListItem(dataType), reference);
    }

    public void showNoDataTypesFound() {
        view.showNoDataTypesFound();
    }

    public void showListItems() {
        view.showOrHideNoCustomItemsMessage();
    }

    public HTMLElement getListItemsElement() {
        return view.getListItems();
    }

    DataTypeListItem makeListItem(final DataType dataType) {

        final DataTypeListItem listItem = makeListItem();

        listItem.setupDataType(dataType, 1);
        getItems().add(listItem);

        return listItem;
    }

    void expandAll() {
        if (!getSearchBar().isEnabled()) {
            getItems().forEach(DataTypeListItem::expand);
        }
    }

    void collapseAll() {
        if (!getSearchBar().isEnabled()) {
            getItems().forEach(DataTypeListItem::collapse);
        }
    }

    DataTypeSearchBar getSearchBar() {
        return searchBar;
    }

    public void enableEditMode(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(DataTypeListItem::enableEditMode);
    }

    public void registerDataTypeListItemUpdateCallback(final Consumer<DataTypeListItem> onDataTypeListItemUpdate) {
        this.onDataTypeListItemUpdate = onDataTypeListItemUpdate;
    }

    void insertNestedField(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(DataTypeListItem::insertNestedField);
    }

    void fireOnDataTypeListItemUpdateCallback(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(this::fireOnDataTypeListItemUpdateCallback);
    }

    private void fireOnDataTypeListItemUpdateCallback(final DataTypeListItem listItem) {
        onDataTypeListItemUpdate.accept(listItem);
    }

    Optional<DataTypeListItem> findItemByDataTypeHash(final String dataTypeHash) {
        return getItems()
                .stream()
                .filter(item -> Objects.equals(calculateHash(item.getDataType()), dataTypeHash))
                .findFirst();
    }

    String calculateParentHash(final DataType dataType) {
        return dataTypeStackHash.calculateParentHash(dataType);
    }

    String calculateHash(final DataType dataType) {
        return dataTypeStackHash.calculateHash(dataType);
    }

    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent event) {
        if (event.isEditModeEnabled()) {
            if (getCurrentEditingItem() != null && getItems().contains(getCurrentEditingItem())) {
                getCurrentEditingItem().disableEditMode();
            }
            setCurrentEditingItem(event.getItem());
        } else if (Objects.equals(event.getItem(), getCurrentEditingItem())) {
            setCurrentEditingItem(null);
        }
    }

    DataTypeListItem getCurrentEditingItem() {
        return currentEditingItem;
    }

    void setCurrentEditingItem(final DataTypeListItem currentEditingItem) {
        this.currentEditingItem = currentEditingItem;
    }

    public interface View extends UberElemental<DataTypeList>,
                                  IsElement {

        void setupListItems(final List<DataTypeListItem> treeGridItems);

        void showOrHideNoCustomItemsMessage();

        void addSubItems(final DataType dataType,
                         final List<DataTypeListItem> treeGridItems);

        void addSubItem(final DataTypeListItem listItem);

        void removeItem(final DataType dataType);

        void cleanSubTypes(DataType dataType);

        void insertBelow(final DataTypeListItem listItem,
                         final DataType reference);

        void insertAbove(final DataTypeListItem listItem,
                         final DataType reference);

        void showNoDataTypesFound();

        HTMLDivElement getListItems();

        void showReadOnlyMessage(boolean show);
    }
}
