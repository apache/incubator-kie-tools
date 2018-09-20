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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.verifier.core.index.keys.Values;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.services.verifier.plugin.client.Logger;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.DataType;

public class ValuesResolver {

    private final VerifierColumnUtilities utils;
    private final int columnIndex;
    private final ConditionCol52 conditionColumn;
    private final DTCellValue52 realCellValue;

    public ValuesResolver(final VerifierColumnUtilities utils,
                          final int columnIndex,
                          final ConditionCol52 conditionColumn,
                          final DTCellValue52 realCellValue) {
        this.utils = PortablePreconditions.checkNotNull("utils",
                                                        utils);
        this.columnIndex = PortablePreconditions.checkNotNull("columnIndex",
                                                              columnIndex);
        this.conditionColumn = PortablePreconditions.checkNotNull("conditionColumn",
                                                                  conditionColumn);
        this.realCellValue = PortablePreconditions.checkNotNull("realCellValue",
                                                                realCellValue);
    }

    public Values getValues() throws
            ValueResolveException {
        final String type = getType();

        final Values values = getValues(type);

        Logger.add("ConditionCol: " + ToString.toString(conditionColumn)
                           + " realCellValue: " + ToString.toString(realCellValue)
                           + " type: " + type + " values: " + values.toString());

        return values;
    }

    private Values getValues(final String type) throws
            ValueResolveException {
        if (isTypeGuvnorEnum()) {
            // Guvnor enum
            return getStringValue();
        } else if (type == null) {
            // type null means the field is free-format
            return getStringValue();
        } else if (type.equals(DataType.TYPE_STRING)) {
            return getStringValue();
        } else if (type.equals(DataType.TYPE_NUMERIC) || type.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
            return getBigDecimalValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
            return getBigIntegerValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_BYTE)) {
            return getNumericValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
            return getDoubleValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_FLOAT)) {
            return getNumericValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_INTEGER)) {
            return getIntegerValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_LONG)) {
            return getLongValue();
        } else if (type.equals(DataType.TYPE_NUMERIC_SHORT)) {
            return getShortValue();
        } else if (type.equals(DataType.TYPE_BOOLEAN)) {
            return getBooleanValue();
        } else if (type.equals(DataType.TYPE_DATE)) {
            return getDateValue();
        } else if (type.equals(DataType.TYPE_COMPARABLE)) {
            return getStringValue();
        } else {
            return getStringValue();
        }
    }

    private String getType() throws
            ValueResolveException {
        try {
            Logger.add("Getting type from utils");

            final String type = utils.getType(conditionColumn,
                                              columnIndex);

            Logger.add("Resolver type from utils: " + type);

            return type;
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to get type for " + ToString.toString(conditionColumn) + " columnIndex: " + columnIndex,
                                            e);
        }
    }

    private Values<Boolean> getBooleanValue() throws
            ValueResolveException {
        try {

            final Boolean booleanValue = realCellValue.getBooleanValue();

            if (booleanValue != null) {
                return new Values<>(booleanValue);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Boolean Value");
        }
    }

    private Values<Date> getDateValue() throws
            ValueResolveException {
        try {

            final Date date = realCellValue.getDateValue();
            if (date != null) {
                return new Values<>(date);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Date Value");
        }
    }

    private Values getNumericValue() throws
            ValueResolveException {
        try {
            if (realCellValue.getNumericValue() != null) {
                return new Values((Comparable) realCellValue.getNumericValue());
            } else {
                return new Values();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Numeric Value");
        }
    }

    private Values<String> getStringValue() throws
            ValueResolveException {
        try {

            final String stringValue = realCellValue.getStringValue();

            if (stringValue != null && !stringValue.isEmpty()) {
                if (conditionColumn.getOperator() != null
                        && (conditionColumn.getOperator()
                        .equals("in") || conditionColumn.getOperator()
                        .equals("not in"))) {
                    final Values values = new Values();

                    for (final String item : stringValue.split(",")) {
                        values.add(item.trim());
                    }

                    return values;
                } else {
                    return new Values<>(stringValue);
                }
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve String Value");
        }
    }

    private Values<Short> getShortValue() throws
            ValueResolveException {
        try {

            final Short aShort = getShort();
            if (aShort != null) {
                return new Values<>(aShort);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Short Value");
        }
    }

    private Short getShort() {
        if (realCellValue.getNumericValue() != null) {
            return (Short) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new Short(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private Values<Long> getLongValue() throws
            ValueResolveException {
        try {

            final Long aLong = getLong();
            if (aLong != null) {
                return new Values<>(aLong);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Long value");
        }
    }

    private Long getLong() {
        if (realCellValue.getNumericValue() != null) {
            return (Long) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new Long(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private Values<Double> getDoubleValue() {
        final Double aDouble = getDouble();
        if (aDouble != null) {
            return new Values<>(aDouble);
        } else {
            return new Values<>();
        }
    }

    private Double getDouble() {
        if (realCellValue.getNumericValue() != null) {
            return (Double) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new Double(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private Values<BigInteger> getBigIntegerValue() throws
            ValueResolveException {
        try {

            final BigInteger bigInteger = getBigInteger();
            if (bigInteger != null) {
                return new Values<>(bigInteger);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolved Big Integer");
        }
    }

    private BigInteger getBigInteger() {
        if (realCellValue.getNumericValue() != null) {
            return (BigInteger) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new BigInteger(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private Values<BigDecimal> getBigDecimalValue() throws
            ValueResolveException {
        try {
            final BigDecimal bigDecimal = getBigDecimal();
            if (bigDecimal != null) {
                return new Values<>(bigDecimal);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Big Decimal.");
        }
    }

    private BigDecimal getBigDecimal() {
        if (realCellValue.getNumericValue() != null) {
            return (BigDecimal) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new BigDecimal(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private Values<Integer> getIntegerValue() throws
            ValueResolveException {
        try {
            final Integer integer = getInteger();
            if (integer != null) {
                return new Values<>(integer);
            } else {
                return new Values<>();
            }
        } catch (final Exception e) {
            throw new ValueResolveException("Failed to resolve Integer Value");
        }
    }

    private Integer getInteger() {
        if (realCellValue.getNumericValue() != null) {
            return (Integer) realCellValue.getNumericValue();
        } else if (realCellValue.getStringValue() == null || realCellValue.getStringValue()
                .isEmpty()) {
            return null;
        } else {
            try {
                return new Integer(realCellValue.getStringValue());
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
    }

    private boolean isTypeGuvnorEnum() {
        return conditionColumn != null &&
                conditionColumn.getValueList() != null &&
                conditionColumn.getValueList().split(",").length != 0;
    }
}
