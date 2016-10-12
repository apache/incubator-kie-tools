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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.DTableUpdateManager;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.DtableRuleInspectorCache;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.IndexBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel.AnalysisReportScreen;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.services.verifier.api.client.checks.base.CheckRunner;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.configuration.DateTimeFormatProvider;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKeyProvider;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.api.shapes.UUID;
import org.uberfire.mvp.PlaceRequest;

public class DecisionTableAnalyzerBuilder {

    protected PlaceRequest placeRequest;
    protected AsyncPackageDataModelOracle oracle;
    protected GuidedDecisionTable52 model;
    protected AnalysisReportScreen analysisReportScreen;

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
        PortablePreconditions.checkNotNull( "placeRequest",
                                            placeRequest );
        PortablePreconditions.checkNotNull( "oracle",
                                            oracle );
        PortablePreconditions.checkNotNull( "model",
                                            model );
        PortablePreconditions.checkNotNull( "analysisReportScreen",
                                            analysisReportScreen );

        return getInnerBuilder().build();
    }

    protected InnerBuilder getInnerBuilder() {
        return new InnerBuilder( new CheckRunner() );
    }

    public CacheBuilder getCacheBuilder() {
        return new CacheBuilder();
    }

    public UpdateManagerBuilder getUpdateManagerBuilder( final CheckRunner checkRunner ) {
        return new UpdateManagerBuilder( checkRunner );
    }

    public class CacheBuilder {
        private final AnalyzerConfiguration configuration;
        private DtableRuleInspectorCache cache;
        private ColumnUtilities columnUtilities;
        private Index index;

        public CacheBuilder() {
            configuration = new AnalyzerConfiguration( new DateTimeFormatProvider() {
                @Override
                public String format( final Date dateValue ) {
                    return DateTimeFormat.getFormat( ApplicationPreferences.getDroolsDateFormat() )
                            .format( dateValue );
                }
            },
                                                       new UUIDKeyProvider() {
                                                           @Override
                                                           protected String newUUID() {
                                                               return UUID.uuid();
                                                           }
                                                       } );
        }

        public DtableRuleInspectorCache buildCache() {
            if ( cache == null ) {
                cache = new DtableRuleInspectorCache( getUtils(),
                                                      model,
                                                      getIndex(),
                                                      configuration );
            }
            return cache;
        }

        protected Index getIndex() {
            if ( index == null ) {
                index = new IndexBuilder( model,
                                          getUtils(),
                                          configuration ).build();
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

    public class UpdateManagerBuilder
            extends CacheBuilder {

        protected final CheckRunner checkRunner;
        protected DTableUpdateManager updateManager;


        public UpdateManagerBuilder( final CheckRunner checkRunner ) {
            this.checkRunner = checkRunner;
        }

        public DTableUpdateManager buildUpdateManager() {
            if ( updateManager == null ) {
                updateManager = new DTableUpdateManager( getIndex(),
                                                         model,
                                                         buildCache(),
                                                         checkRunner );
            }
            return updateManager;
        }
    }

    public class InnerBuilder
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
