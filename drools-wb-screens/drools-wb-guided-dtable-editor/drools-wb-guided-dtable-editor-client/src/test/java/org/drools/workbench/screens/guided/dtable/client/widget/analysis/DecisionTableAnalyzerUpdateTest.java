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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.Checks;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnDeleted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AfterColumnInserted;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.mockito.Mock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

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

    @Mock
    AsyncPackageDataModelOracle oracle;

    private AnalysisReport analysisReport;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        oracle = mock( AsyncPackageDataModelOracle.class );

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

    }

    @Test
    public void testRowValueChange() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, true },
                        { 2, "description", 1, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

        table52.getData().get( 1 ).get( 2 ).setNumericValue( 0 );

        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( 1, 2 ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        analyzer.onValidate( new ValidateEvent( updates ) );

        assertColumnValuesAreEmpty( analysisReport );
    }

    @Test
    public void testRemoveRow() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, 1, true },
                        { 2, "description", 0, 1, true },
                        { 3, "description", 2, 2, true },
                        { 4, "description", 1, 1, false } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ConflictingRows", analysisReport, 4 );
        assertContains( "ImpossibleMatch", analysisReport, 2 );

        // REMOVE 2
        table52.getData().remove( 1 );

        analyzer.onDeleteRow( new DeleteRowEvent( 1 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertContains( "ConflictingRows", analysisReport, 3 );
        assertDoesNotContain( "ImpossibleMatch", analysisReport, 3 );

        // BREAK LINE NUMBER 2 ( previously line number 3 )
        table52.getData().get( 1 ).get( 3 ).setNumericValue( 1 ); // Change the value of person.age ==

        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        analyzer.onValidate( new ValidateEvent( getUpdates( 1, 3 ) ) );

        assertContains( "ImpossibleMatch", analysisReport, 2 );

    }

    @Test
    public void testRemoveRow2() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "a", "Person", "age", "<" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, 10, true },
                        { 2, "description", 1, 10, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

        // REMOVE 2
        table52.getData().remove( 0 );

        analyzer.onDeleteRow( new DeleteRowEvent( 0 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertTrue( analysisReport.getAnalysisData().isEmpty() );
    }

    @Test
    public void testRemoveColumn() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, true },
                        { 2, "description", 2, true },
                        { 3, "description", 3, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertColumnValuesAreEmpty( analysisReport );

        // REMOVE COLUMN
        table52.getActionCols().remove( 0 );
        table52.getData().get( 0 ).remove( 3 );
        table52.getData().get( 1 ).remove( 3 );
        table52.getData().get( 2 ).remove( 3 );

        analyzer.onAfterDeletedColumn( new AfterColumnDeleted() );

        assertContains( "RuleHasNoAction", analysisReport );

    }

    @Test
    public void testAddColumn() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, true },
                        { 2, "description", 2, true },
                        { 3, "description", 3, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertColumnValuesAreEmpty( analysisReport );

        // ADD COLUMN
        table52.getActionCols().add( createActionSetField( "a", "approved", DataType.TYPE_BOOLEAN ) );
        table52.getData().get( 0 ).add( new DTCellValue52( true ) );
        table52.getData().get( 1 ).add( new DTCellValue52( true ) );
        table52.getData().get( 2 ).add( new DTCellValue52( false ) );

        analyzer.onAfterColumnInserted( new AfterColumnInserted() );

        assertContains( "MultipleValuesForOneAction", analysisReport, 3 );

    }

    @Test
    public void testInsertRow() throws Exception {

        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, 1, true },
                        { 2, "description", 0, 1, true },
                        { 3, "description", 2, 2, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        table52.getData().add( 0, new ArrayList<DTCellValue52>() );
        analyzer.onInsertRow( new InsertRowEvent( 0 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertContains( "ImpossibleMatch", analysisReport, 3 );

    }

    @Test
    public void testAppendRow() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 1, 1, true },
                        { 2, "description", 0, 1, true },
                        { 3, "description", 2, 2, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ImpossibleMatch", analysisReport, 2 );

        table52.getData().add( new ArrayList<DTCellValue52>() );
        analyzer.onAppendRow( new AppendRowEvent() );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertContains( "ImpossibleMatch", analysisReport, 2 );
    }

    private ArrayList<CellValue<? extends Comparable<?>>> getMockColumnData( int size ) {
        ArrayList<CellValue<? extends Comparable<?>>> list = new ArrayList<CellValue<? extends Comparable<?>>>();
        for ( int i = 0; i < size; i++ ) {
            list.add( mock( CellValue.class ) );
        }
        return list;
    }

    private HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> getUpdates( int x,
                                                                                            int y ) {
        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( x, y ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        return updates;
    }

    private void assertColumnValuesAreEmpty( AnalysisReport report ) {
        assertTrue( "Was not empty", report.getAnalysisData().isEmpty() );
    }

    // TODO: Move Column
    // TODO: Remove add row/column
    private DecisionTableAnalyzer getDecisionTableAnalyzer( GuidedDecisionTable52 table52 ) {
        return new DecisionTableAnalyzer( mock( PlaceRequest.class ),
                                          oracle,
                                          table52,
                                          mock( EventBus.class ) ) {
            @Override
            protected void sendReport( AnalysisReport report ) {
                analysisReport = report;
            }

            @Override
            protected Checks getChecks() {
                return new Checks() {
                    @Override
                    protected void doRun( final CancellableRepeatingCommand command ) {
                        while ( command.execute() ) {
                            //loop
                        }
                    }
                };
            }

            @Override
            protected ParameterizedCommand<Status> getOnStatusCommand() {
                return null;
            }

            @Override
            protected Command getOnCompletionCommand() {
                return new Command() {
                    @Override
                    public void execute() {
                        sendReport( makeAnalysisReport() );
                    }
                };
            }

        };
    }
}