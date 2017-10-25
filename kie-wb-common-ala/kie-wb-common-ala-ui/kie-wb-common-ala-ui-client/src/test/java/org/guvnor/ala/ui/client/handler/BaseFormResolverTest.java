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

package org.guvnor.ala.ui.client.handler;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseFormResolverTest<T extends ProviderConfigurationForm> {

    protected FormResolver<T> formResolver;

    @Mock
    protected ManagedInstance<T> managedInstance;

    @Mock
    protected T form;

    @Before
    public void setUp() {
        when(managedInstance.get()).thenReturn(form);
        formResolver = createFormResolver(managedInstance);
    }

    @Test
    public void testNewProviderConfigurationForm() {
        T result = formResolver.newProviderConfigurationForm();
        assertEquals(form,
                     result);
        verify(managedInstance,
               times(1)).get();
    }

    @Test
    public void testDestroyForm() {
        formResolver.destroyForm(form);
        verify(managedInstance,
               times(1)).destroy(form);
    }

    @Test
    public void testDestroyNullForm() {
        formResolver.destroyForm(null);
        verify(managedInstance,
               never()).destroy(anyObject());
    }

    protected abstract FormResolver<T> createFormResolver(ManagedInstance<T> managedInstance);
}