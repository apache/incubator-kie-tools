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

package org.kie.workbench.common.dmn.client.property.dmn;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameFieldType;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NameFieldRendererTest {

    private static final String NAME = "name";

    @Mock
    private TextBox textBox;

    @Mock
    private FormRenderingContext context;

    @Mock
    private NameFieldDefinition definition;

    @Mock
    private ManagedInstance<DefaultFormGroup> formGroupInstance;

    @Mock
    private DefaultFormGroup formGroup;

    @Mock
    private ChangeEvent event;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerArgumentCaptor;

    private NameFieldRenderer renderer;

    @Before
    public void setup() {
        final NameFieldRenderer wrapped = new NameFieldRenderer(textBox) {{
            this.formGroupsInstance = NameFieldRendererTest.this.formGroupInstance;
        }};
        this.renderer = spy(wrapped);

        when(formGroupInstance.get()).thenReturn(formGroup);
        when(definition.getFieldType()).thenReturn(new NameFieldType());
    }

    @Test
    public void testChangeHandlerWithWhitespace() {
        when(textBox.getValue()).thenReturn("  " + NAME + "  ");

        verify(textBox).addChangeHandler(changeHandlerArgumentCaptor.capture());

        final ChangeHandler changeHandler = changeHandlerArgumentCaptor.getValue();
        changeHandler.onChange(event);

        verify(textBox).setValue(NAME);
    }

    @Test
    public void testChangeHandlerWithoutWhitespace() {
        when(textBox.getValue()).thenReturn(NAME);

        verify(textBox).addChangeHandler(changeHandlerArgumentCaptor.capture());

        final ChangeHandler changeHandler = changeHandlerArgumentCaptor.getValue();
        changeHandler.onChange(event);

        verify(textBox, never()).setValue(NAME);
    }

    @Test
    public void testGetName() {
        assertEquals(NameFieldDefinition.FIELD_TYPE.getTypeName(),
                     renderer.getName());
    }

    @Test
    public void testGetFormGroupWhenEditMode() {
        renderer.init(context, definition);

        assertFormGroup(RenderMode.EDIT_MODE, true);
    }

    @Test
    public void testGetFormGroupWhenReadOnlyMode() {
        when(definition.getReadOnly()).thenReturn(true);

        renderer.init(context, definition);

        assertFormGroup(RenderMode.READ_ONLY_MODE, false);
    }

    @Test
    public void testGetFormGroupWhenPrettyMode() {
        renderer.init(context, definition);

        renderer.getFormGroup(RenderMode.PRETTY_MODE);

        verify(formGroup).render(any(HTML.class), eq(definition));
    }

    private void assertFormGroup(final RenderMode mode,
                                 final boolean enabled) {
        renderer.getFormGroup(mode);

        verify(textBox).setEnabled(enabled);
        verify(formGroup).render(Mockito.<String>any(), eq(textBox), eq(definition));
    }

    @Test
    public void testSetReadOnly() {
        renderer.setReadOnly(false);
        verify(textBox).setEnabled(true);

        renderer.setReadOnly(true);
        verify(textBox).setEnabled(false);
    }

    @Test
    public void testConverterModelType() {
        assertEquals(Name.class, renderer.getConverter().getModelType());
    }

    @Test
    public void testConverterComponentType() {
        assertEquals(String.class, renderer.getConverter().getComponentType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConverterToModelValue() {
        assertEquals(new Name(NAME), renderer.getConverter().toModelValue(NAME));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConverterToWidgetValue() {
        assertEquals(NAME, renderer.getConverter().toWidgetValue(new Name(NAME)));
    }
}
