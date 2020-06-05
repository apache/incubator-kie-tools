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
import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
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

public class MetaDataColumnSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testAppend() throws VetoException {
        final MetadataCol52 column = new MetadataCol52();
        column.setMetadata("smurf");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getMetadataCols().size());
        assertEquals("smurf",
                     model.getMetadataCols().get(0).getMetadata());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertEquals("smurf",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testUpdate1() throws VetoException {
        final MetadataCol52 column = spy(new MetadataCol52());
        column.setMetadata("smurf");

        modelSynchronizer.appendColumn(column);

        final MetadataCol52 edited = new MetadataCol52();
        edited.setWidth(column.getWidth());
        edited.setMetadata("changed");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(1,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getMetadataCols().size());
        assertEquals("changed",
                     model.getMetadataCols().get(0).getMetadata());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertEquals("changed",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testUpdate2() throws VetoException {
        final MetadataCol52 column = spy(new MetadataCol52());
        column.setMetadata("smurf");

        modelSynchronizer.appendColumn(column);

        final MetadataCol52 edited = new MetadataCol52();
        edited.setWidth(column.getWidth());
        edited.setMetadata("smurf");
        edited.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(1,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getMetadataCols().size());
        assertEquals("smurf",
                     model.getMetadataCols().get(0).getMetadata());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertEquals("smurf",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        final MetadataCol52 column = new MetadataCol52();
        column.setMetadata("smurf");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getMetadataCols().size());
        assertEquals(4,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(0,
                     model.getMetadataCols().size());
        assertEquals(3,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        final MetadataCol52 column1 = new MetadataCol52();
        column1.setMetadata("metadata1");
        final MetadataCol52 column2 = new MetadataCol52();
        column2.setMetadata("metadata2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("metadata1"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("metadata2"));

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column1,
                     model.getMetadataCols().get(0));
        assertEquals(column2,
                     model.getMetadataCols().get(1));
        assertEquals("metadata1",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata2",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("metadata1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column2,
                     model.getMetadataCols().get(0));
        assertEquals(column1,
                     model.getMetadataCols().get(1));
        assertEquals("metadata2",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata1",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("metadata2",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata1",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws VetoException {
        final MetadataCol52 column1 = new MetadataCol52();
        column1.setMetadata("metadata1");
        final MetadataCol52 column2 = new MetadataCol52();
        column2.setMetadata("metadata2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("metadata1"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("metadata2"));

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column1,
                     model.getMetadataCols().get(0));
        assertEquals(column2,
                     model.getMetadataCols().get(1));
        assertEquals("metadata1",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata2",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("metadata1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(4,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column2,
                     model.getMetadataCols().get(0));
        assertEquals(column1,
                     model.getMetadataCols().get(1));
        assertEquals("metadata2",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata1",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("metadata2",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata1",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws VetoException {
        final MetadataCol52 column1 = new MetadataCol52();
        column1.setMetadata("metadata1");
        final MetadataCol52 column2 = new MetadataCol52();
        column2.setMetadata("metadata2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("metadata1"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("metadata2"));

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column1,
                     model.getMetadataCols().get(0));
        assertEquals(column2,
                     model.getMetadataCols().get(1));
        assertEquals("metadata1",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata2",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("metadata1",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata2",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(1,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getMetadataCols().size());
        assertEquals(column1,
                     model.getMetadataCols().get(0));
        assertEquals(column2,
                     model.getMetadataCols().get(1));
        assertEquals("metadata1",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata2",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("metadata1",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata2",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnsTo_MoveLeft() throws VetoException {
        final MetadataCol52 column1 = new MetadataCol52();
        column1.setMetadata("metadata1");
        final MetadataCol52 column2 = new MetadataCol52();
        column2.setMetadata("metadata2");
        final MetadataCol52 column3 = new MetadataCol52();
        column3.setMetadata("metadata3");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);
        modelSynchronizer.appendColumn(column3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("metadata1"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("metadata2"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("metadata3"));

        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_1,
                                uiModelColumn2_1,
                                uiModelColumn3_1);

        //Moving multiple MetaData columns as an unsupported operation as it's impossible via the UI
        uiModel.moveColumnsTo(3,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn2_1);
                                  add(uiModelColumn3_1);
                              }});

        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_2,
                                uiModelColumn2_2,
                                uiModelColumn3_2);
    }

    @Test
    public void testMoveColumnsTo_MoveRight() throws VetoException {
        final MetadataCol52 column1 = new MetadataCol52();
        column1.setMetadata("metadata1");
        final MetadataCol52 column2 = new MetadataCol52();
        column2.setMetadata("metadata2");
        final MetadataCol52 column3 = new MetadataCol52();
        column3.setMetadata("metadata3");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);
        modelSynchronizer.appendColumn(column3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<String>("metadata1"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("metadata2"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("metadata3"));

        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_1,
                                uiModelColumn2_1,
                                uiModelColumn3_1);

        //Moving multiple MetaData columns as an unsupported operation as it's impossible via the UI
        uiModel.moveColumnsTo(5,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }});

        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);

        assertTestMoveColumnsTo(column1,
                                column2,
                                column3,
                                uiModelColumn1_2,
                                uiModelColumn2_2,
                                uiModelColumn3_2);
    }

    @Test
    public void checkHandlesMoveColumnsToWithEmptyMetadata() throws VetoException {
        final MetaDataColumnSynchronizer synchronizer = new MetaDataColumnSynchronizer();

        assertFalse(synchronizer.handlesMoveColumnsTo(Collections.emptyList()));
    }

    @Test
    public void checkHandlesMoveColumnsToWithMultipleMetadata() throws VetoException {
        final BaseSynchronizer.MoveColumnToMetaData md0 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final BaseSynchronizer.MoveColumnToMetaData md1 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final MetaDataColumnSynchronizer synchronizer = new MetaDataColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(MetadataCol52.class));
        when(md1.getColumn()).thenReturn(mock(MetadataCol52.class));

        assertFalse(synchronizer.handlesMoveColumnsTo(new ArrayList<BaseSynchronizer.MoveColumnToMetaData>() {{
            add(md0);
            add(md1);
        }}));
    }

    @Test
    public void checkHandlesMoveColumnsToWithSingleMetadata() throws VetoException {
        final BaseSynchronizer.MoveColumnToMetaData md0 = mock(BaseSynchronizer.MoveColumnToMetaData.class);
        final MetaDataColumnSynchronizer synchronizer = new MetaDataColumnSynchronizer();
        when(md0.getColumn()).thenReturn(mock(MetadataCol52.class));

        assertTrue(synchronizer.handlesMoveColumnsTo(Collections.singletonList(md0)));
    }

    private void assertTestMoveColumnsTo(final MetadataCol52 column1,
                                         final MetadataCol52 column2,
                                         final MetadataCol52 column3,
                                         final GridColumn<?> uiModelColumn1,
                                         final GridColumn<?> uiModelColumn2,
                                         final GridColumn<?> uiModelColumn3) {
        assertEquals(3,
                     model.getMetadataCols().size());
        assertEquals(column1,
                     model.getMetadataCols().get(0));
        assertEquals(column2,
                     model.getMetadataCols().get(1));
        assertEquals(column3,
                     model.getMetadataCols().get(2));
        assertEquals("metadata1",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("metadata2",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("metadata3",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        assertEquals("metadata1",
                     uiModelColumn1.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata2",
                     uiModelColumn2.getHeaderMetaData().get(0).getTitle());
        assertEquals("metadata3",
                     uiModelColumn3.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1.getIndex());
        assertEquals(4,
                     uiModelColumn2.getIndex());
        assertEquals(5,
                     uiModelColumn3.getIndex());
        assertEquals("metadata1",
                     uiModel.getRow(0).getCells().get(uiModelColumn1.getIndex()).getValue().getValue());
        assertEquals("metadata2",
                     uiModel.getRow(0).getCells().get(uiModelColumn2.getIndex()).getValue().getValue());
        assertEquals("metadata3",
                     uiModel.getRow(0).getCells().get(uiModelColumn3.getIndex()).getValue().getValue());
    }
}
