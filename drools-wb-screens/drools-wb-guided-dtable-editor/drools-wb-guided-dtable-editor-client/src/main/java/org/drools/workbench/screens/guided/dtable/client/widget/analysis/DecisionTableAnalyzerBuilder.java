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

import java.util.logging.Logger;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.services.verifier.plugin.client.api.Initialize;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.PlaceRequest;

public class DecisionTableAnalyzerBuilder {

    private static final Logger LOGGER = Logger.getLogger( "DTable Analyzer" );

    protected PlaceRequest placeRequest;
    protected AsyncPackageDataModelOracle oracle;
    protected GuidedDecisionTable52 model;
    protected AnalysisReportScreen analysisReportScreen;
    protected DTableUpdateManagerImpl updateManager;
    private AnalysisReporter analysisReporter;
    private DecisionTableAnalyzer decisionTableAnalyzer;
    private VerifierWebWorkerConnectionImpl webWorker;
    private FieldTypeProducer fieldTypeProducer;

    public DecisionTableAnalyzerBuilder withPlaceRequest( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        return this;
    }

    public DecisionTableAnalyzerBuilder withOracle( final AsyncPackageDataModelOracle oracle ) {
        this.oracle = oracle;
        fieldTypeProducer = new FieldTypeProducer( oracle );
        return this;
    }

    public DecisionTableAnalyzerBuilder withModel( final GuidedDecisionTable52 model ) {
        this.model = model;
        return this;
    }

    public DecisionTableAnalyzerBuilder withReportScreen( final AnalysisReportScreen analysisReportScreen ) {
        this.analysisReportScreen = analysisReportScreen;
        return this;
    }

    public DecisionTableAnalyzer build() {
        PortablePreconditions.checkNotNull( "placeRequest",
                                            placeRequest );
        PortablePreconditions.checkNotNull( "oracle",
                                            oracle );
        PortablePreconditions.checkNotNull( "model",
                                            model );
        PortablePreconditions.checkNotNull( "analysisReportScreen",
                                            analysisReportScreen );
        return getDTableAnalyzer();
    }

    private VerifierWebWorkerConnectionImpl getWebWorker() {
        if ( webWorker == null ) {
            webWorker = new VerifierWebWorkerConnectionImpl( new Initialize( UUID.uuid(),
                                                                             model,
                                                                             new ModelMetaDataEnhancer( model ).getHeaderMetaData(),
                                                                             fieldTypeProducer.getFactTypes(),
                                                                             ApplicationPreferences.getDroolsDateFormat() ),
                                                             getAnalysisReporter() );
        }
        return webWorker;
    }

    protected AnalysisReporter getAnalysisReporter() {
        if ( analysisReporter == null ) {
            analysisReporter = new AnalysisReporter( placeRequest,
                                                     analysisReportScreen );
        }
        return analysisReporter;
    }

    public DTableUpdateManagerImpl getUpdateManager() {
        if ( this.updateManager == null ) {
            this.updateManager = new DTableUpdateManagerImpl( getWebWorker(),
                                                              fieldTypeProducer );
        }
        return this.updateManager;
    }


    private DecisionTableAnalyzer getDTableAnalyzer() {
        if ( decisionTableAnalyzer == null ) {
            decisionTableAnalyzer = new DecisionTableAnalyzer( model,
                                                               getUpdateManager(),
                                                               getWebWorker() );
        }
        return decisionTableAnalyzer;
    }
}
