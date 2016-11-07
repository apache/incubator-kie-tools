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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.drools.workbench.AnalysisReportScreen", preferredWidth = 360)
public class AnalysisReportScreen {

    public static final String IDENTIFIER = "org.drools.workbench.AnalysisReportScreen";

    private AnalysisReportScreenView view;
    private PlaceManager placeManager;
    private Event<IssueSelectedEvent> issueSelectedEvent;

    private final ListDataProvider<Issue> dataProvider = new ListDataProvider<Issue>();
    private AnalysisReport currentReport;

    public AnalysisReportScreen() {
    }

    @Inject
    public AnalysisReportScreen( final AnalysisReportScreenView view,
                                 final PlaceManager placeManager,
                                 final Event<IssueSelectedEvent> issueSelectedEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.issueSelectedEvent = issueSelectedEvent;

        view.setPresenter( this );
        view.setUpDataProvider( dataProvider );
    }

    public void onDTableClose( @Observes ClosePlaceEvent event ) {
        if ( currentReport != null && event.getPlace().equals( currentReport.getPlace() ) ) {
            placeManager.closePlace( IDENTIFIER );
        }
    }

    public void showReport( final AnalysisReport report ) {

        view.showStatusComplete();

        currentReport = report;

        if ( !report.getAnalysisData().isEmpty() ) {
            placeManager.goTo( IDENTIFIER );
        } else {
            placeManager.closePlace( IDENTIFIER );
        }

        dataProvider.setList( getIssues( report ) );

        if ( dataProvider.getList().isEmpty() ) {
            fireIssueSelectedEvent( Issue.EMPTY );
            view.clearIssue();

        } else {
            final Issue issue = dataProvider.getList().get( 0 );
            onSelect( issue );
        }
    }

    private ArrayList<Issue> getIssues( final AnalysisReport report ) {
        final TreeSet<Issue> issues = new TreeSet<>( new Comparator<Issue>() {
            @Override
            public int compare( final Issue issue,
                                final Issue other ) {
                int compareToSeverity = issue.getSeverity().compareTo( other.getSeverity() );

                if ( compareToSeverity == 0 ) {
                    int compareToTitle = issue.getTitle().compareTo( other.getTitle() );
                    if ( compareToTitle == 0 ) {
                        return compareRowNumbers( issue.getRowNumbers(),
                                                  other.getRowNumbers() );
                    } else {
                        return compareToTitle;
                    }
                } else {
                    return compareToSeverity;
                }

            }

            private int compareRowNumbers( final Set<Integer> rowNumbers,
                                           final Set<Integer> other ) {
                if ( rowNumbers.equals( other ) ) {
                    return 0;
                } else {
                    for ( Integer a : rowNumbers ) {
                        for ( Integer b : other ) {
                            if ( a < b ) {
                                return -1;
                            }
                        }
                    }
                    return 1;
                }
            }
        } );
        for ( final Issue issue : report.getAnalysisData() ) {
            issues.add( issue );
        }
        return new ArrayList<>( issues );
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.Analysis();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onSelect( final Issue issue ) {
        view.showIssue( issue );
        fireIssueSelectedEvent( issue );
    }

    void fireIssueSelectedEvent( final Issue issue ) {
        issueSelectedEvent.fire( new IssueSelectedEvent( currentReport.getPlace(),
                                                         issue ) );
    }

    public void showStatus( final Status status ) {
        view.showStatusTitle( status.getStart(),
                              status.getEnd(),
                              status.getTotalCheckCount() );
    }
}
