/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseSingletonDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.SalienceUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BaseSynchronizer.MoveColumnToMetaData;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttributeColumnSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testAppend() throws VetoException {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     model.getAttributeCols().get(0).getAttribute());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(true,
                     ((BaseSingletonDOMElementUiColumn) uiModel.getColumns().get(2)).isEditable());
    }

    @Test
    public void testUpdate1() throws VetoException {
        final AttributeCol52 column = spy(new AttributeCol52());
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        modelSynchronizer.appendColumn(column);

        final AttributeCol52 edited = new AttributeCol52();
        edited.setWidth(column.getWidth());
        edited.setAttribute(Attribute.ENABLED.getAttributeName());

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(1,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(Attribute.ENABLED.getAttributeName(),
                     model.getAttributeCols().get(0).getAttribute());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof BooleanUiColumn);
        assertEquals(Attribute.ENABLED.getAttributeName(),
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testUpdate2() throws VetoException {
        final AttributeCol52 column = spy(new AttributeCol52());
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        modelSynchronizer.appendColumn(column);

        final AttributeCol52 edited = new AttributeCol52();
        edited.setWidth(column.getWidth());
        edited.setAttribute(Attribute.SALIENCE.getAttributeName());
        edited.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(1,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     model.getAttributeCols().get(0).getAttribute());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
    }

    @Test
    public void testUpdateSalienceRowNumber() throws VetoException {
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();

        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        modelSynchronizer.appendColumn(column);

        final AttributeCol52 edited1 = new AttributeCol52();
        edited1.setAttribute(Attribute.SALIENCE.getAttributeName());
        edited1.setUseRowNumber(true);

        modelSynchronizer.updateColumn(column,
                                       edited1);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     model.getAttributeCols().get(0).getAttribute());
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals(2,
                     model.getData().get(1).get(2).getNumericValue());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof SalienceUiColumn);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(true,
                     ((SalienceUiColumn) uiModel.getColumns().get(2)).isUseRowNumber());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(2).getValue().getValue());
        assertEquals(2,
                     uiModel.getRow(1).getCells().get(2).getValue().getValue());

        final AttributeCol52 edited2 = new AttributeCol52();
        edited2.setAttribute(Attribute.SALIENCE.getAttributeName());
        edited2.setUseRowNumber(true);
        edited2.setReverseOrder(true);

        modelSynchronizer.updateColumn(column,
                                       edited2);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     model.getAttributeCols().get(0).getAttribute());
        assertEquals(2,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals(1,
                     model.getData().get(1).get(2).getNumericValue());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof SalienceUiColumn);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(true,
                     ((SalienceUiColumn) uiModel.getColumns().get(2)).isUseRowNumber());
        assertEquals(2,
                     uiModel.getRow(0).getCells().get(2).getValue().getValue());
        assertEquals(1,
                     uiModel.getRow(1).getCells().get(2).getValue().getValue());
    }

    @Test
    public void testDelete() throws VetoException {
        final AttributeCol52 column = new AttributeCol52();
        column.setAttribute(Attribute.SALIENCE.getAttributeName());

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getAttributeCols().size());
        assertEquals(3,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(0,
                     model.getAttributeCols().size());
        assertEquals(2,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        final AttributeCol52 column1 = new AttributeCol52();
        column1.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 column2 = new AttributeCol52();
        column2.setAttribute(Attribute.AGENDA_GROUP.getAttributeName());

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Integer>(1));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("smurf"));

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column1,
                     model.getAttributeCols().get(0));
        assertEquals(column2,
                     model.getAttributeCols().get(1));
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("smurf",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column2,
                     model.getAttributeCols().get(0));
        assertEquals(column1,
                     model.getAttributeCols().get(1));
        assertEquals("smurf",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals(1,
                     model.getData().get(0).get(3).getNumericValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws VetoException {
        final AttributeCol52 column1 = new AttributeCol52();
        column1.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 column2 = new AttributeCol52();
        column2.setAttribute(Attribute.AGENDA_GROUP.getAttributeName());

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Integer>(1));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("smurf"));

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column1,
                     model.getAttributeCols().get(0));
        assertEquals(column2,
                     model.getAttributeCols().get(1));
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("smurf",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column2,
                     model.getAttributeCols().get(0));
        assertEquals(column1,
                     model.getAttributeCols().get(1));
        assertEquals("smurf",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals(1,
                     model.getData().get(0).get(3).getNumericValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws VetoException {
        final AttributeCol52 column1 = new AttributeCol52();
        column1.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 column2 = new AttributeCol52();
        column2.setAttribute(Attribute.AGENDA_GROUP.getAttributeName());

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Integer>(1));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("smurf"));

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column1,
                     model.getAttributeCols().get(0));
        assertEquals(column2,
                     model.getAttributeCols().get(1));
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("smurf",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(0,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getAttributeCols().size());
        assertEquals(column1,
                     model.getAttributeCols().get(0));
        assertEquals(column2,
                     model.getAttributeCols().get(1));
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("smurf",
                     model.getData().get(0).get(3).getStringValue());

        assertEquals(4,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnsTo_MoveLeft() throws VetoException {
        final AttributeCol52 column1 = new AttributeCol52();
        column1.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 column2 = new AttributeCol52();
        column2.setAttribute(Attribute.AGENDA_GROUP.getAttributeName());
        final AttributeCol52 column3 = new AttributeCol52();
        column3.setAttribute(Attribute.AUTO_FOCUS.getAttributeName());

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);
        modelSynchronizer.appendColumn(column3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Integer>(1));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("smurf"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<Boolean>(true));

        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_1,
                                uiModelColumn2_1,
                                uiModelColumn3_1);

        //Moving multiple Attribute columns as an unsupported operation as it's impossible via the UI
        uiModel.moveColumnsTo(2,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn2_1);
                                  add(uiModelColumn3_1);
                              }});

        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_2,
                                uiModelColumn2_2,
                                uiModelColumn3_2);
    }

    @Test
    public void testMoveColumnsTo_MoveRight() throws VetoException {
        final AttributeCol52 column1 = new AttributeCol52();
        column1.setAttribute(Attribute.SALIENCE.getAttributeName());
        final AttributeCol52 column2 = new AttributeCol52();
        column2.setAttribute(Attribute.AGENDA_GROUP.getAttributeName());
        final AttributeCol52 column3 = new AttributeCol52();
        column3.setAttribute(Attribute.AUTO_FOCUS.getAttributeName());

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);
        modelSynchronizer.appendColumn(column3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             2,
                             new BaseGridCellValue<Integer>(1));
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("smurf"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<Boolean>(true));

        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_1,
                                uiModelColumn2_1,
                                uiModelColumn3_1);

        uiModel.moveColumnsTo(4,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }});

        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_2,
                                uiModelColumn2_2,
                                uiModelColumn3_2);
    }

    @Test
    public void checkHandlesMoveColumnsToWithEmptyMetadata() throws VetoException {
        final AttributeColumnSynchronizer synchronizer = new AttributeColumnSynchronizer();

        assertFalse(synchronizer.handlesMoveColumnsTo(Collections.emptyList()));
    }

    @Test
    public void checkHandlesMoveColumnsToWithMultipleMetadata() throws VetoException {
        final MoveColumnToMetaData md0 = mock(MoveColumnToMetaData.class);
        final MoveColumnToMetaData md1 = mock(MoveColumnToMetaData.class);
        final AttributeColumnSynchronizer synchronizer = new AttributeColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(AttributeCol52.class));
        when(md1.getColumn()).thenReturn(mock(AttributeCol52.class));

        assertFalse(synchronizer.handlesMoveColumnsTo(Arrays.asList(md0,
                                                                    md1)));
    }

    @Test
    public void checkHandlesMoveColumnsToWithSingleMetadata() throws VetoException {
        final MoveColumnToMetaData md0 = mock(MoveColumnToMetaData.class);
        final AttributeColumnSynchronizer synchronizer = new AttributeColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(AttributeCol52.class));

        assertTrue(synchronizer.handlesMoveColumnsTo(Collections.singletonList(md0)));
    }

    private void assertTestMoveColumnsTo(final AttributeCol52 column1,
                                         final AttributeCol52 column2,
                                         final AttributeCol52 column3,
                                         final GridColumn<?> uiModelColumn1,
                                         final GridColumn<?> uiModelColumn2,
                                         final GridColumn<?> uiModelColumn3) {
        assertEquals(3,
                     model.getAttributeCols().size());
        assertEquals(column1,
                     model.getAttributeCols().get(0));
        assertEquals(column2,
                     model.getAttributeCols().get(1));
        assertEquals(column3,
                     model.getAttributeCols().get(2));
        assertEquals(1,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(true,
                     model.getData().get(0).get(4).getBooleanValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertEquals(Attribute.SALIENCE.getAttributeName(),
                     uiModelColumn1.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AGENDA_GROUP.getAttributeName(),
                     uiModelColumn2.getHeaderMetaData().get(0).getTitle());
        assertEquals(Attribute.AUTO_FOCUS.getAttributeName(),
                     uiModelColumn3.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3 instanceof BooleanUiColumn);
        assertEquals(2,
                     uiModelColumn1.getIndex());
        assertEquals(3,
                     uiModelColumn2.getIndex());
        assertEquals(4,
                     uiModelColumn3.getIndex());
        assertEquals(1,
                     uiModel.getRow(0).getCells().get(uiModelColumn1.getIndex()).getValue().getValue());
        assertEquals("smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2.getIndex()).getValue().getValue());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn3.getIndex()).getValue().getValue());
    }
}
