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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.services.verifier.core.main.Analyzer;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.verifier.webworker.client.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.verifier.webworker.client.testutil.TestUtil.*;

@RunWith(GwtMockitoTestRunner.class)
@Ignore("Just for profiling")
public class SpeedTest {

    @GwtMock
    AnalysisConstants analysisConstants;
    @GwtMock
    DateTimeFormat dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws
                        Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    @Test
    public void subsumptionTable() throws
                                   Exception,
                                   UpdateException {
        long baseline = System.currentTimeMillis();

        final DataBuilderProvider.DataBuilder builder = DataBuilderProvider
                .row( true,
                      null,
                      true );
        for ( int i = 0; i < 1000; i++ ) {

            builder
                    .row( null,
                          false,
                          true );
        }

        final Object[][] end = builder.end();

        long now = System.currentTimeMillis();
        System.out.println( "Loading of model took.. " + ( now - baseline ) + " ms" );
        baseline = now;

        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                .withPersonApprovedColumn( "==" )
                .withPersonApprovedColumn( "!=" )
                .withPersonApprovedActionSetField()
                .withData( end )
                .buildTable();

        now = System.currentTimeMillis();
        System.out.println( "Made table.. " + ( now - baseline ) + " ms" );
        baseline = now;

        final Analyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        now = System.currentTimeMillis();
        System.out.println( "Indexing.. " + ( now - baseline ) + " ms" );
        baseline = now;

        analyzer.analyze();

        now = System.currentTimeMillis();
        System.out.println( "Validated.. " + ( now - baseline ) + " ms" );
        baseline = now;

        table52.getData()
                .get( 1 )
                .get( 2 )
                .setBooleanValue( false );

        List<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 1,
                                     2 ) );
        analyzerProvider.getUpdateManager( table52,
                                           analyzer )
                .update( table52,
                         updates );

        now = System.currentTimeMillis();
        System.out.println( "Update.. " + ( now - baseline ) + " ms" );


        assertContains( "RedundantRows",
                        analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void noConflictTable() throws
                                  Exception,
                                  UpdateException {
        long baseline = System.currentTimeMillis();

        final DataBuilderProvider.DataBuilder builder = DataBuilderProvider
                .row( -1,
                      true );
        for ( int i = 0; i < 1000; i++ ) {
            builder
                    .row( i,
                          true );
        }

        final Object[][] end = builder.end();

        long now = System.currentTimeMillis();
        System.out.println( "Loading of model took.. " + ( now - baseline ) + " ms" );
        baseline = now;

        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                .withPersonAgeColumn( "==" )
                .withPersonApprovedActionSetField()
                .withData( end )
                .buildTable();

        now = System.currentTimeMillis();
        System.out.println( "Made table.. " + ( now - baseline ) + " ms" );
        baseline = now;

        final Analyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        now = System.currentTimeMillis();
        System.out.println( "Indexing.. " + ( now - baseline ) + " ms" );
        baseline = now;

        analyzer.analyze();

        now = System.currentTimeMillis();
        System.out.println( "Validated.. " + ( now - baseline ) + " ms" );
        baseline = now;

        table52.getData()
                .get( 1 )
                .get( 2 )
                .setNumericValue( 3 );

        List<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 1,
                                     2 ) );
        analyzerProvider.getUpdateManager( table52,
                                           analyzer )
                .update( table52,
                         updates );

        now = System.currentTimeMillis();
        System.out.println( "Update.. " + ( now - baseline ) + " ms" );


        assertContains( "RedundantRows",
                        analyzerProvider.getAnalysisReport() );
    }


}