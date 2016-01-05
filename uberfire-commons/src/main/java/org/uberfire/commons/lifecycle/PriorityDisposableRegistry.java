/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.commons.lifecycle;

import java.util.*;

public final class PriorityDisposableRegistry {

    private static Set<PriorityDisposable> disposables = new HashSet<PriorityDisposable>();
    private static Map<String, Object> registry = new HashMap<String, Object>();

    private PriorityDisposableRegistry() {
    }

    public static void register( final PriorityDisposable priorityDisposable ) {
        disposables.add( priorityDisposable );
    }

    public static void register( final String refName,
                                 final Object disposable ) {
        registry.put( refName, disposable );
    }

    public static void clear() {
        disposables.clear();
        registry.clear();
    }

    public static Collection<PriorityDisposable> getDisposables() {
        return disposables;
    }

    static Map<String, Object> getRegistry() {
        return registry;
    }

    public static void unregister( final String refName ) {
        registry.remove( refName );
    }

    public static Object get( final String refName ) {
        return registry.get( refName );
    }
}
