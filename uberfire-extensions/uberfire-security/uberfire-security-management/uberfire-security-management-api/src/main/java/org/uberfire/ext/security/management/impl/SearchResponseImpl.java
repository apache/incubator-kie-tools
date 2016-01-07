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

package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.AbstractEntityManager;

import java.util.List;

/**
 * <p>A default search response implementation for the users system management.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class SearchResponseImpl<T> implements AbstractEntityManager.SearchResponse<T>{
    private String searchPattern = "";
    private int page = -1;
    private int pageSize = -1;
    private List<T> results;
    private int total = -1;
    private boolean hasNextPage;

    public SearchResponseImpl() {
    }

    public SearchResponseImpl(final List<T> results, final int page, final int pageSize, final int total, final boolean hasNextPage) {
        this.results = results;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.hasNextPage = hasNextPage;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }

    @Override
    public String getSearchPattern() {
        return searchPattern;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
