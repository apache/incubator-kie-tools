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
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.dtable.client.widget.table.DefaultValueDropDownManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.LimitedEntryDropDownManager;
import org.guvnor.common.services.shared.config.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.util.ConstraintValueHelper;
import org.kie.workbench.common.widgets.client.widget.PopupDatePicker;
import org.uberfire.client.common.AbstractRestrictedEntryTextBox;
import org.uberfire.client.common.NumericBigDecimalTextBox;
import org.uberfire.client.common.NumericBigIntegerTextBox;
import org.uberfire.client.common.NumericByteTextBox;
import org.uberfire.client.common.NumericDoubleTextBox;
import org.uberfire.client.common.NumericFloatTextBox;
import org.uberfire.client.common.NumericIntegerTextBox;
import org.uberfire.client.common.NumericLongTextBox;
import org.uberfire.client.common.NumericShortTextBox;
import org.uberfire.client.common.NumericTextBox;

/**
 * A Factory for Widgets to edit DTCellValues
 */
public class DTCellValueWidgetFactory {

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;
    private final LimitedEntryDropDownManager dropDownManager;
    private final boolean isReadOnly;
    private final boolean allowEmptyValues;

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat format = DateTimeFormat.getFormat( DATE_FORMAT );

    public static DTCellValueWidgetFactory getInstance( GuidedDecisionTable52 model,
                                                        AsyncPackageDataModelOracle oracle,
                                                        boolean isReadOnly,
                                                        boolean allowEmptyValues ) {
        switch ( model.getTableFormat() ) {
            case EXTENDED_ENTRY:
                return new DTCellValueWidgetFactory( model,
                                                     oracle,
                                                     new DefaultValueDropDownManager( model,
                                                                                      oracle ),
                                                     isReadOnly,
                                                     allowEmptyValues );
            default:
                return new DTCellValueWidgetFactory( model,
                                                     oracle,
                                                     new LimitedEntryDropDownManager( model,
                                                                                      oracle ),
                                                     isReadOnly,
                                                     allowEmptyValues );
        }
    }

    private DTCellValueWidgetFactory( GuidedDecisionTable52 model,
                                      AsyncPackageDataModelOracle oracle,
                                      LimitedEntryDropDownManager dropDownManager,
                                      boolean isReadOnly,
                                      boolean allowEmptyValues ) {
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
        this.dropDownManager = dropDownManager;
        this.isReadOnly = isReadOnly;
        this.allowEmptyValues = allowEmptyValues;
    }

    /**
     * Make a DTCellValue for a column
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue( DTColumnConfig52 c ) {
        DataType.DataTypes type = utils.getTypeSafeType( c );
        return new DTCellValue52( type,
                                  allowEmptyValues );
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the pattern may be different to that to which the
     * column has been bound in the Decision Table model, i.e. when adding or
     * editing a column
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue( Pattern52 p,
                                       ConditionCol52 c ) {
        DataType.DataTypes type = utils.getTypeSafeType( p,
                                                         c );
        return new DTCellValue52( type,
                                  allowEmptyValues );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the pattern may be different to that to
     * which the column has been bound in the Decision Table model, i.e. when
     * adding or editing a column
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget( Pattern52 pattern,
                             ConditionCol52 column,
                             DTCellValue52 value ) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if ( utils.hasValueList( column ) ) {
            final String[] valueList = utils.getValueList( column );
            return makeListBox( valueList,
                                pattern,
                                column,
                                value );

        } else if ( oracle.hasEnums( pattern.getFactType(),
                                     column.getFactField() ) ) {
            final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( pattern,
                                                                                                         column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = oracle.getEnums( pattern.getFactType(),
                                                     column.getFactField(),
                                                     currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[ 0 ],
                                    pattern,
                                    column,
                                    value );
            }
            return makeListBox( dd.getFixedList(),
                                pattern,
                                column,
                                value );
        }

        DataType.DataTypes type = utils.getTypeSafeType( pattern,
                                                         column );
        switch ( type ) {
            case NUMERIC:
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL:
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER:
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE:
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE:
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT:
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER:
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG:
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT:
                return makeNumericShortTextBox( value );
            case BOOLEAN:
                return makeBooleanSelector( value );
            case DATE:
                return makeDateSelector( value );
            default:
                return makeTextBox( value );
        }
    }

    /**
     * Make a DTCellValue for a column. This overloaded method takes a Pattern52
     * object as well since the ActionSetFieldCol52 column may be associated
     * with an unbound Pattern
     * @param p
     * @param c
     * @return
     */
    public DTCellValue52 makeNewValue( Pattern52 p,
                                       ActionSetFieldCol52 c ) {
        DataType.DataTypes type = utils.getTypeSafeType( p,
                                                         c );
        return new DTCellValue52( type,
                                  allowEmptyValues );
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List"). This overloaded method takes a
     * Pattern52 object as well since the ActionSetFieldCol52 column may be
     * associated with an unbound Pattern
     * @param pattern
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget( Pattern52 pattern,
                             ActionSetFieldCol52 column,
                             DTCellValue52 value ) {

        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if ( utils.hasValueList( column ) ) {
            final String[] valueList = utils.getValueList( column );
            return makeListBox( valueList,
                                pattern,
                                column,
                                value );

        } else if ( oracle.hasEnums( pattern.getFactType(),
                                     column.getFactField() ) ) {
            final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( pattern,
                                                                                                         column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = oracle.getEnums( pattern.getFactType(),
                                                     column.getFactField(),
                                                     currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[ 0 ],
                                    pattern,
                                    column,
                                    value );
            }
            return makeListBox( dd.getFixedList(),
                                pattern,
                                column,
                                value );
        }

        DataType.DataTypes type = utils.getTypeSafeType( pattern,
                                                         column );
        switch ( type ) {
            case NUMERIC:
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL:
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER:
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE:
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE:
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT:
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER:
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG:
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT:
                return makeNumericShortTextBox( value );
            case BOOLEAN:
                return makeBooleanSelector( value );
            case DATE:
                return makeDateSelector( value );
            default:
                return makeTextBox( value );
        }
    }

    /**
     * Get a Widget to edit a DTCellValue. A value is explicitly provided as
     * some columns (in the future) will have multiple DTCellValues (for
     * "Default Value" and "Option List").
     * @param column
     * @param value
     * @return
     */
    public Widget getWidget( ActionInsertFactCol52 column,
                             DTCellValue52 value ) {
        //Check if the column has a "Value List" or an enumeration. Value List takes precedence
        if ( utils.hasValueList( column ) ) {
            final String[] valueList = utils.getValueList( column );
            return makeListBox( valueList,
                                column,
                                value );

        } else if ( oracle.hasEnums( column.getFactType(),
                                     column.getFactField() ) ) {
            final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( column );
            final Map<String, String> currentValueMap = dropDownManager.getCurrentValueMap( context );
            final DropDownData dd = oracle.getEnums( column.getFactType(),
                                                     column.getFactField(),
                                                     currentValueMap );
            if ( dd == null ) {
                return makeListBox( new String[ 0 ],
                                    column,
                                    value );
            }
            return makeListBox( dd.getFixedList(),
                                column,
                                value );
        }

        DataType.DataTypes type = utils.getTypeSafeType( column );
        switch ( type ) {
            case NUMERIC:
                return makeNumericTextBox( value );
            case NUMERIC_BIGDECIMAL:
                return makeNumericBigDecimalTextBox( value );
            case NUMERIC_BIGINTEGER:
                return makeNumericBigIntegerTextBox( value );
            case NUMERIC_BYTE:
                return makeNumericByteTextBox( value );
            case NUMERIC_DOUBLE:
                return makeNumericDoubleTextBox( value );
            case NUMERIC_FLOAT:
                return makeNumericFloatTextBox( value );
            case NUMERIC_INTEGER:
                return makeNumericIntegerTextBox( value );
            case NUMERIC_LONG:
                return makeNumericLongTextBox( value );
            case NUMERIC_SHORT:
                return makeNumericShortTextBox( value );
            case BOOLEAN:
                return makeBooleanSelector( value );
            case DATE:
                return makeDateSelector( value );
            default:
                return makeTextBox( value );
        }
    }

    private ListBox makeBooleanSelector( final DTCellValue52 value ) {
        final ListBox lb = new ListBox();
        int indexTrue = 0;
        int indexFalse = 1;

        if ( allowEmptyValues ) {
            indexTrue = 1;
            indexFalse = 2;
            lb.addItem( GuidedDecisionTableConstants.INSTANCE.Choose(),
                        "" );
        }

        lb.addItem( "true" );
        lb.addItem( "false" );
        Boolean currentItem = value.getBooleanValue();
        if ( currentItem == null ) {
            lb.setSelectedIndex( 0 );
        } else {
            lb.setSelectedIndex( currentItem ? indexTrue : indexFalse );

        }

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    final String txtValue = lb.getValue( lb.getSelectedIndex() );
                    Boolean boolValue = ( txtValue.equals( "" ) ? null : txtValue.equals( "true" ) );
                    value.setBooleanValue( boolValue );
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox( final String[] completions,
                                 final Pattern52 basePattern,
                                 final ConditionCol52 baseCondition,
                                 final DTCellValue52 dcv ) {
        final boolean isMultipleSelect = isExplicitListOperator( baseCondition.getOperator() );
        final ListBox lb = makeListBox( completions,
                                        isMultipleSelect,
                                        dcv );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    String value = null;
                    if ( lb.isMultipleSelect() ) {
                        for ( int i = 0; i < lb.getItemCount(); i++ ) {
                            if ( lb.isItemSelected( i ) ) {
                                if ( value == null ) {
                                    value = lb.getValue( i );
                                } else {
                                    value = value + "," + lb.getValue( i );
                                }
                            }
                        }
                    } else {
                        int index = lb.getSelectedIndex();
                        if ( index > -1 ) {
                            //Set base column value
                            value = lb.getValue( index );
                        }
                    }

                    dcv.setStringValue( value );

                    //Update any dependent enumerations
                    final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( basePattern,
                                                                                                                 baseCondition );
                    Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                    for ( Integer iCol : dependentColumnIndexes ) {
                        BaseColumn column = model.getExpandedColumns().get( iCol );
                        if ( column instanceof LimitedEntryCol ) {
                            ( (LimitedEntryCol) column ).setValue( null );
                        } else if ( column instanceof DTColumnConfig52 ) {
                            ( (DTColumnConfig52) column ).setDefaultValue( null );
                        }
                    }
                }
            } );
        }
        return lb;
    }

    private boolean isExplicitListOperator( final String operator ) {
        final List<String> ops = Arrays.asList( OperatorsOracle.EXPLICIT_LIST_OPERATORS );
        return ops.contains( operator );
    }

    private ListBox makeListBox( final String[] completions,
                                 final Pattern52 basePattern,
                                 final ActionSetFieldCol52 baseAction,
                                 final DTCellValue52 value ) {
        final ListBox lb = makeListBox( completions,
                                        value );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        //Set base column value
                        value.setStringValue( lb.getValue( index ) );

                        //Update any dependent enumerations
                        final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( basePattern,
                                                                                                                     baseAction );
                        Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                        for ( Integer iCol : dependentColumnIndexes ) {
                            BaseColumn column = model.getExpandedColumns().get( iCol );
                            if ( column instanceof LimitedEntryCol ) {
                                ( (LimitedEntryCol) column ).setValue( null );
                            } else if ( column instanceof DTColumnConfig52 ) {
                                ( (DTColumnConfig52) column ).setDefaultValue( null );
                            }
                        }
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox( final String[] completions,
                                 final ActionInsertFactCol52 baseAction,
                                 final DTCellValue52 value ) {
        final ListBox lb = makeListBox( completions,
                                        value );

        // Wire up update handler
        lb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            lb.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        //Set base column value
                        value.setStringValue( lb.getValue( index ) );

                        //Update any dependent enumerations
                        final LimitedEntryDropDownManager.Context context = new LimitedEntryDropDownManager.Context( baseAction );
                        Set<Integer> dependentColumnIndexes = dropDownManager.getDependentColumnIndexes( context );
                        for ( Integer iCol : dependentColumnIndexes ) {
                            BaseColumn column = model.getExpandedColumns().get( iCol );
                            if ( column instanceof LimitedEntryCol ) {
                                ( (LimitedEntryCol) column ).setValue( null );
                            } else if ( column instanceof DTColumnConfig52 ) {
                                ( (DTColumnConfig52) column ).setDefaultValue( null );
                            }
                        }
                    } else {
                        value.setStringValue( null );
                    }
                }

            } );
        }
        return lb;
    }

    private ListBox makeListBox( final String[] completions,
                                 final DTCellValue52 value ) {
        return makeListBox( completions,
                            false,
                            value );
    }

    private ListBox makeListBox( final String[] completions,
                                 final boolean isMultipleSelect,
                                 final DTCellValue52 value ) {
        int selectedIndex = -1;
        final ListBox lb = new ListBox( isMultipleSelect );

        if ( allowEmptyValues ) {
            lb.addItem( GuidedDecisionTableConstants.INSTANCE.Choose(),
                        "" );
        }

        String currentItem = value.getStringValue() == null ? "" : value.getStringValue();
        List<String> currentItems = Arrays.asList( currentItem.split( "," ) );
        int selectedIndexOffset = ( allowEmptyValues ? 1 : 0 );
        for ( int i = 0; i < completions.length; i++ ) {
            String item = completions[ i ].trim();
            String[] splut = ConstraintValueHelper.splitValue(item);
            lb.addItem( splut[ 1 ],
                        splut[ 0 ] );
            lb.setItemSelected( i + selectedIndexOffset,
                                currentItems.contains( splut[ 0 ] ) );
            selectedIndex = i + selectedIndexOffset;
        }

        //If nothing has been selected, select the first value
        if ( selectedIndex == -1 ) {
            if ( lb.getItemCount() > 0 ) {
                lb.setSelectedIndex( 0 );
                value.setStringValue( lb.getValue( 0 ) );
            } else {
                value.setStringValue( null );
            }
        }

        return lb;
    }

    private AbstractRestrictedEntryTextBox makeNumericTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericTextBox( allowEmptyValues );
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toPlainString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new BigDecimal( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (BigDecimal) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigDecimal.ZERO );
                            tb.setValue( BigDecimal.ZERO.toPlainString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigDecimalTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigDecimalTextBox( allowEmptyValues );
        final BigDecimal numericValue = (BigDecimal) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toPlainString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new BigDecimal( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (BigDecimal) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigDecimal.ZERO );
                            tb.setValue( BigDecimal.ZERO.toPlainString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericBigIntegerTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericBigIntegerTextBox( allowEmptyValues );
        final BigInteger numericValue = (BigInteger) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new BigInteger( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (BigInteger) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( BigInteger.ZERO );
                            tb.setValue( BigInteger.ZERO.toString() );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericByteTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericByteTextBox( allowEmptyValues );
        final Byte numericValue = (Byte) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Byte( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Byte) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Byte( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericDoubleTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericDoubleTextBox( allowEmptyValues );
        final Double numericValue = (Double) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Double( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Double) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Double( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericFloatTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericFloatTextBox( allowEmptyValues );
        final Float numericValue = (Float) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Float( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Float) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Float( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericIntegerTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericIntegerTextBox( allowEmptyValues );
        final Integer numericValue = (Integer) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Integer( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Integer) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Integer( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericLongTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericLongTextBox( allowEmptyValues );
        final Long numericValue = (Long) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Long( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Long) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Long( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private AbstractRestrictedEntryTextBox makeNumericShortTextBox( final DTCellValue52 value ) {
        final AbstractRestrictedEntryTextBox tb = new NumericShortTextBox( allowEmptyValues );
        final Short numericValue = (Short) value.getNumericValue();
        tb.setValue( numericValue == null ? "" : numericValue.toString() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    try {
                        value.setNumericValue( new Short( event.getValue() ) );
                    } catch ( NumberFormatException nfe ) {
                        if ( allowEmptyValues ) {
                            value.setNumericValue( (Short) null );
                            tb.setValue( "" );
                        } else {
                            value.setNumericValue( new Short( "0" ) );
                            tb.setValue( "0" );
                        }
                    }
                }

            } );
        }
        return tb;
    }

    private TextBox makeTextBox( final DTCellValue52 value ) {
        TextBox tb = new TextBox();
        tb.setValue( value.getStringValue() );

        // Wire up update handler
        tb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                public void onValueChange( ValueChangeEvent<String> event ) {
                    value.setStringValue( event.getValue() );
                }

            } );
        }
        return tb;
    }

    private Widget makeDateSelector( final DTCellValue52 value ) {
        //If read-only return a label
        if ( isReadOnly ) {
            Label dateLabel = new Label();
            dateLabel.setText( format.format( value.getDateValue() ) );
            return dateLabel;
        }

        PopupDatePicker dp = new PopupDatePicker( allowEmptyValues );
        if ( value.getDateValue() != null ) {
            dp.setValue( value.getDateValue() );
        }

        // Wire up update handler
        dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange( ValueChangeEvent<Date> event ) {
                value.setDateValue( event.getValue() );
            }

        } );
        return dp;
    }

    /**
     * An editor for whether the column is hidden or not
     * @param col
     * @return
     */
    public static CheckBox getHideColumnIndicator( final DTColumnConfig52 col ) {
        final CheckBox chkHide = new CheckBox();
        chkHide.setValue( col.isHideColumn() );
        chkHide.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent sender ) {
                col.setHideColumn( chkHide.getValue() );
            }
        } );
        return chkHide;
    }

}
