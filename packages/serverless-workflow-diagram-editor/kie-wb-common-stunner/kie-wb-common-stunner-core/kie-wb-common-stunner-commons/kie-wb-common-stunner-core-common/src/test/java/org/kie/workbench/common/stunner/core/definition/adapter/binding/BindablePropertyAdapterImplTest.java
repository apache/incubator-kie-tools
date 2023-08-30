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


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BindablePropertyAdapterImplTest {

    private static final BindableTestProperty1 PROPERTY_1 = new BindableTestProperty1();
    private static final BindableTestProperty2 PROPERTY_2 = new BindableTestProperty2();

    private BindablePropertyAdapterImpl<Object, Object> tested;

    @Mock
    private StunnerTranslationService translationService;

    @Mock
    private BindableAdapterFunctions functions;

    @Before
    public void setUp() {
        tested = BindablePropertyAdapterImpl.create(translationService, functions);
        tested.addBinding(BindableTestProperty1.class, "value");
        tested.addBinding(BindableTestProperty2.class, "someValue");
    }

    @Test
    public void testGetId() {
        String id1 = tested.getId(PROPERTY_1);
        assertEquals(BindableTestProperty1.class.getName(), id1);
        String id2 = tested.getId(PROPERTY_2);
        assertEquals(BindableTestProperty2.class.getName(), id2);
    }

    @Test
    public void testGetCaption() {
        when(translationService.getPropertyCaption(BindableTestProperty1.class.getName()))
                .thenReturn("p1");
        when(translationService.getPropertyCaption(BindableTestProperty2.class.getName()))
                .thenReturn("p2");
        String caption1 = tested.getCaption(PROPERTY_1);
        String caption2 = tested.getCaption(PROPERTY_2);
        assertEquals("p1", caption1);
        assertEquals("p2", caption2);
    }

    @Test
    public void testGetValue() {
        when(functions.getValue(PROPERTY_1, "value")).thenReturn("p1Value");
        when(functions.getValue(PROPERTY_2, "someValue")).thenReturn("p2Value");
        Object value1 = tested.getValue(PROPERTY_1);
        Object value2 = tested.getValue(PROPERTY_2);
        assertEquals("p1Value", value1);
        assertEquals("p2Value", value2);
    }

    @Test
    public void testSetValue() {
        tested.setValue(PROPERTY_1, "newValue1");
        tested.setValue(PROPERTY_2, "newValue2");
        verify(functions, times(1)).setValue(eq(PROPERTY_1), eq("value"), eq("newValue1"));
        verify(functions, times(1)).setValue(eq(PROPERTY_2), eq("someValue"), eq("newValue2"));
    }
}
