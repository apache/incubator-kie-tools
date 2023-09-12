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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.slider;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType.MAX_PARAM;
import static org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType.MIN_PARAM;
import static org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType.PRECISION_PARAM;
import static org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType.STEP_PARAM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractSliderFieldInitializerTest<INITIALIZER extends AbstractSliderFieldInitializer<FIELD, T>, FIELD extends SliderBaseDefinition<T>, T extends Number> {

    private static final String MIN = "0";
    private static final String MAX = "5";
    private static final String STEP = "1";
    private static final String PRECISION = "0";

    protected FIELD field;

    @Mock
    protected FieldElement fieldElement;

    @Mock
    protected FormGenerationContext context;

    protected Map<String, String> fieldElementParams = new HashMap<>();

    protected INITIALIZER initializer;

    @Before
    public void init() {
        initializer = getInitializerInstance();
        field = spy(getFieldInstance());
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
    }

    @Test
    public void testInitializeWithParams() {
        fieldElementParams.put(MIN_PARAM,
                               MIN);
        fieldElementParams.put(MAX_PARAM,
                               MAX);
        fieldElementParams.put(STEP_PARAM,
                               STEP);
        fieldElementParams.put(PRECISION_PARAM,
                               PRECISION);

        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field).setMin(any());
        verify(field).setMax(any());
        verify(field).setStep(any());
        verify(field).setPrecision(any());

        assertEquals(initializer.parseValue(MIN),
                     field.getMin());
        assertEquals(initializer.parseValue(MAX),
                     field.getMax());
        assertEquals(initializer.parseValue(STEP),
                     field.getStep());
        assertEquals(initializer.parseValue(PRECISION),
                     field.getPrecision());
    }

    @Test
    public void testInitializeWithoutParams() {
        initializer.initialize(field,
                               fieldElement,
                               context);

        verify(field,
               never()).setMin(any());
        verify(field,
               never()).setMax(any());
        verify(field,
               never()).setStep(any());
        verify(field,
               never()).setPrecision(any());
    }

    abstract INITIALIZER getInitializerInstance();

    abstract FIELD getFieldInstance();
}
