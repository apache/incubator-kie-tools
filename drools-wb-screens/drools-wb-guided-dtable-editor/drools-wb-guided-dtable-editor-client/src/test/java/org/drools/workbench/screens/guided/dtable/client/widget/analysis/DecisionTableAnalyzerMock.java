/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReport;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

public class DecisionTableAnalyzerMock
        extends DecisionTableAnalyzer {

    private final Callback<Status>         statusCallback;
    private       Callback<AnalysisReport> reportCallback;
    private AnalysisReportScreen analysisReportScreen = new AnalysisReportScreen() {
        @Override
        public void showReport( final AnalysisReport report ) {
            reportCallback.callback( report );
        }

        @Override
        public void showStatus( final Status status ) {
            statusCallback.callback( status );
        }
    };

    public DecisionTableAnalyzerMock( final AsyncPackageDataModelOracle oracle,
                                      final GuidedDecisionTable52 model,
                                      final Callback<AnalysisReport> reportCallback,
                                      final Callback<Status> statusCallback ) {
        super( mock( PlaceRequest.class ),
               oracle,
               model,
               mock( EventBus.class ) );
        this.reportCallback = reportCallback;

        this.statusCallback = statusCallback;
    }

    @Override
    protected CheckRunner getCheckRunner() {
        return new CheckRunner() {
            @Override
            protected void doRun( final CancellableRepeatingCommand command ) {
                while ( command.execute() ) {
                    //loop
                }
            }
        };
    }

    @Override
    protected AnalysisReportScreen getAnalysisReportScreen() {
        return analysisReportScreen;
    }
}
