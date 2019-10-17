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
import java.util.HashSet;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.IllegalVerifierStateIssue;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class IssuePresenterTest {

    private IssuePresenter screen;
    private IssuePresenterView view;

    @Before
    public void setUp() throws
            Exception {
        view = mock(IssuePresenterView.class);
        screen = new IssuePresenter(view);
    }

    @Test
    public void testShow() throws
            Exception {

        Issue issue = new Issue(Severity.WARNING,
                                CheckType.REDUNDANT_ROWS,
                                new HashSet<>(Arrays.asList(1,
                                                            2,
                                                            3))
        );

        screen.show(issue);

        verify(view).setIssueTitle("RedundantRows");
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass(SafeHtml.class);
        verify(view).setExplanation(safeHtmlArgumentCaptor.capture());
        assertEquals("<p>MissingRangeP1(1)</p>",
                     safeHtmlArgumentCaptor.getValue()
                             .asString());
        verify(view).setLines("1, 2, 3");
    }

    @Test
    public void testIllegalVerifierState() {

        final Issue issue = new IllegalVerifierStateIssue();

        screen.show(issue);

        verify(view).setIssueTitle("VerifierFailedTitle");
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass(SafeHtml.class);
        verify(view).setExplanation(safeHtmlArgumentCaptor.capture());
        assertEquals("<p>VerifierFailed</p>",
                     safeHtmlArgumentCaptor.getValue().asString());
        verify(view).setLines("");
    }

    @Test
    public void testShowEmptyIssue() throws
            Exception {

        screen.show(Issue.EMPTY);

        verify(view).setIssueTitle("---");
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass(SafeHtml.class);
        verify(view).setExplanation(safeHtmlArgumentCaptor.capture());
        assertEquals("---",
                     safeHtmlArgumentCaptor.getValue()
                             .asString());
        verify(view).setLines("");
    }

    @Test
    public void testClear() throws
            Exception {
        screen.clear();

        verify(view).setIssueTitle("");
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass(SafeHtml.class);
        verify(view).setExplanation(safeHtmlArgumentCaptor.capture());
        assertEquals("",
                     safeHtmlArgumentCaptor.getValue()
                             .asString());
        verify(view).hideLines();
        verify(view).setLines("");
    }
}