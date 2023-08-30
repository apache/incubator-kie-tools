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


package org.kie.workbench.common.forms.dynamic.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.rendering.FieldRendererTypesProvider;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DynamicRendererEntryPointTest {

    @Mock
    private SyncBeanManager beanManager;

    private DynamicRendererEntryPoint entryPoint;

    private List<FieldRendererTypesProvider> rendererTypesProviders = new ArrayList<>();

    @Before
    public void init() {

        prepareFieldRendererProviders();

        entryPoint = new DynamicRendererEntryPoint(beanManager);

        entryPoint.init();
    }

    @Test
    public void testPopulateFieldRendererProviders() {
        verify(beanManager).lookupBeans(eq(FieldRendererTypesProvider.class));

        rendererTypesProviders.forEach(provider -> {
            verify(provider).getFieldTypeRenderers();
            verify(provider).getFieldDefinitionRenderers();
            verify(beanManager).destroyBean(provider);
        });
    }

    private void prepareFieldRendererProviders() {
        List<SyncBeanDef<FieldRendererTypesProvider>> beanDefs = new ArrayList<>();

        beanDefs.add(newProvider());
        beanDefs.add(newProvider());
        beanDefs.add(newProvider());

        when(beanManager.lookupBeans(eq(FieldRendererTypesProvider.class))).thenReturn(beanDefs);
    }

    private SyncBeanDef<FieldRendererTypesProvider> newProvider() {
        FieldRendererTypesProvider provider = mock(FieldRendererTypesProvider.class);

        rendererTypesProviders.add(provider);

        when(provider.getFieldTypeRenderers()).thenReturn(new HashMap<>());
        when(provider.getFieldDefinitionRenderers()).thenReturn(new HashMap<>());


        SyncBeanDef<FieldRendererTypesProvider> beanDef = mock(SyncBeanDef.class);
        when(beanDef.newInstance()).thenReturn(provider);

        return beanDef;
    }
}
