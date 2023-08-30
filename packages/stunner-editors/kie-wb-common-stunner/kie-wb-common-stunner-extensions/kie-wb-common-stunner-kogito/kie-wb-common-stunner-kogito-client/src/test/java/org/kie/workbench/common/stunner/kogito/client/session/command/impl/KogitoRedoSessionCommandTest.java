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


package org.kie.workbench.common.stunner.kogito.client.session.command.impl;

import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KogitoRedoSessionCommandTest {

    @Mock
    private StateControl stateControl;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler;

    @Mock
    protected KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    protected EditorSession session;

    private boolean envelopeAvailable = false;

    private KogitoRedoSessionCommand redoSessionCommand;

    @Before
    public void setup() {
        when(session.getKeyboardControl()).thenReturn(keyboardControl);

        redoSessionCommand = new KogitoRedoSessionCommand(sessionCommandManager, redoCommandHandler, () -> envelopeAvailable, () -> stateControl);
    }

    @Test
    public void testBindCommandInKogito() {
        this.envelopeAvailable = true;

        redoSessionCommand.bind(session);

        verify(redoCommandHandler).setSession(eq(session));

        verify(keyboardControl, never()).addKeyShortcutCallback(any());
        verify(stateControl).setRedoCommand(any());
    }

    @Test
    public void testBindCommandOutsideKogito() {

        redoSessionCommand.bind(session);

        verify(redoCommandHandler).setSession(eq(session));

        verify(keyboardControl).addKeyShortcutCallback(any());
        verify(stateControl, never()).setRedoCommand(any());
    }
}
