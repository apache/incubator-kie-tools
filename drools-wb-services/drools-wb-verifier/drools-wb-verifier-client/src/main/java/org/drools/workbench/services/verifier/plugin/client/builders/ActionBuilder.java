/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.BRLAction;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.FieldAction;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.RetractAction;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.api.client.index.WorkItemAction;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.DataType;

import static org.drools.workbench.services.verifier.plugin.client.builders.Utils.getRealCellValue;

public class ActionBuilder {

    private BuilderFactory builderFactory;
    private final Index index;
    private List<DTCellValue52> row;
    private ActionCol52 actionCol;
    private Rule rule;
    private AnalyzerConfiguration configuration;
    private int columnIndex;

    public ActionBuilder(final BuilderFactory builderFactory,
                         final Index index,
                         final AnalyzerConfiguration configuration) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public static Comparable getValue(final DTCellValue52 dtCellValue52) {
        switch (dtCellValue52.getDataType()) {
            case NUMERIC_BIGDECIMAL:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new BigDecimal(dtCellValue52.getNumericValue()
                                                  .toString());
                }
            case NUMERIC_BIGINTEGER:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new BigInteger(dtCellValue52.getNumericValue()
                                                  .toString());
                }
            case NUMERIC_BYTE:
                return new Byte(dtCellValue52.getStringValue());
            case NUMERIC_DOUBLE:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new Double(dtCellValue52.getNumericValue()
                                              .toString());
                }
            case NUMERIC_FLOAT:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new Float(dtCellValue52.getNumericValue()
                                             .toString());
                }
            case NUMERIC_INTEGER:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new Integer(dtCellValue52.getNumericValue()
                                               .toString());
                }
            case NUMERIC_LONG:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new Long(dtCellValue52.getNumericValue()
                                            .toString());
                }
            case NUMERIC_SHORT:
                if (dtCellValue52.getNumericValue() == null) {
                    return null;
                } else {
                    return new Short(dtCellValue52.getNumericValue()
                                             .toString());
                }
            case DATE:
                return dtCellValue52.getDateValue();
            case BOOLEAN:
                return dtCellValue52.getBooleanValue();
            case STRING:
            case NUMERIC:
            default:
                final String stringValue = dtCellValue52.getStringValue();
                if (stringValue == null) {
                    return null;
                } else if (stringValue.isEmpty()) {
                    return null;
                } else {
                    return stringValue;
                }
        }
    }

    public Action build() throws
            BuildException {
        if (actionCol instanceof BRLActionVariableColumn) {
            return addBRLAction();
        } else if (actionCol instanceof ActionRetractFactCol52) {
            return addRetractAction();
        } else if (actionCol instanceof ActionInsertFactCol52) {
            return addInsertFactAction((ActionInsertFactCol52) actionCol);
        } else if (actionCol instanceof ActionWorkItemCol52) {
            return addWorkItem();
        } else {
            return addAction(actionCol,
                             row.get(columnIndex));
        }
    }

    private Action addInsertFactAction(final ActionInsertFactCol52 actionCol) throws
            BuildException {

        builderFactory.getPatternResolver()
                .with(rule)
                .with(columnIndex)
                .resolve();

        return addAction(actionCol,
                         row.get(columnIndex));
    }

    private Action addWorkItem() {
        return new WorkItemAction(getColumn(),
                                  getValues(row.get(columnIndex)),
                                  configuration);
    }

    private Action addRetractAction() {
        return new RetractAction(getColumn(),
                                 getValues(row.get(columnIndex)),
                                 configuration);
    }

    private Action addBRLAction() {
        return new BRLAction(getColumn(),
                             getValues(row.get(columnIndex)),
                             configuration);
    }

    private Action addAction(final ActionCol52 actionCol,
                             final DTCellValue52 visibleCellValue) throws
            BuildException {
        final Field field = resolveField(actionCol);
        final Action action = buildAction(field,
                                          visibleCellValue);
        field.getActions()
                .add(action);
        return action;
    }

    private Field resolveField(final ActionCol52 actionCol) throws
            BuildException {
        return builderFactory.getFieldResolver()
                .with(rule)
                .with(actionCol)
                .with(columnIndex)
                .resolve();
    }

    private Action buildAction(final Field field,
                               final DTCellValue52 visibleCellValue) {

        return new FieldAction(field,
                               getColumn(),
                               convert(visibleCellValue.getDataType()),
                               getValues(visibleCellValue),
                               configuration);
    }

    private org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes convert(final DataType.DataTypes dataType) {
        switch (dataType) {
            case STRING:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.STRING;
            case NUMERIC:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC;
            case NUMERIC_BIGDECIMAL:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_BIGDECIMAL;
            case NUMERIC_BIGINTEGER:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_BIGINTEGER;
            case NUMERIC_BYTE:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_BYTE;
            case NUMERIC_DOUBLE:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_DOUBLE;
            case NUMERIC_FLOAT:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_FLOAT;
            case NUMERIC_INTEGER:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_INTEGER;
            case NUMERIC_LONG:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_LONG;
            case NUMERIC_SHORT:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.NUMERIC_SHORT;
            case DATE:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.DATE;
            case BOOLEAN:
                return org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes.BOOLEAN;
            default:
                return null;
        }
    }

    private Values getValues(final DTCellValue52 visibleCellValue) {
        final Comparable value = getValue(getRealCellValue(actionCol,
                                                           visibleCellValue));
        if (value == null) {
            return new Values<>();
        } else {
            return new Values(value);
        }
    }

    private Column getColumn() {

        return index.getColumns()
                .where(Column.index()
                               .is(columnIndex))
                .select()
                .first();
    }

    //TODO: Inconsistent, should return action and the caller adds into the rule
    public ActionBuilder with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public ActionBuilder with(final List<DTCellValue52> row) {
        this.row = row;
        return this;
    }

    public ActionBuilder with(final ActionCol52 actionCol) {
        this.actionCol = actionCol;
        return this;
    }

    public ActionBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
