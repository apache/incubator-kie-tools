/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorTest {

    private static final String NODE_UUID = "uuid";

    @Mock
    private ExpressionEditorView view;

    @Mock
    private SessionPresenter sessionPresenter;

    @Mock
    private DecisionNavigatorPresenter decisionNavigator;

    @Mock
    private DMNEditorToolbar editorToolbar;

    @Mock
    private HasName hasName;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Command command;

    private ExpressionEditor testedEditor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        testedEditor = spy(new ExpressionEditor(view, decisionNavigator));
        when(sessionPresenter.getToolbar()).thenReturn(editorToolbar);
        when(editorToolbar.isEnabled(any(ToolbarCommand.class))).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        testedEditor.init(sessionPresenter);

        verify(sessionPresenter).getToolbar();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetExpression() {
        testedEditor.init(sessionPresenter);

        testedEditor.setExpression(NODE_UUID,
                                   hasExpression,
                                   Optional.of(hasName));

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(hasExpression),
                                   eq(Optional.of(hasName)));
        verify(editorToolbar, atLeast(1)).disable(any(ToolbarCommand.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExitWithCommand() {
        testedEditor.init(sessionPresenter);
        testedEditor.setExpression(NODE_UUID,
                                   hasExpression,
                                   Optional.of(hasName));
        testedEditor.setExitCommand(command);

        testedEditor.exit();

        verify(decisionNavigator).clearSelections();
        verify(editorToolbar, atLeast(1)).enable(any(ToolbarCommand.class));
        verify(command).execute();
        assertEquals(Optional.empty(), testedEditor.getExitCommand());
    }

    @Test
    public void testOnCanvasFocusedSelectionEvent() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final String uuid = "uuid";
        final CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler, uuid);

        testedEditor.onCanvasFocusedSelectionEvent(event);

        verify(testedEditor).exit();
    }
}
