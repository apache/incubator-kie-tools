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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLookupManager<I, T, R extends LookupManager.LookupRequest> implements LookupManager<T, R> {

    protected abstract List<I> getItems(R request);

    protected abstract boolean matches(String criteria,
                                       I item);

    protected abstract T buildResult(I item);

    @Override
    public LookupResponse<T> lookup(final R request) {
        final List<I> items = getItems(request);
        if (null != items) {
            if (!items.isEmpty()) {
                final int page = request.getPage();
                final int pageSize = request.getPageSize();
                final int from = page * pageSize;
                final String criteria = request.getCriteria();
                // Criteria filtering.
                List<I> result = new LinkedList<>();
                for (I item : items) {
                    if (matches(criteria,
                                item)) {
                        result.add(item);
                    }
                }
                // Pagination checks.
                if (result.size() < from) {
                    throw new IllegalArgumentException("Specified lookup request page [" + from + "] cannot be used, as there are no many results.");
                }
                final int to = result.size() < (from + pageSize) ? result.size() : (from + pageSize);
                // Apply pagination and build results.
                final List<T> representations =
                        result.subList(from,
                                       to)
                                .stream()
                                .map(this::buildResult)
                                .collect(Collectors.toList());
                return new LookupResponseImpl<T>(representations,
                                                 representations.size(),
                                                 items.size() > to,
                                                 request.getCriteria(),
                                                 request.getPage(),
                                                 request.getPageSize());
            }
        }
        return buildEmptyResponse(request);
    }

    protected LookupResponse<T> buildEmptyResponse(final R request) {
        return new LookupResponseImpl<T>(new ArrayList<T>(),
                                         0,
                                         false,
                                         request.getCriteria(),
                                         request.getPage(),
                                         request.getPageSize());
    }
}
