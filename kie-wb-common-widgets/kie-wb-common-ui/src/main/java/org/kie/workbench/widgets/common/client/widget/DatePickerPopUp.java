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

package org.kie.workbench.widgets.common.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import org.kie.workbench.widgets.common.client.resources.i18n.CommonConstants;

import java.util.Date;

public class DatePickerPopUp extends PopupPanel {

    private Label timeColonLabel = new Label( ":" );

    private Label timeHyphenLabel = new Label( " - " );

    // It is show time! (When to show time next to date selectors)
    protected boolean showTime = false;

    protected ListBox years  = new ListBox();
    protected ListBox months = new ListBox();
    protected ListBox dates  = new ListBox();

    protected ListBox hours   = new ListBox();
    protected ListBox minutes = new ListBox();

    /**
     * @param okClickHandler What to do when ok, is pressed
     * @param showTime Can you select time too.
     */
    public DatePickerPopUp( ClickHandler okClickHandler,
                            DateTimeFormat formatter ) {

        setGlassEnabled( true );
        HorizontalPanel horizontalPanel = new HorizontalPanel();

        // Add years
        // Take the current year and add 50 to each sides
        Date now = new Date();
        int year = now.getYear() + 1900 - 50;
        for ( int i = year; i < ( year + 100 ); i++ ) {
            years.addItem( Integer.toString( i ) );
        }
        years.setSelectedIndex( 50 );
        horizontalPanel.add( years );

        // Add months
        months.addItem( CommonConstants.INSTANCE.January() );
        months.addItem( CommonConstants.INSTANCE.February() );
        months.addItem( CommonConstants.INSTANCE.March() );
        months.addItem( CommonConstants.INSTANCE.April() );
        months.addItem( CommonConstants.INSTANCE.May() );
        months.addItem( CommonConstants.INSTANCE.June() );
        months.addItem( CommonConstants.INSTANCE.July() );
        months.addItem( CommonConstants.INSTANCE.August() );
        months.addItem( CommonConstants.INSTANCE.September() );
        months.addItem( CommonConstants.INSTANCE.October() );
        months.addItem( CommonConstants.INSTANCE.November() );
        months.addItem( CommonConstants.INSTANCE.December() );

        months.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                fillDates();
            }
        } );

        horizontalPanel.add( months );

        // Add dates
        fillDates();
        horizontalPanel.add( dates );

        showTime = hasTime( formatter );
        if ( showTime ) {
            // Hours
            for ( int i = 0; i < 24; i++ ) {
                hours.addItem( Integer.toString( i ) );
            }
            horizontalPanel.add( timeHyphenLabel );
            horizontalPanel.add( hours );

            // Minutes 
            for ( int i = 0; i < 60; i++ ) {
                minutes.addItem( Integer.toString( i ) );
            }
            horizontalPanel.add( timeColonLabel );
            horizontalPanel.add( minutes );
        }

        Button okButton = new Button( CommonConstants.INSTANCE.OK() );
        okButton.addClickHandler( okClickHandler );
        horizontalPanel.add( okButton );

        add( horizontalPanel );
    }

    /**
     * Simple check, if time format has hours it has time.
     * @param formatter
     * @return
     */
    private boolean hasTime( DateTimeFormat formatter ) {
        return formatter.getPattern().contains( "h" ) || formatter.getPattern().contains( "H" ) || formatter.getPattern().contains( "k" ) || formatter.getPattern().contains( "K" );
    }

    private void setTimeVisible( boolean visible ) {
        hours.setVisible( visible );
        minutes.setVisible( visible );
        timeHyphenLabel.setVisible( visible );
        timeColonLabel.setVisible( visible );
    }

    /**
     * Sets the current year, month ect to dropdowns.
     */
    public void setDropdowns( DateTimeFormat formatter,
                              String text ) {
        Date date;
        try {
            date = formatter.parse( text );
        } catch ( Exception e ) {
            date = new Date();
        }

        // Set year
        years.clear();
        int year = date.getYear() + 1900 - 50;
        for ( int i = 0; i < 100; i++ ) {
            years.addItem( Integer.toString( year ) );
            if ( year == ( date.getYear() + 1900 ) ) {
                years.setSelectedIndex( i );
            }
            year++;
        }

        // month
        months.setSelectedIndex( date.getMonth() );
        // day
        dates.setSelectedIndex( date.getDate() - 1 );

        setTimeVisible( showTime );

        if ( showTime ) {
            // hours
            hours.setSelectedIndex( date.getHours() );

            // minutes
            minutes.setSelectedIndex( date.getMinutes() );
        }
    }

    private void fillDates() {
        setVisible( false );

        dates.clear();

        // Check month 
        int days = daysInMonth( months.getSelectedIndex() + 1 );

        for ( int i = 1; i <= days; i++ ) {
            dates.addItem( Integer.toString( i ) );
        }

        setVisible( true );
    }

    private int daysInMonth( int month ) {
        switch ( month ) {
            case 2:
                // Can be 28 or 29, returns 29 just in case
                return 29;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

}
