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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorControlImplTest {

    @Mock
    private DMNSession session;

    @Mock
    private ExpressionEditorView view;

    @Mock
    private DecisionNavigatorPresenter decisionNavigator;

    @Mock
    private ExpressionEditorView.Presenter editor;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Node node;

    @Mock
    private CanvasSelectionEvent event;

    private ExpressionEditorControlImpl control;

    @Before
    public void setup() {
        this.control = spy(new ExpressionEditorControlImpl(view,
                                                           decisionNavigator));
        doReturn(editor).when(control).makeExpressionEditor(any(ExpressionEditorView.class),
                                                            any(DecisionNavigatorPresenter.class));
    }

    @Test
    public void testBind() {
        control.bind(session);

        assertNotNull(control.getExpressionEditor());
        verify(editor).bind(session);
    }

    @Test
    public void testDoInit() {
        assertNull(control.getExpressionEditor());

        control.doInit();

        assertNull(control.getExpressionEditor());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        control.doDestroy();

        assertNull(control.getExpressionEditor());
    }

    @Test
    public void testOnCanvasFocusedSelectionEventWhenBound() {
        control.bind(session);

        control.onCanvasFocusedSelectionEvent(event);

        verify(editor).exit();
    }

    @Test
    public void testOnCanvasFocusedSelectionEventWhenNotBound() {
        control.onCanvasFocusedSelectionEvent(event);

        verifyNoMoreInteractions(editor);
    }

    @Test
    public void testOnCanvasElementUpdated() {
        control.bind(session);

        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        control.onCanvasElementUpdated(event);

        verify(editor).handleCanvasElementUpdated(event);
    }
}
