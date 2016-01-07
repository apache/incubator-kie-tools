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
import org.uberfire.ext.security.management.impl.SearchResponseImpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <p>Base runtime search engine implementation for collections of entities.</p>
 * 
 * @since 0.8.0
 */
public abstract class AbstractRuntimeSearchEngine<T> implements RuntimeSearchEngine<T> {

    @Override
    public AbstractEntityManager.SearchResponse<T> search(Collection<T> entities, AbstractEntityManager.SearchRequest request) {
        if (entities == null || request == null) return null;

        // First page must be 1.
        if (request.getPage() <= 0) throw new RuntimeException("First page must be 1.");
        
        // Search elements using the given pattern & check the returning elements are not considered roles on UF.
        final String pattern = request.getSearchPattern();
        final boolean isPatternEmpty = isEmpty(pattern);
        Collection<T> result = isPatternEmpty ? entities : new LinkedList<T>();
        if (!isPatternEmpty) {
            for (T entity : entities) {
                final String id = getIdentifier(entity);
                if ( !isConstrained(request, id) && id.contains(pattern) ) {
                    result.add(entity);
                }

            }
        }

        return createResponse(result, request);
    }

    protected boolean isConstrained(AbstractEntityManager.SearchRequest request, String name) {
        final Set<String> constrainedIdentifiers = request.getConstrainedIdentifiers();
        if ( null != constrainedIdentifiers ) {
            for (final String id : constrainedIdentifiers) {
                if (id.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public AbstractEntityManager.SearchResponse<T> createResponse(Collection<T> entities, AbstractEntityManager.SearchRequest request) {
        List<T> result = new LinkedList<T>(entities);
        
        // Apply pagination.
        final int total = result.size();
        // First page is 1.
        final int page = request.getPage() - 1;
        final int pageSize = request.getPageSize();
        final int startPos = page * pageSize;
        final int endPos = startPos + pageSize > total ? total : startPos + pageSize;
        if (result.size() >= startPos) {
            result = result.subList(startPos, endPos);
        }

        // Return the paginated response.
        return new SearchResponseImpl<T>(result, page + 1, pageSize, total, total > endPos);
    }
        
    
    protected abstract String getIdentifier(T entity);
    
    protected boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

}
