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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.uberfire.mvp.PlaceRequest;

@Dependent
public class AnalysisReportScreen implements IsWidget {

    private static final Logger LOGGER = Logger.getLogger("DTable Analyzer");
    private final ListDataProvider<Issue> dataProvider = new ListDataProvider<Issue>();
    private AnalysisReportScreenView view;
    private Event<IssueSelectedEvent> issueSelectedEvent;
    private PlaceRequest currentPlace;

    public AnalysisReportScreen() {
    }

    @Inject
    public AnalysisReportScreen(final AnalysisReportScreenView view,
                                final Event<IssueSelectedEvent> issueSelectedEvent) {
        this.view = view;
        this.issueSelectedEvent = issueSelectedEvent;

        view.setPresenter(this);
        view.setUpDataProvider(dataProvider);
    }

    public void showReport(final AnalysisReport report) {
        LOGGER.finest("Received report for: " + report.getPlace().getPath());

        if (!report.getPlace().equals(currentPlace)) {
            return;
        }

        view.showStatusComplete();

        final List<Issue> issues = getIssues(report);
        final boolean isIllegalState = issues.stream()
                .filter(issue -> Objects.equals(issue.getCheckType(),
                                                CheckType.ILLEGAL_VERIFIER_STATE))
                .count() > 0;
        if (isIllegalState) {
            view.hideProgressStatus();
        }

        dataProvider.setList(issues);

        if (dataProvider.getList().isEmpty()) {
            fireIssueSelectedEvent(Issue.EMPTY);
            view.clearIssue();
        } else {
            final Issue issue = dataProvider.getList().get(0);
            onSelect(issue);
        }
    }

    public void setCurrentPlace(final PlaceRequest place) {
        LOGGER.info("Activating place: " + place.getPath());
        currentPlace = place;
    }

    private List<Issue> getIssues(final AnalysisReport report) {
        return new ArrayList<>(new IssuesSet(report.getAnalysisData()));
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onSelect(final Issue issue) {
        view.showIssue(issue);
        fireIssueSelectedEvent(issue);
    }

    void fireIssueSelectedEvent(final Issue issue) {
        LOGGER.finest("issue.debug: " + issue.getDebugMessage());
        issueSelectedEvent.fire(new IssueSelectedEvent(currentPlace,
                                                       issue));
    }

    public void showStatus(final Status status) {
        view.showStatusTitle(status.getStart(),
                             status.getEnd(),
                             status.getTotalCheckCount());
    }
}
