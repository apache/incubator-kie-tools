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


package org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;

public class BindableTypeAhead<T> implements IsWidget,
                                             HasValue<T> {

    protected T value;

    private BindableTypeAheadView view;

    @Inject
    public BindableTypeAhead(BindableTypeAheadView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void init(String mask,
                     Dataset<T> dataset) {
        view.init(dataset,
                  mask);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(T value,
                         boolean fireEvents) {
        if (this.value == null) {
            this.value = value;
        } else if (this.value.equals(value)) {
            return;
        }

        this.value = value;

        view.setValue(value);

        if (fireEvents) {
            ValueChangeEvent.fire(this,
                                  value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> valueChangeHandler) {
        return view.asWidget().addHandler(valueChangeHandler,
                                          ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }
}
