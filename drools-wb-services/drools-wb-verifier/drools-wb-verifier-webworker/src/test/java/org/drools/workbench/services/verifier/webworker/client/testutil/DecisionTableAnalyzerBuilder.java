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

package org.drools.workbench.services.verifier.webworker.client.testutil;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.drools.workbench.services.verifier.plugin.client.builders.BuildException;
import org.drools.workbench.services.verifier.plugin.client.builders.IndexBuilder;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.drools.workbench.services.verifier.plugin.client.builders.VerifierColumnUtilities;
import org.drools.workbench.services.verifier.webworker.client.DTableUpdateManager;
import org.kie.workbench.common.services.verifier.api.client.checks.GWTCheckRunner;

import static org.mockito.Mockito.mock;

public class DecisionTableAnalyzerBuilder {

    protected GuidedDecisionTable52 model;
    protected Analyzer analyzer;
    private AnalyzerConfiguration configuration;
    private FactTypes factTypes;
    private VerifierColumnUtilities columnUtilities;
    private Index index;

    public DecisionTableAnalyzerBuilder withFieldTypes(final FactTypes factTypes) {
        this.factTypes = factTypes;
        return this;
    }

    public DecisionTableAnalyzerBuilder withAnalyzer(final Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public DecisionTableAnalyzerBuilder withModel(final GuidedDecisionTable52 model) {
        this.model = model;
        return this;
    }

    public Analyzer build() {
        return getInnerBuilder().build();
    }

    protected InnerBuilder getInnerBuilder() {
        return new InnerBuilder(new AnalyzerConfiguration(
                "UUID",
                new DateTimeFormatProviderMock(),
                new UUIDKeyProviderMock(),
                CheckConfiguration.newDefault(),
                new GWTCheckRunner()));
    }

    public UpdateManagerBuilder getUpdateManagerBuilder() {
        return new UpdateManagerBuilder();
    }

    public DecisionTableAnalyzerBuilder withConfiguration(final AnalyzerConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public DecisionTableAnalyzerBuilder withIndex(final Index index) {
        this.index = index;
        return this;
    }

    public class CacheBuilder {

        protected Index getIndex() {
            if (index == null) {
                try {
                    index = new IndexBuilder(model,
                                             new ModelMetaDataEnhancer(model).getHeaderMetaData(),
                                             getUtils(),
                                             configuration).build();
                } catch (final BuildException buildException) {
                    buildException.printStackTrace();
                }
            }
            return index;
        }

        protected VerifierColumnUtilities getUtils() {
            if (columnUtilities == null) {
                columnUtilities = new VerifierColumnUtilities(model,
                                                              new ModelMetaDataEnhancer(model).getHeaderMetaData(),
                                                              factTypes);
            }
            return columnUtilities;
        }
    }

    public class UpdateManagerBuilder
            extends CacheBuilder {

        protected DTableUpdateManager updateManager;

        public DTableUpdateManager buildUpdateManager() {
            if (this.updateManager == null) {
                this.updateManager = new DTableUpdateManager(getIndex(),
                                                             analyzer,
                                                             configuration);
            }
            return this.updateManager;
        }
    }

    public class InnerBuilder
            extends UpdateManagerBuilder {

        private Reporter analysisReporter;

        public InnerBuilder(final AnalyzerConfiguration configuration) {
            DecisionTableAnalyzerBuilder.this.configuration = configuration;
        }

        private Analyzer build() {
            analyzer = new Analyzer(getAnalysisReporter(),
                                    getIndex(),
                                    configuration);

            return analyzer;
        }

        protected Reporter getAnalysisReporter() {
            if (analysisReporter == null) {
                analysisReporter = mock(Reporter.class);
            }
            return analysisReporter;
        }
    }
}