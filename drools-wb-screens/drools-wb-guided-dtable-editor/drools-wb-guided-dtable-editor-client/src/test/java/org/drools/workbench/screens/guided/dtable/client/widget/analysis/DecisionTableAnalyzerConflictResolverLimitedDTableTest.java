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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerConflictResolverLimitedDTableTest {

    @GwtMock
    AnalysisConstants analysisConstants;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Mock
    AsyncPackageDataModelOracle oracle;
    private AnalysisReport analysisReport;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "lastName" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Account", "deposit" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

    }

    @Test
    public void testConflict() throws Exception {

        final GuidedDecisionTable52 table52 = new LimitedGuidedDecisionTableBuilder( "org.test",
                                                                                     new ArrayList<Import>(),
                                                                                     "mytable" )
                .withIntegerColumn( "a", "Person", "age", "==", 0 )
                .withAction( "a", "approved", DataType.TYPE_BOOLEAN, new DTCellValue52() {
                    {
                        setBooleanValue( true );
                    }
                } )
                .withAction( "a", "approved", DataType.TYPE_BOOLEAN, new DTCellValue52() {
                    {
                        setBooleanValue( false );
                    }
                } )
                .withData( new Object[][]{
                        { 1, "description", true, true, false },
                        { 2, "description", true, false, true } } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = getAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );

        assertContains( "ConflictingRows", analysisReport, 2 );
        assertContains( "ConflictingRows", analysisReport, 1 );

    }

    private DecisionTableAnalyzer getAnalyser( final GuidedDecisionTable52 table52 ) {
        return new DecisionTableAnalyzerMock( oracle,
                                              table52,
                                              result -> analysisReport = result );
    }
}