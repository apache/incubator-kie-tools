/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.expressions.types.invocation;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Binding;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddParameterBindingCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private ExpressionEditorDefinitions expressionEditorDefinitions;

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

    private DMNGridRow uiModelRow;

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

        this.uiModel = new BaseGridData();
        this.uiModelRow = new DMNGridRow();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        this.uiModelMapper = new InvocationUIModelMapper(() -> uiModel,
                                                         () -> Optional.of(invocation),
                                                         () -> expressionEditorDefinitions);

        this.command = new AddParameterBindingCommand(invocation,
                                                      binding,
                                                      uiModel,
                                                      uiModelRow,
                                                      uiModelMapper,
                                                      canvasOperation);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();

        doReturn(Optional.empty()).when(expressionEditorDefinitions).getExpressionEditorDefinition(any(Optional.class));
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithParameters() {
        final Binding otherBinding = new Binding();
        invocation.getBinding().add(otherBinding);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(2,
                                 otherBinding, binding);
    }

    @Test
    public void testGraphCommandExecuteWithNoParameters() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertBindingDefinitions(1,
                                 binding);
    }

    @Test
    public void testGraphCommandUndoWithParameters() {
        final Binding otherBinding = new Binding();
        invocation.getBinding().add(otherBinding);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions(1,
                                 otherBinding);
    }

    @Test
    public void testGraphCommandUndoWithNoParameters() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertBindingDefinitions(0);
    }

    private void assertBindingDefinitions(final int expectedCount,
                                          final Binding... bindings) {
        assertEquals(expectedCount,
                     invocation.getBinding().size());
        for (int i = 0; i < expectedCount; i++) {
            assertEquals(bindings[i],
                         invocation.getBinding().get(i));
        }
    }

    @Test
    public void testCanvasCommandAllow() {
        //There are no Canvas mutations by the
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
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
        assertEquals("p0",
                     uiModel.getCell(0, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue());
        assertNull(uiModel.getCell(0, BINDING_EXPRESSION_COLUMN_INDEX));

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteMultipleEntries() {
        // first row
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> firstEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     firstEntryCanvasCommand.execute(handler));

        // second row
        final Binding secondRowEntry = new Binding();
        final InformationItem parameter = new InformationItem();
        parameter.setName(new Name("last entry"));
        secondRowEntry.setParameter(parameter);
        final DMNGridRow uiSecondModelRow = new DMNGridRow();
        command = new AddParameterBindingCommand(invocation,
                                                 secondRowEntry,
                                                 uiModel,
                                                 uiSecondModelRow,
                                                 uiModelMapper,
                                                 canvasOperation);
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> secondEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     secondEntryCanvasCommand.execute(handler));

        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(uiSecondModelRow,
                     uiModel.getRows().get(1));
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
        assertEquals("p0",
                     uiModel.getCell(0, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue());
        assertNull(uiModel.getCell(0, BINDING_EXPRESSION_COLUMN_INDEX));
        assertEquals(2,
                     uiModel.getCell(1, ROW_NUMBER_COLUMN_INDEX).getValue().getValue());
        assertEquals("last entry",
                     uiModel.getCell(1, BINDING_PARAMETER_COLUMN_INDEX).getValue().getValue());
        assertNull(uiModel.getCell(1, BINDING_EXPRESSION_COLUMN_INDEX));

        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
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
