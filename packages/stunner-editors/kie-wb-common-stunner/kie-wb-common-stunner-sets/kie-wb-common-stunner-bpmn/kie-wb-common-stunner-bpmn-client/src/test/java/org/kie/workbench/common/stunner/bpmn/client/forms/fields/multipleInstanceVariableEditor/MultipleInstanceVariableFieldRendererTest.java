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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormFieldImpl;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleInstanceVariableFieldRendererTest {

    @Mock
    private MultipleInstanceVariableEditorWidget widget;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private ClientTranslationService translationService;

    private MultipleInstanceVariableFieldRenderer renderer;

    @Before
    public void setUp() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getNode(anyString())).thenReturn(node);
        renderer = new MultipleInstanceVariableFieldRenderer(widget, sessionManager, translationService);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCustomValidatorWasRegistered() {
        ArgumentCaptor<MultipleInstanceVariableValidator> validatorCaptor = ArgumentCaptor.forClass(MultipleInstanceVariableValidator.class);
        FormFieldImpl field = mock(FormFieldImpl.class);
        List<CustomFieldValidator> validators = mock(List.class);
        when(field.getCustomValidators()).thenReturn(validators);
        renderer.registerCustomFieldValidators(field);
        verify(validators).add(validatorCaptor.capture());
        assertNotNull(validatorCaptor.getValue());
    }

    @Test
    public void testGetName() {
        assertEquals("MultipleInstanceVariableFieldType", renderer.getName());
    }

    @Test
    public void testSetReadonlyTrue() {
        renderer.setReadOnly(true);
        verify(widget).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        renderer.setReadOnly(false);
        verify(widget).setReadOnly(false);
    }
}
