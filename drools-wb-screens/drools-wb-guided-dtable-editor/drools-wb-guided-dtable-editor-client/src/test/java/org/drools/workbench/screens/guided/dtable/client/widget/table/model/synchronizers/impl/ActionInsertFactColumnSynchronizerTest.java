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

import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseMultipleDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseSingletonDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ActionInsertFactColumnSynchronizerTest extends BaseSynchronizerTest {

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = super.getOracle();
        oracle.addModelFields(Collections.singletonMap("Applicant",
                                                       new ModelField[]{
                                                               modelField("this",
                                                                          "Applicant"),
                                                               modelField("age",
                                                                          DataType.TYPE_NUMERIC_INTEGER),
                                                               modelField("name",
                                                                          DataType.TYPE_STRING),
                                                               modelField("approved",
                                                                          DataType.TYPE_BOOLEAN)}));

        return oracle;
    }

    @Test
    public void testAppend() throws VetoException {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactType("Applicant");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertEquals(true,
                     ((BaseSingletonDOMElementUiColumn) uiModel.getColumns().get(3)).isEditable());
    }

    @Test
    public void testAppendBoolean() throws VetoException {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactType("Applicant");
        column.setFactField("approved");

        //Test column append
        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof BooleanUiColumn);
        assertEquals(true,
                     ((BaseMultipleDOMElementUiColumn) uiModel.getColumns().get(3)).isEditable());

        //Test row append (boolean cells should be instantiated for Model and UiModel)
        modelSynchronizer.appendRow();

        assertFalse(model.getData().get(0).get(3).getBooleanValue());
        assertFalse(((Boolean) uiModel.getRow(0).getCells().get(3).getValue().getValue()));
    }

    @Test
    public void testUpdate() throws VetoException {
        final ActionInsertFactCol52 column = spy(new ActionInsertFactCol52());
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactType("Applicant");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        final ActionInsertFactCol52 edited = new ActionInsertFactCol52();
        edited.setWidth(column.getWidth());
        edited.setBoundName("$a");
        edited.setFactType("Applicant");
        edited.setFactField("name");
        edited.setHideColumn(true);
        edited.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(3,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        final ActionInsertFactCol52 column = new ActionInsertFactCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactType("Applicant");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getActionCols().size());
        assertEquals(4,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(0,
                     model.getActionCols().size());
        assertEquals(3,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        final ActionInsertFactCol52 column1 = new ActionInsertFactCol52();
        column1.setBoundName("$a");
        column1.setFactType("Applicant");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionInsertFactCol52 column2 = new ActionInsertFactCol52();
        column2.setBoundName("$a");
        column2.setFactType("Applicant");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws VetoException {
        final ActionInsertFactCol52 column1 = new ActionInsertFactCol52();
        column1.setBoundName("$a");
        column1.setFactType("Applicant");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionInsertFactCol52 column2 = new ActionInsertFactCol52();
        column2.setBoundName("$a");
        column2.setFactType("Applicant");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(4,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws VetoException {
        final ActionInsertFactCol52 column1 = new ActionInsertFactCol52();
        column1.setBoundName("$a");
        column1.setFactType("Applicant");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionInsertFactCol52 column2 = new ActionInsertFactCol52();
        column2.setBoundName("$a");
        column2.setFactType("Applicant");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(1,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("$a : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }
}
