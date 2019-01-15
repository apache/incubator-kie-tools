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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.DrlInitialize;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.AnalysisReporter;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.Receiver;
import org.kie.workbench.common.services.verifier.reporting.client.analysis.VerifierWebWorkerConnectionImpl;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.mvp.PlaceRequest;

public class DecisionTableAnalyzerBuilder {

    protected PlaceRequest placeRequest;
    protected AsyncPackageDataModelOracle oracle;
    protected GuidedDecisionTable52 model;
    protected AnalysisReportScreen analysisReportScreen;
    protected DTableUpdateManager updateManager;
    private AnalysisReporter analysisReporter;
    private DecisionTableAnalyzer decisionTableAnalyzer;
    private VerifierWebWorkerConnectionImpl webWorker;
    private FieldTypeProducer fieldTypeProducer;
    private Poster poster;

    public DecisionTableAnalyzerBuilder withPlaceRequest(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        return this;
    }

    public DecisionTableAnalyzerBuilder withOracle(final AsyncPackageDataModelOracle oracle) {
        this.oracle = oracle;
        fieldTypeProducer = new FieldTypeProducer(oracle);
        return this;
    }

    public DecisionTableAnalyzerBuilder withModel(final GuidedDecisionTable52 model) {
        this.model = model;
        return this;
    }

    public DecisionTableAnalyzerBuilder withReportScreen(final AnalysisReportScreen analysisReportScreen) {
        this.analysisReportScreen = analysisReportScreen;
        return this;
    }

    public DecisionTableAnalyzer build() {
        PortablePreconditions.checkNotNull("placeRequest",
                                           placeRequest);
        PortablePreconditions.checkNotNull("oracle",
                                           oracle);
        PortablePreconditions.checkNotNull("model",
                                           model);
        PortablePreconditions.checkNotNull("analysisReportScreen",
                                           analysisReportScreen);
        return getDTableAnalyzer();
    }

    private VerifierWebWorkerConnectionImpl getWebWorker() {
        if (webWorker == null) {
            webWorker = new VerifierWebWorkerConnectionImpl(new DrlInitialize(UUID.uuid(),
                                                                              model,
                                                                              new ModelMetaDataEnhancer(model).getHeaderMetaData(),
                                                                              fieldTypeProducer.getFactTypes(),
                                                                              ApplicationPreferences.getDroolsDateFormat()),
                                                            "verifier/dtableVerifier/dtableVerifier.nocache.js",
                                                            getPoster(),
                                                            new Receiver(getAnalysisReporter()));
        }
        return webWorker;
    }

    private Poster getPoster() {
        if (poster == null) {
            poster = new Poster();
        }
        return poster;
    }

    protected AnalysisReporter getAnalysisReporter() {
        if (analysisReporter == null) {
            analysisReporter = new AnalysisReporter(placeRequest,
                                                    analysisReportScreen);
        }
        return analysisReporter;
    }

    private DTableUpdateManager getUpdateManager() {
        if (this.updateManager == null) {
            this.updateManager = new DTableUpdateManager(getPoster(),
                                                         fieldTypeProducer);
        }
        return this.updateManager;
    }

    private DecisionTableAnalyzer getDTableAnalyzer() {
        if (decisionTableAnalyzer == null) {
            reset();
            decisionTableAnalyzer = new DecisionTableAnalyzer(model,
                                                              getUpdateManager(),
                                                              getWebWorker());
        }
        return decisionTableAnalyzer;
    }

    private void reset() {
        poster = null;
    }
}
