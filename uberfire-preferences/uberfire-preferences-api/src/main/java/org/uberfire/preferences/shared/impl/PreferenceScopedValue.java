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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.PreferenceScope;

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
    private final PreferenceScope scope;

    public PreferenceScopedValue(@MapsTo("value") final T value,
                                 @MapsTo("scope") final PreferenceScope scope) {
        this.value = value;
        this.scope = scope;
    }

    public T getValue() {
        return value;
    }

    public PreferenceScope getScope() {
        return scope;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PreferenceScopedValue<?> that = (PreferenceScopedValue<?>) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return !(scope != null ? !scope.equals(that.scope) : that.scope != null);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
