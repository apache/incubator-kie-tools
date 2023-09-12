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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import org.eclipse.bpmn2.BaseElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.elements.ElementDefinition;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class CustomElementTest {

    private ElementDefinition<Object> elementDefinition;

    private Object defaultValue = new Object();

    @Mock
    private BaseElement baseElement;

    @Before
    public void setUp() {
        elementDefinition = new ElementDefinitionStub("someDefinition", defaultValue);
    }

    @Test
    public void testGet() {
        Object value = new Object();
        elementDefinition.setValue(baseElement, value);
        assertEquals(value, new CustomElement<>(elementDefinition, baseElement).get());
    }

    @Test
    public void testSetNonDefaultValue() {
        Object newValue = new Object();
        CustomElement<Object> customElement = new CustomElement<>(elementDefinition, baseElement);
        customElement.set(newValue);
        assertEquals(newValue, elementDefinition.getValue(baseElement));
    }

    @Test
    public void testSetDefaultValue() {
        Object currentValue = elementDefinition.getValue(baseElement);
        CustomElement<Object> customElement = new CustomElement<>(elementDefinition, baseElement);
        customElement.set(defaultValue);
        assertNotEquals(defaultValue, customElement.get());
        assertEquals(currentValue, customElement.get());
    }

    private class ElementDefinitionStub extends ElementDefinition<Object> {

        private Object value;

        public ElementDefinitionStub(String name, Object defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public Object getValue(BaseElement element) {
            return value;
        }

        @Override
        public void setValue(BaseElement element, Object value) {
            this.value = value;
        }

        @Override
        protected void setStringValue(BaseElement element, String value) {

        }
    }
}
