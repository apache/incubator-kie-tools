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

package org.guvnor.common.services.backend.preferences;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.guvnor.common.services.shared.preferences.DefaultWorkbenchPreferenceScopeResolutionStrategies;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;

public class WorkbenchPreferenceScopeResolutionStrategiesProducer {

    @Inject
    private Instance<WorkbenchPreferenceScopeResolutionStrategies> workbenchPreferenceScopeResolutionStrategies;

    @Inject
    @Customizable
    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private DefaultWorkbenchPreferenceScopeResolutionStrategies defaultWorkbenchPreferenceScopeResolutionStrategies = null;

    @Produces
    @Customizable
    public WorkbenchPreferenceScopeResolutionStrategies workbenchPreferenceScopeResolutionStrategiesProducer(final InjectionPoint ip) {
        if (this.workbenchPreferenceScopeResolutionStrategies.isUnsatisfied()) {
            return new DefaultWorkbenchPreferenceScopeResolutionStrategies(preferenceScopeResolutionStrategy);
        }

        return this.workbenchPreferenceScopeResolutionStrategies.get();
    }
}
