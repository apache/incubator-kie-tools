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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import java.util.List;
import java.util.stream.Collectors;

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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.event.ReassignmentEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.PopoverTextCell;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

@Dependent
@Templated("ReassignmentWidgetViewImpl.html#container")
public class ReassignmentWidgetViewImpl extends Composite implements ReassignmentWidgetView {

    private static final ProvidesKey<ReassignmentRow> KEY_PROVIDER = item -> item == null ? null : item.getId();

    private ReassignmentWidgetView.Presenter presenter;

    private ListDataProvider<ReassignmentRow> dataProvider = new ListDataProvider<>();

    private List<ReassignmentRow> rows;

    @DataField
    @Inject
    private HTMLButtonElement closeButton, addButton, okButton;

    @Inject
    private ReassignmentEditorWidget editor;

    @DataField
    private SimpleTable<ReassignmentRow> table = new SimpleTable(KEY_PROVIDER);

    private BaseModal modal = new BaseModal();

    private boolean readOnly = false;

    @PostConstruct
    public void init() {
        addButton.addEventListener("click", event -> addOrEdit(new ReassignmentRow()), false);
        closeButton.addEventListener("click", event -> hide(), false);
        okButton.addEventListener("click", event -> ok(), false);
    }

    @Override
    public void init(final ReassignmentWidgetView.Presenter presenter, List<ReassignmentRow> rows) {
        this.rows = rows;
        presenter.addValueChangeHandler(event -> {
            dataProvider.getList().clear();
            dataProvider.getList().addAll(event.getValue());
            refreshTable();
        });

        this.presenter = presenter;
        this.dataProvider.setList(rows);

        modal.setTitle(presenter.getNameHeader());
        modal.setWidth("900px");
        modal.setBody(this);
        modal.setClosable(false);

        table.setToolBarVisible(false);

        setWidth("870px");

        initTable();
    }

    void initTable() {
        initColumns();

        table.setRowCount(dataProvider.getList().size(), true);
        table.setRowData(0, dataProvider.getList());
        dataProvider.addDataDisplay(table);
    }

    private void initColumns() {
        initUsers();
        initGroups();
        initExpiresAt();
        initType();

        initEdit();
        initDelete();
    }

    private void initUsers() {
        PopoverTextCell toUsers = new PopoverTextCell();
        Column<ReassignmentRow, String> toUsersColumn = new Column<ReassignmentRow, String>(
                toUsers) {
            @Override
            public String getValue(ReassignmentRow object) {
                if (object.getUsers() != null) {
                    return object.getUsers().stream().collect(Collectors.joining(","));
                } else {
                    return "";
                }
            }
        };
        toUsersColumn.setSortable(false);
        table.addColumn(toUsersColumn, presenter.getToUsersLabel());
        table.setColumnWidth(toUsersColumn, 250, Style.Unit.PX);
    }

    private void initGroups() {
        PopoverTextCell toGroups = new PopoverTextCell();
        Column<ReassignmentRow, String> toGroupsColumn = new Column<ReassignmentRow, String>(
                toGroups) {
            @Override
            public String getValue(ReassignmentRow object) {
                if (object.getGroups() != null) {
                    return object.getGroups().stream().collect(Collectors.joining(","));
                } else {
                    return "";
                }
            }
        };
        toGroupsColumn.setSortable(false);
        table.addColumn(toGroupsColumn, presenter.getToGroupsLabel());
        table.setColumnWidth(toGroupsColumn, 150, Style.Unit.PX);
    }

    private void initExpiresAt() {
        TextCell expiresAt = new TextCell();
        Column<ReassignmentRow, String> expiresAtColumn = new Column<ReassignmentRow, String>(
                expiresAt) {
            @Override
            public String getValue(ReassignmentRow object) {
                if (object.getDuration() != null) {
                    return object.getDuration();
                }
                return "";
            }
        };
        expiresAtColumn.setSortable(false);
        table.addColumn(expiresAtColumn, presenter.getExpiresAtLabel());
        table.setColumnWidth(expiresAtColumn, 80, Style.Unit.PX);
    }

    private void initType() {
        TextCell typeCell = new TextCell();
        Column<ReassignmentRow, String> typeColumn = new Column<ReassignmentRow, String>(
                typeCell) {
            @Override
            public String getValue(ReassignmentRow object) {
                if (object.getType() != null) {
                    return object.getType().getType();
                }
                return "";
            }
        };
        typeColumn.setSortable(false);
        table.addColumn(typeColumn, presenter.getTypeLabel());
        table.setColumnWidth(typeColumn, 140, Style.Unit.PX);
    }

    private void initDelete() {
        AbstractCell<ReassignmentRow> buttonCell = new AbstractCell<ReassignmentRow>(ClickEvent.getType().getName()) {
            @Override
            public void render(Context context, ReassignmentRow value, SafeHtmlBuilder sb) {
                Button button = new Button();
                button.setSize(ButtonSize.SMALL);
                button.add(new Icon(IconType.REMOVE));
                sb.append(SafeHtmlUtils.fromTrustedString(button.toString()));
            }

            @Override
            public void onBrowserEvent(Context context, Element parent, ReassignmentRow value,
                                       NativeEvent event, ValueUpdater<ReassignmentRow> valueUpdater) {
                if (!readOnly) {
                    delete(value);
                }
            }
        };

        Column<ReassignmentRow, ReassignmentRow> deleteColumn = new Column<ReassignmentRow, ReassignmentRow>(buttonCell) {
            @Override
            public ReassignmentRow getValue(ReassignmentRow object) {
                return object;
            }
        };
        deleteColumn.setSortable(false);
        table.addColumn(deleteColumn, presenter.getDeleteLabel());
        table.setColumnWidth(deleteColumn, 60, Style.Unit.PX);
    }

    @Override
    public void delete(ReassignmentRow row) {
        dataProvider.getList().remove(row);
        table.setRowCount(dataProvider.getList().size());
        table.redraw();
    }

    private void initEdit() {
        AbstractCell<ReassignmentRow> buttonCell = new AbstractCell<ReassignmentRow>(ClickEvent.getType().getName()) {
            @Override
            public void render(Context context, ReassignmentRow value, SafeHtmlBuilder sb) {
                Button button = new Button();
                button.setSize(ButtonSize.SMALL);
                button.add(new Icon(IconType.EDIT));
                sb.append(SafeHtmlUtils.fromTrustedString(button.toString()));
            }

            @Override
            public void onBrowserEvent(Context context, Element parent, ReassignmentRow value,
                                       NativeEvent event, ValueUpdater<ReassignmentRow> valueUpdater) {
                if (!readOnly) {
                    addOrEdit(value);
                }
            }
        };

        Column<ReassignmentRow, ReassignmentRow> editColumn = new Column<ReassignmentRow, ReassignmentRow>(buttonCell) {
            @Override
            public ReassignmentRow getValue(ReassignmentRow object) {
                return object;
            }
        };
        editColumn.setSortable(false);
        table.addColumn(editColumn, StunnerFormsClientFieldsConstants.CONSTANTS.Edit());
        table.setColumnWidth(editColumn, 50, Style.Unit.PX);
    }

    @Override
    public void addOrEdit(ReassignmentRow row) {
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

    public void onSubscription(@Observes ReassignmentEvent event) {
        this.getParent().getParent().setVisible(true);

        ReassignmentRow reassignment = event.getReassignment();
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
