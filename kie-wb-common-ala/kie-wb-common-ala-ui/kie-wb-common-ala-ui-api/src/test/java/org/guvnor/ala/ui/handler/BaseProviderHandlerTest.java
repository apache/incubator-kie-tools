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

package org.guvnor.ala.ui.handler;

import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseProviderHandlerTest<T extends ProviderHandler> {

    protected T providerHandler;

    @Before
    public void setUp() {
        providerHandler = createProviderHandler();
    }

    @Test
    public void testGetPriority() {
        assertEquals(expectedPriority(),
                     providerHandler.getPriority());
    }

    @Test
    public void testAcceptProviderType() {
        assertTrue(providerHandler.acceptProviderType(expectedProviderType()));
    }

    @Test
    public void testAcceptArbitraryProviderType() {
        ProviderTypeKey providerTypeKey = mock(ProviderTypeKey.class);
        assertFalse(providerHandler.acceptProviderType(providerTypeKey));
    }

    protected abstract String getProviderTypeName();

    protected ProviderTypeKey expectedProviderType() {
        ProviderTypeKey providerTypeKey = mock(ProviderTypeKey.class);
        when(providerTypeKey.getId()).thenReturn(getProviderTypeName());
        return providerTypeKey;
    }

    protected abstract T createProviderHandler();

    protected abstract int expectedPriority();
}
