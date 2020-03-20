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

package org.drools.workbench.services.verifier.plugin.client.testutil;

import java.util.HashSet;
import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.DTableUpdateManager;
import org.drools.workbench.services.verifier.plugin.client.api.FactTypes;
import org.kie.soup.project.datamodel.oracle.DataType;

public class AnalyzerProvider {

    private Set<Issue> analysisReport;
    private Status status;

    private Analyzer analyzer = null;
    private AnalyzerConfiguration configuration = new AnalyzerConfigurationMock();
    private DTableUpdateManager updateManager;
    private FactTypes factTypes = new FactTypes();
    private DecisionTableAnalyzerBuilder decisionTableAnalyzerBuilder;

    public AnalyzerProvider() {

        factTypes.add(new FactTypes.FactType("Applicant",
                                             new HashSet<FactTypes.Field>() {{
                                                 add(new FactTypes.Field("age",
                                                                         DataType.TYPE_NUMERIC_INTEGER));
                                             }}));

        factTypes.add(new FactTypes.FactType("Account",
                                             new HashSet<FactTypes.Field>() {{
                                                 add(new FactTypes.Field("deposit",
                                                                         DataType.TYPE_NUMERIC_DOUBLE));
                                             }}));

        factTypes.add(new FactTypes.FactType("Person",
                                             new HashSet<FactTypes.Field>() {{
                                                 add(new FactTypes.Field("age",
                                                                         DataType.TYPE_NUMERIC_INTEGER));
                                                 add(new FactTypes.Field("name",
                                                                         DataType.TYPE_STRING));
                                                 add(new FactTypes.Field("lastName",
                                                                         DataType.TYPE_STRING));
                                                 add(new FactTypes.Field("description",
                                                                         DataType.TYPE_STRING));
                                                 add(new FactTypes.Field("approved",
                                                                         DataType.TYPE_BOOLEAN));
                                                 add(new FactTypes.Field("salary",
                                                                         DataType.TYPE_NUMERIC_INTEGER));
                                             }}));
    }

    public FactTypes getFactTypes() {
        return factTypes;
    }

    public Set<Issue> getAnalysisReport() {
        return analysisReport;
    }

    public Status getStatus() {
        return status;
    }

    public Analyzer getAnalyser(final GuidedDecisionTable52 table52) {

        final DecisionTableAnalyzerBuilder builder = getDecisionTableAnalyzerBuilder()
                .withFieldTypes(factTypes)
                .withAnalyzer(analyzer)
                .withModel(table52);

        return builder.build();
    }

    public DTableUpdateManager getUpdateManager(final GuidedDecisionTable52 table52,
                                                final Analyzer analyzer) {
        updateManager = getDecisionTableAnalyzerBuilder()
                .withFieldTypes(factTypes)
                .withModel(table52)
                .withAnalyzer(analyzer)
                .withConfiguration(configuration)
                .getUpdateManagerBuilder()
                .buildUpdateManager();
        return updateManager;
    }

    private DecisionTableAnalyzerBuilder getDecisionTableAnalyzerBuilder() {
        if (decisionTableAnalyzerBuilder == null) {
            decisionTableAnalyzerBuilder = new DecisionTableAnalyzerBuilder() {
                @Override
                protected InnerBuilder getInnerBuilder() {
                    return new InnerBuilder(configuration) {
                        @Override
                        protected Reporter getAnalysisReporter() {
                            return AnalyzerProvider.this.getAnalysisReporter();
                        }
                    };
                }
            };
        }
        return decisionTableAnalyzerBuilder;
    }

    private Reporter getAnalysisReporter() {
        return new Reporter() {

            @Override
            public void sendReport(final Set<Issue> issues) {
                analysisReport = issues;
            }

            @Override
            public void sendStatus(final Status _status) {
                status = _status;
            }
        };
    }

    public AnalyzerBuilder makeAnalyser() {
        return new AnalyzerBuilder(this);
    }

    public Analyzer makeAnalyser(final GuidedDecisionTable52 table52) {
        return getAnalyser(table52);
    }

    public void setConfiguration(final AnalyzerConfiguration analyzerConfiguration) {
        this.configuration = analyzerConfiguration;
    }
}