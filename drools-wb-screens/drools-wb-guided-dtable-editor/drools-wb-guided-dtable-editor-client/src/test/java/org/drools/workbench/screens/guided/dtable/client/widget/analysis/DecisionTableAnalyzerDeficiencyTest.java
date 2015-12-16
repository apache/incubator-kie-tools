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
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerDeficiencyTest {

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
        when( oracle.getFieldType( "Person", "salary" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "description" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "lastName" ) ).thenReturn( DataType.TYPE_STRING );

        Map<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
    }

    @Test
    public void testRuleIsDeficient001() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", null, "Eder", null, 100 },
                        { 2, "description", 10, null, null, 200 },
                        { 3, "description", null, "Toni", "Rikkola", 300 },
                        { 4, "description", null, null, null, null }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );
        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertContains( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

    }

    @Test
    public void testRuleIsDeficient002() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "description", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", 10, "", "", 100, "ok" },
                        { 2, "description", null, "", "", 200, "ok" },
                        { 3, "description", 12, "", "Rikkola", 300, "ok" },
                        { 4, "description", null, "", "", null, "" }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertDoesNotContain( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

        table52.getData().get( 1 ).get( 3 ).setStringValue( "Toni" );

        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( 1, 3 ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        analyzer.onValidate( new ValidateEvent( updates ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertContains( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

    }

    @Test
    public void testRuleIsDeficient003() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "description", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", 10, "", "", 100, "ok" },
                        { 2, "description", null, "Eder", "", 200, "ok" },
                        { 3, "description", 12, "", "Rikkola", 300, "ok" },
                        { 4, "description", null, "", "", null, "" }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertContains( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

        table52.getData().get( 2 ).get( 3 ).setStringValue( "Toni" );

        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( 2, 3 ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        analyzer.onValidate( new ValidateEvent( updates ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertDoesNotContain( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

    }

    @Test
    public void testRuleIsDeficient004() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "description", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", 10, "", "", 100, "ok" },
                        { 2, "description", null, "Eder", "", 200, "ok" },
                        { 3, "description", 12, "Toni", "Rikkola", 300, "ok" },
                        { 4, "description", null, "", "", null, "" }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertDoesNotContain( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

        table52.getData().get( 2 ).get( 3 ).setStringValue( "" );

        HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        updates.put( new Coordinate( 2, 3 ), new ArrayList<List<CellValue<? extends Comparable<?>>>>() );
        analyzer.onValidate( new ValidateEvent( updates ) );

        assertDoesNotContain( "DeficientRow", analysisReport, 1 );
        assertContains( "DeficientRow", analysisReport, 2 );
        assertDoesNotContain( "DeficientRow", analysisReport, 3 );
        assertDoesNotContain( "DeficientRow", analysisReport, 4 );

    }

    private DecisionTableAnalyzer getDecisionTableAnalyzer( GuidedDecisionTable52 table52 ) {
        return new DecisionTableAnalyzer( mock( PlaceRequest.class ),
                                          oracle,
                                          table52,
                                          mock( EventBus.class ) ) {
            @Override
            protected void sendReport( AnalysisReport report ) {
                DecisionTableAnalyzerDeficiencyTest.this.analysisReport = report;
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