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
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class NavigateToExpressionEditorCommandTest extends BaseNavigationCommandTest {

    @Override
    @SuppressWarnings("unchecked")
    protected BaseNavigateCommand getCommand() {
        return new NavigateToExpressionEditorCommand(editor,
                                                     sessionPresenter,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     Optional.of(hasName),
                                                     hasExpression);
    }

    @Test
    @Override
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(layer).disableHandlers();
        verify(session).pause();
        verify(command).hidePaletteWidget(eq(true));
        verify(command).addExpressionEditorToCanvasWidget();
    }

    @Test
    @Override
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(layer).enableHandlers();
        verify(session).resume();
        verify(command).hidePaletteWidget(eq(false));
        verify(command).addDRGEditorToCanvasWidget();
    }

    @Test
    public void checkCommandDefinition() {
        assertTrue(command instanceof VetoExecutionCommand);
    }
}
