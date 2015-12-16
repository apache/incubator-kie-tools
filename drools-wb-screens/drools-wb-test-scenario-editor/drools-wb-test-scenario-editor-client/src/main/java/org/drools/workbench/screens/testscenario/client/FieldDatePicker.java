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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

public class FieldDatePicker
        implements IsWidget,
                   HasValueChangeHandlers<String> {

    private final DateTimeFormat formatter = getFormat();

    private FieldDatePickerView view;

    private HandlerManager handlerManager = new HandlerManager( this );

    public FieldDatePicker( final FieldDatePickerView view ) {
        this.view = view;
        view.setPresenter( this );
    }

    public void setValue( String value ) {
        if ( value != null && !value.trim().isEmpty() ) {
            view.setValue( formatter.parse( value ) );
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> handler ) {

        HandlerRegistration registration = new HandlerRegistration() {
            @Override
            public void removeHandler() {
                handlerManager.removeHandler( ValueChangeEvent.getType(), handler );
            }
        };

        handlerManager.addHandler( ValueChangeEvent.getType(), handler );

        return registration;
    }

    public void onDateSelected( Date value ) {
        ValueChangeEvent.fire( this, formatter.format( value ) );
    }

    @Override
    public void fireEvent( GwtEvent<?> event ) {
        handlerManager.fireEvent( event );
    }

    protected DateTimeFormat getFormat() {
        return DateTimeFormat.getFormat( ApplicationPreferences.getDroolsDateFormat() );
    }
}
