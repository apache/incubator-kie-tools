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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.RuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.UpdateManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.base.CheckRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Index;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.IndexBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.PlaceRequest;

public class DecisionTableAnalyzerBuilder {

    protected PlaceRequest                placeRequest;
    protected AsyncPackageDataModelOracle oracle;
    protected GuidedDecisionTable52       model;
    protected AnalysisReportScreen        analysisReportScreen;

    public DecisionTableAnalyzerBuilder withPlaceRequest( final PlaceRequest placeRequest ) {
        this.placeRequest = placeRequest;
        return this;
    }

    public DecisionTableAnalyzerBuilder withOracle( final AsyncPackageDataModelOracle oracle ) {
        this.oracle = oracle;
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
        PortablePreconditions.checkNotNull( "placeRequest", placeRequest );
        PortablePreconditions.checkNotNull( "oracle", oracle );
        PortablePreconditions.checkNotNull( "model", model );
        PortablePreconditions.checkNotNull( "analysisReportScreen", analysisReportScreen );

        return getInnerBuilder().build();
    }

    protected InnerBuilder getInnerBuilder() {
        return new InnerBuilder( new CheckRunner() );
    }

    protected CacheBuilder getCacheBuilder() {
        return new CacheBuilder();
    }

    protected UpdateManagerBuilder getUpdateManagerBuilder( final CheckRunner checkRunner ) {
        return new UpdateManagerBuilder( checkRunner );
    }

    class CacheBuilder {
        private RuleInspectorCache cache;
        private ColumnUtilities    columnUtilities;
        private Index              index;

        protected RuleInspectorCache buildCache() {
            if ( cache == null ) {
                cache = new RuleInspectorCache( getUtils(),
                                                model,
                                                getIndex() );
            }
            return cache;
        }

        protected Index getIndex() {
            if ( index == null ) {
                index = new IndexBuilder( model,
                                          getUtils() ).build();
            }
            return index;
        }

        protected ColumnUtilities getUtils() {
            if ( columnUtilities == null ) {
                columnUtilities = new ColumnUtilities( model,
                                                       oracle );
            }
            return columnUtilities;
        }
    }

    class UpdateManagerBuilder
            extends CacheBuilder {

        protected final CheckRunner   checkRunner;
        protected       UpdateManager updateManager;


        public UpdateManagerBuilder( final CheckRunner checkRunner ) {
            this.checkRunner = checkRunner;
        }

        protected UpdateManager buildUpdateManager() {
            if ( updateManager == null ) {
                updateManager = new UpdateManager( getIndex(),
                                                   model,
                                                   buildCache(),
                                                   checkRunner );
            }
            return updateManager;
        }
    }

    class InnerBuilder
            extends UpdateManagerBuilder {

        private AnalysisReporter analysisReporter;

        public InnerBuilder( final CheckRunner checkRunner ) {
            super( checkRunner );
        }

        private DecisionTableAnalyzer build() {
            return new DecisionTableAnalyzer( getAnalysisReporter(),
                                              model,
                                              buildCache(),
                                              buildUpdateManager(),
                                              checkRunner );
        }

        protected AnalysisReporter getAnalysisReporter() {
            if ( analysisReporter == null ) {
                analysisReporter = new AnalysisReporter( placeRequest,
                                                         analysisReportScreen );
            }
            return analysisReporter;
        }
    }
}
