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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.decisionservice.parameters.DecisionServiceParametersListWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.property.dmn.DecisionParametersFieldDefinition.FIELD_TYPE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionParameterFieldRendererTest {

    @Mock
    private DecisionServiceParametersListWidget widget;

    @Mock
    private DefaultFormGroup formGroup;

    private DecisionParameterFieldRenderer renderer;

    @Before
    public void setup() {
        renderer = spy(new DecisionParameterFieldRenderer(widget));
        doReturn(formGroup).when(renderer).getFormGroupInstance();
    }

    @Test
    public void testGetFormGroup() {

        final RenderMode renderMode = RenderMode.EDIT_MODE;

        final FormGroup actualGroup = renderer.getFormGroup(renderMode);

        verify(formGroup).render(widget, renderer.getField());
        assertEquals(formGroup, actualGroup);
    }

    @Test
    public void testSetReadOnly() {

        renderer.setReadOnly(true);

        verify(widget).setEnabled(false);

        renderer.setReadOnly(false);

        verify(widget).setEnabled(true);
    }

    @Test
    public void testSGetName() {

        final String expected = FIELD_TYPE.getTypeName();
        final String actual = renderer.getName();

        assertEquals(expected, actual);
    }
}
