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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("AssigneeEditorWidget.html#widget")
public class AssigneeEditorWidgetViewImpl extends Composite implements AssigneeEditorWidgetView,
                                                                       HasValue<String> {

    ListBoxValues nameListBoxValues;

    private String sAssignees;

    protected Presenter presenter;

    @Inject
    @DataField
    protected Button addButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    protected TableCellElement nameth = Document.get().createTHElement();

    List<String> names;

    @Inject
    protected ClientUserSystemManager userSystemManager;

    protected final static int MAX_SEARCH_RESULTS = 1000;

    /**
     * The list of assigneeRows that currently exist.
     */
    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<AssigneeRow, AssigneeListItemWidgetViewImpl> assigneeRows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return sAssignees;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        String oldValue = sAssignees;
        sAssignees = value;
        if (names == null) {
            getNames();
        }
        initView();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            sAssignees);
        }
    }

    @Override
    public void doSave() {
        String newValue = presenter.serializeAssignees(getAssigneeRows());
        setValue(newValue,
                 true);
    }

    protected void initView() {
        List<AssigneeRow> arrAssigneeRows = presenter.deserializeAssignees(sAssignees);
        setAssigneeRows(arrAssigneeRows);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    /**
     * Tests whether a AssigneeRow name occurs more than once in the list of rows
     * @param name
     * @return
     */
    public boolean isDuplicateName(final String name) {
        return presenter.isDuplicateName(name);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        addButton.setIcon(IconType.PLUS);
        nameth.setInnerText("Name");
    }

    @Override
    public int getAssigneeRowsCount() {
        return assigneeRows.getValue().size();
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.TABLE);
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void setAssigneeRows(final List<AssigneeRow> rows) {
        assigneeRows.setValue(rows);
        for (int i = 0; i < getAssigneeRowsCount(); i++) {
            AssigneeListItemWidgetView widget = getAssigneeWidget(i);
            widget.setNames(nameListBoxValues);
            widget.setParentWidget(presenter);
        }
    }

    @Override
    public List<AssigneeRow> getAssigneeRows() {
        return assigneeRows.getValue();
    }

    @Override
    public AssigneeListItemWidgetView getAssigneeWidget(final int index) {
        return assigneeRows.getComponent(index);
    }

    public void setAssigneesNames(final ListBoxValues nameListBoxValues) {
        this.nameListBoxValues = nameListBoxValues;
        for (int i = 0; i < getAssigneeRowsCount(); i++) {
            getAssigneeWidget(i).setNames(nameListBoxValues);
        }
    }

    @EventHandler("addButton")
    public void handleAddButton(final ClickEvent e) {
        presenter.addAssignee();
    }

    protected void getNames() {
        RemoteCallback<AbstractEntityManager.SearchResponse<?>> searchResponseRemoteCallback =
                new RemoteCallback<AbstractEntityManager.SearchResponse<?>>() {
                    @Override
                    public void callback(final AbstractEntityManager.SearchResponse<?> response) {
                        names = new ArrayList<String>();
                        if (response != null) {
                            List<?> items = response.getResults();
                            if (items != null) {
                                for (Object item : items) {
                                    addItemToNames(item);
                                }
                            }
                        }
                        Collections.sort(names);
                        presenter.setNames(names);
                    }
                };

        ErrorCallback<Message> searchErrorCallback =
                new ErrorCallback<Message>() {
                    @Override
                    public boolean error(final Message message,
                                         final Throwable throwable) {
                        names = new ArrayList<String>();
                        presenter.setNames(names);
                        return false;
                    }
                };

        // Call backend service.
        if (presenter.getType() == AssigneeType.USER) {
            userSystemManager.users(searchResponseRemoteCallback,
                                    searchErrorCallback).
                    search(new SearchRequestImpl("",
                                                 1,
                                                 MAX_SEARCH_RESULTS));
        } else {
            userSystemManager.groups(searchResponseRemoteCallback,
                                     searchErrorCallback).
                    search(new SearchRequestImpl("",
                                                 1,
                                                 MAX_SEARCH_RESULTS));
        }
    }

    protected <T> void addItemToNames(final T item) {
        if (item instanceof User) {
            names.add(((User) item).getIdentifier());
        } else if (item instanceof Group) {
            names.add(((Group) item).getName());
        }
    }

    @Override
    public void showMaxAssigneesAdded() {
        notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.INSTANCE.Max_assignees_added(),
                                                NotificationEvent.NotificationType.ERROR));
    }
}
