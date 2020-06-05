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
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BaseMultipleDOMElementUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Before;
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

public class ActionWorkItemSetFieldColumnSynchronizerTest extends BaseSynchronizerTest {

    private static final String WORK_ITEM_NAME = "WorkItemDefinition";

    @Before
    public void setupWorkItemExecution() throws VetoException {
        final ActionWorkItemCol52 column = new ActionWorkItemCol52();
        final PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName(WORK_ITEM_NAME);
        column.setWorkItemDefinition(pwd);
        column.setHeader("wid");

        modelSynchronizer.appendColumn(column);
    }

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
                                                                          DataType.TYPE_STRING)}));

        return oracle;
    }

    @Test
    public void testAppend() throws VetoException {
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        column.setWorkItemName(WORK_ITEM_NAME);
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(2,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof BooleanUiColumn);
        assertEquals(true,
                     ((BaseMultipleDOMElementUiColumn) uiModel.getColumns().get(4)).isEditable());
    }

    @Test
    public void testAppendMultipleColumns() throws VetoException {
        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setWorkItemName(WORK_ITEM_NAME);
        column1.setHeader("col1");

        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setWorkItemName(WORK_ITEM_NAME);
        column2.setHeader("col2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        assertEquals(3,
                     model.getActionCols().size());

        assertEquals(6,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof BooleanUiColumn);
        assertTrue(uiModel.getColumns().get(5) instanceof BooleanUiColumn);
        assertEquals(true,
                     ((BaseMultipleDOMElementUiColumn) uiModel.getColumns().get(4)).isEditable());
        assertEquals(true,
                     ((BaseMultipleDOMElementUiColumn) uiModel.getColumns().get(5)).isEditable());

        assertEquals(column1.getHeader(),
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
        assertEquals(column2.getHeader(),
                     uiModel.getColumns().get(5).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate() throws VetoException {
        final ActionWorkItemSetFieldCol52 column = spy(new ActionWorkItemSetFieldCol52());
        column.setWorkItemName(WORK_ITEM_NAME);
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        final ActionWorkItemSetFieldCol52 edited = new ActionWorkItemSetFieldCol52();
        edited.setWorkItemName(WORK_ITEM_NAME);
        edited.setWidth(column.getWidth());
        edited.setHideColumn(true);
        edited.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(column,
                                                                         edited);
        assertEquals(2,
                     diffs.size());
        verify(column).diff(edited);

        assertEquals(2,
                     model.getActionCols().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(4) instanceof BooleanUiColumn);
        assertEquals("updated",
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
        assertEquals(false,
                     uiModel.getColumns().get(4).isVisible());
    }

    @Test
    public void testDelete() throws VetoException {
        final ActionWorkItemSetFieldCol52 column = new ActionWorkItemSetFieldCol52();
        column.setWorkItemName(WORK_ITEM_NAME);
        column.setHeader("col1");

        modelSynchronizer.appendColumn(column);

        assertEquals(2,
                     model.getActionCols().size());
        assertEquals(5,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(column);

        assertEquals(1,
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setWorkItemName(WORK_ITEM_NAME);
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("wid1");
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setWorkItemName(WORK_ITEM_NAME);
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("wid2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(column2,
                     model.getActionCols().get(2));
        assertEquals(true,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(6);
        assertEquals("wid1",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid2",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(5,
                     uiModelColumn1_1.getIndex());
        assertEquals(6,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(5,
                             uiModelColumn2_1);

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(column1,
                     model.getActionCols().get(2));
        assertEquals(false,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(true,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(6);
        assertEquals("wid2",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid1",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(6,
                     uiModelColumn1_2.getIndex());
        assertEquals(5,
                     uiModelColumn2_2.getIndex());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(true,
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setWorkItemName(WORK_ITEM_NAME);
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("wid1");
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setWorkItemName(WORK_ITEM_NAME);
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("wid2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(column2,
                     model.getActionCols().get(2));
        assertEquals(true,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(6);
        assertEquals("wid1",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid2",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(5,
                     uiModelColumn1_1.getIndex());
        assertEquals(6,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(6,
                             uiModelColumn1_1);

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column2,
                     model.getActionCols().get(1));
        assertEquals(column1,
                     model.getActionCols().get(2));
        assertEquals(false,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(true,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(6);
        assertEquals("wid2",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid1",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(6,
                     uiModelColumn1_2.getIndex());
        assertEquals(5,
                     uiModelColumn2_2.getIndex());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(true,
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

        final ActionWorkItemSetFieldCol52 column1 = new ActionWorkItemSetFieldCol52();
        column1.setWorkItemName(WORK_ITEM_NAME);
        column1.setBoundName("$a");
        column1.setFactField("age");
        column1.setHeader("wid1");
        final ActionWorkItemSetFieldCol52 column2 = new ActionWorkItemSetFieldCol52();
        column2.setWorkItemName(WORK_ITEM_NAME);
        column2.setBoundName("$a");
        column2.setFactField("name");
        column2.setHeader("wid2");

        modelSynchronizer.appendColumn(column1);
        modelSynchronizer.appendColumn(column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<Boolean>(true));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<Boolean>(false));

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(column2,
                     model.getActionCols().get(2));
        assertEquals(true,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(6);
        assertEquals("wid1",
                     uiModelColumn1_1.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid2",
                     uiModelColumn2_1.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_1 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_1 instanceof BooleanUiColumn);
        assertEquals(5,
                     uiModelColumn1_1.getIndex());
        assertEquals(6,
                     uiModelColumn2_1.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(2,
                             uiModelColumn1_1);

        assertEquals(3,
                     model.getActionCols().size());
        assertEquals(column1,
                     model.getActionCols().get(1));
        assertEquals(column2,
                     model.getActionCols().get(2));
        assertEquals(true,
                     model.getData().get(0).get(5).getBooleanValue());
        assertEquals(false,
                     model.getData().get(0).get(6).getBooleanValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(6);
        assertEquals("wid1",
                     uiModelColumn1_2.getHeaderMetaData().get(1).getTitle());
        assertEquals("wid2",
                     uiModelColumn2_2.getHeaderMetaData().get(1).getTitle());
        assertTrue(uiModelColumn1_2 instanceof BooleanUiColumn);
        assertTrue(uiModelColumn2_2 instanceof BooleanUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(6,
                     uiModelColumn2_2.getIndex());
        assertEquals(true,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals(false,
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
    }
}
