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

import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.kie.soup.commons.validation.PortablePreconditions;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public class CellBuilder {

    private final BuilderFactory builderFactory;
    private int columnIndex;
    private BaseColumn baseColumn;
    private Rule rule;
    private List<DTCellValue52> row;

    public CellBuilder(final BuilderFactory builderFactory) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
    }

    public void build() throws
            BuildException {
        if (baseColumn instanceof ActionCol52) {

            final Action action = builderFactory.getActionBuilder()
                    .with(rule)
                    .with(row)
                    .with(columnIndex)
                    .with((ActionCol52) baseColumn)
                    .build();
            rule.getActions()
                    .add(action);
        } else if (baseColumn instanceof ConditionCol52) {

            if (baseColumn instanceof BRLConditionVariableColumn) {
                final Condition condition = builderFactory.getBRLConditionBuilder()
                        .with((BRLConditionVariableColumn) baseColumn)
                        .with(getRealCellValue((BRLConditionVariableColumn) baseColumn,
                                               row.get(columnIndex)))
                        .with(columnIndex)
                        .build();

                rule.getConditions()
                        .add(condition);
            } else {
                final Condition condition = builderFactory.getFieldConditionsBuilder()
                        .with(resolvePattern(rule))
                        .with((ConditionCol52) baseColumn)
                        .with(getRealCellValue((ConditionCol52) baseColumn,
                                               row.get(columnIndex)))
                        .with(columnIndex)
                        .build();

                rule.getConditions()
                        .add(condition);
            }
        }
    }

    private Pattern resolvePattern(final Rule rule) {
        return builderFactory.getPatternResolver()
                .with(rule)
                .with(columnIndex)
                .resolve();
    }

    public CellBuilder with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public CellBuilder with(final List<DTCellValue52> row) {
        this.row = row;
        return this;
    }

    public CellBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public CellBuilder with(final BaseColumn baseColumn) {
        this.baseColumn = baseColumn;
        return this;
    }
}
