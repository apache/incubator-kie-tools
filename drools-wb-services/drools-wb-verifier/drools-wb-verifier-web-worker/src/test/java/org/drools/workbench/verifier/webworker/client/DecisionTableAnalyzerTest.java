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

package org.drools.workbench.verifier.webworker.client;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.resources.i18n.AnalysisConstants;
import org.drools.workbench.verifier.webworker.client.testutil.ExtendedGuidedDecisionTableBuilder;
import org.drools.workbench.verifier.webworker.client.testutil.LimitedGuidedDecisionTableBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.verifier.webworker.client.testutil.TestUtil.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableAnalyzerTest
        extends AnalyzerUpdateTestBase {

    @Test
    public void testEmpty() throws Exception {
        table52 = new GuidedDecisionTable52();

        fireUpAnalyzer();

        assertTrue( analyzerProvider.getAnalysisReport().isEmpty() );

    }

    @Test
    public void testEmptyRow() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", "" } } )
                .buildTable();

        fireUpAnalyzer();

        assertContains( "EmptyRule", analyzerProvider.getAnalysisReport(), 1 );

    }

    @Test
    public void testMultipleEmptyRows() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                new ArrayList<Import>(),
                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", "==" )
                .withData( new Object[][]{ { 1, "description", "" },
                                           { 2, "description", "" }
                                         } )
                .buildTable();

        fireUpAnalyzer();

        assertEquals( 4, analyzerProvider.getAnalysisReport().size() );
        assertContains( AnalysisConstants.INSTANCE.EmptyRule(), analyzerProvider.getAnalysisReport(), 1 );
        assertContains( AnalysisConstants.INSTANCE.EmptyRule(), analyzerProvider.getAnalysisReport(), 2 );
        assertOnlyContains( analyzerProvider.getAnalysisReport(),
                            AnalysisConstants.INSTANCE.EmptyRule(),
                            AnalysisConstants.INSTANCE.SingleHitLost() );
    }

    @Test
    public void testRuleHasNoAction() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", 0 } } )
                .buildTable();

        fireUpAnalyzer();

        assertContains( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testMultipleRuleHasNoActions() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                new ArrayList<Import>(),
                "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withData( new Object[][]{ { 1, "description", 0 },
                                           { 2, "description", 1 } } )
                .buildTable();

        fireUpAnalyzer();

        assertContains( AnalysisConstants.INSTANCE.RuleHasNoAction(), analyzerProvider.getAnalysisReport(), 1 );
        assertContains( AnalysisConstants.INSTANCE.RuleHasNoAction(), analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void ruleHasNoActionShouldNotIgnoreRetract() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withRetract()
                .withData( DataBuilderProvider
                                   .row( 0, "a" )
                                   .end() )
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testRuleHasNoActionSet() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
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

        fireUpAnalyzer();

        assertContains( "RuleHasNoAction", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "EmptyRule", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "EmptyRule", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoAction", analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void testRuleHasNoRestrictions() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<>(),
                                                          "mytable" )
                .withActionInsertFact( "Person", "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{{1, "description", true}} )
                .buildTable();

        fireUpAnalyzer();

        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testRuleHasNoRestrictionsSet() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
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

        fireUpAnalyzer();

        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "EmptyRule", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "EmptyRule", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void testMultipleValuesForOneAction() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{ { 1, "description", 100, true, false } } )
                .buildTable();

        analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.start();

        assertContains( "MultipleValuesForOneAction", analyzerProvider.getAnalysisReport() );

    }

    @Test
    public void testRedundancy() throws Exception {
        table52 = new LimitedGuidedDecisionTableBuilder( "org.test",
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

        fireUpAnalyzer();

        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport(), 2 );

        assertNotNull( analyzerProvider.getStatus() );
    }

    // GUVNOR-2546: Verification & Validation: BRL fragments are ignored
    @Test
    public void testRuleHasNoActionBRLFragmentHasAction() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionBRLFragment()
                .withData( new Object[][]{{1, "description", 0, true}} )
                .buildTable();

        fireUpAnalyzer();

        assertDoesNotContain( "EmptyRule", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoAction", analyzerProvider.getAnalysisReport() );
    }

    // GUVNOR-2546: Verification & Validation: BRL fragments are ignored
    @Test
    public void testConditionsShouldNotBeIgnored() throws Exception {
        table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                          new ArrayList<Import>(),
                                                          "mytable" )
                .withConditionBRLColumn()
                .withActionInsertFact( "Application",
                                       "a",
                                       "approved",
                                       DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", null, true},
                        {2, "description", null, null}
                } )
                .buildTable();

        fireUpAnalyzer();

        assertContains( "EmptyRule", analyzerProvider.getAnalysisReport(), 2 );
        assertDoesNotContain( "EmptyRule", analyzerProvider.getAnalysisReport(), 1 );
        assertContains( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "RuleHasNoRestrictionsAndWillAlwaysFire", analyzerProvider.getAnalysisReport(), 2 );

    }
}