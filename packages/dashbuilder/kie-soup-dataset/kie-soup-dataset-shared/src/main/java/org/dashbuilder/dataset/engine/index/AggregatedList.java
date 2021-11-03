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
package org.dashbuilder.dataset.engine.index;

import java.util.AbstractList;
import java.util.List;
import java.util.ArrayList;

/**
 * A read-only list implementation that aggregates a set of sub lists.
 */
public class AggregatedList<T> extends AbstractList<T> {

    protected List<Integer> subIndexes = new ArrayList<Integer>();
    protected List<List<T>> subLists = new ArrayList<List<T>>();

    public AggregatedList() {
        super();
    }

    protected int lastIndex() {
        if (subIndexes.isEmpty()) return 0;
        return subIndexes.get(subIndexes.size()-1);
    }

    public void addSubList(List<T> l) {
        subIndexes.add(lastIndex() + l.size());
        subLists.add(l);
    }


    @Override
    public int size() {
        return lastIndex();
    }

    @Override
    /**
     *  <pre>
     *
     *            | sub-index | elements
     *  -----------------------------------------
     *  Sublist 1 |   3       |  A B C
     *  Sublist 2 |   7       |  D E F G
     *  Sublist 3 |   10      |  H I J
     *
     *  Element:   |A|B|C| D|E|F|G| H|I|J|
     *  Real idx:  |0|1|2| 3|4|5|6| 7|8|9|
     *  Local idx: |0|1|2| 0|1|2|3| 0|1|2|
     *  </pre>
     */
    public T get(int index) {
        for (int i=0; i< subIndexes.size(); i++) {
            Integer relIndex = subIndexes.get(i);
            if (index < relIndex) {
                List<T> subList = subLists.get(i);
                int realIndex = index - (relIndex - subList.size());
                return (T) subList.get(realIndex);
            }
        }
        throw new IndexOutOfBoundsException("The last index allowed is: " + lastIndex());
    }
}

