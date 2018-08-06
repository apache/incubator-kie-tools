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
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private DMNSession dmnSession;

    @Mock
    private ToolbarStateHandler toolbarStateHandler;

    @Mock
    private Decision decision;

    @Mock
    private Command command;

    @Mock
    private ManagedSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Node node;

    @Mock
    private Definition definition;

    @Captor
    private ArgumentCaptor<Optional<HasName>> optionalHasNameCaptor;

    private ExpressionEditor testedEditor;

    private ExpressionGridCache expressionGridCache;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        this.expressionGridCache = new ExpressionGridCacheImpl();

        testedEditor = spy(new ExpressionEditor(view,
                                                decisionNavigator));
        testedEditor.bind(dmnSession);
        when(session.getCanvasControl(eq(ExpressionGridCache.class))).thenReturn(expressionGridCache);
    }

    @Test
    public void testBind() {
        //ExpressionEditor.bind(..) is called in @Before setup
        verify(view).bind(eq(dmnSession));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetExpression() {
        setupExpression(toolbarStateHandler);

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(decision),
                                   eq(Optional.of(decision)));
        verify(toolbarStateHandler).enterGridView();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExitWithCommand() {
        setupExpression(toolbarStateHandler);

        testedEditor.setExitCommand(command);

        testedEditor.exit();

        verify(decisionNavigator).clearSelections();
        verify(toolbarStateHandler).enterGraphView();
        verify(command).execute();
        assertEquals(Optional.empty(), testedEditor.getExitCommand());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetExpressionWithoutToolbar() {
        setupExpression(null);

        verifyNoMoreInteractions(toolbarStateHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExitWithCommandWithoutToolbar() {
        setupExpression(null);

        testedEditor.setExitCommand(command);

        testedEditor.exit();

        verify(decisionNavigator).clearSelections();
        verifyNoMoreInteractions(toolbarStateHandler);
        verify(command).execute();
        assertEquals(Optional.empty(), testedEditor.getExitCommand());
    }

    @SuppressWarnings("unchecked")
    private void setupExpression(final ToolbarStateHandler toolbarStateHandler) {
        testedEditor.setToolbarStateHandler(toolbarStateHandler);

        testedEditor.setExpression(NODE_UUID,
                                   decision,
                                   Optional.of(decision));

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(decision),
                                   eq(Optional.of(decision)));
    }

    @Test
    public void testOnCanvasElementUpdated() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(decision);

        setupExpression(toolbarStateHandler);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view).setReturnToDRGText(optionalHasNameCaptor.capture());

        final Optional<HasName> optionalHasName = optionalHasNameCaptor.getValue();
        assertTrue(optionalHasName.isPresent());
        assertEquals(decision,
                     optionalHasName.get());
    }

    @Test
    public void testOnCanvasElementUpdatedDifferentNode() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        final Decision differentNodeDefinition = mock(Decision.class);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(differentNodeDefinition);

        setupExpression(toolbarStateHandler);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view, never()).setReturnToDRGText(any(Optional.class));
    }
}
