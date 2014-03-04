/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DateConverter;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

/**
 * Utilities relating to the use of DTCellValue's
 */
public class DTCellValueUtilities {

    // Dates are serialised to Strings with the user-defined format, or dd-MMM-yyyy by default
    protected static DateConverter DATE_CONVERTOR = null;

    /**
     * Override the default, GWT-centric, Date conversion utility class. Only
     * use to hook-in a JVM Compatible implementation for tests
     * @param dc
     */
    public static void injectDateConvertor( DateConverter dc ) {
        DATE_CONVERTOR = dc;
    }

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;

    public DTCellValueUtilities( final GuidedDecisionTable52 model,
                                 final AsyncPackageDataModelOracle oracle ) {
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
    }

    /**
     * Get the Data Type corresponding to a given column. If the column is a
     * ConditonCol52 and it is not associated with a Pattern52 in the decision
     * table (e.g. it has been cloned) the overloaded method
     * getDataType(Pattern52, ConditionCol52) should be used.
     * @param column
     * @return
     */
    public DataType.DataTypes getDataType( BaseColumn column ) {

        //Limited Entry are simply boolean
        if ( column instanceof LimitedEntryCol ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Action Work Items are always boolean
        if ( column instanceof ActionWorkItemCol52 ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Actions setting Field Values from Work Item Result Parameters are always boolean
        if ( column instanceof ActionWorkItemSetFieldCol52 || column instanceof ActionWorkItemInsertFactCol52 ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Operators "is null" and "is not null" require a boolean cell
        if ( column instanceof ConditionCol52 ) {
            ConditionCol52 cc = (ConditionCol52) column;
            if ( cc.getOperator() != null && ( cc.getOperator().equals( "== null" ) || cc.getOperator().equals( "!= null" ) ) ) {
                return DataType.DataTypes.BOOLEAN;
            }
        }

        //Extended Entry...
        return utils.getTypeSafeType( column );
    }

    /**
     * Get the Data Type corresponding to a given column
     * @param pattern Pattern52
     * @param condition ConditionCol52
     * @return
     */
    public DataType.DataTypes getDataType( Pattern52 pattern,
                                           ConditionCol52 condition ) {

        //Limited Entry are simply boolean
        if ( condition instanceof LimitedEntryCol ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Operators "is null" and "is not null" require a boolean cell
        if ( condition.getOperator() != null && ( condition.getOperator().equals( "== null" ) || condition.getOperator().equals( "!= null" ) ) ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Extended Entry...
        return utils.getTypeSafeType( pattern,
                                      condition );
    }

    /**
     * Get the Data Type corresponding to a given column
     * @param pattern Pattern52
     * @param action ActionSetFieldCol52
     * @return
     */
    public DataType.DataTypes getDataType( Pattern52 pattern,
                                           ActionSetFieldCol52 action ) {

        //Limited Entry are simply boolean
        if ( action instanceof LimitedEntryCol ) {
            return DataType.DataTypes.BOOLEAN;
        }

        //Extended Entry...
        return utils.getTypeSafeType( pattern,
                                      action );
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
    public void assertDTCellValue( DataType.DataTypes dataType,
                                   DTCellValue52 dcv ) {
        if ( dcv == null ) {
            return;
        }

        //If already converted exit
        if ( dataType.equals( dcv.getDataType() ) ) {
            return;
        }

        switch ( dcv.getDataType() ) {
            case NUMERIC:
                convertDTCellValueFromNumeric( dataType,
                                               dcv );
                break;
            default:
                convertDTCellValueFromString( dataType,
                                              dcv );
        }
    }

    /**
     * Convert a DTCellValue52 to it's String representation
     * @param dcv
     * @return
     */
    public String asString( final DTCellValue52 dcv ) {
        switch ( dcv.getDataType() ) {
            case BOOLEAN:
                return convertBooleanValueToString( dcv );
            case DATE:
                return convertDateValueToString( dcv );
            case NUMERIC:
                return convertNumericValueToString( dcv );
            case NUMERIC_BIGDECIMAL:
                return convertBigDecimalValueToString( dcv );
            case NUMERIC_BIGINTEGER:
                return convertBigIntegerValueToString( dcv );
            case NUMERIC_BYTE:
                return convertByteValueToString( dcv );
            case NUMERIC_DOUBLE:
                return convertDoubleValueToString( dcv );
            case NUMERIC_FLOAT:
                return convertFloatValueToString( dcv );
            case NUMERIC_INTEGER:
                return convertIntegerValueToString( dcv );
            case NUMERIC_LONG:
                return convertLongValueToString( dcv );
            case NUMERIC_SHORT:
                return convertShortValueToString( dcv );
        }
        return convertStringValueToString( dcv );
    }

    /**
     * Remove a comma-separated value, replacing the comma-separated value with the first in the comma-separated list
     * @param dcv
     */
    public void removeCommaSeparatedValue( DTCellValue52 dcv ) {
        if ( dcv == null ) {
            return;
        }
        if ( dcv.getStringValue() == null ) {
            return;
        }
        String[] values = dcv.getStringValue().split( "," );
        if ( values.length > 0 ) {
            dcv.setStringValue( values[ 0 ] );
        }
    }

    //Convert a Boolean value to a String
    private String convertBooleanValueToString( DTCellValue52 dcv ) {
        final Boolean value = dcv.getBooleanValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Date value to a String
    private String convertDateValueToString( DTCellValue52 dcv ) {
        final Date value = dcv.getDateValue();
        String result = "";
        if ( value != null ) {
            result = DATE_CONVERTOR.format( (Date) value );
        }
        return result;
    }

    //Convert a Generic Numeric (BigDecimal) value to a String
    private String convertNumericValueToString( DTCellValue52 dcv ) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return ( value == null ? "" : value.toPlainString() );
    }

    //Convert a BigDecimal value to a String
    private String convertBigDecimalValueToString( DTCellValue52 dcv ) {
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        return ( value == null ? "" : value.toPlainString() );
    }

    //Convert a BigInteger value to a String
    private String convertBigIntegerValueToString( DTCellValue52 dcv ) {
        final BigInteger value = (BigInteger) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Byte value to a String
    private String convertByteValueToString( DTCellValue52 dcv ) {
        final Byte value = (Byte) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Double value to a String
    private String convertDoubleValueToString( DTCellValue52 dcv ) {
        final Double value = (Double) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Float value to a String
    private String convertFloatValueToString( DTCellValue52 dcv ) {
        final Float value = (Float) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Integer value to a String
    private String convertIntegerValueToString( DTCellValue52 dcv ) {
        final Integer value = (Integer) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Long value to a String
    private String convertLongValueToString( DTCellValue52 dcv ) {
        final Long value = (Long) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a Short value to a String
    private String convertShortValueToString( DTCellValue52 dcv ) {
        final Short value = (Short) dcv.getNumericValue();
        return ( value == null ? "" : value.toString() );
    }

    //Convert a String value to a String
    private String convertStringValueToString( DTCellValue52 dcv ) {
        final String value = dcv.getStringValue();
        return ( value == null ? "" : value );
    }

    //If the Decision Table model has been converted from the legacy text based
    //class then all values are held in the DTCellValue's StringValue. This
    //function attempts to set the correct DTCellValue property based on
    //the DTCellValue's data type.
    private void convertDTCellValueFromString( DataType.DataTypes dataType,
                                               DTCellValue52 dcv ) {
        String text = dcv.getStringValue();
        switch ( dataType ) {
            case BOOLEAN:
                dcv.setBooleanValue( ( text == null ? Boolean.FALSE : Boolean.valueOf( text ) ) );
                break;
            case DATE:
                Date d = null;
                try {
                    if ( text != null ) {
                        if ( DATE_CONVERTOR == null ) {
                            throw new IllegalArgumentException( "DATE_CONVERTOR has not been initialised." );
                        }
                        d = DATE_CONVERTOR.parse( text );
                    }
                } catch ( IllegalArgumentException e ) {
                }
                dcv.setDateValue( d );
                break;
            case NUMERIC:
                BigDecimal numericValue = null;
                try {
                    if ( text != null ) {
                        numericValue = new BigDecimal( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( numericValue );
                break;
            case NUMERIC_BIGDECIMAL:
                BigDecimal bigDecimalValue = null;
                try {
                    if ( text != null ) {
                        bigDecimalValue = new BigDecimal( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( bigDecimalValue );
                break;
            case NUMERIC_BIGINTEGER:
                BigInteger bigIntegerValue = null;
                try {
                    if ( text != null ) {
                        bigIntegerValue = new BigInteger( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( bigIntegerValue );
                break;
            case NUMERIC_BYTE:
                Byte byteValue = null;
                try {
                    if ( text != null ) {
                        byteValue = Byte.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( byteValue );
                break;
            case NUMERIC_DOUBLE:
                Double doubleValue = null;
                try {
                    if ( text != null ) {
                        doubleValue = Double.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( doubleValue );
                break;
            case NUMERIC_FLOAT:
                Float floatValue = null;
                try {
                    if ( text != null ) {
                        floatValue = Float.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( floatValue );
                break;
            case NUMERIC_INTEGER:
                Integer integerValue = null;
                try {
                    if ( text != null ) {
                        integerValue = Integer.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( integerValue );
                break;
            case NUMERIC_LONG:
                Long longValue = null;
                try {
                    if ( text != null ) {
                        longValue = Long.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( longValue );
                break;
            case NUMERIC_SHORT:
                Short shortValue = null;
                try {
                    if ( text != null ) {
                        shortValue = Short.valueOf( text );
                    }
                } catch ( Exception e ) {
                }
                dcv.setNumericValue( shortValue );
                break;
        }

    }

    //If the Decision Table model was pre-5.4 Numeric data-types were always stored as 
    //BigDecimals. This function attempts to set the correct DTCellValue property based 
    //on the *true* data type.
    private void convertDTCellValueFromNumeric( DataType.DataTypes dataType,
                                                DTCellValue52 dcv ) {
        //Generic type NUMERIC was always stored as a BigDecimal
        final BigDecimal value = (BigDecimal) dcv.getNumericValue();
        switch ( dataType ) {
            case NUMERIC_BIGDECIMAL:
                dcv.setNumericValue( value == null ? null : value );
                break;
            case NUMERIC_BIGINTEGER:
                dcv.setNumericValue( value == null ? null : value.toBigInteger() );
                break;
            case NUMERIC_BYTE:
                dcv.setNumericValue( value == null ? null : value.byteValue() );
                break;
            case NUMERIC_DOUBLE:
                dcv.setNumericValue( value == null ? null : value.doubleValue() );
                break;
            case NUMERIC_FLOAT:
                dcv.setNumericValue( value == null ? null : value.floatValue() );
                break;
            case NUMERIC_INTEGER:
                dcv.setNumericValue( value == null ? null : value.intValue() );
                break;
            case NUMERIC_LONG:
                dcv.setNumericValue( value == null ? null : value.longValue() );
                break;
            case NUMERIC_SHORT:
                dcv.setNumericValue( value == null ? null : value.shortValue() );
                break;
        }

    }

}
