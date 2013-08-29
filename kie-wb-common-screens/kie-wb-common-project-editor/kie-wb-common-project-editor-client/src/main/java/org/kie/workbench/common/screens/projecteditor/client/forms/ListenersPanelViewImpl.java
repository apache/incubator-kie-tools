/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ListenerModel;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

public class ListenersPanelViewImpl
        extends Composite
        implements ListenersPanelView {

    private static final String WORKING_MEMORY_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.WorkingMemoryEventListener();
    private static final String AGENDA_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.AgendaEventListener();
    private static final String PROCESS_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.ProcessEventListener();

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, ListenersPanelViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    DataGrid<ListenerModel> grid;

    @UiField
    Button addButton;

    @Inject
    public ListenersPanelViewImpl() {
//        grid = new CellTable<ListenerModel>();
//        grid.setHeight("100%");
//        grid.setWidth("100%");
        grid = new DataGrid<ListenerModel>();

        grid.setEmptyTableWidget(new Label("---"));
        grid.setBordered(true);

        addKindColumn();
        addTypeColumn();
        addDeleteColumn();

        initWidget(uiBinder.createAndBindUi(this));
    }

    private void addDeleteColumn() {
        Column<ListenerModel, String> column = new Column<ListenerModel, String>(new ButtonCell()) {
            @Override
            public String getValue(ListenerModel object) {
                return ProjectEditorConstants.INSTANCE.Delete();
            }
        };

        column.setFieldUpdater(new FieldUpdater<ListenerModel, String>() {
            @Override
            public void update(int index, ListenerModel model, String value) {
                presenter.onDelete(model);
            }
        });

        grid.addColumn(column);
        grid.setColumnWidth(column, "70px");
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void addTypeColumn() {

        Column<ListenerModel, String> column = new Column<ListenerModel, String>(new EditTextCell()) {
            @Override
            public String getValue(ListenerModel listenerModel) {
                return listenerModel.getType();
            }
        };

        column.setFieldUpdater(new FieldUpdater<ListenerModel, String>() {
            @Override
            public void update(int index, ListenerModel model, String value) {
                model.setType(value);
            }
        });

        grid.addColumn(column, ProjectEditorConstants.INSTANCE.Type());
    }

    private void addKindColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(WORKING_MEMORY_EVENT_LISTENER);
        options.add(AGENDA_EVENT_LISTENER);
        options.add(PROCESS_EVENT_LISTENER);

        Column<ListenerModel, String> column = new Column<ListenerModel, String>(new SelectionCell(options)) {
            @Override
            public String getValue(ListenerModel listenerModel) {
                if (listenerModel.getKind() == null || listenerModel.getKind().equals(ListenerModel.Kind.WORKING_MEMORY_EVENT_LISTENER)) {
                    return WORKING_MEMORY_EVENT_LISTENER;
                } else if (listenerModel.getKind().equals(ListenerModel.Kind.PROCESS_EVENT_LISTENER)) {
                    return PROCESS_EVENT_LISTENER;
                } else if (listenerModel.getKind().equals(ListenerModel.Kind.AGENDA_EVENT_LISTENER)) {
                    return AGENDA_EVENT_LISTENER;
                } else {
                    return listenerModel.getKind().toString();
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<ListenerModel, String>() {
            @Override
            public void update(int index, ListenerModel model, String value) {
                if (value.equals(WORKING_MEMORY_EVENT_LISTENER)) {
                    model.setKind(ListenerModel.Kind.WORKING_MEMORY_EVENT_LISTENER);
                } else if (value.equals(PROCESS_EVENT_LISTENER)) {
                    model.setKind(ListenerModel.Kind.PROCESS_EVENT_LISTENER);
                } else if (value.equals(AGENDA_EVENT_LISTENER)) {
                    model.setKind(ListenerModel.Kind.AGENDA_EVENT_LISTENER);
                }
            }
        });

        grid.addColumn(column, ProjectEditorConstants.INSTANCE.Kind());
    }

    public void setModels(List<ListenerModel> listeners) {
        grid.setRowData(listeners);
    }

    @UiHandler("addButton")
    public void onAddClick(ClickEvent event) {
        presenter.onAdd();
    }

}
