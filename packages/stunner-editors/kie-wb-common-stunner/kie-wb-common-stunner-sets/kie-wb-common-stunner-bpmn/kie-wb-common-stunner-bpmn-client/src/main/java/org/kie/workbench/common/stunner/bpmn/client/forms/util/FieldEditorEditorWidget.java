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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import javax.annotation.PostConstruct;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public abstract class FieldEditorEditorWidget<T, E extends FieldEditorPresenter<T>>
        extends Composite
        implements HasValue<T> {

    protected E editor;

    public FieldEditorEditorWidget(final E editor) {
        this.editor = editor;
    }

    @PostConstruct
    public void init() {
        initWidget(getWrapperWidget(editor.getView().getElement()));
        editor.addChangeHandler(this::notifyChange);
    }

    public void setReadOnly(final boolean readOnly) {
        editor.setReadOnly(readOnly);
    }

    @Override
    public T getValue() {
        return editor.getValue();
    }

    @Override
    public void setValue(final T value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final T value,
                         final boolean fireEvents) {
        T oldValue = editor.getValue();
        editor.setValue(value);
        if (fireEvents) {
            notifyChange(oldValue,
                         value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    protected void notifyChange(T oldValue,
                                T newValue) {
        ValueChangeEvent.fireIfNotEqual(this,
                                        oldValue,
                                        newValue);
    }

    protected Widget getWrapperWidget(HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }
}