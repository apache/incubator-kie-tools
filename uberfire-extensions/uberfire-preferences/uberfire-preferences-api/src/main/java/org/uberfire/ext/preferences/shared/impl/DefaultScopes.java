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

package org.uberfire.ext.preferences.shared.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of default scope types that can be used.
 */
public enum DefaultScopes {

    /**
     * Define a global scope type, responsible to store preferences for all users.
     */
    GLOBAL( "global" ),

    /**
     * Define a user scope type, responsible to store preferences for users, individually.
     */
    USER( "user" );

    private final String type;

    DefaultScopes( final String type ) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    /**
     * Returns all default scope types.
     * @return Scope types.
     */
    public static List<String> allTypes() {
        List<String> types = new ArrayList<>();

        for ( DefaultScopes scopeType : values() ) {
            types.add( scopeType.type() );
        }

        return types;
    }
}
