/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.backend.service.handler;

import javax.enterprise.inject.Instance;

import org.guvnor.ala.ui.handler.AbstractProviderHandlerRegistryTest;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BackendProviderHandlerRegistryTest
        extends AbstractProviderHandlerRegistryTest<BackendProviderHandler> {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Override
    protected BackendProviderHandlerRegistry createHandlerRegistry(Instance<BackendProviderHandler> handlerInstance) {
        return new BackendProviderHandlerRegistry(handlerInstance);
    }

    @Override
    protected BackendProviderHandler mockHandler() {
        return mock(BackendProviderHandler.class);
    }

    @Test
    public void testEnsureHandlerForRegisteredProviderType() throws Exception {
        //pick an arbitrary registered provider type.
        ProviderTypeKey providerTypeKey = expectedKeys.get(0);
        BackendProviderHandler handler = ((BackendProviderHandlerRegistry) handlerRegistry).ensureHandler(providerTypeKey);
        assertEquals(expectedHandlers.get(0),
                     handler);
    }

    @Test
    public void testEnsureHandlerForNonRegisteredProviderType() throws Exception {
        //pick an arbitrary non registered provider type.
        ProviderTypeKey providerTypeKey = mock(ProviderTypeKey.class);
        expectedException.expectMessage("BackendProviderHandler was not found for providerTypeKey: " + providerTypeKey);
        ((BackendProviderHandlerRegistry) handlerRegistry).ensureHandler(providerTypeKey);
    }
}
