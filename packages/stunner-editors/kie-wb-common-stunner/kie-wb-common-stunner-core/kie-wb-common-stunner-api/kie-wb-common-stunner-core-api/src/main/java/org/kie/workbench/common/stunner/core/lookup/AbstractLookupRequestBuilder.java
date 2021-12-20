/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

public abstract class AbstractLookupRequestBuilder<T> {

    protected int page = 0;
    protected int pageSize = 10;

    @SuppressWarnings("unchecked")
    public T page(final int page) {
        this.page = page;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return (T) this;
    }

    protected String fromKeyValue(final String key,
                                  final String value) {
        return key + "=" + value + ";";
    }

    protected String fromSet(final Set<String> set) {
        if (null != set && !set.isEmpty()) {
            final StringBuilder builder = new StringBuilder("[");
            int x = 0;
            for (final String s : set) {
                builder.append(s);
                if (x < (set.size() - 1)) {
                    builder.append(",");
                }
                x++;
            }
            return builder.append("]").toString();
        }
        return null;
    }
}
