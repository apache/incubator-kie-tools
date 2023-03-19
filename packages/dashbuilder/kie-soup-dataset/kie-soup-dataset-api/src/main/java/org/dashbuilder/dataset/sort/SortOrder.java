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
package org.dashbuilder.dataset.sort;

/**
 * A sort order
 */
public enum SortOrder {
    ASCENDING,
    DESCENDING,
    UNSPECIFIED;

    private static final String ASC = "asc";
    private static final String DESC = "desc";

    public int asInt() {
        switch (this) {
            case ASCENDING: return 1;
            case DESCENDING: return -1;
            default: return 0;
        }
    }

    public static SortOrder getByName(String str) {
        try {
            if (ASC.equals(str)) return ASCENDING;
            if (DESC.equals(str)) return DESCENDING;
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    public SortOrder reverse() {
        switch (this) {
            case ASCENDING: return SortOrder.DESCENDING;
            case DESCENDING: return SortOrder.ASCENDING;
            default: return SortOrder.UNSPECIFIED;
        }
    }
}
