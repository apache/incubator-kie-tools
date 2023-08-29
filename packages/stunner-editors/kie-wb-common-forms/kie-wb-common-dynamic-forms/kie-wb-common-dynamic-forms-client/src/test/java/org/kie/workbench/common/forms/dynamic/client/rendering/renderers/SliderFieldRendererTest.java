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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.databinding.client.api.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.Slider;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.converters.IntegerToDoubleConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroup;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.DoubleSliderDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SliderFieldRendererTest extends AbstractFieldRendererTest<SliderFieldRenderer, SliderBaseDefinition, SliderFormGroup> {

    private static String NAME = "slider";

    @Mock
    private SliderFormGroup formGroup;

    @GwtMock
    private Slider sliderMock;

    @Spy
    @InjectMocks
    private SliderFieldRenderer fieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetConverterInteger() {
        when(fieldDefinition.getStandaloneClassName()).thenReturn(Integer.class.getName());
        Converter converter = fieldRenderer.getConverter();
        assertNotNull(converter);
        assertThat(converter,
                   instanceOf(IntegerToDoubleConverter.class));
    }

    @Test
    public void testGetConverterInt() {
        when(fieldDefinition.getStandaloneClassName()).thenReturn("int");
        Converter converter = fieldRenderer.getConverter();
        assertNotNull(converter);
        assertThat(converter,
                   instanceOf(IntegerToDoubleConverter.class));
    }

    @Test
    public void testGetConverterNotInt() {
        when(fieldDefinition.getStandaloneClassName()).thenReturn(Double.class.getName());
        Converter converter = fieldRenderer.getConverter();
        assertNull(converter);
    }

    @Override
    protected SliderFieldRenderer getRendererInstance() {
        return fieldRenderer;
    }

    @Override
    protected SliderBaseDefinition getFieldDefinition() {
        DoubleSliderDefinition doubleSliderDefinition = new DoubleSliderDefinition();
        doubleSliderDefinition.setName(NAME);
        doubleSliderDefinition.setBinding(NAME);
        doubleSliderDefinition.setMin(0d);
        doubleSliderDefinition.setMax(100d);
        doubleSliderDefinition.setStep(1d);
        doubleSliderDefinition.setPrecision(.1d);
        return doubleSliderDefinition;
    }
}
