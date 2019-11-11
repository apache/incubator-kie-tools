/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.util;

import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BOOLEAN;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BYTE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.CHAR;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.DOUBLE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.FLOAT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.INT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.LONG;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.SHORT;

public class NamingUtils {

    public static String extractClassName(final String fullClassName) {

        if (fullClassName == null) {
            return null;
        }
        int index = fullClassName.lastIndexOf(".");
        if (index > 0) {
            return fullClassName.substring(index + 1, fullClassName.length());
        } else {
            return fullClassName;
        }
    }

    public static String extractPackageName(final String fullClassName) {
        if (fullClassName == null) {
            return null;
        }
        int index = fullClassName.lastIndexOf(".");
        if (index > 0) {
            return fullClassName.substring(0, index);
        } else {
            return null;
        }
    }

    public static boolean isPrimitiveTypeClass(final String className) {
        //returns true for: byte, short, int, long, float, double, char, boolean

        return
                Byte.class.getName().equals(className) ||
                        Short.class.getName().equals(className) ||
                        Integer.class.getName().equals(className) ||
                        Long.class.getName().equals(className) ||
                        Float.class.getName().equals(className) ||
                        Double.class.getName().equals(className) ||
                        Character.class.getName().equals(className) ||
                        Boolean.class.getName().equals(className);
    }

    public static boolean isPrimitiveTypeId(final String type) {
        //returns true for: byte, short, int, long, float, double, char, boolean
        return
                BYTE.equals(type) ||
                        SHORT.equals(type) ||
                        INT.equals(type) ||
                        LONG.equals(type) ||
                        FLOAT.equals(type) ||
                        DOUBLE.equals(type) ||
                        CHAR.equals(type) ||
                        BOOLEAN.equals(type);
    }

    public static boolean isByteId(final String type) {
        return BYTE.equals(type != null ? type.trim() : type);
    }

    public static boolean isCharId(final String type) {
        return CHAR.equals(type != null ? type.trim() : type);
    }

    public static boolean isLongId(final String type) {
        return LONG.equals(type != null ? type.trim() : type);
    }

    public static boolean isFloatId(final String type) {
        return FLOAT.equals(type != null ? type.trim() : type);
    }

    public static boolean isDoubleId(final String type) {
        return FLOAT.equals(type != null ? type.trim() : type);
    }

    public static boolean isQualifiedName(final String type) {
        String[] tokens = tokenizeClassName(type);
        return (tokens != null) && (tokens.length > 1);
    }

    public static String[] tokenizeClassName(final String className) {
        String[] result = null;
        if (className != null) {
            result = className.split("\\.");
        }
        return result;
    }

    public static String createQualifiedName(String packageName, String className) {
        if (packageName != null && !"".equals(packageName)) {
            return packageName + "." + className;
        } else {
            return className;
        }
    }

    public static Object parsePrimitiveValue(final String type, final String value) throws NumberFormatException {

        if (value == null) {
            return null;
        }

        if (BYTE.equals(type)) {
            return Byte.valueOf(value);
        }
        if (SHORT.equals(type)) {
            return Short.valueOf(value);
        }
        if (INT.equals(type)) {
            return Integer.valueOf(value);
        }
        if (LONG.equals(type)) {
            return parseLongValue(value);
        }
        if (FLOAT.equals(type)) {
            return parseFloatValue(value);
        }
        if (DOUBLE.equals(type)) {
            return parseDoubleValue(value);
        }
        if (CHAR.equals(type)) {
            return parseCharValue(value);
        }
        if (BOOLEAN.equals(type)) {
            return Boolean.valueOf(value);
        }

        return null;
    }

    public static String parseCharValue(String value) {
        return value;
    }

    public static Long parseLongValue(final String value) {
        String trimmedValue = PortableStringUtils.removeLastChar(value != null ? value.trim() : null, 'L');
        if (trimmedValue == null || "".equals(trimmedValue)) {
            return null;
        }
        return Long.valueOf(trimmedValue);
    }

    public static Float parseFloatValue(final String value) {
        String trimmedValue = PortableStringUtils.removeLastChar(value != null ? value.trim() : null, 'f');
        if (trimmedValue == null || "".equals(trimmedValue)) {
            return null;
        }
        return Float.valueOf(trimmedValue);
    }

    public static Double parseDoubleValue(final String value) {
        String trimmedValue = PortableStringUtils.removeLastChar(value != null ? value.trim() : null, 'd');
        if (trimmedValue == null || "".equals(trimmedValue)) {
            return null;
        }
        return Double.valueOf(trimmedValue);
    }

    public static String normalizeClassName(String className) {
        if (className != null && className.contains("$")) {
            return className.replace("$", ".");
        } else {
            return className;
        }
    }
}