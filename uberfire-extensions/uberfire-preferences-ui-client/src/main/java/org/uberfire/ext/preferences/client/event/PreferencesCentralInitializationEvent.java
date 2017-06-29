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

package org.uberfire.ext.preferences.client.event;

import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public class PreferencesCentralInitializationEvent {

    private String preferenceIdentifier;

    private PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy;

    private PreferenceScope preferenceScope;

    public PreferencesCentralInitializationEvent(final String preferenceIdentifier,
                                                 final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                                                 final PreferenceScope preferenceScope) {
        this.preferenceIdentifier = preferenceIdentifier;
        this.customScopeResolutionStrategy = customScopeResolutionStrategy;
        this.preferenceScope = preferenceScope;
    }

    public String getPreferenceIdentifier() {
        return preferenceIdentifier;
    }

    public boolean isUseCustomScopeResolutionStrategy() {
        return customScopeResolutionStrategy != null;
    }

    public PreferenceScopeResolutionStrategyInfo getCustomScopeResolutionStrategy() {
        return customScopeResolutionStrategy;
    }

    public PreferenceScope getPreferenceScope() {
        return preferenceScope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PreferencesCentralInitializationEvent)) {
            return false;
        }

        PreferencesCentralInitializationEvent that = (PreferencesCentralInitializationEvent) o;

        if (preferenceIdentifier != null ? !preferenceIdentifier.equals(that.preferenceIdentifier) : that.preferenceIdentifier != null) {
            return false;
        }
        if (customScopeResolutionStrategy != null ? !customScopeResolutionStrategy.equals(that.customScopeResolutionStrategy) : that.customScopeResolutionStrategy != null) {
            return false;
        }
        return !(preferenceScope != null ? !preferenceScope.equals(that.preferenceScope) : that.preferenceScope != null);
    }

    @Override
    public int hashCode() {
        int result = preferenceIdentifier != null ? preferenceIdentifier.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (customScopeResolutionStrategy != null ? customScopeResolutionStrategy.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (preferenceScope != null ? preferenceScope.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
