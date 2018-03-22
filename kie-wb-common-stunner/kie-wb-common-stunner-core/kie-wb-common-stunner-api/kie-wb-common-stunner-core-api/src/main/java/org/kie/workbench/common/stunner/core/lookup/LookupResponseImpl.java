/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.lookup;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public final class LookupResponseImpl<T> implements LookupManager.LookupResponse<T> {

    private final List<T> results;
    private final int total;
    private final boolean hasNextPage;
    private final String lookupCriteria;
    private final int page;
    private final int pageSize;

    public LookupResponseImpl(final @MapsTo("results") List<T> results,
                              final @MapsTo("total") int total,
                              final @MapsTo("hasNextPage") boolean hasNextPage,
                              final @MapsTo("lookupCriteria") String lookupCriteria,
                              final @MapsTo("page") int page,
                              final @MapsTo("pageSize") int pageSize) {
        this.results = results;
        this.total = total;
        this.hasNextPage = hasNextPage;
        this.lookupCriteria = lookupCriteria;
        this.page = page;
        this.pageSize = pageSize;
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
    public String getLookupCriteria() {
        return lookupCriteria;
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
