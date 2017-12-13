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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class InvocationGridDataTest {

    @Mock
    private GridRow gridRow;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private GridCellValue gridCellValue;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Command canvasOperation;

    @Mock
    private DMNGridData delegate;

    private InvocationGridData uiModel;

    private Optional<Invocation> expression = Optional.of(new Invocation());

    @Before
    public void setup() {
        this.uiModel = new InvocationGridData(delegate,
                                              sessionManager,
                                              sessionCommandManager,
                                              expression,
                                              canvasOperation);

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
    }

    // --- Intercepted methods delegated to commands ---

    @Test
    public void testMoveRowTo() {
        uiModel.moveRowTo(0,
                          gridRow);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveRowsCommand.class));
    }

    @Test
    public void testMoveRowsTo() {
        uiModel.moveRowsTo(0,
                           Collections.singletonList(gridRow));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveRowsCommand.class));
    }

    // --- Delegated to real class ---

    @Test
    public void testDelegateMoveColumnTo() {
        uiModel.moveColumnTo(1,
                             gridColumn);

        verify(delegate).moveColumnTo(eq(1),
                                      eq(gridColumn));
    }

    @Test
    public void testDelegateMoveColumnsTo() {
        final List<GridColumn<?>> gridColumns = Collections.singletonList(gridColumn);
        uiModel.moveColumnsTo(1,
                              gridColumns);

        verify(delegate).moveColumnsTo(eq(1),
                                       eq(gridColumns));
    }

    @Test
    public void testDelegateSelectCell() {
        uiModel.selectCell(0, 1);

        verify(delegate).selectCell(eq(0),
                                    eq(1));
    }

    @Test
    public void testDelegateSelectCells() {
        uiModel.selectCells(0, 1, 2, 3);

        verify(delegate).selectCells(eq(0),
                                     eq(1),
                                     eq(2),
                                     eq(3));
    }

    @Test
    public void testDelegateSetCell() {
        uiModel.setCell(0, 1, gridCellValue);

        verify(delegate).setCell(eq(0),
                                 eq(1),
                                 eq(gridCellValue));
    }

    @Test
    public void testDelegateDeleteCell() {
        uiModel.deleteCell(0, 1);

        verify(delegate).deleteCell(eq(0),
                                    eq(1));
    }

    @Test
    public void testDelegateGetColumns() {
        uiModel.getColumns();

        verify(delegate).getColumns();
    }

    @Test
    public void testDelegateGetColumnCount() {
        uiModel.getColumnCount();

        verify(delegate).getColumnCount();
    }

    @Test
    public void testDelegateAppendColumn() {
        uiModel.appendColumn(gridColumn);

        verify(delegate).appendColumn(eq(gridColumn));
    }

    @Test
    public void testDelegateInsertColumn() {
        uiModel.insertColumn(0, gridColumn);

        verify(delegate).insertColumn(eq(0),
                                      eq(gridColumn));
    }

    @Test
    public void testDelegateDeleteColumn() {
        uiModel.deleteColumn(gridColumn);

        verify(delegate).deleteColumn(eq(gridColumn));
    }

    @Test
    public void testDelegateGetRows() {
        uiModel.getRows();

        verify(delegate).getRows();
    }

    @Test
    public void testDelegateExpandCell() {
        uiModel.expandCell(0, 1);

        verify(delegate).expandCell(eq(0),
                                    eq(1));
    }

    @Test
    public void testDelegateCollapseCell() {
        uiModel.collapseCell(0, 1);

        verify(delegate).collapseCell(eq(0),
                                      eq(1));
    }

    @Test
    public void testDelegateSetColumnDraggingEnabled() {
        uiModel.setColumnDraggingEnabled(true);

        verify(delegate).setColumnDraggingEnabled(eq(true));
    }

    @Test
    public void testDelegateIsColumnDraggingEnabled() {
        uiModel.isColumnDraggingEnabled();

        verify(delegate).isColumnDraggingEnabled();
    }

    @Test
    public void testDelegateSetRowDraggingEnabled() {
        uiModel.setRowDraggingEnabled(true);

        verify(delegate).setRowDraggingEnabled(eq(true));
    }

    @Test
    public void testDelegateIsRowDraggingEnabled() {
        uiModel.isRowDraggingEnabled();

        verify(delegate).isRowDraggingEnabled();
    }

    @Test
    public void testDelegateSetMerged() {
        uiModel.setMerged(true);

        verify(delegate).setMerged(eq(true));
    }

    @Test
    public void testDelegateIsMerged() {
        uiModel.isMerged();

        verify(delegate).isMerged();
    }

    @Test
    public void testDelegateUpdateColumn() {
        uiModel.updateColumn(0, gridColumn);

        verify(delegate).updateColumn(eq(0),
                                      eq(gridColumn));
    }

    @Test
    public void testDelegateClearSelections() {
        uiModel.clearSelections();

        verify(delegate).clearSelections();
    }

    @Test
    public void testDelegateGetSelectedCells() {
        uiModel.getSelectedCells();

        verify(delegate).getSelectedCells();
    }

    @Test
    public void testDelegateGetSelectedCellsOrigin() {
        uiModel.getSelectedCellsOrigin();

        verify(delegate).getSelectedCellsOrigin();
    }

    @Test
    public void testDelegateGetCell() {
        uiModel.getCell(0, 1);

        verify(delegate).getCell(eq(0),
                                 eq(1));
    }

    @Test
    public void testDelegateSetHeaderRowCount() {
        uiModel.setHeaderRowCount(1);

        verify(delegate).setHeaderRowCount(eq(1));
    }

    @Test
    public void testDelegateGetHeaderRowCount() {
        uiModel.getHeaderRowCount();

        verify(delegate).getHeaderRowCount();
    }

    @Test
    public void testDelegateGetRowCount() {
        uiModel.getRowCount();

        verify(delegate).getRowCount();
    }

    @Test
    public void testDelegateDeleteRow() {
        uiModel.deleteRow(0);

        verify(delegate).deleteRow(eq(0));
    }

    @Test
    public void testDelegateInsertRow() {
        uiModel.insertRow(0, gridRow);

        verify(delegate).insertRow(eq(0),
                                   eq(gridRow));
    }

    @Test
    public void testDelegateAppendRow() {
        uiModel.appendRow(gridRow);

        verify(delegate).appendRow(eq(gridRow));
    }

    @Test
    public void testDelegateGetRow() {
        uiModel.getRow(0);

        verify(delegate).getRow(eq(0));
    }
}
