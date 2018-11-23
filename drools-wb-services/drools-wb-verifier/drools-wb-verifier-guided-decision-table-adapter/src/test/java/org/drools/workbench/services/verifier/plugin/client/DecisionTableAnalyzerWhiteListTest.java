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

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.workbench.services.verifier.plugin.client.testutil.AnalyzerConfigurationMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.services.verifier.plugin.client.testutil.TestUtil.assertOnlyContains;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerWhiteListTest
        extends AnalyzerUpdateTestBase {

    @Override
    @Before
    public void setUp() throws
            Exception {
        super.setUp();

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn(">")
                .withPersonApprovedActionSetField()
                .withData(DataBuilderProvider
                                  .row(0,
                                       true)
                                  .row(0,
                                       true)
                                  .row(null,
                                       null)
                                  .end())
                .buildTable();
    }

    @Test
    public void defaultWhiteList() throws
            Exception {

        analyzerProvider.setConfiguration(new AnalyzerConfigurationMock());

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains(analysisReport,
                           CheckType.REDUNDANT_ROWS,
                           CheckType.SINGLE_HIT_LOST,
                           CheckType.EMPTY_RULE);
    }

    @Test
    public void noRedundantRows() throws
            Exception {

        final AnalyzerConfigurationMock analyzerConfiguration = new AnalyzerConfigurationMock();
        analyzerConfiguration.getCheckConfiguration()
                .getCheckConfiguration()
                .remove(CheckType.REDUNDANT_ROWS);
        analyzerConfiguration.getCheckConfiguration()
                .getCheckConfiguration()
                .remove(CheckType.SUBSUMPTANT_ROWS);
        analyzerProvider.setConfiguration(analyzerConfiguration);

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains(analysisReport,
                           CheckType.SINGLE_HIT_LOST,
                           CheckType.EMPTY_RULE);
    }

    @Test
    public void noEmptyRule() throws
            Exception {

        final AnalyzerConfigurationMock analyzerConfiguration = new AnalyzerConfigurationMock();
        analyzerConfiguration.getCheckConfiguration()
                .getCheckConfiguration()
                .remove(CheckType.EMPTY_RULE);
        analyzerProvider.setConfiguration(analyzerConfiguration);

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains(analysisReport,
                           CheckType.REDUNDANT_ROWS,
                           CheckType.SINGLE_HIT_LOST);
    }
}