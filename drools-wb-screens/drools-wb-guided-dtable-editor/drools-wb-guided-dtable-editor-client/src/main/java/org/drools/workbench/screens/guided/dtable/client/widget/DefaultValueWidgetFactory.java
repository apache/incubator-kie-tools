/*
 * Copyright 2013 JBoss Inc
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

import java.util.Date;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.guvnor.common.services.shared.config.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.widget.PopupDatePicker;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;

/**
 * Factory for Default Value widgets
 */
public class DefaultValueWidgetFactory {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
    private static final DateTimeFormat format = DateTimeFormat.getFormat( DATE_FORMAT );

    /**
     * BZ-996932: Default value changed event handler interface.
     *
     * Widgets using DefaultValueWidget should listen to value changed event by implementing this custom GWT Event Handler.
     */
    interface DefaultValueChangedEventHandler {
        void onDefaultValueChanged(DefaultValueChangedEvent event);
    }

    /**
     * BZ-996932: Default value changed event definition.
     *
     * When default value is changed this event if fired to notified handlers.
     */
    static class DefaultValueChangedEvent {

        private final DTCellValue52 defaultValue;
        private final DTCellValue52 oldDefaultValue;

        public DefaultValueChangedEvent(DTCellValue52 defaultValue, DTCellValue52 oldDefaultValue) {
            this.defaultValue = defaultValue;
            this.oldDefaultValue = oldDefaultValue;
        }

        public DTCellValue52 getDefaultValue() {
            return defaultValue;
        }

        public DTCellValue52 getOldDefaultValue() {
            return oldDefaultValue;
        }

    }

    // BZ-996932: Added value change notifications.
    public static Widget getDefaultValueWidget( final AttributeCol52 ac,
                                        final boolean isReadOnly,
                                        final DefaultValueChangedEventHandler defaultValueChangedEventHandler ) {
        Widget editor = null;
        final String attributeName = ac.getAttribute();
        if ( attributeName.equals( RuleAttributeWidget.RULEFLOW_GROUP_ATTR )
                || attributeName.equals( RuleAttributeWidget.AGENDA_GROUP_ATTR )
                || attributeName.equals( RuleAttributeWidget.ACTIVATION_GROUP_ATTR )
                || attributeName.equals( RuleAttributeWidget.TIMER_ATTR )
                || attributeName.equals( RuleAttributeWidget.CALENDARS_ATTR ) ) {
            final TextBox tb = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( "" ) );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            tb.setValue( defaultValue.getStringValue() );
            tb.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {
                tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                    public void onValueChange( ValueChangeEvent<String> event ) {
                        DTCellValue52 clonedDefaultValue = defaultValue.cloneDefaultValueCell();
                        defaultValue.setStringValue( tb.getValue() );
                        defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue, clonedDefaultValue));
                    }

                } );
            }
            editor = tb;

        } else if ( attributeName.equals( RuleAttributeWidget.SALIENCE_ATTR ) ) {
            final TextBox tb = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_INTEGER );
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( 0 ) );
            } else {
                assertIntegerDefaultValue( ac.getDefaultValue() );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Integer numericValue = (Integer) defaultValue.getNumericValue();
            tb.setValue( numericValue == null ? "" : numericValue.toString() );
            tb.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {
                tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                    public void onValueChange( ValueChangeEvent<String> event ) {
                        DTCellValue52 clonedDefaultValue = defaultValue.cloneDefaultValueCell();
                        try {
                            defaultValue.setNumericValue( Integer.valueOf( event.getValue() ) );
                        } catch ( NumberFormatException nfe ) {
                            defaultValue.setNumericValue( 0 );
                            tb.setValue( "0" );
                        } finally {
                            defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue, clonedDefaultValue));
                        }
                    }

                } );
            }
            editor = tb;

        } else if ( attributeName.equals( RuleAttributeWidget.DURATION_ATTR ) ) {
            final TextBox tb = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_LONG );
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( 0L ) );
            } else {
                assertLongDefaultValue( ac.getDefaultValue() );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Long numericValue = (Long) defaultValue.getNumericValue();
            tb.setValue( numericValue == null ? "" : numericValue.toString() );
            tb.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {
                tb.addValueChangeHandler( new ValueChangeHandler<String>() {

                    public void onValueChange( ValueChangeEvent<String> event ) {
                        DTCellValue52 clonedDefaultValue = defaultValue.cloneDefaultValueCell();
                        try {
                            defaultValue.setNumericValue( Long.valueOf( event.getValue() ) );
                        } catch ( NumberFormatException nfe ) {
                            defaultValue.setNumericValue( 0L );
                            tb.setValue( "0" );
                        } finally {
                            defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue, clonedDefaultValue));
                        }
                    }

                } );
            }
            editor = tb;

        } else if ( attributeName.equals( RuleAttributeWidget.NO_LOOP_ATTR )
                || attributeName.equals( RuleAttributeWidget.LOCK_ON_ACTIVE_ATTR )
                || attributeName.equals( RuleAttributeWidget.AUTO_FOCUS_ATTR )
                || attributeName.equals( RuleAttributeWidget.ENABLED_ATTR )
                || attributeName.equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
            final CheckBox cb = new CheckBox();
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( Boolean.FALSE ) );
            } else {
                assertBooleanDefaultValue( ac.getDefaultValue() );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final Boolean booleanValue = defaultValue.getBooleanValue();
            cb.setEnabled( !isReadOnly );
            if ( booleanValue == null ) {
                cb.setValue( false );
                defaultValue.setBooleanValue( Boolean.FALSE );
            } else {
                cb.setValue( booleanValue );
            }

            cb.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    DTCellValue52 clonedDefaultValue = defaultValue.cloneDefaultValueCell();
                    defaultValue.setBooleanValue( cb.getValue() );
                    defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue, clonedDefaultValue));
                }
            } );
            editor = cb;

        } else if ( attributeName.equals( RuleAttributeWidget.DATE_EFFECTIVE_ATTR )
                || attributeName.equals( RuleAttributeWidget.DATE_EXPIRES_ATTR ) ) {
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( new Date() ) );
            } else {
                assertDateDefaultValue( ac.getDefaultValue() );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            if ( isReadOnly ) {
                final TextBox tb = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
                tb.setValue( format.format( defaultValue.getDateValue() ) );
                tb.setEnabled( false );
            } else {
                final PopupDatePicker dp = new PopupDatePicker( false );
                final Date dateValue = defaultValue.getDateValue();
                dp.setValue( dateValue );

                dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

                    public void onValueChange( ValueChangeEvent<Date> event ) {
                        DTCellValue52 clonedDefaultValue = defaultValue.cloneDefaultValueCell();
                        defaultValue.setDateValue( event.getValue() );
                        defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(defaultValue, clonedDefaultValue));
                    }

                } );
                editor = dp;
            }
        } else if ( attributeName.equals( RuleAttributeWidget.DIALECT_ATTR ) ) {
            final ListBox lb = new ListBox();
            lb.addItem( RuleAttributeWidget.DIALECTS[ 0 ] );
            lb.addItem( RuleAttributeWidget.DIALECTS[ 1 ] );
            if ( ac.getDefaultValue() == null ) {
                ac.setDefaultValue( new DTCellValue52( RuleAttributeWidget.DIALECTS[ 1 ] ) );
            }
            final DTCellValue52 defaultValue = ac.getDefaultValue();
            final String stringValue = defaultValue.getStringValue();
            lb.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {
                lb.addChangeHandler( new ChangeHandler() {
                    @Override
                    public void onChange( ChangeEvent event ) {
                        final int selectedIndex = lb.getSelectedIndex();
                        if ( selectedIndex < 0 ) {
                            return;
                        }
                        DTCellValue52 newValue = new DTCellValue52( lb.getValue( selectedIndex ) );
                        ac.setDefaultValue( newValue );
                        defaultValueChangedEventHandler.onDefaultValueChanged(new DefaultValueChangedEvent(newValue, defaultValue));
                    }
                } );
            }
            if ( stringValue == null || stringValue.isEmpty() ) {
                lb.setSelectedIndex( 1 );
                defaultValue.setStringValue( RuleAttributeWidget.DIALECTS[ 1 ] );
            } else if ( stringValue.equals( RuleAttributeWidget.DIALECTS[ 0 ] ) ) {
                lb.setSelectedIndex( 0 );
            } else if ( stringValue.equals( RuleAttributeWidget.DIALECTS[ 1 ] ) ) {
                lb.setSelectedIndex( 1 );
            } else {
                lb.setSelectedIndex( 1 );
                defaultValue.setStringValue( RuleAttributeWidget.DIALECTS[ 1 ] );
            }
            editor = lb;
        }
        return editor;
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertIntegerDefaultValue( final DTCellValue52 dcv ) {
        if ( dcv.getNumericValue() == null ) {
            try {
                dcv.setNumericValue( Integer.valueOf ( dcv.getStringValue() ) );
            } catch ( NumberFormatException nfe ) {
                dcv.setNumericValue( 0 );
            }
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertLongDefaultValue( final DTCellValue52 dcv ) {
        if ( dcv.getNumericValue() == null ) {
            try {
                dcv.setNumericValue( Long.valueOf( dcv.getStringValue() ) );
            } catch ( NumberFormatException nfe ) {
                dcv.setNumericValue( 0L );
            }
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertBooleanDefaultValue( final DTCellValue52 dcv ) {
        if ( dcv.getBooleanValue() == null ) {
            dcv.setBooleanValue( Boolean.valueOf( dcv.getStringValue() ) );
        }
    }

    //Legacy DefaultValues always used String to store the value; so attempt to convert it
    private static void assertDateDefaultValue( final DTCellValue52 dcv ) {
        if ( dcv.getDateValue() == null ) {
            try {
                dcv.setDateValue( format.parse( dcv.getStringValue() ) );
            } catch ( IllegalArgumentException eae ) {
                dcv.setDateValue( new Date() );
            }
        }
    }

}
