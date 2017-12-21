/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

public class TimerSettingsFieldEditorWidget
        extends Composite
        implements HasValue<TimerSettingsValue> {

    private final TimerSettingsFieldEditorPresenter editor;

    @Inject
    public TimerSettingsFieldEditorWidget(final TimerSettingsFieldEditorPresenter editor) {
        this.editor = editor;
    }

    @PostConstruct
    public void init() {
        initWidget(getWrapperWidget(editor.getView().getElement()));
        editor.addChangeHandler(this::notifyChange);
    }

    @Override
    public TimerSettingsValue getValue() {
        return editor.getValue();
    }

    @Override
    public void setValue(final TimerSettingsValue value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final TimerSettingsValue value,
                         final boolean fireEvents) {
        TimerSettingsValue oldValue = editor.getValue();
        editor.setValue(value);
        if (fireEvents) {
            notifyChange(oldValue,
                         value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TimerSettingsValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    protected void notifyChange(TimerSettingsValue oldValue,
                                TimerSettingsValue newValue) {
        ValueChangeEvent.fireIfNotEqual(this,
                                        oldValue,
                                        newValue);
    }

    protected Widget getWrapperWidget(HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }
}