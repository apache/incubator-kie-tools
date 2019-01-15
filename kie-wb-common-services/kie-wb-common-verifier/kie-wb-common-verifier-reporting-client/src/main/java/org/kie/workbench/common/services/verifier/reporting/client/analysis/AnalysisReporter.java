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

package org.kie.workbench.common.services.verifier.reporting.client.analysis;

import java.util.Set;

import org.drools.verifier.api.Reporter;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReport;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.uberfire.mvp.PlaceRequest;

public class AnalysisReporter
        implements Reporter {

    private PlaceRequest place;
    private AnalysisReportScreen reportScreen;

    public AnalysisReporter(final PlaceRequest place,
                            final AnalysisReportScreen reportScreen) {
        this.place = place;
        this.reportScreen = reportScreen;
    }

    public void sendReport(final AnalysisReport report) {
        reportScreen.showReport(report);
    }

    @Override
    public void sendReport(final Set<Issue> issues) {
        sendReport(new AnalysisReport(place,
                                      issues));
    }

    @Override
    public void sendStatus(final Status status) {
        reportScreen.showStatus(status);
    }

    @Override
    public void activate() {
        reportScreen.setCurrentPlace(place);
    }
}
