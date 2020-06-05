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

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
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

public class ActionSetFieldColumnSynchronizerTest extends BaseSynchronizerTest {

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
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof IntegerUiColumn);
        assertEquals(true,
                     ((BaseSingletonDOMElementUiColumn) uiModel.getColumns().get(3)).isEditable());
    }

    @Test
    public void testAppendBoolean() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        //Test column append
        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactField("approved");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof BooleanUiColumn);
        assertEquals(true,
                     ((BaseSingletonDOMElementUiColumn) uiModel.getColumns().get(3)).isEditable());

        //Test row append (boolean cells should be instantiated for Model and UiModel)
        modelSynchronizer.appendRow();

        assertFalse(model.getData().get(0).get(4).getBooleanValue());
        assertFalse(((Boolean) uiModel.getRow(0).getCells().get(4).getValue().getValue()));
    }

    @Test
    public void testUpdate() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column = spy(new ActionSetFieldCol52());
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        final ActionSetFieldCol52 edited = new ActionSetFieldCol52();
        edited.setWidth(column.getWidth());
        edited.setBoundName("$a");
        edited.setFactField("name");
        edited.setHideColumn(true);
        edited.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(3,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(4).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column = new ActionSetFieldCol52();
        column.setHeader("col1");
        column.setBoundName("$a");
        column.setFactField("age");

        modelSynchronizer.appendColumn(column);

        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getActionCols().size());
        assertEquals(5,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(0,
                     model.getActionCols().size());
        assertEquals(4,
                     uiModel.getColumns().size());
    }

    @Test
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column1 = new ActionSetFieldCol52();
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionSetFieldCol52 column2 = new ActionSetFieldCol52();
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_1.getIndex());
        assertEquals(5,
                     uiModelColumn2_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(4,
                             uiModelColumn2_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(5).getNumericValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_MoveRight() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column1 = new ActionSetFieldCol52();
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionSetFieldCol52 column2 = new ActionSetFieldCol52();
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_1.getIndex());
        assertEquals(5,
                     uiModelColumn2_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(5,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(0));
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(5).getNumericValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof IntegerUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_OutOfBounds() throws VetoException {
        //Add a Pattern to be updated
        final Pattern52 pattern = new Pattern52();
        pattern.setBoundName("$a");
        pattern.setFactType("Applicant");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final ActionSetFieldCol52 column1 = new ActionSetFieldCol52();
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("age");
        final ActionSetFieldCol52 column2 = new ActionSetFieldCol52();
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("name");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(0));
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_1.getIndex());
        assertEquals(5,
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
                     model.getData().get(0).get(4).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(5);
        assertEquals("$a",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("age",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("$a",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("name",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertEquals(4,
                     uiModelColumn1_2.getIndex());
        assertEquals(5,
                     uiModelColumn2_2.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }
}
