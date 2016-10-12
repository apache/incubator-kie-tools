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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.drools.workbench.services.verifier.api.client.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.mvp.PlaceRequest;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.Util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AnalysisReportScreenTest {

    private AnalysisReportScreen screen;

    @Mock
    private AnalysisReportScreenView view;
    @Mock
    private PlaceManager placeManager;

    private ListDataProvider dataProvider;

    @Before
    public void setUp() throws Exception {

        screen = new AnalysisReportScreen( view,
                                           placeManager );
        ArgumentCaptor<ListDataProvider> listDataProviderArgumentCaptor = ArgumentCaptor.forClass( ListDataProvider.class );
        verify( view ).setPresenter( screen );
        verify( view ).setUpDataProvider( listDataProviderArgumentCaptor.capture() );
        dataProvider = listDataProviderArgumentCaptor.getValue();
    }

    @Test
    public void testShowReport() throws Exception {
        Issue issue1 = new Issue( Severity.WARNING, "something", mock( ExplanationProvider.class ) );
        screen.showReport( getAnalysis( issue1 ) );

        verify( placeManager ).goTo( eq( "org.drools.workbench.AnalysisReportScreen" ) );

        assertEquals( 1, dataProvider.getList().size() );
        assertTrue( dataProvider.getList().contains( issue1 ) );

        Issue issue2 = new Issue( Severity.ERROR, "something else 1", mock( ExplanationProvider.class ) );
        Issue issue3 = new Issue( Severity.WARNING, "something else 2", mock( ExplanationProvider.class ) );
        screen.showReport( getAnalysis( issue2, issue3 ) );

        verify( placeManager, times( 2 ) ).goTo( eq( "org.drools.workbench.AnalysisReportScreen" ) );

        verify( view ).showIssue( issue1 );

        assertEquals( 2, dataProvider.getList().size() );
        assertFalse( dataProvider.getList().contains( issue1 ) );
        assertTrue( dataProvider.getList().contains( issue2 ) );
        assertTrue( dataProvider.getList().contains( issue3 ) );
    }

    @Test
    public void testDoNotShowIfThereAreNoIssues() throws Exception {
        screen.showReport( getAnalysis() );

        assertEquals( 0, dataProvider.getList().size() );

        verify( view, never() ).showIssue( any( Issue.class ) );
        verify( placeManager, never() ).goTo( eq( "org.drools.workbench.AnalysisReportScreen" ) );
        verify( placeManager ).closePlace( eq( "org.drools.workbench.AnalysisReportScreen" ) );
    }

    @Test
    public void testShowEverythingOnce() throws Exception {

        Issue issue2 = new Issue( Severity.WARNING, "we are one", mock( ExplanationProvider.class ) );
        Issue issue3 = new Issue( Severity.WARNING, "we are one", mock( ExplanationProvider.class ) );
        Issue issue4 = new Issue( Severity.WARNING, "we are one", mock( ExplanationProvider.class ), getMockRuleInspector( 1 ), getMockRuleInspector( 2 ), getMockRuleInspector( 3 ) );
        Issue issue5 = new Issue( Severity.WARNING, "we are one", mock( ExplanationProvider.class ), getMockRuleInspector( 1 ), getMockRuleInspector( 2 ), getMockRuleInspector( 3 ) );
        screen.showReport( getAnalysis( issue2, issue3, issue4, issue5 ) );

        assertEquals( 2, dataProvider.getList().size() );

    }

    @Test
    public void testOnSelect() throws Exception {
        Issue issue1 = new Issue( Severity.WARNING, "something", mock( ExplanationProvider.class ) );
        Issue issue2 = new Issue( Severity.WARNING, "something else", mock( ExplanationProvider.class ) );
        screen.showReport( getAnalysis( issue1, issue2 ) );

        screen.onSelect( issue2 );

        verify( view ).showIssue( issue2 );
    }

    @Test
    public void testDTableCloses() throws Exception {
        Issue issue1 = new Issue( Severity.WARNING, "something", mock( ExplanationProvider.class ) );

        PlaceRequest thisPlace = mock( PlaceRequest.class );
        PlaceRequest someOtherPlace = mock( PlaceRequest.class );
        screen.showReport( getAnalysis( thisPlace, issue1 ) );

        verify( view ).showStatusComplete();
        verify( view ).showIssue( issue1 );

        screen.onDTableClose( new ClosePlaceEvent( someOtherPlace ) );
        verify( placeManager, never() ).closePlace( eq( "org.drools.workbench.AnalysisReportScreen" ) );

        screen.onDTableClose( new ClosePlaceEvent( thisPlace ) );
        verify( placeManager ).closePlace( eq( "org.drools.workbench.AnalysisReportScreen" ) );
    }

    @Test
    public void testShowStatus() throws Exception {
        screen.showStatus( new Status( 1, 2, 3 ) );

        verify( view ).showStatusTitle( 1, 2, 3 );
    }

    @Test
    public void testDTableClosesWhenThereIsNoReport() throws Exception {

        screen.onDTableClose( new ClosePlaceEvent( mock( PlaceRequest.class ) ) );
        verify( placeManager, never() ).closePlace( eq( "org.drools.workbench.AnalysisReportScreen" ) );
    }

    @Test
    public void testNoIssuesShowNothing() throws Exception {
        screen.showReport( getAnalysis() );

        verify( view, never() ).showIssue( any( Issue.class ) );
        verify( view ).clearIssue();
    }

    private AnalysisReport getAnalysis( Issue... issues ) {
        return getAnalysis( mock( PlaceRequest.class ), issues );
    }

    private AnalysisReport getAnalysis( PlaceRequest place,
                                        Issue... issues ) {
        final AnalysisReport report = new AnalysisReport( place );
        final Set<Issue> unorderedIssues = new HashSet<Issue>();

        for ( Issue issue : issues ) {
            unorderedIssues.add( issue );
        }
        report.setIssues( unorderedIssues );

        return report;
    }
}