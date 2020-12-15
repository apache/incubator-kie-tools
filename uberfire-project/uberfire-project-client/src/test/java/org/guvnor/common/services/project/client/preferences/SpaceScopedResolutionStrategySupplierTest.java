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

import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SpaceScopedResolutionStrategySupplierTest {

    private SpaceScopedResolutionStrategySupplier strategySupplier;

    @Mock
    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    @Before
    public void setup() {
        strategySupplier = spy(new SpaceScopedResolutionStrategySupplier(scopeResolutionStrategies));
    }

    @Test
    public void getWithoutOrganizationalUnitTest() {
        final PreferenceScopeResolutionStrategyInfo strategyInfo = strategySupplier.get();

        verify(scopeResolutionStrategies).getSpaceInfoFor(null);
    }

    @Test
    public void getWithOrganizationalUnitTest() {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn("identifier").when(organizationalUnit).getIdentifier();

        final WorkspaceProjectContextChangeEvent event = mock(WorkspaceProjectContextChangeEvent.class);
        doReturn(organizationalUnit).when(event).getOrganizationalUnit();

        strategySupplier.onWorkspaceProjectContextChangeEvent(event);
        final PreferenceScopeResolutionStrategyInfo strategyInfo = strategySupplier.get();

        verify(scopeResolutionStrategies).getSpaceInfoFor("identifier");
    }
}
