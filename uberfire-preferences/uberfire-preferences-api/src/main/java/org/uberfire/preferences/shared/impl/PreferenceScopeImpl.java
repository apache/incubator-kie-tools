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
 * Default portable implementation for a preference scope, containing a type (required), a key (required)
 * and a child scope (optional).
 */
@Portable
public class PreferenceScopeImpl implements PreferenceScope {

    private final String type;

    private final String key;

    private final PreferenceScope childScope;

    public PreferenceScopeImpl(@MapsTo("type") final String type,
                               @MapsTo("key") final String key,
                               @MapsTo("childScope") final PreferenceScope childScope) {
        this.type = type;
        this.key = key;
        this.childScope = childScope;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public PreferenceScope childScope() {
        return childScope;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PreferenceScopeImpl that = (PreferenceScopeImpl) o;

        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }
        return !(childScope != null ? !childScope.equals(that.childScope) : that.childScope != null);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (childScope != null ? childScope.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
