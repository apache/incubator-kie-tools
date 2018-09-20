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
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.kie.workbench.common.services.verifier.reporting.client.resources.i18n.AnalysisConstants;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = AnalysisReportScreen.IDENTIFIER, preferredWidth = 360)
public class AnalysisReportScreen {

    public static final String IDENTIFIER = "org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen";

    private static final Logger LOGGER = Logger.getLogger("DTable Analyzer");

    private AnalysisReportScreenView view;
    private PlaceManager placeManager;

    private Event<IssueSelectedEvent> issueSelectedEvent;
    private final ListDataProvider<Issue> dataProvider = new ListDataProvider<Issue>();
    private PlaceRequest currentPlace;

    public AnalysisReportScreen() {
    }

    @Inject
    public AnalysisReportScreen(final AnalysisReportScreenView view,
                                final PlaceManager placeManager,
                                final Event<IssueSelectedEvent> issueSelectedEvent) {
        this.view = view;
        this.placeManager = placeManager;
        this.issueSelectedEvent = issueSelectedEvent;

        view.setPresenter(this);
        view.setUpDataProvider(dataProvider);
    }

    @OnClose
    public void onClose() {
        dataProvider.flush();
        view.clearIssue();
    }

    public void showReport(final AnalysisReport report) {
        LOGGER.finest("Received report for: " + report.getPlace().getPath());

        if (!report.getPlace()
                .equals(currentPlace)) {
            return;
        }

        view.showStatusComplete();

        dataProvider.setList(getIssues(report));

        if (dataProvider.getList()
                .isEmpty()) {
            fireIssueSelectedEvent(Issue.EMPTY);
            view.clearIssue();
        } else {
            final Issue issue = dataProvider.getList()
                    .get(0);
            onSelect(issue);
        }

        if (!report.getAnalysisData()
                .isEmpty()) {
            LOGGER.finest("goto " + IDENTIFIER);
            placeManager.goTo(IDENTIFIER);
            LOGGER.finest("went " + IDENTIFIER);
        } else {
            LOGGER.finest("close " + IDENTIFIER);
            placeManager.closePlace(IDENTIFIER);
            LOGGER.finest("closed " + IDENTIFIER);
        }
    }

    public void setCurrentPlace(final PlaceRequest place) {
        LOGGER.info("Activating place: " + place.getPath());
        currentPlace = place;
    }

    private ArrayList<Issue> getIssues(final AnalysisReport report) {
        return new ArrayList<>(new IssuesSet(report.getAnalysisData()));
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return AnalysisConstants.INSTANCE.Analysis();
    }

    @WorkbenchPartView
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
