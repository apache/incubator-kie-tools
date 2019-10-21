/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.verifier.reporting.client.panel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AnalysisReportScreenTest {

    private AnalysisReportScreen screen;

    @Mock
    private AnalysisReportScreenView view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<IssueSelectedEvent> issueSelectedEvent;

    @Captor
    private ArgumentCaptor<ListDataProvider<Issue>> listDataProviderArgumentCaptor;

    @Captor
    private ArgumentCaptor<IssueSelectedEvent> issueSelectedEventCaptor;

    private ListDataProvider dataProvider;

    @Mock
    private PlaceRequest place;

    @Before
    public void setUp() throws
            Exception {

        screen = new AnalysisReportScreen(view,
                                          placeManager,
                                          issueSelectedEvent);

        screen.setCurrentPlace(place);

        verify(view).setPresenter(screen);
        verify(view).setUpDataProvider(listDataProviderArgumentCaptor.capture());
        dataProvider = listDataProviderArgumentCaptor.getValue();
    }

    @Test
    public void testShowReport() throws
            Exception {
        Issue issue1 = new Issue(Severity.WARNING,
                                 CheckType.DEFICIENT_ROW,
                                 Collections.emptySet());
        screen.showReport(getAnalysis(issue1));

        verify(placeManager).goTo(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));

        assertEquals(1,
                     dataProvider.getList()
                             .size());
        assertTrue(dataProvider.getList()
                           .contains(issue1));

        Issue issue2 = new Issue(Severity.ERROR,
                                 CheckType.CONFLICTING_ROWS,
                                 Collections.emptySet());
        Issue issue3 = new Issue(Severity.WARNING,
                                 CheckType.SINGLE_HIT_LOST,
                                 Collections.emptySet());
        screen.showReport(getAnalysis(issue2,
                                      issue3));

        verify(placeManager,
               times(2)).goTo(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));

        verify(view).showIssue(issue1);

        assertEquals(2,
                     dataProvider.getList()
                             .size());
        assertFalse(dataProvider.getList()
                            .contains(issue1));
        assertTrue(dataProvider.getList()
                           .contains(issue2));
        assertTrue(dataProvider.getList()
                           .contains(issue3));
    }

    @Test
    public void testShowReportIllegalState() {
        final Issue illegalStateIssue = new Issue(Severity.ERROR,
                                                  CheckType.ILLEGAL_VERIFIER_STATE,
                                                  Collections.emptySet());
        screen.showReport(getAnalysis(illegalStateIssue));

        verify(placeManager).goTo(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));

        assertEquals(1,
                     dataProvider.getList().size());
        assertTrue(dataProvider.getList().contains(illegalStateIssue));

        verify(view).hideProgressStatus();
    }

    @Test
    public void testMergeEmptyRules() throws
            Exception {
        testMerge(CheckType.EMPTY_RULE);
    }

    @Test
    public void testMergeMissingAction() throws
            Exception {
        testMerge(CheckType.MISSING_ACTION);
    }

    @Test
    public void testMergeMissingRestriction() throws
            Exception {
        testMerge(CheckType.MISSING_RESTRICTION);
    }

    private void testMerge(CheckType type) {
        Issue issue1 = new Issue(Severity.WARNING,
                                 type,
                                 new HashSet<>(Arrays.asList(1)));

        Issue issue2 = new Issue(Severity.WARNING,
                                 type,
                                 new HashSet<>(Arrays.asList(2)));
        Issue issue3 = new Issue(Severity.WARNING,
                                 type,
                                 new HashSet<>(Arrays.asList(3)));
        screen.showReport(getAnalysis(issue1, issue2, issue3));

        verify(placeManager).goTo(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));

        assertEquals(1, dataProvider.getList().size());

        Issue issue = (Issue) dataProvider.getList().get(0);

        assertEquals(Severity.WARNING, issue.getSeverity());
        assertEquals(type, issue.getCheckType());
        Iterator<Integer> rowNumbers = issue.getRowNumbers().iterator();
        for (Integer rowNumber : Arrays.asList(1, 2, 3)) {
            assertEquals(rowNumber, rowNumbers.next());
        }

        verify(view).showIssue(issue);
    }

    @Test
    public void testDoNotShowIfThereAreNoIssues() throws
            Exception {
        screen.showReport(getAnalysis());

        assertEquals(0,
                     dataProvider.getList()
                             .size());

        verify(view,
               never()).showIssue(any(Issue.class));
        verify(placeManager,
               never()).goTo(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));
        verify(placeManager).closePlace(eq("org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen"));
    }

    @Test
    public void testShowEverythingOnce() throws
            Exception {

        Issue issue2 = new Issue(Severity.WARNING,
                                 CheckType.REDUNDANT_ROWS,
                                 Collections.emptySet());
        Issue issue3 = new Issue(Severity.WARNING,
                                 CheckType.REDUNDANT_ROWS,
                                 Collections.emptySet());
        Issue issue4 = new Issue(Severity.WARNING,
                                 CheckType.REDUNDANT_ROWS,
                                 new HashSet<>(Arrays.asList(1,
                                                             2,
                                                             3)));
        Issue issue5 = new Issue(Severity.WARNING,
                                 CheckType.REDUNDANT_ROWS,
                                 new HashSet<>(Arrays.asList(1,
                                                             2,
                                                             3)));
        screen.showReport(getAnalysis(issue2,
                                      issue3,
                                      issue4,
                                      issue5));

        assertEquals(2,
                     dataProvider.getList()
                             .size());
    }

    @Test
    public void testOnSelect() throws
            Exception {
        Issue issue1 = new Issue(Severity.WARNING,
                                 CheckType.REDUNDANT_ROWS,
                                 Collections.emptySet());
        Issue issue2 = new Issue(Severity.WARNING,
                                 CheckType.SINGLE_HIT_LOST,
                                 Collections.emptySet());
        screen.showReport(getAnalysis(issue1,
                                      issue2));

        verify(issueSelectedEvent,
               times(1)).fire(issueSelectedEventCaptor.capture());
        assertEquals(issue1,
                     issueSelectedEventCaptor.getValue().getIssue());

        screen.onSelect(issue2);

        verify(view).showIssue(issue2);

        verify(issueSelectedEvent,
               times(2)).fire(issueSelectedEventCaptor.capture());
        assertEquals(issue2,
                     issueSelectedEventCaptor.getValue().getIssue());
    }

    @Test
    public void testShowStatus() throws
            Exception {
        screen.showStatus(new Status("UUID",
                                     1,
                                     2,
                                     3));

        verify(view).showStatusTitle(1,
                                     2,
                                     3);
    }

    @Test
    public void testThePlaceInReportIsNotActive() throws
            Exception {

        screen.showReport(getAnalysis(PlaceRequest.NOWHERE));

        verify(view,
               never()).showStatusComplete();
    }

    @Test
    public void testNoIssuesShowNothing() throws
            Exception {
        screen.showReport(getAnalysis());

        verify(view,
               never()).showIssue(any(Issue.class));
        verify(view).clearIssue();

        verify(issueSelectedEvent,
               times(1)).fire(issueSelectedEventCaptor.capture());
        assertEquals(Issue.EMPTY,
                     issueSelectedEventCaptor.getValue().getIssue());
    }

    private AnalysisReport getAnalysis(Issue... issues) {
        return getAnalysis(place,
                           issues);
    }

    private AnalysisReport getAnalysis(PlaceRequest place,
                                       Issue... issues) {
        final AnalysisReport report = new AnalysisReport(place);
        final Set<Issue> unorderedIssues = new HashSet<Issue>();

        for (Issue issue : issues) {
            unorderedIssues.add(issue);
        }
        report.setIssues(unorderedIssues);

        return report;
    }
}