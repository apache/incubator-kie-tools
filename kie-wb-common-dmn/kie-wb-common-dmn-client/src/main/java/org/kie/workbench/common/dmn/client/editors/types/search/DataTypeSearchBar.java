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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@ApplicationScoped
public class DataTypeSearchBar {

    private final View view;

    private final DataTypeSearchEngine searchEngine;

    private final DataTypeList dataTypeList;

    private String currentSearch;

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
    }

    void search(final String keyword) {

        final List<DataType> results = searchEngine.search(keyword);

        showEmptyView(results.isEmpty());
        setCurrentSearch(keyword);

        if (isEmpty(keyword)) {
            reset();
        } else {
            view.showSearchResults(results);
        }
    }

    HTMLElement getResultsContainer() {
        return dataTypeList.getListItemsElement();
    }

    String getCurrentSearch() {
        return currentSearch;
    }

    void setCurrentSearch(final String currentSearch) {
        this.currentSearch = currentSearch;
    }

    private void showEmptyView(final boolean show) {
        if (show) {
            dataTypeList.showNoDataTypesFound();
        } else {
            dataTypeList.showListItems();
        }
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
