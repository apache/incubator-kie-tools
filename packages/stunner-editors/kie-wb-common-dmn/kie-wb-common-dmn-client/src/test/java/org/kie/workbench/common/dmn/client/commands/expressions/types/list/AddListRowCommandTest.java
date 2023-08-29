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

package org.kie.workbench.common.dmn.client.commands.expressions.types.list;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
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
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.ROW_COLUMN_INDEX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddListRowCommandTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

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

    @Mock
    private GridCellTuple parent;

    @Mock
    private ExpressionEditorDefinition<Expression> literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    private List list;

    private HasExpression hasExpression;

    private GridData uiModel;

    private GridRow uiModelRow;

    private ListUIModelMapper uiModelMapper;

    private AddListRowCommand command;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.list = new List();
        this.hasExpression = HasExpression.wrap(list, new LiteralExpression());
        this.uiModel = new BaseGridData();
        this.uiModelRow = new BaseGridRow();

        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        when(gridWidget.getModel()).thenReturn(uiModel);
        when(handler.getRuleManager()).thenReturn(ruleManager);
        when(uiRowNumberColumn.getIndex()).thenReturn(0);
        when(uiExpressionEditorColumn.getIndex()).thenReturn(1);

        this.uiModel.setCellValue(0,
                                  EXPRESSION_COLUMN_INDEX,
                                  new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(literalExpressionEditor.getParentInformation()).thenReturn(parent);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpression()));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));
        this.uiModelMapper = spy(new ListUIModelMapper(gridWidget,
                                                       () -> uiModel,
                                                       () -> Optional.of(list),
                                                       () -> false,
                                                       () -> expressionEditorDefinitions,
                                                       listSelector,
                                                       0));
    }

    private void makeCommand() {
        this.command = spy(new AddListRowCommand(list,
                                                 hasExpression,
                                                 uiModel,
                                                 uiModelRow,
                                                 list.getExpression().size(),
                                                 uiModelMapper,
                                                 canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(1,
                     list.getExpression().size());
        assertEquals(hasExpression,
                     list.getExpression().get(0));

        assertEquals(list,
                     hasExpression.getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteMultipleEntriesPresent() {
        final HasExpression firstEntry = HasExpression.wrap(list, new LiteralExpression());
        list.getExpression().add(0, firstEntry);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     list.getExpression().size());
        assertEquals(firstEntry,
                     list.getExpression().get(0));
        assertEquals(hasExpression,
                     list.getExpression().get(1));

        assertEquals(list,
                     hasExpression.getExpression().getParent());
    }

    @Test
    public void testGraphCommandUndo() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(0,
                     list.getExpression().size());

        assertNull(hasExpression.asDMNModelInstrumentedBase().getParent());
    }

    @Test
    public void testGraphCommandUndoMultipleEntriesPresent() {
        final HasExpression firstEntry = HasExpression.wrap(list, new LiteralExpression());
        list.getExpression().add(0, firstEntry);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     list.getExpression().size());
        assertEquals(firstEntry,
                     list.getExpression().get(0));
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand();

        //Add Graph row first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(1));

        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, ROW_COLUMN_INDEX).getValue().getValue());
        assertTrue(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteMultipleEntries() {
        makeCommand();

        // first row
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> firstEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     firstEntryCanvasCommand.execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        // second row
        final HasExpression secondRowEntry = HasExpression.wrap(list, new LiteralExpression());
        final GridRow uiSecondModelRow = new BaseGridRow();
        command = spy(new AddListRowCommand(list,
                                            secondRowEntry,
                                            uiModel,
                                            uiSecondModelRow,
                                            list.getExpression().size(),
                                            uiModelMapper,
                                            canvasOperation));
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> secondEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     secondEntryCanvasCommand.execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(uiSecondModelRow,
                     uiModel.getRows().get(1));
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(ROW_COLUMN_INDEX));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(EXPRESSION_COLUMN_INDEX));

        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, ROW_COLUMN_INDEX).getValue().getValue());
        assertTrue(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);

        assertEquals(2,
                     uiModel.getRows().get(1).getCells().size());
        assertEquals(2,
                     uiModel.getCell(1, ROW_COLUMN_INDEX).getValue().getValue());
        assertTrue(uiModel.getCell(1, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);

        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        makeCommand();

        //Add Graph row first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(ROW_COLUMN_INDEX));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(EXPRESSION_COLUMN_INDEX));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoRedoReuseEditor() {
        makeCommand();

        //Add Graph row first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add row and assert editor instance
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        verify(uiModelMapper).fromDMNModel(0,
                                           ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);

        final GridCellValue<?> originalCellValue = uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue();
        assertTrue(originalCellValue instanceof ExpressionCellValue);
        final BaseExpressionGrid originalEditor = ((ExpressionCellValue) originalCellValue).getValue().get();
        assertEquals(literalExpressionEditor, originalEditor);

        //Undo addition
        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        //Redo addition and assert the same editor instance was reused
        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        verify(uiModelMapper).fromDMNModel(0,
                                           ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX);

        final GridCellValue<?> redoCellValue = uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue();
        assertTrue(redoCellValue instanceof ExpressionCellValue);
        final BaseExpressionGrid redoEditor = ((ExpressionCellValue) redoCellValue).getValue().get();
        assertEquals(literalExpressionEditor, redoEditor);
    }
}
