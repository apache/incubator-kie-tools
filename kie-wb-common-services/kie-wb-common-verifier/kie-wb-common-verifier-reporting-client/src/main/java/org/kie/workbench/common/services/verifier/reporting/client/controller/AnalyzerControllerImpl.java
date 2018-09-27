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

package org.kie.workbench.common.services.verifier.reporting.client.controller;

import java.util.logging.Logger;

import com.google.gwt.event.shared.EventBus;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.DecisionTableAnalyzer;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.uberfire.client.mvp.PlaceManager;

public class AnalyzerControllerImpl
        implements AnalyzerController,
                   ValidateEvent.Handler,
                   DeleteRowEvent.Handler,
                   AfterColumnDeleted.Handler,
                   UpdateColumnDataEvent.Handler,
                   AppendRowEvent.Handler,
                   InsertRowEvent.Handler,
                   AfterColumnInserted.Handler {

    private static final Logger LOGGER = Logger.getLogger("DTable Analyzer");

    private final DecisionTableAnalyzer decisionTableAnalyzer;
    private final Events events;
    private PlaceManager placeManager;

    public AnalyzerControllerImpl(final DecisionTableAnalyzer decisionTableAnalyzer,
                                  final PlaceManager placeManager,
                                  final EventBus eventBus) {
        this.decisionTableAnalyzer = PortablePreconditions.checkNotNull("decisionTableAnalyzer",
                                                                        decisionTableAnalyzer);
        this.placeManager = PortablePreconditions.checkNotNull("placeManager",
                                                               placeManager);
        events = new Events(PortablePreconditions.checkNotNull("eventBus",
                                                               eventBus),
                            this);
    }

    @Override
    public void initialiseAnalysis() {
        LOGGER.info("Initializing analysis.");

        events.setup();

        decisionTableAnalyzer.activate();
    }

    @Override
    public void terminateAnalysis() {

        LOGGER.info("Terminating analysis.");

        events.teardown();

        decisionTableAnalyzer.terminate();
        placeManager.closePlace(AnalysisReportScreen.IDENTIFIER);
    }

    @Override
    public void onValidate(final ValidateEvent event) {
        decisionTableAnalyzer.analyze(event.getUpdates());
    }

    @Override
    public void onAfterDeletedColumn(final AfterColumnDeleted event) {
        decisionTableAnalyzer.deleteColumns(event.getFirstColumnIndex(),
                                            event.getNumberOfColumns());
    }

    @Override
    public void onAfterColumnInserted(final AfterColumnInserted event) {
        decisionTableAnalyzer.insertColumn(event.getColumn());
    }

    @Override
    public void onAppendRow(final AppendRowEvent event) {
        decisionTableAnalyzer.appendRow();
    }

    @Override
    public void onDeleteRow(final DeleteRowEvent event) {
        decisionTableAnalyzer.deleteRow(event.getIndex());
    }

    @Override
    public void onInsertRow(final InsertRowEvent event) {
        decisionTableAnalyzer.insertRow(event.getIndex());
    }

    @Override
    public void onUpdateColumnData(final UpdateColumnDataEvent event) {
        decisionTableAnalyzer.updateColumns(event.getColumnData().size());
    }
}
