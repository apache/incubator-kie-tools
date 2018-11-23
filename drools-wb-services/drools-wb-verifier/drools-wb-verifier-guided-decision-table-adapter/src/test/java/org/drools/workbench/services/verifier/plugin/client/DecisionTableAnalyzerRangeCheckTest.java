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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertDoesNotContain;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerRangeCheckTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testMissingRangeNoIssueNameHasNoRange() throws Exception {

        analyzer = analyzerProvider.makeAnalyser()
                .withPersonNameColumn("==")
                .withApplicationApprovedSetField()
                .withData(DataBuilderProvider
                                  .row("Toni",
                                       true)
                                  .row("Michael",
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.MISSING_RANGE,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingRangeNoIssue() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonApprovedColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(true,
                                       true)
                                  .row(false,
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.MISSING_RANGE,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    @Ignore("list of admitted values is in the model and currently not accessible for the analyzer")
    public void testMissingRangeMissingNotApprovedFromLHS() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonApprovedColumn("==")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(true,
                                       true)
                                  .row(true,
                                       false)
                                  .end()
                )
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE);
    }

    @Test
    public void testMissingAgeBetween1And100() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("<")
                .withPersonAgeColumn(">=")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(0,
                                       null,
                                       true)
                                  .row(null,
                                       100,
                                       false)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE);
    }

    @Test
    public void testCompleteAgeRange() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn("<")
                .withPersonAgeColumn(">=")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(0,
                                       null,
                                       true)
                                  .row(null,
                                       0,
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.MISSING_RANGE,
                             analyzerProvider.getAnalysisReport());
    }

    @Test
    public void testMissingDepositBetween0And12345() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withAccountDepositColumn("<")
                .withAccountDepositColumn(">")
                .withPersonApprovedActionInsertFact()
                .withData(DataBuilderProvider
                                  .row(0.0,
                                       null,
                                       true)
                                  .row(null,
                                       12345.0,
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertContains(analyzerProvider.getAnalysisReport(),
                       CheckType.MISSING_RANGE,
                       Severity.NOTE);
    }

    @Test
    public void testCompleteAccountRange() throws Exception {
        analyzer = analyzerProvider.makeAnalyser()
                .withAccountDepositColumn(">=")
                .withAccountDepositColumn("<")
                .withPersonApprovedActionInsertFact()
                .withData(DataBuilderProvider
                                  .row(0.0,
                                       null,
                                       true)
                                  .row(null,
                                       0.0,
                                       true)
                                  .end())
                .buildAnalyzer();

        fireUpAnalyzer();

        assertDoesNotContain(CheckType.MISSING_RANGE,
                             analyzerProvider.getAnalysisReport());
    }
}