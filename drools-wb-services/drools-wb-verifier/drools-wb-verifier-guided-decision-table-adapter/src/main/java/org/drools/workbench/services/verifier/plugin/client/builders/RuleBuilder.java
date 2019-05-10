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
import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.RuleAttribute;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.soup.commons.validation.PortablePreconditions;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public class RuleBuilder {

    private final BuilderFactory builderFactory;
    private final GuidedDecisionTable52 model;
    private final AnalyzerConfiguration configuration;
    private List<DTCellValue52> row;
    private Rule rule;

    public RuleBuilder(final BuilderFactory builderFactory,
                       final GuidedDecisionTable52 model,
                       final AnalyzerConfiguration configuration) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Rule build() throws
            BuildException {

        resolveColumns();

        return rule;
    }

    private void resolveColumns() throws
            BuildException {

        int columnIndex = 0;

        for (final BaseColumn baseColumn : model.getExpandedColumns()) {

            if (baseColumn instanceof AttributeCol52) {
                final String attribute = ((AttributeCol52) baseColumn).getAttribute();
                final DTCellValue52 realCellValue = getRealCellValue((AttributeCol52) baseColumn,
                                                                     row.get(columnIndex));

                final Optional<RuleAttribute> ruleAttribute = AttributeBuilder.build(columnIndex,
                                                                                     configuration,
                                                                                     attribute,
                                                                                     realCellValue);
                if (ruleAttribute.isPresent()) {
                    rule.addRuleAttribute(ruleAttribute.get());
                }
            } else if (baseColumn instanceof ConditionCol52) {
                final Condition condition = builderFactory.getConditionBuilder()
                        .with((ConditionCol52) baseColumn)
                        .with(rule)
                        .with(row)
                        .with(columnIndex)
                        .build();

                rule.getConditions()
                        .add(condition);
            } else if (baseColumn instanceof ActionCol52) {

                final Action action = builderFactory.getActionBuilder()
                        .with(rule)
                        .with((ActionCol52) baseColumn)
                        .with(row)
                        .with(columnIndex)
                        .build();

                rule.getActions()
                        .add(action);
            }

            columnIndex++;
        }
    }

    public RuleBuilder with(final int rowIndex) {

        this.row = model.getData()
                .get(rowIndex);
        this.rule = new Rule(rowIndex,
                             configuration);

        return this;
    }
}
