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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import elemental2.dom.HTMLButtonElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.event.NotificationEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.PopoverTextCell;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

import static java.lang.String.join;

@Dependent
@Templated("NotificationWidgetViewImpl.html#container")
public class NotificationWidgetViewImpl extends Composite implements NotificationWidgetView {

    private static final ProvidesKey<NotificationRow> KEY_PROVIDER = item -> item == null ? null : item.getId();

    private NotificationWidgetView.Presenter presenter;

    private ListDataProvider<NotificationRow> dataProvider = new ListDataProvider<>();

    private List<NotificationRow> rows;

    @DataField
    @Inject
    private HTMLButtonElement closeButton, addButton, okButton;

    @Inject
    private NotificationEditorWidget editor;

    @DataField
    private SimpleTable<NotificationRow> table = new SimpleTable(KEY_PROVIDER);

    private BaseModal modal = new BaseModal();

    private boolean readOnly = false;

    @PostConstruct
    public void init() {
        addButton.addEventListener("click", event -> addOrEdit(new NotificationRow()), false);
        closeButton.addEventListener("click", event -> hide(), false);
        okButton.addEventListener("click", event -> ok(), false);
    }

    @Override
    public void init(final NotificationWidgetView.Presenter presenter, List<NotificationRow> rows) {
        this.rows = rows;
        presenter.addValueChangeHandler(event -> {
            dataProvider.getList().clear();
            dataProvider.getList().addAll(event.getValue());
            refreshTable();
        });

        this.presenter = presenter;
        this.dataProvider.setList(rows);

        modal.setTitle(presenter.getNameHeader());
        modal.setWidth("1200px");
        modal.setBody(this);
        modal.setClosable(false);

        table.setToolBarVisible(false);
        table.setWidth("1220px");

        initTable();
    }

    void initTable() {
        initColumns();

        table.setRowCount(dataProvider.getList().size(), true);
        table.setRowData(0, dataProvider.getList());
        dataProvider.addDataDisplay(table);
    }

    private void initColumns() {
        initType();
        initExpiresAt();
        initFrom();
        initUsers();
        initGroups();
        initEmails();
        initReplyTo();
        initSubject();
        initBody();

        initEdit();
        initDelete();
    }

    private void initUsers() {
        PopoverTextCell toUsers = new PopoverTextCell();
        Column<NotificationRow, String> toUsersColumn = new Column<NotificationRow, String>(
                toUsers) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getUsers() != null) {
                    return join(",", object.getUsers());
                } else {
                    return "";
                }
            }
        };
        toUsersColumn.setSortable(false);
        table.addColumn(toUsersColumn, presenter.getToUsersLabel());
        table.setColumnWidth(toUsersColumn, 160, Style.Unit.PX);
    }

    private void initEmails() {
        PopoverTextCell toEmails = new PopoverTextCell();
        Column<NotificationRow, String> toEmailsColumn = new Column<NotificationRow, String>(toEmails) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getEmails() != null) {
                    return join(",", object.getEmails());
                }

                return "";
            }
        };
        toEmailsColumn.setSortable(false);
        table.addColumn(toEmailsColumn, presenter.getToEmailsLabel());
        table.setColumnWidth(toEmailsColumn, 160, Style.Unit.PX);
    }

    private void initGroups() {
        PopoverTextCell toGroups = new PopoverTextCell();
        Column<NotificationRow, String> toGroupsColumn = new Column<NotificationRow, String>(
                toGroups) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getGroups() != null) {
                    return join(",", object.getGroups());
                } else {
                    return "";
                }
            }
        };
        toGroupsColumn.setSortable(false);
        table.addColumn(toGroupsColumn, presenter.getToGroupsLabel());
        table.setColumnWidth(toGroupsColumn, 160, Style.Unit.PX);
    }

    private void initExpiresAt() {
        PopoverTextCell expiresAt = new PopoverTextCell();
        Column<NotificationRow, String> expiresAtColumn = new Column<NotificationRow, String>(
                expiresAt) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getExpiresAt() != null) {
                    return object.getExpiresAt();
                }
                return "";
            }
        };
        expiresAtColumn.setSortable(false);
        table.addColumn(expiresAtColumn, presenter.getExpiresAtLabel());
        table.setColumnWidth(expiresAtColumn, 80, Style.Unit.PX);
    }

    private void initFrom() {
        TextCell typeCell = new TextCell();
        Column<NotificationRow, String> fromColumn = new Column<NotificationRow, String>(
                typeCell) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getFrom() != null) {
                    return object.getFrom();
                }
                return "";
            }
        };
        fromColumn.setSortable(false);
        table.addColumn(fromColumn, presenter.getFromLabel());
        table.setColumnWidth(fromColumn, 70, Style.Unit.PX);
    }

    private void initReplyTo() {
        TextCell typeCell = new TextCell();
        Column<NotificationRow, String> replyToColumn = new Column<NotificationRow, String>(
                typeCell) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getReplyTo() != null) {
                    return object.getReplyTo();
                }
                return "";
            }
        };
        replyToColumn.setSortable(false);
        table.addColumn(replyToColumn, presenter.getReplyToLabel());
        table.setColumnWidth(replyToColumn, 70, Style.Unit.PX);
    }

    private void initSubject() {
        PopoverTextCell typeCell = new PopoverTextCell();
        Column<NotificationRow, String> subjectColumn = new Column<NotificationRow, String>(
                typeCell) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getSubject() != null) {
                    return object.getSubject();
                }
                return "";
            }
        };
        subjectColumn.setSortable(false);
        table.addColumn(subjectColumn, presenter.getSubjectLabel());
        table.setColumnWidth(subjectColumn, 120, Style.Unit.PX);
    }

    private void initBody() {
        PopoverTextCell typeCell = new PopoverTextCell();
        Column<NotificationRow, String> bodyColumn = new Column<NotificationRow, String>(
                typeCell) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getBody() != null) {
                    return object.getBody();
                }
                return "";
            }
        };
        bodyColumn.setSortable(false);
        table.addColumn(bodyColumn, presenter.getBodyLabel());
        table.setColumnWidth(bodyColumn, 160, Style.Unit.PX);
    }

    private void initType() {
        TextCell typeCell = new TextCell();
        Column<NotificationRow, String> typeColumn = new Column<NotificationRow, String>(
                typeCell) {
            @Override
            public String getValue(NotificationRow object) {
                if (object.getType() != null) {
                    return object.getType().getType();
                }
                return "";
            }
        };
        typeColumn.setSortable(false);
        table.addColumn(typeColumn, presenter.getTypeLabel());
        table.setColumnWidth(typeColumn, 70, Style.Unit.PX);
    }

    private void initDelete() {
        AbstractCell<NotificationRow> buttonCell = new AbstractCell<NotificationRow>(ClickEvent.getType().getName()) {
            @Override
            public void render(Context context, NotificationRow value, SafeHtmlBuilder sb) {
                Button button = new Button();
                button.setSize(ButtonSize.SMALL);
                button.add(new Icon(IconType.TRASH));
                sb.append(SafeHtmlUtils.fromTrustedString(button.toString()));
            }

            @Override
            public void onBrowserEvent(Context context, Element parent, NotificationRow value,
                                       NativeEvent event, ValueUpdater<NotificationRow> valueUpdater) {
                if (!readOnly) {
                    delete(value);
                }
            }
        };

        Column<NotificationRow, NotificationRow> deleteColumn = new Column<NotificationRow, NotificationRow>(buttonCell) {
            @Override
            public NotificationRow getValue(NotificationRow object) {
                return object;
            }
        };
        deleteColumn.setSortable(false);
        table.addColumn(deleteColumn, presenter.getDeleteLabel());
        table.setColumnWidth(deleteColumn, 60, Style.Unit.PX);
    }

    private void initEdit() {
        AbstractCell<NotificationRow> buttonCell = new AbstractCell<NotificationRow>(ClickEvent.getType().getName()) {
            @Override
            public void render(Context context, NotificationRow value, SafeHtmlBuilder sb) {
                Button button = new Button();
                button.setSize(ButtonSize.SMALL);
                button.add(new Icon(IconType.EDIT));
                sb.append(SafeHtmlUtils.fromTrustedString(button.toString()));
            }

            @Override
            public void onBrowserEvent(Context context, Element parent, NotificationRow value,
                                       NativeEvent event, ValueUpdater<NotificationRow> valueUpdater) {
                if (!readOnly) {
                    addOrEdit(value);
                }
            }
        };

        Column<NotificationRow, NotificationRow> editColumn = new Column<NotificationRow, NotificationRow>(buttonCell) {
            @Override
            public NotificationRow getValue(NotificationRow object) {
                return object;
            }
        };
        editColumn.setSortable(false);
        table.addColumn(editColumn, StunnerFormsClientFieldsConstants.CONSTANTS.Edit());
        table.setColumnWidth(editColumn, 50, Style.Unit.PX);
    }

    @Override
    public void delete(NotificationRow row) {
        dataProvider.getList().remove(row);
        table.setRowCount(dataProvider.getList().size());
        table.redraw();
    }

    @Override
    public void addOrEdit(NotificationRow row) {
        this.getParent().getParent().setVisible(false);
        editor.createOrEdit(this, row);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        addButton.disabled = readOnly;
        okButton.disabled = readOnly;
        this.readOnly = readOnly;
    }

    @Override
    public void show() {
        table.redraw();
        modal.show();
    }

    public void onSubscription(@Observes NotificationEvent event) {
        this.getParent().getParent().setVisible(true);

        NotificationRow reassignment = event.getNotification();
        if (reassignment != null) {

            if (!dataProvider.getList().contains(reassignment)) {
                dataProvider.getList().add(reassignment);
            }
            refreshTable();
        }
    }

    @Override
    public void refreshTable() {
        table.setRowCount(dataProvider.getList().size(), true);
        table.setRowData(0, dataProvider.getList());
    }

    void ok() {
        presenter.setValue(dataProvider.getList());
        presenter.ok();
    }

    @Override
    public void hide() {
        dataProvider.getList().clear();
        table.setRowCount(dataProvider.getList().size(), true);
        modal.hide();
    }
}
