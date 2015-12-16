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

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IssuePresenterTest {

    private IssuePresenter screen;
    private IssuePresenterView view;

    @Before
    public void setUp() throws Exception {
        view = mock( IssuePresenterView.class );
        screen = new IssuePresenter( view );
    }

    @Test
    public void testShow() throws Exception {

        Issue issue = new Issue( Severity.WARNING, "some title", 1, 2, 3 );

        issue.getExplanation().addParagraph( "explanation" );

        screen.show( issue );

        verify( view ).setIssueTitle( "some title" );
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass( SafeHtml.class );
        verify( view ).setExplanation( safeHtmlArgumentCaptor.capture() );
        assertEquals( "<p>explanation</p>", safeHtmlArgumentCaptor.getValue().asString() );
        verify( view ).setLines( "1, 2, 3" );
    }

    @Test
    public void testClear() throws Exception {
        screen.clear();

        verify( view ).setIssueTitle( "" );
        ArgumentCaptor<SafeHtml> safeHtmlArgumentCaptor = ArgumentCaptor.forClass( SafeHtml.class );
        verify( view ).setExplanation( safeHtmlArgumentCaptor.capture() );
        assertEquals( "", safeHtmlArgumentCaptor.getValue().asString() );
        verify( view ).hideLines();
        verify( view ).setLines( "" );
    }
}