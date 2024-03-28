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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.lang.annotation.Annotation;

import jakarta.enterprise.event.Event;
import org.appformer.client.stateControl.registry.Registry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CutSelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    private CutSelectionSessionCommand cutSelectionSessionCommand;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    private Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;

    @Mock
    private EventSourceMock<CutSelectionSessionCommandExecutedEvent> commandExecutedEvent;

    @Mock
    private ArgumentCaptor<CutSelectionSessionCommandExecutedEvent> commandExecutedEventCaptor;

    @Mock
    private ClientSessionCommand.Callback mainCallback;

    @Mock
    private Registry commandRegistry;

    @Mock
    private DeleteNodeCommand deleteNodeCommand;

    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    private final String DEFINITION_SET_ID = "mockDefinitionSetId";

    @Mock
    private Annotation qualifier;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        clipboardControl = spy(new LocalClipboardControl());
        when(session.getCommandRegistry()).thenReturn(commandRegistry);
        when(commandRegistry.peek()).thenReturn(deleteNodeCommand);
        when(session.getClipboardControl()).thenReturn(clipboardControl);
        when(sessionManager.getCurrentSession()).thenReturn(session);

        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);
        when(canvasCommandFactoryInstance.select(eq(qualifier))).thenReturn(canvasCommandFactoryInstance);
        when(canvasCommandFactoryInstance.isUnsatisfied()).thenReturn(false);
        when(canvasCommandFactoryInstance.get()).thenReturn(canvasCommandFactory);

        super.setup();
        this.cutSelectionSessionCommand = getCommand();

        commandExecutedEventCaptor = ArgumentCaptor.forClass(CutSelectionSessionCommandExecutedEvent.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {

        cutSelectionSessionCommand.bind(session);
        CopySelectionSessionCommand.getInstance(sessionManager).bind(session);
        cutSelectionSessionCommand.setCopySelectionSessionCommand(copySelectionSessionCommand);
        cutSelectionSessionCommand.execute(mainCallback);

        ArgumentCaptor<ClientSessionCommand.Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(ClientSessionCommand.Callback.class);
        verify(copySelectionSessionCommand).execute(callbackArgumentCaptor.capture());

        //success
        callbackArgumentCaptor.getValue().onSuccess();
        verify(session.getCommandRegistry(), atLeastOnce()).peek();
        verify(clipboardControl, atLeastOnce()).setRollbackCommand(deleteNodeCommand);
        verify(commandExecutedEvent, times(1)).fire(commandExecutedEventCaptor.capture());
        assertEquals(session, commandExecutedEventCaptor.getValue().getClientSession());
        assertEquals(cutSelectionSessionCommand, commandExecutedEventCaptor.getValue().getExecutedCommand());

        //error
        Object error = new Object();
        callbackArgumentCaptor.getValue().onError(error);
        verify(mainCallback, times(1)).onError(error);
    }

    @Override
    protected CutSelectionSessionCommand getCommand() {
        return new CutSelectionSessionCommand(commandExecutedEvent, sessionManager);
    }

    @Override
    protected Key[] getExpectedKeys() {
        return new Key[]{Key.CONTROL, Key.X};
    }

    @Override
    protected Key[] getUnexpectedKeys() {
        return new Key[]{Key.ESC};
    }

    @Override
    protected int getExpectedKeyBoardControlRegistrationCalls() {
        return 2;
    }
}
