/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.services.verifier.plugin.client.testutil.ExtendedGuidedDecisionTableBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerUpdateTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void updateRetract() throws Exception {

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withRetract()
                .withData(DataBuilderProvider
                                  .row(1,
                                       null)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_ACTION,
                       Severity.WARNING);

        setValue(0,
                 3,
                 "a");

        assertDoesNotContain(CheckType.MISSING_ACTION,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testRowValueChange() throws Exception {

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       true)
                                  .row(1,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1, 2);

        setValue(1,
                 2,
                 0);

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());
    }

    @Test
    public void testUpdateDateEffective() throws Exception {

        table52 = analyzerProvider.makeAnalyser()
                .withAttributeColumn(Attribute.DATE_EFFECTIVE)
                .withAttributeColumn(Attribute.DATE_EXPIRES)
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(new Date(2000, 11, 01), new Date(3000, 11, 02), 1, true)
                                  .row(new Date(2000, 11, 03), new Date(5000, 11, 04), 1, true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1, 2);

        setValue(1,
                 2,
                 new Date(4000, 11, 05));

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());
    }

    @Test
    public void testUpdateDateExpires() throws Exception {

        table52 = analyzerProvider.makeAnalyser()
                .withAttributeColumn(Attribute.DATE_EFFECTIVE)
                .withAttributeColumn(Attribute.DATE_EXPIRES)
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(new Date(2000, 11, 01), new Date(3000, 11, 02), 1, true)
                                  .row(new Date(1000, 11, 03), new Date(5000, 11, 04), 1, true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1, 2);

        setValue(1,
                 3,
                 new Date(1500, 11, 05));

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());
    }

    @Test
    public void testRemoveRow() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       1,
                                       true)
                                  .row(0,
                                       1,
                                       true)
                                  .row(2,
                                       2,
                                       true)
                                  .row(1,
                                       1,
                                       false)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       4);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       2);

        removeRow(1);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       3);
        assertDoesNotContain(CheckType.IMPOSSIBLE_MATCH,
                             analyzerProvider.getAnalysisReport(),
                             3);

        // BREAK LINE NUMBER 2 ( previously line number 3 )
        setValue(1,
                 3,
                 1);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       2);
    }

    @Test
    public void testRemoveRow2() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonAgeColumn("<")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       10,
                                       true)
                                  .row(1,
                                       10,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.REDUNDANT_ROWS,
                       Severity.WARNING,
                       1, 2);

        // REMOVE 2
        removeRow(0);

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testRemoveColumn() throws Exception {

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       true)
                                  .row(2,
                                       true)
                                  .row(3,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());

        removeActionColumn(3,
                           0);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_ACTION,
                       Severity.WARNING);
    }

    @Test
    public void testAddColumn() throws Exception {
        table52 = analyzerProvider
                .makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       true)
                                  .row(2,
                                       true)
                                  .row(3,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());

        appendActionColumn(4,
                           ExtendedGuidedDecisionTableBuilder.createActionSetField("a",
                                                                                   "approved",
                                                                                   DataType.TYPE_BOOLEAN),
                           true,
                           true,
                           false);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MULTIPLE_VALUES_FOR_ONE_ACTION,
                       Severity.WARNING,
                       3);
    }

    @Test
    public void testAddBRLColumn() throws Exception {
        table52 = analyzerProvider
                .makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       true)
                                  .row(2,
                                       true)
                                  .row(3,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertTrue(analyzerProvider.getAnalysisReport().isEmpty());

        insertConditionColumn(3,
                              ExtendedGuidedDecisionTableBuilder.createBRLConditionColumn(),
                              true,
                              true,
                              true);

        assertDoesNotContain(CheckType.REDUNDANT_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testInsertRow() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       1,
                                       true)
                                  .row(0,
                                       1,
                                       true)
                                  .row(2,
                                       2,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        insertRow(0,
                  DataType.DataTypes.NUMERIC,
                  DataType.DataTypes.NUMERIC,
                  DataType.DataTypes.BOOLEAN);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       3);
    }

    @Test
    public void testAppendRow() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(1,
                                       1,
                                       true)
                                  .row(0,
                                       1,
                                       true)
                                  .row(2,
                                       2,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       2);

        appendRow(DataType.DataTypes.NUMERIC,
                  DataType.DataTypes.NUMERIC,
                  DataType.DataTypes.STRING);

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       2);
    }
}