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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeSearchBarView_Search;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Templated
@ApplicationScoped
public class DataTypeSearchBarView implements DataTypeSearchBar.View {

    static final String ENABLED_SEARCH = "kie-search-engine-enabled";

    @DataField("search-bar")
    private final HTMLInputElement searchBar;

    @DataField("search-icon")
    private final HTMLElement searchIcon;

    @DataField("close-search")
    private final HTMLButtonElement closeSearch;

    private final TranslationService translationService;

    private DataTypeSearchBar presenter;

    @Inject
    public DataTypeSearchBarView(final HTMLInputElement searchBar,
                                 final @Named("span") HTMLElement searchIcon,
                                 final HTMLButtonElement closeSearch,
                                 final TranslationService translationService) {
        this.searchBar = searchBar;
        this.searchIcon = searchIcon;
        this.closeSearch = closeSearch;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setupSearchBar() {
        searchBar.placeholder = translationService.format(DataTypeSearchBarView_Search);
    }

    @Override
    public void init(final DataTypeSearchBar presenter) {
        this.presenter = presenter;
    }

    @EventHandler("close-search")
    public void onSearchBarCloseButton(final ClickEvent e) {
        presenter.reset();
    }

    @EventHandler("search-bar")
    public void onSearchBarKeyUpEvent(final KeyUpEvent event) {
        if (isEscape(event)) {
            presenter.reset();
        } else {
            search();
        }
    }

    @EventHandler("search-bar")
    public void onSearchBarKeyDownEvent(final KeyDownEvent e) {
        refreshSearchBarState();
    }

    @EventHandler("search-bar")
    public void onSearchBarChangeEvent(final ChangeEvent e) {
        refreshSearchBarState();
    }

    @Override
    public void resetSearchBar() {
        searchBar.value = "";

        refreshSearchBarState();
        disableSearch();
    }

    @Override
    public void showSearchResults(final List<DataType> results) {

        final AtomicInteger position = new AtomicInteger(0);
        final List<DataTypeListItem> listItems = presenter.getDataTypeListItemsSortedByPositionY();

        expandListItems(listItems);

        final List<DataTypeListItem> grouped = groupElementsWithItsParents(listItems);
        for (final DataTypeListItem listItem : grouped) {

            final HTMLElement element = listItem.getDragAndDropElement();

            if (results.contains(listItem.getDataType())) {
                showElementAt(element, position);
            } else {
                hideElement(element);
            }
        }

        refreshItemsPosition();
        enableSearch();
        refreshDragAreaSize();
    }

    void expandListItems(final List<DataTypeListItem> listItems) {
        for (final DataTypeListItem listItem : listItems) {
            listItem.expand();
        }
    }

    List<DataTypeListItem> groupElementsWithItsParents(final List<DataTypeListItem> allElements) {

        final List<DataTypeListItem> groupedElements = getGroupedElementsList();

        for (final DataTypeListItem item : allElements) {
            groupElementWithItsParent(groupedElements, allElements, item);
        }

        return groupedElements;
    }

    List<DataTypeListItem> getGroupedElementsList() {
        return new ArrayList<>();
    }

    void groupElementWithItsParent(final List<DataTypeListItem> groupedElements,
                                   final List<DataTypeListItem> allElements,
                                   final DataTypeListItem item) {

        if (groupedElements.contains(item)) {
            return;
        }

        final String parentElementId = item.getDragAndDropElement().getAttribute(PARENT_UUID_ATTR);
        if (!StringUtils.isEmpty(parentElementId)) {

            final Optional<DataTypeListItem> parentElement = allElements.stream()
                    .filter(element -> Objects.equals(element.getDragAndDropElement().getAttribute(UUID_ATTR), parentElementId))
                    .findFirst();
            parentElement.ifPresent(parent -> {
                if (!isParentElementOnList(groupedElements, parentElementId)) {
                    groupElementWithItsParent(groupedElements, allElements, parent);
                    groupedElements.add(item);
                } else {

                    final int index = getIndexOfParentOrLastElementInGroup(groupedElements, parent) + 1;
                    if (index == groupedElements.size()) {
                        groupedElements.add(item);
                    } else {
                        groupedElements.add(index, item);
                    }
                }
            });
        } else {
            groupedElements.add(item);
        }
    }

    int getIndexOfParentOrLastElementInGroup(final List<DataTypeListItem> groupedElements,
                                             final DataTypeListItem parent) {
        final int parentIndex = groupedElements.indexOf(parent);
        final String parentId = parent.getDragAndDropElement().getAttribute(UUID_ATTR);
        int index = parentIndex;
        for (int i = parentIndex; i < groupedElements.size(); i++) {
            if (Objects.equals(groupedElements.get(i).getDragAndDropElement().getAttribute(PARENT_UUID_ATTR), parentId)) {
                index++;
            }
        }
        return index;
    }

    boolean isParentElementOnList(final List<DataTypeListItem> groupedElements, final String parentId) {
        return groupedElements.stream()
                .anyMatch(element -> Objects.equals(element.getDragAndDropElement().getAttribute(UUID_ATTR), parentId));
    }

    private void refreshDragAreaSize() {
        presenter.getDNDListComponent().refreshDragAreaSize();
    }

    public void refreshItemsPosition() {
        presenter.getDNDListComponent().refreshItemsPosition();
    }

    private void hideElement(final HTMLElement element) {
        HiddenHelper.hide(element);
        presenter.getDNDListComponent().setPositionY(element, -1);
    }

    private void showElementAt(final HTMLElement element,
                               final AtomicInteger position) {
        HiddenHelper.show(element);
        presenter.getDNDListComponent().setPositionY(element, position.getAndIncrement());
    }

    void enableSearch() {
        getResultsContainer().classList.add(ENABLED_SEARCH);
    }

    void disableSearch() {
        getResultsContainer().classList.remove(ENABLED_SEARCH);
    }

    void search() {

        final String currentValue = searchBar.value;

        setTimeout((type) -> {
            if (Objects.equals(searchBar.value, currentValue)) {
                presenter.search(currentValue);
            }
        }, 500d);
    }

    void refreshSearchBarState() {
        final boolean isActive = !isEmpty(searchBar.value);
        searchBarActive(isActive);
    }

    private void searchBarActive(final boolean isActive) {
        if (isActive) {
            hide(searchIcon);
            show(closeSearch);
        } else {
            show(searchIcon);
            hide(closeSearch);
        }
    }

    void setTimeout(final DomGlobal.SetTimeoutCallbackFn callback,
                    final double delay) {
        DomGlobal.setTimeout(callback, delay);
    }

    private boolean isEscape(final KeyUpEvent event) {
        return Objects.equals(event.getNativeKeyCode(), KeyCodes.KEY_ESCAPE);
    }

    private Element getResultsContainer() {
        return presenter.getResultsContainer();
    }
}
