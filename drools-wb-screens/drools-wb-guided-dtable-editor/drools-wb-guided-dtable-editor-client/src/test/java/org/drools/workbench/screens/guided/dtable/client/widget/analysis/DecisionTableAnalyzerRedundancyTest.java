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
public class DecisionTableAnalyzerRedundancyTest {

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

        when( oracle.getFieldType( "LoanApplication", "amount" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "LoanApplication", "lengthYears" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "LoanApplication", "deposit" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "LoanApplication", "approved" ) ).thenReturn( DataType.TYPE_BOOLEAN );
        when( oracle.getFieldType( "LoanApplication", "insuranceCost" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "LoanApplication", "approvedRate" ) ).thenReturn( DataType.TYPE_NUMERIC_INTEGER );
        when( oracle.getFieldType( "IncomeSource", "type" ) ).thenReturn( DataType.TYPE_STRING );
        when( oracle.getFieldType( "Person", "name" ) ).thenReturn( DataType.TYPE_STRING );

    }

    @Test
    public void testNoIssues() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "application", "LoanApplication", "amount", ">" )
                .withConditionIntegerColumn( "application", "LoanApplication", "amount", "<=" )
                .withConditionIntegerColumn( "application", "LoanApplication", "lengthYears", "==" )
                .withConditionIntegerColumn( "application", "LoanApplication", "deposit", "<" )
                .withStringColumn( "income", "IncomeSource", "type", "==" )
                .withActionSetField( "application", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "application", "insuranceCost", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "application", "approvedRate", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2 },
                        { 2, "description", 10000, 100000, 20, 2000, "Job", true, 0, 4 },
                        { 3, "description", 100001, 130000, 20, 3000, "Job", true, 10, 6 },
                        { 4, "description", null, null, null, null, null, null, null, null },
                        { 5, "description", null, null, null, null, null, null, null, null } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "ThisRowIsRedundantTo(1)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(2)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(3)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(4)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(5)", analysisReport );

    }

    @Test
    public void testNoIssues2() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "application", "LoanApplication", "amount", ">" )
                .withConditionIntegerColumn( "application", "LoanApplication", "amount", "<=" )
                .withConditionIntegerColumn( "application", "LoanApplication", "lengthYears", "==" )
                .withConditionIntegerColumn( "application", "LoanApplication", "deposit", "<" )
                .withStringColumn( "income", "IncomeSource", "type", "==" )
                .withActionSetField( "application", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "application", "insuranceCost", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "application", "approvedRate", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2 },
                        { 2, "description", 1000, 200000, 30, 20000, "Asset", true, 0, 2 },
                        { 3, "description", 100001, 130000, 20, 3000, "Job", true, 10, 6 } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "ThisRowIsRedundantTo(1)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(2)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(3)", analysisReport );

    }

    @Test
    public void testRedundantRows001() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withNumericColumn( "application", "LoanApplication", "amount", ">" )
                .withNumericColumn( "application", "LoanApplication", "amount", "<=" )
                .withNumericColumn( "application", "LoanApplication", "lengthYears", "==" )
                .withNumericColumn( "application", "LoanApplication", "deposit", "<" )
                .withStringColumn( "income", "IncomeSource", "type", "==" )
                .withActionSetField( "application", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "application", "insuranceCost", DataType.TYPE_NUMERIC )
                .withActionSetField( "application", "approvedRate", DataType.TYPE_NUMERIC )
                .withData( new Object[][]{
                        { 1, "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2 },
                        { 2, "description", 131000, 200000, 30, 20000, "Asset", true, 0, 2 },
                        { 3, "description", 100001, 130000, 20, 3000, "Job", true, 10, 6 } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

    }

    @Test
    public void testRedundantRows002() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withStringColumn( "application", "LoanApplication", "amount", ">" )
                .withStringColumn( "person", "Person", "name", "==" )
                .withStringColumn( "income", "IncomeSource", "type", "==" )
                .withActionSetField( "application", "approved", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", "131000", "Toni", "Asset", "true" },
                        { 2, "description", "131000", "Toni", "Asset", "true" },
                        { 3, "description", "100001", "Michael", "Job", "true" } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

    }

    @Test
    public void testRedundantRows003() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withStringColumn( "application", "LoanApplication", "amount", ">" )
                .withStringColumn( "person", "Person", "name", "==" )
                .withEnumColumn( "income", "IncomeSource", "type", "==", "Asset,Job" )
                .withActionSetField( "application", "approved", DataType.TYPE_STRING )
                .withData( new Object[][]{
                        { 1, "description", "131000", "Toni", "Asset", "true" },
                        { 2, "description", "131000", "Toni", "Asset", "true" },
                        { 3, "description", "100001", "Michael", "Job", "true" } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantRows", analysisReport, 1 );
        assertContains( "RedundantRows", analysisReport, 2 );

    }

    @Test
    public void testRedundantConditions001() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withEnumColumn( "a", "Person", "name", "==", "Toni,Eder" )
                .withConditionIntegerColumn( "a", "Person", "name", "==" )
                .withData( new Object[][]{ { 1, "description", "Toni", "Toni" } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "RedundantConditions", analysisReport );

    }

    @Test
    public void testRedundantRowsWithConflict() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "d", "Account", "deposit", "<" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        { 1, "description", 100, 0, true, true },
                        { 2, "description", 100, 0, true, false } } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertDoesNotContain( "ThisRowIsRedundantTo(1)", analysisReport );
        assertDoesNotContain( "ThisRowIsRedundantTo(2)", analysisReport );

    }

    @Test
    public void testRedundantActionsInOneRow001() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "name", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", "Toni", 100, 100 },
                        { 2, "description", "Eder", 200, null },
                        { 3, "description", "Michael", null, 300 },
                        { 4, "description", null, null, null, null, null }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ValueForFactFieldIsSetTwice(a, salary)", analysisReport );

    }

    @Test
    public void testRedundantActionsInOneRow002() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "name", "==" )
                .withActionInsertFact( "Person", "b", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withActionSetField( "b", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", "Toni", 100, 100 },
                        { 2, "description", "Eder", 200, null },
                        { 3, "description", "Michael", null, 300 },
                        { 4, "description", null, null, null, null, null }
                } )
                .build();

        DecisionTableAnalyzer analyzer = getDecisionTableAnalyzer( table52 );

        analyzer.onValidate( new ValidateEvent( new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>() ) );

        assertContains( "ValueForFactFieldIsSetTwice(b, salary)", analysisReport );

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