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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.string;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StringSelectorTest {

    @Mock
    private BaseSelector.View view;

    private StringSelector stringSelector;

    @Before
    public void setup() {
        stringSelector = new StringSelector(view);
    }

    @Test
    public void testSetValue() {
        stringSelector.setValue("\"value\"");
        verify(view).setValue("value");
    }

    @Test
    public void testSetEmptyValue() {
        stringSelector.setValue("");
        verify(view).setValue("");
    }

    @Test
    public void testSetNullValue() {
        stringSelector.setValue(null);
        verify(view).setValue("");
    }

    @Test
    public void testSetValueWithIntermediateDoubleQuote() {
        stringSelector.setValue("\"va\"lue\"");
        verify(view).setValue("va\"lue");
    }

    @Test
    public void testSetValueWithMultipleDoubleQuote() {
        stringSelector.setValue("\"\"value\"\"");
        verify(view).setValue("\"value\"");
    }

    @Test
    public void testGetValueWithRawValue() {

        when(view.getValue()).thenReturn("value");

        final String expected = "\"value\"";
        final String actual = stringSelector.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueWithQuotedValue() {

        when(view.getValue()).thenReturn("\"value\"");

        final String expected = "\"value\"";
        final String actual = stringSelector.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueWithBlankValue() {

        when(view.getValue()).thenReturn("");

        final String expected = "";
        final String actual = stringSelector.getValue();

        assertEquals(expected, actual);
    }
}
