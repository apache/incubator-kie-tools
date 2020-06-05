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
import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.LongUiColumn;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BRLActionColumnSynchronizerTest extends BaseSynchronizerTest {

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = super.getOracle();
        oracle.addModelFields(new HashMap<String, ModelField[]>() {
                                  {
                                      put("Applicant",
                                          new ModelField[]{
                                                  modelField("this",
                                                             "Applicant"),
                                                  modelField("age",
                                                             DataType.TYPE_NUMERIC_INTEGER),
                                                  modelField("salary",
                                                             DataType.TYPE_NUMERIC_LONG),
                                                  modelField("name",
                                                             DataType.TYPE_STRING)});
                                      put("Address",
                                          new ModelField[]{
                                                  modelField("this",
                                                             "Address"),
                                                  modelField("country",
                                                             DataType.TYPE_STRING)});
                                  }
                              }

        );
        return oracle;
    }

    @Test
    public void testAppend1() throws VetoException {
        //Single Column, single variable
        final BRLActionColumn column = new BRLActionColumn();
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        column.getChildColumns().add(columnV0);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
    }

    @Test
    public void testAppend2() throws VetoException {
        //Single Column, multiple variables
        final BRLActionColumn column = new BRLActionColumn();
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        final BRLActionVariableColumn columnV1 = new BRLActionVariableColumn("$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Applicant",
                                                                             "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);

        assertEquals("col1v0",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals("$age",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
        assertEquals("col1v1",
                     uiModel.getColumns().get(4).getHeaderMetaData().get(0).getTitle());
        assertEquals("$name",
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate1() throws VetoException {
        //Single Column, single variable
        final BRLActionColumn column = spy(new BRLActionColumn());
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        column.getChildColumns().add(columnV0);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);

        final BRLActionColumn edited = new BRLActionColumn();
        final BRLActionVariableColumn editedColumnV0 = new BRLActionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        edited.getChildColumns().add(editedColumnV0);
        edited.setHideColumn(true);
        edited.setHeader("updated");
        editedColumnV0.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(5,
                     // header, hide, field name, field type, binding
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals("$name",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
    }

    @Test
    public void testUpdate2() throws VetoException {
        //Single Column, multiple variables
        final BRLActionColumn column = spy(new BRLActionColumn());
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        final BRLActionVariableColumn columnV1 = new BRLActionVariableColumn("$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Applicant",
                                                                             "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);

        final BRLActionColumn edited = new BRLActionColumn();
        final BRLActionVariableColumn editedColumnV0 = new BRLActionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        edited.getChildColumns().add(editedColumnV0);
        edited.setHideColumn(true);
        edited.setHeader("updated");
        editedColumnV0.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(6,
                     // header, hide, field name, field type, binding, removed column
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals("$name",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
    }

    @Test
    public void testUpdate3() throws VetoException {
        //Single Column, multiple variables
        final BRLActionColumn column = spy(new BRLActionColumn());
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        final BRLActionVariableColumn columnV1 = new BRLActionVariableColumn("$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Applicant",
                                                                             "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);

        final BRLActionColumn edited = new BRLActionColumn();
        final BRLActionVariableColumn editedColumnV0 = new BRLActionVariableColumn("$s",
                                                                                   DataType.TYPE_NUMERIC_LONG,
                                                                                   "Applicant",
                                                                                   "salary");
        edited.getChildColumns().add(editedColumnV0);
        edited.setHideColumn(true);
        edited.setHeader("updated");
        editedColumnV0.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(6,
                     // header, hide, field name, field type, binding, removed column
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof LongUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals("$s",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        final BRLActionColumn column = new BRLActionColumn();
        final BRLActionVariableColumn columnV0 = new BRLActionVariableColumn("$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Applicant",
                                                                             "age");
        final BRLActionVariableColumn columnV1 = new BRLActionVariableColumn("$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Applicant",
                                                                             "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(0,
                     model.getActionCols().size());

        assertEquals(3,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveBRLActionVariableColumnTo() throws VetoException {
        final BRLActionColumn column1 = new BRLActionColumn();
        final BRLActionVariableColumn column1v0 = new BRLActionVariableColumn("$age",
                                                                              DataType.TYPE_NUMERIC_INTEGER,
                                                                              "Applicant",
                                                                              "age");
        column1v0.setHeader("age");
        final BRLActionVariableColumn column1v1 = new BRLActionVariableColumn("$name",
                                                                              DataType.TYPE_STRING,
                                                                              "Applicant",
                                                                              "name");
        column1v1.setHeader("name");

        final BRLActionColumn column2 = new BRLActionColumn();
        final BRLActionVariableColumn column2v0 = new BRLActionVariableColumn("$country",
                                                                              DataType.TYPE_STRING,
                                                                              "Address",
                                                                              "country");
        column2v0.setHeader("country");

        column1.getChildColumns().add(column1v0);
        column1.getChildColumns().add(column1v1);
        column2.getChildColumns().add(column2v0);

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(55));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("Canada"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn2_1);

        //The move should have been vetoed and nothing changed
        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals(5,
                     uiModelColumn3_2.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveBRLActionBlockTo() throws VetoException {
        final BRLActionColumn column1 = new BRLActionColumn();
        final BRLActionVariableColumn column1v0 = new BRLActionVariableColumn("$age",
                                                                              DataType.TYPE_NUMERIC_INTEGER,
                                                                              "Applicant",
                                                                              "age");
        column1v0.setHeader("age");
        final BRLActionVariableColumn column1v1 = new BRLActionVariableColumn("$name",
                                                                              DataType.TYPE_STRING,
                                                                              "Applicant",
                                                                              "name");
        column1v1.setHeader("name");

        final ActionInsertFactCol52 column2 = new ActionInsertFactCol52();
        column2.setHeader("country");
        column2.setBoundName("$a");
        column2.setFactType("Address");
        column2.setFactField("country");

        column1.getChildColumns().add(column1v0);
        column1.getChildColumns().add(column1v1);

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<>(55));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<>("Canada"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnsTo(5,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }});

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        assertEquals("$a : Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals(4,
                     uiModelColumn3_2.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveActionBefore() throws VetoException {
        final BRLActionColumn column1 = new BRLActionColumn();
        final BRLActionVariableColumn column1v0 = new BRLActionVariableColumn("$age",
                                                                              DataType.TYPE_NUMERIC_INTEGER,
                                                                              "Applicant",
                                                                              "age");
        column1v0.setHeader("age");
        final BRLActionVariableColumn column1v1 = new BRLActionVariableColumn("$name",
                                                                              DataType.TYPE_STRING,
                                                                              "Applicant",
                                                                              "name");
        column1v1.setHeader("name");

        final ActionInsertFactCol52 column2 = new ActionInsertFactCol52();
        column2.setHeader("country");
        column2.setBoundName("$a");
        column2.setFactType("Address");
        column2.setFactField("country");

        column1.getChildColumns().add(column1v0);
        column1.getChildColumns().add(column1v1);

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<>(55));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<>("Canada"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(3,
                             uiModelColumn3_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        assertEquals("$a : Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals(4,
                     uiModelColumn3_2.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveActionAfter() throws VetoException {
        final ActionInsertFactCol52 column1 = new ActionInsertFactCol52();
        column1.setHeader("country");
        column1.setBoundName("$a");
        column1.setFactType("Address");
        column1.setFactField("country");

        final BRLActionColumn column2 = new BRLActionColumn();
        final BRLActionVariableColumn column2v0 = new BRLActionVariableColumn("$age",
                                                                              DataType.TYPE_NUMERIC_INTEGER,
                                                                              "Applicant",
                                                                              "age");
        column2v0.setHeader("age");
        final BRLActionVariableColumn column2v1 = new BRLActionVariableColumn("$name",
                                                                              DataType.TYPE_STRING,
                                                                              "Applicant",
                                                                              "name");
        column2v1.setHeader("name");

        column2.getChildColumns().add(column2v0);
        column2.getChildColumns().add(column2v1);

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<>("Canada"));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<>(55));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(5,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Address",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(5,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }
}
