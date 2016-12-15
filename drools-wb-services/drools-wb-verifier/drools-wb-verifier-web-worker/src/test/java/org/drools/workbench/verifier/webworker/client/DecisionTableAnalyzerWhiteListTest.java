/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.verifier.webworker.client;

import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.verifier.webworker.client.testutil.AnalyzerConfigurationMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.verifier.webworker.client.testutil.TestUtil.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerWhiteListTest
        extends AnalyzerUpdateTestBase {


    @Override
    @Before
    public void setUp() throws
                        Exception {
        super.setUp();

        table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn( ">" )
                .withPersonApprovedActionSetField()
                .withData( DataBuilderProvider
                                   .row( 0,
                                         true )
                                   .row( 0,
                                         true )
                                   .row( null,
                                         null )
                                   .end() )
                .buildTable();
    }

    @Test
    public void defaultWhiteList() throws
                                   Exception {

        analyzerProvider.setConfiguration( new AnalyzerConfigurationMock() );

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains( analysisReport,
                            "RedundantRows",
                            "SingleHitLost",
                            "EmptyRule" );
    }

    @Test
    public void noRedundantRows() throws
                                  Exception {

        final AnalyzerConfigurationMock analyzerConfiguration = new AnalyzerConfigurationMock();
        analyzerConfiguration.getCheckWhiteList()
                .getAllowedCheckTypes()
                .remove( CheckType.REDUNDANT_ROWS );
        analyzerConfiguration.getCheckWhiteList()
                .getAllowedCheckTypes()
                .remove( CheckType.SUBSUMPTANT_ROWS );
        analyzerProvider.setConfiguration( analyzerConfiguration );

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains( analysisReport,
                            "SingleHitLost",
                            "EmptyRule" );
    }

    @Test
    public void noEmptyRule() throws
                              Exception {

        final AnalyzerConfigurationMock analyzerConfiguration = new AnalyzerConfigurationMock();
        analyzerConfiguration.getCheckWhiteList()
                .getAllowedCheckTypes()
                .remove( CheckType.EMPTY_RULE );
        analyzerProvider.setConfiguration( analyzerConfiguration );

        fireUpAnalyzer();

        final Set<Issue> analysisReport = analyzerProvider.getAnalysisReport();
        assertOnlyContains( analysisReport,
                            "RedundantRows",
                            "SingleHitLost" );
    }

}