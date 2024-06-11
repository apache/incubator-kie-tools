/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.yard.validator;

import org.yard.model.Operators;
import org.yard.validator.key.ColumnKey;
import org.yard.validator.key.Key;
import org.yard.validator.key.OperatorValueKey;

import java.util.*;

public class CustomTreeSet {

    private final TreeSet<Key> innerSet;

    private final Set<ColumnKey> equalColumns = new HashSet<>();

    public CustomTreeSet() {
        innerSet = new TreeSet<>(getComparator());
    }

    private static Comparator<Key> getComparator() {
        return (a, b) -> {
            final int compareResult = compareKeys(a, b);
            if (compareResult == 0) {
                if (a instanceof OperatorValueKey
                        && b instanceof OperatorValueKey) {
                    return Operators.compare(
                            ((OperatorValueKey) a).getOperator(),
                            ((OperatorValueKey) b).getOperator());
                } else {
                    throw new IllegalArgumentException("What? Looks like it was not an OperatorValueKey.");
                }
            }
            return compareResult;
        };
    }

    private static int compareKeys(final Key a,
            final Key b) {
        if (a.getParent() instanceof Comparable
                && b.getParent() instanceof Comparable) {
            return ((Comparable) a.getParent()).compareTo(b.getParent());
        }
        return -1;
    }

    public Set<ColumnKey> getEqualColumns() {
        return equalColumns;
    }

    public int getHash(final Set<ColumnKey> bundleKeys) {
        int result = 0;

        // Inner HashMap would speed this up, but not sure if it is worth it.
        // All the ParentKeys that we are looking for are like on the front of the list.
        for (Key key : innerSet) {
            if (bundleKeys.contains(key.getParent())) {
                result = 31 * result + ((OperatorValueKey) key).getValue().hashCode();
            }
        }

        return result;
    }

    public boolean addAll(final CustomTreeSet c) {
        if (c instanceof CustomTreeSet) {
            equalColumns.addAll(((CustomTreeSet) c).equalColumns);
        }
        return innerSet.addAll(c.innerSet);
    }

    public boolean add(final Key o) {
        if (o instanceof OperatorValueKey) {
            if (Objects.equals(Operators.EQUALS, ((OperatorValueKey) o).getOperator())) {
                equalColumns.add((ColumnKey) o.getParent());
            }
        }
        return innerSet.add(o);
    }

    public boolean isEmpty() {
        return innerSet.isEmpty();
    }

    public int size() {
        return innerSet.size();
    }

    public Key[] toArray(final Key[] keys) {
        return innerSet.toArray(keys);
    }

    @Override
    public String toString() {
        String result = "";

        for (Key key : innerSet) {
            result += key.toString();
            result += "\n";
        }

        return result;
    }
}
