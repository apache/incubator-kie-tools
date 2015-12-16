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

import java.util.Iterator;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;

public class IssuePresenter
        implements IsWidget {

    private IssuePresenterView view;

    @Inject
    public IssuePresenter( final IssuePresenterView view ) {
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show( final Issue issue ) {
        if ( issue.getTitle() == null ) {
            view.setIssueTitle( issue.getTitle() );
        } else {
            view.setIssueTitle( issue.getTitle() );
        }
        view.setExplanation( issue.getExplanation().toHTML() );
        view.setLines( makeRowNumbers( issue ) );
    }

    private String makeRowNumbers( final Issue issue ) {
        StringBuilder builder = new StringBuilder();

        Iterator<Integer> iterator = issue.getRowNumbers().iterator();

        while ( iterator.hasNext() ) {
            builder.append( iterator.next() );
            if ( iterator.hasNext() ) {
                builder.append( ", " );
            }
        }

        return builder.toString();
    }

    public void clear() {
        view.setIssueTitle( "" );
        view.setExplanation( new SafeHtml() {
            @Override
            public String asString() {
                return "";
            }
        } );
        view.setLines( "" );
        view.hideLines();
    }
}
