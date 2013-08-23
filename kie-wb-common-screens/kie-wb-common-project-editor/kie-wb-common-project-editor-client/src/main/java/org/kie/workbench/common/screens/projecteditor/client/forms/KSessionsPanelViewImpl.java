/*
 * Copyright 2012 JBoss Inc
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
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ClockTypeOption;
import org.guvnor.common.services.project.model.KSessionModel;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

public class KSessionsPanelViewImpl
        extends Composite
        implements KSessionsPanelView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionsPanelViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    DataGrid<KSessionModel> grid;

    @UiField
    Button addButton;

    @UiField
    Button removeButton;

    @Inject
    public KSessionsPanelViewImpl() {
        grid = new DataGrid<KSessionModel>();

        grid.setEmptyTableWidget(new Label("---"));

        setUpNameColumn();
        setUpClockColumn();
        setUpStateColumn();

        grid.setBordered(true);

        initWidget(uiBinder.createAndBindUi(this));
    }

    private void setUpNameColumn() {
        grid.addColumn(new TextColumn<KSessionModel>() {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                return kSessionModel.getName();
            }
        }, ProjectEditorConstants.INSTANCE.Name());
    }

    private void setUpClockColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(ProjectEditorConstants.INSTANCE.Realtime());
        options.add(ProjectEditorConstants.INSTANCE.Pseudo());

        Column<KSessionModel, String> column = new Column<KSessionModel, String>(new SelectionCell(options)) {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                if (kSessionModel.getClockType().equals(ClockTypeOption.PSEUDO)) {
                    return ProjectEditorConstants.INSTANCE.Pseudo();
                } else if (kSessionModel.getClockType().equals(ClockTypeOption.REALTIME)) {
                    return ProjectEditorConstants.INSTANCE.Realtime();
                } else {
                    return kSessionModel.getClockType().toString();
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update(int index, KSessionModel model, String value) {
                if (value.equals(ProjectEditorConstants.INSTANCE.Pseudo())) {
                    model.setClockType(ClockTypeOption.PSEUDO);
                } else {
                    model.setClockType(ClockTypeOption.REALTIME);
                }
            }
        });

        grid.addColumn(column, ProjectEditorConstants.INSTANCE.Clock());
    }

    private void setUpStateColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(ProjectEditorConstants.INSTANCE.Stateful());
        options.add(ProjectEditorConstants.INSTANCE.Stateless());

        Column<KSessionModel, String> column = new Column<KSessionModel, String>(new SelectionCell(options)) {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                if (kSessionModel.getType() == null) {
                    return ProjectEditorConstants.INSTANCE.Stateful();
                } else if (kSessionModel.getType().equals("stateful")) {
                    return ProjectEditorConstants.INSTANCE.Stateful();
                } else if (kSessionModel.getType().equals("stateless")) {
                    return ProjectEditorConstants.INSTANCE.Stateless();
                } else {
                    return kSessionModel.getType();
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update(int index, KSessionModel model, String value) {
                if (value.equals(ProjectEditorConstants.INSTANCE.Stateful())) {
                    model.setType("stateful");
                } else {
                    model.setType("stateless");
                }
            }
        });

        grid.addColumn(column, ProjectEditorConstants.INSTANCE.State());
    }

    @Override
    public void makeReadOnly() {
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    @Override
    public void makeEditable() {
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearList() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setItemList(List<KSessionModel> list) {
        grid.setRowData(list);
        refresh();
    }

    @Override
    public void refresh() {
        grid.redraw();
    }

    @Override
    public void setSelected(String theOne) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove(String removeMe) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showPleaseSelectAnItem() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @UiHandler("addButton")
    public void onAddClicked(ClickEvent event) {
        presenter.onAdd();
    }

    @UiHandler("removeButton")
    public void onRemoveClicked(ClickEvent event) {
        presenter.onRemove();
    }

}
