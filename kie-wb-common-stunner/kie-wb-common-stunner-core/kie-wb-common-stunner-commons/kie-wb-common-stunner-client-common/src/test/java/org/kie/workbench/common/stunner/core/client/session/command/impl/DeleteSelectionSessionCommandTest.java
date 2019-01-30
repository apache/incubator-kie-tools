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

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteSelectionSessionCommandTest extends BaseSessionCommandKeyboardSelectionAwareTest {

    @Mock
    private Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private DefinitionUtils definitionUtils;

    private final String DEFINITION_SET_ID = "mockDefinitionSetId";

    @Mock
    private Annotation qualifier;

    @Mock
    private ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;

    @Override
    public void setup() {
        super.setup();

        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(definitionUtils.getQualifier(eq(DEFINITION_SET_ID))).thenReturn(qualifier);
        when(canvasCommandFactoryInstance.select(eq(qualifier))).thenReturn(canvasCommandFactoryInstance);
        when(canvasCommandFactoryInstance.isUnsatisfied()).thenReturn(false);
        when(canvasCommandFactoryInstance.get()).thenReturn(canvasCommandFactory);
    }

    @Override
    protected AbstractClientSessionCommand<EditorSession> getCommand() {
        return new DeleteSelectionSessionCommand(sessionCommandManager,
                                                 canvasCommandFactoryInstance,
                                                 canvasClearSelectionEventEvent,
                                                 definitionUtils);
    }

    @Test
    public void testClearSessionInvoked() {
        DeleteSelectionSessionCommand deleteCommand = (DeleteSelectionSessionCommand) this.command;

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
