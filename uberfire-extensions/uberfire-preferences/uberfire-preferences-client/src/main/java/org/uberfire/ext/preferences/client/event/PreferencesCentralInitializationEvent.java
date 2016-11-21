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

import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public class PreferencesCentralInitializationEvent {

    private String preferenceIdentifier;

    private PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy;

    public PreferencesCentralInitializationEvent( final String preferenceIdentifier ) {
        this.preferenceIdentifier = preferenceIdentifier;
        this.customScopeResolutionStrategy = null;
    }

    public PreferencesCentralInitializationEvent( final String preferenceIdentifier,
                                                  final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy ) {
        this.preferenceIdentifier = preferenceIdentifier;
        this.customScopeResolutionStrategy = customScopeResolutionStrategy;
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

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PreferencesCentralInitializationEvent ) ) {
            return false;
        }

        final PreferencesCentralInitializationEvent that = (PreferencesCentralInitializationEvent) o;

        if ( preferenceIdentifier != null ? !preferenceIdentifier.equals( that.preferenceIdentifier ) : that.preferenceIdentifier != null ) {
            return false;
        }
        return !( customScopeResolutionStrategy != null ? !customScopeResolutionStrategy.equals( that.customScopeResolutionStrategy ) : that.customScopeResolutionStrategy != null );

    }

    @Override
    public int hashCode() {
        int result = preferenceIdentifier != null ? preferenceIdentifier.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( customScopeResolutionStrategy != null ? customScopeResolutionStrategy.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
