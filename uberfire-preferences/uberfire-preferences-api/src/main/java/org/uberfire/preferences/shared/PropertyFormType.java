/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.preferences.shared;

import com.google.common.graph.ElementOrder.Type;

/**
 * Enum used to define a property type for default form generation purposes.
 */
public enum PropertyFormType {

    TEXT {
        @Override
        public Object fromString(final String stringValue) {
            return stringValue;
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    },
    BOOLEAN {
        @Override
        public Object fromString(final String stringValue) {
            return Boolean.valueOf(stringValue);
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    },
    NATURAL_NUMBER {
        @Override
        public Object fromString(final String stringValue) {
            return Integer.parseInt(stringValue);
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    },
    SECRET_TEXT {
        @Override
        public Object fromString(final String stringValue) {
            return stringValue;
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    },
    COLOR {
        @Override
        public Object fromString(final String stringValue) {
            return stringValue;
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    },
    COMBO {
        @Override
        public Object fromString(final String stringValue) {
            return stringValue;
        }

        @Override
        public String toString(final Object realValue) {
            return String.valueOf(realValue);
        }
    };

    public abstract Object fromString(String stringValue);

    public abstract String toString(Object realValue);
    
}