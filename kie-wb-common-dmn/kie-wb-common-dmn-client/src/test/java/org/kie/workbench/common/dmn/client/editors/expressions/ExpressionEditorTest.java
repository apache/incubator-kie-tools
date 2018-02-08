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
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorTest {

    @Mock
    private ExpressionEditorView view;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitions;

    private ExpressionEditor testedEditor;

    @Before
    public void setUp() throws Exception {
        testedEditor = new ExpressionEditor(view,
                                            sessionManager,
                                            sessionCommandManager,
                                            expressionEditorDefinitions);
    }

    @Test
    public void testObservesExpressionEditorSelectedEvent_SameSessions() throws Exception {
        final ExpressionEditorSelectedEvent firedEvent = mock(ExpressionEditorSelectedEvent.class);
        final Optional<BaseExpressionGrid> expressionGrid = Optional.of(mock(BaseExpressionGrid.class));
        final ClientSession clientSession = mock(ClientSession.class);

        doReturn(expressionGrid).when(firedEvent).getEditor();
        // same client sessions
        doReturn(clientSession).when(sessionManager).getCurrentSession();
        doReturn(clientSession).when(firedEvent).getSession();

        testedEditor.onExpressionEditorSelected(firedEvent);

        verify(view).onExpressionEditorSelected(expressionGrid);
    }

    @Test
    public void testObservesExpressionEditorSelectedEvent_DifferentSessions() throws Exception {
        final ExpressionEditorSelectedEvent firedEvent = mock(ExpressionEditorSelectedEvent.class);
        final Optional<BaseExpressionGrid> expressionGrid = Optional.of(mock(BaseExpressionGrid.class));

        doReturn(expressionGrid).when(firedEvent).getEditor();
        // different client sessions
        doReturn(mock(ClientSession.class)).when(sessionManager).getCurrentSession();
        doReturn(mock(ClientSession.class)).when(firedEvent).getSession();

        testedEditor.onExpressionEditorSelected(firedEvent);

        verify(view, never()).onExpressionEditorSelected(expressionGrid);
    }
}
