/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Analysis;
import org.drools.workbench.models.guided.dtable.shared.model.AnalysisCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.dtable.client.widget.table.cells.AnalysisCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.cells.PopupBoundPatternDropDownEditCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.cells.PopupValueListDropDownEditCell;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DecoratedGridCellValueAdaptor;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.AbstractProxyPopupDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.PopupDialectDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.PopupDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.PopupTextEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupDateDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigDecimalDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericBigIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericByteDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericDoubleDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericFloatDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericIntegerDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericLongDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupNumericShortDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.ProxyPopupTextDropDownEditCell;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells.RowNumberCell;

/**
 * A Factory to provide the Cells for given coordinate for Decision Tables.
 */
public class DecisionTableCellFactory extends AbstractCellFactory<BaseColumn> {

    private GuidedDecisionTableUtils utils;
    private GuidedDecisionTable52 model;

    /**
     * Construct a Cell Factory for a specific Decision Table
     * @param oracle SuggestionCompletionEngine to assist with drop-downs
     * @param model GuidedDecisionTable52 Decision table model
     * @param dropDownManager DropDownManager for dependent cells
     * @param isReadOnly Should cells be created for a read-only mode of operation
     * @param eventBus An EventBus on which cells can subscribe to events
     */
    public DecisionTableCellFactory( final GuidedDecisionTable52 model,
                                     final AsyncPackageDataModelOracle oracle,
                                     final DecisionTableDropDownManager dropDownManager,
                                     final boolean isReadOnly,
                                     final EventBus eventBus ) {
        super( oracle,
               dropDownManager,
               isReadOnly,
               eventBus );
        if ( model == null ) {
            throw new IllegalArgumentException( "model cannot be null" );
        }
        if ( oracle == null ) {
            throw new IllegalArgumentException( "oracle cannot be null" );
        }
        this.model = model;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
    }

    /**
     * Create a Cell for the given DTColumnConfig
     * @param column The Decision Table model column
     * @return A Cell
     */
    public DecoratedGridCellValueAdaptor<? extends Comparable<?>> getCell( BaseColumn column ) {

        //This is the cell that will be used to edit values; its type can differ to the "fieldType" 
        //of the underlying model. For example a "Guvnor-enum" requires a drop-down list of potential 
        //values whereas the "fieldType" may be a String. 
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

        if ( column instanceof RowNumberCol52 ) {
            cell = makeRowNumberCell();

        } else if ( column instanceof AttributeCol52 ) {
            AttributeCol52 attrCol = (AttributeCol52) column;
            String attrName = attrCol.getAttribute();
            if ( attrName.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
                if ( attrCol.isUseRowNumber() ) {
                    cell = makeRowNumberCell();
                } else {
                    cell = makeNumericIntegerCell();
                }
            } else if ( attrName.equals( GuidedDecisionTable52.ENABLED_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.NO_LOOP_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DURATION_ATTR ) ) {
                cell = makeNumericLongCell();
            } else if ( attrName.equals( GuidedDecisionTable52.TIMER_ATTR ) ) {
                cell = makeTimerCell();
            } else if ( attrName.equals( GuidedDecisionTable52.CALENDARS_ATTR ) ) {
                cell = makeCalendarsCell();
            } else if ( attrName.equals( GuidedDecisionTable52.AUTO_FOCUS_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR ) ) {
                cell = makeBooleanCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DATE_EFFECTIVE_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DATE_EXPIRES_ATTR ) ) {
                cell = makeDateCell();
            } else if ( attrName.equals( GuidedDecisionTable52.DIALECT_ATTR ) ) {
                cell = makeDialectCell();
            } else if ( attrName.equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
                cell = makeBooleanCell();
            }

        } else if ( column instanceof LimitedEntryCol ) {
            cell = makeBooleanCell();

        } else if ( column instanceof BRLConditionVariableColumn ) {
            //Before ConditionCol52 as this is a sub-class
            cell = derieveCellFromCondition( (BRLConditionVariableColumn) column );

        } else if ( column instanceof ConditionCol52 ) {
            cell = derieveCellFromCondition( (ConditionCol52) column );

        } else if ( column instanceof ActionWorkItemSetFieldCol52 ) {
            //Before ActionSetFieldCol52 as this is a sub-class
            cell = makeBooleanCell();

        } else if ( column instanceof ActionWorkItemInsertFactCol52 ) {
            //Before ActionInsertFactCol52 as this is a sub-class
            cell = makeBooleanCell();

        } else if ( column instanceof ActionSetFieldCol52 ) {
            cell = derieveCellFromAction( (ActionSetFieldCol52) column );

        } else if ( column instanceof ActionInsertFactCol52 ) {
            cell = derieveCellFromAction( (ActionInsertFactCol52) column );

        } else if ( column instanceof ActionRetractFactCol52 ) {
            cell = derieveCellFromAction( (ActionRetractFactCol52) column );

        } else if ( column instanceof ActionWorkItemCol52 ) {
            cell = makeBooleanCell();

        } else if ( column instanceof BRLActionVariableColumn ) {
            cell = derieveCellFromAction( (BRLActionVariableColumn) column );

        } else if ( column instanceof AnalysisCol52 ) {
            cell = makeRowAnalysisCell();
        }

        return cell;

    }

    // Make a new Cell for Condition columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromCondition( ConditionCol52 col ) {

        //Operators "is null" and "is not null" require a boolean cell
        if ( col.getOperator() != null && ( col.getOperator().equals( "== null" ) || col.getOperator().equals( "!= null" ) ) ) {
            return makeBooleanCell();
        }

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = model.getPattern( col ).getFactType();
        final String fieldName = col.getFactField();
        final String dataType = utils.getType( col );
        if ( utils.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( oracle.hasEnums( factType,
                                     fieldName ) ) {
            if ( OperatorsOracle.operatorRequiresList( col.getOperator() ) ) {
                return makeMultipleSelectEnumCell( factType,
                                                   fieldName );
            } else {
                return makeSingleSelectionEnumCell( factType,
                                                    fieldName,
                                                    dataType );
            }
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for BRLConditionVariableColumn columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromCondition( BRLConditionVariableColumn col ) {

        //Check if the column has an enumeration
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        final String dataType = utils.getType( col );
        if ( oracle.hasEnums( factType,
                              fieldName ) ) {
            if ( OperatorsOracle.operatorRequiresList( col.getOperator() ) ) {
                return makeMultipleSelectEnumCell( factType,
                                                   fieldName );

            } else {
                return makeSingleSelectionEnumCell( factType,
                                                    fieldName,
                                                    dataType );
            }
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionSetField columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromAction( ActionSetFieldCol52 col ) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = utils.getBoundFactType( col.getBoundName() );
        final String fieldName = col.getFactField();
        final String dataType = utils.getType( col );
        if ( utils.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( oracle.hasEnums( factType,
                                     fieldName ) ) {
            return makeSingleSelectionEnumCell( factType,
                                                fieldName,
                                                dataType );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionInsertFact columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromAction( ActionInsertFactCol52 col ) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        final String dataType = utils.getType( col );
        if ( utils.hasValueList( col ) ) {
            return makeValueListCell( col );

        } else if ( oracle.hasEnums( factType,
                                     fieldName ) ) {
            return makeSingleSelectionEnumCell( factType,
                                                fieldName,
                                                dataType );
        }

        return derieveCellFromModel( col );
    }

    // Make a new Cell for ActionRetractFactCol52 columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromAction( ActionRetractFactCol52 col ) {

        //Drop down of possible patterns
        PopupBoundPatternDropDownEditCell pudd = new PopupBoundPatternDropDownEditCell( eventBus,
                                                                                        isReadOnly );
        BRLRuleModel rm = new BRLRuleModel( model );
        pudd.setFactBindings( rm.getLHSBoundFacts() );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    // Make a new Cell for BRLActionVariableColumn columns
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromAction( BRLActionVariableColumn col ) {

        //Check if the column has an enumeration
        final String factType = col.getFactType();
        final String fieldName = col.getFactField();
        final String dataType = utils.getType( col );
        if ( oracle.hasEnums( factType,
                              fieldName ) ) {
            return makeSingleSelectionEnumCell( factType,
                                                fieldName,
                                                dataType );
        }

        return derieveCellFromModel( col );
    }

    //Get Cell applicable to Model's data-type
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> derieveCellFromModel( DTColumnConfig52 col ) {

        //Extended Entry...
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

        //Get a cell based upon the data-type
        String type = utils.getType( col );

        if ( type.equals( DataType.TYPE_NUMERIC ) ) {
            cell = makeNumericCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            cell = makeNumericBigDecimalCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            cell = makeNumericBigIntegerCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            cell = makeNumericByteCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            cell = makeNumericDoubleCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            cell = makeNumericFloatCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            cell = makeNumericIntegerCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            cell = makeNumericLongCell();
        } else if ( type.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            cell = makeNumericShortCell();
        } else if ( type.equals( DataType.TYPE_BOOLEAN ) ) {
            cell = makeBooleanCell();
        } else if ( type.equals( DataType.TYPE_DATE ) ) {
            cell = makeDateCell();
        }

        return cell;
    }

    // Make a new Cell for Dialect columns
    private DecoratedGridCellValueAdaptor<String> makeDialectCell() {
        PopupDialectDropDownEditCell pudd = new PopupDialectDropDownEditCell( isReadOnly );
        return new DecoratedGridCellValueAdaptor<String>( pudd,
                                                          eventBus );
    }

    // Make a new Cell for Row Number columns
    private DecoratedGridCellValueAdaptor<Integer> makeRowNumberCell() {
        return new DecoratedGridCellValueAdaptor<Integer>( new RowNumberCell(),
                                                           eventBus );
    }

    // Make a new Cell for Timer columns
    private DecoratedGridCellValueAdaptor<String> makeTimerCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell( isReadOnly ),
                                                          eventBus );
    }

    // Make a new Cell for Calendars columns
    private DecoratedGridCellValueAdaptor<String> makeCalendarsCell() {
        return new DecoratedGridCellValueAdaptor<String>( new PopupTextEditCell( isReadOnly ),
                                                          eventBus );
    }

    // Make a new Cell for Rule Analysis columns
    private DecoratedGridCellValueAdaptor<Analysis> makeRowAnalysisCell() {
        return new DecoratedGridCellValueAdaptor<Analysis>( new AnalysisCell(),
                                                            eventBus );
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeValueListCell( final ConditionCol52 col ) {
        // Columns with "Value Lists" are always Text (for now)
        final boolean isMultipleSelect = OperatorsOracle.operatorRequiresList( col.getOperator() );
        PopupValueListDropDownEditCell pudd = new PopupValueListDropDownEditCell( utils.getValueList( col ),
                                                                                  isMultipleSelect,
                                                                                  isReadOnly );
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                                                                                 eventBus );
        return cell;
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeValueListCell( final ActionCol52 col ) {
        // Columns with "Value Lists" are always Text (for now)
        PopupValueListDropDownEditCell pudd = new PopupValueListDropDownEditCell( utils.getValueList( col ),
                                                                                  isReadOnly );
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                                                                                 eventBus );
        return cell;
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeMultipleSelectEnumCell( String factType,
                                                                                               String fieldName ) {
        // Columns with enumerations are always Text
        PopupDropDownEditCell pudd = new PopupDropDownEditCell( factType,
                                                                fieldName,
                                                                oracle,
                                                                dropDownManager,
                                                                true,
                                                                isReadOnly );
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                                                                                 eventBus );
        return cell;
    }

    //Get a cell for a Value List
    private DecoratedGridCellValueAdaptor<? extends Comparable<?>> makeSingleSelectionEnumCell( String factType,
                                                                                                String fieldName,
                                                                                                String dataType ) {
        DecoratedGridCellValueAdaptor<? extends Comparable<?>> cell;
        if ( dataType.equals( DataType.TYPE_NUMERIC ) ) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigDecimal>( pudd,
                                                                  eventBus );

        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGDECIMAL ) ) {
            final AbstractProxyPopupDropDownEditCell<BigDecimal, BigDecimal> pudd = new ProxyPopupNumericBigDecimalDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigDecimal>( pudd,
                                                                  eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BIGINTEGER ) ) {
            final AbstractProxyPopupDropDownEditCell<BigInteger, BigInteger> pudd = new ProxyPopupNumericBigIntegerDropDownEditCell( factType,
                                                                                                                                     fieldName,
                                                                                                                                     oracle,
                                                                                                                                     dropDownManager,
                                                                                                                                     isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<BigInteger>( pudd,
                                                                  eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_BYTE ) ) {
            final AbstractProxyPopupDropDownEditCell<Byte, Byte> pudd = new ProxyPopupNumericByteDropDownEditCell( factType,
                                                                                                                   fieldName,
                                                                                                                   oracle,
                                                                                                                   dropDownManager,
                                                                                                                   isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Byte>( pudd,
                                                            eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_DOUBLE ) ) {
            final AbstractProxyPopupDropDownEditCell<Double, Double> pudd = new ProxyPopupNumericDoubleDropDownEditCell( factType,
                                                                                                                         fieldName,
                                                                                                                         oracle,
                                                                                                                         dropDownManager,
                                                                                                                         isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Double>( pudd,
                                                              eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_FLOAT ) ) {
            final AbstractProxyPopupDropDownEditCell<Float, Float> pudd = new ProxyPopupNumericFloatDropDownEditCell( factType,
                                                                                                                      fieldName,
                                                                                                                      oracle,
                                                                                                                      dropDownManager,
                                                                                                                      isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Float>( pudd,
                                                             eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_INTEGER ) ) {
            final AbstractProxyPopupDropDownEditCell<Integer, Integer> pudd = new ProxyPopupNumericIntegerDropDownEditCell( factType,
                                                                                                                            fieldName,
                                                                                                                            oracle,
                                                                                                                            dropDownManager,
                                                                                                                            isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Integer>( pudd,
                                                               eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_LONG ) ) {
            final AbstractProxyPopupDropDownEditCell<Long, Long> pudd = new ProxyPopupNumericLongDropDownEditCell( factType,
                                                                                                                   fieldName,
                                                                                                                   oracle,
                                                                                                                   dropDownManager,
                                                                                                                   isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Long>( pudd,
                                                            eventBus );
        } else if ( dataType.equals( DataType.TYPE_NUMERIC_SHORT ) ) {
            final AbstractProxyPopupDropDownEditCell<Short, Short> pudd = new ProxyPopupNumericShortDropDownEditCell( factType,
                                                                                                                      fieldName,
                                                                                                                      oracle,
                                                                                                                      dropDownManager,
                                                                                                                      isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<Short>( pudd,
                                                             eventBus );
        } else if ( dataType.equals( DataType.TYPE_BOOLEAN ) ) {
            cell = makeBooleanCell();
        } else if ( dataType.equals( DataType.TYPE_DATE ) ) {
            final AbstractProxyPopupDropDownEditCell<Date, Date> pudd = new ProxyPopupDateDropDownEditCell( factType,
                                                                                                            fieldName,
                                                                                                            oracle,
                                                                                                            dropDownManager,
                                                                                                            isReadOnly,
                                                                                                            DATE_FORMAT );
            cell = new DecoratedGridCellValueAdaptor<Date>( pudd,
                                                            eventBus );
        } else {
            final AbstractProxyPopupDropDownEditCell<String, String> pudd = new ProxyPopupTextDropDownEditCell( factType,
                                                                                                                fieldName,
                                                                                                                oracle,
                                                                                                                dropDownManager,
                                                                                                                isReadOnly );
            cell = new DecoratedGridCellValueAdaptor<String>( pudd,
                                                              eventBus );
        }

        return cell;
    }

}
