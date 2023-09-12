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


package org.kie.workbench.common.stunner.core.definition.adapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionAdapterWrapperTest {

    private DefinitionAdapterWrapper<Object, DefinitionAdapter<Object>> tested;

    @Mock
    private BindableDefinitionAdapter<Object> wrapped;

    @Mock
    private Object pojo;

    @Before
    @SuppressWarnings("all")
    public void setUp() {
        tested = new DefinitionAdapterWrapper(wrapped) {
        };
    }

    @Test
    public void testGetId() {
        tested.getId(pojo);
        verify(wrapped, times(1)).getId(eq(pojo));
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getTitle(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetCategory() {
        tested.getCategory(pojo);
        verify(wrapped, times(1)).getCategory(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getTitle(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetTitle() {
        tested.getTitle(pojo);
        verify(wrapped, times(1)).getTitle(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetDescription() {
        tested.getDescription(pojo);
        verify(wrapped, times(1)).getDescription(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetLabels() {
        tested.getLabels(pojo);
        verify(wrapped, times(1)).getLabels(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetPropertyFields() {
        tested.getPropertyFields(pojo);
        verify(wrapped, times(1)).getPropertyFields(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetProperty() {
        tested.getProperty(pojo, "someField");
        verify(wrapped, times(1)).getProperty(eq(pojo), eq("someField"));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetGraphFactory() {
        tested.getGraphFactoryType(pojo);
        verify(wrapped, times(1)).getGraphFactoryType(eq(pojo));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetMetaPropertyField() {
        tested.getMetaPropertyField(pojo, PropertyMetaTypes.NAME);
        verify(wrapped, times(1)).getMetaPropertyField(eq(pojo), eq(PropertyMetaTypes.NAME));
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
    }

    @Test
    public void testAccepts() {
        tested.accepts(any());
        verify(wrapped, times(1)).accepts(any());
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }

    @Test
    public void testGetPriority() {
        tested.getPriority();
        verify(wrapped, times(1)).getPriority();
        verify(wrapped, never()).getId(any());
        verify(wrapped, never()).getCategory(any());
        verify(wrapped, never()).getDescription(any());
        verify(wrapped, never()).getLabels(any());
        verify(wrapped, never()).getPropertyFields(any());
        verify(wrapped, never()).getProperty(any(), any());
        verify(wrapped, never()).getGraphFactory(any());
        verify(wrapped, never()).getMetaPropertyField(any(), any());
    }
}
