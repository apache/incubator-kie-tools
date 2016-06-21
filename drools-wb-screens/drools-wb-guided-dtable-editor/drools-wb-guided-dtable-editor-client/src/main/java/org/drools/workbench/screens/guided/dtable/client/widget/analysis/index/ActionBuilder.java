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
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Utils.*;

public class ActionBuilder {

    private final Index                 index;
    private final GuidedDecisionTable52 model;
    private final Rule rule;
    private final List<DTCellValue52> row;
    private final ColumnUtilities     utils;
    private final ActionCol52         actionCol;

    public ActionBuilder( final Index index,
                          final GuidedDecisionTable52 model,
                          final Rule rule,
                          final List<DTCellValue52> row,
                          final ColumnUtilities utils,
                          final ActionCol52 actionCol ) {
        this.index = index;
        this.model = model;
        this.rule = rule;
        this.row = row;
        this.utils = utils;
        this.actionCol = actionCol;
    }

    public void build() {

        //BRLActionColumns cannot be analysed
        if ( actionCol instanceof BRLActionColumn ) {
            return;
        }

        int columnIndex = model.getExpandedColumns().indexOf( actionCol );
        if ( rowHasIndex( columnIndex,
                          row ) ) {
            addActionInspector( actionCol,
                                row.get( columnIndex ) );
        }
    }

    private void addActionInspector( final ActionCol52 actionCol,
                                     final DTCellValue52 visibleCellValue ) {
        final Field field = resolveField( actionCol );
        if ( field != null ) {
            field.getActions().add( buildAction( field,
                                                 visibleCellValue ) );
        }
    }

    private Field resolveField( final ActionCol52 actionCol ) {
        if ( actionCol instanceof ActionSetFieldCol52 ) {
            final Pattern pattern = rule.getPatterns()
                                        .where( Pattern.boundName().is( (( ActionSetFieldCol52 ) actionCol).getBoundName() ) )
                                        .select().first();

            if ( pattern == null ) {
                return null;
            } else {
                return getField( pattern.getBoundName(),
                                 pattern.getName(),
                                 (( ActionSetFieldCol52 ) actionCol).getType(),
                                 (( ActionSetFieldCol52 ) actionCol).getFactField() );
            }
        } else if ( actionCol instanceof ActionInsertFactCol52 ) {
            return getField( (( ActionInsertFactCol52 ) actionCol).getBoundName(),
                             (( ActionInsertFactCol52 ) actionCol).getFactType(),
                             (( ActionInsertFactCol52 ) actionCol).getType(),
                             (( ActionInsertFactCol52 ) actionCol).getFactField() );
        }
        return null;
    }

    private Field getField( final String boundName,
                            final String factType,
                            final String fieldType,
                            final String factField ) {
        return Utils.resolveField( Utils.resolvePattern( rule,
                                                         boundName,
                                                         factType ),
                                   fieldType,
                                   factField );
    }

    private Action buildAction( final Field field,
                                final DTCellValue52 visibleCellValue ) {
        return new Action( field,
                           getColumn(),
                           visibleCellValue.getDataType(),
                           getValue( getRealCellValue( actionCol,
                                                       visibleCellValue ) ) );
    }

    private Column getColumn() {
        return index.columns
                .where( HasIndex.index().is( model.getExpandedColumns().indexOf( actionCol ) ) )
                .select().first();
    }

    public static Comparable getValue( final DTCellValue52 dtCellValue52 ) {
        switch ( dtCellValue52.getDataType() ) {
            case NUMERIC_BIGDECIMAL:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new BigDecimal( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_BIGINTEGER:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                return new BigInteger( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_BYTE:
                return new Byte( dtCellValue52.getStringValue() );
            case NUMERIC_DOUBLE:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Double( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_FLOAT:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Float( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_INTEGER:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Integer( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_LONG:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Long( dtCellValue52.getNumericValue().toString() );
                }
            case NUMERIC_SHORT:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Short( dtCellValue52.getNumericValue().toString() );
                }
            case DATE:
                return dtCellValue52.getDateValue();
            case BOOLEAN:
                return dtCellValue52.getBooleanValue();
            case STRING:
            case NUMERIC:
            default:
                final String stringValue = dtCellValue52.getStringValue();
                if ( stringValue == null ) {
                    return null;
                } else if ( stringValue.isEmpty() ) {
                    return null;
                } else {
                    return stringValue;
                }
        }
    }

}
