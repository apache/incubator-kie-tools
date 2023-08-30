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


package org.kie.workbench.common.widgets.client.search.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.uberfire.mvp.Command;

public abstract class BaseEditorSearchIndex<T extends Searchable> implements EditorSearchIndex<T> {

    private final List<HasSearchableElements<T>> hasSearchableElementsList = new ArrayList<>();

    private List<T> results = new ArrayList<>();

    private T currentResult;

    private String currentTerm;

    private Integer currentAssetHash = null;

    private Command searchPerformedCallback = () -> {/* Nothing */};

    private Command noResultsFoundCallback = () -> {/* Nothing */};

    private Command clearCurrentResultsCallback = () -> {/* Nothing */};

    private Command searchClosedCallback = () -> {/* Nothing */};

    private Supplier<Integer> currentAssetHashcodeSupplier;

    @Override
    public void setSearchPerformedCallback(final Command searchPerformedCallback) {
        this.searchPerformedCallback = searchPerformedCallback;
    }

    @Override
    public List<HasSearchableElements<T>> getSubIndexes() {
        return hasSearchableElementsList;
    }

    @Override
    public void registerSubIndex(final HasSearchableElements<T> hasSearchableElements) {
        hasSearchableElementsList.add(hasSearchableElements);
    }

    @Override
    public void search(final String term) {

        triggerClearCurrentResultsCallback();
        final boolean isNewSearch = !Objects.equals(term, currentTerm) || isDirty();
        final Optional<T> result;

        if (isNewSearch) {
            loadSearchResults(term);
            result = getFirstSearchResult();
        } else {
            result = findNextElement();
        }

        currentResult = result.orElse(null);
        currentTerm = term;

        triggerOnFoundCommand();
        triggerOnSearchPerformedCommand();
    }

    @Override
    public int getCurrentResultNumber() {
        return results.size() > 0 ? getCurrentResultIndex() + 1 : 0;
    }

    @Override
    public int getTotalOfResultsNumber() {
        return results.size();
    }

    @Override
    public void close() {
        results = new ArrayList<>();
        currentTerm = "";
        currentResult = null;
        triggerOnSearchPerformedCommand();
        triggerSearchClosedCommand();
    }

    @Override
    public void setNoResultsFoundCallback(final Command callback) {
        noResultsFoundCallback = callback;
    }

    @Override
    public void setClearCurrentResultsCallback(final Command callback) {
        clearCurrentResultsCallback = callback;
    }

    @Override
    public void setSearchClosedCallback(final Command searchClosedCallback) {
        this.searchClosedCallback = searchClosedCallback;
    }

    @Override
    public boolean isDirty() {
        return currentAssetHash != null && !Objects.equals(currentAssetHash, getCurrentAssetHashcode());
    }

    @Override
    public void nextResult() {
        final Optional<T> result = findNextElement();
        currentResult = result.orElse(null);
        triggerOnFoundCommand();
        triggerOnSearchPerformedCommand();
    }

    @Override
    public void previousResult() {
        final Optional<T> result = findPreviousElement();
        currentResult = result.orElse(null);
        triggerOnFoundCommand();
        triggerOnSearchPerformedCommand();
    }

    @Override
    public Integer getCurrentAssetHashcode() {
        return getCurrentAssetHashcodeSupplier()
                .map(Supplier::get)
                .orElseThrow(() -> new UnsupportedOperationException("The asset hashcode supplier must be set in the 'EditorSearchIndex'."));
    }

    public void setCurrentAssetHashcodeSupplier(final Supplier<Integer> currentAssetHashcodeSupplier) {
        this.currentAssetHashcodeSupplier = currentAssetHashcodeSupplier;
    }

    private Optional<Supplier<Integer>> getCurrentAssetHashcodeSupplier() {
        return Optional.ofNullable(currentAssetHashcodeSupplier);
    }

    List<T> getResults() {
        return results;
    }

    String getCurrentTerm() {
        return currentTerm;
    }

    private Optional<T> getFirstSearchResult() {
        if (results.size() > 0) {
            return Optional.of(results.get(0));
        } else {
            return Optional.empty();
        }
    }

    private Optional<T> getLastSearchResult() {
        final int totalOfResults = results.size();
        if (totalOfResults > 0) {
            return Optional.of(results.get(totalOfResults - 1));
        } else {
            return Optional.empty();
        }
    }

    private Optional<T> findNextElement() {
        final int nextElementIndex = getCurrentResultIndex() + 1;
        if (nextElementIndex < results.size()) {
            return Optional.of(results.get(nextElementIndex));
        } else {
            return getFirstSearchResult();
        }
    }

    private Optional<T> findPreviousElement() {
        final int previousElementIndex = getCurrentResultIndex() - 1;
        if (previousElementIndex >= 0) {
            return Optional.of(results.get(previousElementIndex));
        } else {
            return getLastSearchResult();
        }
    }

    private Integer getCurrentResultIndex() {
        return getCurrentResult().map(c -> results.indexOf(c)).orElse(0);
    }

    private void loadSearchResults(final String term) {

        final List<T> searchableElements = getSearchableElements();

        updateCurrentHashcode();
        updateResultsState(term, searchableElements);
    }

    private void updateResultsState(final String term, final List<T> searchableElements) {
        results = searchableElements
                .stream()
                .filter(element -> element.matches(term))
                .collect(Collectors.toList());
    }

    private void updateCurrentHashcode() {
        currentAssetHash = getCurrentAssetHashcode();
    }

    private void triggerOnFoundCommand() {
        triggerClearCurrentResultsCallback();
        if (getCurrentResult().isPresent()) {
            getCurrentResult().get().onFound().execute();
        } else {
            triggerNoResultsFoundCommand();
        }
    }

    private void triggerNoResultsFoundCommand() {
        noResultsFoundCallback.execute();
    }

    void triggerOnSearchPerformedCommand() {
        if (!Objects.isNull(searchPerformedCallback)) {
            searchPerformedCallback.execute();
        }
    }

    @Override
    public Optional<T> getCurrentResult() {
        return Optional.ofNullable(currentResult);
    }

    private void triggerClearCurrentResultsCallback() {
        clearCurrentResultsCallback.execute();
    }
    private void triggerSearchClosedCommand() {
        searchClosedCallback.execute();
    }

    protected abstract List<T> getSearchableElements();
}
