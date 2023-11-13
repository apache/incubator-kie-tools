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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.inject.Instance;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextPropertyProviderFactoryTest {

    @Mock
    private Element element;

    private TextPropertyProviderFactory factory;

    private TextPropertyProvider provider1 = makeTextPropertyProvider(2);
    private TextPropertyProvider provider2 = makeTextPropertyProvider(1);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final Instance<TextPropertyProvider> providers = new MockInstanceImpl<>(provider1,
                                                                                provider2);
        final ManagedInstance<TextPropertyProvider> managedProviders = mock(ManagedInstance.class);
        when(managedProviders.iterator()).thenReturn(providers.iterator());

        this.factory = new TextPropertyProviderFactoryImpl(managedProviders);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkFactoryOrdersProvidersByPriorityAscending() {
        final TextPropertyProvider provider = factory.getProvider(element);
        assertEquals(provider2,
                     provider);
    }

    @SuppressWarnings("unchecked")
    private TextPropertyProvider makeTextPropertyProvider(final int priority) {
        final TextPropertyProvider provider = mock(TextPropertyProvider.class);
        when(provider.supports(any(Element.class))).thenReturn(true);
        when(provider.getPriority()).thenReturn(priority);
        return provider;
    }
}
