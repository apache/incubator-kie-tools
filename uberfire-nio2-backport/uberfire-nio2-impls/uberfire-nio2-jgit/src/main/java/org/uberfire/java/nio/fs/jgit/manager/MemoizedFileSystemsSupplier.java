/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class MemoizedFileSystemsSupplier<T> implements Supplier<T> {

    final Supplier<T> delegate;
    ConcurrentMap<Class<?>, T> map = new ConcurrentHashMap<>(1);

    private MemoizedFileSystemsSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {

        T t = this.map.computeIfAbsent(MemoizedFileSystemsSupplier.class,
                                       k -> this.delegate.get());
        return t;
    }

    public static <T> Supplier<T> of(Supplier<T> provider) {
        return new MemoizedFileSystemsSupplier<>(provider);
    }
}