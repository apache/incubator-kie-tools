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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class UpdateCanvasNodeNameCommandTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index graphIndex;

    private UpdateCanvasNodeNameCommand command;

    @Before
    public void setup() {

        final ClientSession currentSession = mock(ClientSession.class);

        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(currentSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionManager.getCurrentSession()).thenReturn(currentSession);

        this.command = spy(new UpdateCanvasNodeNameCommand(sessionManager,
                                                           definitionUtils,
                                                           canvasCommandFactory));
    }

    @Test
    public void testExecute() {

        final String nodeUUID = "node uuid";
        final HasName hasName = mock(HasName.class);

        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        final Object definitionObject = mock(Object.class);
        final String nameId = "nameId";

        final CanvasCommand canvasCommand = mock(CanvasCommand.class);

        doReturn(canvasCommand).when(command).getCommand(hasName,
                                                         element,
                                                         nameId);

        when(definitionUtils.getNameIdentifier(definitionObject)).thenReturn(nameId);
        when(definition.getDefinition()).thenReturn(definitionObject);
        when(element.getContent()).thenReturn(definition);
        when(graphIndex.get(nodeUUID)).thenReturn(element);

        command.execute(nodeUUID, hasName);

        verify(canvasCommand).execute(canvasHandler);
    }

    @Test
    public void testGetCommand_WhenHasNameHasValue() {

        final Element element = mock(Element.class);
        final String nameId = "nameId";
        final HasName hasName = mock(HasName.class);
        final Name name = mock(Name.class);
        final CanvasCommand<AbstractCanvasHandler> canvasCommand = mock(CanvasCommand.class);

        when(hasName.getValue()).thenReturn(name);
        when(canvasCommandFactory.updatePropertyValue(element, nameId, name)).thenReturn(canvasCommand);

        final CanvasCommand<AbstractCanvasHandler> actualCommand = command.getCommand(hasName,
                                                                                      element,
                                                                                      nameId);

        assertEquals(canvasCommand, actualCommand);
    }

    @Test
    public void testGetCommand_WhenHasNameDoesNotHasValue() {

        final Element element = mock(Element.class);
        final String nameId = "nameId";
        final Name name = mock(Name.class);
        final CanvasCommand<AbstractCanvasHandler> canvasCommand = mock(CanvasCommand.class);

        when(canvasCommandFactory.updatePropertyValue(element, nameId, HasName.NOP.getValue())).thenReturn(canvasCommand);

        final CanvasCommand<AbstractCanvasHandler> actualCommand = command.getCommand(null,
                                                                                      element,
                                                                                      nameId);

        assertEquals(canvasCommand, actualCommand);
    }
}
