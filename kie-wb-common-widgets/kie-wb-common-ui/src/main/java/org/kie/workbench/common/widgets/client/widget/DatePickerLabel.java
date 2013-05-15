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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

import java.util.Date;

public class DatePickerLabel extends DatePicker {

    protected Label labelWidget = new Label();

    public DatePickerLabel( String selectedDate ) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerLabel( String selectedDate,
                            String visualFormat ) {
        solveVisualFormat( visualFormat );

        visualFormatFormatter = DateTimeFormat.getFormat( this.visualFormat );

        datePickerPopUp = new DatePickerPopUp( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                try {
                    Date date = fillDate();

                    textWidget.setText( visualFormatFormatter.format( date ) );
                    labelWidget.setText( textWidget.getText() );

                    valueChanged();
                    makeDirty();
                    panel.clear();
                    panel.add( labelWidget );
                    datePickerPopUp.hide();
                } catch ( Exception e ) {
                    Window.alert( CommonConstants.INSTANCE.InvalidDateFormatMessage() );
                }

            }
        },
                                               visualFormatFormatter );

        labelWidget.setStyleName( "form-field" );

        labelWidget.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                panel.clear();
                panel.add( textWidget );
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                datePickerPopUp.setDropdowns( visualFormatFormatter,
                                              textWidget.getText() );
                datePickerPopUp.show();
            }
        } );

        // Check if there is a valid date set. If not, set this date.
        try {
            DateTimeFormat formatter = DateTimeFormat.getFormat( defaultFormat );
            Date date = formatter.parse( selectedDate );
            selectedDate = visualFormatFormatter.format( date );
        } catch ( Exception e ) {
            selectedDate = visualFormatFormatter.format( new Date() );
        }

        if ( selectedDate != null && !selectedDate.equals( "" ) ) {
            textWidget.setText( selectedDate );
            labelWidget.setText( selectedDate );
        }

        textWidget.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent event ) {
                TextBox box = (TextBox) event.getSource();
                textWidget.setText( box.getText() );
                labelWidget.setText( box.getText() );
                valueChanged();
                makeDirty();
                panel.clear();
                panel.add( labelWidget );
                datePickerPopUp.hide();
            }
        } );

        panel.add( labelWidget );
        initWidget( panel );
    }

}
