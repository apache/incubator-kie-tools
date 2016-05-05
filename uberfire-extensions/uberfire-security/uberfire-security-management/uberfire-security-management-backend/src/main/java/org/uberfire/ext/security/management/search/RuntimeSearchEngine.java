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

package org.uberfire.ext.security.management.search;

import org.uberfire.ext.security.management.api.AbstractEntityManager;

import java.util.Collection;


public interface RuntimeSearchEngine<T> {

    /**
     * <p>Perform a search using the given search request constraints over a collection of entities.</p>
     * @param entities The entities collection used as search source.
     * @param request The search constraints. If search pattern is <code>null</code> or empty, the result must contains all the entities from the source collection.
     * @return The search response.
     */
    AbstractEntityManager.SearchResponse<T> search(Collection<T> entities, AbstractEntityManager.SearchRequest request);
    
}
