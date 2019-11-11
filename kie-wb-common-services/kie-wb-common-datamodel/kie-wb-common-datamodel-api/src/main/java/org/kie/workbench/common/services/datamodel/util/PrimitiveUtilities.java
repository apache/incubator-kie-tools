/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.util;

/**
 * Utilities for Java primitive types
 */
public class PrimitiveUtilities {

    public static final String BYTE = "byte";

    public static final String SHORT = "short";

    public static final String INT = "int";

    public static final String LONG = "long";

    public static final String FLOAT = "float";

    public static final String DOUBLE = "double";

    public static final String CHAR = "char";

    public static final String BOOLEAN = "boolean";

    /**
     * Gets the class name of the boxed form or a Java primitive.
     * @param type The type of the primitive.
     * @return The boxed form class name or null if not a primitive.
     */
    public static String getClassNameForPrimitiveType(final String type) {
        if (BYTE.equals(type)) {
            return Byte.class.getName();
        }
        if (SHORT.equals(type)) {
            return Short.class.getName();
        }
        if (INT.equals(type)) {
            return Integer.class.getName();
        }
        if (LONG.equals(type)) {
            return Long.class.getName();
        }
        if (FLOAT.equals(type)) {
            return Float.class.getName();
        }
        if (DOUBLE.equals(type)) {
            return Double.class.getName();
        }
        if (CHAR.equals(type)) {
            return Character.class.getName();
        }
        if (BOOLEAN.equals(type)) {
            return Boolean.class.getName();
        }

        return null;
    }
}
