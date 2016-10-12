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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.cache.util.HasIndex;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.BRLAction;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.FieldAction;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.RetractAction;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.uberfire.commons.validation.PortablePreconditions;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.Utils.*;

public class ActionBuilder {

    private final Index index;
    private final GuidedDecisionTable52 model;
    private final Rule rule;
    private final List<DTCellValue52> row;
    private final ActionCol52 actionCol;
    private AnalyzerConfiguration configuration;

    public ActionBuilder( final Index index,
                          final GuidedDecisionTable52 model,
                          final Rule rule,
                          final List<DTCellValue52> row,
                          final ActionCol52 actionCol,
                          final AnalyzerConfiguration configuration ) {
        this.index = PortablePreconditions.checkNotNull( "index",
                                                         index );
        this.model = PortablePreconditions.checkNotNull( "index",
                                                         model );
        this.rule = PortablePreconditions.checkNotNull( "rule",
                                                        rule );
        this.row = PortablePreconditions.checkNotNull( "row",
                                                       row );
        this.actionCol = PortablePreconditions.checkNotNull( "actionCol",
                                                             actionCol );
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
    }

    public static Comparable getValue( final DTCellValue52 dtCellValue52 ) {
        switch ( dtCellValue52.getDataType() ) {
            case NUMERIC_BIGDECIMAL:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new BigDecimal( dtCellValue52.getNumericValue()
                                                   .toString() );
                }
            case NUMERIC_BIGINTEGER:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new BigInteger( dtCellValue52.getNumericValue()
                                                   .toString() );
                }
            case NUMERIC_BYTE:
                return new Byte( dtCellValue52.getStringValue() );
            case NUMERIC_DOUBLE:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Double( dtCellValue52.getNumericValue()
                                               .toString() );
                }
            case NUMERIC_FLOAT:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Float( dtCellValue52.getNumericValue()
                                              .toString() );
                }
            case NUMERIC_INTEGER:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Integer( dtCellValue52.getNumericValue()
                                                .toString() );
                }
            case NUMERIC_LONG:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Long( dtCellValue52.getNumericValue()
                                             .toString() );
                }
            case NUMERIC_SHORT:
                if ( dtCellValue52.getNumericValue() == null ) {
                    return null;
                } else {
                    return new Short( dtCellValue52.getNumericValue()
                                              .toString() );
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

    public void build() {

        if ( actionCol instanceof BRLActionColumn ) {
            addBRLAction( (BRLActionColumn) actionCol );
        } else if ( actionCol instanceof ActionRetractFactCol52 ) {
            addRetractAction( (ActionRetractFactCol52) actionCol );
        } else {
            final int columnIndex = model.getExpandedColumns()
                    .indexOf( actionCol );
            if ( rowHasIndex( columnIndex,
                              row ) ) {
                addAction( actionCol,
                           row.get( columnIndex ) );
            }
        }
    }

    private void addRetractAction( final ActionRetractFactCol52 actionCol ) {
        final int columnIndex = model.getExpandedColumns()
                .indexOf( actionCol );

        rule.getActions()
                .add( new RetractAction( getColumn( actionCol ),
                                         getValues( row.get( columnIndex ) ),
                                         configuration ) );
    }

    private void addBRLAction( final BRLActionColumn brlActionColumn ) {
        for ( final BRLActionVariableColumn brlActionVariableColumn : brlActionColumn.getChildColumns() ) {

            final int columnIndex = model.getExpandedColumns()
                    .indexOf( brlActionVariableColumn );

            rule.getActions()
                    .add( new BRLAction( getColumn( brlActionVariableColumn ),
                                         getValues( row.get( columnIndex ) ),
                                         configuration ) );

        }
    }

    private void addAction( final ActionCol52 actionCol,
                            final DTCellValue52 visibleCellValue ) {
        final Field field = resolveField( actionCol );
        if ( field != null ) {
            final Action action = buildAction( field,
                                               visibleCellValue );
            field.getActions()
                    .add( action );
            rule.getActions()
                    .add( action );
        }
    }

    private Field resolveField( final ActionCol52 actionCol ) {
        if ( actionCol instanceof ActionSetFieldCol52 ) {
            final Pattern pattern = rule.getPatterns()
                    .where( Pattern.boundName()
                                    .is( ( (ActionSetFieldCol52) actionCol ).getBoundName() ) )
                    .select()
                    .first();

            if ( pattern == null ) {
                return null;
            } else {
                return getField( pattern.getBoundName(),
                                 pattern.getName(),
                                 ( (ActionSetFieldCol52) actionCol ).getType(),
                                 ( (ActionSetFieldCol52) actionCol ).getFactField() );
            }
        } else if ( actionCol instanceof ActionInsertFactCol52 ) {
            return getField( ( (ActionInsertFactCol52) actionCol ).getBoundName(),
                             ( (ActionInsertFactCol52) actionCol ).getFactType(),
                             ( (ActionInsertFactCol52) actionCol ).getType(),
                             ( (ActionInsertFactCol52) actionCol ).getFactField() );
        }
        return null;
    }

    private Field getField( final String boundName,
                            final String factType,
                            final String fieldType,
                            final String factField ) {
        return Utils.resolveField( Utils.resolvePattern( index,
                                                         rule,
                                                         boundName,
                                                         factType,
                                                         configuration ),
                                   fieldType,
                                   factField,
                                   configuration );
    }

    private Action buildAction( final Field field,
                                final DTCellValue52 visibleCellValue ) {

        return new FieldAction( field,
                                getColumn( actionCol ),
                                convert( visibleCellValue.getDataType() ),
                                getValues( visibleCellValue ),
                                configuration );
    }

    private org.drools.workbench.services.verifier.api.client.index.DataType.DataTypes convert( final DataType.DataTypes dataType ) {
        switch ( dataType ) {
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

    private Values getValues( final DTCellValue52 visibleCellValue ) {
        final Comparable value = getValue( getRealCellValue( actionCol,
                                                             visibleCellValue ) );
        if ( value == null ) {
            return new Values<>();
        } else {
            return new Values( value );
        }
    }

    private Column getColumn( final ActionCol52 actionCol52 ) {
        return index.columns
                .where( HasIndex.index()
                                .is( model.getExpandedColumns()
                                             .indexOf( actionCol52 ) ) )
                .select()
                .first();
    }

}
