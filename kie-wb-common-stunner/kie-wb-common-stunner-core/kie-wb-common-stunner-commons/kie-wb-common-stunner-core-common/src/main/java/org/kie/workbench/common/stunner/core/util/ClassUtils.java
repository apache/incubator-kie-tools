/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClassUtils {

    private final Map<Class<?>, Class<?>> WRAPPER_MAP = new HashMap<>();
    private boolean initialized;

    public ClassUtils() {
        init();
    }

    @PostConstruct
    private void init() {
        if (initialized) {
            return;
        }

        WRAPPER_MAP.put(Boolean.class, Boolean.TYPE);
        WRAPPER_MAP.put(Byte.class, Byte.TYPE);
        WRAPPER_MAP.put(Character.class, Character.TYPE);
        WRAPPER_MAP.put(Short.class, Short.TYPE);
        WRAPPER_MAP.put(Integer.class, Integer.TYPE);
        WRAPPER_MAP.put(Long.class, Long.TYPE);
        WRAPPER_MAP.put(Double.class, Double.TYPE);
        WRAPPER_MAP.put(Float.class, Float.TYPE);
        WRAPPER_MAP.put(Void.class, Void.TYPE);
        initialized = true;
    }

    /**
     * Server and client side oriented type checking.
     */
    public static boolean isTypeOf(Class<?> type,
                                   Object instance) {
        return instance.getClass().getName().equals(type.getName());
    }

    public boolean isPrimitiveClass(Class<?> type) {
        return type.isPrimitive() || WRAPPER_MAP.containsKey(type);
    }
}