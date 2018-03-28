/**
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
package org.dashbuilder.common.client.editor.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

@Dependent
public class DropDownEditor implements IsWidget, LeafAttributeEditor<String> {

    public interface View extends UberView<DropDownEditor> {

        View addHelpContent(final String title, final String content, final Placement placement);

        View setDropDown(LiveSearchDropDown dropDown);

        View showError(final SafeHtml message);

        View clearError();
    }

    public class Entry {
        private String value;
        private String hint;

        public Entry(String value, String hint) {
            this.value = value;
            this.hint = hint;
        }

        public String getValue() {
            return value;
        }

        public String getHint() {
            return hint;
        }
    }

    public View view;
    LiveSearchDropDown<String> dropDown;
    LiveSearchService<String> searchService = new LiveSearchService<String>() {
        @Override
        public void search(String pattern, int maxResults, LiveSearchCallback<String> callback) {
            getDropDownEntries(pattern, maxResults, callback);
        }

        @Override
        public void searchEntry(String key, LiveSearchCallback<String> callback) {

        }
    };

    SingleLiveSearchSelectionHandler<String> selectionHandler = new SingleLiveSearchSelectionHandler<>();

    Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent;
    Collection<Entry> entries = new ArrayList<>();
    String selectorHint;
    String value;

    @Inject
    public DropDownEditor(final View view,
                          final LiveSearchDropDown dropDown,
                          final Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent) {
        this.view = view;
        this.dropDown = dropDown;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setDropDown(dropDown);
        dropDown.setClearSelectionEnabled(false);
        dropDown.setSearchEnabled(false);
        dropDown.init(searchService, selectionHandler);
        dropDown.setOnChange(this::onEntrySelected);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void getDropDownEntries(String pattern, int maxResults, LiveSearchCallback<String> callback) {
        final LiveSearchResults results = new LiveSearchResults();
        entries.stream()
                .filter(e -> e.getHint().contains(pattern))
                .forEach(e -> results.add(e.getValue(), e.getHint()));
        callback.afterSearch(results);
    }

    public void getExactEntry(String key, LiveSearchCallback<String> callback) {
        final LiveSearchResults results = new LiveSearchResults(1);
        entries.stream()
                .filter(e -> e.getValue().equals(key))
                .findAny()
                .ifPresent(e -> results.add(e.getValue(), e.getHint()));
        callback.afterSearch(results);
    }

    public void onEntrySelected() {
        String oldValue = value;
        value = getSelectedValue();
        valueChangeEvent.fire(new ValueChangeEvent<>(this, oldValue, value));
    }

    public String getSelectedValue() {
        String hint = selectionHandler.getSelectedValue();
        Entry entry = getEntryByHint(hint);
        return entry.getValue();
    }

    private Entry getEntryByHint(String hint) {
        for (Entry entry : entries) {
            if (entry.getHint().equals(hint)) {
                return entry;
            }
        }
        return null;
    }

    private Entry getEntryByValue(String value) {
        for (Entry entry : entries) {
            if (entry.getValue().equals(value)) {
                return entry;
            }
        }
        return null;
    }

    public void setSelectHint(String hint) {
        selectorHint = hint;
        dropDown.setSelectorHint(hint);
    }

    public Entry newEntry(final String value, String hint) {
        return new Entry(value, hint);
    }

    public void setEntries(final Collection<Entry> entries) {
        this.entries.clear();
        if (entries != null) {
            for (Entry entry : entries) {
                this.entries.add(entry);
                if (entry.getValue().equals(value)) {
                    this.dropDown.setSelectedItem(entry.getValue());
                }
            }
        }
   }

    public void addHelpContent(final String title, final String content, final Placement placement) {
        view.addHelpContent(title, content, placement);
    }

    public void clear() {
        this.entries.clear();
        this.value = null;
        this.dropDown.clear();
    }

    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public void showErrors(final List<EditorError> errors) {
        StringBuilder sb = new StringBuilder();
        for (EditorError error : errors) {
            if (error.getEditor().equals(this)) {
                sb.append("\n").append(error.getMessage());
            }
        }

        boolean hasErrors = sb.length() > 0;
        if (!hasErrors) {
            view.clearError();
            return;
        }

        // Show the errors.
        view.showError(new SafeHtmlBuilder().appendEscaped(sb.substring(1)).toSafeHtml());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
        Entry entry = getEntryByValue(value);
        if (entry != null) {
            this.dropDown.setSelectedItem(value);
        } else {
            this.dropDown.setSelectedItem(selectorHint);
        }
    }
}
