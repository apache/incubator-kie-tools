/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls.util;

import java.util.Collection;
import java.util.Collections;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionsToolboxHelperTest {

    @Mock
    private ActionsToolboxFactory commonActionToolbox;

    @Mock
    private FormGenerationToolboxAction generateFormsAction;

    @Mock
    private ManagedInstance<FormGenerationToolboxAction> generationToolboxActions;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Element element;

    @Mock
    private ToolboxAction toolboxAction;

    @Mock
    private Node node;

    @Mock
    private View view;

    private ActionsToolboxHelper tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(commonActionToolbox.getActions(eq(canvasHandler), eq(element))).thenReturn(Collections.singletonList(toolboxAction));

        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(view);

        when(generationToolboxActions.get()).thenReturn(generateFormsAction);

        tested = new ActionsToolboxHelper(commonActionToolbox, generationToolboxActions);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetActions_supportedElement() throws Exception {
        when(view.getDefinition()).thenReturn(mock(BaseUserTask.class));

        final Collection<ToolboxAction<AbstractCanvasHandler>> results =
                tested.getActions(canvasHandler, element);

        assertEquals(2, results.size());
        assertTrue(results.contains(generateFormsAction));
        assertTrue(results.contains(toolboxAction));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetActions_unsupportedElement() throws Exception {
        when(view.getDefinition()).thenReturn(mock(BaseTask.class));

        final Collection<ToolboxAction<AbstractCanvasHandler>> results =
                tested.getActions(canvasHandler, element);

        assertEquals(1, results.size());
        assertFalse(results.contains(generateFormsAction));
        assertTrue(results.contains(toolboxAction));
    }
}