/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.sort;

import java.util.AbstractList;
import java.util.List;

/**
 * An unmodifiable list which provides a lightweight read-only access to an existing list but applying a pre-established order.
 * <p>The order is specified by a list of integers containing the order of the elements.</p>
 */
public class SortedList extends AbstractList {

    protected List<Integer> rows = null;
    protected List realList = null;

    public SortedList() {
    }

    public SortedList(List realList, List<Integer> rows) {
        if (rows.size() > realList.size()) {
            throw new IllegalArgumentException("The number of rows (" + rows.size() + ") can be greater than the real list (" + realList.size() + ").");
        }
        this.realList = realList;
        this.rows = rows;
    }

    public int row(int index) {
        if (index >= rows.size()) return -1;
        return rows.get(index);
    }

    @Override
    public Object get(int index) {
        int realIndex = row(index);
        if (realIndex == -1) return null;
        return realList.get(realIndex);
    }

    @Override
    public int size() {
        return rows.size();
    }
}
