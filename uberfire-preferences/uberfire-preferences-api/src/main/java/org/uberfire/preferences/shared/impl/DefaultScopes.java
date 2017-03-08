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

package org.uberfire.preferences.shared.impl;

/**
 * Definition of default scope types that can be used.
 */
public enum DefaultScopes {

    /**
     * Scope type responsible to store preferences for all users.
     */
    ALL_USERS("all-users"),

    /**
     * Scope type responsible to store preferences for the entire application.
     */
    ENTIRE_APPLICATION("entire-application"),

    /**
     * Scope type responsible to store preferences separately for each component.
     */
    COMPONENT("component"),

    /**
     * Scope type responsible to store preferences separately for each username.
     */
    USER("user");

    private final String type;

    DefaultScopes(final String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
