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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.formGroup;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeEditorFieldDefinition;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AssigneeFormGroupTest {

    private static final String INPUT_ID = "id";

    @GwtMock
    protected Widget widget;

    @Mock
    protected AssigneeEditorFieldDefinition fieldDefinition;

    @Mock
    protected AssigneeFormGroupView view;

    protected AssigneeFormGroup formGroup;

    @Before
    public void setUp() {
        formGroup = new AssigneeFormGroup(view);
    }

    @Test
    public void testFunctionallity() {
        formGroup.render(widget,
                         fieldDefinition);

        verify(view).render(widget,
                            fieldDefinition);

        formGroup.getElement();

        verify(view).getElement();
    }

    @Test
    public void testRenderWithInputId() {
        formGroup.render(INPUT_ID, widget, fieldDefinition);

        verify(view).render(INPUT_ID, widget, fieldDefinition);

        formGroup.getElement();

        verify(view).getElement();
    }
}
