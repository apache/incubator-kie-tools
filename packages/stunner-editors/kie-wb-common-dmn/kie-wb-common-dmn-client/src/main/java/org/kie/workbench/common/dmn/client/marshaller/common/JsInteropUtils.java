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

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import elemental2.core.Global;
import jsinterop.base.Js;

/*
 * Lists containing native types require uncheckedCasts many operations.
 */
public class JsInteropUtils {

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static <T> void forEach(final List<T> list,
                                   final Consumer<T> consumer) {
        for (int i = 0; i < list.size(); i++) {
            final T item = Js.uncheckedCast(list.get(i));
            consumer.accept(item);
        }
    }

    public static <T> boolean anyMatch(final List<T> list,
                                       final Function<T, Boolean> function) {
        final AtomicBoolean anyMatch = new AtomicBoolean(false);
        forEach(list, item -> {
            if (function.apply(item)) {
                anyMatch.set(true);
            }
        });
        return anyMatch.get();
    }

    @SuppressWarnings("unused")
    public static <T> T getItem(final List<T> list,
                                final int index) {
        return Js.uncheckedCast(list.get(index));
    }

    public static <T> T jsCopy(final T element) {
        final String stringify = Global.JSON.stringify(element);
        final Object copy = Global.JSON.parse(stringify);
        return Js.uncheckedCast(copy);
    }
}
