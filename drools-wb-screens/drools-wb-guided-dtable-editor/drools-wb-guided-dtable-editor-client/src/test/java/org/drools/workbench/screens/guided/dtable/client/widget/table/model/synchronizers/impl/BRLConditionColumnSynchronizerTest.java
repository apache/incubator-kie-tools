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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.appformer.project.datamodel.oracle.DataType;
import org.appformer.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.appformer.project.datamodel.oracle.ModelField;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.LongUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BRLConditionColumnSynchronizerTest extends BaseSynchronizerTest {

    @Override
    protected AsyncPackageDataModelOracle getOracle() {
        final AsyncPackageDataModelOracle oracle = super.getOracle();
        oracle.addModelFields(new HashMap<String, ModelField[]>() {
                                  {
                                      put("Applicant",
                                          new ModelField[]{
                                                  new ModelField("this",
                                                                 "Applicant",
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 "Applicant"),
                                                  new ModelField("age",
                                                                 Integer.class.getName(),
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 DataType.TYPE_NUMERIC_INTEGER),
                                                  new ModelField("salary",
                                                                 Long.class.getName(),
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 DataType.TYPE_NUMERIC_LONG),
                                                  new ModelField("name",
                                                                 String.class.getName(),
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 DataType.TYPE_STRING)});
                                      put("Address",
                                          new ModelField[]{
                                                  new ModelField("this",
                                                                 "Address",
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 "Address"),
                                                  new ModelField("country",
                                                                 String.class.getName(),
                                                                 ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                                 ModelField.FIELD_ORIGIN.SELF,
                                                                 FieldAccessorsAndMutators.ACCESSOR,
                                                                 DataType.TYPE_STRING)});
                                  }
                              }

        );
        return oracle;
    }

    @Test
    public void testAppend1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, single variable
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        column.getChildColumns().add(columnV0);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");

        modelSynchronizer.appendColumn(column);

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertEquals(2,
                     uiModel.getColumns().get(2).getHeaderMetaData().size());
        assertEquals("$age",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testAppend2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);

        assertEquals(2,
                     uiModel.getColumns().get(2).getHeaderMetaData().size());
        assertEquals("$age",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());

        assertEquals(2,
                     uiModel.getColumns().get(3).getHeaderMetaData().size());
        assertEquals("$name",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate1() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, single variable
        final BRLConditionColumn column = spy(new BRLConditionColumn());
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        column.getChildColumns().add(columnV0);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");

        modelSynchronizer.appendColumn(column);

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);

        assertEquals(2,
                     uiModel.getColumns().get(2).getHeaderMetaData().size());
        assertEquals("$age",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedColumnV0 = new BRLConditionVariableColumn("$name",
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

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
        assertEquals(2,
                     uiModel.getColumns().get(2).getHeaderMetaData().size());
        assertEquals("$name",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate2() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = spy(new BRLConditionColumn());
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedColumnV0 = new BRLConditionVariableColumn("$name",
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

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
        assertEquals("$name",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate3() throws ModelSynchronizer.MoveColumnVetoException {
        //Single Column, multiple variables
        final BRLConditionColumn column = spy(new BRLConditionColumn());
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);

        assertEquals("$age",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());

        final BRLConditionColumn edited = new BRLConditionColumn();
        final BRLConditionVariableColumn editedColumnV0 = new BRLConditionVariableColumn("$s",
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

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(3,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(2) instanceof LongUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(0).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(2).isVisible());
        assertEquals("$s",
                     uiModel.getColumns().get(2).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testDelete() throws ModelSynchronizer.MoveColumnVetoException {
        final BRLConditionColumn column = new BRLConditionColumn();
        final BRLConditionVariableColumn columnV0 = new BRLConditionVariableColumn("$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Applicant",
                                                                                   "age");
        final BRLConditionVariableColumn columnV1 = new BRLConditionVariableColumn("$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Applicant",
                                                                                   "name");
        column.getChildColumns().add(columnV0);
        column.getChildColumns().add(columnV1);
        column.setHeader("col1");
        columnV0.setHeader("col1v0");
        columnV1.setHeader("col1v1");

        modelSynchronizer.appendColumn(column);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);

        assertEquals(2,
                     model.getExpandedColumns().size());
        assertEquals(0,
                     model.getConditions().size());

        assertEquals(2,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveBRLConditionVariableColumnTo() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new BRLConditionColumn();
        final BRLConditionVariableColumn column1v0 = new BRLConditionVariableColumn("$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age");
        column1v0.setHeader("age");
        final BRLConditionVariableColumn column1v1 = new BRLConditionVariableColumn("$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name");
        column1v1.setHeader("name");

        final CompositeColumn<BRLConditionVariableColumn> column2 = new BRLConditionColumn();
        final BRLConditionVariableColumn column2v0 = new BRLConditionVariableColumn("$country",
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
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<Integer>(55));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<String>("Smurf"));
        uiModel.setCell(0,
                        4,
                        new BaseGridCellValue<String>("Canada"));

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column1,
                     model.getConditions().get(0));
        assertEquals(column2,
                     model.getConditions().get(1));
        assertEquals(55,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(4,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn2_1);

        //The move should have been vetoed and nothing changed
        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column1,
                     model.getConditions().get(0));
        assertEquals(column2,
                     model.getConditions().get(1));
        assertEquals(55,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_2.getIndex());
        assertEquals(3,
                     uiModelColumn2_2.getIndex());
        assertEquals(4,
                     uiModelColumn3_2.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveBRLConditionBlockTo() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new BRLConditionColumn();
        final BRLConditionVariableColumn column1v0 = new BRLConditionVariableColumn("$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age");
        column1v0.setHeader("age");
        final BRLConditionVariableColumn column1v1 = new BRLConditionVariableColumn("$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name");
        column1v1.setHeader("name");

        column1.getChildColumns().add(column1v0);
        column1.getChildColumns().add(column1v1);

        final Pattern52 column2 = new Pattern52();
        column2.setFactType("Address");
        final ConditionCol52 column2v0 = new ConditionCol52();
        column2v0.setBinding("$country");
        column2v0.setFactField("country");
        column2v0.setFieldType(DataType.TYPE_STRING);
        column2v0.setHeader("country");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2,
                                       column2v0);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<>(55));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<>("Smurf"));
        uiModel.setCell(0,
                        4,
                        new BaseGridCellValue<>("Canada"));

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column1,
                     model.getConditions().get(0));
        assertEquals(column2,
                     model.getConditions().get(1));
        assertEquals(55,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(4,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnsTo(4,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }}
        );

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column2,
                     model.getConditions().get(0));
        assertEquals(column1,
                     model.getConditions().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);
        assertEquals("Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMovePatternBefore() throws ModelSynchronizer.MoveColumnVetoException {
        final CompositeColumn<BRLConditionVariableColumn> column1 = new BRLConditionColumn();
        final BRLConditionVariableColumn column1v0 = new BRLConditionVariableColumn("$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age");
        column1v0.setHeader("age");
        final BRLConditionVariableColumn column1v1 = new BRLConditionVariableColumn("$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name");
        column1v1.setHeader("name");

        column1.getChildColumns().add(column1v0);
        column1.getChildColumns().add(column1v1);

        final Pattern52 column2 = new Pattern52();
        column2.setFactType("Address");
        final ConditionCol52 column2v0 = new ConditionCol52();
        column2v0.setBinding("$country");
        column2v0.setFactField("country");
        column2v0.setFieldType(DataType.TYPE_STRING);
        column2v0.setHeader("country");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2,
                                       column2v0);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<>(55));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<>("Smurf"));
        uiModel.setCell(0,
                        4,
                        new BaseGridCellValue<>("Canada"));

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column1,
                     model.getConditions().get(0));
        assertEquals(column2,
                     model.getConditions().get(1));
        assertEquals(55,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("Address",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(4,
                     uiModelColumn3_1.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn3_1);

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column2,
                     model.getConditions().get(0));
        assertEquals(column1,
                     model.getConditions().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);
        assertEquals("Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(2,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMovePatternAfter() throws ModelSynchronizer.MoveColumnVetoException {
        final Pattern52 column1 = new Pattern52();
        column1.setFactType("Address");
        final ConditionCol52 column1v0 = new ConditionCol52();
        column1v0.setBinding("$country");
        column1v0.setFactField("country");
        column1v0.setFieldType(DataType.TYPE_STRING);
        column1v0.setHeader("country");

        final CompositeColumn<BRLConditionVariableColumn> column2 = new BRLConditionColumn();
        final BRLConditionVariableColumn column2v0 = new BRLConditionVariableColumn("$age",
                                                                                    DataType.TYPE_NUMERIC_INTEGER,
                                                                                    "Applicant",
                                                                                    "age");
        column2v0.setHeader("age");
        final BRLConditionVariableColumn column2v1 = new BRLConditionVariableColumn("$name",
                                                                                    DataType.TYPE_STRING,
                                                                                    "Applicant",
                                                                                    "name");
        column2v1.setHeader("name");

        column2.getChildColumns().add(column2v0);
        column2.getChildColumns().add(column2v1);

        modelSynchronizer.appendColumn(column1,
                                       column1v0);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCell(0,
                        2,
                        new BaseGridCellValue<>("Canada"));
        uiModel.setCell(0,
                        3,
                        new BaseGridCellValue<>(55));
        uiModel.setCell(0,
                        4,
                        new BaseGridCellValue<>("Smurf"));

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column1,
                     model.getConditions().get(0));
        assertEquals(column2,
                     model.getConditions().get(1));
        assertEquals("Canada",
                     model.getData().get(0).get(2).getStringValue());
        assertEquals(55,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(4);
        assertEquals("Address",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(2,
                     uiModelColumn1_1.getIndex());
        assertEquals(3,
                     uiModelColumn2_1.getIndex());
        assertEquals(4,
                     uiModelColumn3_1.getIndex());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(4,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getConditions().size());
        assertEquals(column2,
                     model.getConditions().get(0));
        assertEquals(column1,
                     model.getConditions().get(1));
        assertEquals(55,
                     model.getData().get(0).get(2).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("Canada",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(2);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(4);
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("Address",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals(2,
                     uiModelColumn3_2.getIndex());
        assertEquals(55,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Canada",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }
}
