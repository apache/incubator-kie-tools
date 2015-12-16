/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtree.client.widget.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.EnumValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

/**
 * Utilities to handle Values
 */
public class ValueUtilities {

    private static final String DROOLS_DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat( DROOLS_DATE_FORMAT );

    /**
     * Convert a Value to a String
     * @param value
     * @return
     */
    public static String convertNodeValue( final Value value ) {
        if ( value == null ) {
            return "";
        }
        if ( value instanceof DateValue ) {
            final DateValue dv = (DateValue) value;
            return DATE_FORMAT.format( dv.getValue() );
        }
        return value.getValue().toString();
    }

    /**
     * Create a Value object for the given data type. Values will be initialised:
     * <ul>
     * <li>Numbers - 0</li>
     * <li>String - new String()</li>
     * <li>Boolean - Boolean.FALSE</li>
     * <li>Date - new Date()</li>
     * </ul>
     * @param dataType The data type
     * @return An initialized Value or null if the data type was not recognised
     */
    public static Value makeEmptyValue( final String dataType ) {
        if ( DataType.TYPE_STRING.equals( dataType ) ) {
            return new StringValue( new String() );

        } else if ( DataType.TYPE_NUMERIC.equals( dataType ) ) {
            return new BigDecimalValue( new BigDecimal( 0 ) );

        } else if ( DataType.TYPE_NUMERIC_BIGDECIMAL.equals( dataType ) ) {
            return new BigDecimalValue( new BigDecimal( 0 ) );

        } else if ( DataType.TYPE_NUMERIC_BIGINTEGER.equals( dataType ) ) {
            return new BigIntegerValue( new BigInteger( "0" ) );

        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            return new ByteValue( new Byte( "0" ) );

        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            return new DoubleValue( 0d );

        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            return new FloatValue( 0f );

        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            return new IntegerValue( 0 );

        } else if ( DataType.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            return new LongValue( 0l );

        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            return new ShortValue( new Short( "0" ) );

        } else if ( DataType.TYPE_BOOLEAN.equals( dataType ) ) {
            return new BooleanValue( Boolean.FALSE );

        } else if ( DataType.TYPE_DATE.equals( dataType ) ) {
            return new DateValue( new Date() );

        } else if ( DataType.TYPE_COMPARABLE.equals( dataType ) ) {
            return new EnumValue( new String() );

        }
        return null;
    }

    /**
     * Clone a Value object from the given Value.
     * @param value The Value to clone
     * @return A cloned Value
     */
    public static Value clone( final Value value ) {
        if ( value instanceof StringValue ) {
            return new StringValue( ( (StringValue) value ).getValue() );

        } else if ( value instanceof BigDecimalValue ) {
            return new BigDecimalValue( ( (BigDecimalValue) value ).getValue() );

        } else if ( value instanceof BigIntegerValue ) {
            return new BigIntegerValue( ( (BigIntegerValue) value ).getValue() );

        } else if ( value instanceof ByteValue ) {
            return new ByteValue( ( (ByteValue) value ).getValue() );

        } else if ( value instanceof DoubleValue ) {
            return new DoubleValue( ( (DoubleValue) value ).getValue() );

        } else if ( value instanceof FloatValue ) {
            return new FloatValue( ( (FloatValue) value ).getValue() );

        } else if ( value instanceof IntegerValue ) {
            return new IntegerValue( ( (IntegerValue) value ).getValue() );

        } else if ( value instanceof LongValue ) {
            return new LongValue( ( (LongValue) value ).getValue() );

        } else if ( value instanceof ShortValue ) {
            return new ShortValue( ( (ShortValue) value ).getValue() );

        } else if ( value instanceof BooleanValue ) {
            return new BooleanValue( ( (BooleanValue) value ).getValue() );

        } else if ( value instanceof DateValue ) {
            return new DateValue( ( (DateValue) value ).getValue() );

        }
        return null;
    }

}
