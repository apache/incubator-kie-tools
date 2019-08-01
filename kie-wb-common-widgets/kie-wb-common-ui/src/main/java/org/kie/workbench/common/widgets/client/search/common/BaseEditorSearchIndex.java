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

    private Supplier<Boolean> isDirtySupplier = () -> false;

    private Command noResultsFoundCallback = () -> {/* Nothing */};

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

        final boolean isNewSearch = !Objects.equals(term, currentTerm);
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
    public void reset() {
        results = new ArrayList<>();
        currentTerm = "";
        triggerNoResultsFoundCommand();
    }

    @Override
    public void setNoResultsFoundCallback(final Command callback) {
        noResultsFoundCallback = callback;
    }

    @Override
    public void setIsDirtySupplier(final Supplier<Boolean> isDirtySupplier) {
        this.isDirtySupplier = isDirtySupplier;
    }

    @Override
    public boolean isDirty() {
        return isDirtySupplier.get();
    }

    @Override
    public void nextResult() {
        final Optional<T> result = findNextElement();
        currentResult = result.orElse(null);
        triggerOnFoundCommand();
    }

    @Override
    public void previousResult() {
        final Optional<T> result = findPreviousElement();
        currentResult = result.orElse(null);
        triggerOnFoundCommand();
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

        results = searchableElements
                .stream()
                .filter(element -> element.matches(term))
                .collect(Collectors.toList());
    }

    private void triggerOnFoundCommand() {
        if (getCurrentResult().isPresent()) {
            getCurrentResult().get().onFound().execute();
        } else {
            triggerNoResultsFoundCommand();
        }
    }

    private void triggerNoResultsFoundCommand() {
        noResultsFoundCallback.execute();
    }

    private Optional<T> getCurrentResult() {
        return Optional.ofNullable(currentResult);
    }

    protected abstract List<T> getSearchableElements();
}
