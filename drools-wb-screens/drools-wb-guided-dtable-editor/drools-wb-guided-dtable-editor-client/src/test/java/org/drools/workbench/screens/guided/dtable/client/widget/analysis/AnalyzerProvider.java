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

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.*;

public class AnalyzerProvider {


    private final AsyncPackageDataModelOracle oracle;
    private       AnalysisReport              analysisReport;

    public AnalyzerProvider() {
        this.oracle = mock( AsyncPackageDataModelOracle.class );

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "lastName" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "description" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Account", "deposit" ) ).thenReturn( DataType.TYPE_NUMERIC_DOUBLE );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );
        when( oracle.getFieldType( "Person", "salary" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    public AnalysisReport getAnalysisReport() {
        return analysisReport;
    }

    public DecisionTableAnalyzer getAnalyser( final GuidedDecisionTable52 table52 ) {
        return new DecisionTableAnalyzerMock( oracle,
                                              table52,
                                              new Callback<AnalysisReport>() {
                                                  @Override
                                                  public void callback( final AnalysisReport result ) {
                                                      analysisReport = result;
                                                  }
                                              } );
    }

    public AnalyzerBuilder makeAnalyser() {
        return new AnalyzerBuilder( this );
    }

    public DecisionTableAnalyzer makeAnalyser( final GuidedDecisionTable52 table52 ) {
        return getAnalyser( table52 );
    }
}