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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.serviceEditor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GenericServiceTaskEditorFieldRendererTest extends ReflectionUtilsTest {

    @Mock
    private GenericServiceTaskEditorWidget widget;

    private GenericServiceTaskEditorFieldRenderer renderer;

    @Mock
    private DefaultFormGroup defaultFormGroup;

    @Mock
    private ManagedInstance<DefaultFormGroup> formGroupsInstance;

    @Before
    public void setUp() {
        renderer = new GenericServiceTaskEditorFieldRenderer(widget);
    }

    @Test
    public void testGetName() {
        assertEquals("GenericServiceTaskEditor",
                     renderer.getName());
    }

    @Test
    public void testSetReadonlyTrue() {
        renderer.setReadOnly(true);
        verify(widget,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        renderer.setReadOnly(false);
        verify(widget,
               times(1)).setReadOnly(false);
    }

    @Test
    public void getFormGroup() {
        renderer = spy(new GenericServiceTaskEditorFieldRenderer(widget));
        when(formGroupsInstance.get()).thenReturn(defaultFormGroup);
        setFieldValue(renderer, "formGroupsInstance", formGroupsInstance);
        FormGroup formGroup = renderer.getFormGroup(RenderMode.EDIT_MODE);
        assertThat(formGroup).isInstanceOf(DefaultFormGroup.class);
    }
}