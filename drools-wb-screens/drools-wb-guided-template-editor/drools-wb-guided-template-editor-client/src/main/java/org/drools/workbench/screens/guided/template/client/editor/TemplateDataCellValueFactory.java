/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.client.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DateConverter;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellValueFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;

/**
 * A Factory to create CellValues applicable to given columns.
 */
public class TemplateDataCellValueFactory
        extends AbstractCellValueFactory<TemplateDataColumn, String> {

    // Dates are serialised to Strings with the user-defined format, or dd-MMM-yyyy by default
    protected static DateConverter DATE_CONVERTOR = null;
    //Template model
    private TemplateModel model;

    /**
     * Construct a Cell Value Factory for a specific Template data editor
     * @param model for which cells will be created
     * @param oracle SuggestionCompletionEngine to assist with drop-downs
     */
    public TemplateDataCellValueFactory(final TemplateModel model,
                                        final AsyncPackageDataModelOracle oracle) {
        super(oracle);
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }
        this.model = model;
    }

    /**
     * Override the default, GWT-centric, Date conversion utility class. Only
     * use to hook-in a JVM Compatible implementation for tests
     * @param dc
     */
    public static void injectDateConvertor(DateConverter dc) {
        DATE_CONVERTOR = dc;
    }

    /**
     * Construct a new row of data for the underlying model
     * @return
     */
    public List<String> makeRowData() {
        List<String> data = new ArrayList<String>();
        InterpolationVariable[] variables = model.getInterpolationVariablesList();
        for (InterpolationVariable var : variables) {
            TemplateDataColumn column = makeModelColumn(var);
            String dcv = makeModelCellValue(column);
            data.add(dcv);
        }
        return data;
    }

    /**
     * Construct a new row of data for the MergableGridWidget
     * @return
     */
    @Override
    public DynamicDataRow makeUIRowData() {
        DynamicDataRow data = new DynamicDataRow();
        InterpolationVariable[] variables = model.getInterpolationVariablesList();
        for (InterpolationVariable var : variables) {
            TemplateDataColumn column = makeModelColumn(var);
            String dcv = makeModelCellValue(column);
            CellValue<? extends Comparable<?>> cell = convertModelCellValue(column,
                                                                            dcv);
            data.add(cell);
        }

        return data;
    }

    /**
     * Construct a new column of data for the underlying model
     * @return
     */
    public List<String> makeColumnData(TemplateDataColumn column) {
        List<String> data = new ArrayList<String>();
        for (int iRow = 0; iRow < model.getRowsCount(); iRow++) {
            String cell = makeModelCellValue(column);
            data.add(cell);
        }
        return data;
    }

    /**
     * Convert a column of domain data to that suitable for the UI
     * @param column
     * @param columnData
     * @return
     */
    public List<CellValue<? extends Comparable<?>>> convertColumnData(TemplateDataColumn column,
                                                                      List<String> columnData) {
        List<CellValue<? extends Comparable<?>>> data = new ArrayList<CellValue<? extends Comparable<?>>>();
        for (int iRow = 0; iRow < model.getRowsCount(); iRow++) {
            String dcv = columnData.get(iRow);
            CellValue<? extends Comparable<?>> cell = convertModelCellValue(column,
                                                                            dcv);
            data.add(cell);
        }
        return data;
    }

    /**
     * Make a Model cell for the given column
     * @param column
     * @return
     */
    @Override
    public String makeModelCellValue(TemplateDataColumn column) {
        DataType.DataTypes dataType = getDataType(column);
        switch (dataType) {
            case BOOLEAN:
                return Boolean.FALSE.toString();
        }

        return "";
    }

    /**
     * Convert a Model cell to one that can be used in the UI
     * @param column
     * @param dcv
     * @return
     */
    @Override
    public CellValue<? extends Comparable<?>> convertModelCellValue(TemplateDataColumn column,
                                                                    String dcv) {

        DataType.DataTypes dataType = getDataType(column);
        CellValue<? extends Comparable<?>> cell = null;

        switch (dataType) {
            case BOOLEAN:
                Boolean b = Boolean.FALSE;
                try {
                    b = Boolean.valueOf(dcv);
                } catch (Exception e) {
                }
                cell = makeNewBooleanCellValue(b);
                break;
            case DATE:
                Date d = null;
                try {
                    if (DATE_CONVERTOR == null) {
                        throw new IllegalArgumentException("DATE_CONVERTOR has not been initialised.");
                    }
                    d = DATE_CONVERTOR.parse(dcv);
                } catch (Exception e) {
                }
                cell = makeNewDateCellValue(d);
                break;
            case NUMERIC:
                BigDecimal numericValue = null;
                try {
                    numericValue = new BigDecimal(dcv);
                } catch (Exception e) {
                }
                cell = makeNewNumericCellValue(numericValue);
                break;
            case NUMERIC_BIGDECIMAL:
                BigDecimal bigDecimalValue = null;
                try {
                    bigDecimalValue = new BigDecimal(dcv);
                } catch (Exception e) {
                }
                cell = makeNewBigDecimalCellValue(bigDecimalValue);
                break;
            case NUMERIC_BIGINTEGER:
                BigInteger bigIntegerValue = null;
                try {
                    bigIntegerValue = new BigInteger(dcv);
                } catch (Exception e) {
                }
                cell = makeNewBigIntegerCellValue(bigIntegerValue);
                break;
            case NUMERIC_BYTE:
                Byte byteValue = null;
                try {
                    byteValue = Byte.valueOf(dcv);
                } catch (Exception e) {
                }
                cell = makeNewByteCellValue(byteValue);
                break;
            case NUMERIC_DOUBLE:
                Double doubleValue = null;
                try {
                    doubleValue = new Double(dcv);
                } catch (Exception e) {
                }
                cell = makeNewDoubleCellValue(doubleValue);
                break;
            case NUMERIC_FLOAT:
                Float floatValue = null;
                try {
                    floatValue = new Float(dcv);
                } catch (Exception e) {
                }
                cell = makeNewFloatCellValue(floatValue);
                break;
            case NUMERIC_INTEGER:
                Integer integerValue = null;
                try {
                    integerValue = Integer.valueOf(dcv);
                } catch (Exception e) {
                }
                cell = makeNewIntegerCellValue(integerValue);
                break;
            case NUMERIC_LONG:
                Long longValue = null;
                try {
                    longValue = Long.valueOf(dcv);
                } catch (Exception e) {
                }
                cell = makeNewLongCellValue(longValue);
                break;
            case NUMERIC_SHORT:
                Short shortValue = null;
                try {
                    shortValue = Short.valueOf(dcv);
                } catch (Exception e) {
                }
                cell = makeNewShortCellValue(shortValue);
                break;
            default:
                cell = makeNewStringCellValue(dcv);
        }

        return cell;
    }

    // Get the Data Type corresponding to a given column
    private DataType.DataTypes getDataType(TemplateDataColumn column) {
        String dataType = column.getDataType();
        if (dataType.equals(DataType.TYPE_BOOLEAN)) {
            return DataType.DataTypes.BOOLEAN;
        } else if (dataType.equals(DataType.TYPE_DATE)) {
            return DataType.DataTypes.DATE;
        } else if (dataType.equals(DataType.TYPE_NUMERIC)) {
            return DataType.DataTypes.NUMERIC;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGDECIMAL)) {
            return DataType.DataTypes.NUMERIC_BIGDECIMAL;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BIGINTEGER)) {
            return DataType.DataTypes.NUMERIC_BIGINTEGER;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_BYTE)) {
            return DataType.DataTypes.NUMERIC_BYTE;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_DOUBLE)) {
            return DataType.DataTypes.NUMERIC_DOUBLE;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_FLOAT)) {
            return DataType.DataTypes.NUMERIC_FLOAT;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_INTEGER)) {
            return DataType.DataTypes.NUMERIC_INTEGER;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_LONG)) {
            return DataType.DataTypes.NUMERIC_LONG;
        } else if (dataType.equals(DataType.TYPE_NUMERIC_SHORT)) {
            return DataType.DataTypes.NUMERIC_SHORT;
        } else {
            return DataType.DataTypes.STRING;
        }
    }

    /**
     * Convert an interpolation variable to a column
     */
    public TemplateDataColumn makeModelColumn(InterpolationVariable var) {
        return new TemplateDataColumn(var.getVarName(),
                                      var.getDataType(),
                                      var.getFactType(),
                                      var.getFactField(),
                                      var.getOperator());
    }

    /**
     * Convert a type-safe UI CellValue into a type-safe Model CellValue
     * @param column Model column from which data-type can be derived
     * @param cv UI CellValue to convert into Model CellValue
     * @return
     */
    public String convertToModelCell(TemplateDataColumn column,
                                     CellValue<?> cv) {
        DataType.DataTypes dataType = getDataType(column);

        switch (dataType) {
            case BOOLEAN:
                return convertBooleanValueToString(cv);
            case DATE:
                return convertDateValueToString(cv);
            case NUMERIC:
                return convertNumericValueToString(cv);
            case NUMERIC_BIGDECIMAL:
                return convertBigDecimalValueToString(cv);
            case NUMERIC_BIGINTEGER:
                return convertBigIntegerValueToString(cv);
            case NUMERIC_BYTE:
                return convertByteValueToString(cv);
            case NUMERIC_DOUBLE:
                return convertDoubleValueToString(cv);
            case NUMERIC_FLOAT:
                return convertFloatValueToString(cv);
            case NUMERIC_INTEGER:
                return convertIntegerValueToString(cv);
            case NUMERIC_LONG:
                return convertLongValueToString(cv);
            case NUMERIC_SHORT:
                return convertShortValueToString(cv);
            default:
                return convertStringValueToString(cv);
        }
    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Boolean) value.getValue()).toString());
    }

    //Convert a Date value to a String
    private String convertDateValueToString(CellValue<?> value) {
        String result = null;
        if (value.getValue() != null) {
            if (DATE_CONVERTOR == null) {
                throw new IllegalArgumentException("DATE_CONVERTOR has not been initialised.");
            }
            result = DATE_CONVERTOR.format((Date) value.getValue());
        }
        return result;
    }

    //Convert a Generic Numeric (BigDecimal) value to a String
    private String convertNumericValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //Convert a BigDecimal value to a String
    private String convertBigDecimalValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((BigDecimal) value.getValue()).toPlainString());
    }

    //Convert a BigInteger value to a String
    private String convertBigIntegerValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((BigInteger) value.getValue()).toString());
    }

    //Convert a Byte value to a String
    private String convertByteValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Byte) value.getValue()).toString());
    }

    //Convert a Double value to a String
    private String convertDoubleValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Double) value.getValue()).toString());
    }

    //Convert a Float value to a String
    private String convertFloatValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Float) value.getValue()).toString());
    }

    //Convert a Integer value to a String
    private String convertIntegerValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Integer) value.getValue()).toString());
    }

    //Convert a Long value to a String
    private String convertLongValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Long) value.getValue()).toString());
    }

    //Convert a Short value to a String
    private String convertShortValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : ((Short) value.getValue()).toString());
    }

    //Convert a String value to a String
    private String convertStringValueToString(CellValue<?> value) {
        return (value.getValue() == null ? null : (String) value.getValue());
    }
}
