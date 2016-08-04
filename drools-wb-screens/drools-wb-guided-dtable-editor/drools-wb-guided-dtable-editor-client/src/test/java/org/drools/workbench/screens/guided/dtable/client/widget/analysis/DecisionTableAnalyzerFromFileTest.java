/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.AnalyzerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.testutil.TestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerFromFileTest {

    @GwtMock
    AnalysisConstants analysisConstants;

    @GwtMock
    DateTimeFormat dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    @Test
    public void testFile1() throws Exception {
        String xml = loadResource( "Pricing loans.gdst" );

        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal( xml );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "MissingRangeTitle" );
    }

    @Test
    public void testFile2() throws Exception {
        String xml = loadResource( "Large file.gdst" );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( GuidedDTXMLPersistence.getInstance().unmarshal( xml ) );

        analyzer.analyze( Collections.emptyList() );

        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "SingleHitLost" );
    }

    @Test
    public void testFile3() throws Exception {
        String xml = loadResource( "Pricing loans version 2.gdst" );

        GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal( xml );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertDoesNotContain( "ThisRowIsRedundantTo", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testFile4() throws Exception {

        when( analyzerProvider.getOracle().getFieldType( "Player", "score" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );

        String xml = loadResource( "Score Achievements.gdst" );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( GuidedDTXMLPersistence.getInstance().unmarshal( xml ) );

        analyzer.analyze( Collections.emptyList() );

        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "MissingRangeTitle",
                            "SingleHitLost" );
    }

    @Test
    public void testFile5() throws Exception {
        String xml = loadResource( "Base entitlement.gdst" );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( GuidedDTXMLPersistence.getInstance().unmarshal( xml ) );

        analyzer.analyze( Collections.emptyList() );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );
    }

    @Test
    public void testFile2WithUpdate() throws Exception {
        long baseline = System.currentTimeMillis();
        String xml = loadResource( "Large file.gdst" );
        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        long now = System.currentTimeMillis();
        System.out.println( "Loading of model took.. " + ( now - baseline ) + " ms" );
        baseline = now;

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        now = System.currentTimeMillis();
        System.out.println( "Indexing took.. " + (now - baseline) + " ms" );

        analyzer.analyze( Collections.emptyList() );
        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "SingleHitLost" );
        now = System.currentTimeMillis();
        System.out.println( "Initial analysis took.. " + ( now - baseline ) + " ms" );
        baseline = now;

        table52.getData().get( 2 ).get( 6 ).clearValues();
        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 2,
                                     6 ) );
        analyzer.analyze( updates );
        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "SingleHitLost" );
        now = System.currentTimeMillis();
        System.out.println( "Partial analysis took.. " + ( now - baseline ) + " ms" );
    }

    @Test
    public void testFile2WithDeletes() throws Exception {
        String xml = loadResource( "Large file.gdst" );
        final GuidedDecisionTable52 table52 = GuidedDTXMLPersistence.getInstance().unmarshal( xml );

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            "SingleHitLost" );
        long baseline = System.currentTimeMillis();

        for ( int iterations = 0; iterations < 10; iterations++ ) {
            final DeleteRowEvent event = new DeleteRowEvent( 100 );
            analyzer.deleteRow( event.getIndex() );
            table52.getData().remove( 100 );
            analyzer.updateColumns( 0 );
            long now = System.currentTimeMillis();
            System.out.println( "Partial analysis took.. " + (now - baseline) + " ms" );
            baseline = now;
            assertOnlyContains( analyzerProvider.getAnalysisReport(),
                                "SingleHitLost" );
        }
    }

}