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

package org.kie.workbench.common.stunner.core.util;

import java.util.Comparator;
import java.util.function.Supplier;

public class SafeComparator<T> implements Comparator<T> {

    public static final SafeComparator<Object> TO_STRING_COMPARATOR = of(() -> (obj1, obj2) -> obj1.toString().compareTo(obj2.toString()));

    private Supplier<Comparator<T>> comparatorSupplier;

    private SafeComparator(Supplier<Comparator<T>> comparatorSupplier) {
        this.comparatorSupplier = comparatorSupplier;
    }

    public static <K> SafeComparator<K> of(Supplier<Comparator<K>> comparatorSupplier) {
        return new SafeComparator<K>(comparatorSupplier);
    }

    @Override
    public int compare(final T obj1,
                       final T obj2) {
        if (obj1 == null) {
            return obj2 != null ? -1 : 0;
        } else if (obj2 == null) {
            return 1;
        } else {
            return comparatorSupplier.get().compare(obj1,
                                                    obj2);
        }
    }
}
