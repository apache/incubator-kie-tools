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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePickerWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class QNameFieldRendererTest {

    @Mock
    private DataTypePickerWidget typePicker;

    @Mock
    private FormRenderingContext context;

    @Mock
    private FormRenderingContext parentContext;

    @Mock
    private QNameFieldDefinition definition;

    @Mock
    private DMNModelInstrumentedBase dmnModel;

    @Mock
    private ManagedInstance<DefaultFormGroup> formGroupInstance;

    @Mock
    private DefaultFormGroup formGroup;

    private QNameFieldRenderer renderer;

    @Before
    public void setup() {
        this.renderer = spy(new QNameFieldRenderer(typePicker));
        this.renderer.setFormGroup(formGroupInstance);

        when(context.getModel()).thenReturn(null);
        when(context.getParentContext()).thenReturn(parentContext);
        when(parentContext.getModel()).thenReturn(dmnModel);
        when(formGroupInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void testInit() {
        renderer.init(context,
                      definition);

        verify(typePicker).setDMNModel(eq(dmnModel));
        verify(renderer).superInit(eq(context), eq(definition));
    }

    @Test
    public void testInitNoParent() {
        when(context.getModel()).thenReturn(dmnModel);

        renderer.init(context,
                      definition);

        verify(typePicker).setDMNModel(eq(dmnModel));
        verify(renderer).superInit(eq(context), eq(definition));
    }

    @Test
    public void testGetName() {
        assertEquals(QNameFieldDefinition.FIELD_TYPE.getTypeName(),
                     renderer.getName());
    }

    @Test
    public void testGetFormGroupWhenEditMode() {
        renderer.init(context,
                      definition);

        assertFormGroup(RenderMode.EDIT_MODE, true);
    }

    @Test
    public void testGetFormGroupWhenReadOnlyMode() {
        renderer.init(context,
                      definition);

        assertFormGroup(RenderMode.READ_ONLY_MODE, false);
    }

    @Test
    public void testGetFormGroupWhenPrettyMode() {
        renderer.init(context,
                      definition);

        assertFormGroup(RenderMode.PRETTY_MODE, false);
    }

    private void assertFormGroup(final RenderMode mode,
                                 final boolean enabled) {
        renderer.getFormGroup(mode);

        verify(typePicker).setEnabled(enabled);
        verify(formGroup).render(eq(typePicker), eq(definition));
    }

    @Test
    public void testSetReadOnly() {
        renderer.setReadOnly(false);
        verify(typePicker).setEnabled(true);

        renderer.setReadOnly(true);
        verify(typePicker).setEnabled(false);
    }
}
