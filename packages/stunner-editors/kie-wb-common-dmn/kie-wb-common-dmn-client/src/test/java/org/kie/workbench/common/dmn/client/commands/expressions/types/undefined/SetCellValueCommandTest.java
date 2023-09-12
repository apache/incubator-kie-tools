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

package org.kie.workbench.common.dmn.client.commands.expressions.types.undefined;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetCellValueCommandTest {

    private static final int ROW_INDEX = 0;

    private static final int COLUMN_INDEX = 0;

    private static final String UUID = "uuid";

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridData gridModel;

    @Mock
    private DMNModelInstrumentedBase hasExpressionParent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Expression expression;

    @Mock
    private BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> expressionEditor;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private ExpressionGridCache expressionGridCache;

    @Mock
    private ParameterizedCommand<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> executeCanvasOperation;

    @Mock
    private org.uberfire.mvp.Command undoCanvasOperation;

    @Mock
    private Expression oldExpression;

    @Mock
    private GridCell oldCell;

    @Mock
    private GridCellValue oldCellValue;

    @Captor
    private ArgumentCaptor<GridCellValue> gridCellValueCaptor;

    private SetCellValueCommand command;

    @SuppressWarnings("unchecked")
    public void setup(final Optional<String> uuid) {
        when(gridWidget.getModel()).thenReturn(gridModel);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(hasExpressionParent);
        when(oldCell.getValue()).thenReturn(oldCellValue);

        this.command = new SetCellValueCommand(new GridCellTuple(ROW_INDEX,
                                                                 COLUMN_INDEX,
                                                                 gridWidget),
                                               uuid,
                                               hasExpression,
                                               () -> Optional.ofNullable(expression),
                                               expressionGridCache,
                                               executeCanvasOperation,
                                               undoCanvasOperation,
                                               () -> Optional.of(expressionEditor));
    }

    @Test
    public void checkGraphCommand() {
        setup(Optional.of(UUID));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).allow(graphCommandExecutionContext));
    }

    @Test
    public void executeGraphCommand() {
        setup(Optional.of(UUID));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));

        verify(hasExpression).setExpression(expression);
        verify(expression).setParent(hasExpressionParent);
    }

    @Test
    public void undoGraphCommand() {
        setup(Optional.of(UUID));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));

        verify(hasExpression).setExpression(null);
    }

    @Test
    public void undoGraphCommandWithNonNullOriginalExpression() {
        when(hasExpression.getExpression()).thenReturn(oldExpression);

        setup(Optional.of(UUID));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        verify(hasExpression).setExpression(expression);
        verify(expression).setParent(hasExpressionParent);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));

        verify(hasExpression).setExpression(eq(oldExpression));
    }

    @Test
    public void allowCanvasCommand() {
        setup(Optional.of(UUID));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(gridModel,
               never()).setCellValue(anyInt(),
                                     anyInt(),
                                     any(GridCellValue.class));
    }

    @Test
    public void executeCanvasCommand() {
        setup(Optional.of(UUID));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(expressionGridCache).putExpressionGrid(eq(UUID), eq(Optional.of(expressionEditor)));

        assertCanvasMutationOnExecute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeCanvasCommandWithNestedGrid() {
        setup(Optional.empty());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(expressionGridCache, never()).putExpressionGrid(Mockito.<String>any(), any(Optional.class));

        assertCanvasMutationOnExecute();
    }

    @Test
    public void undoCanvasCommand() {
        setup(Optional.of(UUID));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(expressionGridCache).removeExpressionGrid(eq(UUID));

        assertCanvasMutationOnUndo();
    }

    @Test
    public void undoCanvasCommandWithNestedGrid() {
        setup(Optional.empty());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(expressionGridCache, never()).removeExpressionGrid(Mockito.<String>any());

        assertCanvasMutationOnUndo();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeCanvasCommandThenUndoWithNonNullOriginalCell() {
        when(gridModel.getCell(ROW_INDEX, COLUMN_INDEX)).thenReturn(oldCell);

        setup(Optional.of(UUID));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutationOnExecute();

        reset(gridModel);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        assertCanvasMutationOnUndoWithNonNullOriginalCell();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeCanvasCommandThenUndoWithNonNullOriginalCellWithNestedGrid() {
        when(gridModel.getCell(ROW_INDEX, COLUMN_INDEX)).thenReturn(oldCell);

        setup(Optional.empty());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutationOnExecute();

        reset(gridModel);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        assertCanvasMutationOnUndoWithNonNullOriginalCell();
    }

    @Test
    public void checkCommandDefinition() {
        setup(Optional.of(UUID));

        assertTrue(command instanceof VetoExecutionCommand);
        assertTrue(command instanceof VetoUndoCommand);
    }

    private void assertCanvasMutationOnExecute() {
        verify(gridModel).setCellValue(eq(ROW_INDEX),
                                       eq(COLUMN_INDEX),
                                       gridCellValueCaptor.capture());

        final GridCellValue<?> gcv = gridCellValueCaptor.getValue();
        assertTrue(gcv instanceof ExpressionCellValue);
        final ExpressionCellValue ecv = (ExpressionCellValue) gcv;
        assertEquals(ecv.getValue().get(), expressionEditor);

        verify(executeCanvasOperation).execute(eq(Optional.of(expressionEditor)));
    }

    private void assertCanvasMutationOnUndo() {
        verify(gridModel).deleteCell(eq(ROW_INDEX),
                                     eq(COLUMN_INDEX));

        verify(undoCanvasOperation).execute();
    }

    private void assertCanvasMutationOnUndoWithNonNullOriginalCell() {
        verify(gridModel).setCellValue(eq(ROW_INDEX),
                                       eq(COLUMN_INDEX),
                                       eq(oldCellValue));

        verify(undoCanvasOperation).execute();
    }
}
