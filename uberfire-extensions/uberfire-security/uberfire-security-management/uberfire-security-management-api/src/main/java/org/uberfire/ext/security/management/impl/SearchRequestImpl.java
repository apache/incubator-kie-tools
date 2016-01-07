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

import java.util.Set;

/**
 * <p>A default search request implementation for the users system management.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class SearchRequestImpl implements AbstractEntityManager.SearchRequest {
    private String searchPattern = "";
    private Set<String> constrainedIdentifiers;
    private int page = 1;
    private int pageSize = 15;

    public SearchRequestImpl() {
    }

    public SearchRequestImpl(String searchPattern, int page, int pageSize) {
        this.searchPattern = searchPattern;
        this.page = page;
        this.pageSize = pageSize;
    }

    public SearchRequestImpl(String searchPattern, int page, int pageSize, Set<String> constrainedIdentifiers) {
        this.searchPattern = searchPattern;
        this.page = page;
        this.pageSize = pageSize;
        this.constrainedIdentifiers = constrainedIdentifiers;
    }

    @Override
    public String getSearchPattern() {
        return searchPattern;
    }

    @Override
    public AbstractEntityManager.SearchRequest setConstrainedIdentifiers(Set<String> constrainedIdentifiers) {
        this.constrainedIdentifiers = constrainedIdentifiers;
        return this;
    }

    @Override
    public Set<String> getConstrainedIdentifiers() {
        return constrainedIdentifiers;
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