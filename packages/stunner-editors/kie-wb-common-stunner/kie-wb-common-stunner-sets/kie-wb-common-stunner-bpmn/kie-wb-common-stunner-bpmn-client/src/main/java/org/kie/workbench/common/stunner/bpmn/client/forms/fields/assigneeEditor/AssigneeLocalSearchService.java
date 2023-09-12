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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget.AssigneeLiveSearchEntryCreationEditor;
import org.uberfire.ext.widgets.common.client.dropdown.EntryCreationLiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class AssigneeLocalSearchService
        implements EntryCreationLiveSearchService<String, AssigneeLiveSearchEntryCreationEditor> {

    private final AssigneeLiveSearchEntryCreationEditor editor;
    private final List<String> customEntries;

    public static AssigneeLocalSearchService build(final AssigneeLiveSearchEntryCreationEditor editor) {
        return new AssigneeLocalSearchService(editor);
    }

    private AssigneeLocalSearchService(final AssigneeLiveSearchEntryCreationEditor editor) {
        this.editor = editor;
        this.customEntries = new ArrayList<>();
        editor.setCustomEntryCommand(this::addCustomEntry);
    }

    public void addCustomEntry(final String customEntry) {
        if (!isEmpty(customEntry)) {
            customEntries.add(customEntry);
        }
    }

    public LiveSearchResults<String> search(final String pattern,
                                            final int maxResults) {
        final Collection<String> items = search(pattern);
        return createSearchResults(items, maxResults);
    }

    public List<String> search(final String pattern) {
        final List<String> filteredCustomEntries;
        if (pattern == null || pattern.isEmpty()) {
            filteredCustomEntries = customEntries;
        } else {
            filteredCustomEntries = customEntries.stream()
                    .filter(entry -> entry.contains(pattern))
                    .collect(Collectors.toList());
        }
        return filteredCustomEntries;
    }

    @Override
    public void search(final String pattern,
                       final int maxResults,
                       final LiveSearchCallback<String> callback) {
        final LiveSearchResults<String> searchResults = search(pattern, maxResults);
        callback.afterSearch(searchResults);
    }

    @Override
    public void searchEntry(final String key,
                            final LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>(1);
        if (key != null) {
            if (!customEntries.contains(key)) {
                addCustomEntry(key);
            }
            results.add(key, key);
        }
        callback.afterSearch(results);
    }

    public static LiveSearchResults<String> createSearchResults(final Collection<String> values,
                                                                final int maxAllowedResults) {
        final int maxResults = values.size() > maxAllowedResults ? maxAllowedResults : values.size();
        final LiveSearchResults<String> results = new LiveSearchResults<>(maxResults);
        new ArrayList<>(values)
                .subList(0, maxResults)
                .forEach(value -> results.add(value, value));
        return results;
    }

    @Override
    public AssigneeLiveSearchEntryCreationEditor getEditor() {
        return editor;
    }

    public List<String> getCustomEntries() {
        return customEntries;
    }

    public void destroy() {
        customEntries.clear();
    }
}
