/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.utilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DateConverter;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

/**
 * Utilities for Cells
 */
public class CellUtilities {

    // Dates are serialised to Strings with the user-defined format, or dd-MMM-yyyy by default
    protected static DateConverter DATE_CONVERTOR = null;

    /**
     * Override the default, GWT-centric, Date conversion utility class. Only
     * use to hook-in a JVM Compatible implementation for tests
     * @param dc
     */
    public static void injectDateConvertor(DateConverter dc) {
        DATE_CONVERTOR = dc;
    }

    /**
     * The column-data type is looked up from the SuggestionCompletionEngine and
     * represents the *true* data-type that the column represents. The data-type
     * associated with the Cell Value can be incorrect for legacy models. For
     * pre-5.2 they will always be String and for pre-5.4 numerical fields are
     * always Numeric
     * @param dataType
     * @param dcv
     */
    public void convertDTCellValueType(DataType.DataTypes dataType,
                                       DTCellValue52 dcv) {
        if (dcv == null) {
            return;
        }

        //If already converted exit
        if (dataType.equals(dcv.getDataType())) {
            return;
        }

        switch (dataType) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
                dcv.setNumericValue(convertToBigDecimal(dcv));
                break;
            case NUMERIC_BIGINTEGER:
                dcv.setNumericValue(convertToBigInteger(dcv));
                break;
            case NUMERIC_BYTE:
                dcv.setNumericValue(convertToByte(dcv));
                break;
            case NUMERIC_DOUBLE:
                dcv.setNumericValue(convertToDouble(dcv));
                break;
            case NUMERIC_FLOAT:
                dcv.setNumericValue(convertToFloat(dcv));
                break;
            case NUMERIC_INTEGER:
                dcv.setNumericValue(convertToInteger(dcv));
                break;
            case NUMERIC_LONG:
                dcv.setNumericValue(convertToLong(dcv));
                break;
            case NUMERIC_SHORT:
                dcv.setNumericValue(convertToShort(dcv));
                break;
            case DATE:
                dcv.setDateValue(convertToDate(dcv));
                break;
            case BOOLEAN:
                dcv.setBooleanValue(convertToBoolean(dcv));
                break;
            case STRING:
                dcv.setStringValue(convertToString(dcv));
                break;
        }
    }

    /**
     * Convert a DTCellValue52 to it's String representation
     * @param dcv
     * @return
     */
    public String asString(final DTCellValue52 dcv) {
        switch (dcv.getDataType()) {
            case BOOLEAN:
                return convertBooleanValueToString(dcv);
            case DATE:
                return convertDateValueToString(dcv);
            case NUMERIC:
                return convertNumericValueToString(dcv);
            case NUMERIC_BIGDECIMAL:
                return convertBigDecimalValueToString(dcv);
            case NUMERIC_BIGINTEGER:
                return convertBigIntegerValueToString(dcv);
            case NUMERIC_BYTE:
                return convertByteValueToString(dcv);
            case NUMERIC_DOUBLE:
                return convertDoubleValueToString(dcv);
            case NUMERIC_FLOAT:
                return convertFloatValueToString(dcv);
            case NUMERIC_INTEGER:
                return convertIntegerValueToString(dcv);
            case NUMERIC_LONG:
                return convertLongValueToString(dcv);
            case NUMERIC_SHORT:
                return convertShortValueToString(dcv);
        }
        return convertStringValueToString(dcv);
    }

    /**
     * Remove a comma-separated value, replacing the comma-separated value with the first in the comma-separated list
     * @param dcv
     */
    public void removeCommaSeparatedValue(DTCellValue52 dcv) {
        if (dcv == null) {
            return;
        }
        if (dcv.getDataType().equals(DataType.DataTypes.STRING)) {
            if (dcv.getStringValue() == null) {
                return;
            }
            String[] values = dcv.getStringValue().split(",");
            if (values.length > 0) {
                dcv.setStringValue(values[0]);
            }
        }
    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString(DTCellValue52 dcv) {
        final Boolean value = dcv.getBooleanValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Date value to a String
    private String convertDateValueToString(DTCellValue52 dcv) {
        final Date value = dcv.getDateValue();
        String result = "";
        if (value != null) {
            result = DATE_CONVERTOR.format((Date) value);
        }
        return result;
    }

    //Convert a Generic Numeric (BigDecimal) value to a String
    private String convertNumericValueToString(DTCellValue52 dcv) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return (value == null ? "" : value.toPlainString());
    }

    //Convert a BigDecimal value to a String
    private String convertBigDecimalValueToString(DTCellValue52 dcv) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return (value == null ? "" : value.toPlainString());
    }

    //Convert a BigInteger value to a String
    private String convertBigIntegerValueToString(DTCellValue52 dcv) {
        final BigInteger value = (BigInteger) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Byte value to a String
    private String convertByteValueToString(DTCellValue52 dcv) {
        final Byte value = (Byte) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Double value to a String
    private String convertDoubleValueToString(DTCellValue52 dcv) {
        final Double value = (Double) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Float value to a String
    private String convertFloatValueToString(DTCellValue52 dcv) {
        final Float value = (Float) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Integer value to a String
    private String convertIntegerValueToString(DTCellValue52 dcv) {
        final Integer value = (Integer) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Long value to a String
    private String convertLongValueToString(DTCellValue52 dcv) {
        final Long value = (Long) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a Short value to a String
    private String convertShortValueToString(DTCellValue52 dcv) {
        final Short value = (Short) dcv.getNumericValue();
        return (value == null ? "" : value.toString());
    }

    //Convert a String value to a String
    private String convertStringValueToString(DTCellValue52 dcv) {
        final String value = dcv.getStringValue();
        return (value == null ? "" : value);
    }

    public BigDecimal convertToBigDecimal(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_BIGDECIMAL:
            case NUMERIC:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new BigDecimal(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new BigDecimal(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public BigInteger convertToBigInteger(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new BigInteger(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new BigInteger(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Byte convertToByte(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_BYTE:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().byteValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Byte(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Byte(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Double convertToDouble(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_DOUBLE:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().doubleValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Double(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Double(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Float convertToFloat(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_FLOAT:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().floatValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Float(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Float(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Integer convertToInteger(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_INTEGER:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().intValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Integer(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Integer(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Long convertToLong(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_LONG:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().longValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_SHORT:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Long(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Long(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Short convertToShort(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC_SHORT:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().shortValue();
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
                try {
                    if (cell.getNumericValue() == null) {
                        return null;
                    }
                    return new Short(cell.getNumericValue().toString());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            case STRING:
                try {
                    if (cell.getStringValue() == null) {
                        return null;
                    }
                    return new Short(cell.getStringValue());
                } catch (NumberFormatException nfe) {
                    return null;
                }
            default:
                return null;
        }
    }

    public Date convertToDate(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case DATE:
                return cell.getDateValue();
            case STRING:
                Date d = null;
                final String text = cell.getStringValue();
                try {
                    if (text != null) {
                        if (DATE_CONVERTOR == null) {
                            throw new IllegalArgumentException("DATE_CONVERTOR has not been initialised.");
                        }
                        d = DATE_CONVERTOR.parse(text);
                    }
                } catch (IllegalArgumentException e) {
                }
                return d;
            default:
                return null;
        }
    }

    public Boolean convertToBoolean(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case BOOLEAN:
                return cell.getBooleanValue();
            case STRING:
                final String text = cell.getStringValue();
                if (Boolean.TRUE.toString().equalsIgnoreCase(text)) {
                    return true;
                } else if (Boolean.FALSE.toString().equalsIgnoreCase(text)) {
                    return false;
                }
            default:
                return null;
        }
    }

    public String convertToString(final DTCellValue52 cell) {
        switch (cell.getDataType()) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return ((BigDecimal) cell.getNumericValue()).toPlainString();
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                if (cell.getNumericValue() == null) {
                    return null;
                }
                return cell.getNumericValue().toString();
            case DATE:
                final Date d = cell.getDateValue();
                if (d != null) {
                    if (DATE_CONVERTOR == null) {
                        throw new IllegalArgumentException("DATE_CONVERTOR has not been initialised.");
                    }
                    return DATE_CONVERTOR.format(d);
                }
                return null;
            case BOOLEAN:
                if (cell.getBooleanValue() == null) {
                    return null;
                }
                return cell.getBooleanValue().toString();
            default:
                return cell.getStringValue();
        }
    }
}
