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

package org.kie.workbench.common.dmn.client.commands.expressions.types.invocation;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteParameterBindingCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Invocation invocation;

    private Binding binding;

    private GridData uiModel;

    private GridRow uiGridRow;

    private DeleteParameterBindingCommand command;

    @Before
    public void setup() {
        this.invocation = new Invocation();
        this.binding = makeBinding("p" + invocation.getBinding().size());
        this.invocation.getBinding().add(binding);

        this.uiModel = new BaseGridData(false);
        this.uiGridRow = new BaseGridRow();
        this.uiModel.appendRow(uiGridRow);
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();
    }

    private void makeCommand(final int uiRowIndex) {
        this.command = spy(new DeleteParameterBindingCommand(invocation,
                                                             uiModel,
                                                             uiRowIndex,
                                                             canvasOperation));
    }

    private Binding makeBinding(final String bindingName) {
        final Binding newBinding = new Binding();
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name(bindingName));
        newBinding.setParameter(parameter);
        return newBinding;
    }

    private void assertRowValues(final int rowNumberIndex, final int rowNumberValue, final String nameColumnValue) {
        assertEquals(2,
                     uiModel.getRows().get(rowNumberIndex).getCells().size());
        assertEquals(rowNumberValue,
                     uiModel.getCell(rowNumberIndex, ROW_NUMBER_COLUMN_INDEX).getValue().getValue());
        assertEquals(nameColumnValue,
                     uiModel.getCell(rowNumberIndex, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue());
        assertNull(uiModel.getCell(rowNumberIndex, BINDING_EXPRESSION_COLUMN_INDEX));
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand(0);
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand(0);
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     invocation.getBinding().size());
    }

    @Test
    public void testGraphCommandExecuteRemoveFromMiddle() {
        final Binding firstBinding = new Binding();
        final Binding lastBinding = new Binding();
        invocation.getBinding().add(0, firstBinding);
        invocation.getBinding().add(lastBinding);
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        Assertions.assertThat(invocation.getBinding()).containsExactly(firstBinding, lastBinding);
    }

    @Test
    public void testGraphCommandUndo() {
        makeCommand(0);
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     invocation.getBinding().size());
        assertEquals(binding,
                     invocation.getBinding().get(0));
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand(0);
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand(0);
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteDeleteOneOfThree() {
        final Binding secondBinding = new Binding();
        final Binding thirdBinding = new Binding();
        invocation.getBinding().add(secondBinding);
        invocation.getBinding().add(thirdBinding);
        final BaseGridRow secondRow = new BaseGridRow();
        final BaseGridRow thirdRow = new BaseGridRow();
        uiModel.appendRow(secondRow);
        uiModel.appendRow(thirdRow);

        makeCommand(1);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(uiGridRow,
                     uiModel.getRow(0));
        assertEquals(thirdRow,
                     uiModel.getRow(1));
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoColumns() {
        makeCommand(0);
        //Delete Parameter Binding and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(uiGridRow,
                     uiModel.getRows().get(0));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }
}
