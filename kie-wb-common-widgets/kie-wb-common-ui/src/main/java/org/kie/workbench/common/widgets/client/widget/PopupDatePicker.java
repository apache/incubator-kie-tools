/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.widgets.client.widget;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.datepicker.client.DatePicker;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

/**
 * A Date Picker that renders its value as a Label. When the Label is clicked on
 * a pop-up Date Picker is shown from which the value can be changed. Unlike
 * GWT's DateBox you cannot enter the value as text. This was preferred to
 * prevent the user from not entering a date which is possible with DateBox.
 */
public class PopupDatePicker extends Composite
        implements
        HasValue<Date>,
        HasValueChangeHandlers<Date> {

    private final TextBox txtDate;
    private final PopupPanel panel;

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    private static final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

    private Date date;
    private final DatePicker datePicker;
    private final DateTimeFormat format;
    private final boolean allowEmptyValue;

    public PopupDatePicker( final boolean allowEmptyValue ) {

        HorizontalPanel container = new HorizontalPanel();

        this.allowEmptyValue = allowEmptyValue;
        this.txtDate = new DateTextBox( allowEmptyValue );
        this.format = DateTimeFormat.getFormat( DATE_FORMAT );
        this.datePicker = new DatePicker();

        // Pressing ESCAPE dismisses the pop-up loosing any changes
        this.panel = new PopupPanel( true,
                                     true ) {
            @Override
            protected void onPreviewNativeEvent( NativePreviewEvent event ) {
                if ( Event.ONKEYUP == event.getTypeInt() ) {
                    if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ) {
                        panel.hide();
                    }
                }
            }

        };

        // Closing the pop-up commits the change
        panel.addCloseHandler( new CloseHandler<PopupPanel>() {

            public void onClose( CloseEvent<PopupPanel> event ) {
                setValue( datePicker.getValue() );
            }
        } );

        // Hide the panel and update our value when a date is selected
        datePicker.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange( ValueChangeEvent<Date> event ) {
                setValue( event.getValue() );
                panel.hide();
            }
        } );

        panel.add( datePicker );
        panel.getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        //Add an icon to select a Date value
        Image imgCalendar = new Image( CommonImages.INSTANCE.calendar() );
        imgCalendar.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                datePicker.setValue( getValue() );
                panel.setPopupPositionAndShow( new PositionCallback() {
                    public void setPosition( int offsetWidth,
                                             int offsetHeight ) {
                        panel.setPopupPosition( txtDate.getAbsoluteLeft(),
                                                txtDate.getAbsoluteTop() + txtDate.getOffsetHeight() );
                    }
                } );
            }

        } );

        //Changes to the TextBox need to be copied to the widget state
        txtDate.addBlurHandler( new BlurHandler() {

            @Override
            public void onBlur( BlurEvent event ) {
                final String value = txtDate.getText();
                try {
                    setValue( DATE_FORMATTER.parseStrict( txtDate.getText() ) );
                } catch ( IllegalArgumentException iae ) {
                    if ( ( "".equals( value ) && allowEmptyValue ) ) {
                        setValue( (Date) null );
                    } else {
                        setValue( new Date() );
                    }
                }
            }

        } );

        container.add( txtDate );
        container.add( imgCalendar );

        initWidget( container );
    }

    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Date> handler ) {
        return datePicker.addValueChangeHandler( handler );
    }

    public Date getValue() {
        return date;
    }

    public void setValue( Date value ) {
        setValue( value,
                  true );
    }

    public void setValue( Date value,
                          boolean fireEvents ) {
        this.date = value;
        if ( value != null ) {
            this.datePicker.setValue( value,
                                      true );
            this.txtDate.setText( format.format( value ) );
        } else {
            this.txtDate.setText( "" );
        }
    }

    public void setValue( String value ) {
        setValue( value,
                  true );
    }

    public void setValue( String value,
                          boolean fireEvents ) {
        try {
            setValue( DATE_FORMATTER.parseStrict( value ) );
        } catch ( IllegalArgumentException iae ) {
            if ( ( "".equals( value ) && allowEmptyValue ) ) {
                setValue( (Date) null );
            } else {
                setValue( new Date() );
            }
        }
    }

    public static String convertToString( final ValueChangeEvent<Date> event ) {
        if ( event == null || event.getValue() == null ) {
            return "";
        }
        return DATE_FORMATTER.format( event.getValue() );
    }

}
