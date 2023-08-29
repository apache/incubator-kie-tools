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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionEditorFieldDefinition;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ConditionEditorFieldEditorRendererTest {

    @Mock
    private ConditionEditorFieldEditorWidget widget;

    @Mock
    private SessionManager sessionManager;

    private ConditionEditorFieldEditorRenderer renderer;

    @Before
    public void setUp() {
        renderer = new ConditionEditorFieldEditorRenderer(widget, sessionManager);
    }

    @Test
    public void testInit() {
        FormRenderingContext context = mock(FormRenderingContext.class);
        ConditionEditorFieldDefinition fieldDefinition = mock(ConditionEditorFieldDefinition.class);
        ClientSession session = mock(ClientSession.class);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        renderer.init(context, fieldDefinition);
        verify(widget).init(session);
    }

    @Test
    public void testGetName() {
        assertEquals("ConditionEditorFieldType",
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
}
