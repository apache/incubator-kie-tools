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


package org.kie.workbench.common.stunner.core.graph.util;

/**
 * Utility class that allows working with functional programming and streams easier when having exceptions.
 */
public class Exceptions {

    /**
     * The same as {@link java.util.function.Supplier} but supports throwing Exceptions.
     * @param <T>
     */
    @FunctionalInterface
    public interface Supplier<T> {

        T get() throws Exception;
    }

    public static <T> T swallow(final Supplier<T> supplier, final T defaultReturn) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return defaultReturn;
        }
    }
}
