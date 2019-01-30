/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClearSessionCommandTest {

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private EventSourceMock<ClearSessionCommandExecutedEvent> commandExecutedEvent;

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private ClearSessionCommand command;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private CanvasCommand clearCanvasCommand;

    @Mock
    private CommandResult commandResult;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    private final String DEFINITION_SET_ID = "mockDefinitionSetId";

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;

    private ArgumentCaptor<ClearSessionCommandExecutedEvent> commandExecutedEventCaptor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(session.getCommandManager()).thenReturn(sessionCommandManager);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionCommandManager.getRegistry()).thenReturn(commandRegistry);
        when(canvasCommandFactory.clearCanvas()).thenReturn(clearCanvasCommand);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);
        when(canvasCommandFactoryInstance.select(eq(qualifier))).thenReturn(canvasCommandFactoryInstance);
        when(canvasCommandFactoryInstance.isUnsatisfied()).thenReturn(false);
        when(canvasCommandFactoryInstance.get()).thenReturn(canvasCommandFactory);

        commandExecutedEventCaptor = ArgumentCaptor.forClass(ClearSessionCommandExecutedEvent.class);

        command = new ClearSessionCommand(canvasCommandFactoryInstance,
                                          sessionCommandManager,
                                          commandExecutedEvent,
                                          definitionUtils);
        command.bind(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        when(sessionCommandManager.execute(canvasHandler,
                                           clearCanvasCommand)).thenReturn(null);
        command.execute(callback);
        verify(sessionCommandManager,
               times(1)).execute(canvasHandler,
                                 clearCanvasCommand);
        verify(commandRegistry,
               times(1)).clear();
        verify(callback,
               times(1)).onSuccess();
        verify(commandExecutedEvent,
               times(1)).fire(commandExecutedEventCaptor.capture());
        assertEquals(session,
                     commandExecutedEventCaptor.getValue().getClientSession());
        assertEquals(command,
                     commandExecutedEventCaptor.getValue().getExecutedCommand());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithErrors() {
        when(sessionCommandManager.execute(canvasHandler,
                                           clearCanvasCommand)).thenReturn(commandResult);
        when(commandResult.getType()).thenReturn(CommandResult.Type.ERROR);

        command.execute(callback);

        verify(sessionCommandManager,
               times(1)).execute(canvasHandler,
                                 clearCanvasCommand);
        verify(commandRegistry,
               never()).clear();
        verify(callback,
               times(1)).onError(commandResult);
        verify(commandExecutedEvent,
               never()).fire(commandExecutedEventCaptor.capture());
    }

    @Test
    public void testAcceptsSession() {
        assertTrue(command.accepts(mock(EditorSession.class)));
        assertFalse(command.accepts(mock(ViewerSession.class)));
    }
}
