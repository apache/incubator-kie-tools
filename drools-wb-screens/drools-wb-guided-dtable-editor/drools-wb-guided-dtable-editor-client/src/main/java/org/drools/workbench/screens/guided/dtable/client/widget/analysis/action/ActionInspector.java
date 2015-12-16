/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.action;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsConflicting;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsRedundant;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.IsSubsuming;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

public class ActionInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting {

    protected final ActionInspectorKey key;
    private final DTCellValue52 value;

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

    public ActionInspector( final ActionInspectorKey key,
                            final DTCellValue52 value ) {
        this.key = key;
        this.value = value;
    }

    public ActionInspectorKey getKey() {
        return key;
    }

    public DTCellValue52 getValue() {
        return value;
    }

    @Override
    public boolean isRedundant( final Object other ) {
        if ( other instanceof ActionInspector ) {
            return key.equals( ( (ActionInspector) other ).key )
                    && isValueRedundant( ( (ActionInspector) other ).value );
        } else {
            return false;
        }
    }

    private boolean isValueRedundant( final DTCellValue52 other ) {
        if ( value.equals( other ) ) {
            return true;
        } else if ( isDataTypeString( value ) && !isDataTypeString( other ) ) {
            return isStringValueEqualTo( value.getStringValue(),
                                         other );
        } else if ( !isDataTypeString( value ) && isDataTypeString( other ) ) {
            return isStringValueEqualTo( other.getStringValue(),
                                         value );
        } else {
            return false;
        }
    }

    private boolean isDataTypeString( final DTCellValue52 value ) {
        return value.getDataType().equals( DataType.DataTypes.STRING );
    }

    private boolean isStringValueEqualTo( final String stringValue,
                                          final DTCellValue52 dtCellValue52 ) {
        switch ( dtCellValue52.getDataType() ) {
            case STRING:
                return stringValue.equals( dtCellValue52.getStringValue() );
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return stringValue.equals( dtCellValue52.getNumericValue().toString() );
            case DATE:
                return stringValue.equals( format( dtCellValue52.getDateValue() ) );
            case BOOLEAN:
                return stringValue.equals( dtCellValue52.getBooleanValue().toString() );
            default:
                return false;
        }
    }

    protected String format( final Date dateValue ) {
        return DATE_FORMATTER.format( dateValue );
    }

    @Override
    public boolean conflicts( final Object other ) {
        if ( other instanceof ActionInspector ) {
            if ( key.equals( ( (ActionInspector) other ).key )
                    && hasValue()
                    && ( (ActionInspector) other ).hasValue() ) {
                return !isRedundant( other );
            }
        }
        return false;
    }

    @Override
    public boolean subsumes( final Object other ) {
        // At the moment we are not smart enough to figure out subsumption in the RHS.
        // So redundancy == subsumption in this case.
        return isRedundant( other );
    }

    public boolean hasValue() {
        switch ( value.getDataType() ) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return value.getNumericValue() != null;
            case BOOLEAN:
                return value.getBooleanValue() != null;
            default:
                return value.getStringValue() != null && !value.getStringValue().isEmpty();
        }
    }

    public String getValueAsString() {
        switch ( value.getDataType() ) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return value.getNumericValue().toString();
            case BOOLEAN:
                return value.getBooleanValue().toString();
            default:
                return value.getStringValue();
        }
    }

    public String toHumanReadableString() {
        return key.toHumanReadableString() + " = " + getValueAsString();
    }
}
