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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractProcessEditorSessionCommandsTest {

    @Mock
    private ManagedClientSessionCommands commands;

    @Mock
    private GenerateProcessFormsSessionCommand generateProcessFormsSessionCommand;

    @Mock
    private GenerateDiagramFormsSessionCommand generateDiagramFormsSessionCommand;

    @Mock
    private GenerateSelectedFormsSessionCommand generateSelectedFormsSessionCommand;

    private List<ClientSessionCommand> commandList = new ArrayList<>();

    private AbstractProcessEditorSessionCommands tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        tested = new AbstractProcessEditorSessionCommands(commands) {
        };

        when(commands.register(any(Class.class))).thenAnswer(invocation -> {
            final Class commandClass = invocation.getArgumentAt(0, Class.class);

            if (GenerateProcessFormsSessionCommand.class.isAssignableFrom(commandClass)) {
                commandList.set(0, generateProcessFormsSessionCommand);
            } else if (GenerateDiagramFormsSessionCommand.class.isAssignableFrom(commandClass)) {
                commandList.set(1, generateDiagramFormsSessionCommand);
            } else if (GenerateSelectedFormsSessionCommand.class.isAssignableFrom(commandClass)) {
                commandList.set(2, generateSelectedFormsSessionCommand);
            } else {
                commandList.add(mock(ClientSessionCommand.class));
            }

            return commands;
        });
        when(commands.get(eq(GenerateProcessFormsSessionCommand.class))).thenAnswer(invocation -> commandList.get(0));
        when(commands.get(eq(GenerateDiagramFormsSessionCommand.class))).thenAnswer(invocation -> commandList.get(1));
        when(commands.get(eq(GenerateSelectedFormsSessionCommand.class))).thenAnswer(invocation -> commandList.get(2));

        tested.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() throws Exception {
        final ArgumentCaptor<Class> registeredCommandClasses = ArgumentCaptor.forClass(Class.class);
        verify(commands, atLeast(3)).register(registeredCommandClasses.capture());

        final List<Class> registeredCommandClassValues = registeredCommandClasses.getAllValues();
        assertTrue(registeredCommandClassValues.contains(GenerateProcessFormsSessionCommand.class));
        assertTrue(registeredCommandClassValues.contains(GenerateDiagramFormsSessionCommand.class));
        assertTrue(registeredCommandClassValues.contains(GenerateSelectedFormsSessionCommand.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBind() throws Exception {
        tested.bind(mock(ClientSession.class));

        final ArgumentCaptor<Predicate> elementAcceptorCaptor = ArgumentCaptor.forClass(Predicate.class);
        verify(generateSelectedFormsSessionCommand, times(1)).setElementAcceptor(elementAcceptorCaptor.capture());

        final Predicate<Element> elemementAcceptor = elementAcceptorCaptor.getValue();
        assertNotNull(elemementAcceptor);

        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final View view = mock(View.class);
        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(view);

        when(view.getDefinition()).thenReturn(mock(BaseUserTask.class));
        assertTrue(elemementAcceptor.test(element));

        when(view.getDefinition()).thenReturn(mock(BaseTask.class));
        assertFalse(elemementAcceptor.test(element));
    }

    @Test
    public void testGetGenerateProcessFormsSessionCommand() throws Exception {
        assertEquals(generateProcessFormsSessionCommand, tested.getGenerateProcessFormsSessionCommand());
    }

    @Test
    public void testGetGenerateDiagramFormsSessionCommand() throws Exception {
        assertEquals(generateDiagramFormsSessionCommand, tested.getGenerateDiagramFormsSessionCommand());
    }

    @Test
    public void testGetGenerateSelectedFormsSessionCommand() throws Exception {
        assertEquals(generateSelectedFormsSessionCommand, tested.getGenerateSelectedFormsSessionCommand());
    }
}