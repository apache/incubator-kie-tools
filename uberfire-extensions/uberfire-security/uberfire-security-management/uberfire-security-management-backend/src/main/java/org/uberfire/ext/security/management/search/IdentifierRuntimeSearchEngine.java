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
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Default runtime search engine implementation for collections of users.</p>
 * 
 * @since 0.8.0
 */
public abstract class IdentifierRuntimeSearchEngine<T> extends AbstractRuntimeSearchEngine<T> {

    public AbstractEntityManager.SearchResponse<T> searchByIdentifiers(Collection<String> entityIdentifiers, AbstractEntityManager.SearchRequest request) {
        if (entityIdentifiers == null || request == null) return null;

        // First page must be 1.
        if (request.getPage() <= 0) throw new RuntimeException("First page must be 1.");

        // Search elements using the given pattern  & check the returning elements are not considered roles on UF.
        final String pattern = request.getSearchPattern();
        final boolean isPatternEmpty = isEmpty(pattern);
        Collection<String> result = isPatternEmpty ? entityIdentifiers : new LinkedList<String>();
        if (!isPatternEmpty) {
            for (String id: entityIdentifiers) {
                if ( !isConstrained(request, id) && id.contains(pattern) ) {
                    result.add(id);
                }
            }
        }

        // Create the entities from the identifiers sublist.
        List<T> resultEntities = new LinkedList<T>();
        for (final String id : result) {
            if (!isConstrained(request, id)) {
                final T entity = createEntity(id);
                resultEntities.add(entity);
            }
        }

        return createResponse(resultEntities, request);
    }

    protected abstract T createEntity(String identifier);

    @Override
    protected String getIdentifier(T entity) {
        return null;
    }
}
