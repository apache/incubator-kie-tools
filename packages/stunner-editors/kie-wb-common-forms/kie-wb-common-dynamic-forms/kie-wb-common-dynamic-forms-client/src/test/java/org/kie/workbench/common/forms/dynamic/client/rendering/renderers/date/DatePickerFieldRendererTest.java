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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.AbstractFieldRendererTest;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapper;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DatePickerFieldRendererTest extends AbstractFieldRendererTest<org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.DatePickerFieldRenderer, DatePickerFieldDefinition, DefaultFormGroup> {

    private static String NAME = "datePicker";

    @Mock
    private DatePickerWrapper datePicker;

    @Mock
    private DefaultFormGroup formGroup;

    @InjectMocks
    @Spy
    private DatePickerFieldRenderer datePickerFieldRenderer;

    @Before
    public void init() {
        super.init();

        when(formGroupsInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testGetFormGroupShortDate() {
        testGetFormGroup(false);
    }

    @Test
    public void testGetFormGroupTimestamp() {
        testGetFormGroup(true);
    }

    private void testGetFormGroup(boolean showTime) {
        when(fieldDefinition.getShowTime()).thenReturn(showTime);

        renderer.getFormGroup(RenderMode.EDIT_MODE);

        verify(formGroupsInstance).get();
        verify(datePicker).setDatePickerWidget(eq(showTime));
        verify(datePicker).setId(any());
        verify(datePicker).setPlaceholder(eq(fieldDefinition.getPlaceHolder()));
        verify(datePicker).setEnabled(eq(!fieldDefinition.getReadOnly()));

        verify(datePicker).asWidget();

        verify(formGroup).render(Mockito.<String>any(), any(), eq(fieldDefinition));
    }

    @Override
    protected DatePickerFieldRenderer getRendererInstance() {
        return datePickerFieldRenderer;
    }

    @Override
    protected DatePickerFieldDefinition getFieldDefinition() {
        DatePickerFieldDefinition datePickerFieldDefinition = new DatePickerFieldDefinition();

        datePickerFieldDefinition.setName(NAME);
        datePickerFieldDefinition.setBinding(NAME);
        datePickerFieldDefinition.setPlaceHolder(NAME);

        return datePickerFieldDefinition;
    }
}
