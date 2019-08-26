/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

import org.kie.soup.project.datamodel.oracle.DataType;

/**
 * Translates Java's Type System to Guvnor's Type System
 */
public class JavaTypeSystemTranslator implements ClassToGenericClassConverter {

    //Convert Java's Type system into a the portable Type system used by Guvnor (that is GWT friendly)
    @Override
    public String translateClassToGenericType(final Class<?> type) {
        String fieldType = null; // if null, will use standard operators
        if (type != null) {
            if (type.isPrimitive()) {
                if (type == byte.class) {
                    fieldType = DataType.TYPE_NUMERIC_BYTE;
                } else if (type == double.class) {
                    fieldType = DataType.TYPE_NUMERIC_DOUBLE;
                } else if (type == float.class) {
                    fieldType = DataType.TYPE_NUMERIC_FLOAT;
                } else if (type == int.class) {
                    fieldType = DataType.TYPE_NUMERIC_INTEGER;
                } else if (type == long.class) {
                    fieldType = DataType.TYPE_NUMERIC_LONG;
                } else if (type == short.class) {
                    fieldType = DataType.TYPE_NUMERIC_SHORT;
                } else if (type == boolean.class) {
                    fieldType = DataType.TYPE_BOOLEAN;
                } else if (type == char.class) {
                    fieldType = DataType.TYPE_STRING;
                } else if (type == void.class) {
                    fieldType = DataType.TYPE_VOID;
                }
            } else if (BigDecimal.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_BIGDECIMAL;
            } else if (BigInteger.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_BIGINTEGER;
            } else if (Byte.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_BYTE;
            } else if (Double.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_DOUBLE;
            } else if (Float.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_FLOAT;
            } else if (Integer.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_INTEGER;
            } else if (Long.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_LONG;
            } else if (Short.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_NUMERIC_SHORT;
            } else if (Boolean.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_BOOLEAN;
            } else if (String.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_STRING;
            } else if (Collection.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_COLLECTION;
            } else if (Date.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_DATE;
            } else if (LocalDate.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_LOCAL_DATE;
            } else if (Comparable.class.isAssignableFrom(type)) {
                fieldType = DataType.TYPE_COMPARABLE;
            } else {
                fieldType = type.getName();
            }
        }
        return fieldType;
    }
}
