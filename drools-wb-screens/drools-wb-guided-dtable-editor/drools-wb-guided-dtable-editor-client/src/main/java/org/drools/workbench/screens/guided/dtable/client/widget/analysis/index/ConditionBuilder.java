/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

public class ConditionBuilder {

    private final ConditionCol52        conditionColumn;
    private final ColumnUtilities       utils;
    private final DTCellValue52         realCellValue;
    private final Index                 index;
    private final GuidedDecisionTable52 model;
    private final Field field;

    public ConditionBuilder( final Index index,
                             final GuidedDecisionTable52 model,
                             final Field field,
                             final ColumnUtilities utils,
                             final ConditionCol52 conditionColumn,
                             final DTCellValue52 realCellValue ) {
        this.index = index;
        this.model = model;
        this.field = field;
        this.utils = utils;
        this.conditionColumn = conditionColumn;
        this.realCellValue = realCellValue;
    }

    public Condition build() {
        String type = utils.getType( conditionColumn );

        if ( isTypeGuvnorEnum( conditionColumn ) ) {
            // Guvnor enum
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getStringValue() );

        } else if ( type == null ) {
            // type null means the field is free-format
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getStringValue() );

        } else if ( type.equals( DataType.TYPE_STRING ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getStringValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC ) || type.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getBigDecimalValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getBigIntegerValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  ( Byte ) realCellValue.getNumericValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getDoubleValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  ( Float ) realCellValue.getNumericValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getIntegerValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getLongValue() );

        } else if ( type.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getShortValue() );

        } else if ( type.equals( DataType.TYPE_BOOLEAN ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  realCellValue.getBooleanValue() );
        } else if ( type.equals( DataType.TYPE_DATE ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  realCellValue.getDateValue() );
        } else if ( type.equals( DataType.TYPE_COMPARABLE ) ) {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getStringValue() );
        } else {
            return new Condition( field,
                                  getColumn(),
                                  conditionColumn.getOperator(),
                                  getStringValue() );
        }
    }

    private String getStringValue() {
        if ( realCellValue.getStringValue() == null ) {
            return null;
        } else if ( realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            return realCellValue.getStringValue();
        }
    }

    private Column getColumn() {
        return index.columns
                .where( HasIndex.index().is( model.getExpandedColumns().indexOf( conditionColumn ) ) )
                .select().first();
    }

    private Short getShortValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( Short ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new Short( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private Long getLongValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( Long ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new Long( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private Double getDoubleValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( Double ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new Double( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private BigInteger getBigIntegerValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( BigInteger ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new BigInteger( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private BigDecimal getBigDecimalValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( BigDecimal ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new BigDecimal( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private Integer getIntegerValue() {
        if ( realCellValue.getNumericValue() != null ) {
            return ( Integer ) realCellValue.getNumericValue();
        } else if ( realCellValue.getStringValue() == null || realCellValue.getStringValue().isEmpty() ) {
            return null;
        } else {
            try {
                return new Integer( realCellValue.getStringValue() );
            } catch ( final NumberFormatException nfe ) {
                return null;
            }
        }
    }

    private boolean isTypeGuvnorEnum( ConditionCol52 conditionColumn ) {
        return utils.getValueList( conditionColumn ).length != 0;
    }

}
