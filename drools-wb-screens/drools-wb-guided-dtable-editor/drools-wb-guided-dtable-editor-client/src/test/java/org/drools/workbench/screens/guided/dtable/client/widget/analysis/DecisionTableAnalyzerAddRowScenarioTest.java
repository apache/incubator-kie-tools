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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.TestUtil.*;
import static org.junit.Assert.*;

@RunWith( GwtMockitoTestRunner.class )
public class DecisionTableAnalyzerAddRowScenarioTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void emptyLineThatIsAddedShouldBeIgnored() throws Exception {
        table52 = analyzerProvider.makeAnalyser()
                                  .withPersonAgeColumn( ">" )
                                  .withPersonApprovedActionSetField()
                                  .withData( DataBuilderProvider
                                                     .row( 0, true )
                                                     .end() )
                                  .buildTable();

        fireUpAnalyzer();

        appendRow( 2 );

        final AnalysisReport analysisReport = analyzerProvider.getAnalysisReport();
        assertContains( "SingleHitLost", analyzerProvider.getAnalysisReport() );
        assertEquals( 1, analysisReport.getAnalysisData().size() );
    }

}