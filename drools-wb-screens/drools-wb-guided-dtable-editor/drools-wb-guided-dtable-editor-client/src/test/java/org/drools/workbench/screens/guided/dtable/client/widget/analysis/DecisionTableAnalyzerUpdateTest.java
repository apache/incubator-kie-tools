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
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.ExtendedGuidedDecisionTableBuilder.*;
import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerUpdateTest {

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
    public void testRowValueChange() throws Exception {

        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, true )
                                                                                 .row( 1, true )
                                                                                 .end() )
                                                              .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 2 );

        table52.getData().get( 1 ).get( 2 ).setNumericValue( 0 );

        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 1, 2 ) );
        analyzer.analyze( updates );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );
    }

    @Test
    public void testRemoveRow() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, 1, true )
                                                                                 .row( 0, 1, true )
                                                                                 .row( 2, 2, true )
                                                                                 .row( 1, 1, false )
                                                                                 .end() )
                                                              .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertContains( "ConflictingRows", analyzerProvider.getAnalysisReport(), 4 );
        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 2 );

        // REMOVE 2
        table52.getData().remove( 1 );

        final DeleteRowEvent event = new DeleteRowEvent( 1 );
        analyzer.deleteRow( event.getIndex() );
        analyzer.updateColumns( table52.getData().size() );

        assertContains( "ConflictingRows", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 3 );

        // BREAK LINE NUMBER 2 ( previously line number 3 )
        table52.getData().get( 1 ).get( 3 ).setNumericValue( 1 ); // Change the value of person.age ==

        analyzer.updateColumns( table52.getData().size() );

        analyzer.analyze( getUpdates( 1, 3 ) );

        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void testRemoveRow2() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( ">" )
                                                              .withPersonAgeColumn( "<" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, 10, true )
                                                                                 .row( 1, 10, true )
                                                                                 .end() )
                                                              .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 2 );

        // REMOVE 2
        table52.getData().remove( 0 );

        final DeleteRowEvent event = new DeleteRowEvent( 0 );
        analyzer.deleteRow( event.getIndex() );
        analyzer.updateColumns( table52.getData().size() );

        assertDoesNotContain( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testRemoveColumn() throws Exception {

        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, true )
                                                                                 .row( 2, true )
                                                                                 .row( 3, true )
                                                                                 .end() )
                                                              .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );

        // REMOVE COLUMN
        table52.getActionCols().remove( 0 );
        table52.getData().get( 0 ).remove( 3 );
        table52.getData().get( 1 ).remove( 3 );
        table52.getData().get( 2 ).remove( 3 );

        analyzer.deleteColumns( 3, 1 );

        assertContains( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testAddColumn() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider
                .makeAnalyser()
                .withPersonAgeColumn( "==" )
                .withPersonApprovedActionSetField()
                .withData( DataBuilderProvider
                                   .row( 1, true )
                                   .row( 2, true )
                                   .row( 3, true )
                                   .end() )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );

        // ADD COLUMN
        table52.getActionCols().add( createActionSetField( "a", "approved", DataType.TYPE_BOOLEAN ) );
        table52.getData().get( 0 ).add( new DTCellValue52( true ) );
        table52.getData().get( 1 ).add( new DTCellValue52( true ) );
        table52.getData().get( 2 ).add( new DTCellValue52( false ) );

        analyzer.insertColumn( 4 );

        assertContains( "MultipleValuesForOneAction", analyzerProvider.getAnalysisReport(), 3 );

    }

    @Test
    public void testInsertRow() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, 1, true )
                                                                                 .row( 0, 1, true )
                                                                                 .row( 2, 2, true )
                                                                                 .end() )
                                                              .buildTable();


        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        table52.getData().add( 0, new ArrayList<DTCellValue52>() );
        final InsertRowEvent event = new InsertRowEvent( 0 );
        analyzer.insertRow( event.getIndex() );
        analyzer.updateColumns( table52.getData().size() );

        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 3 );

    }

    @Test
    public void testAppendRow() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonAgeColumn( "==" )
                                                              .withPersonApprovedActionSetField()
                                                              .withData( DataBuilderProvider
                                                                                 .row( 1, 1, true )
                                                                                 .row( 0, 1, true )
                                                                                 .row( 2, 2, true )
                                                                                 .end() )
                                                              .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 2 );

        table52.getData().add( new ArrayList<>() );
        analyzer.appendRow();
        analyzer.updateColumns( table52.getData().size() );

        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport(), 2 );
    }

    private ArrayList<CellValue<? extends Comparable<?>>> getMockColumnData( int size ) {
        ArrayList<CellValue<? extends Comparable<?>>> list = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( int i = 0; i < size; i++ ) {
            list.add( mock( CellValue.class ) );
        }
        return list;
    }

    private ArrayList<Coordinate> getUpdates( final int x,
                                              final int y ) {
        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( x, y ) );
        return updates;
    }

}