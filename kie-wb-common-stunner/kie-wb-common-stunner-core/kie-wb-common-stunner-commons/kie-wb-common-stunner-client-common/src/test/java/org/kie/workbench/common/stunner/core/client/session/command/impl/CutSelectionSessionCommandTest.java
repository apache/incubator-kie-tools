/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CutSelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    private CutSelectionSessionCommand cutSelectionSessionCommand;

    @Mock
    private CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    private DeleteSelectionSessionCommand deleteSelectionSessionCommand;

    @Mock
    private EventSourceMock<CutSelectionSessionCommandExecutedEvent> commandExecutedEvent;

    @Mock
    private ArgumentCaptor<CutSelectionSessionCommandExecutedEvent> commandExecutedEventCaptor;

    @Mock
    private ClientSessionCommand.Callback mainCallback;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private DeleteNodeCommand deleteNodeCommand;

    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        clipboardControl = spy(new LocalClipboardControl());
        when(sessionCommandManager.getRegistry()).thenReturn(commandRegistry);
        when(commandRegistry.peek()).thenReturn(deleteNodeCommand);
        when(session.getClipboardControl()).thenReturn(clipboardControl);
        commandExecutedEventCaptor = ArgumentCaptor.forClass(CutSelectionSessionCommandExecutedEvent.class);
        super.setup();
        this.cutSelectionSessionCommand = getCommand();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        cutSelectionSessionCommand.bind(session);
        cutSelectionSessionCommand.execute(mainCallback);
        ArgumentCaptor<ClientSessionCommand.Callback> callbackArgumentCaptor = ArgumentCaptor.forClass(ClientSessionCommand.Callback.class);
        verify(copySelectionSessionCommand).execute(callbackArgumentCaptor.capture());

        //success
        callbackArgumentCaptor.getValue().onSuccess();
        verify(deleteSelectionSessionCommand, times(1)).execute(mainCallback);
        verify(sessionCommandManager.getRegistry(), atLeastOnce()).peek();
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
        return new CutSelectionSessionCommand(copySelectionSessionCommand, deleteSelectionSessionCommand, sessionCommandManager, commandExecutedEvent);
    }

    @Override
    protected Key[] getExpectedKeys() {
        return new Key[]{Key.CONTROL, Key.X};
    }

    @Override
    protected Key[] getUnexpectedKeys() {
        return new Key[]{Key.ESC};
    }
}