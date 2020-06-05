/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertResultIsEmpty;

@RunWith(GwtMockitoTestRunner.class)
public class NoOperatorTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void detectCellOperators() throws
            Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row("< 0", ">= 10", true)
                                  .row(">= 20", ">= 20", true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertContains(analysisReport,
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR,
                       1);
        assertDoesNotContain(CheckType.IMPOSSIBLE_MATCH,
                             analysisReport,
                             2);
        assertContains(analysisReport,
                       CheckType.REDUNDANT_CONDITIONS_TITLE,
                       Severity.NOTE,
                       2);
    }

    @Test
    public void detectCellOperatorsOnUpdate() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withPersonApprovedActionSetField()
                .buildTable();

        fireUpAnalyzer();

        appendRow(DataType.DataTypes.STRING,
                  DataType.DataTypes.STRING,
                  DataType.DataTypes.BOOLEAN);

        setCoordinate().row(0).column(3).toValue(">= 20");
        setCoordinate().row(0).column(4).toValue(">= 20");
        setCoordinate().row(0).column(5).toValue(Boolean.TRUE);

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertDoesNotContain(CheckType.IMPOSSIBLE_MATCH,
                             analysisReport);
        assertContains(analysisReport,
                       CheckType.REDUNDANT_CONDITIONS_TITLE,
                       Severity.NOTE,
                       1);
    }

    @Test
    public void testIntervalsDirectlyInCells() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withNoOperatorConditionIntegerColumn("a",
                                                      "Person",
                                                      "age")
                .withPersonApprovedActionSetField()
                .buildTable();

        fireUpAnalyzer();

        appendRow(DataType.DataTypes.STRING,
                  DataType.DataTypes.STRING,
                  DataType.DataTypes.BOOLEAN);

        setCoordinate().row(0).column(3).toValue(">= 20");
        setCoordinate().row(0).column(4).toValue("<= 30");
        setCoordinate().row(0).column(5).toValue(Boolean.TRUE);

        Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertResultIsEmpty(analysisReport);

        appendRow(DataType.DataTypes.STRING,
                  DataType.DataTypes.STRING,
                  DataType.DataTypes.BOOLEAN);

        setCoordinate().row(1).column(3).toValue("< 20");
        setCoordinate().row(1).column(5).toValue(Boolean.TRUE);

        analysisReport = analyzerProvider.getAnalysisReport();
        assertResultIsEmpty(analysisReport);

        appendRow(DataType.DataTypes.STRING,
                  DataType.DataTypes.STRING,
                  DataType.DataTypes.BOOLEAN);

        setCoordinate().row(2).column(4).toValue("< 20");
        setCoordinate().row(2).column(5).toValue(Boolean.FALSE);

        analysisReport = analyzerProvider.getAnalysisReport();
        assertContains(analysisReport,
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2, 3);
    }
}