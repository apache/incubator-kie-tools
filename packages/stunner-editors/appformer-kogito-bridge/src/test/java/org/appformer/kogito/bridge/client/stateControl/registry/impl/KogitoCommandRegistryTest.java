/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.appformer.kogito.bridge.client.stateControl.registry.impl;

import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.appformer.kogito.bridge.client.stateControl.registry.interop.KogitoJSCommandRegistry;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KogitoCommandRegistryTest {

    private boolean envelopeEnabled = true;

    @Mock
    private RegistryChangeListener registryChangeListener;

    @Mock
    private KogitoJSCommandRegistry<Object> kogitoJSCommandRegistry;

    private KogitoCommandRegistry<Object> commandRegistry;

    @Before
    public void setUp() {
        when(kogitoJSCommandRegistry.getCommands()).thenReturn(new Object[]{});
        when(kogitoJSCommandRegistry.pop()).thenReturn(new Object());
        when(kogitoJSCommandRegistry.peek()).thenReturn(new Object());

        commandRegistry = new KogitoCommandRegistry<>(() -> envelopeEnabled, () -> kogitoJSCommandRegistry);
        commandRegistry.setRegistryChangeListener(registryChangeListener);
    }

    @Test
    public void testBuildOutsideEnvelope() {
        this.envelopeEnabled = false;

        Assertions.assertThatThrownBy(() -> new KogitoCommandRegistry<>(() -> envelopeEnabled, () -> kogitoJSCommandRegistry))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Envelope isn't present, we shouldn't be here!");
    }

    @Test
    public void testRegisterCommand() {
        commandRegistry.register(new Object());
        verify(kogitoJSCommandRegistry).register(anyString(), anyObject());
        verify(registryChangeListener).notifyRegistryChange();
    }

    @Test
    public void testPeek() {
        commandRegistry.peek();
        verify(kogitoJSCommandRegistry).peek();
    }

    @Test
    public void testPop() {
        commandRegistry.pop();
        verify(kogitoJSCommandRegistry).pop();
        verify(registryChangeListener).notifyRegistryChange();
    }

    @Test
    public void testClear() {
        commandRegistry.clear();
        verify(kogitoJSCommandRegistry).clear();
        verify(registryChangeListener).notifyRegistryChange();
    }

    @Test
    public void testIsEmpty() {
        commandRegistry.isEmpty();
        verify(kogitoJSCommandRegistry).isEmpty();
    }

    @Test
    public void testGetCommandsHistory() {
        commandRegistry.getHistory();
        verify(kogitoJSCommandRegistry).getCommands();
    }

    @Test
    public void testSetMaxSize() {
        commandRegistry.setMaxSize(1);
        verify(kogitoJSCommandRegistry).setMaxSize(eq(1));
    }

    @Test
    public void testSettingWrongMaxSize() {
        Assertions.assertThatThrownBy(() -> commandRegistry.setMaxSize(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The registry size should be a positive number");
    }
}
