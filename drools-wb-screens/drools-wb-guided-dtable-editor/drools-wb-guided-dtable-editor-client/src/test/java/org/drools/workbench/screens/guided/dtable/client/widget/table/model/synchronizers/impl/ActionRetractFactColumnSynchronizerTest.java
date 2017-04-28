/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseSingletonDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BoundFactUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActionRetractFactColumnSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testAppend() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column = new ActionRetractFactCol52();
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof BoundFactUiColumn);
        assertEquals(true,
                     ((BaseSingletonDOMElementUiColumn) uiModel.getColumns().get(2)).isEditable());
    }

    @Test
    public void testUpdate() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column = spy(new ActionRetractFactCol52());
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        final ActionRetractFactCol52 edited = new ActionRetractFactCol52();
        edited.setWidth(column.getWidth());
        edited.setHideColumn(true);
        edited.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(2,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof BoundFactUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
    }

    @Test
    public void testDelete() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column = new ActionRetractFactCol52();
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());
        assertEquals(3,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(0,
                     model.getActionCols().size());
        assertEquals(2,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column1 = new ActionRetractFactCol52();
        column1.setHeader("retract1");
        final ActionRetractFactCol52 column2 = new ActionRetractFactCol52();
        column2.setHeader("retract2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<String>("$r1"));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<String>("$r2"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals("$r1",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r2",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("retract1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BoundFactUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("$r2",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r1",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("retract2",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract1",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BoundFactUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column1 = new ActionRetractFactCol52();
        column1.setHeader("retract1");
        final ActionRetractFactCol52 column2 = new ActionRetractFactCol52();
        column2.setHeader("retract2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<String>("$r1"));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<String>("$r2"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals("$r1",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r2",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("retract1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BoundFactUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("$r2",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r1",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("retract2",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract1",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BoundFactUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws ModelSynchronizer.MoveColumnVetoException {
        final ActionRetractFactCol52 column1 = new ActionRetractFactCol52();
        column1.setHeader("retract1");
        final ActionRetractFactCol52 column2 = new ActionRetractFactCol52();
        column2.setHeader("retract2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<String>("$r1"));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<String>("$r2"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals("$r1",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r2",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals("retract1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BoundFactUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(0,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals("$r1",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals("$r2",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals("retract1",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("retract2",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BoundFactUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BoundFactUiColumn);
        assertEquals(2,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals("$r1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("$r2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }
}
