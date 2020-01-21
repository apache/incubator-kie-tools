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

package org.guvnor.common.services.project.client.preferences;

import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@ApplicationScoped
public class SpaceScopedResolutionStrategySupplier implements Supplier<PreferenceScopeResolutionStrategyInfo> {

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    private OrganizationalUnit organizationalUnit;

    public SpaceScopedResolutionStrategySupplier() {
    }

    @Inject
    public SpaceScopedResolutionStrategySupplier(final WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies) {
        this.scopeResolutionStrategies = scopeResolutionStrategies;
    }

    public void onWorkspaceProjectContextChangeEvent(@Observes final WorkspaceProjectContextChangeEvent event) {
        this.organizationalUnit = event.getOrganizationalUnit();
    }

    @Override
    public PreferenceScopeResolutionStrategyInfo get() {
        final String scopeKey = organizationalUnit != null ? organizationalUnit.getIdentifier() : null;
        return scopeResolutionStrategies.getSpaceInfoFor(scopeKey);
    }
}
