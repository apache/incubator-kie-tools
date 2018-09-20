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

import java.util.List;

import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.kie.soup.commons.validation.PortablePreconditions;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public class ConditionBuilder {

    private final BuilderFactory builderFactory;

    private ConditionCol52 conditionCol52;
    private List<DTCellValue52> row;
    private int columnIndex;
    private Rule rule;

    public ConditionBuilder(final BuilderFactory builderFactory) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
    }

    public ConditionBuilder with(final ConditionCol52 conditionCol52) {
        this.conditionCol52 = conditionCol52;
        return this;
    }

    public ConditionBuilder with(final List<DTCellValue52> row) {
        this.row = row;
        return this;
    }

    public ConditionBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public ConditionBuilder with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public Condition build() throws
            BuildException {
        PortablePreconditions.checkNotNull("conditionCol52",
                                           conditionCol52);
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);
        PortablePreconditions.checkNotNull("row",
                                           row);

        if (conditionCol52 instanceof BRLConditionVariableColumn) {

            return builderFactory.getBRLConditionBuilder()
                    .with((BRLConditionVariableColumn) conditionCol52)
                    .with(row.get(columnIndex))
                    .with(columnIndex)
                    .build();
        } else {

            PortablePreconditions.checkNotNull("rule",
                                               rule);

            final Pattern pattern = builderFactory.getPatternResolver()
                    .with(rule)
                    .with(columnIndex)
                    .resolve();

            return builderFactory.getFieldConditionsBuilder()
                    .with(pattern)
                    .with(conditionCol52)
                    .with(getRealCellValue(conditionCol52,
                                           row.get(columnIndex)))
                    .with(columnIndex)
                    .build();
        }
    }
}
