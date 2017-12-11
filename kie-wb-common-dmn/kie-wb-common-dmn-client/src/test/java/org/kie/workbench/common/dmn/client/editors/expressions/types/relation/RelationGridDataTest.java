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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.MoveRowsCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.MoveColumnsCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationGridDataTest {

    @Mock
    private GridRow gridRow;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private GridCellValue gridCellValue;

    @Mock
    private DMNGridLayer gridLayer;

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

    private DMNGridData delegate;

    private RelationGridData uiModel;

    private Optional<Relation> expression = Optional.of(new Relation());

    @Before
    public void setup() {
        this.delegate = spy(new DMNGridData(gridLayer));
        this.uiModel = new RelationGridData(delegate,
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

    @Test
    public void testMoveColumnTo() {
        uiModel.moveColumnTo(0,
                             gridColumn);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveColumnsCommand.class));
    }

    @Test
    public void testMoveColumnsTo() {
        uiModel.moveColumnsTo(0,
                              Collections.singletonList(gridColumn));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(MoveColumnsCommand.class));
    }

    @Test
    public void testAppendColumn() {
        uiModel.appendColumn(gridColumn);

        verify(delegate).appendColumn(eq(gridColumn));

        verify(gridColumn).setResizable(eq(false));
    }

    @Test
    public void testInsertColumn() {
        uiModel.insertColumn(0, gridColumn);

        verify(delegate).insertColumn(eq(0),
                                      eq(gridColumn));

        verify(gridColumn).setResizable(eq(false));
    }

    @Test
    public void testDeleteColumn() {
        final GridColumn<?> anotherGridColumn = mock(GridColumn.class);
        uiModel.appendColumn(anotherGridColumn);
        uiModel.appendColumn(gridColumn);

        //Reset as methods were invoked by the appendColumn(..) calls
        reset(anotherGridColumn);
        uiModel.deleteColumn(gridColumn);

        verify(delegate).deleteColumn(eq(gridColumn));

        verify(anotherGridColumn).setResizable(eq(false));
    }

    // --- Delegated to real class ---

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
        uiModel.appendColumn(gridColumn);
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        uiModel.setCell(1, 0, gridCellValue);

        verify(delegate).setCell(eq(1),
                                 eq(0),
                                 eq(gridCellValue));
    }

    @Test
    public void testDelegateDeleteCell() {
        uiModel.appendColumn(gridColumn);
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        uiModel.deleteCell(1, 0);

        verify(delegate).deleteCell(eq(1),
                                    eq(0));
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
        uiModel.appendColumn(gridColumn);

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
        uiModel.appendRow(gridRow);

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
        uiModel.appendRow(new DMNGridRow());

        uiModel.getRow(0);

        verify(delegate).getRow(eq(0));
    }
}
