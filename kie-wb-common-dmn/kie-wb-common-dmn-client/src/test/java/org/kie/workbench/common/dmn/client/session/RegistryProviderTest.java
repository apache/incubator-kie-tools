/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.session;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RegistryProviderTest {

    @Mock
    private ManagedInstance<CommandRegistryHolder> registryHolders;

    @Mock
    private DMNGraphsProvider graphsProvider;

    private RegistryProvider provider;

    @Before
    public void setup() {
        provider = spy(new RegistryProvider(registryHolders,
                                            graphsProvider));
    }

    @Test
    public void testInitializeRegistry() {

        final String id = "id";
        final Registry registry = mock(Registry.class);

        doReturn(registry).when(provider).createRegistry();

        provider.initializeRegistry(id);

        assertTrue(provider.getRegistryMap().containsKey(id));
        assertEquals(registry, provider.getRegistryMap().get(id));
    }

    @Test
    public void testGetCurrentCommandRegistryWhenItIsInitialized() {

        final String diagramId = "diagram id";
        final Registry registry = mock(Registry.class);

        when(graphsProvider.getCurrentDiagramId()).thenReturn(diagramId);

        provider.getRegistryMap().put(diagramId, registry);

        final Registry currentCommandRegistry = provider.getCurrentCommandRegistry();

        assertEquals(registry, currentCommandRegistry);
        verify(provider, never()).initializeRegistry(diagramId);
    }

    @Test
    public void testGetCurrentCommandRegistryWhenItIsNotPresent() {

        final String diagramId = "diagram id";
        final Registry createdRegistry = mock(Registry.class);

        when(graphsProvider.getCurrentDiagramId()).thenReturn(diagramId);

        doReturn(createdRegistry).when(provider).createRegistry();

        final Registry currentCommandRegistry = provider.getCurrentCommandRegistry();

        assertEquals(createdRegistry, currentCommandRegistry);
        verify(provider).initializeRegistry(diagramId);
    }

    @Test
    public void testCreateRegistry() {

        final CommandRegistryHolder registryHolder = mock(CommandRegistryHolder.class);
        final Registry registry = mock(Registry.class);

        when(registryHolders.get()).thenReturn(registryHolder);
        when(registryHolder.getRegistry()).thenReturn(registry);

        final Registry createdRegistry = provider.createRegistry();

        assertEquals(registry, createdRegistry);
        verify(createdRegistry, never()).setRegistryChangeListener(any());
    }

    @Test
    public void testCreateRegistryWhenRegistryChangeListenerIsSet() {

        final CommandRegistryHolder registryHolder = mock(CommandRegistryHolder.class);
        final Registry registry = mock(Registry.class);
        final RegistryChangeListener listener = mock(RegistryChangeListener.class);

        provider.setRegistryChangeListener(listener);

        when(registryHolders.get()).thenReturn(registryHolder);
        when(registryHolder.getRegistry()).thenReturn(registry);

        final Registry createdRegistry = provider.createRegistry();

        assertEquals(registry, createdRegistry);
        verify(createdRegistry).setRegistryChangeListener(listener);
    }
}
