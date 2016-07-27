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

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;

@RunWith( GwtMockitoTestRunner.class )
public class DecisionTableAnalyzerSubsumptionTest {

    @GwtMock
    AnalysisConstants analysisConstants;
    @GwtMock
    DateTimeFormat    dateTimeFormat;

    private AnalyzerProvider analyzerProvider;

    @Before
    public void setUp() throws Exception {
        analyzerProvider = new AnalyzerProvider();
    }

    @Test
    public void testSubsumptionBooleanDifferentValueDifferentOperator() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withConditionBooleanColumn( "a", "Person", "approved", "!=" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", true, null, true},
                        {2, "description", null, false, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testSubsumptionBooleansWithSameValue() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", true, null, true},
                        {2, "description", null, true, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }


    @Test
    public void testBooleansAreNotRedundant() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", true, null, true},
                        {2, "description", null, false, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertDoesNotContain( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testBooleansAreNotRedundantDifferentOperator() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionBooleanColumn( "a", "Person", "approved", "==" )
                .withConditionBooleanColumn( "a", "Person", "approved", "!=" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", true, null, true},
                        {2, "description", null, true, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertDoesNotContain( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testSumbsumptantAgeRows() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", 0, null, true},
                        {2, "description", null, 0, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testSumbsumptantAgeDifferentOperator() throws Exception {
        final GuidedDecisionTable52 table52 = new ExtendedGuidedDecisionTableBuilder( "org.test",
                                                                                      new ArrayList<Import>(),
                                                                                      "mytable" )
                .withConditionIntegerColumn( "a", "Person", "age", ">" )
                .withConditionIntegerColumn( "a", "Person", "age", ">=" )
                .withActionSetField( "a", "approved", DataType.TYPE_BOOLEAN )
                .withData( new Object[][]{
                        {1, "description", 0, null, true},
                        {2, "description", null, 1, true},
                } )
                .buildTable();

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.onValidate( new ValidateEvent( Collections.emptyList() ) );
        assertContains( "RedundantRows", analyzerProvider.getAnalysisReport() );
    }

}