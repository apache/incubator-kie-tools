/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.AssigneeLiveSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.AssigneeLocalSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget.AssigneeLiveSearchEntryCreationEditor;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class AssigneeLiveSearchProjectService implements AssigneeLiveSearchService {

    private static final Logger LOGGER = Logger.getLogger(AssigneeLiveSearchProjectService.class.getName());

    private final ClientUserSystemManager userSystemManager;
    private final AssigneeLiveSearchEntryCreationEditor editor;

    private AssigneeLocalSearchService localSearchService;
    private AssigneeType type = AssigneeType.USER;
    private Consumer<Throwable> searchErrorHandler;

    @Inject
    public AssigneeLiveSearchProjectService(ClientUserSystemManager userSystemManager,
                                            AssigneeLiveSearchEntryCreationEditor editor) {
        this.userSystemManager = userSystemManager;
        this.editor = editor;
    }

    @PostConstruct
    public void postConstruct() {
        localSearchService = AssigneeLocalSearchService.build(editor);
    }

    public void init(AssigneeType type) {
        this.type = type;
    }

    public void setSearchErrorHandler(Consumer<Throwable> searchErrorHandler) {
        this.searchErrorHandler = searchErrorHandler;
    }

    @Override
    public void search(String pattern,
                       int maxResults,
                       LiveSearchCallback<String> callback) {

        final List<String> filteredCustomEntries = localSearchService.search(pattern);

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> searchResponseRemoteCallback = response -> processFilterResponse(response, filteredCustomEntries, maxResults, callback);

        ErrorCallback<Message> searchErrorCallback = (message, throwable) -> processSearchError(filteredCustomEntries, maxResults, callback, throwable);

        SearchRequestImpl request = new SearchRequestImpl(pattern, 1, maxResults);

        if (AssigneeType.USER.equals(type)) {
            userSystemManager.users(searchResponseRemoteCallback, searchErrorCallback).search(request);
        } else {
            userSystemManager.groups(searchResponseRemoteCallback, searchErrorCallback).search(request);
        }
    }

    @Override
    public void addCustomEntry(final String customEntry) {
        localSearchService.addCustomEntry(customEntry);
    }

    @Override
    public AssigneeLiveSearchEntryCreationEditor getEditor() {
        return editor;
    }

    @Override
    public void searchEntry(String key,
                            LiveSearchCallback<String> callback) {

        SearchRequestImpl request = new SearchRequestImpl(key, 1, 1);

        ErrorCallback<Message> searchErrorCallback = (message, throwable) -> processSearchEntryError(key, callback, throwable);

        RemoteCallback<AbstractEntityManager.SearchResponse<?>> searchResponseRemoteCallback = response -> searchEntry(key, response, callback);

        if (AssigneeType.USER.equals(type)) {
            userSystemManager.users(searchResponseRemoteCallback, searchErrorCallback).search(request);
        } else {
            userSystemManager.groups(searchResponseRemoteCallback, searchErrorCallback).search(request);
        }
    }

    @PreDestroy
    public void destroy() {
        localSearchService.destroy();
        localSearchService = null;
    }

    private boolean processSearchEntryError(String key, LiveSearchCallback<String> callback, Throwable throwable) {
        LiveSearchResults<String> results = new LiveSearchResults<>(1);
        if (!isEmpty(key)) {
            addCustomEntry(key);
            results.add(key, key);
        }
        callback.afterSearch(results);
        processError("It was not possible to get user or group: " + key + " from the users system.", throwable);
        return false;
    }

    private boolean processSearchError(List<String> filteredCustomEntries, int maxResults, LiveSearchCallback<String> callback, Throwable throwable) {
        int maxSize = maxResults > filteredCustomEntries.size() ? filteredCustomEntries.size() : maxResults;
        maxSize = maxSize < 0 ? 0 : maxSize;
        LiveSearchResults<String> result = new LiveSearchResults<>(maxSize);
        filteredCustomEntries.subList(0, maxSize).forEach(entry -> result.add(entry, entry));
        callback.afterSearch(result);
        processError("It was not possible to execute search on the users system.", throwable);
        return false;
    }

    private void processError(String errorMessage, Throwable throwable) {
        LOGGER.log(Level.SEVERE, errorMessage, throwable);
        if (searchErrorHandler != null) {
            searchErrorHandler.accept(throwable);
        }
    }

    private void searchEntry(String key,
                             AbstractEntityManager.SearchResponse<?> response,
                             LiveSearchCallback<String> liveSearchCallback) {

        LiveSearchResults<String> results = new LiveSearchResults<>(1);

        if (key != null) {
            if (!getCustomEntries().contains(key)) {
                Optional<?> exists = response.getResults().stream().filter(item -> {

                    String value = null;

                    if (item instanceof User) {
                        value = ((User) item).getIdentifier();
                    } else if (item instanceof Group) {
                        value = ((Group) item).getName();
                    }

                    return key.equals(value);
                }).findAny();

                if (!exists.isPresent()) {
                    addCustomEntry(key);
                }
            }
            results.add(key, key);
        }

        liveSearchCallback.afterSearch(results);
    }

    protected void processFilterResponse(AbstractEntityManager.SearchResponse<?> response,
                                         List<String> filteredCustomEntries,
                                         int maxResults,
                                         LiveSearchCallback<String> liveSearchCallback) {

        Set<String> values = new TreeSet<>(filteredCustomEntries);

        response.getResults().forEach(item -> {
            String value = null;

            if (item instanceof User) {
                value = ((User) item).getIdentifier();
            } else if (item instanceof Group) {
                value = ((Group) item).getName();
            }

            if (value != null) {
                values.add(value);
            }
        });

        if (maxResults > values.size()) {
            maxResults = values.size();
        }

        LiveSearchResults<String> results = AssigneeLocalSearchService.createSearchResults(values, maxResults);

        liveSearchCallback.afterSearch(results);
    }

    List<String> getCustomEntries() {
        return localSearchService.getCustomEntries();
    }
}
