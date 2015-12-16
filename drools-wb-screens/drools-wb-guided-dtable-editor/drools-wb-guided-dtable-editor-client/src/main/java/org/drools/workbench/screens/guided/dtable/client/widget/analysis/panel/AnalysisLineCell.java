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
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Severity;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class AnalysisLineCell
        extends AbstractCell<Issue> {

    interface CellTemplate
            extends
            SafeHtmlTemplates {

        @Template("<span>" +
                "<i class=\"fa {0}\" ></i>" +
                "<span> - {1} - {2}</span>" +
                "</span>")
        SafeHtml text( String cssStyleName,
                       SafeHtml lineNumbers,
                       String message );
    }

    private static final CellTemplate TEMPLATE = GWT.create( CellTemplate.class );

    @Override
    public void render( final Context context,
                        final Issue issue,
                        final SafeHtmlBuilder safeHtmlBuilder ) {
        safeHtmlBuilder.append( TEMPLATE.text( getImage( issue.getSeverity() ),
                                               getLineNumbers( issue.getRowNumbers() ),
                                               issue.getTitle() ) );
    }

    private SafeHtml getLineNumbers( final Set<Integer> rowNumbers ) {
        return new SafeHtml() {
            @Override
            public String asString() {
                StringBuilder builder = new StringBuilder();
                Iterator<Integer> iterator = rowNumbers.iterator();

                while ( iterator.hasNext() ) {
                    builder.append( iterator.next() );
                    if ( iterator.hasNext() ) {
                        builder.append( ", " );
                    }
                }
                return builder.toString();
            }
        };
    }

    private String getImage( final Severity severity ) {
        switch ( severity ) {
            case ERROR:
                return IconType.REMOVE.getCssName();
            case WARNING:
                return IconType.QUESTION.getCssName();
            default:
                return IconType.INFO.getCssName();
        }
    }

}
