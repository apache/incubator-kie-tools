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

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddParameterBindingCommandTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private ExpressionEditorDefinitions expressionEditorDefinitions;

    @Mock
    private ListSelectorView.Presenter listSelector;

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

    private GridRow uiModelRow;

    private InvocationUIModelMapper uiModelMapper;

    private AddParameterBindingCommand command;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.invocation = new Invocation();
        this.binding = new Binding();
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name("p" + invocation.getBinding().size()));
        this.binding.setParameter(parameter);

        this.uiModel = new BaseGridData(false);
        this.uiModelRow = new BaseGridRow();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        this.uiModelMapper = new InvocationUIModelMapper(gridWidget,
                                                         () -> uiModel,
                                                         () -> Optional.of(invocation),
                                                         () -> false,
                                                         () -> expressionEditorDefinitions,
                                                         listSelector,
                                                         0);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();
        doReturn(uiModel).when(gridWidget).getModel();

        doReturn(Optional.empty()).when(expressionEditorDefinitions).getExpressionEditorDefinition(any(Optional.class));
    }

    private void makeCommand(final int uiRowIndex,
                             final Binding rowBindingEntry,
                             final GridRow uiGridRow) {
        this.command = new AddParameterBindingCommand(invocation,
                                                      rowBindingEntry,
                                                      uiModel,
                                                      uiGridRow,
                                                      uiRowIndex,
                                                      uiModelMapper,
                                                      canvasOperation);
    }

    private void makeCommand() {
        makeCommand(invocation.getBinding().size(), binding, uiModelRow);
    }

    private void makeCommand(final int uiRowIndex) {
        makeCommand(uiRowIndex, binding, uiModelRow);
    }

    private void makeCommand(final int uiRowIndex,
                             final GridRow uiGridRow) {
        final Binding rowEntry = new Binding();
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name());
        rowEntry.setParameter(parameter);
        makeCommand(uiRowIndex, rowEntry, uiGridRow);
    }

    private void assertBindingDefinitions(final Binding... bindings) {
        Assertions.assertThat(invocation.getBinding()).containsExactly(bindings);

        assertEquals(InvocationDefaultValueUtilities.PREFIX + "1",
                     binding.getParameter().getName().getValue());
    }

    private void assertRowValues(final int rowNumberIndex, final int rowNumberValue, final String nameColumnValue) {
        assertEquals(2,
                     uiModel.getRows().get(rowNumberIndex).getCells().size());
        assertEquals(rowNumberValue,
                     uiModel.getCell(rowNumberIndex, ROW_NUMBER_COLUMN_INDEX).getValue().getValue());
        assertEquals(nameColumnValue,
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(rowNumberIndex, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue()).getName().getValue());
        assertNull(uiModel.getCell(rowNumberIndex, BINDING_EXPRESSION_COLUMN_INDEX));
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithParameters() {
        final Binding otherBinding = new Binding();
        invocation.getBinding().add(otherBinding);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(otherBinding, binding);

        assertEquals(invocation,
                     binding.getParent());
        assertEquals(binding,
                     binding.getParameter().getParent());
    }

    @Test
    public void testGraphCommandExecuteInsertToFirstPlace() {
        final Binding firstBinding = new Binding();
        final Binding secondBinding = new Binding();
        invocation.getBinding().add(firstBinding);
        invocation.getBinding().add(secondBinding);

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(binding, firstBinding, secondBinding);

        assertEquals(invocation,
                     binding.getParent());
        assertEquals(binding,
                     binding.getParameter().getParent());
    }

    @Test
    public void testGraphCommandExecuteInsertIntoMiddle() {
        final Binding firstBinding = new Binding();
        final Binding secondBinding = new Binding();
        invocation.getBinding().add(firstBinding);
        invocation.getBinding().add(secondBinding);

        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(firstBinding, binding, secondBinding);

        assertEquals(invocation,
                     binding.getParent());
        assertEquals(binding,
                     binding.getParameter().getParent());
    }

    @Test
    public void testGraphCommandExecuteWithNoParameters() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(binding);

        assertEquals(invocation,
                     binding.getParent());
        assertEquals(binding,
                     binding.getParameter().getParent());
    }

    @Test
    public void testGraphCommandUndoWithParameters() {
        final Binding otherBinding = new Binding();
        invocation.getBinding().add(otherBinding);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions(otherBinding);
    }

    @Test
    public void testGraphCommandUndoFromStart() {
        final Binding firstBinding = new Binding();
        final Binding secondBinding = new Binding();
        invocation.getBinding().add(firstBinding);
        invocation.getBinding().add(secondBinding);

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions(firstBinding, secondBinding);
    }

    @Test
    public void testGraphCommandUndoFromMiddle() {
        final Binding firstBinding = new Binding();
        final Binding secondBinding = new Binding();
        invocation.getBinding().add(firstBinding);
        invocation.getBinding().add(secondBinding);

        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions(firstBinding, secondBinding);
    }

    @Test
    public void testGraphCommandUndoWithNoParameters() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions();
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand();

        //There are no Canvas mutations by the
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand();

        //Add Graph entry first as InvocationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(ROW_NUMBER_COLUMN_INDEX));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(BINDING_PARAMETER_COLUMN_INDEX));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(BINDING_EXPRESSION_COLUMN_INDEX));
        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, ROW_NUMBER_COLUMN_INDEX).getValue().getValue());
        assertEquals(InvocationDefaultValueUtilities.PREFIX + "1",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue()).getName().getValue());
        assertNull(uiModel.getCell(0, BINDING_EXPRESSION_COLUMN_INDEX));

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteMultipleEntries() {
        // first row
        makeCommand();
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> firstEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     firstEntryCanvasCommand.execute(handler));

        // second row
        final GridRow uiSecondModelRow = new BaseGridRow();
        makeCommand(1, uiSecondModelRow);
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> secondEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     secondEntryCanvasCommand.execute(handler));

        // third row
        final GridRow uiThirdModelRow = new BaseGridRow();
        makeCommand(0, uiThirdModelRow);
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> thirdEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     thirdEntryCanvasCommand.execute(handler));

        assertEquals(3,
                     uiModel.getRowCount());
        assertEquals(uiThirdModelRow,
                     uiModel.getRows().get(0));
        assertEquals(uiModelRow,
                     uiModel.getRows().get(1));
        assertEquals(uiSecondModelRow,
                     uiModel.getRows().get(2));
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(ROW_NUMBER_COLUMN_INDEX));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(BINDING_PARAMETER_COLUMN_INDEX));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(BINDING_EXPRESSION_COLUMN_INDEX));

        assertRowValues(0, 1, InvocationDefaultValueUtilities.PREFIX + "3");
        assertRowValues(1, 2, InvocationDefaultValueUtilities.PREFIX + "1");
        assertRowValues(2, 3, InvocationDefaultValueUtilities.PREFIX + "2");

        verify(canvasOperation, times(3)).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        makeCommand();

        //Add Graph entry first as InvocationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add Binding and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(ROW_NUMBER_COLUMN_INDEX));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(BINDING_PARAMETER_COLUMN_INDEX));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(BINDING_EXPRESSION_COLUMN_INDEX));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(canvasOperation).execute();
    }
}
