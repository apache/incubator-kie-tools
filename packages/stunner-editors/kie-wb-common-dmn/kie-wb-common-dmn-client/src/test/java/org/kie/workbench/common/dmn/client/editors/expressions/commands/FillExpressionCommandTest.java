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

package org.kie.workbench.common.dmn.client.editors.expressions.commands;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FillExpressionCommandTest {

    @Mock
    private Expression newExpression;

    @Mock(extraInterfaces = {HasName.class, HasVariable.class})
    private HasExpression hasExpression;

    private ExpressionProps expressionProps;

    @Mock
    private Event<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private ExpressionEditorView view;

    @Mock
    private Expression existingExpression;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    private final String nodeUUID = "uuid";

    private final String name = "name";

    private final String dataType = "data type";

    private FillExpressionCommandMock command;

    @Before
    public void setup() {

        String logicType = "logic type";
        expressionProps = new ExpressionProps("id",
                                              name,
                                              dataType,
                                              logicType);

        when(hasExpression.getExpression()).thenReturn(existingExpression);

        command = spy(new FillExpressionCommandMock(hasExpression,
                                                    expressionProps,
                                                    editorSelectedEvent,
                                                    nodeUUID));
    }

    @Test
    public void testExecute() {

        final InOrder inOrder = Mockito.inOrder(command, view);

        doNothing().when(command).setExpressionName(expressionProps.name);
        doNothing().when(command).setTypeRef(any());

        command.execute();

        inOrder.verify(command).fireEditorSelectedEvent();
        inOrder.verify(command).setExpressionName(expressionProps.name);
        inOrder.verify(command).setTypeRef(dataType);
        inOrder.verify(command).createExpression();
        inOrder.verify(command).fill();
    }

    @Test
    public void testCreateExpression() {

        when(hasExpression.getExpression()).thenReturn(null);

        command.createExpression();

        verify(hasExpression).setExpression(newExpression);
    }

    @Test
    public void testFireEditorSelectedEvent() {

        final ArgumentCaptor<ExpressionEditorChanged> captor = ArgumentCaptor.forClass(ExpressionEditorChanged.class);

        command.fireEditorSelectedEvent();

        verify(editorSelectedEvent).fire(captor.capture());

        final ExpressionEditorChanged editorChanged = captor.getValue();

        assertEquals(nodeUUID, editorChanged.getNodeUUID());
    }

    @Test
    public void testSetTypeRef() {

        final InformationItemPrimary variable = mock(InformationItemPrimary.class);
        final QName typeRef = mock(QName.class);

        doReturn(typeRef).when(command).getTypeRef(dataType);
        when(((HasVariable) hasExpression).getVariable()).thenReturn(variable);

        command.setTypeRef(dataType);

        verify(variable).setTypeRef(typeRef);
    }

    @Test
    public void testSetExpressionName() {

        final Name currentName = mock(Name.class);
        when(((HasName) hasExpression).getName()).thenReturn(currentName);

        command.setExpressionName(expressionProps.name);

        verify(currentName).setValue(name);
    }

    @Test
    public void testGetTypeRef() {

        for (final BuiltInType value : BuiltInType.values()) {
            final QName result = command.getTypeRef(value.getName());
            assertEquals(value.asQName(), result);
        }
    }

    @Test
    public void testGetTypeRef_WhenIsAUnknownTypeRef() {

        final QName result = command.getTypeRef("unknown");

        assertEquals(BuiltInType.UNDEFINED.asQName(), result);
    }

    @Test
    public void testGetTypeRef_WhenIsACustomTypeRef() {
        final String customName = "custom";
        final ItemDefinition customItemDefinition = new ItemDefinition();
        customItemDefinition.setName(new Name(customName));

        when(itemDefinitionUtils.findByName(customName)).thenReturn(Optional.of(customItemDefinition));

        final QName result = command.getTypeRef(customName);

        assertEquals(customName, result.getLocalPart());
    }

    class FillExpressionCommandMock extends FillExpressionCommand {

        public FillExpressionCommandMock(final HasExpression hasExpression,
                                         final ExpressionProps expressionProps,
                                         final Event<ExpressionEditorChanged> editorSelectedEvent,
                                         final String nodeUUID) {
            super(hasExpression, expressionProps, editorSelectedEvent, nodeUUID, itemDefinitionUtils, Optional.empty());
        }

        @Override
        protected void fill() {
        }

        @Override
        public boolean isCurrentExpressionOfTheSameType() {
            return false;
        }

        @Override
        protected Expression getNewExpression() {
            return newExpression;
        }
    }
}
