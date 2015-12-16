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
import org.mockito.Mock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerTest {

    @GwtMock
    AnalysisConstants analysisConstants;
    @GwtMock
    DateTimeFormat dateTimeFormat;

    @Mock
    AsyncPackageDataModelOracle oracle;

    private AnalysisReport analysisReport;

    @Before
    public void setUp() throws Exception {

        when( oracle.getFieldType( "Person", "age" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "Person", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    @Test
    public void testEmpty() throws Exception {
        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( new GuidedDecisionTable52() );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertTrue( analysisReport.getAnalysisData().isEmpty() );

    }

    @Test
    public void testEmptyRow() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", "" } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertTrue( analysisReport.getAnalysisData().isEmpty() );

    }

    @Test
    public void testRuleHasNoAction() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", 0 } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertContains( "RuleHasNoAction", analysisReport );

    }

    @Test
    public void testRuleHasNoActionSet() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionSetField( "a", "age", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "a", "name", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", 0, null, null, "" },
                        { 2, "description", null, null, null, null }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertContains( "RuleHasNoAction", analysisReport, 1 );
        assertDoesNotContain( "RuleHasNoAction", analysisReport, 2 );

    }

    @Test
    public void testRuleHasNoRestrictions() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{ { 1, "description", true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analysisReport );

    }

    @Test
    public void testRuleHasNoRestrictionsSet() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", null, "", true },
                        { 2, "description", null, null, null }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analysisReport, 1 );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analysisReport, 2 );

    }

    @Test
    public void testMultipleValuesForOneAction() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{ { 1, "description", 100, true, false } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onFocus();

        assertContains( "MultipleValuesForOneAction", analysisReport );

    }

    @Test
    public void testRedundancy() throws Exception {
        GuidedDecisionTable52 table52 = new LimitedGuidedDecisionTableBuilder( "org.test",
                                                                               new ArrayList<Import>(),
                                                                               "mytable" )
                .withIntegerColumn( "a", "Person", "age", "==", 0 )
                .withAction( "a", "Person", "approved", new DTCellValue52() {
                    {
                        setBooleanValue( true );
                    }
                } ).withAction( "a", "Person", "approved", new DTCellValue52() {
                    {
                        setBooleanValue( true );
                    }
                } )
                .withData( new Object[][]{
                        { 1, "description", true, true, false },
                        { 2, "description", true, false, true } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

    }

    @Test
    public void testOnFocus() throws Exception {
        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( new GuidedDecisionTable52() );
        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        analysisReport = null;

        analyzer.onFocus();

        assertNotNull( analysisReport );
    }

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