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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class NavigateToExpressionEditorCommandTest extends BaseNavigationCommandTest {

    private static final String NODE_UUID = "uuid";

    @Override
    @SuppressWarnings("unchecked")
    protected BaseNavigateCommand getCommand() {
        return new NavigateToExpressionEditorCommand(editor,
                                                     sessionPresenter,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     refreshFormPropertiesEvent,
                                                     NODE_UUID,
                                                     hasExpression,
                                                     Optional.of(hasName));
    }

    @Test
    @Override
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(canvas).disableHandlers();
        verify(command).hidePaletteWidget(eq(true));
        verify(command).addExpressionEditorToCanvasWidget();
        verify(sessionPresenterView).setCanvasWidget(editorContainerForErrai1090);
        verify(sessionPresenterView).setContentScrollType(eq(SessionPresenter.View.ScrollType.CUSTOM));
        verify(editor).setExpression(eq(NODE_UUID),
                                     eq(hasExpression),
                                     eq(Optional.of(hasName)));
        verify(expressionEditorView).setFocus();
    }

    @Test
    @Override
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(canvas).enableHandlers();
        verify(command).hidePaletteWidget(eq(false));
        verify(command).addDRGEditorToCanvasWidget();
        verify(sessionPresenterView).setCanvasWidget(view);
        verify(sessionPresenterView).setContentScrollType(eq(SessionPresenter.View.ScrollType.AUTO));

        verify(canvasHandler).notifyCanvasClear();
    }

    @Test
    public void checkCommandDefinition() {
        assertTrue(command instanceof VetoExecutionCommand);
    }
}
