/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.AnalysisConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.TestUtil.*;
import static org.junit.Assert.*;

@RunWith( GwtMockitoTestRunner.class )
public class DecisionTableAnalyzerConflictResolverTest {

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
    public void testNoIssue() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withPersonAgeColumn( ">" )
                                                               .withAccountDepositColumn( "<" )
                                                               .withApplicationApprovedSetField()
                                                               .withData( DataBuilderProvider
                                                                                  .row( 100, 0.0, true )
                                                                                  .end() )
                                                               .buildAnalyzer();

        analyzer.analyze( Collections.emptyList() );
        assertDoesNotContain( "ConflictingRows", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testNoIssueWithNulls() throws Exception {
        final GuidedDecisionTable52 table52 = analyzerProvider.makeAnalyser()
                                                              .withPersonAgeColumn( ">" )
                                                              .withPersonAgeColumn( "<" )
                                                              .withData( DataBuilderProvider
                                                                                 .row( null, null )
                                                                                 .end() )
                                                              .buildTable();


        // After a save has been done, the server side sometimes sets the String field value to "" for numbers, even when the data type is a number
        table52.getData().get( 0 ).get( 2 ).setStringValue( "" );
        table52.getData().get( 0 ).get( 3 ).setStringValue( "" );

        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser( table52 );

        analyzer.analyze( Collections.emptyList() );
        assertTrue( analyzerProvider.getAnalysisReport().getAnalysisData().isEmpty() );

    }

    @Test
    public void testImpossibleMatch001() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withPersonAgeColumn( ">" )
                                                               .withPersonAgeColumn( "<" )
                                                               .withData( DataBuilderProvider
                                                                                  .row( 100, 0 )
                                                                                  .end() )
                                                               .buildAnalyzer();


        analyzer.analyze( Collections.emptyList() );
        assertContains( "ImpossibleMatch", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testImpossibleMatch002() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withEnumColumn( "a", "Person", "name", "==", "Toni,Eder" )
                                                               .withPersonNameColumn( "==" )
                                                               .withData( DataBuilderProvider
                                                                                  .row( "Toni", "" )
                                                                                  .end() )
                                                               .buildAnalyzer();

        analyzer.analyze( Collections.emptyList() );
        assertDoesNotContain( "ImpossibleMatch", analyzerProvider.getAnalysisReport() );
    }

    @Test
    public void testConflictIgnoreEmptyRows() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withPersonAgeColumn( "==" )
                                                               .withPersonApprovedActionSetField()
                                                               .withData( DataBuilderProvider
                                                                                  .row( null, "" )
                                                                                  .row( null, "true" )
                                                                                  .end() )
                                                               .buildAnalyzer();

        analyzer.analyze( Collections.emptyList() );

        assertDoesNotContain( "ConflictingRows", analyzerProvider.getAnalysisReport(), 1 );
        assertDoesNotContain( "ConflictingRows", analyzerProvider.getAnalysisReport(), 2 );

    }

    @Test
    public void testConflictWithASubsumingRow() throws Exception {
        final DecisionTableAnalyzer analyzer = analyzerProvider.makeAnalyser()
                                                               .withPersonAgeColumn( "==" )
                                                               .withPersonNameColumn( "==" )
                                                               .withPersonLastNameColumn( "==" )
                                                               .withPersonSalarySetFieldAction()
                                                               .withPersonDescriptionSetActionField()
                                                               .withData( DataBuilderProvider
                                                                                  .row( null, null, null, 100, "ok" )
                                                                                  .row( null, "Toni", null, 200, "ok" )
                                                                                  .row( 12, "Toni", "Rikkola", 300, "ok" )
                                                                                  .row( null, null, null, null, null )
                                                                                  .end()
                                                                        )
                                                               .buildAnalyzer();


        analyzer.analyze( Collections.emptyList() );

        assertContains( "ConflictingRows", analyzerProvider.getAnalysisReport(), 2 );
        assertContains( "ConflictingRows", analyzerProvider.getAnalysisReport(), 3 );

    }

}