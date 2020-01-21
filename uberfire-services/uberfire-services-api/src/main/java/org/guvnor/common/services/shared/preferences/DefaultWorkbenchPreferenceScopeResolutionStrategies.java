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

package org.guvnor.common.services.shared.preferences;

import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@FallbackImplementation
public class DefaultWorkbenchPreferenceScopeResolutionStrategies implements WorkbenchPreferenceScopeResolutionStrategies {

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    public DefaultWorkbenchPreferenceScopeResolutionStrategies() {
    }

    public DefaultWorkbenchPreferenceScopeResolutionStrategies(final PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy) {
        this.preferenceScopeResolutionStrategy = preferenceScopeResolutionStrategy;
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getUserInfoFor(final String scopeType,
                                                                final String scopeKey) {
        return preferenceScopeResolutionStrategy.getInfo();
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo getSpaceInfoFor(final String scopeKey) {
        return preferenceScopeResolutionStrategy.getInfo();
    }
}
