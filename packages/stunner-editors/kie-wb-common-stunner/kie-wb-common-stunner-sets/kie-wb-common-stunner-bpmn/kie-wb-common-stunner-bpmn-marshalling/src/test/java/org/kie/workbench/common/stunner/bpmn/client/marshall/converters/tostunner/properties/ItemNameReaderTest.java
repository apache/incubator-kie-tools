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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ItemNameReaderTest {

    private static String NAME = "NAME";
    private static String ID = "PARENT_ID";

    @Mock
    private Property property;

    @Mock
    private DataInput dataInput;

    @Mock
    private DataOutput dataOutput;

    @Test
    public void testGetPropertyName() {
        when(property.getName()).thenReturn(NAME);
        when(property.getId()).thenReturn(ID);
        testGetName(NAME, property);
    }

    @Test
    public void testGetPropertyID() {
        when(property.getName()).thenReturn(null);
        when(property.getId()).thenReturn(ID);
        testGetName(ID, property);
    }

    @Test
    public void testGetDataInputName() {
        when(dataInput.getName()).thenReturn(NAME);
        when(dataInput.getId()).thenReturn(ID);
        testGetName(NAME, dataInput);
    }

    @Test
    public void testGetDataInputID() {
        when(dataInput.getName()).thenReturn(null);
        when(dataInput.getId()).thenReturn(ID);
        testGetName(ID, dataInput);
    }

    @Test
    public void testGetDataOutputName() {
        when(dataOutput.getName()).thenReturn(NAME);
        when(dataOutput.getId()).thenReturn(ID);
        testGetName(NAME, dataOutput);
    }

    @Test
    public void testGetDataOutputID() {
        when(dataOutput.getName()).thenReturn(null);
        when(dataOutput.getId()).thenReturn(ID);
        testGetName(ID, dataOutput);
    }

    private void testGetName(String expectedValue, ItemAwareElement element) {
        assertEquals(expectedValue, ItemNameReader.from(element).getName());
    }
}
