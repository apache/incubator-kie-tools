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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget.NotificationWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;

@Dependent
@Templated
public class NotificationsEditorWidget extends Composite implements HasValue<NotificationTypeListValue> {

    @Inject
    @DataField
    private HTMLInputElement notificationTextBox;

    private NotificationTypeListValue notificationTypeListValue = new NotificationTypeListValue();

    @Inject
    @DataField
    private HTMLButtonElement notificationButton;

    @Inject
    private NotificationWidget notificationWidget;

    @PostConstruct
    public void init() {
        notificationButton.addEventListener("click", event -> showNotificationsDialog(), false);
        notificationTextBox.addEventListener("click", event -> showNotificationsDialog(), false);
    }

    void showNotificationsDialog() {
        notificationWidget.setValue(notificationTypeListValue.getValues()
                                            .stream()
                                            .map(NotificationRow::new)
                                            .collect(Collectors.toList()),
                                    true);

        notificationWidget.setCallback(data -> setValue(data,
                                                        true));
        notificationWidget.show();
    }

    @Override
    public void setValue(NotificationTypeListValue value, boolean fireEvents) {
        if (value != null) {
            NotificationTypeListValue oldValue = notificationTypeListValue;
            notificationTypeListValue = value;
            initTextBox();
            if (fireEvents) {
                ValueChangeEvent.fireIfNotEqual(this,
                                                oldValue,
                                                notificationTypeListValue);
            }
        }
    }

    private void initTextBox() {
        if (notificationTypeListValue == null) {
            notificationTextBox.value = "zero notifications";
        } else {
            notificationTextBox.value = notificationTypeListValue.getValues().size() + " notifications";
        }
    }

    @Override
    public NotificationTypeListValue getValue() {
        return notificationTypeListValue;
    }

    @Override
    public void setValue(NotificationTypeListValue value) {
        if (value != null) {
            setValue(value,
                     false);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<NotificationTypeListValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(final boolean readOnly) {
        notificationWidget.setReadOnly(readOnly);
    }

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Notifications data.
     */
    public interface GetNotificationsCallback {

        void getData(NotificationTypeListValue value);
    }
}
