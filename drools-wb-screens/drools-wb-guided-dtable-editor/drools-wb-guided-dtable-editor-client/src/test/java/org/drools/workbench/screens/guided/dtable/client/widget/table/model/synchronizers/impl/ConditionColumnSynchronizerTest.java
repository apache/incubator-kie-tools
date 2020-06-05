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
import java.util.HashMap;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoDeletePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoUpdatePatternInUseException;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.drools.workbench.screens.guided.rule.client.util.ModelFieldUtil.modelField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ConditionColumnSynchronizerTest extends BaseSynchronizerTest {

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
                                                  modelField("name",
                                                             DataType.TYPE_STRING),
                                                  modelField("approved",
                                                             DataType.TYPE_BOOLEAN)});
                                      put("Address",
                                          new ModelField[]{
                                                  modelField("this",
                                                             "Address"),
                                                  modelField("state",
                                                             DataType.TYPE_STRING),
                                                  modelField("country",
                                                             DataType.TYPE_STRING)});
                                  }
                              }

        );
        return oracle;
    }

    private ConditionCol52 ageEqualsCondition() {
        ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("age");
        condition.setOperator("==");

        return condition;
    }

    private ConditionCol52 nameEqualsCondition() {
        ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col2");
        condition.setFactField("name");
        condition.setOperator("==");
        return condition;
    }

    private Pattern52 boundApplicantPattern(final String boundName) {
        Pattern52 pattern = new Pattern52();
        pattern.setBoundName(boundName);
        pattern.setFactType("Applicant");
        return pattern;
    }

    private Pattern52 boundAddressPattern(final String boundName) {
        Pattern52 pattern = new Pattern52();
        pattern.setBoundName(boundName);
        pattern.setFactType("Address");
        return pattern;
    }

    private ActionCol52 actionUpdatePattern(final String boundName) {
        final ActionSetFieldCol52 action = new ActionSetFieldCol52();
        action.setBoundName(boundName);
        action.setFactField("age");
        action.setHeader("action1");
        return action;
    }

    private BRLActionColumn actionCallMethod(final String boundName) {
        final ActionCallMethod action = new ActionCallMethod();
        action.setVariable(boundName);
        action.setMethodName("toString()");

        final BRLActionColumn brl = new BRLActionColumn();
        brl.setHeader("brl-action");
        brl.setDefinition(Collections.singletonList(action));
        brl.getChildColumns().add(new BRLActionVariableColumn() {{
            setHeader("brl-action-v0");
        }});

        return brl;
    }

    @Test
    public void testOtherwise() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new GuidedDecisionTableUiCell("John"));
        modelSynchronizer.appendRow();
        uiModel.setCellValue(1,
                             3,
                             new GuidedDecisionTableUiCell("George"));
        modelSynchronizer.setCellOtherwiseState(1,
                                                3);

        assertFalse(((GuidedDecisionTableUiCell) uiModel.getCell(0,
                                                                 3).getValue()).isOtherwise());
        assertTrue(((GuidedDecisionTableUiCell) uiModel.getCell(1,
                                                                3).getValue()).isOtherwise());
    }

    @Test
    public void testAppend1() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
    }

    @Test
    public void testAppend2() throws VetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition1);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);

        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition2);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(2,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);
    }

    @Test
    public void testAppend3() throws VetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 condition1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       condition1);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition2.setHeader("col2");
        condition2.setFactField("country");
        condition2.setOperator("==");

        modelSynchronizer.appendColumn(pattern2,
                                       condition2);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(2,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());
        assertEquals(1,
                     model.getConditionPattern("$d").getChildColumns().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);
    }

    @Test
    public void testAppendNegated() throws VetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setNegated(true);
        pattern.setFactType("Applicant");

        final ConditionCol52 condition1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition1);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);

        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition2);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(2,
                     model.getConditions().get(0).getChildColumns().size());
        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);
        assertEquals("not Applicant",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals("not Applicant",
                     uiModel.getColumns().get(4).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testAppendBoolean() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition = new ConditionCol52();
        condition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition.setHeader("col1");
        condition.setFactField("approved");
        condition.setOperator("==");

        //Test column append
        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendRow();

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof BooleanUiColumn);

        //Test row append (boolean cells should be instantiated for Model and UiModel)
        modelSynchronizer.appendRow();

        assertFalse(model.getData().get(0).get(3).getBooleanValue());
        assertFalse(((Boolean) uiModel.getRow(0).getCells().get(3).getValue().getValue()));
    }

    @Test
    public void testUpdate1() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final Pattern52 editedPattern = boundApplicantPattern("$a");

        final ConditionCol52 editedCondition = nameEqualsCondition();
        editedCondition.setWidth(condition.getWidth());

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                         condition,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(2,
                     diffs.size());
        verify(pattern).diff(editedPattern);
        verify(condition).diff(editedCondition);

        assertEquals("header",
                     diffs.get(0).getFieldName());
        assertEquals("factField",
                     diffs.get(1).getFieldName());

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate2() throws VetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition1 = spy(ageEqualsCondition());
        condition1.setHeader("$a age is");

        modelSynchronizer.appendColumn(pattern,
                                       condition1);

        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition2);

        final Pattern52 editedPattern = boundApplicantPattern("$a2");

        final ConditionCol52 editedCondition = ageEqualsCondition();
        editedCondition.setWidth(condition2.getWidth());
        editedCondition.setHeader("$a2 age is");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                         condition1,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(2,
                     diffs.size());
        verify(pattern).diff(editedPattern);
        verify(condition1).diff(editedCondition);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(2,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());
        assertEquals(1,
                     model.getConditionPattern("$a2").getChildColumns().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof IntegerUiColumn);
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(4).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate3() throws VetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition1 = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern1,
                                       condition1);

        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       condition2);

        final Pattern52 editedPattern = boundAddressPattern("$d");

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setWidth(condition1.getWidth());
        editedCondition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        editedCondition.setHeader("col1");
        editedCondition.setFactField("country");
        editedCondition.setOperator("==");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern1,
                                                                         condition1,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(3,
                     diffs.size());
        verify(pattern1).diff(editedPattern);
        verify(condition1).diff(editedCondition);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(2,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());
        assertEquals(1,
                     model.getConditionPattern("$d").getChildColumns().size());

        assertEquals(5,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertTrue(uiModel.getColumns().get(4) instanceof StringUiColumn);
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(4).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(4).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate4() throws VetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition1 = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern1,
                                       condition1);

        final Pattern52 editedPattern = boundAddressPattern("$d");

        final ConditionCol52 editedCondition = new ConditionCol52();
        editedCondition.setWidth(condition1.getWidth());
        editedCondition.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        editedCondition.setHeader("col1");
        editedCondition.setFactField("country");
        editedCondition.setOperator("==");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern1,
                                                                         condition1,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(3,
                     diffs.size());
        verify(pattern1).diff(editedPattern);
        verify(condition1).diff(editedCondition);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertNull(model.getConditionPattern("$a"));
        assertEquals(1,
                     model.getConditionPattern("$d").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate5() throws VetoException {
        final Pattern52 pattern1 = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition1 = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern1,
                                       condition1);

        final Pattern52 editedPattern = boundApplicantPattern("$a");

        final ConditionCol52 editedCondition = ageEqualsCondition();
        editedCondition.setWidth(condition1.getWidth());
        editedCondition.setHideColumn(true);
        editedCondition.setHeader("updated");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern1,
                                                                         condition1,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(2,
                     diffs.size());
        verify(pattern1).diff(editedPattern);
        verify(condition1).diff(editedCondition);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertEquals(false,
                     uiModel.getColumns().get(3).isVisible());
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate6() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        final Pattern52 editedPattern = boundApplicantPattern("$a");

        final ConditionCol52 editedCondition = ageEqualsCondition();
        editedCondition.setWidth(condition.getWidth());
        editedCondition.setOperator("!=");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                         condition,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(1,
                     diffs.size());
        verify(pattern).diff(editedPattern);
        verify(condition).diff(editedCondition);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
        assertTrue(uiModel.getColumns().get(3) instanceof IntegerUiColumn);
        assertEquals(editedPattern.getBoundName() + " : " + editedPattern.getFactType(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void testUpdate7() throws VetoException {
        //Single Pattern (updated), single Condition

        final String boundName = "$a";
        final Pattern52 pattern = spy(boundApplicantPattern(boundName));
        final Pattern52 editedPattern = boundApplicantPattern(boundName);
        final ConditionCol52 condition = spy(ageEqualsCondition());
        final ConditionCol52 editedCondition = nameEqualsCondition();
        final String entryPoint = "entryPoint";

        modelSynchronizer.appendColumn(pattern, condition);

        editedPattern.setEntryPointName(entryPoint);
        editedCondition.setWidth(condition.getWidth());

        final List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                               condition,
                                                                               editedPattern,
                                                                               editedCondition);
        assertEquals(3, diffs.size());

        verify(pattern).diff(editedPattern);
        verify(condition).diff(editedCondition);
        verify(pattern).setEntryPointName(entryPoint);

        assertEquals("entryPointName", diffs.get(0).getFieldName());
        assertEquals("header", diffs.get(1).getFieldName());
        assertEquals("factField", diffs.get(2).getFieldName());

        assertEquals(4, model.getExpandedColumns().size());
        assertEquals(1, model.getConditions().size());
        assertEquals(1, model.getConditionPattern(boundName).getChildColumns().size());

        assertEquals(4, uiModel.getColumns().size());

        assertTrue(uiModel.getColumns().get(3) instanceof StringUiColumn);

        final String expectedTitle = editedPattern.getBoundName() + " : " + editedPattern.getFactType();
        final String actualTitle = uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle();

        assertEquals(expectedTitle, actualTitle);

        final String expectedHeader = editedCondition.getHeader();
        final String actualHeader = uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void testUpdateToNegated() throws VetoException {
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition = spy(ageEqualsCondition());

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        assertEquals("$a : Applicant",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());

        final Pattern52 editedPattern = new Pattern52();
        editedPattern.setNegated(true);
        editedPattern.setFactType("Applicant");

        final ConditionCol52 editedCondition = spy(ageEqualsCondition());

        modelSynchronizer.updateColumn(pattern,
                                       condition,
                                       editedPattern,
                                       editedCondition);

        assertEquals("not Applicant",
                     uiModel.getColumns().get(3).getHeaderMetaData().get(0).getTitle());
        assertEquals(editedCondition.getHeader(),
                     uiModel.getColumns().get(3).getHeaderMetaData().get(1).getTitle());
    }

    @Test
    public void checkConditionCannotBeUpdatedWhenBindingIsUsedInAction() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition = ageEqualsCondition();
        final ActionCol52 action = actionUpdatePattern("$a");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendColumn(action);

        try {
            final Pattern52 editedPattern = boundApplicantPattern("$a2");
            final ConditionCol52 editedCondition = nameEqualsCondition();
            modelSynchronizer.updateColumn(pattern,
                                           condition,
                                           editedPattern,
                                           editedCondition);

            fail("Update of the column should have been vetoed.");
        } catch (VetoUpdatePatternInUseException veto) {
            //This is expected
        } catch (VetoException veto) {
            fail("VetoUpdatePatternInUseException was expected.");
        }

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
        assertEquals(condition,
                     model.getExpandedColumns().get(3));
        assertEquals(action,
                     model.getExpandedColumns().get(4));
    }

    @Test
    public void checkConditionCannotBeUpdatedWhenFieldBindingIsUsedInAction() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition = ageEqualsCondition();
        condition.setBinding("$age");
        final BRLActionColumn action = actionCallMethod("$age");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendColumn(action);

        try {
            final Pattern52 editedPattern = boundApplicantPattern("$a");
            final ConditionCol52 editedCondition = ageEqualsCondition();
            editedCondition.setBinding("$age2");
            modelSynchronizer.updateColumn(pattern,
                                           condition,
                                           editedPattern,
                                           editedCondition);

            fail("Update of the column should have been vetoed.");
        } catch (VetoUpdatePatternInUseException veto) {
            //This is expected
        } catch (VetoException veto) {
            fail("VetoUpdatePatternInUseException was expected.");
        }

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
        assertEquals(condition,
                     model.getExpandedColumns().get(3));
        assertEquals(action.getChildColumns().get(0),
                     model.getExpandedColumns().get(4));
    }

    @Test
    public void checkAddToValueListPreservesData() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition = spy(nameEqualsCondition());
        condition.setValueList("A,B,C");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<>("A"));
        uiModel.setCellValue(1,
                             3,
                             new BaseGridCellValue<>("B"));
        uiModel.setCellValue(2,
                             3,
                             new BaseGridCellValue<>("C"));

        final Pattern52 editedPattern = boundApplicantPattern("$a");

        final ConditionCol52 editedCondition = nameEqualsCondition();
        editedCondition.setWidth(condition.getWidth());
        editedCondition.setValueList("A,B,C,D");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                         condition,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(1,
                     diffs.size());
        verify(pattern).diff(editedPattern);
        verify(condition).diff(editedCondition);

        assertEquals(ConditionCol52.FIELD_VALUE_LIST,
                     diffs.get(0).getFieldName());
        assertEquals("A",
                     uiModel.getCell(0,
                                     3).getValue().getValue().toString());
        assertEquals("B",
                     uiModel.getCell(1,
                                     3).getValue().getValue().toString());
        assertEquals("C",
                     uiModel.getCell(2,
                                     3).getValue().getValue().toString());
        assertEquals("A",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("B",
                     model.getData().get(1).get(3).getStringValue());
        assertEquals("C",
                     model.getData().get(2).get(3).getStringValue());
    }

    @Test
    public void checkRemoveFromValueListClearsData() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = spy(boundApplicantPattern("$a"));

        final ConditionCol52 condition = spy(nameEqualsCondition());
        condition.setValueList("A,B,C");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<>("A"));
        uiModel.setCellValue(1,
                             3,
                             new BaseGridCellValue<>("B"));
        uiModel.setCellValue(2,
                             3,
                             new BaseGridCellValue<>("C"));

        final Pattern52 editedPattern = boundApplicantPattern("$a");

        final ConditionCol52 editedCondition = nameEqualsCondition();
        editedCondition.setWidth(condition.getWidth());
        editedCondition.setValueList("A");

        List<BaseColumnFieldDiff> diffs = modelSynchronizer.updateColumn(pattern,
                                                                         condition,
                                                                         editedPattern,
                                                                         editedCondition);
        assertEquals(1,
                     diffs.size());
        verify(pattern).diff(editedPattern);
        verify(condition).diff(editedCondition);

        assertEquals(ConditionCol52.FIELD_VALUE_LIST,
                     diffs.get(0).getFieldName());
        assertEquals("A",
                     uiModel.getCell(0,
                                     3).getValue().getValue().toString());
        assertNull(uiModel.getCell(1,
                                   3));
        assertNull(uiModel.getCell(2,
                                   3));
        assertEquals("A",
                     model.getData().get(0).get(3).getStringValue());
        assertFalse(model.getData().get(1).get(3).hasValue());
        assertFalse(model.getData().get(2).get(3).hasValue());
    }

    @Test
    public void testDelete1() throws VetoException {
        //Single Pattern, single Condition
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(4,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(condition);

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertEquals(0,
                     model.getConditions().size());
        assertNull(model.getConditionPattern("$a"));

        assertEquals(3,
                     uiModel.getColumns().size());
    }

    @Test
    public void testDelete2() throws VetoException {
        //Single Pattern, multiple Conditions
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 condition1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition1);

        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition2);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());

        assertEquals(5,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(condition1);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
    }

    @Test
    public void testDelete3() throws VetoException {
        //Multiple Patterns, multiple Conditions
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 condition1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       condition1);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 condition2 = new ConditionCol52();
        condition2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        condition2.setHeader("col2");
        condition2.setFactField("country");
        condition2.setOperator("==");

        modelSynchronizer.appendColumn(pattern2,
                                       condition2);

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertEquals(2,
                     model.getConditions().size());
        assertEquals(1,
                     model.getConditionPattern("$a").getChildColumns().size());
        assertEquals(1,
                     model.getConditionPattern("$d").getChildColumns().size());

        assertEquals(5,
                     uiModel.getColumns().size());

        modelSynchronizer.deleteColumn(condition1);

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertEquals(1,
                     model.getConditions().size());
        assertNull(model.getConditionPattern("$a"));
        assertEquals(1,
                     model.getConditionPattern("$d").getChildColumns().size());

        assertEquals(4,
                     uiModel.getColumns().size());
    }

    @Test
    public void checkConditionCannotBeDeletedWithSingleChildColumnWithAction() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition = ageEqualsCondition();
        final ActionCol52 action = actionUpdatePattern("$a");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendColumn(action);

        try {
            modelSynchronizer.deleteColumn(condition);

            fail("Deletion of the column should have been vetoed.");
        } catch (VetoDeletePatternInUseException veto) {
            //This is expected
        } catch (VetoException veto) {
            fail("VetoDeletePatternInUseException was expected.");
        }

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
        assertEquals(condition,
                     model.getExpandedColumns().get(3));
        assertEquals(action,
                     model.getExpandedColumns().get(4));
    }

    @Test
    public void checkConditionCannotBeDeletedWhenFieldBindingIsUsedInAction() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition = ageEqualsCondition();
        condition.setBinding("$age");
        final BRLActionColumn action = actionCallMethod("$age");

        modelSynchronizer.appendColumn(pattern,
                                       condition);
        modelSynchronizer.appendColumn(action);

        try {
            modelSynchronizer.deleteColumn(condition);

            fail("Deletion of the column should have been vetoed.");
        } catch (VetoDeletePatternInUseException veto) {
            //This is expected
        } catch (VetoException veto) {
            fail("VetoDeletePatternInUseException was expected.");
        }

        assertEquals(5,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
        assertEquals(condition,
                     model.getExpandedColumns().get(3));
        assertEquals(action.getChildColumns().get(0),
                     model.getExpandedColumns().get(4));
    }

    @Test
    public void checkConditionCanBeDeletedWithSingleChildColumnWithNoAction() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition);

        try {
            modelSynchronizer.deleteColumn(condition);
        } catch (VetoException veto) {
            fail("Deletion should have been permitted.");
        }

        assertEquals(3,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
    }

    @Test
    public void checkConditionCanBeDeletedWithMultipleChildColumns() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");
        final ConditionCol52 condition1 = ageEqualsCondition();
        final ConditionCol52 condition2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       condition1);
        modelSynchronizer.appendColumn(pattern,
                                       condition2);

        try {
            modelSynchronizer.deleteColumn(condition2);
        } catch (VetoException veto) {
            fail("Deletion should have been permitted.");
        }

        assertEquals(4,
                     model.getExpandedColumns().size());
        assertTrue(model.getExpandedColumns().get(0) instanceof RowNumberCol52);
        assertTrue(model.getExpandedColumns().get(1) instanceof RuleNameColumn);
        assertTrue(model.getExpandedColumns().get(2) instanceof DescriptionCol52);
        assertEquals(condition1,
                     model.getExpandedColumns().get(3));
    }

    @Test
    public void testMoveLeftNegatedPattern() throws VetoException {
        final Pattern52 pattern = new Pattern52();
        pattern.setNegated(true);
        pattern.setFactType("Applicant");

        final ConditionCol52 column1 = ageEqualsCondition();

        final ConditionCol52 column2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       column1);
        modelSynchronizer.appendColumn(pattern,
                                       column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1,
                     conditionColumns1_1.get(0));
        assertEquals(column2,
                     conditionColumns1_1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        assertEquals("not Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("not Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
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

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column2,
                     conditionColumns1_2.get(0));
        assertEquals(column1,
                     conditionColumns1_2.get(1));
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("not Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("not Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
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
    public void testMoveColumnTo_MoveLeft() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 column1 = ageEqualsCondition();

        final ConditionCol52 column2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       column1);
        modelSynchronizer.appendColumn(pattern,
                                       column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1,
                     conditionColumns1_1.get(0));
        assertEquals(column2,
                     conditionColumns1_1.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
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

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column2,
                     conditionColumns1_2.get(0));
        assertEquals(column1,
                     conditionColumns1_2.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
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
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 column1 = ageEqualsCondition();

        final ConditionCol52 column2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       column1);
        modelSynchronizer.appendColumn(pattern,
                                       column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1,
                     conditionColumns1_1.get(0));
        assertEquals(column2,
                     conditionColumns1_1.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
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

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column2,
                     conditionColumns1_2.get(0));
        assertEquals(column1,
                     conditionColumns1_2.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
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
    public void testMoveColumnTo_OutOfBounds_OutOfConditions() throws VetoException {
        final Pattern52 pattern = boundApplicantPattern("$a");

        final ConditionCol52 column1 = ageEqualsCondition();

        final ConditionCol52 column2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern,
                                       column1);
        modelSynchronizer.appendColumn(pattern,
                                       column2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1,
                     conditionColumns1_1.get(0));
        assertEquals(column2,
                     conditionColumns1_1.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
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

        assertEquals(1,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1,
                     conditionColumns1_2.get(0));
        assertEquals(column2,
                     conditionColumns1_2.get(1));
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
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
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

    @Test
    public void testMoveColumnTo_OutOfBounds_OutOfPattern() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);

        final ConditionCol52 column2p1 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column2p1);

        final Pattern52 pattern2 = boundApplicantPattern("$a2");

        final ConditionCol52 column1p2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Fred"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_1.get(1));
        final List<ConditionCol52> conditionColumns2_1 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns2_1.get(0));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Fred",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a2 : Applicant",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Fred",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());

        uiModel.moveColumnTo(5,
                             uiModelColumn1_1);

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_2.get(0));
        assertEquals(column2p1,
                     conditionColumns1_2.get(1));
        final List<ConditionCol52> conditionColumns2_2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns2_2.get(0));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Fred",
                     model.getData().get(0).get(4).getStringValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(5).getStringValue());

        assertEquals(6,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        assertEquals("$a : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a2 : Applicant",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals(5,
                     uiModelColumn3_2.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Fred",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnTo_SingleColumnPattern() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);

        final Pattern52 pattern2 = boundApplicantPattern("$a2");

        final ConditionCol52 column1p2 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1.get(0));
        final List<ConditionCol52> conditionColumns2_1 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns2_1.get(0));
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
        assertEquals("$a2 : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
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
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_2.get(0));
        final List<ConditionCol52> conditionColumns2_2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns2_2.get(0));
        assertEquals("Smurf",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals(45,
                     model.getData().get(0).get(4).getNumericValue());

        assertEquals(5,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        assertEquals("$a2 : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
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
    public void testMoveColumnsTo_MoveLeft() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        final ConditionCol52 column2p1 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);
        modelSynchronizer.appendColumn(pattern1,
                                       column2p1);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p2.setFactField("state");
        column1p2.setOperator("==");
        column1p2.setHeader("state");

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p2.setFactField("country");
        column2p2.setOperator("==");
        column2p2.setHeader("country");

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);
        modelSynchronizer.appendColumn(pattern2,
                                       column2p2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("NY"));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<String>("America"));

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_1p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_1p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_1p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(5).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(6).getStringValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get(6);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn4_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn4_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(6,
                     uiModelColumn4_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_1.getIndex()).getValue().getValue());

        uiModel.moveColumnsTo(3,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn3_1);
                                  add(uiModelColumn4_1);
                              }});

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_2p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_2p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_2p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_2p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(5).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(6).getStringValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get(6);
        assertEquals("$d : Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn4_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn4_2 instanceof StringUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(6,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals(4,
                     uiModelColumn4_2.getIndex());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnsTo_MoveLeft_MidPoint() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        final ConditionCol52 column2p1 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);
        modelSynchronizer.appendColumn(pattern1,
                                       column2p1);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p2.setFactField("state");
        column1p2.setOperator("==");
        column1p2.setHeader("state $d");

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p2.setFactField("country");
        column2p2.setOperator("==");
        column2p2.setHeader("country $d");

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);
        modelSynchronizer.appendColumn(pattern2,
                                       column2p2);

        final Pattern52 pattern3 = boundAddressPattern("$d2");

        final ConditionCol52 column1p3 = new ConditionCol52();
        column1p3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p3.setFactField("state");
        column1p3.setOperator("==");
        column1p3.setHeader("state $d2");

        final ConditionCol52 column2p3 = new ConditionCol52();
        column2p3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p3.setFactField("country");
        column2p3.setOperator("==");
        column2p3.setHeader("country $d2");

        modelSynchronizer.appendColumn(pattern3,
                                       column1p3);
        modelSynchronizer.appendColumn(pattern3,
                                       column2p3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("NY"));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<String>("America"));
        uiModel.setCellValue(0,
                             7,
                             new BaseGridCellValue<String>("Essex"));
        uiModel.setCellValue(0,
                             8,
                             new BaseGridCellValue<String>("England"));

        assertEquals(3,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_1p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_1p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_1p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(5).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(6).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p3 = model.getPatterns().get(2).getChildColumns();
        assertEquals(column1p3,
                     conditionColumns1_1p3.get(0));
        assertEquals(column2p3,
                     conditionColumns1_1p3.get(1));
        assertEquals("Essex",
                     model.getData().get(0).get(7).getStringValue());
        assertEquals("England",
                     model.getData().get(0).get(8).getStringValue());

        assertEquals(9,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get(6);
        final GridColumn<?> uiModelColumn5_1 = uiModel.getColumns().get(7);
        final GridColumn<?> uiModelColumn6_1 = uiModel.getColumns().get(8);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn4_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn5_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn6_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn4_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn5_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn6_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(6,
                     uiModelColumn4_1.getIndex());
        assertEquals(7,
                     uiModelColumn5_1.getIndex());
        assertEquals(8,
                     uiModelColumn6_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_1.getIndex()).getValue().getValue());
        assertEquals("Essex",
                     uiModel.getRow(0).getCells().get(uiModelColumn5_1.getIndex()).getValue().getValue());
        assertEquals("England",
                     uiModel.getRow(0).getCells().get(uiModelColumn6_1.getIndex()).getValue().getValue());

        uiModel.moveColumnsTo(5,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn5_1);
                                  add(uiModelColumn6_1);
                              }});

        assertEquals(3,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_2p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_2p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p3 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p3,
                     conditionColumns1_2p3.get(0));
        assertEquals(column2p3,
                     conditionColumns1_2p3.get(1));
        assertEquals("Essex",
                     model.getData().get(0).get(5).getStringValue());
        assertEquals("England",
                     model.getData().get(0).get(6).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get(2).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_2p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_2p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(7).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(8).getStringValue());

        assertEquals(9,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get(6);
        final GridColumn<?> uiModelColumn5_2 = uiModel.getColumns().get(7);
        final GridColumn<?> uiModelColumn6_2 = uiModel.getColumns().get(8);
        assertEquals("$a : Applicant",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn4_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn5_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn6_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn4_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn5_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn6_2 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_2.getIndex());
        assertEquals(4,
                     uiModelColumn2_2.getIndex());
        assertEquals(7,
                     uiModelColumn3_2.getIndex());
        assertEquals(8,
                     uiModelColumn4_2.getIndex());
        assertEquals(5,
                     uiModelColumn5_2.getIndex());
        assertEquals(6,
                     uiModelColumn6_2.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals("Essex",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
        assertEquals("England",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_2.getIndex()).getValue().getValue());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn5_2.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn6_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnsTo_MoveRight() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        final ConditionCol52 column2p1 = nameEqualsCondition();

        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);
        modelSynchronizer.appendColumn(pattern1,
                                       column2p1);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p2.setFactField("state");
        column1p2.setOperator("==");
        column1p2.setHeader("state");

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p2.setFactField("country");
        column2p2.setOperator("==");
        column2p2.setHeader("country");

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);
        modelSynchronizer.appendColumn(pattern2,
                                       column2p2);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("NY"));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<String>("America"));

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_1p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_1p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_1p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(5).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(6).getStringValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get(6);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn4_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn4_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(6,
                     uiModelColumn4_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_1.getIndex()).getValue().getValue());

        // The target column index is the right-most of the target pattern. This
        // index is provided by wires-grid's at runtime when dragging blocked columns.
        uiModel.moveColumnsTo(6,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }});

        assertEquals(2,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_2p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_2p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_2p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_2p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(5).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(6).getStringValue());

        assertEquals(7,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get(6);
        assertEquals("$d : Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn4_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn4_2 instanceof StringUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(6,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals(4,
                     uiModelColumn4_2.getIndex());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_2.getIndex()).getValue().getValue());
    }

    @Test
    public void testMoveColumnsTo_MoveRight_MidPoint() throws VetoException {
        final Pattern52 pattern1 = boundApplicantPattern("$a");

        final ConditionCol52 column1p1 = ageEqualsCondition();

        final ConditionCol52 column2p1 = nameEqualsCondition();
        modelSynchronizer.appendColumn(pattern1,
                                       column1p1);
        modelSynchronizer.appendColumn(pattern1,
                                       column2p1);

        final Pattern52 pattern2 = boundAddressPattern("$d");

        final ConditionCol52 column1p2 = new ConditionCol52();
        column1p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p2.setFactField("state");
        column1p2.setOperator("==");
        column1p2.setHeader("state");

        final ConditionCol52 column2p2 = new ConditionCol52();
        column2p2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p2.setFactField("country");
        column2p2.setOperator("==");
        column2p2.setHeader("country");

        modelSynchronizer.appendColumn(pattern2,
                                       column1p2);
        modelSynchronizer.appendColumn(pattern2,
                                       column2p2);

        final Pattern52 pattern3 = boundAddressPattern("$d2");

        final ConditionCol52 column1p3 = new ConditionCol52();
        column1p3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column1p3.setFactField("state");
        column1p3.setOperator("==");
        column1p3.setHeader("state");

        final ConditionCol52 column2p3 = new ConditionCol52();
        column2p3.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        column2p3.setFactField("country");
        column2p3.setOperator("==");
        column2p3.setHeader("country");

        modelSynchronizer.appendColumn(pattern3,
                                       column1p3);
        modelSynchronizer.appendColumn(pattern3,
                                       column2p3);

        modelSynchronizer.appendRow();
        uiModel.setCellValue(0,
                             3,
                             new BaseGridCellValue<Integer>(45));
        uiModel.setCellValue(0,
                             4,
                             new BaseGridCellValue<String>("Smurf"));
        uiModel.setCellValue(0,
                             5,
                             new BaseGridCellValue<String>("NY"));
        uiModel.setCellValue(0,
                             6,
                             new BaseGridCellValue<String>("America"));
        uiModel.setCellValue(0,
                             7,
                             new BaseGridCellValue<String>("Essex"));
        uiModel.setCellValue(0,
                             8,
                             new BaseGridCellValue<String>("England"));

        assertEquals(3,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_1p1 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_1p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_1p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(3).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p2 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_1p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_1p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(5).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(6).getStringValue());
        final List<ConditionCol52> conditionColumns1_1p3 = model.getPatterns().get(2).getChildColumns();
        assertEquals(column1p3,
                     conditionColumns1_1p3.get(0));
        assertEquals(column2p3,
                     conditionColumns1_1p3.get(1));
        assertEquals("Essex",
                     model.getData().get(0).get(7).getStringValue());
        assertEquals("England",
                     model.getData().get(0).get(8).getStringValue());

        assertEquals(9,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_1 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_1 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_1 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_1 = uiModel.getColumns().get(6);
        final GridColumn<?> uiModelColumn5_1 = uiModel.getColumns().get(7);
        final GridColumn<?> uiModelColumn6_1 = uiModel.getColumns().get(8);
        assertEquals("$a : Applicant",
                     uiModelColumn1_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn2_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn3_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn4_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn5_1.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn6_1.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_1 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn2_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn4_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn5_1 instanceof StringUiColumn);
        assertTrue(uiModelColumn6_1 instanceof StringUiColumn);
        assertEquals(3,
                     uiModelColumn1_1.getIndex());
        assertEquals(4,
                     uiModelColumn2_1.getIndex());
        assertEquals(5,
                     uiModelColumn3_1.getIndex());
        assertEquals(6,
                     uiModelColumn4_1.getIndex());
        assertEquals(7,
                     uiModelColumn5_1.getIndex());
        assertEquals(8,
                     uiModelColumn6_1.getIndex());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn1_1.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_1.getIndex()).getValue().getValue());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn3_1.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_1.getIndex()).getValue().getValue());
        assertEquals("Essex",
                     uiModel.getRow(0).getCells().get(uiModelColumn5_1.getIndex()).getValue().getValue());
        assertEquals("England",
                     uiModel.getRow(0).getCells().get(uiModelColumn6_1.getIndex()).getValue().getValue());

        // The target column index is the right-most of the target pattern. This
        // index is provided by wires-grid's at runtime when dragging blocked columns.
        uiModel.moveColumnsTo(6,
                              new ArrayList<GridColumn<?>>() {{
                                  add(uiModelColumn1_1);
                                  add(uiModelColumn2_1);
                              }});

        assertEquals(3,
                     model.getPatterns().size());
        final List<ConditionCol52> conditionColumns1_2p2 = model.getPatterns().get(0).getChildColumns();
        assertEquals(column1p2,
                     conditionColumns1_2p2.get(0));
        assertEquals(column2p2,
                     conditionColumns1_2p2.get(1));
        assertEquals("NY",
                     model.getData().get(0).get(3).getStringValue());
        assertEquals("America",
                     model.getData().get(0).get(4).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p1 = model.getPatterns().get(1).getChildColumns();
        assertEquals(column1p1,
                     conditionColumns1_2p1.get(0));
        assertEquals(column2p1,
                     conditionColumns1_2p1.get(1));
        assertEquals(45,
                     model.getData().get(0).get(5).getNumericValue());
        assertEquals("Smurf",
                     model.getData().get(0).get(6).getStringValue());
        final List<ConditionCol52> conditionColumns1_2p3 = model.getPatterns().get(2).getChildColumns();
        assertEquals(column1p3,
                     conditionColumns1_2p3.get(0));
        assertEquals(column2p3,
                     conditionColumns1_2p3.get(1));
        assertEquals("Essex",
                     model.getData().get(0).get(7).getStringValue());
        assertEquals("England",
                     model.getData().get(0).get(8).getStringValue());

        assertEquals(9,
                     uiModel.getColumns().size());
        final GridColumn<?> uiModelColumn1_2 = uiModel.getColumns().get(3);
        final GridColumn<?> uiModelColumn2_2 = uiModel.getColumns().get(4);
        final GridColumn<?> uiModelColumn3_2 = uiModel.getColumns().get(5);
        final GridColumn<?> uiModelColumn4_2 = uiModel.getColumns().get(6);
        final GridColumn<?> uiModelColumn5_2 = uiModel.getColumns().get(7);
        final GridColumn<?> uiModelColumn6_2 = uiModel.getColumns().get(8);
        assertEquals("$d : Address",
                     uiModelColumn1_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d : Address",
                     uiModelColumn2_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn3_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$a : Applicant",
                     uiModelColumn4_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn5_2.getHeaderMetaData().get(0).getTitle());
        assertEquals("$d2 : Address",
                     uiModelColumn6_2.getHeaderMetaData().get(0).getTitle());
        assertTrue(uiModelColumn1_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn2_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn3_2 instanceof IntegerUiColumn);
        assertTrue(uiModelColumn4_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn5_2 instanceof StringUiColumn);
        assertTrue(uiModelColumn6_2 instanceof StringUiColumn);
        assertEquals(5,
                     uiModelColumn1_2.getIndex());
        assertEquals(6,
                     uiModelColumn2_2.getIndex());
        assertEquals(3,
                     uiModelColumn3_2.getIndex());
        assertEquals(4,
                     uiModelColumn4_2.getIndex());
        assertEquals(7,
                     uiModelColumn5_2.getIndex());
        assertEquals(8,
                     uiModelColumn6_2.getIndex());
        assertEquals("NY",
                     uiModel.getRow(0).getCells().get(uiModelColumn1_2.getIndex()).getValue().getValue());
        assertEquals("America",
                     uiModel.getRow(0).getCells().get(uiModelColumn2_2.getIndex()).getValue().getValue());
        assertEquals(45,
                     uiModel.getRow(0).getCells().get(uiModelColumn3_2.getIndex()).getValue().getValue());
        assertEquals("Smurf",
                     uiModel.getRow(0).getCells().get(uiModelColumn4_2.getIndex()).getValue().getValue());
        assertEquals("Essex",
                     uiModel.getRow(0).getCells().get(uiModelColumn5_2.getIndex()).getValue().getValue());
        assertEquals("England",
                     uiModel.getRow(0).getCells().get(uiModelColumn6_2.getIndex()).getValue().getValue());
    }
}
