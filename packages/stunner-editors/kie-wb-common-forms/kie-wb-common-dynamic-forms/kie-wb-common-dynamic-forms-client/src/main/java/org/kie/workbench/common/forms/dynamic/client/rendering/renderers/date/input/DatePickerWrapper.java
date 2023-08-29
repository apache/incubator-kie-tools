/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input;

import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class DatePickerWrapper implements IsWidget,
                                          DatePickerWrapperView.Presenter {

    private DatePickerWrapperView view;

    @Inject
    public DatePickerWrapper(DatePickerWrapperView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setValue(Date values) {
        setValue(values, false);
    }

    @Override
    public void setValue(Date value,
                         boolean fireEvents) {
        view.setDateValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public void setId(String id) {
        view.setId(id);
    }

    @Override
    public void setName(String name) {
        view.setName(name);
    }

    @Override
    public void setPlaceholder(String placeholder) {
        view.setPlaceholder(placeholder);
    }

    @Override
    public void setEnabled(boolean enabled) {
        view.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        view.addDateValueChangeHandler(handler);
        return view.asWidget().addHandler(handler,
                                          ValueChangeEvent.getType());
    }

    @Override
    public Date getValue() {
        return view.getDateValue();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        asWidget().fireEvent(event);
    }

    @Override
    public void setDatePickerWidget(boolean showTime) {
        view.setDatePickerWidget(showTime);
    }

    @Override
    public void disableActions() {
        view.disableActions();
    }
}
