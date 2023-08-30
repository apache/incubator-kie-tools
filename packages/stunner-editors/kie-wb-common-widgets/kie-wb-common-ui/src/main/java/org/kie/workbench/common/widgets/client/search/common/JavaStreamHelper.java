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


package org.kie.workbench.common.widgets.client.search.common;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class JavaStreamHelper<T> {

    private final List<T> list;

    private JavaStreamHelper(final List<T> list) {
        this.list = list;
    }

    public static <T> JavaStreamHelper<T> indexedStream(final List<T> list) {
        return new JavaStreamHelper<>(list);
    }

    public Stream<Tuple<T>> filter(final BiFunction<Integer, T, Boolean> filter) {
        final AtomicInteger index = new AtomicInteger(-1);
        return list
                .stream()
                .filter(element -> {
                    final int currentIndex = index.incrementAndGet();
                    return filter.apply(currentIndex, element);
                })
                .map(e -> new Tuple<>(index.get(), e));
    }

    public static class Tuple<T> {

        private final Integer index;
        private final T element;

        Tuple(final Integer index,
              final T element) {
            this.index = index;
            this.element = element;
        }

        public Integer getIndex() {
            return index;
        }

        public T getElement() {
            return element;
        }
    }
}
