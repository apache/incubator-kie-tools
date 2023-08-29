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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Assignee;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class AssigneeEditorWidget implements IsWidget,
                                             AssigneeEditorWidgetView.Presenter {

    private AssigneeEditorWidgetView view;

    private ManagedInstance<AssigneeListItem> listItems;

    private TranslationService translationService;

    private Event<NotificationEvent> notification;

    private List<AssigneeListItem> assigneeRows = new ArrayList<>();

    private AssigneeType type;

    private String value;

    private int max = -1;

    private boolean errorNotificationsEnabled = true;

    @Inject
    public AssigneeEditorWidget(AssigneeEditorWidgetView view,
                                ManagedInstance<AssigneeListItem> listItems,
                                TranslationService translationService,
                                Event<NotificationEvent> notification) {
        this.view = view;
        this.listItems = listItems;
        this.translationService = translationService;
        this.notification = notification;

        this.view.init(this);
    }

    public void init(AssigneeType type, int max) {
        this.type = type;
        this.max = max;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String newValue, boolean fireEvents) {
        String oldValue = value;
        value = newValue;

        deserializeAssignees(value);

        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void doSave() {
        String oldValue = value;

        value = serializeAssignees(assigneeRows.stream()
                                           .map(AssigneeListItem::getAssignee)
                                           .collect(Collectors.toList()));

        ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
    }

    public void deserializeAssignees(String serializedValue) {
        assigneeRows = new ArrayList<>();

        view.clearList();
        listItems.destroyAll();

        if (serializedValue != null && !serializedValue.isEmpty()) {
            String[] assigneeArray = serializedValue.split(",");
            for (String assigneString : assigneeArray) {
                if (!assigneString.isEmpty()) {
                    addAssignee(new Assignee(assigneString));
                }
            }
        }
    }

    private void addAssignee(Assignee assignee) {
        AssigneeListItem listItem = listItems.get();
        listItem.init(type, assignee, this::doSave, this::removeAssignee, this::onError);
        assigneeRows.add(listItem);
        view.add(listItem);
        if (max != -1 && assigneeRows.size() == max) {
            view.disableAddButton();
        }
    }

    public String serializeAssignees(List<Assignee> assigneeRows) {
        return StringUtils.getStringForList(assigneeRows);
    }

    @Override
    public void addAssignee() {
        if (max == -1 || assigneeRows.size() < max) {
            addAssignee(new Assignee());
        }
    }

    @Override
    public boolean isDuplicateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        return assigneeRows.stream()
                .filter(assigneeListItem -> assigneeListItem.getAssignee().getName().equals(name))
                .findAny()
                .isPresent();
    }

    public void removeAssignee(AssigneeListItem listItem) {
        assigneeRows.remove(listItem);
        listItems.destroy(listItem);
        doSave();
        view.enableAddButton();
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        view.asWidget().fireEvent(gwtEvent);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return view.asWidget().addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public String getNameHeader() {
        return translationService.getTranslation(StunnerBPMNConstants.ASSIGNEE_LABEL);
    }

    @Override
    public String getAddLabel() {
        return translationService.getTranslation(StunnerBPMNConstants.ASSIGNEE_NEW);
    }

    @PreDestroy
    public void destroy() {
        view.clearList();
        listItems.destroyAll();
    }

    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    private void onError(Throwable e) {
        if (errorNotificationsEnabled) {
            notification.fire(new NotificationEvent(translationService.format(StunnerBPMNConstants.ASSIGNEE_SEARCH_ERROR, e.getMessage() != null ? e.getMessage() : "")));
            errorNotificationsEnabled = false;
        }
    }
}
