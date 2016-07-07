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

package org.uberfire.ext.preferences.shared;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeImpl;

/**
 * Represents a preference value and its scope.
 * @param <T> Type of the preference value.
 */
@Portable
public class PreferenceScopedValue<T> {

    /**
     * Typed preference value.
     */
    private final T value;

    /**
     * Scope under which this preference value is defined.
     */
    private final PreferenceScopeImpl scope;

    public PreferenceScopedValue( @MapsTo( "value" ) final T value,
                                  @MapsTo( "scopeType" ) final String scopeType,
                                  @MapsTo( "scopeKey" ) final String scopeKey ) {
        this.value = value;

        if ( scopeType != null ) {
            this.scope = new PreferenceScopeImpl( scopeType, scopeKey );
        } else {
            this.scope = null;
        }
    }

    public T getValue() {
        return value;
    }

    public PreferenceScope getScope() {
        return scope;
    }
}
