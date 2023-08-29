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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.UpdateCanvasNodeNameCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionStateTest {

    @Mock(extraInterfaces = {HasVariable.class, HasName.class})
    private HasExpression hasExpression;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private UpdateCanvasNodeNameCommand updateCanvasNodeCommand;

    @Mock
    private HasName hasName;

    private static final String NODE_UUID = "uuid";

    private ExpressionState expressionState;

    @Before
    public void setup() {
        expressionState = spy(new ExpressionState(hasExpression,
                                                  editorSelectedEvent,
                                                  NODE_UUID,
                                                  Optional.of(hasName),
                                                  updateCanvasNodeCommand));
    }

    @Test
    public void testSetExpressionName() {

        final String expressionName = "expression name";

        expressionState.setExpressionName(expressionName);

        verify(updateCanvasNodeCommand).execute(NODE_UUID,
                                                hasName);
    }

    @Test
    public void testGetFallbackHasName_WhenHasExpressionIsNotHasName() {

        final HasExpression hasExpressionNotHasName = mock(HasExpression.class);
        doReturn(hasExpressionNotHasName).when(expressionState).getHasExpression();

        final HasName hasNameFallback = expressionState.getFallbackHasName();

        assertEquals(HasName.NOP, hasNameFallback);
    }

    @Test
    public void testGetFallbackHasName_WhenHasExpressionIsHasName() {

        final HasName hasNameFallback = expressionState.getFallbackHasName();

        assertEquals(hasExpression, hasNameFallback);
    }

    @Test
    public void testApply() {

        doNothing().when(expressionState).restoreExpression();
        doNothing().when(expressionState).restoreTypeRef();
        doNothing().when(expressionState).restoreExpressionName();
        doNothing().when(expressionState).fireEditorSelectedEvent();

        final InOrder inOrder = inOrder(expressionState);

        expressionState.apply();

        inOrder.verify(expressionState).restoreExpression();
        inOrder.verify(expressionState).restoreTypeRef();
        inOrder.verify(expressionState).restoreExpressionName();
        inOrder.verify(expressionState).fireEditorSelectedEvent();
    }

    @Test
    public void testFireEditorSelectedEvent() {

        final ArgumentCaptor<ExpressionEditorChanged> eventCaptor = ArgumentCaptor.forClass(ExpressionEditorChanged.class);

        expressionState.fireEditorSelectedEvent();

        verify(editorSelectedEvent).fire(eventCaptor.capture());

        final ExpressionEditorChanged eventArg = eventCaptor.getValue();

        assertEquals(NODE_UUID, eventArg.getNodeUUID());
    }

    @Test
    public void testRestoreExpressionName() {

        final String savedName = "saved name";

        expressionState.setSavedExpressionName(savedName);

        expressionState.restoreExpressionName();

        verify(expressionState).setExpressionName(savedName);
    }

    @Test
    public void testRestoreExpression() {

        final Expression savedExpression = mock(Expression.class);

        expressionState.setSavedExpression(savedExpression);

        expressionState.restoreExpression();

        verify(hasExpression).setExpression(savedExpression);
    }

    @Test
    public void testRestoreExpressionBKMNode() {

        final FunctionDefinition savedExpression = mock(FunctionDefinition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        final FunctionDefinition fd = mock(FunctionDefinition.class);
        final DMNModelInstrumentedBase parent = mock(DMNModelInstrumentedBase.class);

        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(bkm);
        when(bkm.getEncapsulatedLogic()).thenReturn(fd);
        when(fd.getParent()).thenReturn(parent);

        expressionState.setSavedExpression(savedExpression);

        expressionState.restoreExpression();

        final InOrder inOrder = inOrder(bkm, fd);

        verify(hasExpression, never()).setExpression(savedExpression);
        inOrder.verify(bkm, times(1)).setEncapsulatedLogic(savedExpression);
        inOrder.verify(fd, times(1)).setParent(parent);
    }

    @Test
    public void testRestoreExpression_WhenThereIsNot() {

        expressionState.setSavedExpression(null);

        expressionState.restoreExpression();

        verify(hasExpression).setExpression(null);
    }

    @Test
    public void testRestoreTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName savedTypeRef = mock(QName.class);

        expressionState.setSavedTypeRef(savedTypeRef);

        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);

        expressionState.restoreTypeRef();

        verify(variable).setTypeRef(savedTypeRef);
    }

    @Test
    public void testSaveCurrentState() {

        doNothing().when(expressionState).saveCurrentExpressionName();
        doNothing().when(expressionState).saveCurrentTypeRef();
        doNothing().when(expressionState).saveCurrentExpression();

        expressionState.saveCurrentState();

        verify(expressionState).saveCurrentExpressionName();
        verify(expressionState).saveCurrentTypeRef();
        verify(expressionState).saveCurrentExpression();
    }

    @Test
    public void testSaveCurrentExpressionName() {

        final String value = "expression name";
        final Name name = new Name(value);

        when(hasName.getName()).thenReturn(name);

        expressionState.saveCurrentExpressionName();

        verify(expressionState).setSavedExpressionName(value);
    }

    @Test
    public void testGetCurrentTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName currentTypeRef = mock(QName.class);

        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);

        when(variable.getTypeRef()).thenReturn(currentTypeRef);

        final QName actual = expressionState.getCurrentTypeRef();

        assertEquals(currentTypeRef, actual);
    }

    @Test
    public void testGetCurrentTypeRef_WhenHasExpressionIsNotHasVariable() {

        final HasExpression localHasExpression = mock(HasExpression.class);
        doReturn(localHasExpression).when(expressionState).getHasExpression();

        final QName actual = expressionState.getCurrentTypeRef();

        assertEquals(BuiltInType.UNDEFINED.asQName(), actual);
    }

    @Test
    public void testSaveCurrentTypeRef() {

        final QName currentTypeRef = mock(QName.class);

        doReturn(currentTypeRef).when(expressionState).getCurrentTypeRef();

        expressionState.saveCurrentTypeRef();

        verify(expressionState).setSavedTypeRef(currentTypeRef);
    }

    @Test
    public void testSaveCurrentExpression_WhenExpressionIsNull() {

        when(hasExpression.getExpression()).thenReturn(null);

        expressionState.saveCurrentExpression();

        verify(expressionState).setSavedExpression(null);
    }

    @Test
    public void testSaveCurrentExpression_WhenExpressionIsNotNull() {

        final Expression expression = mock(Expression.class);
        final Expression expressionCopy = mock(Expression.class);

        when(expression.exactCopy()).thenReturn(expressionCopy);
        when(hasExpression.getExpression()).thenReturn(expression);

        expressionState.saveCurrentExpression();

        final Expression saved = expressionState.getSavedExpression();

        assertEquals(expressionCopy, saved);
    }
}
