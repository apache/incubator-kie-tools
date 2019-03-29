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

import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.services.verifier.plugin.client.util.NullEqualityOperator;
import org.kie.soup.commons.validation.PortablePreconditions;

public class FieldConditionBuilder {

    private final BuilderFactory builderFactory;
    private final Index index;
    private final VerifierColumnUtilities utils;
    private final AnalyzerConfiguration configuration;
    private Pattern pattern;
    private ConditionCol52 conditionCol52;
    private DTCellValue52 realCellValue;
    private int columnIndex;

    public FieldConditionBuilder(final BuilderFactory builderFactory,
                                 final Index index,
                                 final VerifierColumnUtilities utils,
                                 final AnalyzerConfiguration configuration) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.utils = PortablePreconditions.checkNotNull("utils",
                                                        utils);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Condition build() throws
            BuildException {

        PortablePreconditions.checkNotNull("conditionCol52",
                                           conditionCol52);
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);

        try {
            final Field field = resolveField();

            final Condition condition = buildCondition(field);
            field.getConditions()
                    .add(condition);
            return condition;
        } catch (final BuildException buildException) {
            throw buildException;
        } catch (final Exception e) {
            throw new BuildException("Failed to build " + pattern.getName() + " # " + ToString.toString(conditionCol52));
        }
    }

    private Field resolveField() throws
            BuildException {
        try {
            return builderFactory.getFieldResolver()
                    .with(pattern)
                    .with(columnIndex)
                    .with(conditionCol52)
                    .resolve();
        } catch (final Exception e) {
            throw new BuildException("Failed to resolve field " + pattern.getName() + " # " + ToString.toString(conditionCol52));
        }
    }

    private Condition buildCondition(final Field field) throws
            BuildException {
        try {

            final Column column = getColumn();

            return new FieldCondition(field,
                                      column,
                                      resolveOperator(),
                                      resolveValues(),
                                      configuration);
        } catch (final BuildException e) {
            throw e;
        } catch (final Exception e) {
            throw new BuildException("Failed to build FieldCondition ");
        }
    }

    private Values resolveValues() throws
            BuildException {

        if (NullEqualityOperator.contains(conditionCol52.getOperator())) {
            if (realCellValue.getBooleanValue() != null && realCellValue.getBooleanValue()) {
                return Values.nullValue();
            } else {
                return new Values();
            }
        } else {
            try {
                Values values = new ValuesResolver(configuration,
                                                   new UtilsTypeResolver(utils,
                                                                         columnIndex,
                                                                         conditionCol52),
                                                   conditionCol52,
                                                   realCellValue).getValues();
                return values;
            } catch (final Exception e) {
                throw new BuildException("Failed to resolve values:" + ToString.toString(conditionCol52) + " " + ToString.toString(realCellValue) + e.getMessage());
            }
        }
    }

    private String resolveOperator() {
        if (conditionCol52.getOperator() == null || conditionCol52.getOperator().trim().isEmpty()) {

            final Optional<String> operatorFromCell = Utils.findOperatorFromCell(realCellValue);
            if (operatorFromCell.isPresent()) {
                return operatorFromCell.get();
            }
        } else if (NullEqualityOperator.contains(conditionCol52.getOperator())) {
            return NullEqualityOperator.resolveOperator(conditionCol52.getOperator());
        }

        return conditionCol52.getOperator();
    }

    private Column getColumn() throws
            BuildException {
        try {

            return index.getColumns()
                    .where(Column.index()
                                   .is(columnIndex))
                    .select()
                    .first();
        } catch (final Exception e) {
            throw new BuildException("Failed to find column ");
        }
    }

    public FieldConditionBuilder with(final Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public FieldConditionBuilder with(final ConditionCol52 conditionCol52) {
        this.conditionCol52 = PortablePreconditions.checkNotNull("conditionCol52",
                                                                 conditionCol52);
        return this;
    }

    public FieldConditionBuilder with(final DTCellValue52 realCellValue) {
        this.realCellValue = realCellValue;
        return this;
    }

    public FieldConditionBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
