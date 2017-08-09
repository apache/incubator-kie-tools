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

package org.kie.workbench.common.dmn.client.commands;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SetCellValueCommandTest {

    private static final int ROW_INDEX = 0;
    private static final int COLUMN_INDEX = 1;
    private static final String OLD_CELL_VALUE = "old-value";
    private static final String NEW_CELL_VALUE = "new-value";

    @Mock
    private GridWidget gridWidget;

    @Mock
    private Layer gridLayer;

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

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(gridWidget.getModel()).thenReturn(gridModel);
        when(gridWidget.getLayer()).thenReturn(gridLayer);
        when(gridModel.getCell(ROW_INDEX,
                               COLUMN_INDEX)).thenReturn(oldGridCell);
        when(oldGridCell.getValue()).thenReturn(oldGridCellValue);
        when(oldGridCellValue.getValue()).thenReturn(OLD_CELL_VALUE);
        when(newGridCell.getValue()).thenReturn(newGridCellValue);
        when(newGridCellValue.getValue()).thenReturn(NEW_CELL_VALUE);

        this.command = new SetCellValueCommand(ROW_INDEX,
                                               COLUMN_INDEX,
                                               gridWidget,
                                               newGridCellValue,
                                               uiModelMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGraphCommand() {
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
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));

        assertGraphMutation(newGridCellValue,
                            NEW_CELL_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
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
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(gridModel,
               never()).setCell(anyInt(),
                                anyInt(),
                                any(GridCellValue.class));
    }

    @Test
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        assertCanvasMutation(newGridCellValue);
    }

    @Test
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        assertCanvasMutation(oldGridCellValue);
    }

    private void assertCanvasMutation(final GridCellValue gridCellValue) {
        verify(gridModel).setCell(eq(ROW_INDEX),
                                  eq(COLUMN_INDEX),
                                  gridCellValueCaptor.capture());

        assertEquals(gridCellValue,
                     gridCellValueCaptor.getValue());

        verify(gridLayer).batch();
    }
}
