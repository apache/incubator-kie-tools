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

package org.drools.workbench.services.verifier.webworker.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertDoesNotContain;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerConflictResolverTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testNoIssue() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withAccountDepositColumn("<")
                .withApplicationApprovedSetField()
                .withData(DataBuilderProvider
                                  .row(100,
                                       0.0,
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.CONFLICTING_ROWS,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testNoIssueWithNulls() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonAgeColumn("<")
                .withData(DataBuilderProvider
                                  .row(null,
                                       null)
                                  .end())
                .buildTable();

        // After a save has been done, the server side sometimes sets the String field value to "" for numbers, even when the data type is a number
        table52.getData().get(0).get(2).setStringValue("");
        table52.getData().get(0).get(3).setStringValue("");

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.EMPTY_RULE,
                       Severity.WARNING,
                       1);
    }

    @Test
    public void testImpossibleMatch001() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonAgeColumn("<")
                .withData(DataBuilderProvider
                                  .row(100,
                                       0)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.IMPOSSIBLE_MATCH,
                       Severity.ERROR
        );
    }

    @Test
    public void testImpossibleMatch002() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withEnumColumn("a",
                                "Person",
                                "name",
                                "==",
                                "Toni,Eder")
                .withPersonNameColumn("==")
                .withData(DataBuilderProvider
                                  .row("Toni",
                                       "")
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.IMPOSSIBLE_MATCH,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testConflictIgnoreEmptyRows() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(null,
                                       "")
                                  .row(null,
                                       "true")
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.CONFLICTING_ROWS,
                             analyzerProvider.getAnalysisReport(),
                             1);
        assertDoesNotContain(CheckType.CONFLICTING_ROWS,
                             analyzerProvider.getAnalysisReport(),
                             2);
    }

    @Test
    public void testConflictWithASubsumingRow() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("==")
                .withPersonNameColumn("==")
                .withPersonLastNameColumn("==")
                .withPersonSalarySetFieldAction()
                .withPersonDescriptionSetActionField()
                .withData(DataBuilderProvider
                                  .row(null,
                                       null,
                                       null,
                                       100,
                                       "ok")
                                  .row(null,
                                       "Toni",
                                       null,
                                       200,
                                       "ok")
                                  .row(12,
                                       "Toni",
                                       "Rikkola",
                                       300,
                                       "ok")
                                  .row(null,
                                       null,
                                       null,
                                       null,
                                       null)
                                  .end()
                )
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       2);
        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.CONFLICTING_ROWS,
                       Severity.WARNING,
                       3);
    }
}