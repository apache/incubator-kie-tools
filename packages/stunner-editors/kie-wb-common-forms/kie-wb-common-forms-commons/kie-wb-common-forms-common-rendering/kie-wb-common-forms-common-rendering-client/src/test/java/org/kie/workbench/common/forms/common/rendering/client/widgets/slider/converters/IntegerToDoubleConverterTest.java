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


package org.kie.workbench.common.forms.common.rendering.client.widgets.slider.converters;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class IntegerToDoubleConverterTest {

    public static final Double DOUBLE_VALUE = 12345d;
    public static final Integer INTEGER_VALUE = 12345;

    protected IntegerToDoubleConverter converter;

    @Before
    public void setup() {
        converter = new IntegerToDoubleConverter();
    }

    @Test
    public void testGetModelType() {
        Class modelType = converter.getModelType();
        assertEquals(Integer.class,
                     modelType);
    }

    @Test
    public void testGetComponentType() {
        Class componentType = converter.getComponentType();
        assertEquals(Double.class,
                     componentType);
    }

    @Test
    public void testToModelValue() {
        Integer modelValue = converter.toModelValue(DOUBLE_VALUE);
        assertEquals(INTEGER_VALUE,
                     modelValue);
    }

    @Test
    public void testToWidgetValue() {
        Double widgetValue = converter.toWidgetValue(INTEGER_VALUE);
        assertEquals(DOUBLE_VALUE,
                     widgetValue);
    }
}
