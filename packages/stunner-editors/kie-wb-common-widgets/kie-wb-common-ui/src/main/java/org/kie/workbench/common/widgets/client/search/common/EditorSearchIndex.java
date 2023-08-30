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

import java.util.List;
import java.util.Optional;

import org.uberfire.mvp.Command;

/**
 * {@link EditorSearchIndex} holds the search logic.
 * ---
 * The {@link EditorSearchIndex} can have many sub-indexes that index different kinds of elements. Each sub-index, or
 * {@link HasSearchableElements}, is naive and will load its searchable elements for every call.
 * Thus, implementations of this interface control lazy approaches, cached approaches, or even the use of a third party
 * library.
 * ---
 * @param <T> represents the type of {@link Searchable} element.
 */
public interface EditorSearchIndex<T extends Searchable> {

    /**
     * Returns the current result.
     * @return The current result.
     */
    Optional<T> getCurrentResult();

    /**
     * Returns the list of sub-indexes.
     * @return the list of {@link HasSearchableElements}.
     */
    List<HasSearchableElements<T>> getSubIndexes();

    /**
     * Registers a new sub-index into {@link EditorSearchIndex}.
     * @param hasSearchableElements represents a new index.
     */
    void registerSubIndex(final HasSearchableElements<T> hasSearchableElements);

    /**
     * Searches for any {@link Searchable} that satisfies the parameter, and triggers the <code>onFound</code>
     * callback for the first result.
     * @param term the string that will trigger the search
     */
    void search(final String term);

    /**
     * Sets the callback that will be triggered when no result is found.
     * @param callback the callback that will be triggered
     */
    void setNoResultsFoundCallback(final Command callback);

    void setSearchClosedCallback(final Command callback);

    /**
     * Sets the callback that will be triggered when search is performed.
     * @param callback the callback that will be triggered
     */
    void setSearchPerformedCallback(final Command callback);

    /**
     * Sets the callback that will be triggered when the current search results needs to be cleared.
     * @param callback the callback that will be triggered
     */
    void setClearCurrentResultsCallback(final Command callback);

    /**
     * This method is used by the <code>isDirty</code> logic.
     * @return the asset hashcode.
     */
    Integer getCurrentAssetHashcode();

    /**
     * Check if the index is dirty.
     * @return true if the index is dirty.
     */
    boolean isDirty();

    /**
     * Triggers the <code>onFound</code> callback for the next result.
     */
    void nextResult();

    /**
     * Triggers the <code>onFound</code> callback for the previous result.
     */
    void previousResult();

    /**
     * Returns the number of the current result.
     * @return a int value representing the number of current result.
     */
    int getCurrentResultNumber();

    /**
     * Returns the number of results.
     * @return a int value representing the number of results.
     */
    int getTotalOfResultsNumber();

    /**
     * Resets the index state.
     * It affects the <code>isDirty</code>, the <code>nextResult</code>, the <code>previousResult</code>,
     * the <code>getCurrentResultNumber</code>, and the <code>getTotalOfResultsNumber</code> method.
     */
    void close();
}
