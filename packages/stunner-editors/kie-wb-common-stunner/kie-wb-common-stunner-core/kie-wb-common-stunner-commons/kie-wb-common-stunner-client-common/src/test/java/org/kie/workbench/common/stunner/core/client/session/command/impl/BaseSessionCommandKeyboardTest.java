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

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseSessionCommandKeyboardTest {

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    protected KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    protected EditorSession session;

    @Mock
    protected SelectionControl<AbstractCanvasHandler, Element> selectionControl;

    @Captor
    protected ArgumentCaptor<KeyShortcutCallback> keyShortcutCallbackCaptor;

    protected AbstractClientSessionCommand<EditorSession> command;

    @Before
    public void setup() {
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        this.command = spy(getCommand());
    }

    @Test
    public void checkBindAttachesKeyHandler() {
        command.bind(session);

        verify(keyboardControl,
               times(getExpectedKeyBoardControlRegistrationCalls())).addKeyShortcutCallback(any(KeyShortcutCallback.class));
    }

    protected int getExpectedKeyBoardControlRegistrationCalls() {
        return 1;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRespondsToExpectedKeys() {
        checkRespondsToExpectedKeysMatch(getExpectedKeys());
    }

    public void checkRespondsToExpectedKeysMatch(final KeyboardEvent.Key[] keys) {
        doReturn(true).when(command).isEnabled();

        command.bind(session);

        verify(keyboardControl,
               times(getExpectedKeyBoardControlRegistrationCalls())).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(keys);

        verify(command,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDoesNotRespondToExpectedKeysWhenDisabled() {
        doReturn(false).when(command).isEnabled();

        command.bind(session);

        verify(keyboardControl,
               times(getExpectedKeyBoardControlRegistrationCalls())).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(getExpectedKeys());

        verify(command, never()).execute(any(ClientSessionCommand.Callback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDoesNotRespondToOtherKey() {
        command.bind(session);

        verify(keyboardControl,
               times(getExpectedKeyBoardControlRegistrationCalls())).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(getUnexpectedKeys());

        verify(command,
               never()).execute(any(ClientSessionCommand.Callback.class));
    }

    protected abstract AbstractClientSessionCommand<EditorSession> getCommand();

    protected abstract KeyboardEvent.Key[] getExpectedKeys();

    protected abstract KeyboardEvent.Key[] getUnexpectedKeys();
}
