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
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerDeficiencyTest {

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
    public void testRuleIsNotDeficient() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        { 1, "description", null, "Eder", null, 100 },
                        {2, "description", 10, null, null, 100},
                        {3, "description", null, "Toni", "Rikkola", 100},
                        { 4, "description", null, null, null, null }
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testRuleIsDeficient001() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withStringColumn( "a", "Person", "name", "==" )
                .withStringColumn( "a", "Person", "lastName", "==" )
                .withActionSetField( "a", "salary", DataType.TYPE_NUMERIC_INTEGER )
                .withData( new Object[][]{
                        {1, "description", null, "Eder", null, 100},
                        {2, "description", 10, null, null, 200},
                        {3, "description", null, "Toni", "Rikkola", 300},
                        {4, "description", null, null, null, null}
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

        table52.getData().get( 1 ).get( 3 ).setStringValue( "Toni" );

        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 1, 3 ) );
        analyzer.analyze( updates );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

        table52.getData().get( 2 ).get( 3 ).setStringValue( "Toni" );

        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 2, 3 ) );
        analyzer.analyze( updates );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

        table52.getData().get( 2 ).get( 3 ).setStringValue( "" );

        ArrayList<Coordinate> updates = new ArrayList<>();
        updates.add( new Coordinate( 2, 3 ) );
        analyzer.analyze( updates );

        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "DeficientRow", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 3 );
        assertDoesNotContain( "DeficientRow", analyzerProvider.getAnalysisReport(), 4 );

    }

}