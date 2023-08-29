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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCellValueCommandTest {

    private static final int ROW_INDEX = 0;
    private static final int COLUMN_INDEX = 1;
    private static final String CELL_VALUE = "value";

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridData gridModel;

    @Mock
    private GridCell gridCell;

    @Mock
    private GridCellValue gridCellValue;

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

    private DeleteCellValueCommand command;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(gridModel.getCell(ROW_INDEX,
                               COLUMN_INDEX)).thenReturn(gridCell);
        when(gridCell.getValue()).thenReturn(gridCellValue);
        when(gridCellValue.getValue()).thenReturn(CELL_VALUE);
        when(gridWidget.getModel()).thenReturn(gridModel);

        this.command = new DeleteCellValueCommand(new GridCellTuple(ROW_INDEX,
                                                                    COLUMN_INDEX,
                                                                    gridWidget),
                                                  () -> uiModelMapper,
                                                  gridLayer::batch);
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

        verify(uiModelMapper).toDMNModel(eq(ROW_INDEX),
                                         eq(COLUMN_INDEX),
                                         gridCellValueSupplierCaptor.capture());

        final Supplier<Optional<GridCellValue<?>>> gridCellValueSupplier = gridCellValueSupplierCaptor.getValue();
        assertNotNull(gridCellValueSupplier);

        final Optional<GridCellValue<?>> oGridCellValue = gridCellValueSupplier.get();
        assertFalse(oGridCellValue.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));

        verify(uiModelMapper).toDMNModel(eq(ROW_INDEX),
                                         eq(COLUMN_INDEX),
                                         gridCellValueSupplierCaptor.capture());

        final Supplier<Optional<GridCellValue<?>>> gridCellValueSupplier = gridCellValueSupplierCaptor.getValue();
        assertNotNull(gridCellValueSupplier);

        final Optional<GridCellValue<?>> oGridCellValue = gridCellValueSupplier.get();
        assertTrue(oGridCellValue.isPresent());

        assertEquals(gridCellValue,
                     oGridCellValue.get());
        assertEquals(CELL_VALUE,
                     oGridCellValue.get().getValue());
    }

    @Test
    public void allowCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(gridModel,
               never()).deleteCell(anyInt(),
                                   anyInt());
    }

    @Test
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));

        verify(gridModel).setCellValue(eq(ROW_INDEX),
                                       eq(COLUMN_INDEX),
                                       eq(null));
        verify(gridLayer).batch();
    }

    @Test
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));

        verify(gridModel).setCellValue(eq(ROW_INDEX),
                                       eq(COLUMN_INDEX),
                                       gridCellValueCaptor.capture());

        assertEquals(gridCellValue,
                     gridCellValueCaptor.getValue());

        verify(gridLayer).batch();
    }

    @Test
    public void checkCommandDefinition() {
        assertTrue(command instanceof VetoExecutionCommand);
        assertTrue(command instanceof VetoUndoCommand);
    }
}
