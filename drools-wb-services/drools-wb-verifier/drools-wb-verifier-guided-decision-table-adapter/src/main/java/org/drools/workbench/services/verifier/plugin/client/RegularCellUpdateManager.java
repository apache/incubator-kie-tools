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
package org.drools.workbench.services.verifier.plugin.client;

import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.builders.ActionBuilder;
import org.drools.workbench.services.verifier.plugin.client.builders.TypeResolver;
import org.drools.workbench.services.verifier.plugin.client.builders.Utils;
import org.drools.workbench.services.verifier.plugin.client.builders.ValueResolveException;
import org.drools.workbench.services.verifier.plugin.client.builders.ValuesResolver;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public class RegularCellUpdateManager
        extends CellUpdateManagerBase {

    private final AnalyzerConfiguration configuration;

    public RegularCellUpdateManager(final Index index,
                                    final AnalyzerConfiguration configuration,
                                    final GuidedDecisionTable52 model,
                                    final Coordinate coordinate) {
        super(index,
              model,
              coordinate);
        this.configuration = configuration;
    }

    @Override
    protected boolean updateAction(final Action action) {
        final Values oldValues = action.getValues();

        final Values values = getValue(model.getData()
                                               .get(coordinate.getRow())
                                               .get(coordinate.getCol()));

        if (values.isThereChanges(oldValues)) {
            action.setValue(values);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean updateCondition(final Condition condition) {
        final Values oldValues = condition.getValues();

        Values values = null;

        final DTCellValue52 cell = model.getData()
                .get(coordinate.getRow())
                .get(coordinate.getCol());
        final BaseColumn baseColumn = model.getExpandedColumns()
                .get(coordinate.getCol());

        if (baseColumn instanceof ConditionCol52 && condition instanceof FieldCondition) {

            final DTCellValue52 realCellValue = getRealCellValue((ConditionCol52) baseColumn,
                                                                 cell);

            final Optional<String> operatorFromCell = Utils.findOperatorFromCell(realCellValue);
            if (operatorFromCell.isPresent()) {
                ((FieldCondition) condition).setOperator(operatorFromCell.get());
            }

            values = useResolver(configuration,
                                 (FieldCondition) condition,
                                 realCellValue,
                                 (ConditionCol52) baseColumn);
        } else {
            values = getValue(cell);
        }

        if (values == null && oldValues == null) {
            return false;
        } else if (values == null || oldValues == null) {
            condition.setValue(values);
            return true;
        } else if (values.isThereChanges(oldValues)) {
            condition.setValue(values);
            return true;
        } else {
            return false;
        }
    }

    private Values useResolver(final AnalyzerConfiguration configuration,
                               final FieldCondition condition,
                               final DTCellValue52 realCellValue,
                               final ConditionCol52 baseColumn) {
        try {
            return new ValuesResolver(configuration,
                                      new TypeResolver() {
                                          @Override
                                          public String getType(final Optional<String> operatorFromCell) throws ValueResolveException {
                                              return condition.getField().getFieldType();
                                          }
                                      },
                                      baseColumn,
                                      realCellValue).getValues();
        } catch (ValueResolveException e) {
            return getValue(realCellValue);
        }
    }

    private Values getValue(final DTCellValue52 cell) {
        final Comparable value = ActionBuilder.getValue(cell);
        if (value == null) {
            return new Values();
        } else if (value instanceof String && ((String) value).isEmpty()) {
            return new Values();
        } else {
            final Values values = new Values();
            values.add(value);
            return values;
        }
    }
}
