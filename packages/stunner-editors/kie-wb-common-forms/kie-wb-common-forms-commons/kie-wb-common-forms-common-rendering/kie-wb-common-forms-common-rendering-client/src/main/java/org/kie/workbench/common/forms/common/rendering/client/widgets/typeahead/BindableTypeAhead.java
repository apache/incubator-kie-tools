/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead;

import javax.inject.Inject;

import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;
import org.gwtproject.event.legacy.shared.GwtEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.HasValue;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

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

    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }

    @Override
    public void fireEvent(Event<?> event) {
        view.asWidget().fireEvent(event);
    }
}
