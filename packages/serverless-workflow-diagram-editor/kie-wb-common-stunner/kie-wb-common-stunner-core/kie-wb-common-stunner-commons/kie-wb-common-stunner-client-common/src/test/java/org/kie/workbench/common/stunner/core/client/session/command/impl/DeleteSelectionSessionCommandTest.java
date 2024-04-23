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
import java.util.Collection;
import java.util.Collections;

import jakarta.enterprise.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.DELETE;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.KEY_BACKSPACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteSelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    @Mock
    private Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private DefinitionUtils definitionUtils;

    private final String DEFINITION_SET_ID = "mockDefinitionSetId";

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;

    @Mock
    private DeleteNodeConfirmation deleteNodeConfirmation;

    @Override
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        super.setup();

        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(anyString())).thenReturn(qualifier);
        when(canvasCommandFactoryInstance.select(eq(qualifier))).thenReturn(canvasCommandFactoryInstance);
        when(canvasCommandFactoryInstance.isUnsatisfied()).thenReturn(false);
        when(canvasCommandFactoryInstance.get()).thenReturn(canvasCommandFactory);
    }

    @Override
    public void checkRespondsToExpectedKeys() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;
        doReturn(true).when(deleteSelectionSessionCommand).isEventHandlesEnabled();
        super.checkRespondsToExpectedKeys();
    }

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {

        return DeleteSelectionSessionCommand.getInstance(sessionCommandManager,
                                                         canvasCommandFactoryInstance,
                                                         canvasClearSelectionEventEvent,
                                                         definitionUtils,
                                                         sessionManager,
                                                         deleteNodeConfirmation);
    }

    @Test
    public void testClearSessionInvoked() {
        DeleteSelectionSessionCommand deleteCommand = (DeleteSelectionSessionCommand) this.command;

        final String element = "element";
        final Collection<String> elements = Collections.singleton(element);
        final Index graphIndex = mock(Index.class);
        final Element theElement = mock(Element.class);
        when(graphIndex.get(element)).thenReturn(theElement);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(selectionControl.getSelectedItems()).thenReturn(elements);
        when(definitionUtils.getQualifier(anyString())).thenReturn(qualifier);

        deleteCommand.bind(session);

        deleteCommand.execute(callback);

        verify(selectionControl).clearSelection();
    }

    @Test
    public void testExecuteNullSessionAndNullSelectionControl() {
        DeleteSelectionSessionCommand deleteCommand = (DeleteSelectionSessionCommand) this.command;
        deleteCommand.execute(callback);
        // if session null, then it should never fire event

        verify(canvasClearSelectionEventEvent, never()).fire(any());

        deleteCommand.bind(session);
        when(session.getSelectionControl()).thenReturn(null);
        deleteCommand.execute(callback);
        // if session null, then it should never fire event
        verify(canvasClearSelectionEventEvent, never()).fire(any());
    }

    @Test
    public void testExecuteBackSpaceKeys() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;
        doReturn(true).when(deleteSelectionSessionCommand).isEventHandlesEnabled();
        this.checkRespondsToExpectedKeysMatch(new KeyboardEvent.Key[]{KeyboardEvent.Key.KEY_BACKSPACE});
    }

    @Test
    public void testBind() {

        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;
        final KeyboardControl.KogitoKeyPress backspaceCallback = mock(KeyboardControl.KogitoKeyPress.class);
        final KeyboardControl.KogitoKeyPress deleteCallback = mock(KeyboardControl.KogitoKeyPress.class);
        final KeyboardControl.KeyShortcutCallback keyDownEvent = mock(KeyboardControl.KeyShortcutCallback.class);

        doNothing().when(deleteSelectionSessionCommand).superBind(session);
        doReturn(keyDownEvent).when(deleteSelectionSessionCommand).getOnKeyDownEvent();
        doReturn(backspaceCallback).when(deleteSelectionSessionCommand).getShortcutCallback(KEY_BACKSPACE);
        doReturn(deleteCallback).when(deleteSelectionSessionCommand).getShortcutCallback(DELETE);
        doReturn(canvasCommandFactory).when(deleteSelectionSessionCommand).loadCanvasFactory(canvasCommandFactoryInstance,
                                                                                             definitionUtils);
        command.bind(session);

        verify(deleteSelectionSessionCommand).superBind(session);
        verify(keyboardControl).addKeyShortcutCallback(backspaceCallback);
        verify(keyboardControl).addKeyShortcutCallback(deleteCallback);
        verify(keyboardControl).addKeyShortcutCallback(keyDownEvent);
        verify(deleteSelectionSessionCommand).setCanvasCommandFactory(canvasCommandFactory);
    }

    @Test
    public void testGetShortcutCallback() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;

        doReturn(true).when(deleteSelectionSessionCommand).isEnabled();
        doReturn(true).when(deleteSelectionSessionCommand).isEventHandlesEnabled();
        doNothing().when(deleteSelectionSessionCommand).execute();

        final KeyboardControl.KogitoKeyPress shortcutCallback = deleteSelectionSessionCommand.getShortcutCallback(KEY_BACKSPACE);
        final KeyboardEvent.Key[] keys = shortcutCallback.getKeyCombination();

        shortcutCallback.onKeyDown();

        verify(deleteSelectionSessionCommand).execute();
        assertEquals(1, keys.length);
        assertEquals(KEY_BACKSPACE, keys[0]);
    }

    @Test
    public void testGetShortcutCallback_WhenEventHandlesIsNotEnabled() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;

        doReturn(true).when(deleteSelectionSessionCommand).isEnabled();
        doReturn(false).when(deleteSelectionSessionCommand).isEventHandlesEnabled();

        final KeyboardControl.KogitoKeyPress shortcutCallback = deleteSelectionSessionCommand.getShortcutCallback(KEY_BACKSPACE);

        shortcutCallback.onKeyDown();

        verify(deleteSelectionSessionCommand, never()).execute();
    }

    @Test
    public void testIsEventHandlesEnabled_WhenItIsNot() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;
        final AbstractCanvas canvas = mock(AbstractCanvas.class);

        deleteSelectionSessionCommand.bind(session);

        when(canvas.isEventHandlesEnabled()).thenReturn(false);
        when(canvasHandler.getCanvas()).thenReturn(canvas);

        final boolean isEnabled = deleteSelectionSessionCommand.isEventHandlesEnabled();

        assertFalse(isEnabled);
    }

    @Test
    public void testIsEventHandlesEnabled_WhenItIs() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;
        final AbstractCanvas canvas = mock(AbstractCanvas.class);

        deleteSelectionSessionCommand.bind(session);
        when(canvas.isEventHandlesEnabled()).thenReturn(true);
        when(canvasHandler.getCanvas()).thenReturn(canvas);

        final boolean isEnabled = deleteSelectionSessionCommand.isEventHandlesEnabled();

        assertTrue(isEnabled);
    }

    @Test
    public void testIsEventHandlesEnabled_WhenItIsNotAbstractCanvas() {

        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = (DeleteSelectionSessionCommand) command;

        deleteSelectionSessionCommand.bind(session);
        when(canvasHandler.getCanvas()).thenReturn(null);

        final boolean isEnabled = deleteSelectionSessionCommand.isEventHandlesEnabled();

        assertTrue(isEnabled);
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyConstructor() {
        DeleteSelectionSessionCommand del = new DeleteSelectionSessionCommand();
    }

    @Override
    protected KeyboardEvent.Key[] getExpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.DELETE};
    }

    @Override
    protected KeyboardEvent.Key[] getUnexpectedKeys() {
        return new KeyboardEvent.Key[]{KeyboardEvent.Key.ESC};
    }

    @Override
    protected int getExpectedKeyBoardControlRegistrationCalls() {
        return 3;
    }
}
