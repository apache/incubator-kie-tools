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

package org.uberfire.ext.widgets.common.client.dropdown;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class LiveSearchDropDown<TYPE> implements IsWidget {

    private View<TYPE> view;
    private int maxItems = 10;
    private LiveSearchService<TYPE> searchService = null;
    private boolean changeCallbackEnabled = true;
    private boolean searchEnabled = true;
    private boolean clearSelectionEnabled;
    private boolean searchCacheEnabled = true;
    private Map<String, LiveSearchResults<TYPE>> searchCache = new HashMap<>();
    private LiveSearchSelectionHandler selectionHandler;

    private ManagedInstance<LiveSearchSelectorItem<TYPE>> liveSearchSelectorItems;
    private String lastSearch = null;
    private String searchHint = null;
    private String selectorHint = null;
    private String notFoundMessage = null;
    private Command onChange;

    private Command onAddItemPressed;

    @Inject
    public LiveSearchDropDown(View view,
                              ManagedInstance<LiveSearchSelectorItem<TYPE>> liveSearchSelectorItems) {
        this.view = view;
        this.liveSearchSelectorItems = liveSearchSelectorItems;

        this.view.init(this);

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

    public void setClearSelectionEnabled(boolean clearSelectionEnabled) {
        this.clearSelectionEnabled = clearSelectionEnabled;
        view.setClearSelectionEnabled(clearSelectionEnabled);
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

    public void init(LiveSearchService<TYPE> searchService,
                     LiveSearchSelectionHandler<TYPE> selectionHandler) {
        this.searchService = searchService;
        this.selectionHandler = selectionHandler;

        selectionHandler.setLiveSearchSelectionCallback(this::onItemSelected);

        view.setClearSelectionMessage(selectionHandler.isMultipleSelection());

        if (searchService instanceof EntryCreationLiveSearchService) {

            view.setNewInstanceEnabled(true);

            EntryCreationLiveSearchService<TYPE, ?> creationService = (EntryCreationLiveSearchService<TYPE, ?>) searchService;

            EntryCreationEditor<TYPE> editor = creationService.getEditor();

            if (creationService.getEditor() instanceof InlineCreationEditor) {
                InlineCreationEditor<TYPE> inlineEditor = (InlineCreationEditor<TYPE>) editor;
                inlineEditor.init(this::addNewItem, view::restoreFooter);
                onAddItemPressed = () -> view.showNewItemEditor(inlineEditor);
            } else if (creationService.getEditor() instanceof ModalCreationEditor) {
                ModalCreationEditor<TYPE> modalEditor = (ModalCreationEditor<TYPE>) editor;
                modalEditor.init(this::addNewItem, () -> {});
                onAddItemPressed = () -> modalEditor.show();
            }
        } else {
            view.setNewInstanceEnabled(false);
        }
    }

    protected void addNewItem(LiveSearchEntry<TYPE> entry) {

        LiveSearchSelectorItem<TYPE> itemInstance = liveSearchSelectorItems.get();

        itemInstance.init(entry.getKey(), entry.getValue());

        selectionHandler.selectItem(itemInstance);

        searchCache.clear();

        String pattern = lastSearch;

        lastSearch = null;

        search(pattern);

        view.restoreFooter();
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
                showResults(getFromSearchCache(lastSearch));
            } else {
                doSearch(pattern);
            }
        }
    }

    public void setSelectedItem(final TYPE key) {
        searchService.searchEntry(key,
                                  results -> {
                                      if(results.size() == 1) {
                                          LiveSearchEntry<TYPE> entry = results.get(0);
                                          changeCallbackEnabled = false;
                                          LiveSearchSelectorItem<TYPE> item = getSelectorItemForEntry(entry);
                                          selectionHandler.selectItem(item);
                                          item.select();
                                          view.clearItems();
                                          lastSearch = null;
                                          changeCallbackEnabled = true;
                                      }
                                  });
    }

    protected void doSearch(String pattern) {
        view.searchInProgress(searchHint);
        searchService.search(lastSearch,
                             maxItems,
                             results -> {
                                 addToSearchCache(pattern,
                                                  results);
                                 showResults(results);
                                 view.searchFinished();
                             });
    }

    protected LiveSearchResults getFromSearchCache(String pattern) {
        return searchCache.get(pattern);
    }

    protected void addToSearchCache(String pattern,
                                    LiveSearchResults searchResults) {
        searchCache.put(pattern,
                        searchResults);
    }

    public void showResults(LiveSearchResults<TYPE> results) {
        view.clearItems();
        if (results.isEmpty()) {
            view.noItems(notFoundMessage);
        } else {
            results.forEach(LiveSearchDropDown.this::getSelectorItemForEntry);
        }
    }

    public LiveSearchSelectorItem<TYPE> getSelectorItemForEntry(LiveSearchEntry<TYPE> entry) {
        LiveSearchSelectorItem<TYPE> item = liveSearchSelectorItems.get();

        item.init(entry.getKey(),
                  entry.getValue());

        selectionHandler.registerItem(item);

        view.addItem(item);

        return item;
    }

    void onItemsShown() {
        Scheduler.get().scheduleDeferred(() -> {
            search(lastSearch);
            view.restoreFooter();
        });
    }

    public void setOnChange(Command onChange) {
        this.onChange = onChange;
    }

    public void setEnabled(boolean enabled) {
        view.setEnabled(enabled);
    }

    void onItemSelected() {
        String message = selectionHandler.getDropDownMenuHeader();

        if (message == null) {
            message = selectorHint;
        }

        view.setDropDownText(message);

        if (onChange != null & changeCallbackEnabled) {
            onChange.execute();
        }
    }

    public void clearSelection() {
        selectionHandler.clearSelection();

        if (onChange != null & changeCallbackEnabled) {
            onChange.execute();
        }
    }

    public void showNewItem() {
        if (onAddItemPressed != null) {
            onAddItemPressed.execute();
        }
    }

    public interface View<TYPE> extends UberView<LiveSearchDropDown<TYPE>> {

        void setNewInstanceEnabled(boolean enabled);

        void showNewItemEditor(InlineCreationEditor editor);

        void restoreFooter();

        void clearItems();

        void noItems(String msg);

        void addItem(LiveSearchSelectorItem<TYPE> item);

        void setSelectedValue(String selectedItem);

        void setSearchEnabled(boolean enabled);

        void setClearSelectionEnabled(boolean clearSelectionEnabled);

        void setSearchHint(String text);

        void clearSearch();

        void searchInProgress(String msg);

        void searchFinished();

        void setDropDownText(String text);

        void setWidth(int minWidth);

        void setMaxHeight(int maxHeight);

        void setNewEntryI18nMessage(String defaultNewInstanceI18nMessage);

        String getDefaultNewEntryI18nMessage();

        String getDefaultSearchHintI18nMessage();

        String getDefaultSelectorHintI18nMessage();

        String getDefaultNotFoundI18nMessage();

        // For single selection
        String getDefaultResetSelectionI18nMessage();

        // For multiple selection
        String getDefaultClearSelectionI18nMessage();

        void setEnabled(boolean enabled);

        void setClearSelectionMessage(boolean multipleSelection);
    }

    @PreDestroy
    public void destroy() {
        liveSearchSelectorItems.destroyAll();
    }
}
