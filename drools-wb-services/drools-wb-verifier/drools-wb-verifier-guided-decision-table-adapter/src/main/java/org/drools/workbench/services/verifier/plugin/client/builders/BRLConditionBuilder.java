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
import org.drools.verifier.core.index.model.BRLCondition;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.kie.soup.commons.validation.PortablePreconditions;

public class BRLConditionBuilder {

    private final Index index;
    private final AnalyzerConfiguration configuration;
    private final VerifierColumnUtilities utils;
    private BRLConditionVariableColumn conditionColumn;
    private DTCellValue52 realCellValue;
    private int columnIndex;

    public BRLConditionBuilder(final Index index,
                               final VerifierColumnUtilities utils,
                               final AnalyzerConfiguration configuration) {
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.utils = PortablePreconditions.checkNotNull("utils",
                                                        utils);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Condition build() throws
            BuildException {
        PortablePreconditions.checkNotNull("realCellValue",
                                           realCellValue);
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);

        try {
            return new BRLCondition(getColumn(),
                                    new ValuesResolver(configuration,
                                                       new UtilsTypeResolver(utils,
                                                                             columnIndex,
                                                                             conditionColumn),
                                                       conditionColumn,
                                                       realCellValue).getValues(),
                                    configuration);
        } catch (final ValueResolveException e) {
            throw new BuildException("Could not build BRLCondition because of: " + e.getMessage());
        }
    }

    private Column getColumn() {

        return index.getColumns()
                .where(Column.index()
                               .is(columnIndex))

                .select()
                .first();
    }

    public BRLConditionBuilder with(final BRLConditionVariableColumn conditionColumn) {
        this.conditionColumn = conditionColumn;
        return this;
    }

    public BRLConditionBuilder with(final DTCellValue52 realCellValue) {
        this.realCellValue = realCellValue;
        return this;
    }

    public BRLConditionBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
