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
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerTest {

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
    public void testEmpty() throws Exception {
        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( new GuidedDecisionTable52() );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );

    }

    @Test
    public void testEmptyRow() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", "" } } )
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );

        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );

    }

    @Test
    public void testRuleHasNoAction() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", 0 } } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testRuleHasNoActionSet() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
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
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RuleHasNoAction", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoAction", analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void testRuleHasNoRestrictions() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<>(),
                                                                                      "mytable" )
                .withActionInsertFact( "Person", "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{{1, "description", true}} )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport() );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 2 );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onFocus();

        assertContains( "MultipleValuesForOneAction", analyzerProvider.getAnalysisReport() );

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
                .buildTable();

        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );

        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 2 );

        assertNotNull( analyzerProvider.getStatus() );
    }

    @Test
    public void testUpdateStatus() throws Exception {
        final DecisionTableAnalyzer decisionTableAnalyzer = analyzerProvider.makeAnalyser().buildAnalyzer();

        final Status parameter = new Status( 1, 2, 3 );
        decisionTableAnalyzer.getOnStatusCommand().execute( parameter );

        assertEquals( parameter, analyzerProvider.getStatus() );
    }

    @Test
    public void testOnFocus() throws Exception {
        DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( new GuidedDecisionTable52() );
        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );

        analyzerProvider.clearAnalysisReport();

        analyzer.onFocus();

        assertNotNull( analyzerProvider.getAnalysisReport() );
    }

    // GUVNOR-2546: Verification & Validation: BRL fragments are ignored
    @Test
    public void testRuleHasNoActionBRLFragmentHasAction() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionBRLFragment()
                .withData( new Object[][]{{1, "description", 0, true}} )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertDoesNotContain( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );
    }

    // GUVNOR-2546: Verification & Validation: BRL fragments are ignored
    @Test
    public void testConditionsShouldNotBeIgnored() throws Exception {
        GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                new ArrayList<Import>(),
                                                                                "mytable" )
                .withConditionBRLColumn()
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", null, true},
                        {2, "description", null, null}
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 2 );

    }
}