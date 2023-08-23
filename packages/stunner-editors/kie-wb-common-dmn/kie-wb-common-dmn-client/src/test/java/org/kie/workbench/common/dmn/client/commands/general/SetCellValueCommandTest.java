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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetCellValueCommandTest {

    private static final int ROW_INDEX = 0;
    private static final int COLUMN_INDEX = 1;
    private static final String OLD_CELL_VALUE = "old-value";
    private static final String NEW_CELL_VALUE = "new-value";

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridData gridModel;

    @Mock
    private GridCell oldGridCell;

    @Mock
    private GridCellValue oldGridCellValue;

    @Mock
    private GridCell newGridCell;

    @Mock
    private GridCellValue newGridCellValue;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private BaseUIModelMapper<?> uiModelMapper;

    @Captor
    private ArgumentCaptor<Supplier<Optional<GridCellValue<?>>>> gridCellValueSupplierCaptor;

    @Captor
    private ArgumentCaptor<GridCellValue<?>> gridCellValueCaptor;

    private SetCellValueCommand command;

    @SuppressWarnings("unchecked")
    public void setup(final GridCell oldGridCell,
                      final GridCellValue oldGridCellValue,
                      final String oldCellValue) {
        when(gridModel.getCell(ROW_INDEX,
                               COLUMN_INDEX)).thenReturn(oldGridCell);
        if (oldGridCell != null) {
            when(oldGridCell.getValue()).thenReturn(oldGridCellValue);
            if (oldGridCellValue != null) {
                when(oldGridCellValue.getValue()).thenReturn(oldCellValue);
            }
        }
        when(newGridCell.getValue()).thenReturn(newGridCellValue);
        when(newGridCellValue.getValue()).thenReturn(NEW_CELL_VALUE);
        when(gridWidget.getModel()).thenReturn(gridModel);

        this.command = new SetCellValueCommand(new GridCellValueTuple<>(ROW_INDEX,
                                                                        COLUMN_INDEX,
                                                                        gridWidget,
                                                                        newGridCellValue),
                                               () -> uiModelMapper,
                                               gridLayer::batch);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGraphCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).allow(graphCommandExecutionContext));
        verify(uiModelMapper,
               never()).toDMNModel(anyInt(),
                                   anyInt(),
                                   any(Supplier.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeGraphCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));

        assertGraphMutation(newGridCellValue,
                            NEW_CELL_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));

        assertGraphMutation(oldGridCellValue,
                            OLD_CELL_VALUE);
    }

    private void assertGraphMutation(final GridCellValue gridCellValue,
                                     final String value) {
        verify(uiModelMapper).toDMNModel(eq(ROW_INDEX),
                                         eq(COLUMN_INDEX),
                                         gridCellValueSupplierCaptor.capture());

        final Supplier<Optional<GridCellValue<?>>> gridCellValueSupplier = gridCellValueSupplierCaptor.getValue();
        assertNotNull(gridCellValueSupplier);

        final Optional<GridCellValue<?>> oGridCellValue = gridCellValueSupplier.get();
        assertTrue(oGridCellValue.isPresent());

        assertEquals(gridCellValue,
                     oGridCellValue.get());
        assertEquals(value,
                     oGridCellValue.get().getValue());
    }

    @Test
    public void allowCanvasCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(gridModel,
               never()).setCellValue(anyInt(),
                                     anyInt(),
                                     any(GridCellValue.class));
    }

    @Test
    public void executeCanvasCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutation(newGridCellValue);
    }

    @Test
    public void undoCanvasCommand() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        assertCanvasMutation(oldGridCellValue);
    }

    @Test
    public void executeCanvasCommandThenUndoWithNullOriginalCell() {
        setup(null,
              null,
              null);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutation(newGridCellValue);

        reset(gridModel, gridLayer);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(gridModel).deleteCell(eq(ROW_INDEX),
                                     eq(COLUMN_INDEX));
        verify(gridLayer).batch();
    }

    @Test
    public void executeCanvasCommandThenUndoWithNullOriginalValue() {
        setup(oldGridCell,
              null,
              null);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutation(newGridCellValue);

        reset(gridModel, gridLayer);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(gridModel).deleteCell(eq(ROW_INDEX),
                                     eq(COLUMN_INDEX));
        verify(gridLayer).batch();
    }

    private void assertCanvasMutation(final GridCellValue gridCellValue) {
        verify(gridModel).setCellValue(eq(ROW_INDEX),
                                       eq(COLUMN_INDEX),
                                       gridCellValueCaptor.capture());

        assertEquals(gridCellValue,
                     gridCellValueCaptor.getValue());

        verify(gridLayer).batch();
    }

    @Test
    public void checkCommandDefinition() {
        setup(oldGridCell,
              oldGridCellValue,
              OLD_CELL_VALUE);
        assertTrue(command instanceof VetoExecutionCommand);
        assertTrue(command instanceof VetoUndoCommand);
    }
}
