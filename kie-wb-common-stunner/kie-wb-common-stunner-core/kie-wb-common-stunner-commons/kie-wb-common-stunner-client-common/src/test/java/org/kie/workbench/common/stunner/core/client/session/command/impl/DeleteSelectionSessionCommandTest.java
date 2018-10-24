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

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteSelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    @Mock
    private Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private EditorSession session;

    @Mock
    private KeyboardControl keyboardControl;

    @Mock
    private SelectionControl selectionControl;

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {
        return new DeleteSelectionSessionCommand(sessionCommandManager,
                                                 canvasCommandFactory,
                                                 canvasClearSelectionEventEvent);
    }

    @Test
    public void testClearSessionInvoked() {
        DeleteSelectionSessionCommand deleteCommand = (DeleteSelectionSessionCommand) getCommand();

        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(session.getSelectionControl()).thenReturn(selectionControl);

        deleteCommand.bind(session);

        deleteCommand.execute(callback);

        verify(selectionControl).clearSelection();
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.DELETE};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }
}
