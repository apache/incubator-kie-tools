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

package org.kie.workbench.common.dmn.client.decision.factories;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.DECISION_TABLE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorNestedItemFactoryTest {

    private static final String DECISION_TABLE_DEFINITION_NAME = "Decision Table";

    @Mock
    private SessionManager sessionManager;

    @Mock
    private EventSourceMock<EditExpressionEvent> editExpressionEvent;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private ExpressionEditorDefinition decisionTableEditorDefinition;

    private DecisionNavigatorNestedItemFactory factory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        factory = spy(new DecisionNavigatorNestedItemFactory(sessionManager,
                                                             editExpressionEvent,
                                                             decisionNavigatorPresenter,
                                                             expressionEditorDefinitionsSupplier,
                                                             canvasSelectionEvent));

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(decisionTableEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(decisionTableEditorDefinition.getModelClass()).thenReturn(Optional.of(new DecisionTable()));
        when(decisionTableEditorDefinition.getName()).thenReturn(DECISION_TABLE_DEFINITION_NAME);
    }

    @Test
    public void testMakeItem() {

        final String uuid = "uuid";
        final String label = "label";
        final DecisionNavigatorItem.Type type = DECISION_TABLE;
        final Command command = mock(Command.class);
        final String parentUUID = "parentUUID";

        when(node.getUUID()).thenReturn(parentUUID);
        doReturn(uuid).when(factory).getUUID(node);
        doReturn(label).when(factory).getLabel(node);
        doReturn(type).when(factory).getType(node);
        doReturn(command).when(factory).makeOnClickCommand(node, parentUUID);

        final DecisionNavigatorItem item = factory.makeItem(node);

        assertEquals(uuid, item.getUUID());
        assertEquals(label, item.getLabel());
        assertEquals(type, item.getType());
        assertEquals(command, item.getOnClick());
        assertEquals(parentUUID, item.getParentUUID());
    }

    @Test
    public void testMakeOnClickCommand() {

        final EditExpressionEvent expressionEvent = mock(EditExpressionEvent.class);
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final CanvasSelectionEvent event = mock(CanvasSelectionEvent.class);
        final String uuid = "uuid";

        when(decisionNavigatorPresenter.getHandler()).thenReturn(canvasHandler);

        doReturn(event).when(factory).makeCanvasSelectionEvent(canvasHandler, uuid);
        doReturn(expressionEvent).when(factory).makeEditExpressionEvent(node);

        factory.makeOnClickCommand(node, uuid).execute();

        verify(canvasSelectionEvent).fire(event);
        verify(editExpressionEvent).fire(expressionEvent);
    }

    @Test
    public void testMakeCanvasSelectionEvent() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final String uuid = "uuid";
        final CanvasSelectionEvent event = factory.makeCanvasSelectionEvent(canvasHandler, uuid);

        assertEquals(canvasHandler, event.getCanvasHandler());
        assertEquals(uuid, event.getIdentifiers().iterator().next());
    }

    @Test
    public void testMakeEditExpressionEvent() {

        final ClientSession currentSession = mock(ClientSession.class);
        final HasName hasName = mock(HasName.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final String uuid = "uuid";

        when(node.getUUID()).thenReturn(uuid);
        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        doReturn(hasName).when(factory).getDefinition(node);
        doReturn(hasExpression).when(factory).getHasExpression(node);

        final EditExpressionEvent expressionEvent = factory.makeEditExpressionEvent(node);

        assertEquals(uuid, expressionEvent.getNodeUUID());
        assertEquals(currentSession, expressionEvent.getSession());
        assertEquals(Optional.of(hasName), expressionEvent.getHasName());
        assertEquals(hasExpression, expressionEvent.getHasExpression());
    }

    @Test
    public void testGetUUID() {

        final Expression expression = mock(Expression.class);
        final Id id = mock(Id.class);
        final String expectedUUID = "uuid";

        doReturn(expression).when(factory).getExpression(node);
        when(expression.getId()).thenReturn(id);
        when(id.getValue()).thenReturn(expectedUUID);

        final String actualUUID = factory.getUUID(node);

        assertEquals(expectedUUID, actualUUID);
    }

    @Test
    public void testGetLabel() {

        final DecisionTable expression = new DecisionTable();

        doReturn(expression).when(factory).getExpression(node);

        final String actualLabel = factory.getLabel(node);

        assertEquals(DECISION_TABLE_DEFINITION_NAME, actualLabel);
    }

    @Test
    public void testGetType() {

        final DecisionTable expression = new DecisionTable();
        final DecisionNavigatorItem.Type expectedType = DECISION_TABLE;

        doReturn(expression).when(factory).getExpression(node);

        final DecisionNavigatorItem.Type actualType = factory.getType(node);

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testHasNestedElementWhenNodeHasExpressionIsNull() {

        final Optional<HasExpression> hasExpression = Optional.empty();
        final Optional<Expression> expression = Optional.empty();

        doReturn(hasExpression).when(factory).getOptionalHasExpression(node);
        doReturn(expression).when(factory).getOptionalExpression(node);

        assertFalse(factory.hasNestedElement(node));
    }

    @Test
    public void testHasNestedElementWhenNodeExpressionIsNull() {

        final Optional<HasExpression> hasExpression = Optional.ofNullable(mock(HasExpression.class));
        final Optional<Expression> expression = Optional.empty();

        doReturn(hasExpression).when(factory).getOptionalHasExpression(node);
        doReturn(expression).when(factory).getOptionalExpression(node);

        assertFalse(factory.hasNestedElement(node));
    }

    @Test
    public void testHasNestedElementWhenNodeHasNestedElement() {

        final Optional<HasExpression> hasExpression = Optional.ofNullable(mock(HasExpression.class));
        final Optional<Expression> expression = Optional.ofNullable(mock(Expression.class));

        doReturn(hasExpression).when(factory).getOptionalHasExpression(node);
        doReturn(expression).when(factory).getOptionalExpression(node);

        assertTrue(factory.hasNestedElement(node));
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsBusinessKnowledgeModel() {

        final View content = mock(View.class);
        final BusinessKnowledgeModel businessKnowledgeModel = new BusinessKnowledgeModel();
        final FunctionDefinition encapsulatedLogic = mock(FunctionDefinition.class);
        businessKnowledgeModel.setEncapsulatedLogic(encapsulatedLogic);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(businessKnowledgeModel);

        final Optional<HasExpression> actualHasExpression = factory.getOptionalHasExpression(node);

        assertTrue(actualHasExpression.isPresent());
        assertEquals(businessKnowledgeModel, actualHasExpression.get().asDMNModelInstrumentedBase());
        assertEquals(encapsulatedLogic, actualHasExpression.get().getExpression());
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsDecision() {

        final View content = mock(View.class);
        final Decision expectedHasExpression = mock(Decision.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(expectedHasExpression);

        final Optional<HasExpression> actualHasExpression = factory.getOptionalHasExpression(node);

        assertTrue(actualHasExpression.isPresent());
        assertEquals(expectedHasExpression, actualHasExpression.get());
    }

    @Test
    public void testGetOptionalHasExpressionWhenNodeIsOtherDRGElement() {

        final View content = mock(View.class);
        final InputData expectedHasExpression = mock(InputData.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(expectedHasExpression);

        final Optional<HasExpression> actualHasExpression = factory.getOptionalHasExpression(node);

        assertFalse(actualHasExpression.isPresent());
    }
}
