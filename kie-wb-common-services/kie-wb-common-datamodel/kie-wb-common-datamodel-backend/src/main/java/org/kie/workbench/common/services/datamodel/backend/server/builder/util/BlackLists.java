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
package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlackLists {

    /**
     * Not all methods make sense when shown in drop downs. Like toArray, hashCode, equals.
     * Methods can only be called or used to set something. Reasonable methods examples: clean, set, add.
     * @param clazz
     * @param methodName
     * @return
     */
    public static boolean isClassMethodBlackListed(final Class<?> clazz,
                                                   final String methodName) {
        if (isInObjectMethodsBlackList(methodName)) {
            return true;
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            if (isInCollectionMethodsBlackList(methodName)) {
                return true;
            }
        }

        if (Set.class.isAssignableFrom(clazz)) {
            if (isInSetMethodsBlackList(methodName)) {
                return true;
            }
        }

        if (List.class.isAssignableFrom(clazz)) {
            if (isInListMethodsBlackList(methodName)) {
                return true;
            }
        }

        if (Map.class.isAssignableFrom(clazz)) {
            if (isInMapMethodsBlackList(methodName)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isInObjectMethodsBlackList(final String methodName) {
        return "wait".equals(methodName)
                || "notify".equals(methodName)
                || "notifyAll".equals(methodName)
                || "class".equals(methodName)
                || "hashCode".equals(methodName)
                || "equals".equals(methodName)
                || "toString".equals(methodName);
    }

    private static boolean isInCollectionMethodsBlackList(final String methodName) {
        return "addAll".equals(methodName)
                || "containsAll".equals(methodName)
                || "iterator".equals(methodName)
                || "removeAll".equals(methodName)
                || "retainAll".equals(methodName)
                || "toArray".equals(methodName);
    }

    private static boolean isInSetMethodsBlackList(final String methodName) {
        return isInCollectionMethodsBlackList(methodName);
    }

    private static boolean isInListMethodsBlackList(final String methodName) {
        return isInCollectionMethodsBlackList(methodName)
                || "listIterator".equals(methodName)
                || "subList".equals(methodName);
    }

    private static boolean isInMapMethodsBlackList(final String methodName) {
        return "entrySet".equals(methodName)
                || "keySet".equals(methodName)
                || "putAll".equals(methodName);
    }

    /**
     * Not all data-types are supported; principally arrays.
     * @param type
     * @return
     */
    public static boolean isTypeBlackListed(final Class<?> type) {
        return type.isArray();
    }

    /**
     * Not all return data-types are supported; principally arrays and primitives.
     * @param type
     * @return
     */
    public static boolean isReturnTypeBlackListed(final Class<?> type) {
        return type.isArray() || type.isPrimitive();
    }
}
