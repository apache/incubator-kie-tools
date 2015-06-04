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
import org.mockito.Mock;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerConflictTest {

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

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "lastName" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Account", "deposit" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );

        eventBus = new EventBusMock();
    }

    @Test
    public void testNoIssue() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "d", "Account", "deposit", "<" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{ { 1, "description", 100, 0, true } } )
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertEmpty( eventBus.getUpdateColumnDataEvent().getColumnData() );

    }

    @Test
    public void testImpossibleMatch() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "a", "Person", "age", "<" )
                .withData( new Object[][]{ { 1, "description", 100, 0 } } )
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertContains( "ImpossibleMatchOn(age)", eventBus.getUpdateColumnDataEvent().getColumnData() );

    }

    @Test
    public void testConflict() throws Exception {

        GuidedDecisionTable52 table52 = new LimitedGuidedDecisionTableBuilder( "org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable" )
                .withIntegerColumn( "a", "Person", "age", "==", 0 )
                .withAction( "a", "approved", DataType.TYPE_BOOLEAN, new DTCellValue52() {
                    {
                        setBooleanValue( true );
                    }
                } ).withAction( "a", "approved", DataType.TYPE_BOOLEAN, new DTCellValue52() {
                    {
                        setBooleanValue( false );
                    }
                } )
                .withData( new Object[][]{
                        {1, "description", true, true, false},
                        { 2, "description", true, false, true } } )
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        List<CellValue<? extends Comparable<?>>> result = eventBus.getUpdateColumnDataEvent().getColumnData();
        assertContains( "ConflictingMatchWithRow(2)", result );
        assertContains( "ConflictingMatchWithRow(1)", result );

    }

    @Test
    public void testConflictIgnoreEmptyRows() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        {1, "description", null, ""},
                        {2, "description", null, "true"}} )
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        List<CellValue<? extends Comparable<?>>> result = eventBus.getUpdateColumnDataEvent().getColumnData();
        assertDoesNotContain( "ConflictingMatchWithRow(1)", result );
        assertDoesNotContain( "ConflictingMatchWithRow(2)", result );

    }

    @Test
    public void testConflictWithASubsumingRow() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "description", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        {1, "description", 10, null, null, 100, "ok"},
                        {2, "description", null, "Toni", null, 200, "ok"},
                        {3, "description", 12, "Toni", "Rikkola", 300, "ok"},
                        {4, "description", null, null, null, null, null}
                } )
                .build();

        DecisionTableAnalyzer analyzer = new DecisionTableAnalyzer( oracle,
                                                                    table52,
                                                                    eventBus );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ConflictingMatchWithRow(3)", eventBus.getUpdateColumnDataEvent().getColumnData(), 1 );

    }
}