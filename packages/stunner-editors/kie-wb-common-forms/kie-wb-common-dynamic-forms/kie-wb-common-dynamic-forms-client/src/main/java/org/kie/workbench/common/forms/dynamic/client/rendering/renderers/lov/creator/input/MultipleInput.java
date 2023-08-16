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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;

@Dependent
public class MultipleInput<TYPE> implements IsWidget,
                                            MultipleInputView.Presenter<TYPE> {

    private MultipleInputView<TYPE> view;

    private MultipleInputComponent<TYPE> component;

    @Inject
    public MultipleInput(MultipleInputView<TYPE> view, MultipleInputComponent<TYPE> component) {
        this.view = view;
        this.component = component;
        this.view.init(this);

        component.setValueChangedCommand(() -> setValue(component.getValues(), true));
    }

    @Override
    public MultipleInputComponent getComponent() {
        return component;
    }

    @Override
    public List<TYPE> getValue() {
        return component.getValues();
    }

    @Override
    public void setValue(List<TYPE> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<TYPE> values,
                         boolean fireEvents) {

        if(!component.getValues().equals(values)) {
            component.setValues(values);
        }

        if(fireEvents) {
            ValueChangeEvent.fire(this,
                                  values);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<TYPE>> handler) {
        return view.asWidget().addHandler(handler,
                                          ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        asWidget().fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(String typeName) {
        component.init(typeName);
    }

    public void setPageSize(Integer pageSize) {
        component.setPageSize(pageSize);
    }

    public void setReadOnly(boolean readOnly) {
        component.setReadOnly(readOnly);
    }
}