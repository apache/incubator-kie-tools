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

package org.kie.workbench.common.stunner.bpmn.integration.client;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationHandlerProviderTest {

    @Mock
    private Instance<IntegrationHandler> integrationHandlerInstance;

    private List<IntegrationHandler> integrationHandlers = new ArrayList<>();

    private IntegrationHandlerProvider integrationHandlerProvider;

    @Test
    public void testIntegrationHandlerProviderWhenIntegrationIsPresent() {
        IntegrationHandler integrationHandler = mock(IntegrationHandler.class);
        integrationHandlers.add(integrationHandler);
        when(integrationHandlerInstance.iterator()).thenReturn(integrationHandlers.iterator());
        integrationHandlerProvider = new IntegrationHandlerProvider(integrationHandlerInstance);
        assertTrue(integrationHandlerProvider.getIntegrationHandler().isPresent());
        assertEquals(integrationHandler, integrationHandlerProvider.getIntegrationHandler().get());
    }

    @Test
    public void testIntegrationHandlerProviderWhenIntegrationIsNotPresent() {
        when(integrationHandlerInstance.iterator()).thenReturn(integrationHandlers.iterator());
        integrationHandlerProvider = new IntegrationHandlerProvider(integrationHandlerInstance);
        assertFalse(integrationHandlerProvider.getIntegrationHandler().isPresent());
    }
}
