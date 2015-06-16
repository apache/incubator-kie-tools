/*
 * Copyright 2015 JBoss Inc
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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
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

    EventBusMock eventBus;

    @Before
    public void setUp() throws Exception {
        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );

        oracle = mock( AsyncPackageDataModelOracle.class );

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

        eventBus = new EventBusMock();
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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ThisRowIsRedundantTo(1)", eventBus.getUpdateColumnDataEvent().getColumnData() );
        assertContains( "ThisRowIsRedundantTo(2)", eventBus.getUpdateColumnDataEvent().getColumnData() );

        table52.getData().get( 1 ).get( 2 ).setNumericValue( 0 );

        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( 1, 2 ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        analyzer.onValidate( new ValidateEvent( updates ) );

        assertColumnValuesAreEmpty( eventBus.getUpdateColumnDataEvent().getColumnData() );
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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ConflictingMatchWithRow(4)", eventBus.getUpdateColumnDataEvent().getColumnData() );
        assertContains( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

        // REMOVE 2
        table52.getData().remove( 1 );

        analyzer.onDeleteRow( new DeleteRowEvent( 1 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertContains( "ConflictingMatchWithRow(3)", eventBus.getUpdateColumnDataEvent().getColumnData() );
        assertDoesNotContain( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

        // BREAK LINE NUMBER 2 ( previously line number 3 )
        table52.getData().get( 1 ).get( 3 ).setNumericValue( 1 ); // Change the value of person.age ==

        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        analyzer.onValidate( new ValidateEvent( getUpdates( 1, 3 ) ) );

        assertContains( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ThisRowIsRedundantTo(2)", eventBus.getUpdateColumnDataEvent().getColumnData(), 0 );
        assertContains( "ThisRowIsRedundantTo(1)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

        // REMOVE 2
        table52.getData().remove( 0 );

        analyzer.onDeleteRow( new DeleteRowEvent( 0 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertEmpty( eventBus.getUpdateColumnDataEvent().getColumnData() );
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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertColumnValuesAreEmpty( eventBus.getUpdateColumnDataEvent().getColumnData() );

        // REMOVE COLUMN
        table52.getActionCols().remove( 0 );
        table52.getData().get( 0 ).remove( 3 );
        table52.getData().get( 1 ).remove( 3 );
        table52.getData().get( 2 ).remove( 3 );

        analyzer.onAfterDeletedColumn( new AfterColumnDeleted() );

        assertContains( "RuleHasNoAction", eventBus.getUpdateColumnDataEvent().getColumnData() );

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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertColumnValuesAreEmpty( eventBus.getUpdateColumnDataEvent().getColumnData() );

        // ADD COLUMN
        table52.getActionCols().add( createActionSetField( "a", "approved", DataType.TYPE_BOOLEAN ) );
        table52.getData().get( 0 ).add( new DTCellValue52( true ) );
        table52.getData().get( 1 ).add( new DTCellValue52( true ) );
        table52.getData().get( 2 ).add( new DTCellValue52( false ) );

        analyzer.onAfterColumnInserted( new AfterColumnInserted() );

        assertContains( "MultipleValuesForOneAction", eventBus.getUpdateColumnDataEvent().getColumnData(), 2 );

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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        table52.getData().add( 0, new ArrayList<DTCellValue52>() );
        analyzer.onInsertRow( new InsertRowEvent( 0 ) );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertEquals( 4, eventBus.getUpdateColumnDataEvent().getColumnData().size() );

        assertContains( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData(), 2 );

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

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

        table52.getData().add( new ArrayList<DTCellValue52>() );
        analyzer.onAppendRow( new AppendRowEvent() );
        analyzer.onUpdateColumnData( new UpdateColumnDataEvent( 0,
                                                                getMockColumnData( table52.getData().size() ) ) );

        assertEquals( 4, eventBus.getUpdateColumnDataEvent().getColumnData().size() );

    }

    // TODO: test ignore changes when UpdateColumnDataEvent size has not changed.
    // TODO: Remove add row/column
    // TODO: Move Column

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

    private void assertColumnValuesAreEmpty( List<CellValue<? extends Comparable<?>>> columnData ) {
        String foundError = null;

        for ( CellValue cellValue : columnData ) {
            Analysis analysis = (Analysis) cellValue.getValue();
            if ( analysis.getWarningsSize() != 0 ) {
                foundError = analysis.toHtmlString();
            }
        }

        assertNull( "Was not empty", foundError );
    }
}