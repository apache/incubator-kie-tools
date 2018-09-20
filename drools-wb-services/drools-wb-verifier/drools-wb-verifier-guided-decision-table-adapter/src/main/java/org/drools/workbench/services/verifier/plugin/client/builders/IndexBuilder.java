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
import org.drools.verifier.core.index.IndexImpl;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class IndexBuilder {

    private final IndexImpl index;
    private final GuidedDecisionTable52 model;
    private final BuilderFactory builderFactory;

    public IndexBuilder(final GuidedDecisionTable52 model,
                        final HeaderMetaData headerMetaData,
                        final VerifierColumnUtilities utils,
                        final AnalyzerConfiguration configuration) {
        this.index = new IndexImpl();

        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);

        this.builderFactory = new BuilderFactory(utils,
                                                 index,
                                                 model,
                                                 headerMetaData,
                                                 configuration);
    }

    public Index build() throws
            BuildException {

        buildColumns();
        buildRules();

        return this.index;
    }

    private void buildColumns() {
        for (int columnIndex = 0; columnIndex < model.getExpandedColumns()
                .size(); columnIndex++) {
            this.index.getColumns()
                    .add(builderFactory.getColumnBuilder()
                                 .with(columnIndex)
                                 .build());
        }
    }

    private void buildRules() throws
            BuildException {

        int size = model.getData()
                .size();

        for (int index = 0; index < size; index++) {
            this.index.getRules()
                    .add(builderFactory.getRuleBuilder()
                                 .with(index)
                                 .build());
        }
    }
}
