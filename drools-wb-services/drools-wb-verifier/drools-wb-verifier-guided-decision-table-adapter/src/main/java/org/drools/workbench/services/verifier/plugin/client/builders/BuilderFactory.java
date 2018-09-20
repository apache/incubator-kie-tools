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

package org.drools.workbench.services.verifier.plugin.client.builders;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class BuilderFactory {

    private final VerifierColumnUtilities utils;
    private final Index index;
    private final GuidedDecisionTable52 model;
    private HeaderMetaData headerMetaData;
    private final AnalyzerConfiguration configuration;

    public BuilderFactory(final VerifierColumnUtilities utils,
                          final Index index,
                          final GuidedDecisionTable52 model,
                          final HeaderMetaData headerMetaData,
                          final AnalyzerConfiguration configuration) {

        this.utils = PortablePreconditions.checkNotNull("utils",
                                                        utils);
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.headerMetaData = PortablePreconditions.checkNotNull("headerMetaData",
                                                                 headerMetaData);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public CellBuilder getCellBuilder() {
        return new CellBuilder(this);
    }

    public RuleBuilder getRuleBuilder() {
        return new RuleBuilder(this,
                               model,
                               configuration);
    }

    public ColumnBuilder getColumnBuilder() {
        return new ColumnBuilder(configuration);
    }

    public ActionBuilder getActionBuilder() {
        return new ActionBuilder(this,
                                 index,
                                 configuration);
    }

    public BRLConditionBuilder getBRLConditionBuilder() {
        return new BRLConditionBuilder(index,
                                       utils,
                                       configuration);
    }

    public FieldConditionBuilder getFieldConditionsBuilder() {
        return new FieldConditionBuilder(this,
                                         index,
                                         utils,
                                         configuration);
    }

    public PatternResolver getPatternResolver() {
        return new PatternResolver(index,
                                   headerMetaData,
                                   configuration);
    }

    public FieldResolver getFieldResolver() {
        return new FieldResolver(this,
                                 headerMetaData,
                                 configuration);
    }

    public ConditionBuilder getConditionBuilder() {
        return new ConditionBuilder(this);
    }
}
