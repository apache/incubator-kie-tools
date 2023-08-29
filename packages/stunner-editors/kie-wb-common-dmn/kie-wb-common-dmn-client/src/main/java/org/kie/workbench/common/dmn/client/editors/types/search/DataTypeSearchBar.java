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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@ApplicationScoped
public class DataTypeSearchBar {

    private final View view;

    private final DataTypeSearchEngine searchEngine;

    private final DataTypeList dataTypeList;

    private String currentSearch;

    private final Map<String, Integer> dataTypeListPositionsStore = new HashMap<>();

    private final Map<String, Boolean> dataTypeListCollapsedStatusStore = new HashMap<>();

    @Inject
    public DataTypeSearchBar(final View view,
                             final DataTypeSearchEngine searchEngine,
                             final DataTypeList dataTypeList) {
        this.view = view;
        this.searchEngine = searchEngine;
        this.dataTypeList = dataTypeList;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void refresh() {
        search(getCurrentSearch());
    }

    public void reset() {
        setCurrentSearch("");
        dataTypeList.showListItems();
        view.resetSearchBar();
        restoreDataTypeListPositions();
    }

    void search(final String keyword) {

        final List<DataType> results = searchEngine.search(keyword);

        storeDataTypeListPositions();
        showEmptyView(results.isEmpty());
        setCurrentSearch(keyword);

        if (isEmpty(keyword)) {
            reset();
        } else {
            view.showSearchResults(results);
        }
    }

    HTMLElement getResultsContainer() {
        return dataTypeList.getElement();
    }

    String getCurrentSearch() {
        return currentSearch;
    }

    void setCurrentSearch(final String currentSearch) {
        this.currentSearch = currentSearch;
    }

    DNDListComponent getDNDListComponent() {
        return dataTypeList.getDNDListComponent();
    }

    void restoreDataTypeListPositions() {

        if (!hasDataTypeListPositionsStored()) {
            return;
        }

        getDataTypeListItems().forEach(item -> {

            final HTMLElement element = item.getDragAndDropElement();
            final String uuid = item.getDataType().getUUID();
            final Integer positionY = getDataTypeListPositionsStore().get(uuid);

            getDNDListComponent().setPositionY(element, positionY);
            if (positionY > -1) {
                HiddenHelper.show(element);
            } else {
                HiddenHelper.hide(element);
            }

            final boolean collapsed = getDataTypeListCollapsedStatusStore().get(uuid);
            if (collapsed) {
                item.collapse();
            }
        });

        getDNDListComponent().refreshItemsPosition();
        getDataTypeListPositionsStore().clear();
        getDataTypeListCollapsedStatusStore().clear();
    }

    void storeDataTypeListPositions() {

        if (hasDataTypeListPositionsStored()) {
            return;
        }

        getDataTypeListItems().forEach(listItem -> {

            final String dataTypeUUID = listItem.getDataType().getUUID();
            final Integer dataTypeYPosition = getDNDListComponent().getPositionY(listItem.getDragAndDropElement());
            getDataTypeListCollapsedStatusStore().put(dataTypeUUID, listItem.isCollapsed());
            getDataTypeListPositionsStore().put(dataTypeUUID, dataTypeYPosition);
        });
    }

    Map<String, Integer> getDataTypeListPositionsStore() {
        return dataTypeListPositionsStore;
    }

    Map<String, Boolean> getDataTypeListCollapsedStatusStore() {
        return dataTypeListCollapsedStatusStore;
    }

    private boolean hasDataTypeListPositionsStored() {
        return !getDataTypeListPositionsStore().isEmpty();
    }

    private List<DataTypeListItem> getDataTypeListItems() {
        return dataTypeList.getItems();
    }

    private void showEmptyView(final boolean show) {
        if (show) {
            dataTypeList.showNoDataTypesFound();
        } else {
            dataTypeList.showListItems();
        }
    }

    List<DataTypeListItem> getDataTypeListItemsSortedByPositionY() {
        return getDataTypeListItems()
                .stream()
                .sorted(Comparator.comparing(item -> getDNDListComponent().getPositionY(item.getDragAndDropElement())))
                .collect(Collectors.toList());
    }

    public boolean isEnabled() {
        return !isEmpty(getCurrentSearch());
    }

    public interface View extends UberElemental<DataTypeSearchBar>,
                                  IsElement {

        void showSearchResults(final List<DataType> results);

        void resetSearchBar();
    }
}
