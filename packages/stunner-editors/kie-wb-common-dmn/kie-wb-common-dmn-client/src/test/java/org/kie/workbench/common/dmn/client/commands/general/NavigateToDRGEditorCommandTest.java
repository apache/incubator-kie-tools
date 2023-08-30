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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class NavigateToDRGEditorCommandTest extends BaseNavigationCommandTest {

    private static final String NODE_UUID = "uuid";

    @Override
    @SuppressWarnings("unchecked")
    protected BaseNavigateCommand getCommand(final boolean isOnlyVisualChangeAllowed) {
        return new NavigateToDRGEditorCommand(editor,
                                              sessionPresenter,
                                              sessionManager,
                                              sessionCommandManager,
                                              refreshFormPropertiesEvent,
                                              NODE_UUID,
                                              hasExpression,
                                              Optional.of(hasName),
                                              isOnlyVisualChangeAllowed);
    }

    @Test
    @Override
    public void executeCanvasCommand() {
        setup(false);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(canvas).enableHandlers();
        verify(command).hidePaletteWidget(eq(false));
        verify(command).addDRGEditorToCanvasWidget();
        verify(sessionPresenterView).setCanvasWidget(view);
        verify(sessionPresenter).focus();

        verify(refreshFormPropertiesEvent).fire(refreshFormPropertiesEventCaptor.capture());

        final RefreshFormPropertiesEvent refreshFormPropertiesEvent = refreshFormPropertiesEventCaptor.getValue();
        assertEquals(NODE_UUID, refreshFormPropertiesEvent.getUuid());
    }

    @Test
    @Override
    public void undoCanvasCommand() {
        setup(false);

        assertUndoCanvasCommand(false);
    }

    @Test
    public void undoCanvasCommandWhenOnlyVisualChangeAllowed() {
        setup(true);

        assertUndoCanvasCommand(true);
    }

    private void assertUndoCanvasCommand(final boolean isOnlyVisualChangeAllowed) {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(canvas).disableHandlers();
        verify(command).hidePaletteWidget(eq(true));
        verify(command).addExpressionEditorToCanvasWidget();
        verify(sessionPresenterView).setCanvasWidget(editorContainerForErrai1090);
        verify(editor).setExpression(eq(NODE_UUID),
                                     eq(hasExpression),
                                     eq(Optional.of(hasName)),
                                     eq(isOnlyVisualChangeAllowed));
        verify(expressionEditorView).setFocus();
        verify(sessionPresenter).lostFocus();
    }

    @Test
    public void checkCommandDefinition() {
        setup(false);

        assertTrue(command instanceof VetoUndoCommand);
    }
}
