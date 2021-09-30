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
package org.dashbuilder.dataset;

/**
 * An enumeration for the different types of DataColumn.
 */
public enum ColumnType {

    /**
     * A Date type column.
     */
    DATE,

    /**
     * A Number type column.
     */
    NUMBER,

    /**
     * A Label type column.
     */
    LABEL,

    /**
     * Text based column not eligible for grouping operations.
     */
    TEXT;

    public static ColumnType getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
