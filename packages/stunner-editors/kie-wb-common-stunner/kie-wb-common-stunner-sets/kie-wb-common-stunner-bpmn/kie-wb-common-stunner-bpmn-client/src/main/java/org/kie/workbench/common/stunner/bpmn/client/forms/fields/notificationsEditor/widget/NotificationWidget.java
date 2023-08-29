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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.NotificationsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
public class NotificationWidget implements IsWidget,
                                           NotificationWidgetView.Presenter {

    private final NotificationWidgetView view;

    private final ClientTranslationService translationService;

    private List<NotificationRow> rows = new ArrayList<>();

    private NotificationsEditorWidget.GetNotificationsCallback callback = null;

    @Inject
    public NotificationWidget(NotificationWidgetView view,
                              ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this, rows);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getNameHeader() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATIONS_LABEL);
    }

    @Override
    public String getToUsersLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_TO_USERS);
    }

    @Override
    public String getToEmailsLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_TO_EMAILS);
    }

    @Override
    public String getToGroupsLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_TO_GROUPS);
    }

    @Override
    public String getExpiresAtLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_EXPIRES_AT);
    }

    @Override
    public String getTypeLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_TYPE);
    }

    @Override
    public String getReplyToLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_REPLY_TO);
    }

    @Override
    public String getSubjectLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_SUBJECT);
    }

    @Override
    public String getBodyLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_BODY);
    }

    @Override
    public String getFromLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_FROM);
    }

    @Override
    public String getDeleteLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_DELETE);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void ok() {
        if (callback != null) {
            List<NotificationValue> notifications = getValue()
                    .stream()
                    .map(NotificationRow::toNotificationValue)
                    .collect(Collectors.toList());
            callback.getData(new NotificationTypeListValue(notifications));
        }
        view.hide();
    }

    @Override
    public List<NotificationRow> getValue() {
        return rows;
    }

    @Override
    public void setValue(List<NotificationRow> values) {
        setValue(values, false);
    }

    @Override
    public void setValue(List<NotificationRow> newValues, boolean fireEvents) {
        List<NotificationRow> oldValue = rows;
        rows = newValues;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, rows);
        }
    }

    public void setCallback(final NotificationsEditorWidget.GetNotificationsCallback callback) {
        this.callback = callback;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<NotificationRow>> handler) {
        return view.asWidget().addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }
}
