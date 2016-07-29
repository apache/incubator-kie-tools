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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.UpdateManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

public class AnalyzerProvider {

    private final AsyncPackageDataModelOracle oracle;
    private       AnalysisReport              analysisReport;
    private       Status                      status;
    private       RuleInspectorCache          cache;

    public AnalyzerProvider() {
        this( mock( AsyncPackageDataModelOracle.class ) );
    }

    public AnalyzerProvider( final AsyncPackageDataModelOracle oracle ) {
        this.oracle = oracle;

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "lastName" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "description" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Account", "deposit" ) ).thenReturn( DataType.TYPE_NUMERIC_DOUBLE );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );
        when( oracle.getFieldType( "Person", "salary" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );

        final Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    public AsyncPackageDataModelOracle getOracle() {
        return oracle;
    }

    public AnalysisReport getAnalysisReport() {
        return analysisReport;
    }

    public Status getStatus() {
        return status;
    }

    public DecisionTableAnalyzer getAnalyser( final GuidedDecisionTable52 table52 ) {

        final DecisionTableAnalyzerBuilder builder = getDecisionTableAnalyzerBuilder()
                .withPlaceRequest( mock( PlaceRequest.class ) )
                .withReportScreen( mock( AnalysisReportScreen.class ) )
                .withOracle( oracle )
                .withModel( table52 );


        return builder.build();
    }

    public RuleInspectorCache getCache( final GuidedDecisionTable52 table52 ) {
        return getDecisionTableAnalyzerBuilder()
                .withModel( table52 )
                .withOracle( oracle )
                .getCacheBuilder()
                .buildCache();
    }

    public UpdateManager getUpdateManager( final CheckRunner checkRunner,
                                           final GuidedDecisionTable52 table52 ) {
        return getDecisionTableAnalyzerBuilder()
                .withModel( table52 )
                .withOracle( oracle )
                .getUpdateManagerBuilder( checkRunner )
                .buildUpdateManager();
    }

    private DecisionTableAnalyzerBuilder getDecisionTableAnalyzerBuilder() {
        return new DecisionTableAnalyzerBuilder() {
            @Override
            protected InnerBuilder getInnerBuilder() {
                return new InnerBuilder( getCheckRunner() ) {
                    @Override
                    protected AnalysisReporter getAnalysisReporter() {
                        return AnalyzerProvider.this.getAnalysisReporter( placeRequest,
                                                                          analysisReportScreen );
                    }
                };
            }

        };
    }

    private AnalysisReporter getAnalysisReporter( final PlaceRequest placeRequest,
                                                  final AnalysisReportScreen analysisReportScreen ) {
        return new AnalysisReporter( placeRequest,
                                     analysisReportScreen ) {
            @Override
            public void sendReport( final AnalysisReport report ) {
                analysisReport = report;
            }

            @Override
            public void sendStatus( final Status _status ) {
                status = _status;
            }
        };
    }

    private CheckRunner getCheckRunner() {
        return new CheckRunner() {
            @Override
            protected void doRun( final CancellableRepeatingCommand command ) {
                while ( command.execute() ) {
                    //loop
                }
            }
        };
    }

    public AnalyzerBuilder makeAnalyser() {
        return new AnalyzerBuilder( this );
    }

    public DecisionTableAnalyzer makeAnalyser( final GuidedDecisionTable52 table52 ) {
        return getAnalyser( table52 );
    }

    public void clearAnalysisReport() {
        analysisReport = null;
    }
}