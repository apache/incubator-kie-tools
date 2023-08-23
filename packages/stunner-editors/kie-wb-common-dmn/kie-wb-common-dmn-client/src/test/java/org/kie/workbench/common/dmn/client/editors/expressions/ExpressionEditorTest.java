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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorTest {

    private static final String NODE_UUID = "uuid";

    private static final String DRG_NAME = "drg-name";

    @Mock
    private ExpressionEditorView view;

    @Mock
    private DecisionNavigatorPresenter decisionNavigator;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private DMNSession dmnSession;

    @Mock
    private ToolbarStateHandler toolbarStateHandler;

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

    @Mock
    private DMNDiagramElement dmnDiagramElement;

    @Mock
    private DRDNameChanger drdNameChanger;

    @Captor
    private ArgumentCaptor<Optional<HasName>> optionalHasNameCaptor;

    private Decision decision;

    private ExpressionEditor testedEditor;

    private ExpressionGridCache expressionGridCache;

    private Definitions definitions;

    @Before
    public void setUp() {
        this.decision = new Decision();
        this.expressionGridCache = new ExpressionGridCacheImpl();
        this.definitions = new Definitions();
        this.definitions.setName(new Name(DRG_NAME));

        testedEditor = spy(new ExpressionEditor(view,
                                                decisionNavigator,
                                                dmnGraphUtils,
                                                dmnDiagramsSession,
                                                drdNameChanger));
        testedEditor.bind(dmnSession);

        when(session.getCanvasControl(eq(ExpressionGridCache.class))).thenReturn(expressionGridCache);
        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(dmnDiagramElement));
    }

    @Test
    public void testBind() {
        //ExpressionEditor.bind(..) is called in @Before setup
        verify(view).bind(eq(dmnSession));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetExpression() {
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(true);

        setupExpression(decision, decision, toolbarStateHandler, false);

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(decision),
                                   eq(Optional.of(decision)),
                                   eq(false));
        verify(view).setReturnToLinkText(eq(DRG_NAME));
        verify(toolbarStateHandler).enterGridView();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetExpressionWhenOnlyVisualChangeAllowed() {
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(true);

        setupExpression(decision, decision, toolbarStateHandler, true);

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(decision),
                                   eq(Optional.of(decision)),
                                   eq(true));
        verify(view).setReturnToLinkText(eq(DRG_NAME));
        verify(toolbarStateHandler).enterGridView();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExitWithCommand() {
        setupExpression(decision, decision, toolbarStateHandler, false);

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
        setupExpression(decision, decision, null, false);

        verifyNoMoreInteractions(toolbarStateHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExitWithCommandWithoutToolbar() {
        setupExpression(decision, decision, null, false);

        testedEditor.setExitCommand(command);

        testedEditor.exit();

        verify(decisionNavigator).clearSelections();
        verifyNoMoreInteractions(toolbarStateHandler);
        verify(command).execute();
        assertEquals(Optional.empty(), testedEditor.getExitCommand());
    }

    @SuppressWarnings("unchecked")
    private void setupExpression(final HasExpression hasExpression,
                                 final HasName hasName,
                                 final ToolbarStateHandler toolbarStateHandler,
                                 final boolean isOnlyVisualChangeAllowed) {
        testedEditor.setToolbarStateHandler(toolbarStateHandler);

        testedEditor.setExpression(NODE_UUID,
                                   hasExpression,
                                   Optional.of(hasName),
                                   isOnlyVisualChangeAllowed);

        verify(view).setExpression(eq(NODE_UUID),
                                   eq(hasExpression),
                                   eq(Optional.of(hasName)),
                                   eq(isOnlyVisualChangeAllowed));
    }

    @Test
    public void testOnCanvasElementUpdated() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(decision);

        setupExpression(decision, decision, toolbarStateHandler, false);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view).setExpressionNameText(optionalHasNameCaptor.capture());
        verify(view).reloadEditor();

        final Optional<HasName> optionalHasName = optionalHasNameCaptor.getValue();
        assertTrue(optionalHasName.isPresent());
        assertEquals(decision,
                     optionalHasName.get());
    }

    @Test
    public void testOnCanvasElementUpdatedDefinitions() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);
        final String NEW_DRG_NAME = "new-drg-name";

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(definitions);
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(true);

        setupExpression(decision, decision, toolbarStateHandler, false);

        verify(view).setReturnToLinkText(eq(DRG_NAME));

        this.definitions.getName().setValue(NEW_DRG_NAME);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view).setReturnToLinkText(eq(NEW_DRG_NAME));
    }

    @Test
    public void testOnCanvasElementUpdatedBusinessKnowledgeModel() {
        final BusinessKnowledgeModel bkm = new BusinessKnowledgeModel();
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(bkm);

        setupExpression(bkm.asHasExpression(), bkm, toolbarStateHandler, false);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view).setExpressionNameText(optionalHasNameCaptor.capture());
        verify(view).reloadEditor();

        final Optional<HasName> optionalHasName = optionalHasNameCaptor.getValue();
        assertTrue(optionalHasName.isPresent());
        assertEquals(bkm,
                     optionalHasName.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnCanvasElementUpdatedDifferentNode() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        final Decision differentNodeDefinition = mock(Decision.class);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(differentNodeDefinition);

        setupExpression(decision, decision, toolbarStateHandler, false);

        testedEditor.handleCanvasElementUpdated(event);

        verify(view, never()).setExpressionNameText(any(Optional.class));
    }

    @Test
    public void testIsActiveWhenExpressionEditorIsNotActive() {
        testedEditor.setExitCommand(null);
        assertFalse(testedEditor.isActive());
    }

    @Test
    public void testIsActiveWhenExpressionEditorIsActive() {
        testedEditor.setExitCommand(mock(Command.class));
        assertTrue(testedEditor.isActive());
    }

    @Test
    public void testSetReturnToLinkTextWhenDrdIsSelected() {
        final String drdName = "DRD Name";
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(false);
        when(dmnDiagramElement.getName()).thenReturn(new Name(drdName));
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(dmnDiagramElement));

        setupExpression(decision, decision, toolbarStateHandler, false);

        verify(view).setReturnToLinkText(eq(drdName));
    }
}
