/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FieldDatePickerTest {

    @GwtMock
    DateTimeFormat dateTimeFormat;

    private FieldDatePickerView view;
    private FieldDatePicker presenter;

    private String selectedTime;
    private Date date = new Date( 1 );
    private Date otherDate = new Date( 2 );

    @Before
    public void setUp() throws Exception {
        HashMap<String, String> preferences = new HashMap<String, String>();
        preferences.put( ApplicationPreferences.DATE_FORMAT, "dd-MMM-yyyy" );
        ApplicationPreferences.setUp( preferences );
        view = mock( FieldDatePickerView.class );
        presenter = new FieldDatePicker( view ) {
            @Override protected DateTimeFormat getFormat() {
                return dateTimeFormat;
            }
        };

        when( dateTimeFormat.parse( "" ) ).thenThrow( new IllegalArgumentException() ); // The parser does not like empty strings
        when( dateTimeFormat.format( date ) ).thenReturn( "some date" );
        when( dateTimeFormat.format( otherDate ) ).thenReturn( "some other date" );

        presenter.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                selectedTime = event.getValue();
            }
        } );
    }

    @Test
    public void testPresenterSet() throws Exception {
        verify( view ).setPresenter( presenter );
    }

    @Test
    public void testSetEmptyStringAsValue() throws Exception {
        presenter.setValue( "" );

    }

    @Test
    public void testSelectDate() throws Exception {

        presenter.onDateSelected( date );

        assertEquals( "some date", selectedTime );

        presenter.onDateSelected( otherDate );

        assertEquals( "some other date", selectedTime );
    }

}