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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

public class FieldDatePickerViewImpl
        implements FieldDatePickerView {

    private DatePicker datePicker = new DatePicker();
    private FieldDatePicker presenter;

    public FieldDatePickerViewImpl() {
        datePicker.addValueChangeHandler( new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange( ValueChangeEvent<Date> event ) {
                presenter.onDateSelected( event.getValue() );
            }
        } );
    }

    @Override
    public void setValue( Date value ) {
        datePicker.setValue( value );
    }

    @Override
    public void setPresenter( FieldDatePicker presenter ) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return datePicker;
    }
}
