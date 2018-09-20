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

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertContains;
import static org.drools.workbench.services.verifier.webworker.client.testutil.TestUtil.assertOnlyContains;
import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerAddRowScenarioTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void emptyLineThatIsAddedShouldBeIgnored() throws
            Exception {
        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(Integer.MIN_VALUE,
                                       true)
                                  .end())
                .buildTable();

        fireUpAnalyzer();

        appendRow(DataType.DataTypes.NUMERIC,
                  DataType.DataTypes.BOOLEAN);

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains(analysisReport,
                           CheckType. SINGLE_HIT_LOST,
                           CheckType.EMPTY_RULE);
        assertContains(analysisReport,
                       CheckType. EMPTY_RULE,
                       Severity.WARNING,
                       2);
        assertEquals(2,
                     analysisReport.size());
    }
}