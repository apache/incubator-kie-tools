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


package org.kie.workbench.common.forms.common.rendering.client.util.valueConverters;

import org.jboss.errai.databinding.client.api.Converter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public abstract class AbstractConverterTest<MODEL_VALUE, WIDGET_VALUE> {

    protected Converter converter;

    @Before
    public void init() {
        converter = ValueConvertersFactory.getConverterForType(getConverterTye().getName());

        assertNotNull(converter);
        assertNotNull(converter.getComponentType());
        assertNotNull(converter.getModelType());
    }

    @Test
    public void testToNullWidgetValue() {
        Object value = converter.toWidgetValue(null);
        assertNull(value);
    }

    @Test
    public void testToNullModelValue() {
        Object value = converter.toModelValue(null);
        assertNull(value);
    }

    @Test
    public void testToModelValue() {
        testToModelValue(getWidgetValue(),
                         getModelValue());
    }

    @Test
    public void testToWidgetValue() {
        testToWidgetValue(getModelValue(),
                          getWidgetValue());
    }

    protected void testToModelValue(WIDGET_VALUE widgetValue,
                                    MODEL_VALUE modelValue) {
        assertNotNull(widgetValue);

        Object resultModelValue = converter.toModelValue(widgetValue);

        assertNotNull(resultModelValue);

        assertEquals(resultModelValue.getClass(),
                     converter.getModelType());

        assertEquals(modelValue,
                     resultModelValue);
    }

    protected void testToWidgetValue(MODEL_VALUE modelValue,
                                     WIDGET_VALUE widgetValue) {
        assertNotNull(modelValue);

        Object resultWidgetValue = converter.toWidgetValue(modelValue);

        assertNotNull(resultWidgetValue);

        assertEquals(resultWidgetValue.getClass(),
                     converter.getComponentType());

        assertEquals(widgetValue,
                     resultWidgetValue);
    }

    abstract Class<MODEL_VALUE> getConverterTye();

    abstract MODEL_VALUE getModelValue();

    abstract WIDGET_VALUE getWidgetValue();
}
