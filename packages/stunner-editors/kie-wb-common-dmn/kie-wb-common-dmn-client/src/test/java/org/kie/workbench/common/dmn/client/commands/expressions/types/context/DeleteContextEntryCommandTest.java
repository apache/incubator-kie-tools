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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteContextEntryCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private ExpressionEditorColumn uiModelColumn;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Context context;

    private GridData uiModel;

    private DeleteContextEntryCommand command;

    @Before
    public void setup() {
        this.context = new Context();
        this.context.getContextEntry().add(new ContextEntry());
        this.uiModel = new BaseGridData(false);
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();
    }

    private void makeCommand(final int deleteFromUiRowIndex) {
        this.command = spy(new DeleteContextEntryCommand(context,
                                                         uiModel,
                                                         deleteFromUiRowIndex,
                                                         canvasOperation));
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
                     context.getContextEntry().size());
    }

    @Test
    public void testGraphCommandExecuteMultipleRows() {
        addContextEntries(3);
        final ContextEntry firstEntry = context.getContextEntry().get(0);
        final ContextEntry lastEntry = context.getContextEntry().get(2);

        makeCommand(1);
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        Assertions.assertThat(context.getContextEntry()).containsExactly(firstEntry, lastEntry);
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
                     context.getContextEntry().size());
    }

    @Test
    public void testGraphCommandUndoMultipleRows() {
        addContextEntries(3);
        final ContextEntry firstEntry = context.getContextEntry().get(0);
        final ContextEntry originalEntry = context.getContextEntry().get(1);
        final ContextEntry lastEntry = context.getContextEntry().get(2);

        makeCommand(1);
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        Assertions.assertThat(context.getContextEntry()).containsExactly(firstEntry, originalEntry, lastEntry);
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand(0);
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteWithColumns() {
        makeCommand(0);
        uiModel.appendColumn(uiModelColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithColumnsMultipleRows() {
        addContextEntries(3);
        final GridRow firstRow = uiModel.getRow(0);
        final GridRow lastRow = uiModel.getRow(2);

        makeCommand(1);
        uiModel.appendColumn(uiModelColumn);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(firstRow,
                     uiModel.getRow(0));
        assertEquals(lastRow,
                     uiModel.getRow(1));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithNoColumns() {
        makeCommand(0);
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());
        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithColumns() {
        makeCommand(0);
        uiModel.appendColumn(uiModelColumn);

        //Delete ContextEntry and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(1,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithColumnsMultipleRows() {
        addContextEntries(3);
        final GridRow firstRow = uiModel.getRow(0);
        final GridRow originalRow = uiModel.getRow(1);
        final GridRow lastRow = uiModel.getRow(2);

        makeCommand(1);
        uiModel.appendColumn(uiModelColumn);

        //Delete ContextEntry and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(3,
                     uiModel.getRowCount());
        assertEquals(firstRow,
                     uiModel.getRow(0));
        assertEquals(originalRow,
                     uiModel.getRow(1));
        assertEquals(lastRow,
                     uiModel.getRow(2));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoColumns() {
        makeCommand(0);
        //Delete ContextEntry and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(1,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteRowNumbering() {
        makeCommand(0);

        setupRowNumbers();

        command.newCanvasCommand(handler).execute(handler);

        assertEquals(2, uiModel.getRowCount());

        assertEquals(1, uiModel.getCell(0, 0).getValue().getValue());
        assertNull(uiModel.getCell(1, 0).getValue());
    }

    @Test
    public void testCanvasCommandUndoRowNumbering() {
        makeCommand(0);

        setupRowNumbers();

        command.newCanvasCommand(handler).execute(handler);
        command.newCanvasCommand(handler).undo(handler);

        assertEquals(3, uiModel.getRowCount());

        assertEquals(1, uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(2, uiModel.getCell(1, 0).getValue().getValue());
        assertNull(uiModel.getCell(2, 0).getValue());
    }

    private void addContextEntries(final int entriesCount) {
        final int originalRowCount = uiModel.getRowCount();
        for (int i = 0; i < originalRowCount; i++) {
            uiModel.deleteRow(0);
        }
        context.getContextEntry().clear();

        for (int i = 0; i < entriesCount; i++) {
            context.getContextEntry().add(new ContextEntry());
            uiModel.appendRow(new BaseGridRow());
        }
    }

    private void setupRowNumbers() {
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        uiModel.setCellValue(0, 0, new BaseGridCellValue<>(1));
        uiModel.setCellValue(1, 0, new BaseGridCellValue<>(2));
        uiModel.setCellValue(2, 0, null);

        assertEquals(3, uiModel.getRowCount());
    }
}
