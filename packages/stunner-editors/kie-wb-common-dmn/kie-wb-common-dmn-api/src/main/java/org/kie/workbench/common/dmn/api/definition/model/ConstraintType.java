/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.Optional;

public enum ConstraintType {

    ENUMERATION,
    EXPRESSION,
    RANGE,
    NONE;

    public static final String CONSTRAINT_KEY = "constraintType";

    public static ConstraintType fromString(final String value) {
        try {
            return valueOf(upperCase(value));
        } catch (final IllegalArgumentException e) {
            return NONE;
        }
    }

    public String value() {
        return name().toLowerCase();
    }

    private static String upperCase(final String value) {
        return Optional
                .ofNullable(value)
                .orElse("")
                .toUpperCase();
    }
}
