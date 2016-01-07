/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api;

import org.uberfire.ext.security.management.api.exception.SecurityManagementException;

import java.util.List;
import java.util.Set;

/**
 * <p>Basic management API for security realm entities type of <code>T</code>.</p>
 * 
 * @since 0.8.0
 */
public interface AbstractEntityManager<T, S extends Settings> {

    /**
     * Search entities.
     * @param request The search request constraints.
     * @return List of resulting entities from the search result. 
     * @throws SecurityManagementException
     */
    SearchResponse<T> search(SearchRequest request) throws SecurityManagementException;

    /**
     * Obtain a single entity instance.
     * @param identifier The entity's identifier.
     * @return The entity for the given identifier.
     * @throws SecurityManagementException
     */
    T get(final String identifier) throws SecurityManagementException;

    /**
     * Creates a given entity in the backend security environment.
     * @param entity The entity to create.
     * @return The entity.
     * @throws SecurityManagementException
     */
    T create(T entity) throws SecurityManagementException;

    /**
     * Creates a given entity in the backend security environment.
     * @param entity The entity to create.
     * @return The entity.
     * @throws SecurityManagementException
     */
    T update(T entity) throws SecurityManagementException;

    /**
     * Deletes a given entity or entities (bulk delete) in the backend security environment.
     * @param identifiers The entity identifiers to delete.
     * @return The entity.
     * @throws SecurityManagementException
     */
    void delete(final String... identifiers) throws SecurityManagementException;

    /**
     * The entity manager settings.
     * @return The settings for the entitty manager.
     */
    S getSettings();

    /**
     * <p>The request parameters for performing entity searching on the backend security server.</p>
     */
    interface SearchRequest {

        /**
         * <p>The search pattern string.</p>
         * @return The search pattern.
         */
        String getSearchPattern();

        /**
         * <p>Constrained (not available to use) identifiers.</p> 
         * <p>If you don't want to include some entities in the response, add their identifiers in the collection.</p>  
         */
        SearchRequest setConstrainedIdentifiers(Set<String> constrainedIdentifiers);

        /**
         * <p>Constrained (not available to use) identifiers.</p> 
         */
        Set<String> getConstrainedIdentifiers();
        
        /**
         * <p>The page number for the search cursor.</p>
         * <p>IMPORTANT NOTE: Page number starts with value <code>1</code>.</p>
         * @return The page for the returned results.
         */
        int getPage();

        /**
         * <p>The number of items for each page.</p>
         * @return The page size.
         */
        int getPageSize();
    }

    /**
     * <p>The response values for a search operation.</p>
     * @param <T> The entity type.
     */
    interface SearchResponse<T> {
        /**
         * <p>The entities resulting from the search operation.</p>
         * @return The entities resulting from the search operation.
         */
        List<T> getResults();
        
        /**
         * <p>The total entities count.</p>
         * @return 
         *  <p>By convention, if the service provider implementation class is not able to get the row count, this method should return <code>-1</code>.</p>
         *  <p>Otherwise, returns search results count for this entity type.</p>
         */
        int getTotal();

        /**
         * <p>Indicates if there are more results (next pages).</p>
         * <p>If the service provider implementation class is not able to return a value for <code>getTotal</code>, this method can be used to find out if there are more pages.</p>
         * @return Indicates if there are more results (next pages).
         */
        boolean hasNextPage();

        /**
         * <p>The search pattern string.</p>
         * @return The search pattern.
         */
        String getSearchPattern();

        /**
         * <p>The page number for the search cursor.</p>
         * <p>IMPORTANT NOTE: Page number starts with value <code>1</code>.</p>
         * @return The page for the returned results.
         */
        int getPage();

        /**
         * <p>The number of items for each page.</p>
         * @return The page size.
         */
        int getPageSize();
    }

}
