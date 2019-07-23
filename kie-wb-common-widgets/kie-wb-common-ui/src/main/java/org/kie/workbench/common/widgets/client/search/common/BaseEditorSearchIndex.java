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

import org.uberfire.mvp.Command;

import static org.kie.workbench.common.widgets.client.search.common.JavaStreamHelper.indexedStream;

public abstract class BaseEditorSearchIndex<T extends Searchable> implements EditorSearchIndex<T> {

    private final List<HasSearchableElements<T>> hasSearchableElementsList = new ArrayList<>();

    private SearchResult<T> currentResult;

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
        final Optional<SearchResult<T>> result;

        if (isNewSearch) {
            result = findFirstElement(term);
        } else {
            result = findNextElement(term);
        }

        currentResult = result.orElse(null);
        currentTerm = term;

        triggerOnFoundCommand();
    }

    private Optional<SearchResult<T>> findNextElement(final String term) {

        final SearchResult<T>[] firstElement = new SearchResult[1];
        final List<T> searchableElements = getSearchableElements();
        final Optional<SearchResult<T>> next =
                indexedStream(searchableElements)
                        .filter((index, element) -> {
                            final boolean matches = element.matches(term);
                            if (matches && firstElement[0] == null) {
                                firstElement[0] = new SearchResult<>(index, element);
                            }
                            return matches && index > currentResult.index;
                        })
                        .findFirst()
                        .map(tuple -> new SearchResult<>(tuple.getIndex(), tuple.getElement()));

        if (next.isPresent()) {
            return next;
        } else {
            return Optional.ofNullable(firstElement[0]);
        }
    }

    private Optional<SearchResult<T>> findFirstElement(final String term) {

        final List<T> searchableElements = getSearchableElements();

        return indexedStream(searchableElements)
                .filter((index, element) -> element.matches(term))
                .map(tuple -> new SearchResult<>(tuple.getIndex(), tuple.getElement()))
                .findFirst();
    }

    private void triggerOnFoundCommand() {
        if (getCurrentResult().isPresent()) {
            getCurrentResult().get().element.onFound().execute();
        } else {
            triggerNoResultsFoundCommand();
        }
    }

    private void triggerNoResultsFoundCommand() {
        noResultsFoundCallback.execute();
    }

    private Optional<SearchResult> getCurrentResult() {
        return Optional.ofNullable(currentResult);
    }

    protected abstract List<T> getSearchableElements();

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

    class SearchResult<R extends Searchable> {

        private final int index;
        private final R element;

        SearchResult(final int index,
                     final R element) {
            this.index = index;
            this.element = element;
        }
    }
}
