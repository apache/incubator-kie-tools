/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.services.verifier.api.client.Reporter;
import org.drools.workbench.services.verifier.api.client.Status;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;
import org.uberfire.mvp.PlaceRequest;

public class AnalysisReporter
        implements Reporter {

    private PlaceRequest place;
    private AnalysisReportScreen reportScreen;

    public AnalysisReporter( final PlaceRequest place,
                             final AnalysisReportScreen reportScreen ) {
        this.place = place;
        this.reportScreen = reportScreen;
    }

    public void sendReport( final AnalysisReport report ) {
        reportScreen.showReport( report );
    }

    @Override
    public void sendReport( final Set<Issue> issues ) {
        sendReport( new AnalysisReport( place,
                                        issues ) );
    }

    @Override
    public void sendStatus( final Status status ) {
        reportScreen.showStatus( status );
    }

    @Override
    public void activate() {
        reportScreen.setCurrentPlace( place );
    }

}
