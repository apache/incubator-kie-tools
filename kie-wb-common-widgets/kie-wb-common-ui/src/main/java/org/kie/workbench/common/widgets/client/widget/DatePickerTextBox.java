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
import com.google.gwt.user.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.uberfire.client.common.ImageButton;

import java.util.Date;

public class DatePickerTextBox extends DatePicker {

    public DatePickerTextBox( String selectedDate ) {
        this( selectedDate,
              defaultFormat );
    }

    public DatePickerTextBox( String selectedDate,
                              String visualFormat ) {
        solveVisualFormat( visualFormat );

        visualFormatFormatter = DateTimeFormat.getFormat( this.visualFormat );

        datePickerPopUp = new DatePickerPopUp( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                try {
                    Date date = fillDate();

                    textWidget.setText( visualFormatFormatter.format( date ) );

                    valueChanged();
                    makeDirty();
                    datePickerPopUp.hide();
                } catch ( Exception e ) {
                    Window.alert( CommonConstants.INSTANCE.InvalidDateFormatMessage() );
                }
            }
        },
                                               visualFormatFormatter );

        ImageButton select = new ImageButton( CommonAltedImages.INSTANCE.Edit() );
        select.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                datePickerPopUp.setPopupPosition( textWidget.getAbsoluteLeft(),
                                                  textWidget.getAbsoluteTop() + 20 );

                if ( textWidget.getText() != null && "".equals( textWidget.getText() ) ) {
                    textWidget.setText( visualFormatFormatter.format( new Date() ) );
                }

                datePickerPopUp.setDropdowns( visualFormatFormatter,
                                              textWidget.getText() );
                datePickerPopUp.show();
            }
        } );

        if ( selectedDate != null && !selectedDate.equals( "" ) ) {
            textWidget.setText( selectedDate );

            // Check if there is a valid date set. If not, set this date.
            try {
                visualFormatFormatter.parse( selectedDate );
            } catch ( Exception e ) {
                selectedDate = null;
            }
        }

        textWidget.addBlurHandler( new BlurHandler() {
            public void onBlur( BlurEvent event ) {
                TextBox box = (TextBox) event.getSource();
                textWidget.setText( box.getText() );
                valueChanged();
                makeDirty();
                datePickerPopUp.hide();
            }
        } );

        panel.add( textWidget );
        panel.add( select );
        initWidget( panel );
    }

    public void clear() {
        textWidget.setText( "" );
    }
}
