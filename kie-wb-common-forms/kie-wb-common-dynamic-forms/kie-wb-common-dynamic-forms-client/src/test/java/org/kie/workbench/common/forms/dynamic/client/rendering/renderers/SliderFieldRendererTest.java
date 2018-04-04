/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers;

import com.google.gwtmockito.GwtMock;
import org.jboss.errai.databinding.client.api.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.Slider;
import org.kie.workbench.common.forms.common.rendering.client.widgets.slider.converters.IntegerToDoubleConverter;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.definition.SliderBaseDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SliderFieldRendererTest {

    private SliderFieldRenderer fieldRenderer;

    @Mock
    private SliderBaseDefinition fieldMock;

    @GwtMock
    private Slider sliderMock;

    @Before
    public void init() {

        when(fieldMock.getMin()).thenReturn(0d);
        when(fieldMock.getMax()).thenReturn(100d);
        when(fieldMock.getStep()).thenReturn(1d);
        when(fieldMock.getPrecision()).thenReturn(.1d);

        fieldRenderer = spy(new SliderFieldRenderer() {
            {
                field = fieldMock;
            }
        });
    }

    @Test
    public void testGetName() {
        String name = fieldRenderer.getName();
        assertEquals(SliderBaseDefinition.FIELD_TYPE.getTypeName(),
                     name);
    }

    @Test
    public void testGetSupportedCode() {
        String name = fieldRenderer.getSupportedCode();
        assertEquals(SliderBaseDefinition.FIELD_TYPE.getTypeName(),
                     name);
    }


    @Test
    public void testGetConverterInteger() {
        when(fieldMock.getStandaloneClassName()).thenReturn(Integer.class.getName());
        Converter converter = fieldRenderer.getConverter();
        assertNotNull(converter);
        assertThat(converter,
                   instanceOf(IntegerToDoubleConverter.class));
    }

    @Test
    public void testGetConverterInt() {
        when(fieldMock.getStandaloneClassName()).thenReturn("int");
        Converter converter = fieldRenderer.getConverter();
        assertNotNull(converter);
        assertThat(converter,
                   instanceOf(IntegerToDoubleConverter.class));
    }

    @Test
    public void testGetConverterNotInt() {
        when(fieldMock.getStandaloneClassName()).thenReturn(Double.class.getName());
        Converter converter = fieldRenderer.getConverter();
        assertNull(converter);
    }
}
