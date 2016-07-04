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

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class LiveSearchDropDown implements IsWidget {

    public interface View extends UberView<LiveSearchDropDown> {

        void clearItems();

        void noItems(String msg);

        void addItem(String item);

        void setSelectedItem(String selectedItem);

        void setSearchEnabled(boolean enabled);

        void setSearchHint(String text);

        void clearSearch();

        void searchInProgress(String msg);

        void searchFinished();

        void setDropDownText(String text);

        void setWidth(int minWidth);

        void setMaxHeight(int maxHeight);

        String getDefaultSearchHintI18nMessage();

        String getDefaultSelectorHintI18nMessage();

        String getDefaultNotFoundI18nMessage();
    }

    View view;
    int maxItems = 10;
    LiveSearchService searchService = null;
    boolean searchEnabled = true;
    boolean searchCacheEnabled = true;
    Map<String,List<String>> searchCache = new HashMap<>();
    String selectedItem = null;
    String lastSearch = null;
    String searchHint = null;
    String selectorHint = null;
    String notFoundMessage = null;
    Command onChange;

    @Inject
    public LiveSearchDropDown(View view) {
        this.view = view;
        view.init(this);

        searchHint = view.getDefaultSearchHintI18nMessage();
        selectorHint = view.getDefaultSelectorHintI18nMessage();
        notFoundMessage = view.getDefaultNotFoundI18nMessage();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
        view.setSearchEnabled(searchEnabled);
    }

    public void setSelectorHint(String text) {
        selectorHint = text;
        view.setDropDownText(text);
    }

    public void setSearchHint(String text) {
        searchHint = text;
        view.setSearchHint(text);
    }

    public void setNotFoundMessage(String noItemsMessage) {
        this.notFoundMessage = noItemsMessage;
    }

    public void setOnChange(Command onChange) {
        this.onChange = onChange;
    }

    public void setSearchService(LiveSearchService searchService) {
        this.searchService = searchService;
    }

    public boolean isSearchCacheEnabled() {
        return searchCacheEnabled;
    }

    public void setSearchCacheEnabled(boolean searchCacheEnabled) {
        this.searchCacheEnabled = searchCacheEnabled;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void setWidth(int minWidth) {
        view.setWidth(minWidth);
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
        view.setSelectedItem(selectedItem);
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void clear() {
        lastSearch = null;
        view.clearSearch();
        view.clearItems();
        view.setDropDownText(selectorHint);
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void search(String pattern) {
        if (lastSearch == null || !lastSearch.equals(pattern)) {
            lastSearch = pattern != null ? pattern : "";

            if (searchCacheEnabled && searchCache.containsKey(lastSearch)) {
                showItemList(getFromSearchCache(lastSearch));
            }
            else {
                doSearch(pattern);
            }
        }
    }

    protected void doSearch(String pattern) {
        view.searchInProgress(searchHint);
        searchService.search(lastSearch, maxItems, itemList -> {
            addToSearchCache(pattern, itemList);
            showItemList(itemList);
            view.searchFinished();
        });
    }

    protected List<String> getFromSearchCache(String pattern) {
        return searchCache.get(pattern);
    }

    protected void addToSearchCache(String pattern, List<String> itemList) {
        searchCache.put(pattern, itemList);
    }

    public void showItemList(List<String> itemList) {
        view.clearItems();
        if (itemList.isEmpty()) {
            view.noItems(notFoundMessage);
        }
        for (String i : itemList) {
            view.addItem(i);
        }
    }

    // View callbacks

    void onItemsShown() {
        Scheduler.get().scheduleDeferred(() -> {
            search(lastSearch);
        });
    }

    void onItemSelected(String item) {
        selectedItem = item;
        view.setDropDownText(item);
        if (onChange != null) {
            onChange.execute();
        }
    }
}
