/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RowSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testAppend() throws VetoException {
        modelSynchronizer.appendRow();

        assertEquals(1,
                     model.getData().size());
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     uiModel.getRow(0).getHeight(),
                     0.0);
    }

    @Test
    public void testInsert() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.insertRow(0);

        assertEquals(2,
                     model.getData().size());
        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     uiModel.getRow(0).getHeight(),
                     0.0);
        assertEquals(GuidedDecisionTableView.ROW_HEIGHT,
                     uiModel.getRow(1).getHeight(),
                     0.0);
    }

    @Test
    public void testDeleteUnmergedData() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.deleteRow(0);

        assertEquals(0,
                     model.getData().size());
        assertEquals(0,
                     uiModel.getRowCount());
    }

    @Test
    public void testDeleteMergedData_Block() throws VetoException {
        uiModel.setMerged(true);
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             1,
                             new GuidedDecisionTableUiCell<String>("a"));
        uiModel.setCellValue(1,
                             1,
                             new GuidedDecisionTableUiCell<String>("a"));
        uiModel.setCellValue(2,
                             1,
                             new GuidedDecisionTableUiCell<String>("b"));
        uiModel.collapseCell(0,
                             1);

        modelSynchronizer.deleteRow(0);

        assertEquals(1,
                     model.getData().size());
        assertEquals(1,
                     uiModel.getRowCount());
    }

    @Test
    public void testDeleteMergedData_WholeBlock() throws VetoException {
        uiModel.setMerged(true);
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             1,
                             new GuidedDecisionTableUiCell<String>("a"));
        uiModel.setCellValue(1,
                             1,
                             new GuidedDecisionTableUiCell<String>("a"));
        uiModel.setCellValue(2,
                             1,
                             new GuidedDecisionTableUiCell<String>("a"));
        uiModel.collapseCell(0,
                             1);

        modelSynchronizer.deleteRow(0);

        assertEquals(0,
                     model.getData().size());
        assertEquals(0,
                     uiModel.getRowCount());
    }

    @Test
    public void testMoveRowMoveUpTopBlock() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(0,
                           new ArrayList<GridRow>() {{
                               add(uiRow2);
                           }});

        assertEquals(uiRow2,
                     uiModel.getRow(0));
        assertEquals(uiRow0,
                     uiModel.getRow(1));
        assertEquals(uiRow1,
                     uiModel.getRow(2));

        assertEquals(row2,
                     model.getData().get(0));
        assertEquals(row0,
                     model.getData().get(1));
        assertEquals(row1,
                     model.getData().get(2));
    }

    @Test
    public void testMoveRowMoveUpMidBlock() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(1,
                           new ArrayList<GridRow>() {{
                               add(uiRow2);
                           }});

        assertEquals(uiRow0,
                     uiModel.getRow(0));
        assertEquals(uiRow2,
                     uiModel.getRow(1));
        assertEquals(uiRow1,
                     uiModel.getRow(2));

        assertEquals(row0,
                     model.getData().get(0));
        assertEquals(row2,
                     model.getData().get(1));
        assertEquals(row1,
                     model.getData().get(2));
    }

    @Test
    public void testMoveRowsMoveUp() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(0,
                           new ArrayList<GridRow>() {{
                               add(uiRow1);
                               add(uiRow2);
                           }});

        assertEquals(uiRow1,
                     uiModel.getRow(0));
        assertEquals(uiRow2,
                     uiModel.getRow(1));
        assertEquals(uiRow0,
                     uiModel.getRow(2));

        assertEquals(row1,
                     model.getData().get(0));
        assertEquals(row2,
                     model.getData().get(1));
        assertEquals(row0,
                     model.getData().get(2));
    }

    @Test
    public void testMoveRowMoveDownEndBlock() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(2,
                           new ArrayList<GridRow>() {{
                               add(uiRow0);
                           }});

        assertEquals(uiRow1,
                     uiModel.getRow(0));
        assertEquals(uiRow2,
                     uiModel.getRow(1));
        assertEquals(uiRow0,
                     uiModel.getRow(2));

        assertEquals(row1,
                     model.getData().get(0));
        assertEquals(row2,
                     model.getData().get(1));
        assertEquals(row0,
                     model.getData().get(2));
    }

    @Test
    public void testMoveRowMoveDownMidBlock() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(1,
                           new ArrayList<GridRow>() {{
                               add(uiRow0);
                           }});

        assertEquals(uiRow1,
                     uiModel.getRow(0));
        assertEquals(uiRow0,
                     uiModel.getRow(1));
        assertEquals(uiRow2,
                     uiModel.getRow(2));

        assertEquals(row1,
                     model.getData().get(0));
        assertEquals(row0,
                     model.getData().get(1));
        assertEquals(row2,
                     model.getData().get(2));
    }

    @Test
    public void testMoveRowsMoveDown() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        final List<DTCellValue52> row0 = model.getData().get(0);
        final List<DTCellValue52> row1 = model.getData().get(1);
        final List<DTCellValue52> row2 = model.getData().get(2);

        uiModel.moveRowsTo(2,
                           new ArrayList<GridRow>() {{
                               add(uiRow0);
                               add(uiRow1);
                           }});

        assertEquals(uiRow2,
                     uiModel.getRow(0));
        assertEquals(uiRow0,
                     uiModel.getRow(1));
        assertEquals(uiRow1,
                     uiModel.getRow(2));

        assertEquals(row2,
                     model.getData().get(0));
        assertEquals(row0,
                     model.getData().get(1));
        assertEquals(row1,
                     model.getData().get(2));
    }

    @Test
    public void testAppendRowNumbers() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        assertEquals(1,
                     uiModel.getRow(0).getCells().get(0).getValue().getValue());
        assertEquals(2,
                     uiModel.getRow(1).getCells().get(0).getValue().getValue());

        assertEquals(1,
                     model.getData().get(0).get(0).getNumericValue());
        assertEquals(2,
                     model.getData().get(1).get(0).getNumericValue());
    }

    @Test
    public void testInsertRowNumbers() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.insertRow(0);

        assertEquals(1,
                     uiModel.getRow(0).getCells().get(0).getValue().getValue());
        assertEquals(2,
                     uiModel.getRow(1).getCells().get(0).getValue().getValue());

        assertEquals(1,
                     model.getData().get(0).get(0).getNumericValue());
        assertEquals(2,
                     model.getData().get(1).get(0).getNumericValue());
    }

    @Test
    public void testDeleteRowNumbers() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.deleteRow(0);

        assertEquals(1,
                     uiModel.getRow(0).getCells().get(0).getValue().getValue());

        assertEquals(1,
                     model.getData().get(0).get(0).getNumericValue());
    }

    @Test
    public void testMoveRowsMoveDownCheckRowNumbers() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow0 = uiModel.getRow(0);
        final GridRow uiRow1 = uiModel.getRow(1);

        uiModel.moveRowsTo(2,
                           new ArrayList<GridRow>() {{
                               add(uiRow0);
                               add(uiRow1);
                           }});

        assertEquals(1,
                     uiModel.getRow(0).getCells().get(0).getValue().getValue());
        assertEquals(2,
                     uiModel.getRow(1).getCells().get(0).getValue().getValue());
        assertEquals(3,
                     uiModel.getRow(2).getCells().get(0).getValue().getValue());

        assertEquals(1,
                     model.getData().get(0).get(0).getNumericValue());
        assertEquals(2,
                     model.getData().get(1).get(0).getNumericValue());
        assertEquals(3,
                     model.getData().get(2).get(0).getNumericValue());
    }

    @Test
    public void testMoveRowsMoveUpCheckRowNumbers() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final GridRow uiRow1 = uiModel.getRow(1);
        final GridRow uiRow2 = uiModel.getRow(2);

        uiModel.moveRowsTo(0,
                           new ArrayList<GridRow>() {{
                               add(uiRow1);
                               add(uiRow2);
                           }});

        assertEquals(1,
                     uiModel.getRow(0).getCells().get(0).getValue().getValue());
        assertEquals(2,
                     uiModel.getRow(1).getCells().get(0).getValue().getValue());
        assertEquals(3,
                     uiModel.getRow(2).getCells().get(0).getValue().getValue());

        assertEquals(1,
                     model.getData().get(0).get(0).getNumericValue());
        assertEquals(2,
                     model.getData().get(1).get(0).getNumericValue());
        assertEquals(3,
                     model.getData().get(2).get(0).getNumericValue());
    }

    @Test
    public void checkBooleanDefaultValueTrue() throws VetoException {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.ENABLED.getAttributeName());
        column.setDefaultValue(new DTCellValue52(true));

        modelSynchronizer.appendColumn(column);

        modelSynchronizer.appendRow();

        assertTrue((Boolean) uiModel.getRow(0).getCells().get(2).getValue().getValue());

        assertTrue(model.getData().get(0).get(2).getBooleanValue());
    }

    @Test
    public void checkBooleanDefaultValueFalse() throws VetoException {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.ENABLED.getAttributeName());
        column.setDefaultValue(new DTCellValue52(false));

        modelSynchronizer.appendColumn(column);

        modelSynchronizer.appendRow();

        assertFalse((Boolean) uiModel.getRow(0).getCells().get(2).getValue().getValue());

        assertFalse(model.getData().get(0).get(2).getBooleanValue());
    }

    @Test
    public void checkBooleanDefaultValueNotSet() throws VetoException {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.ENABLED.getAttributeName());

        modelSynchronizer.appendColumn(column);

        modelSynchronizer.appendRow();

        assertFalse((Boolean) uiModel.getRow(0).getCells().get(2).getValue().getValue());

        assertFalse(model.getData().get(0).get(2).getBooleanValue());
    }
}
