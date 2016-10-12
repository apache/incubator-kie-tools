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

import java.util.Collections;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.TestUtil.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RangeCheckFromFileTest {

    @GwtMock
    AnalysisConstants analysisConstants;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();


        when( analyzerProvider.getOracle().getFieldType( "Employee", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( analyzerProvider.getOracle().getFieldType( "Employee", "yearsService" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( analyzerProvider.getOracle().getFieldType( "Employee", "vacationEntitlement" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
    }

    @Test
    public void testFileExtraDays() throws Exception {
        String xml = loadResource( "Extra 5 days.gdst" );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( GuidedDTXMLPersistence.getInstance().unmarshal( xml ) );

        analyzer.analyze( Collections.emptyList() );

        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "MissingRangeTitle" );
    }


}